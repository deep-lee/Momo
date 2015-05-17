package com.bmob.im.demo.bean;

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
	
	// 生日
	private String birthday;
	
	// 游戏类型
	private String gameType;
	
	// 游戏难度
	private String gameDifficulty;
	
	// 照片墙
	private String photoWallFile;
	
	// 情感状况
	private String love;
	
	// 个性签名
	private String personalizedSignature;
	
	// 职业
	private String career;
	
	// 公司
	private String company;
	
	//学校
	private String school;
	
	// 家乡
	private String hometown;
	
	private String book;
	
	private String movie;
	
	private String music;
	
	// 兴趣爱好
	private String interests;
		
	private String usuallyAppear;
	
	private int recentPlayGame;
	
	// 收藏的状态
	private BmobRelation favorite;
	
	// 我的状态
	private BmobRelation states;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public BmobRelation getStates() {
		return states;
	}



	public void setStates(BmobRelation states) {
		this.states = states;
	}



	public BmobRelation getFavorite() {
		return favorite;
	}

	public void setFavorite(BmobRelation favorite) {
		this.favorite = favorite;
	}

	public int getRecentPlayGame() {
		return recentPlayGame;
	}
	public void setRecentPlayGame(int recentPlayGame) {
		this.recentPlayGame = recentPlayGame;
	}
	public String getPersonalizedSignature() {
		return personalizedSignature;
	}
	public void setPersonalizedSignature(String personalizedSignature) {
		this.personalizedSignature = personalizedSignature;
	}
	public String getCareer() {
		return career;
	}
	public void setCareer(String career) {
		this.career = career;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getHometown() {
		return hometown;
	}
	public void setHometown(String hometown) {
		this.hometown = hometown;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getMovie() {
		return movie;
	}
	public void setMovie(String movie) {
		this.movie = movie;
	}
	public String getMusic() {
		return music;
	}
	public void setMusic(String music) {
		this.music = music;
	}
	public String getInterests() {
		return interests;
	}
	public void setInterests(String interests) {
		this.interests = interests;
	}
	public String getUsuallyAppear() {
		return usuallyAppear;
	}
	public void setUsuallyAppear(String usuallyAppear) {
		this.usuallyAppear = usuallyAppear;
	}
	public String getLove() {
		return love;
	}
	public void setLove(String love) {
		this.love = love;
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
