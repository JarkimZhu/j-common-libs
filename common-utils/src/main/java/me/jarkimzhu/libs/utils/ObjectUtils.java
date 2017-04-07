package me.jarkimzhu.libs.utils;

import java.io.*;

/**
 * @author JarkimZhu
 *         Created on 2017/3/16.
 * @since jdk1.8
 */
public abstract class ObjectUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T clone(T obj) throws IOException, ClassNotFoundException {
        try (
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream oOut = new ObjectOutputStream(bOut)
        ) {
            oOut.writeObject(obj);

            try (
                    ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
                    ObjectInputStream oIn = new ObjectInputStream(bIn)
            ) {
                return (T) oIn.readObject();
            }
        }
    }

    public static <T extends Serializable> byte[] serialize(T obj) throws IOException {
        try (
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream oOut = new ObjectOutputStream(bOut)
        ) {
            oOut.writeObject(obj);

            return bOut.toByteArray();
        }
    }
}
