/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2016/9/2.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public abstract class MathUtils {
    public static String randomInt(int count) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(9));
        }
        return sb.toString();
    }
}
