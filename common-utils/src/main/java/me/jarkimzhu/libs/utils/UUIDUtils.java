package me.jarkimzhu.libs.utils;

import java.util.UUID;

/**
 * @author JarkimZhu
 *         Created on 2017/3/8.
 * @since jdk1.8
 */
public abstract class UUIDUtils {
    public static UUID generateId() {
        return UUID.randomUUID();
    }

    public static String generate() {
        return generate(true);
    }

    public static String generate(boolean noneSplit) {
        UUID uuid = generateId();
        if(noneSplit) {
            return uuid.toString().replaceAll("-", "");
        } else {
            return uuid.toString();
        }
    }
}
