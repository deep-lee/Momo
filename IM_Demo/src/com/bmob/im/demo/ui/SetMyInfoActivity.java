package com.bmob.im.demo.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.Blog;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.util.JudgeDate;
import com.bmob.im.demo.util.PhotoUtil;
import com.bmob.im.demo.util.ScreenInfo;
import com.bmob.im.demo.util.WheelMain;
import com.bmob.im.demo.view.dialog.DateChooseDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.dialog.SingleChoiceDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * ��������ҳ��
 * 
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 ����2:55:19
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility", "InflateParams" })
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

	// iv_arraw��ʾͷ���ͷ��iv_nickarraw��ʾ�ǳƼ�ͷ
	TextView tv_set_name, tv_set_nick, tv_set_gender, tv_set_birthday, tv_set_game, tv_set_game_difficulty;
	ImageView iv_set_avator, iv_arraw, iv_nickarraw, iv_sexarrow, iv_birthdayarrow, iv_gamearrow;
	LinearLayout layout_all;

	Button btn_chat, btn_back, btn_add_friend;
	RelativeLayout layout_head, layout_nick, layout_gender, layout_black_tips, 
					layout_birthday, layout_game, layout_game_difficulty, layout_photo_wall;

	String from = "";
	String username = "";
	User user;
	
	List<String> sexs = new ArrayList<String>();
	List<String> gameList = new ArrayList<String>();
	List<String> gameDifficultyList = new ArrayList<String>();
	

	WheelMain wheelMain;
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��Ϊ�����ֻ���������������ĵ�����ť����Ҫ�������ص�����Ȼ���ڵ����պ����������ť������setContentView֮ǰ���ò�����Ч
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
		setContentView(R.layout.activity_set_info);
		
		// me �ҵ�����
		// add ��������
		// other �ҵĺ���
		from = getIntent().getStringExtra("from");//me add other
		username = getIntent().getStringExtra("username");
		initView();
	}

	private void initView() {
		
		layout_all = (LinearLayout) findViewById(R.id.layout_all); // ��������
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator); // ͷ��
		iv_arraw = (ImageView) findViewById(R.id.iv_arraw);           // ͷ��ļ�ͷ
		iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);   // �ǳƵļ�ͷ
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);      // ����
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);      // �ǳ�
		tv_set_birthday = (TextView) findViewById(R.id.tv_set_birthday); // ����
		tv_set_game = (TextView) findViewById(R.id.tv_set_game);
		tv_set_game_difficulty = (TextView) findViewById(R.id.tv_set_game_difficulty);
		layout_head = (RelativeLayout) findViewById(R.id.layout_head); // ͷ�񲼾�
		layout_nick = (RelativeLayout) findViewById(R.id.layout_nick); // �ǳƲ���
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender); // �Ա𲼾�
		layout_game_difficulty = (RelativeLayout) findViewById(R.id.layout_game_difficulty); // ��Ϸ�ѶȲ���
		layout_photo_wall = (RelativeLayout) findViewById(R.id.layout_photo_wall);
		layout_photo_wall.setOnClickListener(this);
		
		iv_sexarrow = (ImageView) findViewById(R.id.iv_sexarraw);
		iv_birthdayarrow = (ImageView) findViewById(R.id.iv_birthdayarraw);
		iv_gamearrow = (ImageView) findViewById(R.id.iv_gamearraw);
		
		iv_sexarrow.setVisibility(View.GONE);
		iv_birthdayarrow.setVisibility(View.GONE);
		iv_gamearrow.setVisibility(View.GONE);
		layout_game_difficulty.setVisibility(View.GONE);
		
		layout_birthday = (RelativeLayout) findViewById(R.id.layout_birthday);
		layout_game = (RelativeLayout) findViewById(R.id.layout_game);
		
		// ��������ʾ��
		layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);  // �Ա�
		btn_chat = (Button) findViewById(R.id.btn_chat); // ����Ự��ť
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend); // ��Ϊ���Ѱ�ť
		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		btn_back.setEnabled(false);
		
		sexs.add("��");
		sexs.add("Ů");
		gameList.add("ˮ��������");
		gameList.add("������");
		gameList.add("mixed color");
		
		gameDifficultyList.add("��");
		gameDifficultyList.add("һ��");
		gameDifficultyList.add("����");
		
		// ��������
		if (from.equals("me")) {
			initTopBarForLeft("��������");
			layout_head.setOnClickListener(this);
			layout_nick.setOnClickListener(this);
			layout_gender.setOnClickListener(this);
			layout_birthday.setOnClickListener(this);
			layout_game.setOnClickListener(this);
			iv_nickarraw.setVisibility(View.VISIBLE);
			iv_arraw.setVisibility(View.VISIBLE);
			iv_sexarrow.setVisibility(View.VISIBLE);
			iv_birthdayarrow.setVisibility(View.VISIBLE);
			iv_gamearrow.setVisibility(View.VISIBLE);
			btn_back.setVisibility(View.GONE);
			btn_chat.setVisibility(View.GONE);
			btn_add_friend.setVisibility(View.GONE);
			layout_game_difficulty.setVisibility(View.VISIBLE);
			layout_game_difficulty.setOnClickListener(this);
		} else {
			initTopBarForLeft("��ϸ����");
			iv_nickarraw.setVisibility(View.GONE);
			iv_arraw.setVisibility(View.INVISIBLE);
			//���ܶԷ��ǲ�����ĺ��ѣ������Է�����Ϣ--BmobIM_V1.1.2�޸�
			btn_chat.setEnabled(true);
			btn_chat.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			if (from.equals("add")) {// �Ӹ��������б���Ӻ���--��Ϊ��ȡ�������˵ķ����������Ƿ���ʾ���ѵ�����������������Ҫ�ж�������û��Ƿ����Լ��ĺ���
				if (mApplication.getContactList().containsKey(username)) {// �Ǻ���
					btn_chat.setVisibility(View.VISIBLE);
					btn_chat.setEnabled(true);
					btn_chat.setOnClickListener(this);
					btn_back.setVisibility(View.VISIBLE);
					btn_back.setOnClickListener(this);
				} else { // ���Ǻ���
//					btn_chat.setVisibility(View.GONE);
					btn_back.setVisibility(View.GONE);
					btn_add_friend.setEnabled(true);
					btn_add_friend.setVisibility(View.VISIBLE);
					btn_add_friend.setOnClickListener(this);
				}
			} else {// �ҵĺ���
//				btn_chat.setVisibility(View.VISIBLE);
//				btn_chat.setOnClickListener(this);
				btn_back.setVisibility(View.VISIBLE);
				btn_back.setOnClickListener(this);
			}
			initOtherData(username);
		}
		
		// Ů������
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
		}
	}

	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("hight = "+user.getHight()+",sex= "+user.getSex());
		
		initOtherData(user.getUsername());
	}

	private void initOtherData(String name) {
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("onError onError:" + arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				// TODO Auto-generated method stub
				if (arg0 != null && arg0.size() > 0) {
					user = arg0.get(0);
					btn_chat.setEnabled(true);
					btn_back.setEnabled(true);
					btn_add_friend.setEnabled(true);
					updateUser(user);
				} else {
					ShowLog("onSuccess ���޴���");
				}
			}
		});
	}

	private void updateUser(User user) {
		// ����
		// ����ͷ��
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");
		tv_set_birthday.setText(user.getBirthday());
		
		
		tv_set_game.setText(user.getGameType());
		
		tv_set_game_difficulty.setText(user.getGameDifficulty());
		
//		switch (user.getGameDifficulty()) {
//		case 0:
//			tv_set_game_difficulty.setText("��");
//			break;
//		case 1:
//			tv_set_game_difficulty.setText("һ��");
//			break;
//		case 2:
//			tv_set_game_difficulty.setText("����");
//			break;
//		}
		
		// ����ǲ��ǴӺ����б��й�����
		if (from.equals("other")) {
			// ����Ƿ�Ϊ�������û�
			if (BmobDB.create(this).isBlackUser(user.getUsername())) {
				btn_back.setVisibility(View.GONE);
				layout_black_tips.setVisibility(View.VISIBLE);
			} else {
				btn_back.setVisibility(View.VISIBLE);
				layout_black_tips.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * ����ͷ�� refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
					ImageLoadOptions.getOptions());
		} else {
			
			// ������ʾĬ�ϵ�ͷ��
			iv_set_avator.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// ����Ǹ������ϵĻ�
		if (from.equals("me")) {
			initMeData();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_chat:// ��������
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", user);
			startAnimActivity(intent);
			finish();
			break;
		case R.id.layout_head:
			// ���Ǹ������Ͻ���ʱ���ܵ��ͷ��Ȼ��ͷ��
			showAvatarPop();
			break;
		case R.id.layout_nick:
			startAnimActivity(UpdateInfoActivity.class);
//			addBlog();
			break;
		case R.id.layout_gender:// �Ա�
			showSexChooseDialog();
			break;
		case R.id.btn_back:// ������
			showBlackDialog(user.getUsername());
			break;
		case R.id.btn_add_friend://��Ӻ���
			addFriend();
			break;
		case R.id.layout_birthday: // ����
			showBirthdayChooseDialog();
			break;
		case R.id.layout_game:
			showGameChooseDialog();
			break;
		case R.id.layout_game_difficulty:
			showGameDifficultyChooseDialog();
			break;
		case R.id.layout_photo_wall:
			Intent intent2 = new Intent();
			intent2.setClass(SetMyInfoActivity.this, PhotoWallFallActivity.class);
			intent2.putExtra("from", from);
			startActivity(intent2);
			break;
		}
	}
	
	// ��Ϸ�Ѷ�ѡ��
	private void showGameDifficultyChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(SetMyInfoActivity.this,
				gameDifficultyList, "ȷ��", "ȡ��", "��Ϸ�Ѷ�", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				if (gameDifficultyList.get(selectItem).equals(tv_set_game_difficulty.getText().toString())) {
					return;
				}else {
					updateGameDifficulty(selectItem);
					singleChoiceDialog.dismiss();
				}
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
	
	
	private void updateGameDifficulty(final int which) {
		final User u = new User();
		
		if(which == 0){
			u.setGameDifficulty("��");
		}else if(which == 1){
			u.setGameDifficulty("һ��");
		}
		else if(which == 2){
			u.setGameDifficulty("����");
		}
		
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					tv_set_game_difficulty.setText("��");
					break;

				case 1:
					tv_set_game_difficulty.setText("һ��");
					break;
					
				case 2:
					tv_set_game_difficulty.setText("����");
					break;
				}
				
				ShowToast("�޸ĳɹ�");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	private void showGameChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(SetMyInfoActivity.this,
				gameList, "ȷ��", "ȡ��", "������Ϸ", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				if (gameList.get(selectItem).equals(tv_set_game.getText().toString())) {
					return;
				}else {
					updateGame(selectItem);
					singleChoiceDialog.dismiss();
				}
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
	
	private void updateGame(final int which) {
		
		final User u = new User();
		
		if(which == 0){
			u.setGameType("ˮ��������");
		}else if(which == 1){
			u.setGameType("������");
		}
		else if(which == 2){
			u.setGameType("mixed color");
		}
		
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					tv_set_game.setText("ˮ��������");
					break;

				case 1:
					tv_set_game.setText("������");
					break;
					
				case 2:
					tv_set_game.setText("mixed color");
					break;
				}
				
				ShowToast("�޸ĳɹ�");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
	
	private void showBirthdayChooseDialog() {
		
//		LayoutInflater inflater=LayoutInflater.from(SetMyInfoActivity.this);
//		final View timepickerview=inflater.inflate(R.layout.timepicker, null);
//		ScreenInfo screenInfo = new ScreenInfo(SetMyInfoActivity.this);
//		wheelMain = new WheelMain(timepickerview);
//		wheelMain.screenheight = screenInfo.getHeight();
//		String time = tv_set_birthday.getText().toString();
//		Calendar calendar = Calendar.getInstance();
//		if(JudgeDate.isDate(time, "yyyy-MM-dd")){
//			try {
//				calendar.setTime(dateFormat.parse(time));
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		int year = calendar.get(Calendar.YEAR);
//		int month = calendar.get(Calendar.MONTH);
//		int day = calendar.get(Calendar.DAY_OF_MONTH);
//		wheelMain.initDateTimePicker(year,month,day);
//		new AlertDialog.Builder(SetMyInfoActivity.this)
//		.setTitle("ѡ������")
//		.setView(timepickerview)
//		.setPositiveButton("����", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
//				final User u = new User();
//				u.setBirthday(wheelMain.getTime());
//				updateUserData(u,new UpdateListener() {
//
//					@Override
//					public void onSuccess() {
//						// TODO Auto-generated method stub
//						ShowToast("�޸ĳɹ�");
//						tv_set_birthday.setText(wheelMain.getTime());
//					}
//
//					@Override
//					public void onFailure(int arg0, String arg1) {
//						// TODO Auto-generated method stub
//						ShowToast("onFailure:" + arg1);
//					}
//				});
//			}
//		})
//		.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//			}
//		})
//		.show();
		
		final DateChooseDialog dateChooseDialog = new DateChooseDialog(SetMyInfoActivity.this, "ȷ��", "ȡ��", "ѡ������", false);
		dateChooseDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				final User u = new User();
				u.setBirthday(dateChooseDialog.getDate());
				updateUserData(u,new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowToast("�޸ĳɹ�");
						tv_set_birthday.setText(dateChooseDialog.getDate());
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("onFailure:" + arg1);
					}
				});
			}
		});
		dateChooseDialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		
		dateChooseDialog.show();
		
	}
	
	

	private void showSexChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(SetMyInfoActivity.this,
				sexs, "ȷ��", "ȡ��", "�Ա�", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				if (sexs.get(selectItem).equals(tv_set_gender.getText().toString())) {
					return;
				}else {
					updateInfo(selectItem);
					singleChoiceDialog.dismiss();
				}
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

	
	/** �޸�����
	  * updateInfo
	  * @Title: updateInfo
	  * @return void
	  * @throws
	  */
	private void updateInfo(int which) {
		final User u = new User();
		if(which == 0){
			u.setSex(true);
		}else{
			u.setSex(false);
		}
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("�޸ĳɹ�");
				tv_set_gender.setText(u.getSex() == true ? "��" : "Ů");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
	/**
	 * ��Ӻ�������
	 * 
	 * @Title: addFriend
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("�������...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// ����tag����TAG_ADD_CONTACT��ʾ��Ӻ���
		
//		
//		��ָ���û�����Tag��ǵ���Ϣ���ṩ�ص�����:�˷������㿪����ʹ���Զ���tag��ǵ���Ϣ����Я���ص�����
//
//    ������
//        tag - ��Ϣ����
//        installId - Ŀ���û��󶨵��豸id
//        pushCallback - ���ͻص� 
		
		BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
				user.getObjectId(), new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("��������ɹ����ȴ��Է���֤��");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("��������ʧ�ܣ�");
						ShowLog("��������ʧ��:" + arg1);
					}
				});
	}

	/**
	 * ��ʾ��������ʾ��
	 * 
	 * @Title: showBlackDialog
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void showBlackDialog(final String username) {
		DialogTips dialog = new DialogTips(this, "���������",
				"������������㽫�����յ��Է�����Ϣ��ȷ��Ҫ������", "ȷ��", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				// ��ӵ��������б�
				userManager.addBlack(username, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowToast("��������ӳɹ�!");
						btn_back.setVisibility(View.GONE);
						layout_black_tips.setVisibility(View.VISIBLE);
						// �����������ڴ��б���ĺ����б�
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("���������ʧ��:" + arg1);
					}
				});
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	LinearLayout layout_choose;
	LinearLayout layout_photo;
	LinearLayout layout_cancle;
	PopupWindow avatorPop;

	public String filePath = "";

	/*
	 * ���Ǹ������Ͻ���ʱ���ܵ��ͷ��Ȼ��ͷ��
	 */
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
		avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
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
			// �ϴ�ͷ��
			uploadAvatar();
			break;
		default:
			break;

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
				String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
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
				iv_set_avator.setImageBitmap(bitmap);
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
	
	/** ���Թ�����ϵ�Ƿ����
	  * @Title: addBlog
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void addBlog(){
		//		BmobRelation relation = new BmobRelation();
		//		blog.setObjectId("c7a9ca9c0c");
		//		relation.add(blog);
		//		user.setBlogs(relation);
		final Blog blog = new Blog();
		blog.setBrief("���");
		blog.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				BmobLog.i("blog����ɹ�");
				User  u =new User();
				u.setBlog(blog);
				updateUserData(u, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						BmobLog.i("user���³ɹ�");
					}
					
					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
						BmobLog.i("code = "+code+",msg = "+msg);
					}
				});
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}

}
