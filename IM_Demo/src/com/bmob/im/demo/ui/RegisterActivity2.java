package com.bmob.im.demo.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.DatePickDialogUtil;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.util.JudgeDate;
import com.bmob.im.demo.util.PhotoUtil;
import com.bmob.im.demo.util.ScreenInfo;
import com.bmob.im.demo.util.WheelMain;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RegisterActivity2 extends BaseActivity implements OnClickListener{

	Button back, next;
	EditText et_username, et_password, et_email, et_nick;
	View birthView;
	RadioGroup sex_select; // �Ա�ѡ��
	Boolean sex;
	Spinner gameChoose;
	
	ArrayAdapter adapter;
	
	Boolean hasChoseBirth = false;
	
	TextView showBirth;
	String birthday = "";
	String initBirth = "";
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	WheelMain wheelMain;
	
	
	int gameType = 0;
	
	
	ViewFlipper viewFlipper;
	int currentPage = 0;
	
	BmobChatUser currentUser;
	
	// ע��ͷ��ѡ���layout�Լ���ť
	RelativeLayout layout_regitster_avator;
	ImageView register_avator, register_avator_arrow;
	
	// �ж��Ƿ��޸���ͷ��
	Boolean avator_changed = false;
	
	RelativeLayout register_layout_all;

	
	RelativeLayout layout_choose, layout_photo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_activity2);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd��");       
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
		initBirth = formatter.format(curDate);   

		// ֻ������а�ť�Լ�����
		initTopBarForLeft("ע��");
		birthView = findViewById(R.id.register_second_birthday_choose);
		gameChoose = (Spinner) findViewById(R.id.register_game_select_spinner);
		viewFlipper = (ViewFlipper) findViewById(R.id.reg_vf_viewflipper);
		
		back = (Button) findViewById(R.id.reg_btn_previous);
		next = (Button) findViewById(R.id.reg_btn_next);
		
		showBirth = (TextView) findViewById(R.id.register_second_show_birthday);

		et_username = (EditText) findViewById(R.id.et_username);
		et_password = (EditText) findViewById(R.id.et_password);
		et_nick = (EditText) findViewById(R.id.et_nick);
		et_email = (EditText) findViewById(R.id.et_email);
		sex_select = (RadioGroup) findViewById(R.id.sex_radioGroup);
		sex = true;
		back.setText("����");
		
		layout_regitster_avator = (RelativeLayout) findViewById(R.id.register_avator);
		register_avator = (ImageView) findViewById(R.id.register_select_avator);
		register_avator_arrow = (ImageView) findViewById(R.id.register_select_avator_arraw);
		register_layout_all = (RelativeLayout) findViewById(R.id.register_layout_all);
		
		layout_regitster_avator.setOnClickListener(this);
		
		
		adapter = ArrayAdapter.createFromResource(this, R.array.games, android.R.layout.simple_spinner_item);
		 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 
		gameChoose.setAdapter(adapter);
		
		gameChoose.setSelection(0);
		
		gameChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				gameType = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sex_select.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int id = group.getCheckedRadioButtonId();
				if (id == R.id.radioMale) {
					sex = true;
				}
				else {
					sex = false;
				}
			}
		});
		
		back.setOnClickListener(this);
		next.setOnClickListener(this);
		birthView.setOnClickListener(this);
		gameChoose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				gameType = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});

