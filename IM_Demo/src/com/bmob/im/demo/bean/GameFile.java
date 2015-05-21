package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class GameFile extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String gameName;
	private String packageName;
	private String gameVersion;
	
	private String gameIcon;
	
	private BmobFile file;
	
	private BmobFile gameDisplay1;
	private BmobFile gameDisplay2;
	private BmobFile gameDisplay3;
	private BmobFile gameDisplay4;
	
	private String gameRule;
	private String gameWinMethod;
	
	private int notificationId;
	
	private User bestUser;
	
	private int bestScore;
	

	public User getBestUser() {
		return bestUser;
	}

	public void setBestUser(User bestUser) {
		this.bestUser = bestUser;
	}

	public int getBestScore() {
		return bestScore;
	}

	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}

	public BmobFile getGameDisplay1() {
		return gameDisplay1;
	}

	public void setGameDisplay1(BmobFile gameDisplay1) {
		this.gameDisplay1 = gameDisplay1;
	}

	public BmobFile getGameDisplay2() {
		return gameDisplay2;
	}

	public void setGameDisplay2(BmobFile gameDisplay2) {
		this.gameDisplay2 = gameDisplay2;
	}

	public BmobFile getGameDisplay3() {
		return gameDisplay3;
	}

	public void setGameDisplay3(BmobFile gameDisplay3) {
		this.gameDisplay3 = gameDisplay3;
	}

	public BmobFile getGameDisplay4() {
		return gameDisplay4;
	}

	public void setGameDisplay4(BmobFile gameDisplay4) {
		this.gameDisplay4 = gameDisplay4;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getGameRule() {
		return gameRule;
	}

	public void setGameRule(String gameRule) {
		this.gameRule = gameRule;
	}

	public String getGameWinMethod() {
		return gameWinMethod;
	}

	public void setGameWinMethod(String gameWinMethod) {
		this.gameWinMethod = gameWinMethod;
	}

	public String getGameIcon() {
		return gameIcon;
	}

	public void setGameIcon(String gameIcon) {
		this.gameIcon = gameIcon;
	}

	public String getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(String gameVersion) {
		this.gameVersion = gameVersion;
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

	public BmobFile getFile() {
		return file;
	}

	public void setFile(BmobFile file) {
		this.file = file;
	}
}

