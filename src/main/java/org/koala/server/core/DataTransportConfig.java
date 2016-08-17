package org.koala.server.core;

import java.util.Map;

import org.koala.server.controller.*;

public class DataTransportConfig extends EasyNettyConfig{

	private static boolean isServer = false;
	private static String confPath = "config.properties";
	private static String log4jPath = "log4j.properties";

	/**
	 * 路由表定义
	 */
	@Override
	public void configRoute(Map<String, Controller> ControllerMap) {
		ControllerMap.put("/DT/job", new dtJobController());
		ControllerMap.put("/DT/stat", new dtStatController());
		ControllerMap.put("/ping", new PingController());
	}

	/**
	 * 初始化配置文件
	 */
	@Override
	public void configProp(NPConf conf) {
		conf.init(confPath, isServer);
		conf.initLog4j(log4jPath, isServer);
	}
	
	/**
	 * 配置第三方插件
	 */
	@Override
	public void configPlugin(NPConf conf) {

	}
	
	/**
	 * 程序入口
	 * @param args
	 */
	public static void main(String [] args) {
		if(args.length == 2) {
			confPath = args[0];
			log4jPath = args[1];
			isServer = true;
		}
		new DataTransportConfig().startup();
	}


}
