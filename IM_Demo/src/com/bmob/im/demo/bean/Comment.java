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

	private User user;  // 单条评论所对应的用户，由于一条评论对应一个用户，所以这里要用User，Web端数据库种要用Point
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
