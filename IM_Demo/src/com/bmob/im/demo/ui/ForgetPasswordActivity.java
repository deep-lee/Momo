package com.bmob.im.demo.ui;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.daimajia.androidanimations.library.fading_entrances.FadeInUpAnimator;
import com.daimajia.androidanimations.library.fading_exits.FadeOutRightAnimator;
import com.dd.library.CircularProgressButton;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ForgetPasswordActivity extends BaseActivity implements OnClickListener, OnTouchListener{
	
	RelativeLayout layout_all;
	
	//��ָ���һ���ʱ����С�ٶ�
	private static final int XSPEED_MIN = 200;
		
	//��ָ���һ���ʱ����С����
	private static final int XDISTANCE_MIN = 150;
		
	//��¼��ָ����ʱ�ĺ����ꡣ
	private float xDown;
		
	//��¼��ָ�ƶ�ʱ�ĺ����ꡣ
	private float xMove;
		
	//���ڼ�����ָ�������ٶȡ�
	private VelocityTracker mVelocityTracker;
	
	EditText et_user_name, et_confim_code, et_new_password;
	CircularProgressButton btn_getconfim_code, btn_sure_change, btn_confim_sure;
	ImageView line_2, line_3;
	YoYo.AnimationComposer shakeAnimation, fadeOutRightAnimation, fadeInUpAnimation;
	
	LinearLayout rl_modify_get_confim_code, rl_modify_new_password;
	
	CustomProgressDialog progress;
	
	String objectId = "";
	
	String username;
	String new_password;
	String sessionToken;
	
	User updateUser;
	
	int time = 30;
	
	@SuppressLint("HandlerLeak")
	Handler handlerGetVerfyCode = new Handler(){
		public void handleMessage(Message msg) { 
            switch (msg.what) {   
            
            case 1:
            	btn_getconfim_code.setClickable(false);
            	btn_getconfim_code.setText("ʣ��" + time + "��");
        		time--;
        		if (time != 0) {
					Message message1 = new Message();
					message1.what = 1;
					handlerGetVerfyCode.sendMessageDelayed(message1, 1000);
				}
        		else {
        			Message message2 = new Message();
					message2.what = 2;
					handlerGetVerfyCode.sendMessageDelayed(message2, 1000);
				}
        		break;
        	case 2:
        		btn_getconfim_code.setClickable(true);
        		btn_getconfim_code.setText("��ȡ��֤��");
        		time = 30;
        		break;
        	case 3:
        		showNewPasswordInput();
        		break;
        	case 4:
        		progress.dismiss();
        		progress = null;
        		shakeAnimation.playOn(et_confim_code);
        		ShowToast("��֤�����");
        		btn_confim_sure.setProgress(0);
        		break;
        		
        	case 5:
        		rl_modify_get_confim_code.setVisibility(View.GONE);
        		line_2.setVisibility(View.GONE);
        		btn_confim_sure.setVisibility(View.GONE);
        		
        		rl_modify_new_password.setVisibility(View.VISIBLE);
        		line_3.setVisibility(View.VISIBLE);
        		btn_sure_change.setVisibility(View.VISIBLE);
        		
        		fadeInUpAnimation.playOn(rl_modify_new_password);
        		fadeInUpAnimation.playOn(line_3);
        		fadeInUpAnimation.playOn(btn_sure_change);
        		
        		et_user_name.setEnabled(false);
        		
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
	            	 progress = null;
	            	 
	            	 Message message = new Message();
	            	 message.what = 3;
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
		setContentView(R.layout.activity_forget_password);
		
		initView();
	}
	
	public void initView() {
		
		SMSSDK.initSDK(ForgetPasswordActivity.this, "6a8b985fa723", "1c7bdde34361d3339f0bf840cee36f2e");
		SMSSDK.registerEventHandler(eh); //ע����Żص�
		
		layout_all = (RelativeLayout) findViewById(R.id.layout_all);
		layout_all.setOnTouchListener(this);
		 
		et_user_name = (EditText) findViewById(R.id.et_username);
		et_new_password = (EditText) findViewById(R.id.et_new_password);
		et_confim_code = (EditText) findViewById(R.id.et_confim_code);
		btn_getconfim_code = (CircularProgressButton) findViewById(R.id.register_btn_get_verfy_code);
		btn_sure_change = (CircularProgressButton) findViewById(R.id.midify_password_sure);
		btn_confim_sure = (CircularProgressButton) findViewById(R.id.confim_code_sure);
		line_2 = (ImageView) findViewById(R.id.line_2);
		line_3 = (ImageView) findViewById(R.id.line_3);
		rl_modify_get_confim_code = (LinearLayout) findViewById(R.id.midify_get_confim_code_layout);
		rl_modify_new_password = (LinearLayout) findViewById(R.id.modify_password_new_password_layout);
		
		btn_getconfim_code.setOnClickListener(this);
		btn_sure_change.setOnClickListener(this);
		btn_confim_sure.setOnClickListener(this);
		
		shakeAnimation = new YoYo.AnimationComposer(new ShakeAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		fadeOutRightAnimation = new YoYo.AnimationComposer(new FadeOutRightAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		fadeInUpAnimation = new YoYo.AnimationComposer(new FadeInUpAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.register_btn_get_verfy_code:
			if (btn_getconfim_code.getProgress() == -1) {
				btn_getconfim_code.setProgress(0);
			}
			else if (btn_getconfim_code.getProgress() == 0) {
				getVerfyCode();
			}
			break;
			
		case R.id.midify_password_sure:
			modifyPassword();
			break;
			
		case R.id.confim_code_sure:
			confimCode();
			break;

		default:
			break;
		}
		
	}
	
	private void getVerfyCode() {
		
		btn_getconfim_code.setProgress(50);
		
		if (TextUtils.isEmpty(et_user_name.getText().toString())) {
			ShowToast(R.string.toast_error_username_null);
			shakeAnimation.playOn(et_user_name);
			btn_getconfim_code.setProgress(-1);
			return;
		}
		
		
		// ����û����Ƿ��Ѿ�����
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", et_user_name.getText().toString());
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast(R.string.network_tips);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
//					ShowToast(R.string.username_has_exited);
//					
//					shakeAnimation.playOn(et_user_name);
//					btn_getconfim_code.setProgress(-1);
					
					// �û�����
					
					
				}
				else {
					// �û�������
					ShowToast("�û������ڣ�");
					shakeAnimation.playOn(et_user_name);
					btn_getconfim_code.setProgress(-1);
					return;
				}
				
				showSurePhoneNumDialog();
			}
		});
			
	}

	private void showSurePhoneNumDialog() {
		DialogTips dialogTips = new DialogTips(ForgetPasswordActivity.this, "���ǽ����Ͷ�����֤�뵽������룺+86 " + et_user_name.getText().toString(),
				"ȷ��", "ȡ��", "ȷ���ֻ�����", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// �����Ϊ�գ�����������֤��
				
				btn_getconfim_code.setProgress(0);
				
				SMSSDK.getVerificationCode("86", et_user_name.getText().toString());
				Message message = new Message();
				message.what = 1;
				handlerGetVerfyCode.sendMessage(message);
				
				btn_confim_sure.setVisibility(View.VISIBLE);
			}
		});
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				btn_getconfim_code.setProgress(0);
				
			}
		});
		dialogTips.show();
	}
	
	public void confimCode() {
		
		if (btn_confim_sure.getProgress() == -1) {
			btn_confim_sure.setProgress(0);
			return;
		}
		
		if (TextUtils.isEmpty(et_user_name.getText().toString())) {
			shakeAnimation.playOn(et_user_name);
			ShowToast(R.string.toast_error_username_null);
			return;
		}

		if (TextUtils.isEmpty(et_confim_code.getText().toString())) {
			ShowToast(R.string.toast_error_confim_code_null);
			
			shakeAnimation.playOn(et_confim_code);
			
			return;
		}
		
		btn_confim_sure.setProgress(50);
		
		SMSSDK.submitVerificationCode("86", et_user_name.getText().toString(), et_confim_code.getText().toString());
		
		progress = new CustomProgressDialog(ForgetPasswordActivity.this, "������֤...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
	}
	
	public void showNewPasswordInput() {
		fadeOutRightAnimation.playOn(rl_modify_get_confim_code);
		fadeOutRightAnimation.playOn(line_2);
		fadeOutRightAnimation.playOn(btn_confim_sure);
		
		Message message = new Message();
		message.what = 5;
		handlerGetVerfyCode.sendMessage(message);
	}
	
	
	public void modifyPassword() {
		
		if (btn_sure_change.getProgress() == -1) {
			btn_sure_change.setProgress(0);
			return;
		}
		
		if (btn_sure_change.getProgress() == -1) {
			btn_sure_change.setProgress(0);
		}
		
		if (TextUtils.isEmpty(et_user_name.getText().toString())) {
			shakeAnimation.playOn(et_user_name);
			return;
		}
		
		if (TextUtils.isEmpty(et_new_password.getText().toString())) {
			shakeAnimation.playOn(et_new_password);
			return;
		}
		
		// ��������
		if (!isNetAvailable()) {
			ShowToast(R.string.network_tips);
			return;
		}
		
		btn_sure_change.setProgress(50);
		
		username = et_user_name.getText().toString();
		new_password = et_new_password.getText().toString();
		
		// User user = new User();

		// �����û�����ѯ���û�
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", username);
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("��������ʧ��1");
				btn_sure_change.setProgress(-1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					// �û�����
					objectId = arg0.get(0).getObjectId();
					sessionToken = arg0.get(0).getSessionToken();
					ShowToast("��ѯ�û��ɹ���" + objectId);
					updateUser = arg0.get(0);
					updatePassword();
				}
				else {
					// �û�������
					ShowToast("��������ʧ��2");
					btn_sure_change.setProgress(-1);
					return;
				}
			}
		});
		
	}
	
	public void updatePassword() {
		
		User user = new User();
		user.setPassword(new_password);
		
		updateUserData(user, new UpdateListener() {
			
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowLog("���³ɹ�");
				btn_sure_change.setProgress(0);
		    	finish();
			}
			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
		    	ShowToast("����ʧ�ܣ�"+msg);
		    	btn_sure_change.setProgress(-1);
			}
		});
		
