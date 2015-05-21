package com.bmob.im.demo.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.id;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimationComposer;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.dd.library.CircularProgressButton;
import com.deep.ui.update.MainActivity2;
import com.nineoldandroids.animation.Animator;

/**
 * @ClassName: LoginActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-3 下午4:41:42
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	EditText et_username, et_password;
	CircularProgressButton btn_login;
	
	TextView tv_register, tv_forget_password;

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();
	
	YoYo.AnimationComposer shakeAnimation;
	CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		
		//注册退出广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
//		showNotice();
	}

	public void showNotice() {
		DialogTips dialog = new DialogTips(this,"提示",getResources().getString(R.string.show_notice), "确定",true,true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				
			}
		});
		
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}
	
	private void init() {
		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		btn_login = (CircularProgressButton) findViewById(R.id.btn_login);
		tv_register = (TextView) findViewById(R.id.login_go_to_register);
		tv_forget_password = (TextView) findViewById(R.id.login_forget_password);
		btn_login.setOnClickListener(this);
		btn_login.setIndeterminateProgressMode(true);
		
		tv_register.setOnClickListener(this);
		tv_forget_password.setOnClickListener(this);
		
		shakeAnimation = new AnimationComposer(new ShakeAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator())
		.withListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && BmobConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
				finish();
			}
		}

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_go_to_register:
			
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			
			break;
			
		case R.id.login_forget_password:
			Intent intent2 = new Intent(LoginActivity.this,
					ForgetPasswordActivity.class);
			startActivity(intent2);
			break;
			
		case R.id.btn_login:
			// 如果当前是error，点击之后就恢复
			if (btn_login.getProgress() == -1) {
				btn_login.setProgress(0);
				return;
			}
			// 初始状态
			else if (btn_login.getProgress() == 0) {
				// 检查网络
				boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
				if(!isNetConnected){
					// 按钮显示error
					btn_login.setProgress(-1);
					ShowToast(R.string.network_tips);
					return;
				}		
				
				login();
			}

			break;
		}
		
	}
	
	private void login(){
		
		btn_login.setProgress(50);
		
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();

		if (TextUtils.isEmpty(name)) {
			shakeAnimation.playOn(et_username);
			ShowToast(R.string.toast_error_username_null);
			btn_login.setProgress(-1);
			return;
		}

		if (TextUtils.isEmpty(password)) {
			shakeAnimation.playOn(et_password);
			ShowToast(R.string.toast_error_password_null);
			btn_login.setProgress(-1);
			return;
		}
		
		progressDialog = new CustomProgressDialog(LoginActivity.this, "正在获取好友列表...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		final User user = new User();
		user.setUsername(name);
		user.setPassword(password);
		userManager.login(user,new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
				progressDialog.show();
				btn_login.setProgress(100);
				
				//更新用户的地理位置以及好友的资料
				updateUserInfos();
				
				progressDialog.dismiss();
				progressDialog = null;
				
				Intent intent = new Intent(LoginActivity.this,MainActivity2.class);
				startActivity(intent);
				// ShowToast(user.getGameType() + "");
				finish();
			}

			@Override
			public void onFailure(int errorcode, String arg0) {
				// TODO Auto-generated method stub
				// progress.dismiss();
				shakeAnimation.playOn(et_username);
				shakeAnimation.playOn(et_password);
				
				// 登录失败
				btn_login.setProgress(-1);
				BmobLog.i(arg0);
				// ShowToast(arg0);
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
}
