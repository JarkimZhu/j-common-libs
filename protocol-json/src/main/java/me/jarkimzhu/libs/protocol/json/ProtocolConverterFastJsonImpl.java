package me.jarkimzhu.libs.protocol.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import me.jarkimzhu.libs.protocol.IProtocolConverter;
import me.jarkimzhu.libs.utils.JsonUtils;

import java.lang.reflect.Type;

/**
 * @author JarkimZhu
 * Created on 2015/12/21.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class ProtocolConverterFastJsonImpl implements IProtocolConverter<String> {

	@Override
	public <T> String toProtocol(T dto) {
		return this.toProtocol(dto, (SerializeFilter[])null, (SerializerFeature[])null);
	}

	@Override
	public <T> T toDto(String protocol) {
		throw new RuntimeException("fastjson must support dto type, please call toDto(E p, Type t) instead");
	}

	@Override
	public <T> T toDto(String protocol, Class<T> clazz) {
		return JSON.parseObject(protocol, clazz);
	}

    @Override
    public <T> T toDto(String protocol, Type type) {
        return JSON.parseObject(protocol, type);
    }

    @Override
	public <T> String toString(T dto) {
		return toProtocol(dto);
	}
	
	public <T> String toJson(T dto, SerializeFilter filter) {
		return JSON.toJSONString(dto, filter);
	}
	
	public <T> String toProtocol(T dto, SerializeFilter[] filters, SerializerFeature... features) {
		return JsonUtils.toJSONString(dto, filters, features);
	}
	
}