//		user.update(this, objectId, new UpdateListener() {
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				ShowLog("���³ɹ�");
//				btn_sure_change.setProgress(0);
//		    	finish();
//			}
//			@Override
//			public void onFailure(int code, String msg) {
//				// TODO Auto-generated method stub
//		    	ShowToast("����ʧ�ܣ�"+msg);
//		    	btn_sure_change.setProgress(-1);
//			}
//		});
		
	}
	
	private void updateUserData(User user,UpdateListener listener){

		user.setObjectId(objectId);
		user.update(this, listener);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		// ShowToast("touched");
		
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			//��ľ���
			int distanceX = (int) (xMove - xDown);
			//��ȡ˳ʱ�ٶ�
			int xSpeed = getScrollVelocity();
			//�������ľ�����������趨����С�����һ�����˲���ٶȴ��������趨���ٶ�ʱ�����ص���һ��activity
			if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
				finish();
			}
			break;
		case MotionEvent.ACTION_UP:
			recycleVelocityTracker();
			break;
		default:
			break;
		}
		
		return true;
	}
	
	/**
	 * ����VelocityTracker���󣬲�������content����Ļ����¼����뵽VelocityTracker���С�
	 * 
	 * @param event
	 *        
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}
	
	/**
	 * ����VelocityTracker����
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}
	
	/**
	 * ��ȡ��ָ��content���滬�����ٶȡ�
	 * 
	 * @return �����ٶȣ���ÿ�����ƶ��˶�������ֵΪ��λ��
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}
}
