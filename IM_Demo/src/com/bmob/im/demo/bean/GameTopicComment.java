package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;

public class GameTopicComment extends BmobObject {
	
	private static final long serialVersionUID = 1L;

	public static final String TAG = "Comment";

	private User user;  // ������������Ӧ���û�������һ�����۶�Ӧһ���û�����������Ҫ��User��Web�����ݿ���Ҫ��Point
	private String commentContent;
	
	public GameTopicComment() {
		
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	
}

