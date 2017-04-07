package me.jarkimzhu.libs.protocol;

/**
 * @author JarkimZhu
 * Created on 2016/2/25.
 * @version 0.0.1-SNAPSHOT
 * @since JDK1.8
 */
public interface IProtocolResult {
    ResultCode SUCCESS();
    ResultCode INVALID_PARAM();
    ResultCode INVALID_SESSION();
    ResultCode INVALID_TOKEN();
    ResultCode SERVER_ERROR();
    ResultCode LOGIN_FAILED();
    ResultCode INVALID_IP();
    ResultCode INVALID_SIGNATURE();
}
