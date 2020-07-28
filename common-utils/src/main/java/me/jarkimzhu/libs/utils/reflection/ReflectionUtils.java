/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.utils.reflection;

import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created on 2017/5/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class ReflectionUtils {

    public static String getSetterName(String fieldName) {
        StringBuilder sb = new StringBuilder(fieldName.length() + 3);
        String upper = String.valueOf(fieldName.charAt(0)).toUpperCase();
        sb.append("set");
        sb.append(upper).append(fieldName.substring(1));
        return sb.toString();
    }

    public static String getGetterName(String fieldName, Class<?> fieldType) {
        StringBuilder sb = new StringBuilder(fieldName.length() + 3);
        String upper = String.valueOf(fieldName.charAt(0)).toUpperCase();
        if (Boolean.class.equals(fieldType) ||
                boolean.class.equals(fieldType)) {
            sb.append("is");
        } else {
            sb.append("get");
        }
        sb.append(upper).append(fieldName.substring(1));
        return sb.toString();
    }

    public static Object getGetterValue(Object value, String fieldName) throws NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> clazz = value.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        String getterName = getGetterName(fieldName, field.getType());
        Method getter = clazz.getMethod(getterName, (Class[]) null);
        return getter.invoke(value, (Object[]) null);
    }

    public static void setSetterValue(Object value, String fieldName, Object... parameters) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> clazz = value.getClass();
        String setterName = getSetterName(fieldName);
        Method setter = clazz.getMethod(setterName, parameters.getClass());
        setter.invoke(value, parameters);
    }

    public static boolean hasMethod(Class<?> clazz, String methodName) {
        return hasMethod(clazz, methodName, (Class<?>[]) null);
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes) != null;
        } catch (NoSuchMethodException ignored) {
        }
        return false;
    }

    public static boolean isInstanceOf(Object obj, String className) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> iface : interfaces) {
            if (iface.getName().equalsIgnoreCase(className)) {
                return true;
            }
        }

        while (clazz != null) {
            if (clazz.getName().equalsIgnoreCase(className)) {
                return true;
            } else {
                clazz = clazz.getSuperclass();
            }
        }

        return false;
    }

    public static Type[] getGenericTypes(Class<?> clazz) {
        ArrayList<Type> types = new ArrayList<>();
        Type genType = clazz.getGenericSuperclass();
        types.addAll(getAndCheckActualTypeArguments(genType));
        Type[] ifaces = clazz.getGenericInterfaces();
        for (Type iface : ifaces) {
            types.addAll(getAndCheckActualTypeArguments(iface));
        }
        return types.toArray(new Type[types.size()]);
    }

    private static Collection<Type> getAndCheckActualTypeArguments(Type genType) {
        ArrayList<Type> types = new ArrayList<>();
        if (genType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genType).getActualTypeArguments();
            for (Type type : actualTypeArguments) {
                if(!(type instanceof TypeVariableImpl)) {
                    types.add(type);
                }
            }
        }
        return types;
    }

    public static boolean isFramework(Type type) {
        return type != Object.class && (type.getTypeName().startsWith("java") || type == int.class || type == byte.class || type == short.class
                || type == char.class || type == float.class || type == double.class || type == boolean.class
                || type == String.class);
    }

    public static boolean isFramework(Class clazz) {
        return clazz != Object.class && (clazz.isPrimitive() || clazz.getTypeName().startsWith("java"));
    }

    public static boolean isPrimitiveOrWrapper(Class clazz) {
        return clazz.isPrimitive() || clazz == String.class || clazz == Integer.class
                || clazz == Double.class || clazz == Float.class || clazz == Long.class
                || clazz == Short.class || clazz == Boolean.class || clazz == Byte.class
                || clazz == Character.class;
    }

    public static Field[] getAllField(Class clazz) {
        Type superType = clazz.getGenericSuperclass();
        if(superType != null && superType instanceof Class && superType != Object.class && !isFramework(superType)) {
            Class superClazz = (Class) superType;
            Field[] fields = superClazz.getDeclaredFields();
            Field[] declaredFields = clazz.getDeclaredFields();

            final ArrayList<Field> list = new ArrayList<>(fields.length + declaredFields.length);
            for(Field field : fields) {
                if(!Modifier.isStatic(field.getModifiers())) {
                    list.add(field);
                }
            }
            for(Field field : declaredFields) {
                if(!Modifier.isStatic(field.getModifiers())) {
                    list.add(field);
                }
            }
            return list.toArray(new Field[list.size()]);
        } else {
            Field[] declaredFields = clazz.getDeclaredFields();
            final ArrayList<Field> list = new ArrayList<>(declaredFields.length);
            for(Field field : declaredFields) {
                if(!Modifier.isStatic(field.getModifiers())) {
                    list.add(field);
                }
            }
            return list.toArray(new Field[list.size()]);
        }
    }
}
