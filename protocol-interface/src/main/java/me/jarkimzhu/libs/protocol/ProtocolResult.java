package me.jarkimzhu.libs.protocol;

/**
 * @author JarkimZhu
 * Created on 2016/2/25.
 * @version 0.0.1-SNAPSHOT
 * @since JDK1.8
 */
public class ProtocolResult implements IProtocolResult {
    @Override
    public ResultCode SUCCESS() {
        return ResultCode.SUCCESS;
    }

    @Override
    public ResultCode INVALID_PARAM() {
        return ResultCode.INVALID_PARAM;
    }

    @Override
    public ResultCode INVALID_SESSION() {
        return ResultCode.INVALID_SESSION;
    }

    @Override
    public ResultCode SERVER_ERROR() {
        return ResultCode.SERVER_ERROR;
    }

    @Override
    public ResultCode INVALID_TOKEN() {
        return ResultCode.INVALID_TOKEN;
    }

    @Override
    public ResultCode LOGIN_FAILED() {
        return ResultCode.LOGIN_FAILED;
    }

    @Override
    public ResultCode INVALID_IP() {
        return ResultCode.INVALID_IP;
    }

    @Override
    public ResultCode INVALID_SIGNATURE() {
        return ResultCode.INVALID_SIGNATURE;
    }
}
