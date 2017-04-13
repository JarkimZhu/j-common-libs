package me.jarkimzhu.libs.utils.serializer;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public class FstSerializer implements ISerializer {

    public static final FstSerializer INSTANT = new FstSerializer();

    @Override
    public <T extends Serializable> byte[] serialize(T obj) throws IOException {
        try (
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                FSTObjectOutput fstOut = new FSTObjectOutput(bytesOut)
        ) {
            fstOut.writeObject(obj);
            fstOut.flush();
            return bytesOut.toByteArray();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if(bytes == null || bytes.length == 0)
            return null;

        try (FSTObjectInput fstInput = new FSTObjectInput(new ByteArrayInputStream(bytes))) {
            return (T) fstInput.readObject();
        }
    }
}
