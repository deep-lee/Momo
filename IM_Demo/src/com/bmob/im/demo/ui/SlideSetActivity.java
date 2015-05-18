package com.bmob.im.demo.ui;

import java.io.File;

import com.bmob.im.demo.R;
import com.bmob.im.demo.util.FileSizeUtil;
import com.bmob.im.demo.view.dialog.DialogTips;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SlideSetActivity extends ActivityBase implements View.OnClickListener{

	RelativeLayout accountSafeLayout, accountBangdingLayout, hidingModeLayout, messageNitifyLayout, personDress, emojStoreLayout, clearCacheLayout;
	TextView tv_cache_capacity;
	
	String cachePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_set);
		initView();
		
	}
	
	private void initView() {
		
		cachePath = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
		
		accountSafeLayout = (RelativeLayout) findViewById(R.id.slide_layout_account_safe);
		accountBangdingLayout = (RelativeLayout) findViewById(R.id.slide_layout_account_bangding);
		hidingModeLayout = (RelativeLayout) findViewById(R.id.slide_layout_hiding_mode);
		messageNitifyLayout = (RelativeLayout) findViewById(R.id.slide_layout_message_notify);
		personDress = (RelativeLayout) findViewById(R.id.slide_layout_person_dress);
		emojStoreLayout = (RelativeLayout) findViewById(R.id.slide_layout_emoj_store);
		clearCacheLayout = (RelativeLayout) findViewById(R.id.slide_layout_clear_cache);
		
		tv_cache_capacity = (TextView) findViewById(R.id.slide_tv_set_cache_capacity);
		tv_cache_capacity.setText(FileSizeUtil.getAutoFileOrFilesSize(cachePath));
		
		accountBangdingLayout.setOnClickListener(this);
		accountSafeLayout.setOnClickListener(this);
		hidingModeLayout.setOnClickListener(this);
		messageNitifyLayout.setOnClickListener(this);
		personDress.setOnClickListener(this);
		emojStoreLayout.setOnClickListener(this);
		clearCacheLayout.setOnClickListener(this);
		
		
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent();
		switch (v.getId()) {
		
		// 账户安全
		case R.id.slide_layout_account_safe:
			
			break;
		// 账户绑定
		case R.id.slide_layout_account_bangding:
						
			break;
		// 隐身模式
		case R.id.slide_layout_hiding_mode:
			intent.setClass(SlideSetActivity.this, HidingModeActivity.class);
			startActivity(intent);
			break;
		// 消息提醒
		case R.id.slide_layout_message_notify:
			intent.setClass(SlideSetActivity.this, SetMessageNotifyActivity.class);		
			startActivity(intent);
			break;
		// 个性装扮
		case R.id.slide_layout_person_dress:
						
			break;
		// 表情商城
		case R.id.slide_layout_emoj_store:
						
			break;
		// 清除缓存
		case R.id.slide_layout_clear_cache:
			clearCache();
			break;
		}
	}
	
	public void clearCache() {
		DialogTips dialogTips = new DialogTips(SlideSetActivity.this, "清除缓存", "当前缓存大小为" + tv_cache_capacity.getText().toString() + "，是否清除缓存？",
				"清除", true, true);
		
		dialogTips.SetOnSuccessListener(new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				FileSizeUtil.DeleteFile(new File(cachePath));
				
				// 刷新缓存大小
				tv_cache_capacity.setText(FileSizeUtil.getAutoFileOrFilesSize(cachePath));
			}
		});
		
		dialogTips.SetOnCancelListener(new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dialogTips.show();
	}
}
