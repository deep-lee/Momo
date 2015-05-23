package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ChatBg extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BmobFile file;
	private int belongTo;
	private String photoName;
	
	public ChatBg() {
		
	}
	public BmobFile getFile() {
		return file;
	}
	public void setFile(BmobFile file) {
		this.file = file;
	}
	public int getBelongTo() {
		return belongTo;
	}
	public void setBelongTo(int belongTo) {
		this.belongTo = belongTo;
	}
	public String getPhotoName() {
		return photoName;
	}
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	
	

}

