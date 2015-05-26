package com.bmob.im.demo;

public class GameRankInfo {
	
	private int bestScore;
	private String bestUsername;
	private String bestUserNick;
	private String bestUserAvatar;
	
	private String gameName;
	
	
	public GameRankInfo(int bestScore, String bestUsername,
			String bestUserNick, String bestUserAvatar, String gameName) {
		super();
		this.bestScore = bestScore;
		this.bestUsername = bestUsername;
		this.bestUserNick = bestUserNick;
		this.bestUserAvatar = bestUserAvatar;
		this.gameName = gameName;
	}
	public int getBestScore() {
		return bestScore;
	}
	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}
	
	
	
	public String getBestUserAvatar() {
		return bestUserAvatar;
	}
	public void setBestUserAvatar(String bestUserAvatar) {
		this.bestUserAvatar = bestUserAvatar;
	}
	public String getBestUsername() {
		return bestUsername;
	}
	public void setBestUsername(String bestUsername) {
		this.bestUsername = bestUsername;
	}
	public String getBestUserNick() {
		return bestUserNick;
	}
	public void setBestUserNick(String bestUserNick) {
		this.bestUserNick = bestUserNick;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	

}
