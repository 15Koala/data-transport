package org.koala.server.core;

import java.util.Map;

public abstract class Controller {
	
	public abstract String process(Map<String,String> paras, String body);
	
}
