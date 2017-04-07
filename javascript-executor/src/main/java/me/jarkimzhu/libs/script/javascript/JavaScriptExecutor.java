package me.jarkimzhu.libs.script.javascript;

import me.jarkimzhu.libs.script.IScriptExecutor;
import me.jarkimzhu.libs.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author JarkimZhu
 *         Created on 2016/10/31.
 * @since jdk1.8
 */
public class JavaScriptExecutor implements IScriptExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JavaScriptExecutor.class);

    private ScriptEngine engine;

    public JavaScriptExecutor() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        engine = scriptEngineManager.getEngineByName("nashorn");
    }

    public JavaScriptExecutor(String filePath) {
        this();
        try {
            load(filePath);
        } catch (IOException | ScriptException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void load(String filePath) throws IOException, ScriptException {
        if(!CommonUtils.isBlank(filePath)) {
            if(filePath.startsWith("classpath:")) {
                URL url = JavaScriptExecutor.class.getClassLoader().getResource(filePath.substring(10));
                if(url != null) {
                    engine.eval(new InputStreamReader(url.openStream()));
                } else {
                    throw new FileNotFoundException("Can't load file " + filePath);
                }
            }
        } else {
            throw new FileNotFoundException("Can't load empty file path !");
        }
    }

    @Override
    public Object eval(String script) throws ScriptException {
        return engine.eval(script);
    }
}
