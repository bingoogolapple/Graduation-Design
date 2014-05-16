package com.bingoogol.frogcare.domain;

public class CacheInfo {
	private String appName;
	private String packname;
	private String dataSize;
	private String codeSize;
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getCodeSize() {
		return codeSize;
	}
	public void setCodeSize(String codeSize) {
		this.codeSize = codeSize;
	}
	public String getDataSize() {
		return dataSize;
	}
	public void setDataSize(String dataSize) {
		this.dataSize = dataSize;
	}
}
