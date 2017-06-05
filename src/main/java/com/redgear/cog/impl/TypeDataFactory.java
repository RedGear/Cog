package com.redgear.cog.impl;

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
            BeanSetter<T> beanSetter;

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

            beanSetter = (obj, resultSet, label) -> {

                Object converted;

                try {
                    converted = converter.read(resultSet, label);
                } catch (Throwable e) {
                    throw new CogConversionException("Could not read result bean " + f, e);
                }

                try {
                    setterHandle.bindTo(obj).invoke(converted);
                } catch (Throwable e) {
                    throw new CogReflectionException("Could not set property with setter. ", e);
                }

            };


            //Set up the getter
            BeanGetter<T> beanGetter;

            AtomicReference<MethodHandle> getterRef = new AtomicReference<>(null);

            try {
                //Try to use getter method if it exists.
                Method getter = clazz.getMethod("get" + camelName);

                if (!getter.getReturnType().equals(type))
                    throw new NoSuchMethodException("Result type of 'get" + camelName + "()' is different than that of field " + fieldName + " conclusion: This is not a field getter. Ignoring getter now. ");

                getterRef.set(lookup.unreflect(getter));
            } catch (IllegalAccessException | NoSuchMethodException e) {
                //If there is no getter method, get the field directly.
                f.setAccessible(true);

                try {
                    getterRef.set(lookup.unreflectGetter(f));
                } catch (IllegalAccessException e2) {
                    throw new CogConversionException("Could not access field. ", e2);
                }
            }

            MethodHandle getterHandle = getterRef.get();

            if (getterHandle == null)
                throw new CogConversionException("Could not access field: " + fieldName);

            beanGetter = (obj, statement, index) -> {
                try {
                    Object value = getterHandle.bindTo(obj).invoke();

                    converter.write(value, statement, index);
                } catch (Throwable e) {
                    throw new CogReflectionException("Could not get property with getter. ", e);
                }
            };

            fieldData.add(new FieldData(fieldName, type, beanGetter, beanSetter));

        }

        return new TypeData<>(clazz, constructor, fieldData);
    }

    private static String camelCase(String in) {
        return Character.toUpperCase(in.charAt(0)) + in.substring(1);
    }
}
