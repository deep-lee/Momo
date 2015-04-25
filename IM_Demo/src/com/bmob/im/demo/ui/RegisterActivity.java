package com.bmob.im.demo.ui;

import static cn.smssdk.framework.utils.R.getStringRes;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.push.a.project;
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
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.util.JudgeDate;
import com.bmob.im.demo.util.PhotoUtil;
import com.bmob.im.demo.util.ScreenInfo;
import com.bmob.im.demo.util.WheelMain;
import com.bmob.im.demo.view.dialog.DateChooseDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.dialog.SingleChoiceDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class RegisterActivity extends BaseActivity implements OnClickListener{

	Button back, next;
	
	// ��һҳ
	EditText et_username, et_confim_code;
	Button getConfimCode;
	
	// �ڶ�ҳ
	EditText  et_password, et_password_confim;
	
	// ����ҳ
	ImageView setAvator;
	EditText  et_nick, et_birth;
	Boolean hasChoseBirth = false;
	
	// ����ҳ
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
	ProgressDialog progress;
	
	String gameType = "";
	
	ViewFlipper viewFlipper;
	int currentPage = 0;
	
	BmobChatUser currentUser;
	
	// �ж��Ƿ��޸���ͷ��
	Boolean avator_changed = false;
	
	RelativeLayout register_layout_all;
	
	LinearLayout layout_choose, layout_photo, layout_cancle;
	
	// �ҵ�ͷ���ַ
	File dir;
	
	private String mCurrentPhotoPath;
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
    				//���µ���λ����Ϣ
    				updateUserLocation();
    				//���㲥֪ͨ��½ҳ���˳�
    				sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
    				progress.dismiss();
            		// ������ҳ
    				Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
    				startActivity(intent);
    				finish();
            		break;
            
            }   
            super.handleMessage(msg);   
       }
	};
	
	Handler handlerGetVerfyCode = new Handler(){
		public void handleMessage(Message msg) { 
            switch (msg.what) {   
            
            	case 1:
            		getConfimCode.setClickable(false);
            		getConfimCode.setText("ʣ��" + time + "��");
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
            		getConfimCode.setText("��ȡ��֤��");
            		time = 30;
            		break;
            	case 3:
            		gotoNextPage();
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
	              ((Throwable)data).printStackTrace(); 
	              int resId = getStringRes(RegisterActivity.this, "smssdk_network_error");
	              Toast.makeText(RegisterActivity.this, "��֤�����", Toast.LENGTH_SHORT).show();
	              if (resId > 0) {
	            	  Toast.makeText(RegisterActivity.this, resId, Toast.LENGTH_SHORT).show();
	              }
           	}
         } 
	 }; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_2);
		
		 SMSSDK.initSDK(RegisterActivity.this, "6a8b985fa723", "1c7bdde34361d3339f0bf840cee36f2e");
		 SMSSDK.registerEventHandler(eh); //ע����Żص�
		 
		// ֻ������а�ť�Լ�����
		initTopBarForLeft("ע��");
		viewFlipper = (ViewFlipper) findViewById(R.id.reg_vf_viewflipper);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd��");       
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
		initBirth = formatter.format(curDate);   
		
		// �·���ť
		back = (Button) findViewById(R.id.reg_btn_previous);
		next = (Button) findViewById(R.id.reg_btn_next);
		back.setOnClickListener(this);
		next.setOnClickListener(this);
		
		// ��һҳ
		et_username = (EditText) findViewById(R.id.et_username);
		et_confim_code = (EditText) findViewById(R.id.et_confim_code);
		getConfimCode = (Button) findViewById(R.id.register_btn_get_verfy_code);
		getConfimCode.setOnClickListener(this);
		
		// �ڶ�ҳ
		et_password = (EditText) findViewById(R.id.et_pwd);
		et_password_confim = (EditText) findViewById(R.id.et_password_again);
		
		// ����ҳ
		setAvator = (ImageView) findViewById(R.id.register_set_avator);
		et_nick = (EditText) findViewById(R.id.register_set_nick_et);
		et_birth = (EditText) findViewById(R.id.register_set_birthday_et);
		setAvator.setOnClickListener(this);
		et_birth.setOnClickListener(this);
		
		// ����ҳ
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
		back.setText("����");
		
		dir = new File(BmobConstants.MyAvatarDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		register_layout_all = (RelativeLayout) findViewById(R.id.register_layout_all);
		
		// checkUser();
		
	}
	
	
	
	private void gotoNextPage() {
		 viewFlipper.showNext();
		 currentPage++;
		 back.setText("��һ��");
	}
	
	PopupWindow avatorPop;

	public String filePath = "";
	
	private void showAvatarPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
				null);
		layout_choose = (LinearLayout) view.findViewById(R.id.register_select_picture_from_image);
		layout_photo = (LinearLayout) view.findViewById(R.id.register_select_picture_from_camera);
		layout_cancle = (LinearLayout) view.findViewById(R.id.register_select_picture_cancle);
		layout_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				avatorPop.dismiss();
			}
		});
		layout_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("�������");
				// TODO Auto-generated method stub			
				getImageFromCamera();
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowLog("������");
				pickImage();
				
			}
		});

		avatorPop = new PopupWindow(view, mScreenWidth, 600);
		avatorPop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatorPop.dismiss();
					return true;
				}
				return false;
			}
		});

		avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatorPop.setTouchable(true);
		avatorPop.setFocusable(true);
		avatorPop.setOutsideTouchable(true);
		avatorPop.setBackgroundDrawable(new BitmapDrawable());
		// ����Ч�� �ӵײ�����
		avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatorPop.showAtLocation(register_layout_all, Gravity.BOTTOM, 0, 0);
	}
	
	
	protected void getImageFromCamera() {
		
		// ԭͼ
		File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
				.format(new Date()));
		filePath = file.getAbsolutePath(); // ��ȡ��Ƭ�ı���·��
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
	boolean isFromCamera = false;// ����������ת
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
                    beginCrop( Uri.fromFile( new File( mCurrentPhotoPath)));
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
			
			// �Ѿ��޸���ͷ��
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
        
        // ���ú���ļ�·��
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
	 * ����ü���ͷ��
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				
				// ��ͼƬ��ΪԲ�ǣ��ڶ���������Բ�ǵĻ���
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					
					// ��תͼƬһ���Ƕ�
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				setAvator.setImageBitmap(bitmap);
				// ����ͼƬ
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date())+".png";
				path = BmobConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(BmobConstants.MyAvatarDir, filename,
						bitmap, true);
				// �ϴ�ͷ��
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}
	
	/*
	 *  �ϴ�ͷ��
	 */
	private void uploadAvatar() {
		BmobLog.i("ͷ���ַ��" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// ��ȡ�ļ��ϴ�֮���ļ��ڷ������϶�Ӧ�ĵ�ַ
				String url = bmobFile.getFileUrl(RegisterActivity.this);
				// ����BmobUser����Ҳ���Ǽ���ͷ��ĵ�ַ
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("ͷ���ϴ�ʧ�ܣ�" + msg);
			}
		});
	}

	// �����û���ͷ��
	private void updateUserAvatar(final String url) {
		User  u =new User();
		u.setAvatar(url);
		updateUserData(u,new UpdateListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("ͷ����³ɹ���");
				// ����ͷ��
				refreshAvatar(url);
				
				Message message = new Message();
				message.what = 0;
				handler.sendMessage(message);
				
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("ͷ�����ʧ�ܣ�" + msg);
			}
		});
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
	
	/**
	 * ����ͷ�� refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, setAvator,
					ImageLoadOptions.getOptions());
		} else {
			
			// ������ʾĬ�ϵ�ͷ��
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
		
		progress = new ProgressDialog(RegisterActivity.this);
		progress.setMessage("����ע��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//����ÿ��Ӧ�õ�ע����������϶���һ������IM sdkδ�ṩע�᷽�����û��ɰ���bmod SDK��ע�᷽ʽ����ע�ᡣ
		//ע���ʱ����Ҫע�����㣺1��User���а��豸id��type��2���豸���а�username�ֶ�
		final User bu = new User();
		bu.setUsername(name);
		bu.setPassword(password);
		bu.setSex(sex);
		bu.setNick(nick); // �����ǳ�
		bu.setBirthday(et_birth.getText().toString());
		bu.setGameType(gameType);
		bu.setGameDifficulty("��");
		bu.setLove(et_love.getText().toString());
		bu.setInterests(et_hobbi.getText().toString());
		
		bu.setPersonalizedSignature("δ��д");
		bu.setCareer("δ��д");
		bu.setCompany("δ��д");
		bu.setSchool("δ��д");
		bu.setHometown("δ��д");
		bu.setBook("δ��д");
		bu.setMovie("δ��д");
		bu.setMusic("δ��д");
		bu.setUsuallyAppear("δ��д");
		
		//��user���豸id���а�
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		
		// ����ע�ᣬ�������ݵ�������
		bu.signUp(RegisterActivity.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
				
				// progress.dismiss();
				ShowToast("ע��ɹ�");
				// ���豸��username���а�
				userManager.bindInstallationForRegister(bu.getUsername());
				
				// �����û���ͷ��
				uploadAvatar();
				
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i(arg1);
				ShowToast("ע��ʧ��:" + arg1);
				progress.dismiss();
			}
		});
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
			
		// ǰһҳ
		case R.id.reg_btn_previous:
			showPrevious();
			break;
			
		// ��һҳ
		case R.id.reg_btn_next:
			showNext();
			break;
			
		// ��ȡ��֤��
		case R.id.register_btn_get_verfy_code:
			getVerfyCode();
			break;
			
		// ����ͷ��
		case R.id.register_set_avator:
			showAvatarPop();
			break;
		
		// ��������
		case R.id.register_set_birthday_et:
			seleteBirth();
			break;
			
		// ��������
		case R.id.register_sex_select_male:
			if (!sex) {
				sex = true;
				setSexFemal.setImageDrawable(bitFemale);
				setSexMale.setImageDrawable(bitMaleChoose);
			}
			break;
			
		// ����Ů��
		case R.id.register_sex_select_female:
			if (sex) {
				sex = false;
				setSexFemal.setImageDrawable(bitFemaleChoose);
				setSexMale.setImageDrawable(bitMale);
			}
			break;
			
		// ������Ϸ
		case R.id.register_set_game_et:
			showGameChooseDialog();
			break;
			
		// �������״��
		case R.id.register_set_love_et:
			showLoveChooseDialog();
			break;
			
		default:
			break;
		}
	}
	
	/*
	 * ѡ�����״��
	 * author:Deep
	 */
	private void showLoveChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(RegisterActivity.this,
				CustomApplcation.loveList, "ȷ��", "ȡ��", "���״��", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					et_love.setText("����");
					break;
				case 1:
					et_love.setText("����");
					break;
				case 2:
					et_love.setText("ʧ��");
					break;
				case 3:
					et_love.setText("����");
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
		
		if (TextUtils.isEmpty(et_username.getText().toString())) {
			ShowToast(R.string.toast_error_username_null);
			return;
		}
		
		// ����û����Ƿ��Ѿ�����
		if (!CommonUtils.isNetworkAvailable(RegisterActivity.this)) {
			ShowToast(R.string.network_tips);
			return;
		}
		
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", et_username.getText().toString());
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					ShowToast(R.string.username_has_exited);
					return;
				}
				
				showSurePhoneNumDialog();
			}
		});
			
	}
	
	private void showSurePhoneNumDialog() {
		DialogTips dialogTips = new DialogTips(RegisterActivity.this, "���ǽ����Ͷ�����֤�뵽������룺+86 " + et_username.getText().toString(),
				"ȷ��", "ȡ��", "ȷ���ֻ�����", true);
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// �����Ϊ�գ�����������֤��
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
				
			}
		});
		dialogTips.show();
	}
	
	private void showGameChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(RegisterActivity.this,
				CustomApplcation.gameList, "ȷ��", "ȡ��", "������Ϸ", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					gameType = "ˮ��������";
					break;
				case 1:
					gameType = "������";
					break;
				case 2:
					gameType = "mixed color";
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
			back.setText("����");
		}
		else if (currentPage == 2) {
			viewFlipper.showPrevious();	
			currentPage--;
			back.setText("��һ��");
			next.setText("��һ��");
		}
		else if (currentPage == 3) {
			viewFlipper.showPrevious();	
			currentPage--;
			back.setText("��һ��");
			next.setText("��һ��");
		}
		
	}
	
	private void showNext() {
		if (currentPage == 0) {
			
			if (TextUtils.isEmpty(et_username.getText().toString())) {
				ShowToast(R.string.toast_error_username_null);
				return;
			}

			if (TextUtils.isEmpty(et_confim_code.getText().toString())) {
				ShowToast(R.string.toast_error_confim_code_null);
				return;
			}
			
			SMSSDK.submitVerificationCode("86", et_username.getText().toString(), et_confim_code.getText().toString());
			
			progress = new ProgressDialog(RegisterActivity.this);
			progress.setMessage("������֤");
			progress.setCanceledOnTouchOutside(false);
			progress.show();
			
//			viewFlipper.showNext();
//			currentPage++;
//			back.setText("��һ��");
			
		}
		
		else if (currentPage == 1) {
			if (TextUtils.isEmpty(et_password.getText().toString())) {
				ShowToast(R.string.toast_error_password_null);
				return;
			}
			
			if (et_password.getText().toString().length() < 6) {
				ShowToast(R.string.login_password_number_error);
				return;
			}
			
			if (!et_password_confim.getText().toString().equals(et_password.getText().toString())) {
				ShowToast(R.string.toast_error_comfirm_password);
				return;
			}
			
			viewFlipper.showNext();
			currentPage++;
			next.setText("��һ��");
		}
		
		else if (currentPage == 2) {
			if (!avator_changed) {
				ShowToast(R.string.toast_error_change_avator);
				return;
			}
			
			if (TextUtils.isEmpty(et_nick.getText().toString())) {
				ShowToast(R.string.toast_error_nick_null);
				return;
			}
			
			if (et_nick.getText().toString().length() > 6) {
				ShowToast(R.string.register_nick_too_long_error);
				return;
			}
			if (!hasChoseBirth) {
				ShowToast(R.string.toast_error_birth_null);
				return;
			}
			
			viewFlipper.showNext();
			currentPage++;
			next.setText("ע��");
		}
		else if(currentPage == 3){
			if (!hasChooseGame) {
				ShowToast(R.string.toast_error_game_null);
				return;
			}
			
			if (!hasChooseLove) {
				ShowToast(R.string.toast_error_love_null);
				return;
			}
			
			DialogTips dialogTips = new DialogTips(RegisterActivity.this, "�Ա�ѡ��֮�󲻿ɸı䣬ȷ��ѡ��", 
					"ȷ��", "ȡ��", "ȷ���Ա�ѡ��", true);
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
		
		final DateChooseDialog dateChooseDialog = new DateChooseDialog(RegisterActivity.this, "ȷ��", "ȡ��", "ѡ������", false);
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
					et_birth.setText("ѡ������");
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
		// registerEventHandler�����unregisterEventHandler����ʹ�ã������������ڴ�й©��
		SMSSDK.unregisterEventHandler(eh);
	}

}
