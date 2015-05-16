package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.CommentAdapter;
import com.bmob.im.demo.bean.Comment;
import com.bmob.im.demo.bean.QiangYu;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.util.ActivityUtil;
import com.deep.db.DatabaseUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CommentActivity extends BaseActivity implements OnClickListener{
	
	public static String TAG = "CommentActivity";
	
	private ListView commentList;
	private TextView footer;

	private EditText commentContent;
	private Button commentCommit;

	private TextView userName;
	private TextView commentItemContent;
	private ImageView commentItemImage;

	private ImageView userLogo;
	private ImageView myFav;
	private TextView comment;
	private TextView share;
	private TextView love;
	private TextView hate;

	private QiangYu qiangYu;
	private String commentEdit = "";

	private CommentAdapter mAdapter;

	private List<Comment> comments = new ArrayList<Comment>();

	private int pageNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		initView();
	}
	
	public void initView() {
		commentList = (ListView) findViewById(R.id.comment_list);
		footer = (TextView) findViewById(R.id.loadmore);

		commentContent = (EditText) findViewById(R.id.comment_content);
		commentCommit = (Button) findViewById(R.id.comment_commit);

		userName = (TextView) findViewById(R.id.user_name);
		commentItemContent = (TextView) findViewById(R.id.content_text);
		commentItemImage = (ImageView) findViewById(R.id.content_image);

		userLogo = (ImageView) findViewById(R.id.user_logo);
		myFav = (ImageView) findViewById(R.id.item_action_fav);
		comment = (TextView) findViewById(R.id.item_action_comment);
		share = (TextView) findViewById(R.id.item_action_share);
		love = (TextView) findViewById(R.id.item_action_love);
		hate = (TextView) findViewById(R.id.item_action_hate);
		
		setListener();
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		qiangYu = (QiangYu) getIntent().getSerializableExtra("data");// MyApplication.getInstance().getCurrentQiangYu();
		pageNum = 0;
		
		mAdapter = new CommentAdapter(CommentActivity.this, comments);
		commentList.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ActivityUtil.show(CommentActivity.this, "po" + position);
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);

		initMoodView(qiangYu);
	}
	
	private void initMoodView(QiangYu mood2) {
		// TODO Auto-generated method stub
		if (mood2 == null) {
			return;
		}
		userName.setText(qiangYu.getAuthor().getNick());
		commentItemContent.setText(qiangYu.getContent());
		if (null == qiangYu.getContentfigureurl()) {
			commentItemImage.setVisibility(View.GONE);
		} else {
			commentItemImage.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					qiangYu.getContentfigureurl().getFileUrl(
							CommentActivity.this) == null ? "" : qiangYu
							.getContentfigureurl().getFileUrl(
									CommentActivity.this),
					commentItemImage,
					CustomApplcation.getInstance().getOptions(
							R.drawable.bg_pic_loading),
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							super.onLoadingComplete(imageUri, view, loadedImage);
							float[] cons = ActivityUtil.getBitmapConfiguration(
									loadedImage, commentItemImage, 1.0f);
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									(int) cons[0], (int) cons[1]);
							layoutParams.addRule(RelativeLayout.BELOW,
									R.id.content_text);
							commentItemImage.setLayoutParams(layoutParams);
						}

					});
		}

		love.setText(qiangYu.getLove() + "");
		if (qiangYu.getMyLove()) {
			love.setTextColor(Color.parseColor("#D95555"));
		} else {
			love.setTextColor(Color.parseColor("#000000"));
		}
		hate.setText(qiangYu.getHate() + "");
		if (qiangYu.getMyFav()) {
			myFav.setImageResource(R.drawable.ic_action_fav_choose);
		} else {
			myFav.setImageResource(R.drawable.ic_action_fav_normal);
		}

		User user = qiangYu.getAuthor();
		BmobFile avatar = new BmobFile();
		avatar.setUrl(user.getAvatar());
		
		if (null != avatar) {
			ImageLoader.getInstance().displayImage(
					avatar.getFileUrl(CommentActivity.this),
					userLogo,
					CustomApplcation.getInstance().getOptions(
							R.drawable.content_image_default),
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							super.onLoadingComplete(imageUri, view, loadedImage);
							Log.i(TAG, "load personal icon completed.");
						}

					});
		}
	}

	public void setListener() {
		// TODO Auto-generated method stub
		footer.setOnClickListener(this);
		commentCommit.setOnClickListener(this);

		userLogo.setOnClickListener(this);
		myFav.setOnClickListener(this);
		love.setOnClickListener(this);
		hate.setOnClickListener(this);
		share.setOnClickListener(this);
		comment.setOnClickListener(this);
	}

	public void fetchData() {
		// TODO Auto-generated method stub
		fetchComment();
	}

	private void fetchComment() {
		
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("relation", new BmobPointer(qiangYu));
		query.include("user");
		query.order("createdAt");
		query.setLimit(Config.NUMBERS_PER_PAGE);
		query.setSkip(Config.NUMBERS_PER_PAGE * (pageNum++));
		query.findObjects(this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> data) {
				// TODO Auto-generated method stub
				Log.i(TAG, "get comment success!" + data.size());
				if (data.size() != 0 && data.get(data.size() - 1) != null) {

					if (data.size() < Config.NUMBERS_PER_PAGE) {
						ActivityUtil.show(CommentActivity.this, "已加载完所有评论~");
						footer.setText("暂无更多评论~");
					}

					mAdapter.getDataList().addAll(data);
					mAdapter.notifyDataSetChanged();
					setListViewHeightBasedOnChildren(commentList);
					Log.i(TAG, "refresh");
				} else {
					ActivityUtil.show(CommentActivity.this, "暂无更多评论~");
					footer.setText("暂无更多评论~");
					pageNum--;
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ActivityUtil.show(CommentActivity.this, "获取评论失败。请检查网络~");
				pageNum--;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.user_logo:
			// onClickUserLogo();
			break;
		case R.id.loadmore:
			onClickLoadMore();
			break;
		case R.id.comment_commit:
			onClickCommit();
			break;
		case R.id.item_action_fav:
			onClickFav(v);
			break;
		case R.id.item_action_love:
			onClickLove();
			break;
		case R.id.item_action_hate:
			onClickHate();
			break;
		case R.id.item_action_share:
			// onClickShare();
			break;
		case R.id.item_action_comment:
			onClickComment();
			break;
		default:
			break;
		}
	}
	
	private void onClickLoadMore() {
		// TODO Auto-generated method stub
		fetchData();
	}

	private void onClickCommit() {
		// TODO Auto-generated method stub
		User currentUser = BmobUser.getCurrentUser(this, User.class);
		if (currentUser != null) {// 已登录
			commentEdit = commentContent.getText().toString().trim();
			if (TextUtils.isEmpty(commentEdit)) {
				ActivityUtil.show(this, "评论内容不能为空。");
				return;
			}
			// comment now
			publishComment(currentUser, commentEdit);
		}

	}

	private void publishComment(User user, String content) {

		final Comment comment = new Comment();
		comment.setUser(user);
		comment.setCommentContent(content);
		comment.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ActivityUtil.show(CommentActivity.this, "评论成功。");
				if (mAdapter.getDataList().size() < Config.NUMBERS_PER_PAGE) {
					mAdapter.getDataList().add(comment);
					mAdapter.notifyDataSetChanged();
					setListViewHeightBasedOnChildren(commentList);
				}
				commentContent.setText("");
				hideSoftInput();

				// 将该评论与强语绑定到一起
				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				qiangYu.setRelation(relation);
				qiangYu.update(CommentActivity.this, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.i(TAG, "更新评论成功。");
						// fetchData();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i(TAG, "更新评论失败。" + arg1);
					}
				});

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ActivityUtil.show(CommentActivity.this, "评论失败。请检查网络~");
			}
		});
	}

	private void onClickFav(View v) {
		// TODO Auto-generated method stub

		User user = BmobUser.getCurrentUser(this, User.class);
		if (user != null && user.getSessionToken() != null) {
			BmobRelation favRelaton = new BmobRelation();
			qiangYu.setMyFav(!qiangYu.getMyFav());
			if (qiangYu.getMyFav()) {
				((ImageView) v)
						.setImageResource(R.drawable.ic_action_fav_choose);
				favRelaton.add(qiangYu);
				ActivityUtil.show(CommentActivity.this, "收藏成功。");
			} else {
				((ImageView) v)
						.setImageResource(R.drawable.ic_action_fav_normal);
				favRelaton.remove(qiangYu);
				ActivityUtil.show(CommentActivity.this, "取消收藏。");
			}

			user.setFavorite(favRelaton);
			user.update(this, new UpdateListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Log.i(TAG, "收藏成功。");
					ActivityUtil.show(CommentActivity.this, "收藏成功。");
					// try get fav to see if fav success
					// getMyFavourite();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.i(TAG, "收藏失败。请检查网络~");
					ActivityUtil.show(CommentActivity.this, "收藏失败。请检查网络~"
							+ arg0);
				}
			});
		}

	}

	private void getMyFavourite() {
		User user = BmobUser.getCurrentUser(this, User.class);
		if (user != null) {
			BmobQuery<QiangYu> query = new BmobQuery<QiangYu>();
			query.addWhereRelatedTo("favorite", new BmobPointer(user));
			query.include("user");
			query.order("createdAt");
			query.setLimit(Config.NUMBERS_PER_PAGE);
			query.findObjects(this, new FindListener<QiangYu>() {

				@Override
				public void onSuccess(List<QiangYu> data) {
					// TODO Auto-generated method stub
					Log.i(TAG, "get fav success!" + data.size());
					ActivityUtil.show(CommentActivity.this,
							"fav size:" + data.size());
				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ActivityUtil.show(CommentActivity.this, "获取收藏失败。请检查网络~");
				}
			});
		}
	}

	boolean isFav = false;

	private void onClickLove() {
		// TODO Auto-generated method stub
		User user = BmobUser.getCurrentUser(this, User.class);
		
		if (qiangYu.getMyLove()) {
			ActivityUtil.show(CommentActivity.this, "您已经赞过啦");
			return;
		}
		isFav = qiangYu.getMyFav();
		if (isFav) {
			qiangYu.setMyFav(false);
		}
		qiangYu.setLove(qiangYu.getLove() + 1);
		love.setTextColor(Color.parseColor("#D95555"));
		love.setText(qiangYu.getLove() + "");
		qiangYu.increment("love", 1);
		qiangYu.update(CommentActivity.this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				qiangYu.setMyLove(true);
				qiangYu.setMyFav(isFav);
				DatabaseUtil.getInstance(CommentActivity.this).insertFav(qiangYu);

				ActivityUtil.show(CommentActivity.this, "点赞成功~");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void onClickHate() {
		// TODO Auto-generated method stub
		qiangYu.setHate(qiangYu.getHate() + 1);
		hate.setText(qiangYu.getHate() + "");
		qiangYu.increment("hate", 1);
		qiangYu.update(CommentActivity.this, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ActivityUtil.show(CommentActivity.this, "点踩成功~");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void onClickComment() {
		// TODO Auto-generated method stub
		commentContent.requestFocus();

		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(commentContent, 0);
	}

	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
	}
	
	/***
	 * 动态设置listview的高度 item 总布局必须是linearLayout
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ 15;
		listView.setLayoutParams(params);
	}
	
	
}
