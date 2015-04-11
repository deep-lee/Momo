package com.bmob.im.demo.bean;

import java.util.ArrayList;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/** 重载BmobChatUser对象：若还有其他需要增加的属性可在此添加
  * @ClassName: TextUser
  * @Description: TODO
  * @author smile
  * @date 2014-5-29 下午6:15:45
  */
public class User extends BmobChatUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 发布的博客列表
	 */
	private BmobRelation blogs;
	
	/**
	 * //显示数据拼音的首字母
	 */
	private String sortLetters;
	
	/**
	 * //性别-true-男
	 */
	private Boolean sex;
	
	private Blog blog;
	
	private String birthday;
	private String gameType;
	
	private String gameDifficulty;
	
	private String photoWallFile;
	
	private String love;
	private String hobbi;
	
	
	
	public String getLove() {
		return love;
	}
	public void setLove(String love) {
		this.love = love;
	}
	public String getHobbi() {
		return hobbi;
	}
	public void setHobbi(String hobbi) {
		this.hobbi = hobbi;
	}
	public String getPhotoWallFile() {
		return photoWallFile;
	}
	public void setPhotoWallFile(String photoWallFile) {
		this.photoWallFile = photoWallFile;
	}
	public String getGameDifficulty() {
		return gameDifficulty;
	}
	public void setGameDifficulty(String gameDifficulty) {
		this.gameDifficulty = gameDifficulty;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getGameType() {
		return gameType;
	}
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	
	/**
	 * 地理坐标
	 */
	private BmobGeoPoint location;//
	
	private Integer hight;
	
	
	public Blog getBlog() {
		return blog;
	}
	public void setBlog(Blog blog) {
		this.blog = blog;
	}
	public Integer getHight() {
		return hight;
	}
	public void setHight(Integer hight) {
		this.hight = hight;
	}
	public BmobRelation getBlogs() {
		return blogs;
	}
	public void setBlogs(BmobRelation blogs) {
		this.blogs = blogs;
	}
	public BmobGeoPoint getLocation() {
		return location;
	}
	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}
	public Boolean getSex() {
		return sex;
	}
	public void setSex(Boolean sex) {
		this.sex = sex;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
}
