package com.redgear.cog.rest.impl;

import com.redgear.cog.core.Param;
import com.redgear.cog.exception.CogConversionException;
import com.redgear.cog.exception.CogReflectionException;
import com.redgear.cog.exception.CogRuntimeException;
import com.redgear.cog.rest.Body;
import com.redgear.cog.rest.ParamMapper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ControllerFactory {

    private final ServerConfig config;

    public ControllerFactory(ServerConfig config) {
        this.config = config;
    }

    public Map<String, Function<Map<String, String>, ?>> build(Object raw) {
        Class<?> clazz = raw.getClass();

        if (Proxy.isProxyClass(clazz)) {
            clazz = clazz.getInterfaces()[0];
        }

        Map<String, Function<Map<String, String>, ?>> handlers = new HashMap<>();

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();

            List<Function<Map<String, String>, Object>> params = Arrays.stream(method.getParameters())
                    .map(param -> {
                        Param name = param.getAnnotation(Param.class);

                        if (name == null) {
                            throw new CogReflectionException("No @Param annotation found on parameter " + param.getName() + " on method " + method);
                        }

                        String key = name.value();

                        Class paramType = param.getType();



                        ParamMapper mapper = config.getMappers().get(paramType);

                        if (mapper == null) {
                            if (paramType == Map.class) {
                                return (Function<Map<String, String>, Object>) (Map<String, String> map) -> map;
                            }

//                            else if (param.getAnnotation(Body.class) != null) {
//
//                            }

//                            throw new CogReflectionException("No Parameter mapper found for type: " + param.getType() + " on method " + method);
                        }

                        return (Function<Map<String, String>, Object>) (Map<String, String> map) -> {
                            try {
                                return mapper.parse(map.get(key));
                            } catch (Exception e) {
                                throw new CogConversionException("Failed to convert value " + map.get(key) + " to type " + mapper.getType() + " for parameter " + key + " in method " + method, e);
                            }
                        };
                    }).collect(Collectors.toList());


            try {
                MethodHandle handle = MethodHandles.lookup().unreflect(method).bindTo(raw);

                handlers.put(methodName, args -> {
                    List<Object> parsedArgs = params.stream().map(fun -> fun.apply(args)).collect(Collectors.toList());

                    try {
                        return handle.invokeWithArguments(parsedArgs);
                    } catch (Throwable throwable) {
                        throw new CogRuntimeException("Exception running method " + method, throwable);
                    }
                });

            } catch (IllegalAccessException e) {
                throw new CogReflectionException("Failed to create method handle for method " + method);
            }

        }


        return handlers;
    }




}
