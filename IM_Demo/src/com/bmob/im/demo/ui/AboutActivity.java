package com.bmob.im.demo.ui;

import java.util.List;

import cn.bmob.im.BmobNotifyManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.Update;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.util.DownloadService;
import com.bmob.im.demo.view.dialog.DialogTips;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class AboutActivity extends BaseActivity implements OnClickListener{
	
	RelativeLayout rl_welcome_page, rl_help_and_feedback, rl_check_for_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initView();
	}
	
	public void initView() {
		rl_welcome_page = (RelativeLayout) findViewById(R.id.about_layout_welcome_page);
		rl_help_and_feedback = (RelativeLayout) findViewById(R.id.about_layout_help_and_feedback);
		rl_check_for_update = (RelativeLayout) findViewById(R.id.about_layout_check_for_update);
		
		rl_welcome_page.setOnClickListener(this);
		rl_help_and_feedback.setOnClickListener(this);
		rl_check_for_update.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.about_layout_welcome_page:
			Intent welcomePageIntent = new Intent();
			welcomePageIntent.setClass(AboutActivity.this, FirstGuildActivity.class);
			welcomePageIntent.putExtra("flag", true);
			startActivity(welcomePageIntent);
			break;
			
		case R.id.about_layout_help_and_feedback:
			
			break;
			
		case R.id.about_layout_check_for_update:
			checkForUpdate();
			break;

		default:
			break;
		}
	}
	
	public void checkForUpdate() {
		BmobQuery<Update> query = new BmobQuery<Update>();
		query.addWhereGreaterThan("versionNum", Config.versionNum);
		query.order("versionNum");
		query.findObjects(AboutActivity.this, new FindListener<Update>() {
			
			@Override
			public void onSuccess(List<Update> arg0) {
				// TODO Auto-generated method stub
				if (arg0.size() == 0) {
					ShowToast("Find已经是最新版本，不需要更新了哦～");
				}else {
					// 最新的版本
					Update update = arg0.get(0);
					showUpdateDialog(update);
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast(R.string.network_tips);
			}
		});
	}
	
	public void showUpdateDialog(final Update update) {
		DialogTips dialogTips = new DialogTips(AboutActivity.this, update.getUpdateLog(), "下载更新", "暂不更新", "更新", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 下载并安装
				// 开启线程，下载文件		
				DownloadService.downNewFile(update.getApkFile().getFileUrl(AboutActivity.this),
						update.getVersionNum(), "Find " + update.getVersion());
			}
		});
		
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 在通知栏显示更新通知
				
				boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
				if(isAllow){
					CustomApplcation.getInstance().getMediaPlayer().start();
				}
				
				String tickerText = "Find " + update.getVersion() + "更新可供下载";
				boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
				showNotifyMessage(isAllow, isAllowVibrate, R.drawable.ic_launcher, tickerText, "更新", tickerText.toString());
			}
		});
		
		dialogTips.show();
	}
	
	public void showNotifyMessage(Boolean isAllow, Boolean isAllowVibrate, int notifyIcon, String tickerText,
			String contentTitle, String contentText) {
		BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, notifyIcon, tickerText, contentTitle, contentText, AboutActivity.class);
	}
}
