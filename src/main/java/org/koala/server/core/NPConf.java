package org.koala.server.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
/*
 * NettyProxyConf
 * @author qiuwenyi
 */
public class NPConf {
	private static Properties prop = new Properties();
	
	public void init(String fn, boolean isServer) {
		try {
			InputStream in = null;
			if(isServer) {
				in = new FileInputStream(fn);
			} else {
				in = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/" + fn);
			}
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initLog4j(String fn, boolean isServer) {
		if(isServer) {
			PropertyConfigurator.configure(fn);

		} else {
			PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/" + fn);
		}
	}
	
	public String get(String key) {
		return prop.getProperty(key);
	}
	
	public String get(String key, String defaultName) {
		return prop.getProperty(key,defaultName);
	}
	
	public int getInt(String key) {
		return Integer.parseInt(prop.getProperty(key));
	}
	
	public int getInt(String key, String defaultName) {
		return Integer.parseInt(prop.getProperty(key,defaultName));
	}
}
