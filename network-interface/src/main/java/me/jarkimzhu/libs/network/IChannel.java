package me.jarkimzhu.libs.network;

import java.io.Closeable;
import java.lang.reflect.Type;

/**
 * @author JarkimZhu
 * Created on 2016/2/2.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public interface IChannel<E> extends Closeable {
    E send(Packet packet);
    <T> T send(Packet packet, Class<T> clazz);
    <T> T send(Packet packet, Type type);
}
