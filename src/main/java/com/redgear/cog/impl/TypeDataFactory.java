package com.redgear.cog.impl;

import com.redgear.cog.TypeConverter;
import com.redgear.cog.exception.CogConversionException;
import com.redgear.cog.exception.CogReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TypeDataFactory {

    private static final Logger log = LoggerFactory.getLogger(TypeDataFactory.class);

    private final Config config;

    public TypeDataFactory(Config config) {
        this.config = config;
    }

    public <T> TypeData<T> build(Class<T> clazz){
        List<FieldData> fieldData = new ArrayList<>();

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Supplier<T> constructor;

        try {
            Constructor<T> constructorMethod = clazz.getConstructor();
            constructorMethod.setAccessible(true);
            MethodHandle constructorHandle = lookup.unreflectConstructor(constructorMethod);
            constructor = () -> {
                try {
                    return (T) constructorHandle.invoke();
                } catch (Throwable e) {
                    throw new CogReflectionException("Failed to create new object of type: " + clazz, e);
                }
            };
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new CogReflectionException("Class " + clazz + " does not have a no args constructor!", e);
        }

        for (Field f : clazz.getDeclaredFields()) {
            log.debug("Found field: {}", f);

            if (Modifier.isTransient(f.getModifiers()) || f.getAnnotationsByType(Transient.class).length > 0) {
                // Skip transient fields.
                continue;
            }

            Class<?> type = f.getType();

            TypeConverter converter = config.getConverter(type);

            if (converter == null) {
                throw new CogReflectionException("No type converter found for type: " + type);
            }

            String fieldName = f.getName();
            String camelName = camelCase(fieldName);

            //Set up setter
            BiConsumer<ResultContext, T> beanSetter;

            AtomicReference<MethodHandle> setterRef = new AtomicReference<>(null);

            try { //Use a setter method if it exists.
                Method setter = clazz.getMethod("set" + camelName, type);

                setterRef.set(lookup.unreflect(setter));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                //No method. Set field directly.

                f.setAccessible(true);

                try {
                    setterRef.set(lookup.unreflectSetter(f));
                } catch (IllegalAccessException e2) {
                    throw new CogReflectionException("Could not access field: " + fieldName, e2);
                }
            }

            MethodHandle setterHandle = setterRef.get();

            if (setterHandle == null)
                throw new CogReflectionException("Could not access field: " + fieldName);

            beanSetter = (context, obj) -> {

                Object converted;

                try {
                    converted = converter.read(context);
                } catch (Throwable e) {
                    throw new CogConversionException("Could not read result bean " + f, e);
                }

                try {
                    setterHandle.bindTo(obj).invoke(converted);
                } catch (Throwable e) {
                    throw new CogReflectionException("Could not set property with setter. ", e);
                }

            };

            fieldData.add(new FieldData(fieldName, type, beanSetter));

        }

        return new BeanTypeData<>(clazz, constructor, fieldData);
    }

    private static String camelCase(String in) {
        return Character.toUpperCase(in.charAt(0)) + in.substring(1);
    }
}