//		btn_register = (Button) findViewById(R.id.btn_register);
//		btn_register.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				register();
//			}
//		});
		checkUser();
	}
	
	
	PopupWindow avatorPop;

	public String filePath = "";
	
	private void showAvatarPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator,
				null);
		layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
		layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
		layout_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShowLog("�������");
				// TODO Auto-generated method stub
				layout_choose.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_photo.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));
				
				// �ҵ�ͷ���ַ
				File dir = new File(BmobConstants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// ԭͼ
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()));
				filePath = file.getAbsolutePath();// ��ȡ��Ƭ�ı���·��
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowLog("������");
				layout_photo.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_choose.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
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
	
	
	Bitmap newBitmap;
	boolean isFromCamera = false;// ����������ת
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// �����޸�ͷ��
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				// ��ȡͼƬ����ת�Ƕ�
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "���պ�ĽǶȣ�" + degree);
				
				// ϵͳ�ü�ͷ��
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// �����޸�ͷ��
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD������");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				
				// ϵͳ�ü�ͷ��
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("��Ƭ��ȡʧ��");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// �ü�ͷ�񷵻�
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "ȡ��ѡ��", Toast.LENGTH_SHORT).show();
				return;
			} else {
				// ����ü���ͷ��
				saveCropAvator(data);
			}
			// ��ʼ���ļ�·��
			filePath = "";
			
			// �Ѿ��޸���ͷ��
			avator_changed = true;
			// �ϴ�ͷ��
			// uploadAvatar();
			break;
		default:
			break;

		}
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
	
	
	String path;

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
				register_avator.setImageBitmap(bitmap);
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
				String url = bmobFile.getFileUrl(RegisterActivity2.this);
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
			ImageLoader.getInstance().displayImage(avatar, register_avator,
					ImageLoadOptions.getOptions());
		} else {
			
			// ������ʾĬ�ϵ�ͷ��
			register_avator.setImageResource(R.drawable.default_head);
		}
	}
	
	
	private void checkUser(){
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", "smile");
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if(arg0!=null && arg0.size()>0){
					User user = arg0.get(0);
					user.setPassword("1234567");
					user.update(RegisterActivity2.this, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							userManager.login("smile", "1234567", new SaveListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									Log.i("smile", "��½�ɹ�");
								}
								
								@Override
								public void onFailure(int code, String msg) {
									// TODO Auto-generated method stub
									Log.i("smile", "��½ʧ�ܣ�"+code+".msg = "+msg);
								}
							});
						}
						
						@Override
						public void onFailure(int code, String msg) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		});
	}
	
	private void register(){
		String name = et_username.getText().toString();
		String password = et_password.getText().toString();
		String pwd_again = et_email.getText().toString();
		String nick = et_nick.getText().toString();
		
		
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if(!isNetConnected){
			ShowToast(R.string.network_tips);
			return;
		}
		
		final ProgressDialog progress = new ProgressDialog(RegisterActivity2.this);
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
		bu.setBirthday(showBirth.getText().toString());
		bu.setGameType(gameType);
		bu.setGameDifficulty(1);
		
		//��user���豸id���а�
		bu.setDeviceType("android");
		bu.setInstallId(BmobInstallation.getInstallationId(this));
		
		// ����ע�ᣬ�������ݵ�������
		bu.signUp(RegisterActivity2.this, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
				// �����û���ͷ��
				uploadAvatar();
				progress.dismiss();
				ShowToast("ע��ɹ�");
				// ���豸��username���а�
				userManager.bindInstallationForRegister(bu.getUsername());
				//���µ���λ����Ϣ
				updateUserLocation();
				//���㲥֪ͨ��½ҳ���˳�
				sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
				// ������ҳ
				Intent intent = new Intent(RegisterActivity2.this,MainActivity.class);
				startActivity(intent);
				finish();
				
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
		case R.id.register_avator:
			showAvatarPop();
			break;

		case R.id.reg_btn_previous:
			showPrevious();
			break;
			
		case R.id.reg_btn_next:
			showNext();
			break;
			
		case R.id.register_second_birthday_choose:
//			DatePickDialogUtil dateTimePicKDialog = new DatePickDialogUtil(  
//                    RegisterActivity2.this, initBirth);  
//            dateTimePicKDialog.dateTimePicKDialog(showBirth);  
			
			seleteBirth();
            
			break;
		default:
			break;
		}
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
		
	}
	
	private void showNext() {
		if (currentPage == 0) {
			
			if (TextUtils.isEmpty(et_username.getText().toString())) {
				ShowToast(R.string.toast_error_username_null);
				return;
			}

			if (TextUtils.isEmpty(et_password.getText().toString())) {
				ShowToast(R.string.toast_error_password_null);
				return;
			}
			if (!(et_password.getText().toString()).equals(et_email.getText().toString())) {
				ShowToast(R.string.toast_error_comfirm_password);
				return;
			}
			
			viewFlipper.showNext();
			currentPage++;
			back.setText("��һ��");
			
		}
		
		else if (currentPage == 1) {
			if (TextUtils.isEmpty(et_nick.getText().toString())) {
				ShowToast(R.string.toast_error_nick_null);
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
		
		else if (currentPage == 2) {
			register();
		}
	}
	
	private void seleteBirth() {
		
		
		LayoutInflater inflater=LayoutInflater.from(RegisterActivity2.this);
		final View timepickerview=inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(RegisterActivity2.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = showBirth.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if(JudgeDate.isDate(time, "yyyy-MM-dd")){
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year,month,day);
		new AlertDialog.Builder(RegisterActivity2.this)
		.setTitle("ѡ������")
		.setView(timepickerview)
		.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showBirth.setText(wheelMain.getTime());
				hasChoseBirth = true;
			}
		})
		.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				showBirth.setText("���ѡ������");
				hasChoseBirth = false;
				
			}
		})
		.show();
		
	}

}
