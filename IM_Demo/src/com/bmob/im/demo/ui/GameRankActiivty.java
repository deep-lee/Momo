package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.GameRankInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GameRankAdapter;
import com.bmob.im.demo.bean.DefaultGameFile;
import com.bmob.im.demo.bean.GameFile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

public class GameRankActiivty extends BaseActivity {
	
	private ListView mList;
	
	private List<GameRankInfo> mData;
	
	GameRankAdapter mAdapter;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
            		mAdapter.updateData(mData);
    				break;
            
            }   
            super.handleMessage(msg);   
       }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_rank_actiivty);
		
		initView();
	}
	
	private void initView(){
		mData = new ArrayList<GameRankInfo>();
		mList = (ListView) findViewById(R.id.game_rank_list);
		mAdapter = new GameRankAdapter(GameRankActiivty.this, mData);
		
		mList.setAdapter(mAdapter);
		
		initData();
	}
	
	public void initData() {
		new Thread(){
			@Override
			public void run(){
				BmobQuery<DefaultGameFile> defaultQuery = new BmobQuery<DefaultGameFile>();
				defaultQuery.findObjects(GameRankActiivty.this, new FindListener<DefaultGameFile>() {
					
					@Override
					public void onSuccess(List<DefaultGameFile> arg0) {
						// TODO Auto-generated method stub
						if (arg0.size() > 0) {
							for (Iterator<DefaultGameFile> iterator = arg0.iterator(); iterator
									.hasNext();) {
								DefaultGameFile defaultGameFile = (DefaultGameFile) iterator
										.next();
								
								Log.i("GameRankActivity", "Test" + defaultGameFile.getBestUserAvatar());
								GameRankInfo gameRankInfo = new GameRankInfo(defaultGameFile.getBestScore(), defaultGameFile.getBestUsername(), 
										defaultGameFile.getBestUserNick(), defaultGameFile.getBestUserAvatar(),defaultGameFile.getGameName());
								mData.add(gameRankInfo);
								
							}
						}
						
						BmobQuery<GameFile> query = new BmobQuery<GameFile>();
						query.findObjects(GameRankActiivty.this, new FindListener<GameFile>() {
							
							@Override
							public void onSuccess(List<GameFile> arg0) {
								// TODO Auto-generated method stub
								if (arg0.size() > 0) {
									for (Iterator<GameFile> iterator = arg0.iterator(); iterator
											.hasNext();) {
										GameFile defaultGameFile = (GameFile) iterator
												.next();
										
										GameRankInfo gameRankInfo = new GameRankInfo(defaultGameFile.getBestScore(), defaultGameFile.getBestUsername(),
												defaultGameFile.getBestUserNick(), defaultGameFile.getBestUserAvatar(), defaultGameFile.getGameName());
										mData.add(gameRankInfo);
										
									}
								}
								
								Message message = new Message();
								message.what = 0;
								handler.sendMessage(message);
							}
							
							@Override
							public void onError(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ShowToast(R.string.network_tips);
							}
						});
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast(R.string.network_tips);
					}
				});
				
				
			}
		}.start();
	}
}
