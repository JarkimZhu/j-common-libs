/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.local;

import me.jarkimzhu.libs.cache.builder.BuildConfig;

import java.util.Map;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class MapBuildConfig extends BuildConfig {
    private Class<? extends Map> storeClass;

    public Class<? extends Map> getStoreClass() {
        return storeClass;
    }

    public void setStoreClass(Class<? extends Map> storeClass) {
        this.storeClass = storeClass;
    }

    public void setStoreClass(String storeClassName) {
        try {
            //noinspection unchecked
            this.storeClass = (Class<? extends Map>) Class.forName(storeClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
