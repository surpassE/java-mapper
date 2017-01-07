package com.sirding;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import com.sirding.model.Config;
import com.sirding.singleton.IniTool;
/**
 * @Described	: 数据库操作工具类
 * @project		: com.sirding.DbUtil
 * @author 		: zc.ding
 * @date 		: 2017年1月7日
 */
public class DbUtil {
	
	private static Connection conn = null;
	private static Object LOCK = new Object();
	private static Map<String, String> typeMap = new HashMap<String, String>();
	
	static{
		//模糊加载数据库与java对应的类型
		typeMap.put("varchar", String.class.getName());
		typeMap.put("int", Integer.class.getName());
		typeMap.put("time", Date.class.getName());
		typeMap.put("double", Double.class.getName());
		typeMap.put("decimal", BigDecimal.class.getName());
	}

	/**
	 * @Described			: 初始化连接
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @return
	 */
	public static Connection initConn(){
		try {
			IniTool iniTool = IniTool.newInstance();
			Config config = iniTool.loadSingleSec(Config.class, true, "global");
			String driver = config.getDriver();
			String url = config.getUrl();
			String userName = config.getUserName();
			String pwd = config.getPwd();
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, pwd) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * @Described			: 获得Connection连接
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @return
	 */
	public static Connection getConn(){
		if(conn == null){
			synchronized (LOCK) {
				if(conn == null){
					return initConn();
				}
			}
		}
		return conn;
	}
	
	/**
	 * @Described			: 通过数据库中定义的类型获得java的类型
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param type
	 * @return
	 */
	public static String getFullType(String type){
		return typeMap.get(getJType(type));
	}
	
	/**
	 * @Described			: 通过数据库中定义的类型获得java的类型
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param type
	 * @return
	 */
	public static String getType(String type){
		String resultType = typeMap.get(getJType(type));
		if(resultType != null){
			resultType = resultType.substring(resultType.lastIndexOf(".") + 1, resultType.length());
		}
		return resultType;
	}
	
	/**
	 * @Described			: 处理数据类型
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param type
	 * @return
	 */
	private static String getJType(String type) {
		if(type != null){
			type = type.toLowerCase();
			if(type.indexOf("int") > -1){
				type = "int";
			}
			if(type.indexOf("char") > -1){
				type = "varchar";
			}
			if(type.indexOf("time") > -1){
				type = "time";
			}
			if(type.indexOf("double") > -1){
				type = "double";
			}
			if(type.indexOf("decimal") > -1){
				type = "decimal";
			}
		}
		return type;
	}
	
	/**
	 * @Described			: 处理属性名称，eg user_name ==> userName
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param field
	 * @return
	 */
	public static String dealFieldName(String field){
		StringBuffer sb = new StringBuffer();
		String[] arr = field.split("_");
		if(arr != null){
			for(int i = 0; i < arr.length; i++){
				String tmp = arr[i];
				if(i != 0){
					tmp = toUpLetter(tmp);
				}
				sb.append(tmp);
			}
		}
		return sb.toString();
	}
	
	/**
	 * @Described			: 对首字母进行转大写
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param letter
	 * @return
	 */
	public static String toUpLetter(String letter){
		String tmp = "";
		if(letter != null){
			tmp = letter.substring(0, 1).toUpperCase();
			tmp +=  letter.substring(1, letter.length());
		}
		return tmp;
	}
}
