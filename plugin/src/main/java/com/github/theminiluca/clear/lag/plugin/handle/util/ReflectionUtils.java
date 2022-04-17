package com.github.theminiluca.clear.lag.plugin.handle.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtils {
    private ReflectionUtils() {}


    public static Field getClassPrivateField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Method getPrivateMethod(Class<?> clazz, String methodName, @SuppressWarnings("rawtypes") Class[] params)
            throws NoSuchMethodException, SecurityException {
        Method method = clazz.getDeclaredMethod(methodName, params);
        method.setAccessible(true);
        return method;
    }
}
