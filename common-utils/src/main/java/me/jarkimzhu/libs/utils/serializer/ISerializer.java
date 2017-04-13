package me.jarkimzhu.libs.utils.serializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created on 2017/4/13.
 *
 * @author JarkimZhu
 * @since jdk1.8
 */
public interface ISerializer {
    <T extends Serializable> byte[] serialize(T obj) throws IOException;
    <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}
