package com.bmob.im.demo;

import android.graphics.Bitmap;

public class ContactsInfo {
	
	String contactsPhone;
	String contactsName;
	
	String contactNick = "";
	
	Bitmap avatar;
	
	public ContactsInfo(){
		
	}

	public String getContactsPhone() {
		return contactsPhone;
	}

	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}

	public String getContactsName() {
		return contactsName;
	}

	public void setContactsName(String contactsName) {
		this.contactsName = contactsName;
	}

	public Bitmap getAvatar() {
		return avatar;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public String getContactNick() {
		return contactNick;
	}

	public void setContactNick(String contactNick) {
		this.contactNick = contactNick;
	}
	
	
	
}

