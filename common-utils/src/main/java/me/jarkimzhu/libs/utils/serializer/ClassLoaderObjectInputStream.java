/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.utils.serializer;

import java.io.*;

/**
 * Created on 2017/6/12.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public class ClassLoaderObjectInputStream extends ObjectInputStream {


    public ClassLoaderObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    protected ClassLoaderObjectInputStream() throws IOException, SecurityException {
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return Class.forName(name, false, loader);
        } catch (ClassNotFoundException ex) {
            return super.resolveClass(desc);
        }
    }
}
