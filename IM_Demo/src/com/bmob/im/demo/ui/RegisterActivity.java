package com.bmob.im.demo.ui;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.ChatBg;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CacheUtils;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.util.PhotoUtil;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DateChooseDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.dialog.SingleChoiceDialog;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimationComposer;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.dd.library.CircularProgressButton;
import com.deep.ui.update.MainActivity2;
import com.nineoldandroids.animation.Animator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class RegisterActivity extends BaseActivity implements OnClickListener{

	Button back, next;
	
	// 第一页
	EditText et_username, et_confim_code;
	CircularProgressButton getConfimCode;
	
	// 第二页
	EditText  et_password, et_password_confim;
	
	// 第三页
	ImageView setAvator;
	EditText  et_nick, et_birth;
	Boolean hasChoseBirth = false;
	
	// 第四页
	ImageView setSexMale, setSexFemal;
	EditText et_game, et_love, et_hobbi;
	Drawable bitMale;
	Drawable bitMaleChoose;
	Drawable bitFemale;
	Drawable bitFemaleChoose;
	Boolean hasChooseGame = false;
	Boolean hasChooseLove = false;
	
	Boolean sex;
	
	int time = 30;

	String birthday = "";
	String initBirth = "";
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	CustomProgressDialog progress;
	
	String gameType = "";
	
	ViewFlipper viewFlipper;
	int currentPage = 0;
	
	BmobChatUser currentUser;
	
	// 判断是否修改了头像
	Boolean avator_changed = false;
	
	RelativeLayout register_layout_all;
	
	LinearLayout layout_choose, layout_photo, layout_cancle;
	
	// 我的头像地址
	File dir;
	
	private String mCurrentPhotoPath;
	
	YoYo.AnimationComposer shakeAnimation;
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
    				//更新地理位置信息
    				updateUserLocation();
    				//发广播通知登陆页面退出
    				sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
    				progress.dismiss();
            		// 启动主页
    				Intent intent = new Intent(RegisterActivity.this,MainActivity2.class);
    				startActivity(intent);
    				finish();
            		break;
            
            }   
            super.handleMessage(msg);   
       }
	};
	
	Handler handlerGetVerfyCode = new Handler(){
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) { 
            switch (msg.what) {   
            
            	case 1:
            		getConfimCode.setClickable(false);
            		getConfimCode.setText("剩余" + time + "秒");
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
            		getConfimCode.setClickable(true);
            		getConfimCode.setText("获取验证码");
            		time = 30;
            		break;
            	case 3:
            		gotoNextPage();
            		break;
            	case 4:
            		progress.dismiss();
            		progress = null;
            		shakeAnimation.playOn(et_confim_code);
            		ShowToast("验证码错误");
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
	            	 progress = null;
	            	 
	            	 Message message = new Message();
	            	 message.what = 3;
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
            	 
//	             ((Throwable)data).printStackTrace(); 
//	             int resId = getStringRes(RegisterActivity.this, "smssdk_network_error");
//	             Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
//	             if (resId > 0) {
//	            	 Toast.makeText(RegisterActivity.this, resId, Toast.LENGTH_SHORT).show();
//	             }
           	}
         } 
	 }; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_2);
		
		 SMSSDK.initSDK(RegisterActivity.this, "6a8b985fa723", "1c7bdde34361d3339f0bf840cee36f2e");
		 SMSSDK.registerEventHandler(eh); //注册短信回调
		 
		// 只有左边有按钮以及标题
