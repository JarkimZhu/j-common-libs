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
public interface ICacheFactory {
    ICacheBuilder getBuilder(String builderName);
    void addBuilder(ICacheBuilder builder);
    void removeBuilder(ICacheBuilder builder);
}
