package com.bmob.im.demo.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/** ����BmobChatUser����������������Ҫ���ӵ����Կ��ڴ����
  * @ClassName: TextUser
  * @Description: TODO
  * @author smile
  * @date 2014-5-29 ����6:15:45
  */
public class User extends BmobChatUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * �����Ĳ����б�
	 */
	private BmobRelation blogs;
	
	/**
	 * //��ʾ����ƴ��������ĸ
	 */
	private String sortLetters;
	
	/**
	 * //�Ա�-true-��
	 */
	private Boolean sex;
	
	private Blog blog;
	
	// ����
	private String birthday;
	
	// ��Ϸ����
	private String gameType;
	
	// ��Ϸ�Ѷ�
	private String gameDifficulty;
	
	// ��Ƭǽ
	private String photoWallFile;
	
	// ���״��
	private String love;
	
	// ����ǩ��
	private String personalizedSignature;
	
	// ְҵ
	private String career;
	
	// ��˾
	private String company;
	
	//ѧУ
	private String school;
	
	// ����
	private String hometown;
	
	private String book;
	
	private String movie;
	
	private String music;
	
	// ��Ȥ����
	private String interests;
		
	private String usuallyAppear;
	
	private int recentPlayGame;
	
	// �ղص�״̬
	private BmobRelation favorite;
	
	// �ҵ�״̬
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
	 * ��������
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
