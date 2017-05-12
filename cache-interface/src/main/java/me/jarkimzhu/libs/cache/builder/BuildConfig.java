/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.builder;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class BuildConfig {
    private String cacheName;
    private long timeout;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
