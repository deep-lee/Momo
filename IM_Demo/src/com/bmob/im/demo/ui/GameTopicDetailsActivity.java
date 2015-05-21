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
import com.bmob.im.demo.adapter.GameTopicCommentAdapter;
import com.bmob.im.demo.bean.GameTopic;
import com.bmob.im.demo.bean.GameTopicComment;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.util.ActivityUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GameTopicDetailsActivity extends BaseActivity implements OnClickListener{
	
public static String TAG = "GameTopicDetailsActivity";
	
	private ListView commentList;
	private ImageView game_topic_author_icon;
	private TextView game_topic_author_name;
	private TextView game_topic_create_time;
	
	private TextView game_topic_content;

	private TextView tv_loade_more;
	
	private EditText commentContent;
	private Button commentCommit;

	private GameTopic gameTopic;
	private String commentEdit = "";

	private List<GameTopicComment> comments = new ArrayList<GameTopicComment>();
	
	private GameTopicCommentAdapter mAdapter;

	private int pageNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_topic_details);
		
		initView();
	}
	
	public void initView() {
		commentList = (ListView) findViewById(R.id.game_topic_comment_list);
		game_topic_author_icon = (ImageView) findViewById(R.id.game_topic_author_icon);
		game_topic_author_name = (TextView) findViewById(R.id.game_topic_author_name);
		game_topic_create_time = (TextView) findViewById(R.id.game_topic_update_time);
		game_topic_content = (TextView) findViewById(R.id.game_topic_content_text);
		tv_loade_more = (TextView) findViewById(R.id.game_topic_loadmore);
		commentContent = (EditText) findViewById(R.id.game_topic_comment_content);
		commentCommit = (Button) findViewById(R.id.game_topic_comment_commit);
		
		setListener();
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		gameTopic = (GameTopic) getIntent().getSerializableExtra("data");
		
		pageNum = 0;
		
		mAdapter = new GameTopicCommentAdapter(GameTopicDetailsActivity.this, comments);
		commentList.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ActivityUtil.show(GameTopicDetailsActivity.this, "po" + position);
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);

		initMoodView(gameTopic);
		
		fetchData();
	}
	
	private void initMoodView(GameTopic mood2) {
		// TODO Auto-generated method stub
		if (mood2 == null) {
			return;
		}
		game_topic_author_name.setText(gameTopic.getAuthor().getNick());
		game_topic_content.setText(gameTopic.getContent());


		User user = gameTopic.getAuthor();
		BmobFile avatar = new BmobFile();
		avatar.setUrl(user.getAvatar());
		
		if (null != avatar) {
			ImageLoader.getInstance().displayImage(
					avatar.getFileUrl(GameTopicDetailsActivity.this),
					game_topic_author_icon,
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
	
	
	public void fetchData() {
		// TODO Auto-generated method stub
		fetchComment();
	}

	private void fetchComment() {
		
		BmobQuery<GameTopicComment> query = new BmobQuery<GameTopicComment>();
		query.addWhereRelatedTo("commentOfTopic", new BmobPointer(gameTopic));
		query.include("user");
		query.order("createdAt");
		query.setLimit(Config.NUMBERS_PER_PAGE);
		query.setSkip(Config.NUMBERS_PER_PAGE * (pageNum++));
		query.findObjects(this, new FindListener<GameTopicComment>() {

			@Override
			public void onSuccess(List<GameTopicComment> data) {
				// TODO Auto-generated method stub
				Log.i(TAG, "get comment success!" + data.size());
				if (data.size() != 0 && data.get(data.size() - 1) != null) {

					if (data.size() < Config.NUMBERS_PER_PAGE) {
						ActivityUtil.show(GameTopicDetailsActivity.this, "已加载完所有评论~");
						tv_loade_more.setText("暂无更多评论~");
					}

					mAdapter.getDataList().addAll(data);
					mAdapter.notifyDataSetChanged();
					setListViewHeightBasedOnChildren(commentList);
					Log.i(TAG, "refresh");
				} else {
					ActivityUtil.show(GameTopicDetailsActivity.this, "暂无更多评论~");
					tv_loade_more.setText("暂无更多评论~");
					pageNum--;
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ActivityUtil.show(GameTopicDetailsActivity.this, "获取评论失败。请检查网络~");
				pageNum--;
			}
		});
	}
	
	public void setListener() {
		// TODO Auto-generated method stub
		tv_loade_more.setOnClickListener(this);
		commentCommit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.game_topic_loadmore:
			onClickLoadMore();
			break;
			
		case R.id.game_topic_comment_commit:
			onClickCommit();
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

		final GameTopicComment comment = new GameTopicComment();
		comment.setUser(user);
		comment.setCommentContent(content);
		comment.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ActivityUtil.show(GameTopicDetailsActivity.this, "评论成功。");
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
				gameTopic.setCommentOfTopic(relation);
				gameTopic.setComment(gameTopic.getComment() + 1);
				gameTopic.update(GameTopicDetailsActivity.this, new UpdateListener() {

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
				ActivityUtil.show(GameTopicDetailsActivity.this, "评论失败。请检查网络~");
			}
		});
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