//		initTopBarForLeft("注册");
		viewFlipper = (ViewFlipper) findViewById(R.id.reg_vf_viewflipper);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		initBirth = formatter.format(curDate);   
		
		// 下方按钮
		back = (Button) findViewById(R.id.reg_btn_previous);
		next = (Button) findViewById(R.id.reg_btn_next);
		back.setOnClickListener(this);
		next.setOnClickListener(this);
		
		// 第一页
		et_username = (EditText) findViewById(R.id.et_username);
		et_confim_code = (EditText) findViewById(R.id.et_confim_code);
		getConfimCode = (CircularProgressButton) findViewById(R.id.register_btn_get_verfy_code);
		getConfimCode.setOnClickListener(this);
		
		// 第二页
		et_password = (EditText) findViewById(R.id.et_pwd);
		et_password_confim = (EditText) findViewById(R.id.et_password_again);
		
		// 第三页
		setAvator = (ImageView) findViewById(R.id.register_set_avator);
		et_nick = (EditText) findViewById(R.id.register_set_nick_et);
		et_birth = (EditText) findViewById(R.id.register_set_birthday_et);
		setAvator.setOnClickListener(this);
		et_birth.setOnClickListener(this);
		
		// 第四页
		bitMale = getResources().getDrawable(R.drawable.register_sex_select_n);
		bitFemale = getResources().getDrawable(R.drawable.register_sex_select_n);
		bitMaleChoose = getResources().getDrawable(R.drawable.register_sex_select_p);
		bitFemaleChoose = getResources().getDrawable(R.drawable.register_sex_select_p);
		
		setSexMale = (ImageView) findViewById(R.id.register_sex_select_male);
		setSexMale.setImageDrawable(bitMaleChoose);
		setSexFemal = (ImageView) findViewById(R.id.register_sex_select_female);
		et_game = (EditText) findViewById(R.id.register_set_game_et);
		et_love = (EditText) findViewById(R.id.register_set_love_et);
		et_hobbi = (EditText) findViewById(R.id.register_set_hobbi_et);
		
		setSexMale.setOnClickListener(this);
		setSexFemal.setOnClickListener(this);
		et_game.setOnClickListener(this);
		et_love.setOnClickListener(this);
		
		sex = true;
		back.setText("返回");
		
		dir = new File(BmobConstants.MyAvatarDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		register_layout_all = (RelativeLayout) findViewById(R.id.register_layout_all);
		
		// checkUser();
		
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
	
	
	
	private void gotoNextPage() {
		 viewFlipper.showNext();
		 currentPage++;
		 back.setText("上一步");
	}
	
	PopupWindow avatorPop;

	public String filePath = "";
	
	private void showAvatarPop() {
		
		// 直接拍照
		getImageFromCamera();
		
//		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
//				null);
//		layout_choose = (LinearLayout) view.findViewById(R.id.register_select_picture_from_image);
//		layout_photo = (LinearLayout) view.findViewById(R.id.register_select_picture_from_camera);
//		layout_cancle = (LinearLayout) view.findViewById(R.id.register_select_picture_cancle);
//		layout_cancle.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				avatorPop.dismiss();
//			}
//		});
//		layout_photo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				ShowLog("点击拍照");
//				// TODO Auto-generated method stub			
//				getImageFromCamera();
//			}
//		});
//		layout_choose.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				ShowLog("点击相册");
//				pickImage();
//				
//			}
//		});
//
//		avatorPop = new PopupWindow(view, mScreenWidth, 600);
//		avatorPop.setTouchInterceptor(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//					avatorPop.dismiss();
//					return true;
//				}
//				return false;
//			}
//		});
//
//		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
//		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//		avatorPop.setTouchable(true);
//		avatorPop.setFocusable(true);
//		avatorPop.setOutsideTouchable(true);
//		avatorPop.setBackgroundDrawable(new BitmapDrawable());
//		// 动画效果 从底部弹起
//		avatorPop.setAnimationStyle(R.style.PopupAnimation);
//		avatorPop.showAtLocation(register_layout_all, Gravity.BOTTOM, 0, 0);
	}
	
	
	protected void getImageFromCamera() {
		
		// 原图
		File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
				.format(new Date()));
		filePath = file.getAbsolutePath(); // 获取相片的保存路径
		Uri imageUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        mCurrentPhotoPath = imageUri.getPath();

        startActivityForResult(intent, BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
    }
	
	protected void pickImage(){
		Crop.pickImage(this);
	}
	
	
	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Crop.REQUEST_PICK) {
                beginCrop( data.getData());
            }
            else if(requestCode == Crop.REQUEST_CROP) {
                handleCrop( resultCode, data);
            }
            else if(requestCode == BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA) {
                System.out.println( " REQUESTCODE_UPLOADAVATAR_CAMERA " + mCurrentPhotoPath);
                if(mCurrentPhotoPath != null) {
                	// 压缩图片
                    beginCrop( Uri.fromFile( new File( saveToSdCard(compressImageFromFile(mCurrentPhotoPath)))));
                }
            }
        }
	}
	
	String path = "";
	private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            System.out.println(" handleCrop: Crop.getOutput(result) "+Crop.getOutput(result));
            
            ViewGroup.LayoutParams lp = setAvator.getLayoutParams();
            lp.width = LayoutParams.FILL_PARENT;
            lp.height = LayoutParams.FILL_PARENT;
            setAvator.setLayoutParams(lp);
            setAvator.setImageURI( Crop.getOutput(result));
            
            if (avatorPop != null) {
				avatorPop.dismiss();
			}
			
			// 已经修改了头像
			avator_changed = true;
            
            
