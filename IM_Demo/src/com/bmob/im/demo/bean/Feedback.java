package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;

public class Feedback extends BmobObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// ��������
	private String content;
	// ��ϵ��ʽ
	private String contacts;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	
	
}
