package me.jarkimzhu.libs.protocol.json.support;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;

import java.util.Set;

/**
 * Created by dev on 2016/6/17.
 */
public class SimplePropertyPreFilter implements PropertyPreFilter {
    private Set<String> includes;
    private Set<String> excludes;
    private Class clazz;

    @Override
    public boolean apply(JSONSerializer jsonSerializer, Object source, String name) {
        if (excludes == null && includes == null) {
            return true;
        }
        if (source == null) {
            return true;
        }
        if (clazz != null && !clazz.getName().equals(source.getClass().getName())) {
            return true;
        }

        if (excludes != null && excludes.contains(name)) {
            return false;
        }

        if (includes != null && includes.contains(name)) {
            return true;
        }

        return false;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
