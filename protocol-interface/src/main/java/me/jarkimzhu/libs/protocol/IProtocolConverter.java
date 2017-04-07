package me.jarkimzhu.libs.protocol;

import java.lang.reflect.Type;

/**
 * @author JarkimZhu
 * Created on 2015/12/21.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public interface IProtocolConverter<E> {
	<T> E toProtocol(T dto);
	<T> T toDto(E protocol, Class<T> clazz);
    <T> T toDto(E protocol, Type type);
	<T> T toDto(E protocol);
	<T> String toString(T dto);
}