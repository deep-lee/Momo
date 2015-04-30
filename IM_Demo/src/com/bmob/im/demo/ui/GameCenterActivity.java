package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.adapter.GameCardAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat.CustomAction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class GameCenterActivity extends BaseActivity {
	
	private ListView mListView;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_center);
		
		initTopBarForLeft("��Ϸ����");
		
		 mListView=(ListView) findViewById(R.id.game_list_view);  
	     GameCardAdapter mAdapter=new GameCardAdapter(this, CustomApplcation.gameCardList);  
	     mListView.setAdapter(mAdapter);  
	}
	
}