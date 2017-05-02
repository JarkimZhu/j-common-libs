/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.utils;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public abstract class CommonUtils {

    private static final Predicate<Object> NOT_NULL_PREDICATE = Objects::isNull;

    public static boolean nullEquals(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            if (isBlank(s1) && !isBlank(s2)) {
                return false;
            } else if (isBlank(s1) && isBlank(s2)) {
                return true;
            }
        } else {
            if (o1 == null && o2 != null) {
                return false;
            } else if (o1 == null) {
                return true;
            }
        }
        return o1.equals(o2);
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * 判断字符串长度是否在min和max之间
     *
     * @param value
     * @param min 最小长度
     * @param max 最大长度
     * @return -1 为空， 0 长度不符， 1 合格
     */
    public static int between(String value, int min, int max) {
        int length;
        if (isBlank(value)) {
            return -1;
        } else if ((length = value.trim().length()) >= min && length <= max) {
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean isBlank(Collection<?> value) {
        return value == null || value.size() == 0;
    }

    public static String getString(String value) {
        if (isBlank(value) || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    public static String getString(String value, String defaultValue) {
        if(isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    public static Double getDouble(String value) {
        if (value != null && value.trim().length() > 0) {
            value = value.replaceAll("[￥,%]", "");
            return Double.parseDouble(value);
        } else {
            return null;
        }
    }

    public static Double getDouble(String value, Double defaultValue) {
        try {
            Double d = getDouble(value);
            if (d != null) {
                return d;
            }
        } catch (Exception e) {
            return defaultValue;
        }
        return defaultValue;
    }

    public static Double getDouble(Double value, double defaultValue) {
        if(value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    public static Integer getInteger(Object value) {
        if (value instanceof BigDecimal) {
            return getInteger((BigDecimal) value);
        } else if (value instanceof String) {
            return getInteger((String) value);
        } else {
            return null;
        }
    }

    public static Integer getInteger(BigDecimal value) {
        if (value != null) {
            return value.intValue();
        }
        return null;
    }

    public static Integer getInteger(Double value) {
        if (value != null) {
            return value.intValue();
        }
        return null;
    }

    public static Integer getInteger(String value) {
        if (isBlank(value)) {
            return null;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static int getInteger(String value, int defaultValue) {
        try {
            Integer i = getInteger(value);
            if (i != null) {
                return i;
            }
        } catch (Exception e) {
            return defaultValue;
        }
        return defaultValue;
    }

    public static Long getLong(String value) {
        if (isBlank(value)) {
            return null;
        } else {
            return Long.parseLong(value);
        }
    }

    public static boolean getBoolean(String value, boolean defaultValue) {
        if (value != null) {
            return "1".equals(value) || "true".equalsIgnoreCase(value);
        } else {
            return defaultValue;
        }
    }

    public static Boolean getBoolean(String value) {
        if (value != null) {
            return "1".equals(value) || "true".equalsIgnoreCase(value);
        } else {
            return null;
        }
    }

    public static BigDecimal getBigDecimal(BigDecimal value, BigDecimal defaultValue) {
        if(value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    public static BigDecimal getBigDecimalNotNull(BigDecimal value) {
        return getBigDecimal(value, BigDecimal.ZERO);
    }

    public static Object getValueByType(String value, Type type) {
        if(isBlank(value) || type == null) {
            return null;
        } else if(type == String.class) {
            return value;
        } else if(type == Integer.class || type == int.class) {
            return getInteger(value);
        } else if(type == Short.class || type == short.class) {
            return getInteger(value);
        } else if(type == Double.class || type == double.class) {
            return getDouble(value);
        } else if(type == Float.class || type == float.class) {
            return getDouble(value);
        } else if(type == Character.class || type == char.class) {
            return value.charAt(0);
        } else if(type == Boolean.class || type == boolean.class) {
            return getBoolean(value);
        } else if(type == BigDecimal.class) {
            return new BigDecimal(value);
        } else {
            return JSON.parseObject(value, type);
        }
    }

    public static void removeIfNull(Collection<?> list) {
        if (!isBlank(list)) {
            list.removeIf(NOT_NULL_PREDICATE);
        }
    }

    public static <T> boolean contains(T[] array, T e) {
        for (T t : array) {
            if (t.equals(e)) {
                return true;
            }
        }
        return false;
    }

    public static Object setOtherNull(Object d, Set<String> includes) {
        Method[] m = d.getClass().getDeclaredMethods();
        for (Method aM : m) {
            if (aM.getName().startsWith("set") && !includes.contains(aM.getName())) {
                try {
                    String[] param = new String[]{null};
                    aM.invoke(d, (Object[]) param);
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return d;
    }

    public static String toString(Object value) {
        if(value != null) {
            return value.toString();
        } else {
            return null;
        }
    }
}
