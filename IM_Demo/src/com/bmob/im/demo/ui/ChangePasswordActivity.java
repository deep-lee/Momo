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
        		ShowToast("��֤�����!");
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
           	
	             //�ص����
	             if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) 
	             {
	            	 //�ύ��֤��ɹ������뵽��һ��ҳ��
	            	 progress.dismiss();
	            	 
	            	 Message message = new Message();
	            	 message.what = 5;
	            	 handlerGetVerfyCode.sendMessage(message);
	            	
	             }
	             else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE)
	             {
	            	 //��ȡ��֤��ɹ�
	            	 ShowToast("��ȡ��֤��ɹ�");
	             }
	             else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES)
	             {
	            	 //����֧�ַ�����֤��Ĺ����б�
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
		SMSSDK.registerEventHandler(eh); //ע����Żص�
		
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
			ShowToast("��֤�벻��Ϊ�գ�");
			
			shakeAnimation.playOn(et_confim_code);
			
			return;
		}
		
		SMSSDK.submitVerificationCode("86", currentUser.getUsername(), et_confim_code.getText().toString());
		
		progress = new CustomProgressDialog(ChangePasswordActivity.this, "������֤...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
	}
	
	private void getVerfyCode() {
		
		showSurePhoneNumDialog(currentUser.getUsername());
			
	}

	private void showSurePhoneNumDialog(final String username) {
		DialogTips dialogTips = new DialogTips(ChangePasswordActivity.this, "���ǽ����Ͷ�����֤�뵽������룺+86 " + username,
				"ȷ��", "ȡ��", "ȷ���ֻ�����", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// �����Ϊ�գ�����������֤��
				
				SMSSDK.getVerificationCode("86", username);
				Message message = new Message();
				message.what = 1;
				handlerGetVerfyCode.sendMessage(message);
				btn_get_confim_code.setTag(2);
				btn_get_confim_code.setText("��֤");
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
			ShowToast("�����벻��Ϊ��");
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
			// name���ϴ����ƶ˵Ĳ������ƣ�ֵ��bmob���ƶ˴������ͨ������request.body.name��ȡ���ֵ
			params.put("objectId", currentUser.getObjectId());
			params.put("newpassword", new_password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// �����ƶ˴������
		AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
		// �첽�����ƶ˴���
		cloudCode.callEndpoint(ChangePasswordActivity.this, cloudCodeName, params,
				new CloudCodeListener() {

					@Override
					public void onSuccess(Object arg0) {
						// TODO Auto-generated method stub
						// ShowToast("�ƶ˴���ִ�гɹ���" + arg0.toString());
						if (arg0.toString().equals("success")) {
							ShowToast("�����޸ĳɹ���");
							finish();
						}else {
							ShowToast("�����޸�ʧ�ܣ�");
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("�����޸�ʧ�ܣ�");
					}
				});
		
	}
	
}
