package com.bmob.im.demo.ui;

import java.util.List;

import cn.bmob.im.BmobNotifyManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateStatus;
import cn.bmob.v3.update.UpdateResponse;

import com.baidu.location.ab;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.Update;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.util.DownloadService;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.ui.update.MainActivity2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AboutActivity extends BaseActivity implements OnClickListener{
	
	RelativeLayout rl_welcome_page, rl_help_and_feedback, rl_check_for_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		BmobUpdateAgent.initAppVersion(AboutActivity.this);
		BmobUpdateAgent.initAppVersion(AboutActivity.this);
		initView();
	}
	
	public void initView() {
		rl_welcome_page = (RelativeLayout) findViewById(R.id.about_layout_welcome_page);
		rl_help_and_feedback = (RelativeLayout) findViewById(R.id.about_layout_help_and_feedback);
		rl_check_for_update = (RelativeLayout) findViewById(R.id.about_layout_check_for_update);
		
		rl_welcome_page.setOnClickListener(this);
		rl_help_and_feedback.setOnClickListener(this);
		rl_check_for_update.setOnClickListener(this);
		
		BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

	        @Override
	        public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
	            // TODO Auto-generated method stub
	            if (updateStatus == UpdateStatus.Yes) {//�汾�и���

	            }else if(updateStatus == UpdateStatus.No){
	                Toast.makeText(AboutActivity.this, "�汾�޸���", Toast.LENGTH_SHORT).show();
	            }else if(updateStatus==UpdateStatus.EmptyField){//����ʾֻ�����ѿ����߹�ע��Щ��������Գɹ���������û���ʾ
	                Toast.makeText(AboutActivity.this, "������AppVersion��ı����1��target_size���ļ���С���Ƿ���д��2��path����android_url���߱�������һ�", Toast.LENGTH_SHORT).show();
	            }else if(updateStatus==UpdateStatus.IGNORED){
	                Toast.makeText(AboutActivity.this, "�ð汾�ѱ����Ը���", Toast.LENGTH_SHORT).show();
	            }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
	                Toast.makeText(AboutActivity.this, "����target_size��д�ĸ�ʽ����ʹ��file.length()������ȡapk��С��", Toast.LENGTH_SHORT).show();
	            }else if(updateStatus==UpdateStatus.TimeOut){
	                Toast.makeText(AboutActivity.this, "��ѯ������ѯ��ʱ", Toast.LENGTH_SHORT).show();
	            }
	        }
	    });
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
			// checkForUpdate();
			BmobUpdateAgent.forceUpdate(AboutActivity.this);
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
					ShowToast("Find�Ѿ������°汾������Ҫ������Ŷ��");
				}else {
					// ���µİ汾
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
		DialogTips dialogTips = new DialogTips(AboutActivity.this, update.getUpdateLog(), "���ظ���", "�ݲ�����", "����", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// ���ز���װ
				// �����̣߳������ļ�		
				DownloadService.downNewFile(update.getApkFile().getFileUrl(AboutActivity.this),
						update.getVersionNum(), "Find " + update.getVersion());
			}
		});
		
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// ��֪ͨ����ʾ����֪ͨ
				
				boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
				if(isAllow){
					CustomApplcation.getInstance().getMediaPlayer().start();
				}
				
				String tickerText = "Find " + update.getVersion() + "���¿ɹ�����";
				boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
				showNotifyMessage(isAllow, isAllowVibrate, R.drawable.ic_launcher, tickerText, "����", tickerText.toString());
			}
		});
		
		dialogTips.show();
	}
	
	public void showNotifyMessage(Boolean isAllow, Boolean isAllowVibrate, int notifyIcon, String tickerText,
			String contentTitle, String contentText) {
		BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, notifyIcon, tickerText, contentTitle, contentText, AboutActivity.class);
	}
}
