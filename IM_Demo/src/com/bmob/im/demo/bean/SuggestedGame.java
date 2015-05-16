package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class SuggestedGame extends BmobObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String gameName;
	private String packageName;
	private BmobFile imageShowFile;
	
	public SuggestedGame() {
		
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public BmobFile getImageShowFile() {
		return imageShowFile;
	}

	public void setImageShowFile(BmobFile imageShowFile) {
		this.imageShowFile = imageShowFile;
	}
	
	

}
