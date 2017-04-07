package me.jarkimzhu.libs.command.impl;


import me.jarkimzhu.libs.command.CommandType;
import me.jarkimzhu.libs.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class CommandToWindowsImpl extends BaseCommandToSystemImpl {

    private static final Logger logger = LoggerFactory.getLogger(CommandToWindowsImpl.class);

    @Override
    protected String[] getCommandWrapper(CommandType type, String[] cmdarray) {
        if (CommandType.CMD == type) {
            String[] ret = new String[cmdarray.length + 2];
            ret[0] = "cmd";
            ret[1] = "/c";
            System.arraycopy(cmdarray, 0, ret, 2, cmdarray.length);
            return ret;
        } else if(CommandType.SCRIPT == type) {
            String[] ret = new String[cmdarray.length + 1];
            ret[0] = "cmd";
            System.arraycopy(cmdarray, 0, ret, 1, cmdarray.length);
            return ret;
        }
        return cmdarray;
    }

    @Override
    public Integer getPidByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getPidByPath(String path) throws IOException, InterruptedException {
        if (!CommonUtils.isBlank(path)) {
            path = path.replace('/', '\\');
            String name = path.substring(path.lastIndexOf("\\") + 1);
            String command = "wmic process where Name='" + name + "' get ProcessId,ExecutablePath";
            logger.debug("SEND CMD[{}]", command);
            Process process = Runtime.getRuntime().exec(command);
            String output = showOutput(process);
            logger.debug("RECV OUTPUT[{}]", output);
            if ("No Instance(s) Available.".equals(output) || output.contains("ERROR")) {
                return -1;
            } else if (output.contains("ERROR")) {
                return -2;
            } else {
                String[] lines = output.split("\\s+");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    if (path.equalsIgnoreCase(line)) {
                        return Integer.parseInt(lines[i + 1]);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean killProcess(int pid) throws IOException, InterruptedException {
        String command = "wmic process where ProcessId='" + pid + "' call terminate";
        logger.debug("SEND CMD[{}]", command);
        Process process = Runtime.getRuntime().exec(command);
        String output = showOutput(process);
        logger.debug("RECV OUTPUT[{}]", output);
        return output.contains("successful");
    }

    @Override
    public boolean killProcess(String name) throws IOException, InterruptedException {
        String command = "wmic process where Name='" + name + "' call terminate";
        logger.debug("SEND CMD[{}]", command);
        Process process = Runtime.getRuntime().exec(command);
        String output = showOutput(process);
        logger.debug("RECV OUTPUT[{}]", output);
        return output.contains("successful");
    }

}
