package com.sirding.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.sirding.DbUtil;
import com.sirding.model.Config;
import com.sirding.singleton.IniTool;
import com.sirding.thirdjar.IniEditor;

public class MapperService {

	private String path = "";
	private IniTool iniTool = IniTool.newInstance();
	private static String SUFFIX = ";\n\t}\n";
	//获得配置文件中字段类型的映射关系
	private Map<String, String> map;
	//存储属性字段和对应的java字段
	private Map<String, String> fieldMap = new Hashtable<String, String>();
	
	public MapperService(){
		iniTool = IniTool.newInstance();
//		path = System.getProperty("user.dir") + "/config.ini";
		path = "C:/yrtz/test/java-mapper/config.ini";
		path = path.replaceAll("\\\\", "/");
		try {
			Config config = iniTool.loadSingleSec(Config.class, true, "global");
			String runSec = config.getRunSec();
			if(runSec != null && runSec.length() > 0){
				map = getTypeMapper(runSec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Described			: 
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param tableName
	 */
	public void load(String tableName){
		StringBuffer sb = new StringBuffer();
		StringBuffer methods = new StringBuffer();
		sb.append("public class " + DbUtil.toUpLetter(DbUtil.dealFieldName(tableName)) + " {\n");
		String sql = "SHOW FULL COLUMNS FROM " + tableName;
		try {
			Connection conn = DbUtil.getConn();
			ResultSet rs = conn.prepareStatement(sql).executeQuery();
			while(rs.next()){
				String field = rs.getString("field");
				String type = rs.getString("type");
				String defValue = rs.getString("default");
				if(defValue == null){
					defValue = "未设置";
				}
				String comment = rs.getString("comment");
//				System.out.println("属性名称：" + field);
//				System.out.println("type:" + type);
//				System.out.println("默认值：" + defValue);
//				System.out.println("备注信息：" + comment);
				String msg = this.getFieldInfo(field, type, defValue, comment);
				sb.append(msg);
				methods.append(this.getSetMethod(field, type));
				methods.append(this.getGetMethod(field, type));
			}
			sb.append("\n" + methods.toString());
			sb.append("}");
//			System.out.println("属性输出.....");
			//
			System.out.println("------生成class文件---start---");
			System.out.println(sb.toString());
			System.out.println("------生成class文件----end--\n\n\n");
			System.out.println("------生成xml文件中的insert、update、select节点信息------");
			String insert = this.getInsertSQL(tableName);
			System.out.println("======生成insert语句=====start=============");
			System.out.println(insert);
			String update = this.getUpdateSQL(tableName);
			System.out.println("======生成insert语句=====end=============\n\n\n");
			System.out.println("======生成update语句=====start=============");
			System.out.println(update);
			String select = this.getSelectSQL(tableName);
			System.out.println("======生成update语句=====end=============\n\n\n");
			System.out.println("======生成select语句=====start=============");
			System.out.println(select);
			System.out.println("======生成select语句=====end=============");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Described			: 获得属性描述信息
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param name
	 * @param type
	 * @param defValue
	 * @param comment
	 * @return
	 */
	public String getFieldInfo(String field, String type, String defValue, String comment){
		StringBuffer sb = new StringBuffer();
		String tab = getTabs(1);
		sb.append(tab + "/**").append("\n");
		sb.append(tab + "*	属性名称	：").append(field).append("\n");
		sb.append(tab + "*	属性类型	：").append(type).append("\n");
		sb.append(tab + "*	属性默认值	：").append(defValue).append("\n");
		sb.append(tab + "*	描述信息	：").append(comment).append("\n");
		sb.append(tab + "**/").append("\n");
		sb.append(tab + "private " + getJavaType(type) + " " + DbUtil.dealFieldName(field) + ";\n");
		fieldMap.put(field, DbUtil.dealFieldName(field));
		return sb.toString();
	}
	
	/**
	 * @Described			: 获得属性的get方法
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param field
	 * @return
	 */
	public String getGetMethod(String field, String type){
		String fieldName = DbUtil.dealFieldName(field);
		StringBuffer sb = new StringBuffer(getTabs(1) + "public " + getJavaType(type) + " get" + DbUtil.toUpLetter(fieldName) + "() {").append("\n");
		sb.append(getTabs(2) + "this." + fieldName + " = " + fieldName);
		sb.append(SUFFIX);
		return sb.toString();
	}
	
	/**
	 * @Described			: 获得属性的set方法
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param field
	 * @param type
	 * @return
	 */
	public String getSetMethod(String field, String type){
		String fieldName = DbUtil.dealFieldName(field);
		StringBuffer sb = new StringBuffer(getTabs(1) + "public void set" + DbUtil.toUpLetter(fieldName));
		sb.append("(" + getJavaType(type) + " " + fieldName + ") {\n");
		sb.append(getTabs(2) + "return this." + fieldName);
		sb.append(SUFFIX);
		return sb.toString();
	}
	
	/**
	 * @Described			: 通过数据库类型映射成java类型
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param type
	 * @return
	 */
	private String getJavaType(String type){
		type = type.toLowerCase();
		type = type.substring(0, type.indexOf("("));
		String resultType = map.get(type);
		if(resultType != null && resultType.length() > 0){
			if(resultType.indexOf(".") > -1){
				resultType = resultType.substring(resultType.lastIndexOf(".") + 1, resultType.length());
			}
		}else{
			resultType = DbUtil.getType(type);
		}
		return resultType;
	}
	
	
	/**
	 * @Described			: 获得tab个数
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param i
	 * @return
	 */
	private String getTabs(int i){
		StringBuffer sb = new StringBuffer();
		if(i > 0){
			int j = 0;
			while(j < i){
				sb.append("\t");
				j++;
			}
		}
		return sb.toString();
	}
	
	/**
	 * @Described			: 获得数据库类型与java类型的映射关系
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param section
	 * @return
	 */
	public Map<String, String> getTypeMapper(String section) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IniEditor conf = new IniEditor(true);
			conf.load(path);
			List<String> list = conf.optionNames(section);
			if(list != null){
				for(String key : list){
					map.put(key, conf.get(section, key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * @Described			: 获得插入的SQL语句
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param tableName
	 * @return
	 */
	public String getInsertSQL(String tableName) {
		String sql = "INSERT INTO " + tableName + "(";
		String value = "";
		for(String key : fieldMap.keySet()){
			sql += key + ",";
			value += "#" + fieldMap.get(key) + "#,";
		}
		if(sql.endsWith(",")){
			sql = sql.substring(0, sql.length() - 1) + ") Value(";
		}
		if(value.endsWith(",")){
			value = value.substring(0, value.length() - 1);
		}
		return sql + value + ");";
	}

	/**
	 * @Described			: 获得更新xml中的SQL语句
	 * @author				: zc.ding
	 * @date 				: 2017年1月7日
	 * @param tableName
	 * @return
	 */
	public String getUpdateSQL(String tableName){
		String sql = "UPDATE TABLE " + tableName + "\n";
		sql += "SET\n<dynamic>\n";
		for(String key : fieldMap.keySet()){
	  		sql += "<isNotEmpty property=\"" + fieldMap.get(key) + "\">\n";
	       	sql += getTabs(1) + key +" = #" + fieldMap.get(key)+ "#,\n";
			sql += "</isNotEmpty>\n";
		}
		sql += "</dynamic>\nid = #id#\n";
		return sql;
	}
	
	/**
	 * @Described			: 获得xml中查询的SQL
	 * @author				: zc.ding
	 * @date 				: 2017年1月8日
	 * @param tableName
	 * @return
	 */
	public String getSelectSQL(String tableName){
		String sql = "SELECT ";
		for(String key : fieldMap.keySet()){
			sql += key + " AS " + fieldMap.get(key) + ",";
		}
		if(sql.endsWith(",")){
			sql = sql.substring(0, sql.length() - 1);
		}
		sql += "\nFROM " + tableName + "\n";
		sql += "WHERE 1=1\n<dynamic>\n";
		for(String key : fieldMap.keySet()){
	  		sql += "<isNotEmpty prepend=\"AND\" property=\"" + fieldMap.get(key) + "\">\n";
	       	sql += getTabs(1) + " <![CDATA[" + key + " = #" + fieldMap.get(key) + "#]]>\n";
			sql += "</isNotEmpty>\n";
		}
		sql += "</dynamic>\n";
		return sql;
	}
	
}
