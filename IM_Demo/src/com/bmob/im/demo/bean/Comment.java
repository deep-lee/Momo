package com.bmob.im.demo.bean;

import cn.bmob.v3.BmobObject;


/**
 * @author kingofglory
 *         email: kingofglory@yeah.net
 *         blog:  http:www.google.com
 * @date 2014-4-2
 * TODO
 */

public class Comment extends BmobObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TAG = "Comment";

	private User user;  // ������������Ӧ���û�������һ�����۶�Ӧһ���û�����������Ҫ��User��Web�����ݿ���Ҫ��Point
	private String commentContent;
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
