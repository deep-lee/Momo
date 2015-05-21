package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Update extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int versionNum;
	private String version;
	private String updateLog;
	private String size;
	
	private BmobFile apkFile;
	
	public Update() {
		
	}
	
	public BmobFile getApkFile() {
		return apkFile;
	}

	public void setApkFile(BmobFile apkFile) {
		this.apkFile = apkFile;
	}
	
	public int getVersionNum() {
		return versionNum;
	}
	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
	

}

