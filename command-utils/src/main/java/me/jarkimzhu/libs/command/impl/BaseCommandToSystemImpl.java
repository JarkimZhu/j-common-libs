package me.jarkimzhu.libs.command.impl;

import me.jarkimzhu.libs.command.CommandType;
import me.jarkimzhu.libs.command.ICommandToSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseCommandToSystemImpl implements ICommandToSystem {

    private static final Logger logger = LoggerFactory.getLogger(BaseCommandToSystemImpl.class);

    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @Override
    public Process exec(CommandType type, String... cmd) throws IOException {
        return exec(type, cmd, null, null);
    }

    @Override
    public Process exec(CommandType type, String[] cmd, String[] envp) throws IOException {
        return exec(type, cmd, envp, null);
    }

    @Override
    public Process exec(CommandType type, String[] cmdarray, String[] envp, File dir) throws IOException {
        String[] commands = getCommandWrapper(type, cmdarray);
        StringBuilder sb = new StringBuilder();
        sb.append("threadPool commands: ");
        for (String cmd : commands) {
            sb.append(cmd).append(", ");
        }
        logger.debug(sb.toString());
        return Runtime.getRuntime().exec(commands, envp, dir);
    }

    protected abstract String[] getCommandWrapper(CommandType type, String[] cmdarray);

    @Override
    public String showOutput(Process process) throws InterruptedException {
        StringBuffer sb = new StringBuffer();
        CountDownLatch count = new CountDownLatch(2);
        BufferedReader br;
        BufferedReader bre;
        try {
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
            bre = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gbk"));
            execute(new BufferRunner(br, sb, count));
            execute(new BufferRunner(bre, sb, count));
            logger.debug("start waitFor");
            process.waitFor();
            count.await();
            logger.debug("end waitFor");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return sb.toString().trim();
    }

    @Override
    public void showOutputAsync(Process process) {
        BufferedReader br;
        BufferedReader bre;
        try {
            br = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
            bre = new BufferedReader(new InputStreamReader(process.getErrorStream(), "gbk"));
            execute(new BufferRunner(br));
            execute(new BufferRunner(bre));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        threadPool.shutdown();
    }

    private void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    private static class BufferRunner implements Runnable {

        private BufferedReader reader;
        private StringBuffer sb;
        private CountDownLatch count;

        BufferRunner(BufferedReader reader, StringBuffer sb, CountDownLatch count) {
            this.reader = reader;
            this.sb = sb;
            this.count = count;
        }

        BufferRunner(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() > 0) {
                        if (sb != null) {
                            sb.append(line).append("\r\n");
                        }
                        logger.trace(line);
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                if(count != null) {
                    count.countDown();
                }
            }
            logger.trace("BufferRunner done !");
        }

    }
}
