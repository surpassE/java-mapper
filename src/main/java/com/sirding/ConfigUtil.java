package com.sirding;

import java.util.List;

import com.sirding.singleton.IniTool;

public class ConfigUtil<T> {

	private static String path = "";
	private static IniTool iniTool = IniTool.newInstance();;
	
	static{
		path = System.getProperty("user.dir") + "/config.ini";
		path = path.replaceAll("\\\\", "/");
	}
	
	public static <T> List<T> load(Class<T> clazz){
		List<T> list = null;
		try {
			list = iniTool.loadSec(clazz.getClass());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list; 
	}
}
