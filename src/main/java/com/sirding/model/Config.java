package com.sirding.model;

import java.io.Serializable;

import com.sirding.annotation.Option;

public class Config implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String CONF_FILE_PATH = "C:/yrtz/test/java-mapper/config.ini";
	static{
		CONF_FILE_PATH = System.getProperty("user.dir") + "/config.ini";
		CONF_FILE_PATH = CONF_FILE_PATH.replaceAll("\\\\", "/");
	}
	
	@Option(isSection = true)
	private String secName;
	@Option
	private String driver;
	@Option
	private String url;
	@Option(key = "user_name")
	private String userName;
	@Option
	private String pwd;
	@Option(key = "run_sec")
	private String runSec;
	
	public String getSecName() {
		return secName;
	}
	public void setSecName(String secName) {
		this.secName = secName;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getRunSec() {
		return runSec;
	}
	public void setRunSec(String runSec) {
		this.runSec = runSec;
	}
	
}
