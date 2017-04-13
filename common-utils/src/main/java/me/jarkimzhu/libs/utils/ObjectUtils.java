package me.jarkimzhu.libs.utils;

import me.jarkimzhu.libs.utils.serializer.FstSerializer;
import me.jarkimzhu.libs.utils.serializer.ISerializer;
import me.jarkimzhu.libs.utils.serializer.JdkSerializer;

import java.io.*;

/**
 * @author JarkimZhu
 *         Created on 2017/3/16.
 * @since jdk1.8
 */
public abstract class ObjectUtils {

    private static ISerializer serializer;

    static {
        String p = System.getProperty("j-serializer", "jdk");
        if("fst".equalsIgnoreCase(p)) {
            serializer = FstSerializer.INSTANT;
        } else {
            serializer = JdkSerializer.INSTANT;
        }
    }

    public static <T extends Serializable> T clone(T obj) throws IOException, ClassNotFoundException {
        byte[] bytes = serializer.serialize(obj);
        return serializer.deserialize(bytes);
    }

    public static <T extends Serializable> byte[] serialize(T obj) throws IOException {
        return serializer.serialize(obj);
    }

    public static <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        return serializer.deserialize(bytes);
    }
}
