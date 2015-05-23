package com.deep.ui.fragment.update;

import java.io.File;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.AboutActivity;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.HidingModeActivity;
import com.bmob.im.demo.ui.LoginActivity;
import com.bmob.im.demo.ui.PersonalDressActivity;
import com.bmob.im.demo.ui.SetMessageNotifyActivity;
import com.bmob.im.demo.util.FileSizeUtil;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.ui.update.MainActivity2;

public class SettingUpdateFragment extends FragmentBase implements OnClickListener{
	
	private Context mContext;
	
	RelativeLayout hidingModeLayout, 
	messageNitifyLayout, personDress, clearCacheLayout, about_find_layout,
	logOutLayout;
	TextView tv_cache_capacity;
	
	String cachePath;
	
	public SettingUpdateFragment() {
		super();
	}
	
	public SettingUpdateFragment(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_setting_update, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView() {
		
		cachePath = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
		
		hidingModeLayout = (RelativeLayout) findViewById(R.id.slide_layout_hiding_mode);
		messageNitifyLayout = (RelativeLayout) findViewById(R.id.slide_layout_message_notify);
		personDress = (RelativeLayout) findViewById(R.id.slide_layout_person_dress);
		clearCacheLayout = (RelativeLayout) findViewById(R.id.slide_layout_clear_cache);
		about_find_layout = (RelativeLayout) findViewById(R.id.slide_layout_about);
		logOutLayout = (RelativeLayout) findViewById(R.id.slide_layout_logout);
		
		tv_cache_capacity = (TextView) findViewById(R.id.slide_tv_set_cache_capacity);
		tv_cache_capacity.setText(FileSizeUtil.getAutoFileOrFilesSize(cachePath));
		
		hidingModeLayout.setOnClickListener(this);
		messageNitifyLayout.setOnClickListener(this);
		personDress.setOnClickListener(this);
		clearCacheLayout.setOnClickListener(this);
		about_find_layout.setOnClickListener(this);
		logOutLayout.setOnClickListener(this);
		
		
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent();
		switch (v.getId()) {
		// 隐身模式
		case R.id.slide_layout_hiding_mode:
			intent.setClass(mContext, HidingModeActivity.class);
			startActivity(intent);
			break;
		// 消息提醒
		case R.id.slide_layout_message_notify:
			intent.setClass(mContext, SetMessageNotifyActivity.class);		
			startActivity(intent);
			break;
		// 个性装扮
		case R.id.slide_layout_person_dress:
			intent.setClass(mContext, PersonalDressActivity.class);
			startActivity(intent);
			break;
		// 清除缓存
		case R.id.slide_layout_clear_cache:
			clearCache();
			break;
			
		//  关于Find
		case R.id.slide_layout_about:
			Intent intent2 = new Intent();
			intent2.setClass(mContext, AboutActivity.class);
			mContext.startActivity(intent2);
			break;
			
		// 退出登陆
		case R.id.slide_layout_logout:
			showLogOutDialog();
			break;
		}
	}
	
	public void showLogOutDialog() {
		
		DialogTips dialogTips = new DialogTips(mContext, "退出登陆后将无法收到别人的消息，是否继续？", "确定", "取消", "提示", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				BmobUser.logOut(mContext);
				Intent intent2 = new Intent();
				intent2.setClass(mContext, LoginActivity.class);
				mContext.startActivity(intent2);
				((MainActivity2)mContext).finish();
			}
		});
		
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dialogTips.show();
		
		
	}
	
	public void clearCache() {
		DialogTips dialogTips = new DialogTips(mContext, "清除缓存", "当前缓存大小为" + tv_cache_capacity.getText().toString() + "，是否清除缓存？",
				"清除", true, true);
		
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				FileSizeUtil.DeleteFile(new File(cachePath));
				
				// 刷新缓存大小
				tv_cache_capacity.setText(FileSizeUtil.getAutoFileOrFilesSize(cachePath));
			}
		});
		
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dialogTips.show();
	}

}
