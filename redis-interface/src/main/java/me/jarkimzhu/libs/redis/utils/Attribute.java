package me.jarkimzhu.libs.redis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenhui 数据库配置文件读取类 /[XX].propertise
 * @date 2008-07-21
 */
public class Attribute {

	private static final Logger log = LoggerFactory.getLogger(Attribute.class);
	
	private String filePath = null; // dd.properties文件地址
	private Properties cache = new Properties(); // 缓存

	/**
	 * 构造方法
	 */
	public Attribute(String filePath) {
		this.filePath = filePath;
		try {
			System.out.println("filePath:"+filePath);
			InputStream in = this.getClass().getResourceAsStream(filePath);
			if(in == null){
				File file = new File(this.getClass().getResource("/")+filePath);
				in = new FileInputStream(file);
			}
			cache.load(in);
		} catch (IOException e) {
			log.warn("未找到指定文件：" + filePath);
		}
	}

	public Properties values() {
		return cache;
	}

	// GET
	public String value(String key) {
		if (cache.containsKey(key)) {
			String value = new Endoding().UTF8((String) cache.get(key));
			return value;
		} else {
			log.warn("在属性文件中，未找到指定的 【KEY:" + key + "】 in " + filePath);
		}
		return null;
	}

	// SET
	public void value(String key, String value) {
		cache.setProperty(key, value);
	}

	/**
	 * 写入
	 */
	public void out(String key, String value) {
		cache.setProperty(key, value);
		try {
			cache.store(new FileOutputStream(filePath), "注释");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取配置信息 For Int
	 * */
	public int getInt(String key){
		String _value = value( key );
		try {
			return Integer.parseInt( _value.trim() );
		} catch (Exception e) {
			return -1;
		}
	}

}