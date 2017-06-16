/*
 * Copyright (c) 2014-2017. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.user;

/**
 * Created on 2017/6/16.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
public interface IClient extends IUser {
    String getDeviceId();
    void setDeviceId();

    String getAppName();
    void setAppName();

    String getVersion();
    void setVersion();

    String getIp();
    void setIp();

    String getStore();
    void setStore();

    String getOS();
    void setOS();
}