//            mCircleView.setImageBitmap( getCircleBitmap(Crop.getOutput(result)));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(RegisterActivity.this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getCircleBitmap(Uri uri) {
       Bitmap src =  BitmapFactory.decodeFile( uri.getPath());
       Bitmap output = Bitmap.createBitmap( src.getWidth(), src.getHeight(), Bitmap.Config.RGB_565);
       Canvas canvas = new Canvas( output);

       Paint paint = new Paint();
       Rect rect = new Rect( 0, 0, src.getWidth(), src.getHeight());

       paint.setAntiAlias( true);
       paint.setFilterBitmap( true);
       paint.setDither( true);
       canvas.drawARGB( 0, 0, 0, 0);
       canvas.drawCircle( src.getWidth() / 2, src.getWidth() / 2, src.getWidth() / 2, paint);
       paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.SRC_IN));
       canvas.drawBitmap( src, rect, rect, paint);
       return output;
    }

    private void beginCrop(Uri source) {
        String fileName = new SimpleDateFormat("yyMMddHHmmss").format(new Date())  + ".png";
        File cropFile = new File( dir, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        
        // 剪裁后的文件路径
        path = cropFile.getAbsolutePath();
        new Crop( source).output( outputUri).setCropType(true).start( this);
    }
	
	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	
	

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				
				// 将图片变为圆角，第二个参数是圆角的弧度
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					
					// 旋转图片一定角度
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				setAvator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date())+".png";
				path = BmobConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}
	
	/*
	 *  上传头像
	 */
	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// 获取文件上传之后文件在服务器断对应的地址
				String url = bmobFile.getFileUrl(RegisterActivity.this);
				// 更新BmobUser对象，也就是加上头像的地址
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	// 更新用户的头像
	private void updateUserAvatar(final String url) {
		User  u =new User();
		u.setAvatar(url);
		updateUserData(u,new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("头像更新成功！");
				// 更新头像
				refreshAvatar(url);
				
				Message message = new Message();
				message.what = 0;
				handler.sendMessage(message);
				
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
	
	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, setAvator,
					ImageLoadOptions.getOptions());
		} else {
			
			// 否则显示默认的头像
			setAvator.setImageResource(R.drawable.default_head);
		}
	}
	
	private void register(){
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();
		String nick = et_nick.getText().toString();
		
		
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if(!isNetConnected){
			ShowToast(R.string.network_tips);
			return;
		}
		
		progress = new CustomProgressDialog(RegisterActivity.this, "正在注册...");
		// progress = new ProgressDialog(RegisterActivity.this);
		// progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();
		
		//由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
		//注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
		final User bu = new User();
		bu.setUsername(name);
		bu.setPassword(password);
		bu.setSex(sex);
		bu.setNick(nick); // 设置昵称
		bu.setBirthday(et_birth.getText().toString());
		bu.setGameType(gameType);
		bu.setGameDifficulty("简单");
		bu.setLove(et_love.getText().toString());
		bu.setInterests(et_hobbi.getText().toString());
		
		bu.setPersonalizedSignature("未填写");
		bu.setCareer("未填写");
		bu.setCompany("未填写");
		bu.setSchool("未填写");
		bu.setHometown("未填写");
		bu.setBook("未填写");
		bu.setMovie("未填写");
		bu.setMusic("未填写");
		bu.setUsuallyAppear("未填写");
		bu.setRecentPlayGame(-1);
		bu.setStatus(true);
		bu.setQiangYuStatus(true);
		
		ChatBg chatBg = new ChatBg();
        chatBg.setBelongTo(0);
        chatBg.setObjectId("5vEm777e");
        chatBg.setPhotoName("默认背景");
		bu.setChatBg(chatBg);
		
		//将user和设备id进行绑定
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		
		// 进行注册，发送数据到服务器
		bu.signUp(RegisterActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
				
				// progress.dismiss();
				ShowToast("注册成功");
				// 将设备与username进行绑定
				userManager.bindInstallationForRegister(bu.getUsername());
				
				// 更新用户的头像
				uploadAvatar();
				
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i(arg1);
				ShowToast("注册失败:" + arg1);
				progress.dismiss();
			}
		});
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
			
		// 前一页
		case R.id.reg_btn_previous:
			showPrevious();
			break;
			
		// 后一页
		case R.id.reg_btn_next:
			showNext();
			break;
			
		// 获取验证码
		case R.id.register_btn_get_verfy_code:
			if (getConfimCode.getProgress() == -1) {
				getConfimCode.setProgress(0);
			}
			else if (getConfimCode.getProgress() == 0) {
				getVerfyCode();
			}
			
			
			break;
			
		// 设置头像
		case R.id.register_set_avator:
			showAvatarPop();
			break;
		
		// 设置生日
		case R.id.register_set_birthday_et:
			seleteBirth();
			break;
			
		// 设置男性
		case R.id.register_sex_select_male:
			if (!sex) {
				sex = true;
				setSexFemal.setImageDrawable(bitFemale);
				setSexMale.setImageDrawable(bitMaleChoose);
			}
			break;
			
		// 设置女性
		case R.id.register_sex_select_female:
			if (sex) {
				sex = false;
				setSexFemal.setImageDrawable(bitFemaleChoose);
				setSexMale.setImageDrawable(bitMale);
			}
			break;
			
		// 设置游戏
		case R.id.register_set_game_et:
			showGameChooseDialog();
			break;
			
		// 设置情感状况
		case R.id.register_set_love_et:
			showLoveChooseDialog();
			break;
			
		default:
			break;
		}
	}
	
	/*
	 * 选择情感状况
	 * author:Deep
	 */
	private void showLoveChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(RegisterActivity.this,
				CustomApplcation.loveList, "确定", "取消", "情感状况", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					et_love.setText("热恋");
					break;
				case 1:
					et_love.setText("单身");
					break;
				case 2:
					et_love.setText("失恋");
					break;
				case 3:
					et_love.setText("保密");
					break;
				default:
					break;
				}
				hasChooseLove = true;
				singleChoiceDialog.dismiss();
			}
		});
		
		singleChoiceDialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		singleChoiceDialog.show();
		
	}
	
	private void getVerfyCode() {
		
		getConfimCode.setProgress(50);
		
		if (TextUtils.isEmpty(et_username.getText().toString())) {
			ShowToast(R.string.toast_error_username_null);
			shakeAnimation.playOn(et_username);
			getConfimCode.setProgress(-1);
			return;
		}
		
		// 检查用户名是否已经存在
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", et_username.getText().toString());
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
					ShowToast(R.string.username_has_exited);
					
					shakeAnimation.playOn(et_username);
					getConfimCode.setProgress(-1);
					
					return;
				}
				
				showSurePhoneNumDialog();
			}
		});
			
	}
	
	private void showSurePhoneNumDialog() {
		DialogTips dialogTips = new DialogTips(RegisterActivity.this, "我们将发送短信验证码到这个号码：+86 " + et_username.getText().toString(),
				"确定", "取消", "确认手机号码", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 如果不为空，就请求发送验证码
				
				getConfimCode.setProgress(0);
				
				SMSSDK.getVerificationCode("86", et_username.getText().toString());
				Message message = new Message();
				message.what = 1;
				handlerGetVerfyCode.sendMessage(message);
			}
		});
		
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				getConfimCode.setProgress(0);
				
			}
		});
		dialogTips.show();
	}
	
	private void showGameChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(RegisterActivity.this,
				CustomApplcation.getInstance().getSelectAbleGame(), "确定", "取消", "解锁游戏", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					gameType = "水果连连看";
					break;
				case 1:
					gameType = "猜数字";
					break;
				case 2:
					gameType = "mixed color";
					break;
				case 3:
					gameType = "oh my egg";
					break;
				case 4:
					gameType = "猜拳大比拼";
					break;
				default:
					break;
				}
				
				hasChooseGame = true;
				et_game.setText(gameType);
				singleChoiceDialog.dismiss();
			}
		});
		
		singleChoiceDialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		singleChoiceDialog.show();
		
	}
	
	public Boolean getHasChoseBirth() {
		return hasChoseBirth;
	}

	public void setHasChoseBirth(Boolean hasChoseBirth) {
		this.hasChoseBirth = hasChoseBirth;
	}

	private void showPrevious() {
		
		if (currentPage == 0) {
			this.finish();
		}else if(currentPage == 1){
			viewFlipper.showPrevious();	
			currentPage--;
			back.setText("返回");
		}
		else if (currentPage == 2) {
			viewFlipper.showPrevious();	
			currentPage--;
			back.setText("上一步");
			next.setText("下一步");
		}
		else if (currentPage == 3) {
			viewFlipper.showPrevious();	
			currentPage--;
			back.setText("上一步");
			next.setText("下一步");
		}
		
	}
	
	private void showNext() {
		if (currentPage == 0) {
			
			if (TextUtils.isEmpty(et_username.getText().toString())) {
				shakeAnimation.playOn(et_username);
				ShowToast(R.string.toast_error_username_null);
				return;
			}

			if (TextUtils.isEmpty(et_confim_code.getText().toString())) {
				ShowToast(R.string.toast_error_confim_code_null);
				
				shakeAnimation.playOn(et_confim_code);
				
				return;
			}
			
			SMSSDK.submitVerificationCode("86", et_username.getText().toString(), et_confim_code.getText().toString());
			
			progress = new CustomProgressDialog(RegisterActivity.this, "正在验证...");
			progress.setCanceledOnTouchOutside(false);
			progress.setCancelable(false);
			progress.show();
			
			// gotoNextPage();
			
//			viewFlipper.showNext();
//			currentPage++;
//			back.setText("上一步");
			
		}
		
		else if (currentPage == 1) {
			if (TextUtils.isEmpty(et_password.getText().toString())) {
				ShowToast(R.string.toast_error_password_null);
				shakeAnimation.playOn(et_password);
				return;
			}
			
			if (et_password.getText().toString().length() < 6) {
				ShowToast(R.string.login_password_number_error);
				shakeAnimation.playOn(et_password_confim);
				return;
			}
			
			if (!et_password_confim.getText().toString().equals(et_password.getText().toString())) {
				ShowToast(R.string.toast_error_comfirm_password);
				shakeAnimation.playOn(et_password);
				shakeAnimation.playOn(et_password_confim);
				return;
			}
			
			viewFlipper.showNext();
			currentPage++;
			next.setText("下一步");
		}
		
		else if (currentPage == 2) {
			if (!avator_changed) {
				ShowToast(R.string.toast_error_change_avator);
				shakeAnimation.playOn(setAvator);
				return;
			}
			
			if (TextUtils.isEmpty(et_nick.getText().toString())) {
				ShowToast(R.string.toast_error_nick_null);
				shakeAnimation.playOn(et_nick);
				return;
			}
			
			if (et_nick.getText().toString().length() > 6) {
				ShowToast(R.string.register_nick_too_long_error);
				shakeAnimation.playOn(et_nick);
				return;
			}
			if (!hasChoseBirth) {
				ShowToast(R.string.toast_error_birth_null);
				shakeAnimation.playOn(et_birth);
				return;
			}
			
			viewFlipper.showNext();
			currentPage++;
			next.setText("注册");
		}
		else if(currentPage == 3){
			if (!hasChooseGame) {
				ShowToast(R.string.toast_error_game_null);
				shakeAnimation.playOn(et_game);
				return;
			}
			
			if (!hasChooseLove) {
				ShowToast(R.string.toast_error_love_null);
				shakeAnimation.playOn(et_love);
				return;
			}
			
			if (TextUtils.isEmpty(et_hobbi.getText().toString())) {
				ShowToast(R.string.toast_error_interest_null);
				shakeAnimation.playOn(et_hobbi);
				return;
			}
			
			DialogTips dialogTips = new DialogTips(RegisterActivity.this, "性别选定之后不可改变，确认选择？", 
					"确认", "取消", "确认性别选择", true);
			dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					register();
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
	
	private void seleteBirth(){
		
		final DateChooseDialog dateChooseDialog = new DateChooseDialog(RegisterActivity.this, "确定", "取消", "选择生日", false);
		dateChooseDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				et_birth.setText(dateChooseDialog.getDate());
				hasChoseBirth = true;
			}
		});
		dateChooseDialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (!hasChoseBirth) {
					et_birth.setText("选择生日");
					hasChoseBirth = false;
				}
			}
		});
		
		dateChooseDialog.show();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		// registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
		SMSSDK.unregisterEventHandler(eh);
	}
	
	// 压缩图片
		private Bitmap compressImageFromFile(String srcPath) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;// 只读边,不读内容
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			float hh = 800f;//
			float ww = 480f;//
			int be = 1;
			if (w > h && w > ww) {
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置采样率

			newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
			newOpts.inPurgeable = true;// 同时设置才会有效
			newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
			// 其实是无效的,大家尽管尝试
			return bitmap;
		}

		public String saveToSdCard(Bitmap bitmap) {
			
			File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
			.format(new Date()) + ".png");
			
//			String files = CacheUtils.getCacheDirectory(RegisterActivity.this, true, "pic")
//					+ dateTime + "_11.jpg";
//			File file = new File(files);
			try {
				FileOutputStream out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
					out.flush();
					out.close();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("WriteStateActivity", file.getAbsolutePath());
			return file.getAbsolutePath();
		}

}
