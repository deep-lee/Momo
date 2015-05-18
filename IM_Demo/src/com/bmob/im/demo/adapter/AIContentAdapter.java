package com.bmob.im.demo.adapter;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.QiangYu;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.ui.CommentActivity;
import com.bmob.im.demo.ui.LifeCircleActivity;
import com.bmob.im.demo.ui.NearPeopleMapActivity;
import com.bmob.im.demo.ui.SetMyInfoActivity2;
import com.bmob.im.demo.util.ActivityUtil;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.db.DatabaseUtil;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author kingofglory email: kingofglory@yeah.net blog: http:www.google.com
 * @date 2014-2-24 TODO
 */

public class AIContentAdapter extends BaseContentAdapter<QiangYu> {

	public static final String TAG = "AIContentAdapter";
	public static final int SAVE_FAVOURITE = 2;
	
	private Context mContext;
	
	private List<User> myFriends;

	public AIContentAdapter(Context context, List<QiangYu> list, List<User> friends) {
		super(context, list);
		// TODO Auto-generated constructor stub
		
		this.mContext = context;
		this.myFriends = friends;
		
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.ai_item, null);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.user_name);
			viewHolder.userLogo = (ImageView) convertView
					.findViewById(R.id.user_logo);
			viewHolder.favMark = (ImageView) convertView
					.findViewById(R.id.item_action_fav);
			viewHolder.contentText = (TextView) convertView
					.findViewById(R.id.content_text);
			viewHolder.contentImage = (ImageView) convertView
					.findViewById(R.id.content_image);
			viewHolder.love = (TextView) convertView
					.findViewById(R.id.item_action_love);
			viewHolder.hate = (TextView) convertView
					.findViewById(R.id.item_action_hate);
			viewHolder.share = (TextView) convertView
					.findViewById(R.id.item_action_share);
			viewHolder.comment = (TextView) convertView
					.findViewById(R.id.item_action_comment);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final QiangYu entity = dataList.get(position);
		// LogUtils.i("user", entity.toString());
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
		
		viewHolder.userLogo.setOnClickListener(new OnClickListener() {

			
			// 如果使好友或者本人就进入资料界面，如果使陌生人就进入游戏界面
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				User currentUser = CustomApplcation.getInstance().getCurrentUser();
				// 是本人
				if (entity.getAuthor().getUsername().equals(currentUser.getUsername())) {
					Intent intent = new Intent();
					intent.setClass(mContext, SetMyInfoActivity2.class);
					intent.putExtra("from", "me");
					mContext.startActivity(intent);
					
					return;
				}
				// 如果是好友
				if (isMyFriend(entity.getAuthor().getUsername())) {
					Intent intent =new Intent(mContext,SetMyInfoActivity2.class);
					intent.putExtra("from", "other");
					intent.putExtra("username", entity.getAuthor().getUsername());
					intent.putExtra("nick", entity.getAuthor().getNick());
					mContext.startActivity(intent);
					
					return;
				}
				
				// 如果是陌生人，则判断游戏类型，和有没有这个游戏
				Toast.makeText(mContext, "该用户并非您的好友，请在附近的人界面进行添加！", Toast.LENGTH_LONG).show();
			}
		});
		viewHolder.userName.setText(entity.getAuthor().getNick());
		viewHolder.contentText.setText(entity.getContent());
		if (null == entity.getContentfigureurl()) {
			viewHolder.contentImage.setVisibility(View.GONE);
		} else {
			viewHolder.contentImage.setVisibility(View.VISIBLE);
			ImageLoader
					.getInstance()
					.displayImage(
							entity.getContentfigureurl().getFileUrl(mContext) == null ? ""
									: entity.getContentfigureurl().getFileUrl(
											mContext),
							viewHolder.contentImage,
							CustomApplcation.getInstance().getOptions(
									R.drawable.bg_pic_loading),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									// TODO Auto-generated method stub
									super.onLoadingComplete(imageUri, view,
											loadedImage);
									float[] cons = ActivityUtil
											.getBitmapConfiguration(
													loadedImage,
													viewHolder.contentImage,
													1.0f);
									RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
											(int) cons[0], (int) cons[1]);
									layoutParams.addRule(RelativeLayout.BELOW,
											R.id.content_text);
									viewHolder.contentImage
											.setLayoutParams(layoutParams);
								}

							});
		}
		viewHolder.love.setText(entity.getLove() + "");
		Log.i("love", entity.getMyLove() + "..");
		if (entity.getMyLove()) {
			viewHolder.love.setTextColor(Color.parseColor("#D95555"));
		} else {
			viewHolder.love.setTextColor(Color.parseColor("#000000"));
		}
		viewHolder.hate.setText(entity.getHate() + "");
		viewHolder.love.setOnClickListener(new OnClickListener() {
			boolean oldFav = entity.getMyFav();

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (entity.getMyLove()) {
					
					return;
				}

				if (DatabaseUtil.getInstance(mContext).isLoved(entity)) {
					ActivityUtil.show(mContext, "您已赞过啦");
					entity.setMyLove(true);
					entity.setLove(entity.getLove() + 1);
					viewHolder.love.setTextColor(Color.parseColor("#D95555"));
					viewHolder.love.setText(entity.getLove() + "");
					return;
				}

				entity.setLove(entity.getLove() + 1);
				viewHolder.love.setTextColor(Color.parseColor("#D95555"));
				viewHolder.love.setText(entity.getLove() + "");

				entity.increment("love", 1);
				if (entity.getMyFav()) {
					entity.setMyFav(false);
				}
				entity.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						entity.setMyLove(true);
						entity.setMyFav(oldFav);
						DatabaseUtil.getInstance(mContext).insertFav(entity);
						// DatabaseUtil.getInstance(mContext).queryFav();
						Log.i(TAG, "点赞成功~");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						entity.setMyLove(true);
						entity.setMyFav(oldFav);
					}
				});
			}
		});
		viewHolder.hate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				entity.setHate(entity.getHate() + 1);
				viewHolder.hate.setText(entity.getHate() + "");
				entity.increment("hate", 1);
				entity.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ActivityUtil.show(mContext, "点踩成功~");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		viewHolder.share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// share to sociaty
				
			}
		});
		viewHolder.comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入评论界面
				// MyApplication.getInstance().setCurrentQiangYu(entity);
				
				Intent intent = new Intent();
				intent.setClass(mContext,
						CommentActivity.class);
				intent.putExtra("data", entity);
				mContext.startActivity(intent);
			}
		});

		if (entity.getMyFav()) {
			viewHolder.favMark
					.setImageResource(R.drawable.ic_action_fav_choose);
		} else {
			viewHolder.favMark
					.setImageResource(R.drawable.ic_action_fav_normal);
		}
		viewHolder.favMark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 收藏
				ActivityUtil.show(mContext, "收藏");
				onClickFav(v, entity);

			}
		});
		return convertView;
	}

	public Boolean isMyFriend(String username) {
		Boolean flag = false;
		
		for (Iterator<User> iterator = myFriends.iterator(); iterator.hasNext();) {
			User user = (User) iterator.next();
			
			if (user.getUsername().equals(username)) {
				flag = true;
				break;
			}
			
		}
		
		return flag;
	}
	
	public static class ViewHolder {
		public ImageView userLogo;
		public TextView userName;
		public TextView contentText;
		public ImageView contentImage;

		public ImageView favMark;
		public TextView love;
		public TextView hate;
		public TextView share;
		public TextView comment;
	}

	private void onClickFav(View v, final QiangYu qiangYu) {
		// TODO Auto-generated method stub
		User user = BmobUser.getCurrentUser(mContext, User.class);
		if (user != null && user.getSessionToken() != null) {
			BmobRelation favRelaton = new BmobRelation();

			qiangYu.setMyFav(!qiangYu.getMyFav());
			if (qiangYu.getMyFav()) {
				((ImageView) v)
						.setImageResource(R.drawable.ic_action_fav_choose);
				favRelaton.add(qiangYu);
				user.setFavorite(favRelaton);
				ActivityUtil.show(mContext, "收藏成功。");
				user.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						DatabaseUtil.getInstance(mContext).insertFav(qiangYu);
						Log.i(TAG, "收藏成功。");
						// try get fav to see if fav success
						// getMyFavourite();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i(TAG, "收藏失败。请检查网络~");
						ActivityUtil.show(mContext, "收藏失败。请检查网络~" + arg0);
					}
				});

			} else {
				((ImageView) v)
						.setImageResource(R.drawable.ic_action_fav_normal);
				favRelaton.remove(qiangYu);
				user.setFavorite(favRelaton);
				ActivityUtil.show(mContext, "取消收藏。");
				user.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						DatabaseUtil.getInstance(mContext).deleteFav(qiangYu);
						Log.i(TAG, "取消收藏。");
						// try get fav to see if fav success
						// getMyFavourite();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i(TAG, "取消收藏失败。请检查网络~");
						ActivityUtil.show(mContext, "取消收藏失败。请检查网络~" + arg0);
					}
				});
			}

		}
	}

	private void getMyFavourite() {
		User user = BmobUser.getCurrentUser(mContext, User.class);
		if (user != null) {
			BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
			query.addWhereRelatedTo("favorite", new BmobPointer(user));
			query.include("user");
			query.order("createdAt");
			query.setLimit(Config.NUMBERS_PER_PAGE);
			query.findObjects(mContext, new FindListener<QiangYu>() {

				@Override
				public void onSuccess(List<QiangYu> data) {
					// TODO Auto-generated method stub
					Log.i(TAG, "get fav success!" + data.size());
					ActivityUtil.show(mContext, "fav size:" + data.size());
				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ActivityUtil.show(mContext, "获取收藏失败。请检查网络~");
				}
			});
		}
	}
	
}