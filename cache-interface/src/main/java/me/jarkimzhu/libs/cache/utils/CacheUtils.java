/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/5/16.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class CacheUtils {
     public static <K, V> boolean filterByParameter(Object parameter, K key, V value) {
        if(key instanceof String) {
            if(((String) key).contains(parameter.toString())) {
                return true;
            }
        } else {
            if(key.equals(parameter)) {
                return true;
            }
        }
        return false;
    }

    public static <K, V> Map<K, V> filter(Map<K, V> all, Object parameter) {
         Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : all.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if(CacheUtils.filterByParameter(parameter, key, value)) {
                result.put(key, value);
            }
        }
        return result;
    }
}
