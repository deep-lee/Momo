package com.bmob.im.demo.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.GameTopicComment;

public class GameTopicCommentAdapter extends BaseContentAdapter<GameTopicComment> {
	
	private Context mContext;

	public GameTopicCommentAdapter(Context context, List<GameTopicComment> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_item, null);
			viewHolder.userName = (TextView)convertView.findViewById(R.id.userName_comment);
			viewHolder.commentContent = (TextView)convertView.findViewById(R.id.content_comment);
			viewHolder.index = (TextView)convertView.findViewById(R.id.index_comment);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		final GameTopicComment comment = dataList.get(position);
		if(comment.getUser()!=null){
			viewHolder.userName.setText(comment.getUser().getNick());
			Log.i("CommentActivity","NAME:"+comment.getUser().getNick());
		}else{
			viewHolder.userName.setText("г╫ся");
		}
		viewHolder.index.setText((position+1)+"б╔");
		viewHolder.commentContent.setText(comment.getCommentContent());
		
		return convertView;
	}
	
	public static class ViewHolder{
		public TextView userName;
		public TextView commentContent;
		public TextView index;
	}
 
}
