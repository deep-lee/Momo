package com.bmob.im.demo.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.GameTopic;
import com.bmob.im.demo.bean.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class GTContentAdapter extends BaseContentAdapter<GameTopic> {
	
	public static final String TAG = "GameTopicContentAdapter";
	
	private Context mContext;

	public GTContentAdapter(Context context, List<GameTopic> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.game_topic_item, null);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.game_topic_author_name);
			viewHolder.userLogo = (ImageView) convertView
					.findViewById(R.id.game_topic_author_icon);
			viewHolder.contentText = (TextView) convertView
					.findViewById(R.id.game_topic_content_text);
			viewHolder.updateTime = (TextView) convertView.findViewById(R.id.game_topic_update_time);
			viewHolder.comment = (TextView) convertView.findViewById(R.id.game_topic_comment);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final GameTopic entity = dataList.get(position);
		
		User user = entity.getAuthor();
		if (user == null) {
			Log.i("user", "USER IS NULL");
		}
		if (user.getAvatar() == null) {
			Log.i("user", "USER avatar IS NULL");
		}
		String avatarUrl = null;
		if (user.getAvatar() != null) {
			avatarUrl = user.getAvatar();
		}
		ImageLoader.getInstance().displayImage(
				avatarUrl,
				viewHolder.userLogo,
				CustomApplcation.getInstance().getOptions(
						R.drawable.user_icon_default_main),
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
					}

				});
		
		viewHolder.userName.setText(user.getNick());
		viewHolder.contentText.setText(entity.getContent());
		viewHolder.updateTime.setText(entity.getUpdatedAt());
		viewHolder.comment.setText("ÆÀÂÛ(" + entity.getComment() + ")");
		
		return convertView;
	}
	
	public static class ViewHolder {
		public ImageView userLogo;
		public TextView userName;
		public TextView contentText;
		public TextView comment;
		public TextView updateTime;
	}

}
