package me.jarkimzhu.libs.command;

import java.io.File;
import java.io.IOException;

public interface ICommandToSystem {
    Process exec(CommandType type, String... cmd) throws IOException;

    Process exec(CommandType type, String[] cmd, String[] envp) throws IOException;

    Process exec(CommandType type, String[] cmdArray, String[] envp, File dir) throws IOException;

    Integer getPidByName(String name);

    Integer getPidByPath(String path) throws IOException, InterruptedException;

    boolean killProcess(int pid) throws IOException, InterruptedException;

    boolean killProcess(String name) throws IOException, InterruptedException;

    String showOutput(Process process) throws InterruptedException;

    void showOutputAsync(Process process);

    void destroy();
}
