package com.bmob.im.demo.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class QiangYu extends BmobObject implements Serializable{

	/**
	 * 每个列表也就是发布的每个状态的内容
	 * qiang yu entity,每个列表item内容
	 * 2014/4/27
	 */
	private static final long serialVersionUID = -6280656428527540320L;
	
	private User author;  // 作者
	private String content;  // 内容
	private BmobFile Contentfigureurl;   // 状态的图片文件
	private int love; // 赞
	private int hate; // 踩
	private int share; // 分享
	private int comment; // 评论数
	private boolean isPass; // 是否通过
	private boolean myFav;//是不是我的收藏
	private boolean myLove;//是不是我赞过的赞
	private BmobRelation relation; // 对应的评论，由于一个状态可以又多个评论，所以这里要使用BmobRelation

	public BmobRelation getRelation() {
		return relation;
	}
	public void setRelation(BmobRelation relation) {
		this.relation = relation;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public BmobFile getContentfigureurl() {
		return Contentfigureurl;
	}
	public void setContentfigureurl(BmobFile contentfigureurl) {
		Contentfigureurl = contentfigureurl;
	}
	public int getLove() {
		return love;
	}
	public void setLove(int love) {
		this.love = love;
	}
	public int getHate() {
		return hate;
	}
	public void setHate(int hate) {
		this.hate = hate;
	}
	public int getShare() {
		return share;
	}
	public void setShare(int share) {
		this.share = share;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public boolean isPass() {
		return isPass;
	}
	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
	public boolean getMyFav() {
		return myFav;
	}
	public void setMyFav(boolean myFav) {
		this.myFav = myFav;
	}
	public boolean getMyLove() {
		return myLove;
	}
	public void setMyLove(boolean myLove) {
		this.myLove = myLove;
	}
	@Override
	public String toString() {
		return "QiangYu [author=" + author + ", content=" + content
				+ ", Contentfigureurl=" + Contentfigureurl + ", love=" + love
				+ ", hate=" + hate + ", share=" + share + ", comment="
				+ comment + ", isPass=" + isPass + ", myFav=" + myFav
				+ ", myLove=" + myLove + ", relation=" + relation + "]";
	}
	
}
