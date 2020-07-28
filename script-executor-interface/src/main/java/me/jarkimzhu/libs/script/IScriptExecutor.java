package me.jarkimzhu.libs.script;

import javax.script.ScriptException;
import java.io.IOException;

/**
 * @author JarkimZhu
 *         Created on 2016/10/31.
 * @since jdk1.8
 */
public interface IScriptExecutor {
    void load(String filePath) throws IOException, ScriptException;
    Object eval(String script) throws ScriptException;
}
