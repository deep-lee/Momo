package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class GameTopic extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User author;  // 作者
	private String content;  // 内容
	private int commentNum; // 评论数
	private BmobRelation commentOfTopic; // 对应的评论，由于一个状态可以又多个评论，所以这里要使用BmobRelation
	
	public GameTopic(){
		
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
	public int getComment() {
		return commentNum;
	}
	public void setComment(int commentNum) {
		this.commentNum = commentNum;
	}
	public BmobRelation getCommentOfTopic() {
		return commentOfTopic;
	}
	public void setCommentOfTopic(BmobRelation commentOfTopic) {
		this.commentOfTopic = commentOfTopic;
	}
	
	
}
