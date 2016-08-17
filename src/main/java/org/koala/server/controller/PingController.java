package org.koala.server.controller;

import java.util.Map;

import org.koala.server.core.Controller;

public class PingController extends Controller{

	@Override
	public String process(Map<String, String> paras, String body) {
		return "ping back";
	}

}
