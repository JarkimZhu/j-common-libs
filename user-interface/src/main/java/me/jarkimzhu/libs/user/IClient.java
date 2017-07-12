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
    void setDeviceId(String deviceId);

    String getAppName();
    void setAppName(String appName);

    String getVersion();
    void setVersion(String version);

    String getIp();
    void setIp(String ip);

    String getStore();
    void setStore(String store);

    String getOS();
    void setOS(String os);
}
