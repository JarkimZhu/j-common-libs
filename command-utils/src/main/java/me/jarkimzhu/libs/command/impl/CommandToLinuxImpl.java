package me.jarkimzhu.libs.command.impl;

import me.jarkimzhu.libs.command.CommandType;
import me.jarkimzhu.libs.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author JarkimZhu
 *         Created on 2016/4/16.
 * @version 0.0.1-SNAPSHOT
 * @since JDK1.8
 */
public class CommandToLinuxImpl extends BaseCommandToSystemImpl {

    private static final Logger logger = LoggerFactory.getLogger(CommandToLinuxImpl.class);

    @Override
    protected String[] getCommandWrapper(CommandType type, String[] cmdarray) {
        if (CommandType.CMD == type) {
            String[] ret = new String[cmdarray.length + 2];
            ret[0] = "sh";
            ret[1] = "-c";
            System.arraycopy(cmdarray, 0, ret, 2, cmdarray.length);
            return ret;
        } else if (CommandType.SCRIPT == type) {
            String[] ret = new String[cmdarray.length + 1];
            ret[0] = "sh";
            System.arraycopy(cmdarray, 0, ret, 1, cmdarray.length);
            return ret;
        }
        return cmdarray;
    }

    @Override
    public Integer getPidByName(String name) {
        try {
            Process p = exec(CommandType.CMD, "ps -ef|grep '" + name + "'");
            String[] pInfo = showOutput(p).split("\\s+");
            if (pInfo.length > 2) {
                return CommonUtils.getInteger(pInfo[1]);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Integer getPidByPath(String path) throws IOException, InterruptedException {
        throw new RuntimeException("Not implement");
    }

    @Override
    public boolean killProcess(int pid) throws IOException, InterruptedException {
        throw new RuntimeException("Not implement");
    }

    @Override
    public boolean killProcess(String name) throws IOException, InterruptedException {
        throw new RuntimeException("Not implement");
    }
}
