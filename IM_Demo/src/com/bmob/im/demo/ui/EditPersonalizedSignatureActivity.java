package com.bmob.im.demo.ui;

import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Attractions;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.dd.library.CircularProgressButton;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditPersonalizedSignatureActivity extends ActivityBase {
	
	CircularProgressButton btn_publish;
	EditText et_personal;
	User user;
	CustomProgressDialog updateDialog;
	
	Handler updateHandler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            	case 0:
            		
            		updateDialog.dismiss();
            		updateDialog = null;
            		finish();
            		
            		break;
            
            }   
            super.handleMessage(msg); 
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_personalized_signature);
		
		initTopBarForLeft("我的签名");
		
		// 女性主题
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
		}
		
		user = userManager.getCurrentUser(User.class); 
		
		btn_publish = (CircularProgressButton) findViewById(R.id.btn_publish);
		et_personal = (EditText) findViewById(R.id.edit_personal_et);
		
		if (user != null) {
			if (user.getPersonalizedSignature() != null) {
				if (!user.getPersonalizedSignature().equals("未填写")) {
					et_personal.setText(user.getPersonalizedSignature());
				}
			}
		}
		
		btn_publish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (btn_publish.getProgress() == -1) {
					btn_publish.setProgress(0);
					return;
				}
				
				if (!isNetAvailable()) {
					ShowToast(R.string.network_tips);
					btn_publish.setProgress(-1);
					return;
				}
				else {
					
					btn_publish.setProgress(50);
					
					User updateUser = new User();
					if (et_personal.getText().toString().equals("")) {
						updateUser.setPersonalizedSignature("未填写");
					}else {
						updateUser.setPersonalizedSignature(et_personal.getText().toString());
					}
				
					updateDialog = new CustomProgressDialog(EditPersonalizedSignatureActivity.this, "正在发布...");
					updateDialog.setCancelable(false);
					updateDialog.setCanceledOnTouchOutside(false);
					updateDialog.show();
					
					updateUserData(updateUser, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							btn_publish.setProgress(100);
							
							Message message = new Message();
							message.what = 0;
							updateHandler.sendMessage(message);
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							ShowToast("发布失败，请重试");
							btn_publish.setProgress(-1);
						}
					});
					
				}
				
				
				
			}
		});
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
	
	
}
