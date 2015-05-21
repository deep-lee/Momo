package com.bmob.im.demo.ui;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.GameTopic;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ActivityUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class WriteGameTopicActivity extends BaseActivity {
	
	public static String TAG = "WriteGameTopicActivity";
	
	EditText content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_game_topic);
		
		initView();
	}
	
	public void initView() {
		content = (EditText) findViewById(R.id.edit_content);
		
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}
	
	// 发布状态
	public void publishTopic(View view) {
			
		String commitContent = content.getText().toString().trim();
		if (TextUtils.isEmpty(commitContent)) {
			ActivityUtil.show(WriteGameTopicActivity.this, "内容不能为空");
			return;
		}
		
		publish(commitContent);
	}
	
	private void publish(final String commitContent) {
		
		final User user = BmobUser.getCurrentUser(WriteGameTopicActivity.this, User.class);
		
		// 每个列表也就是发布的每个状态的内容
		final GameTopic gameTopic = new GameTopic();
		gameTopic.setAuthor(user);
		gameTopic.setContent(commitContent);
		gameTopic.setComment(0);
		gameTopic.save(WriteGameTopicActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ActivityUtil.show(WriteGameTopicActivity.this, "发表成功！");
				Log.i(TAG, "创建成功。");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ActivityUtil.show(WriteGameTopicActivity.this, "发表失败！yg" + arg1);
				Log.i(TAG, "创建失败。" + arg1);
			}
		});
	}
}
