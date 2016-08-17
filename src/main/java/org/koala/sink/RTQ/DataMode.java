package org.koala.sink.RTQ;
import java.util.HashMap;

public class DataMode {

	public static String ZIPPC = "ZIPPC";
	public static String SVZIPPC = "SVZIPPC";
	public static String ZIPDEV = "ZIPDEV";
	public static String ZIPMAPPING = "ZIPMAPPING";
	public static String PC = "PC";
	public static String DEV = "DEV";
	public static String PCBLK = "PCBLK";
	public static String DEVBLK = "DEVBLK";
	public static String IPBLK = "IPBLK";
	public static String EMPTY = "EMPTY";
	
	private static HashMap<String,String> prefix = new HashMap<String,String>();
	
	static {
		prefix.put(ZIPPC, "pc");
		prefix.put(SVZIPPC, "Hm");
		prefix.put(ZIPDEV, "Hm");
		prefix.put(PC, "Hm");
		prefix.put(DEV, "Hm");
		prefix.put(PCBLK, "B1");
		prefix.put(DEVBLK, "B2");
		prefix.put(IPBLK, "B3");
		prefix.put(EMPTY, "xx");
	}
	
	public static String getPrefix(String mode) {
		return prefix.get(mode);
	}
}
