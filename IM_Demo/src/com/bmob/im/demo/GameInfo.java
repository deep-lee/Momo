package com.bmob.im.demo;

import java.io.Serializable;

/*
 * 2015年5月15日
 * deeplee
 */

public class GameInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String game_name;
	public String object_id;
	public String package_name;
	public String game_icon;
	public String game_rule;
	public String game_win_method;
	public int game_status; // 0 未下载   1 已下载，未安装   2 已下载，已安装  3 已下载已安装，待更新 
	public String game_version;
	public int notificationId;
	
	public GameInfo() {
		// TODO Auto-generated constructor stub
	}
	
	

	public int getNotificationId() {
		return notificationId;
	}



	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}



	public String getGame_version() {
		return game_version;
	}



	public void setGame_version(String game_version) {
		this.game_version = game_version;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGame_name() {
		return game_name;
	}

	public void setGame_name(String game_name) {
		this.game_name = game_name;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getGame_icon() {
		return game_icon;
	}

	public void setGame_icon(String game_icon) {
		this.game_icon = game_icon;
	}

	public String getGame_rule() {
		return game_rule;
	}

	public void setGame_rule(String game_rule) {
		this.game_rule = game_rule;
	}

	public String getGame_win_method() {
		return game_win_method;
	}

	public void setGame_win_method(String game_win_method) {
		this.game_win_method = game_win_method;
	}

	public int getGame_status() {
		return game_status;
	}

	public void setGame_status(int game_status) {
		this.game_status = game_status;
	}
	
}


