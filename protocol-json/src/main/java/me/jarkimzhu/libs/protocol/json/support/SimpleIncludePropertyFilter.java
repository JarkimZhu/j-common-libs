package me.jarkimzhu.libs.protocol.json.support;

import com.alibaba.fastjson.serializer.PropertyFilter;

import java.util.Set;

/**
 * @author JarkimZhu
 * Created on 2015/12/21.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class SimpleIncludePropertyFilter implements PropertyFilter {

	private Set<String> includes;
	
	@Override
	public boolean apply(Object object, String name, Object value) {
		if(includes != null) {
			if (includes.contains(name)) {
				return true;
			}
			return false;
		}
		return true;
	}

	public void setIncludes(Set<String> includes) {
		this.includes = includes;
	}

}
