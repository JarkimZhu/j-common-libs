/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache.local;

import me.jarkimzhu.libs.cache.ICache;
import me.jarkimzhu.libs.cache.builder.BuildConfig;
import me.jarkimzhu.libs.cache.builder.ICacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/5/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class MapCacheBuilder implements ICacheBuilder {

    public static final String BUILDER_NAME = "LOCAL_MAP";

    private static final Logger logger = LoggerFactory.getLogger(MapCacheBuilder.class);

    private MapBuildConfig config;

    public MapCacheBuilder(MapBuildConfig mapBuildConfig) {
        this.config = mapBuildConfig;
    }

    public Map buildStore() {
        if(config == null) {
            return new HashMap();
        } else {
            Class<? extends Map> storeClass = config.getStoreClass();
            try {
                return storeClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                return new HashMap();
            }
        }
    }

    @Override
    public ICache build() {
        return null;
    }

    @Override
    public ICache build(BuildConfig buildConfig) {
        this.config = (MapBuildConfig) buildConfig;
        return build();
    }

    @Override
    public String getBuilderName() {
        return BUILDER_NAME;
    }
}
