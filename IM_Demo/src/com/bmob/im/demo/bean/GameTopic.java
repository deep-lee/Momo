package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class GameTopic extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User author;  // ����
	private String content;  // ����
	private int commentNum; // ������
	private BmobRelation commentOfTopic; // ��Ӧ�����ۣ�����һ��״̬�����ֶ�����ۣ���������Ҫʹ��BmobRelation
	
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
