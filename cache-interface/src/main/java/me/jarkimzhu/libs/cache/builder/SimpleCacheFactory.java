/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.builder;

import java.util.HashMap;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class SimpleCacheFactory implements ICacheFactory {

    private HashMap<String, ICacheBuilder> builders = new HashMap<>();

    @Override
    public ICacheBuilder getBuilder(String builderName) {
        return builders.get(builderName);
    }

    @Override
    public void addBuilder(ICacheBuilder builder) {
        builders.put(builder.getBuilderName(), builder);
    }

    @Override
    public void removeBuilder(ICacheBuilder builder) {
        builders.remove(builder.getBuilderName());
    }
}
