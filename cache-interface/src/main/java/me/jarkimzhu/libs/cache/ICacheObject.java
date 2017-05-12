/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.cache;

/**
 * Created on 2017/5/10.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public interface ICacheObject<V> {
    V getData();

    void setData(V data);

    void settle();

    void unsettle();

    void change();

    boolean isChange();

    boolean isDirty();
}
