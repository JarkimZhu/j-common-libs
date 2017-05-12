/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.builder;

import me.jarkimzhu.libs.cache.ICache;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public interface ICacheBuilder {
    ICache build();
    ICache build(BuildConfig buildConfig);
    String getBuilderName();
}
