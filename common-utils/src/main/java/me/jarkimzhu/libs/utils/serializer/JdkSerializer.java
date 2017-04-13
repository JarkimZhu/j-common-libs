package me.jarkimzhu.libs.utils.serializer;

import java.io.*;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class JdkSerializer implements ISerializer {

    public static final JdkSerializer INSTANT = new JdkSerializer();

    @Override
    public <T extends Serializable> byte[] serialize(T obj) throws IOException {
        if (obj == null) {
            return null;
        }

        try (
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream oOut = new ObjectOutputStream(bOut)
        ) {
            oOut.writeObject(obj);
            return bOut.toByteArray();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try (
                ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
                ObjectInputStream oIn = new ObjectInputStream(bIn)
        ) {
            return (T) oIn.readObject();
        }
    }
}
