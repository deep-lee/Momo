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
 * 个人资料页面
 * 
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午2:55:19
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility", "InflateParams" })
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

	// iv_arraw表示头像箭头，iv_nickarraw表示昵称箭头
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
		// 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
		setContentView(R.layout.activity_set_info);
		
		// me 我的资料
		// add 附近的人
		// other 我的好友
		from = getIntent().getStringExtra("from");//me add other
		username = getIntent().getStringExtra("username");
		initView();
	}

	private void initView() {
		
		layout_all = (LinearLayout) findViewById(R.id.layout_all); // 整个布局
		iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator); // 头像
		iv_arraw = (ImageView) findViewById(R.id.iv_arraw);           // 头像的箭头
		iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);   // 昵称的箭头
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);      // 名字
		tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);      // 昵称
		tv_set_birthday = (TextView) findViewById(R.id.tv_set_birthday); // 生日
		tv_set_game = (TextView) findViewById(R.id.tv_set_game);
		tv_set_game_difficulty = (TextView) findViewById(R.id.tv_set_game_difficulty);
		layout_head = (RelativeLayout) findViewById(R.id.layout_head); // 头像布局
		layout_nick = (RelativeLayout) findViewById(R.id.layout_nick); // 昵称布局
		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender); // 性别布局
		layout_game_difficulty = (RelativeLayout) findViewById(R.id.layout_game_difficulty); // 游戏难度布局
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
		
		// 黑名单提示语
		layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
		tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);  // 性别
		btn_chat = (Button) findViewById(R.id.btn_chat); // 发起会话按钮
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_add_friend = (Button) findViewById(R.id.btn_add_friend); // 加为好友按钮
		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		btn_back.setEnabled(false);
		
		sexs.add("男");
		sexs.add("女");
		gameList.add("水果连连看");
		gameList.add("猜数字");
		gameList.add("mixed color");
		
		gameDifficultyList.add("简单");
		gameDifficultyList.add("一般");
		gameDifficultyList.add("困难");
		
		// 个人资料
		if (from.equals("me")) {
			initTopBarForLeft("个人资料");
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
			initTopBarForLeft("详细资料");
			iv_nickarraw.setVisibility(View.GONE);
			iv_arraw.setVisibility(View.INVISIBLE);
			//不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
			btn_chat.setEnabled(true);
			btn_chat.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
				if (mApplication.getContactList().containsKey(username)) {// 是好友
					btn_chat.setVisibility(View.VISIBLE);
					btn_chat.setEnabled(true);
					btn_chat.setOnClickListener(this);
					btn_back.setVisibility(View.VISIBLE);
					btn_back.setOnClickListener(this);
				} else { // 不是好友
//					btn_chat.setVisibility(View.GONE);
					btn_back.setVisibility(View.GONE);
					btn_add_friend.setEnabled(true);
					btn_add_friend.setVisibility(View.VISIBLE);
					btn_add_friend.setOnClickListener(this);
				}
			} else {// 我的好友
//				btn_chat.setVisibility(View.VISIBLE);
//				btn_chat.setOnClickListener(this);
				btn_back.setVisibility(View.VISIBLE);
				btn_back.setOnClickListener(this);
			}
			initOtherData(username);
		}
		
		// 女性主题
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
					ShowLog("onSuccess 查无此人");
				}
			}
		});
	}

	private void updateUser(User user) {
		// 更改
		// 更新头像
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");
		tv_set_birthday.setText(user.getBirthday());
		
		
		tv_set_game.setText(user.getGameType());
		
		tv_set_game_difficulty.setText(user.getGameDifficulty());
		
//		switch (user.getGameDifficulty()) {
//		case 0:
//			tv_set_game_difficulty.setText("简单");
//			break;
//		case 1:
//			tv_set_game_difficulty.setText("一般");
//			break;
//		case 2:
//			tv_set_game_difficulty.setText("困难");
//			break;
//		}
		
		// 检测是不是从好友列表中过来的
		if (from.equals("other")) {
			// 检测是否为黑名单用户
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
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
					ImageLoadOptions.getOptions());
		} else {
			
			// 否则显示默认的头像
			iv_set_avator.setImageResource(R.drawable.default_head);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 如果是个人资料的话
		if (from.equals("me")) {
			initMeData();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_chat:// 发起聊天
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", user);
			startAnimActivity(intent);
			finish();
			break;
		case R.id.layout_head:
			// 当是个人资料界面时才能点击头像，然后换头像
			showAvatarPop();
			break;
		case R.id.layout_nick:
			startAnimActivity(UpdateInfoActivity.class);
//			addBlog();
			break;
		case R.id.layout_gender:// 性别
			showSexChooseDialog();
			break;
		case R.id.btn_back:// 黑名单
			showBlackDialog(user.getUsername());
			break;
		case R.id.btn_add_friend://添加好友
			addFriend();
			break;
		case R.id.layout_birthday: // 生日
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
	
	// 游戏难度选择
	private void showGameDifficultyChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(SetMyInfoActivity.this,
				gameDifficultyList, "确定", "取消", "游戏难度", true);
		
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
			u.setGameDifficulty("简单");
		}else if(which == 1){
			u.setGameDifficulty("一般");
		}
		else if(which == 2){
			u.setGameDifficulty("困难");
		}
		
		updateUserData(u,new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				switch (which) {
				case 0:
					tv_set_game_difficulty.setText("简单");
					break;

				case 1:
					tv_set_game_difficulty.setText("一般");
					break;
					
				case 2:
					tv_set_game_difficulty.setText("困难");
					break;
				}
				
				ShowToast("修改成功");
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
				gameList, "确定", "取消", "解锁游戏", true);
		
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
			u.setGameType("水果连连看");
		}else if(which == 1){
			u.setGameType("猜数字");
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
					tv_set_game.setText("水果连连看");
					break;

				case 1:
					tv_set_game.setText("猜数字");
					break;
					
				case 2:
					tv_set_game.setText("mixed color");
					break;
				}
				
				ShowToast("修改成功");
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
//		.setTitle("选择日期")
//		.setView(timepickerview)
//		.setPositiveButton("设置", new DialogInterface.OnClickListener() {
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
//						ShowToast("修改成功");
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
//		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//			}
//		})
//		.show();
		
		final DateChooseDialog dateChooseDialog = new DateChooseDialog(SetMyInfoActivity.this, "确定", "取消", "选择生日", false);
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
						ShowToast("修改成功");
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
				sexs, "确定", "取消", "性别", true);
		
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

	
	/** 修改资料
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
				ShowToast("修改成功");
				tv_set_gender.setText(u.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
	/**
	 * 添加好友请求
	 * 
	 * @Title: addFriend
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求，TAG_ADD_CONTACT表示添加好友
		
//		
//		给指定用户推送Tag标记的消息请提供回调操作:此方法方便开发者使用自定义tag标记的消息，不携带回调方法
//
//    参数：
//        tag - 消息类型
//        installId - 目标用户绑定的设备id
//        pushCallback - 发送回调 
		
		BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
				user.getObjectId(), new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求失败！");
						ShowLog("发送请求失败:" + arg1);
					}
				});
	}

	/**
	 * 显示黑名单提示框
	 * 
	 * @Title: showBlackDialog
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void showBlackDialog(final String username) {
		DialogTips dialog = new DialogTips(this, "加入黑名单",
				"加入黑名单，你将不再收到对方的消息，确定要继续吗？", "确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				// 添加到黑名单列表
				userManager.addBlack(username, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowToast("黑名单添加成功!");
						btn_back.setVisibility(View.GONE);
						layout_black_tips.setVisibility(View.VISIBLE);
						// 重新设置下内存中保存的好友列表
						CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("黑名单添加失败:" + arg1);
					}
				});
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	LinearLayout layout_choose;
	LinearLayout layout_photo;
	LinearLayout layout_cancle;
	PopupWindow avatorPop;

	public String filePath = "";

	/*
	 * 当是个人资料界面时才能点击头像，然后换头像
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
				ShowLog("点击拍照");
				// TODO Auto-generated method stub
				layout_choose.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_photo.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));
				
				// 我的头像地址
				File dir = new File(BmobConstants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 原图
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()));
				filePath = file.getAbsolutePath();// 获取相片的保存路径
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
				ShowLog("点击相册");
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
		// 动画效果 从底部弹起
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
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				// 获取图片的旋转角度
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				
				// 系统裁减头像
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
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
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				
				// 系统裁减头像
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				// 保存裁减的头像
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

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
				String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
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
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}

	String path;

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
				iv_set_avator.setImageBitmap(bitmap);
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
	
	/** 测试关联关系是否可用
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
		blog.setBrief("你好");
		blog.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				BmobLog.i("blog保存成功");
				User  u =new User();
				u.setBlog(blog);
				updateUserData(u, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						BmobLog.i("user更新成功");
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
