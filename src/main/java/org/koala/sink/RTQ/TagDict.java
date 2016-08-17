package org.koala.sink.RTQ;
import java.util.HashMap;

public class TagDict {
	
	public static HashMap<String,String> map  = new HashMap<String,String>();
	static {
		map.put("0-14","102001");
		map.put("15-19","102002");
		map.put("20-24","102003");
		map.put("25-29","102004");
		map.put("30-34","102005");
		map.put("35-39","102006");
		map.put("40-44","102007");
		map.put("45-49","102008");
		map.put("50-54","102009");
		map.put("55-59","102010");
		map.put("60","102011");
		map.put("male","101001");
		map.put("female","101002");
	}
	
	public static String getTagCode(String tagName) {
		return map.get(tagName);
	}
	
}
