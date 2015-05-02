package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.adapter.GameCardAdapter;
import com.bmob.im.demo.bean.User;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat.CustomAction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class GameCenterActivity extends ActivityBase {
	
	private ListView mListView;  
	
	public static User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_center);
		
		initTopBarForLeft("游戏中心");
		// 女性主题
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
		}
		
		user = userManager.getCurrentUser(User.class);
		
		 mListView=(ListView) findViewById(R.id.game_list_view);  
	     GameCardAdapter mAdapter=new GameCardAdapter(this, CustomApplcation.gameCardList);  
	     mListView.setAdapter(mAdapter);  
	     
//	     setActionBgForFemale();;
	}
	
	public void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
