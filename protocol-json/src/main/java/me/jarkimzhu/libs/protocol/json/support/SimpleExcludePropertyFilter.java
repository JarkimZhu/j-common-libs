package me.jarkimzhu.libs.protocol.json.support;

import com.alibaba.fastjson.serializer.PropertyFilter;

import java.util.Set;

/**
 * @author JarkimZhu
 * Created on 2015/12/21.
 * @version 0.1.0-SNAPSHOT
 * @since JDK1.8
 */
public class SimpleExcludePropertyFilter implements PropertyFilter {

	private Set<String> excludes;
	
	@Override
	public boolean apply(Object object, String name, Object value) {
		if(excludes.contains(name)) {
			return false;
		}
		return true;
	}

	public void setExcludes(Set<String> excludes) {
		this.excludes = excludes;
	}

}
