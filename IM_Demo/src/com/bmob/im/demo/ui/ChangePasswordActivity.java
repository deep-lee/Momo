package com.bmob.im.demo.ui;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInUpAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutRightAnimator;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswordActivity extends BaseActivity implements OnClickListener{

	EditText et_new_password, et_confim_code;
	TextView tv_tips1, tv_tips2;
	Button btn_sure, btn_get_confim_code;
	
	String old_password = "";
	String new_password = "";
	
	User currentUser;
	CustomProgressDialog progress;
	
	YoYo.AnimationComposer shakeAnimation, fadeOutRightAnimation, fadeInUpAnimation;
	
	@SuppressLint("HandlerLeak")
	Handler handlerGetVerfyCode = new Handler(){
		public void handleMessage(Message msg) { 
            switch (msg.what) {   

        	case 4:
        		progress.dismiss();
        		progress = null;
        		shakeAnimation.playOn(et_confim_code);
        		ShowToast("验证码错误!");
        		break;
        		
        	case 5:
        		fadeOutRightAnimation.playOn(et_confim_code);
        		fadeOutRightAnimation.playOn(btn_get_confim_code);
        		fadeOutRightAnimation.playOn(tv_tips1);
        		et_confim_code.setVisibility(View.GONE);
        		btn_get_confim_code.setVisibility(View.GONE);
        		tv_tips1.setVisibility(View.GONE);
        		
        		et_new_password.setVisibility(View.VISIBLE);
        		btn_sure.setVisibility(View.VISIBLE);
        		tv_tips2.setVisibility(View.VISIBLE);
        		
        		fadeInUpAnimation.playOn(et_new_password);
        		fadeInUpAnimation.playOn(btn_sure);
        		fadeInUpAnimation.playOn(tv_tips2);
        		
        		break;
            
            }   
            super.handleMessage(msg);  
		}
	};
	
	EventHandler eh=new EventHandler(){
		 
        @Override
        public void afterEvent(int event, int result, Object data) {

           if (result == SMSSDK.RESULT_COMPLETE) {
           	
	             //回调完成
	             if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) 
	             {
	            	 //提交验证码成功，进入到下一个页面
	            	 progress.dismiss();
	            	 
	            	 Message message = new Message();
	            	 message.what = 5;
	            	 handlerGetVerfyCode.sendMessage(message);
	            	
	             }
	             else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE)
	             {
	            	 //获取验证码成功
	            	 ShowToast("获取验证码成功");
	             }
	             else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES)
	             {
	            	 //返回支持发送验证码的国家列表
	             } 
           }
           else
           {                  
           	 Message message = new Message();
           	 message.what = 4;
           	 handlerGetVerfyCode.sendMessage(message);
           	 
          	}
        } 
	 }; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		initView();
	}
	public void initView() {
		
		SMSSDK.initSDK(ChangePasswordActivity.this, "6a8b985fa723", "1c7bdde34361d3339f0bf840cee36f2e");
		SMSSDK.registerEventHandler(eh); //注册短信回调
		
		fadeOutRightAnimation = new YoYo.AnimationComposer(new FadeOutRightAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		fadeInUpAnimation = new YoYo.AnimationComposer(new FadeInUpAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		currentUser = CustomApplcation.getInstance().getCurrentUser();
		
		et_confim_code = (EditText) findViewById(R.id.confim_code_et);
		et_new_password = (EditText) findViewById(R.id.new_password_et);
		btn_sure = (Button) findViewById(R.id.btn_change_sure);
		btn_get_confim_code = (Button) findViewById(R.id.btn_get_confim_code);
		tv_tips1 = (TextView) findViewById(R.id.confim_code__tips);
		tv_tips2 = (TextView) findViewById(R.id.new_password_tips);
		
		shakeAnimation = new YoYo.AnimationComposer(new ShakeAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		btn_sure.setOnClickListener(this);
		btn_get_confim_code.setOnClickListener(this);
		btn_get_confim_code.setTag(1);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_change_sure:
			checkPassword();
			break;
		case R.id.btn_get_confim_code:
			if (btn_get_confim_code.getTag() == (Integer)1) {
				getVerfyCode();
			}else {
				confimCode();
			}
			break;

		default:
			break;
		}
	}

	public void confimCode() {

		if (TextUtils.isEmpty(et_confim_code.getText().toString())) {
			ShowToast("验证码不能为空！");
			
			shakeAnimation.playOn(et_confim_code);
			
			return;
		}
		
		SMSSDK.submitVerificationCode("86", currentUser.getUsername(), et_confim_code.getText().toString());
		
		progress = new CustomProgressDialog(ChangePasswordActivity.this, "正在验证...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
	}
	
	private void getVerfyCode() {
		
		showSurePhoneNumDialog(currentUser.getUsername());
			
	}

	private void showSurePhoneNumDialog(final String username) {
		DialogTips dialogTips = new DialogTips(ChangePasswordActivity.this, "我们将发送短信验证码到这个号码：+86 " + username,
				"确定", "取消", "确认手机号码", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 如果不为空，就请求发送验证码
				
				SMSSDK.getVerificationCode("86", username);
				Message message = new Message();
				message.what = 1;
				handlerGetVerfyCode.sendMessage(message);
				btn_get_confim_code.setTag(2);
				btn_get_confim_code.setText("验证");
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
	
	public void checkPassword() {
		
		new_password = et_new_password.getText().toString();
		if (new_password == null || new_password.equals("")) {
			ShowToast("新密码不能为空");
			shakeAnimation.playOn(et_new_password);
			return;
		}
		
		if (new_password.length() < 6) {
			ShowToast(R.string.login_password_number_error);
			shakeAnimation.playOn(et_new_password);
			return;
		}
		
		if (currentUser == null) {
			ShowToast(R.string.network_tips);
			return;
		}
		
		String cloudCodeName = "resetPassword";
		JSONObject params = new JSONObject();
		try {
			// name是上传到云端的参数名称，值是bmob，云端代码可以通过调用request.body.name获取这个值
			params.put("objectId", currentUser.getObjectId());
			params.put("newpassword", new_password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建云端代码对象
		AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
		// 异步调用云端代码
		cloudCode.callEndpoint(ChangePasswordActivity.this, cloudCodeName, params,
				new CloudCodeListener() {

					@Override
					public void onSuccess(Object arg0) {
						// TODO Auto-generated method stub
						// ShowToast("云端代码执行成功：" + arg0.toString());
						if (arg0.toString().equals("success")) {
							ShowToast("密码修改成功！");
							finish();
						}else {
							ShowToast("密码修改失败！");
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("密码修改失败！");
					}
				});
		
	}
	
}
