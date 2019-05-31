package com.zsx.utils;
/**
 * ConfigUtil.java
 *
 * @author Yanjie Gao
 * @date 2017/3/31 16:55
 */
public class ConfigUtil {

	private ConfigUtil(){
	}

	private static FileProperties SystemAttributes = new FileProperties("/conf.properties");

	public static String getAttribute(String key) {
		if (SystemAttributes != null) {
			return SystemAttributes.getProperty(key);
		} else {
			return null;
		}
	}

	public static String getAttribute(String key, String defaultValue) {
		if (SystemAttributes != null) {
			return SystemAttributes.getProperty(key, defaultValue);
		} else {
			return null;
		}
	}
}
