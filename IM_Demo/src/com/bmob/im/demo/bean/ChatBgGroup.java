package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class ChatBgGroup extends BmobObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupName;
	private int groupNo;
	private int numOfPhoto;
	private BmobFile groupShow;
	public ChatBgGroup() {
		super();
	}
	
	public BmobFile getGroupShow() {
		return groupShow;
	}

	public void setGroupShow(BmobFile groupShow) {
		this.groupShow = groupShow;
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}
	public int getNumOfPhoto() {
		return numOfPhoto;
	}
	public void setNumOfPhoto(int numOfPhoto) {
		this.numOfPhoto = numOfPhoto;
	}
}
