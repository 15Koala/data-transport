package org.koala.server.core;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.koala.server.handler.HttpQueueServerHandler;

//import handler.HttpQueueServerHandler;

/**
 * EasyNettyConfig.
 * <p>
 * Config order: configConstant(), configRoute(), configPlugin(), configInterceptor(), configHandler()
 */
public abstract class EasyNettyConfig {
	private static Logger logger = Logger.getLogger(EasyNettyConfig.class);
	private static Map<String, Controller> ControllerMap = new HashMap<String, Controller>();
	
	private NPConf conf = new NPConf();

	public abstract void configProp(NPConf conf) ;

	public abstract void configRoute(Map<String, Controller> ControllerMap);
	
	/**
	 * 利用配置去初始化第三方的插件
	 * @param conf
	 */
	public abstract void configPlugin(final NPConf conf);

	/**
	 * 决定了初始化顺序
	 */
	public void startup() {
		configProp(conf);
		logger.info("Configuration loaded.");	
		configRoute(ControllerMap);
		logger.info("Routes loaded.");
		configPlugin(conf);
		logger.info("Plugin loaded.");
		int port = conf.getInt("server.port");
		int pgnum = conf.getInt("server.ploop");
		int cgnum = conf.getInt("server.cloop");
		int qsiz = conf.getInt("server.pojo.queue");
		HttpQueueServerHandler.init(qsiz, 3);
		new HttpServer(port, pgnum, cgnum).run();
	}

	public String getProp(String name) {
		return conf.get(name);
	}
	
	public static Controller getController(String uri) {
		Controller controller = ControllerMap.get(uri);
		return controller;
	}
	
}
