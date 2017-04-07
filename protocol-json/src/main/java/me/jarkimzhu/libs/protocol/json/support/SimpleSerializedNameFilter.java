package me.jarkimzhu.libs.protocol.json.support;

import com.alibaba.fastjson.serializer.NameFilter;

import java.util.Map;

/**
 * @author JarkimZhu
 * Created on 2015/12/21.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class SimpleSerializedNameFilter implements NameFilter {

	private Map<String, String> nameMapping;
	
	@Override
	public String process(Object source, String name, Object value) {
		String mapping = nameMapping.get(name);
		if(mapping != null) {
			return mapping;
		} else {
			return name;
		}
	}

	public void setNameMapping(Map<String, String> nameMapping) {
		this.nameMapping = nameMapping;
	}
}
