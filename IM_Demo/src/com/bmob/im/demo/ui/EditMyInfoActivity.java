package com.bmob.im.demo.ui;

import java.io.File;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.id;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.view.EditTextWithDel;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DateChooseDialog;
import com.bmob.im.demo.view.dialog.SingleChoiceDialog;
import com.soundcloud.android.crop.Crop;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditMyInfoActivity extends ActivityBase implements OnClickListener{
	
	RelativeLayout rl_edit_birthday, rl_edit_game, rl_edit_game_difficulty, 
				   rl_edit_love_status, rl_edit_hometown, rl_edit_career;
	
	EditTextWithDel et_nick, et_personalized_signature, et_company, et_school, et_book, 
					et_movie, et_music, et_interests, et_usually_appear;
	
	TextView tv_birthday, tv_game, tv_game_difficulty, tv_love_status, tv_hometown, tv_career;
	

	User currentUser;
	
	String gameType, gameDifficulty, lovtStatus;
	
	CustomProgressDialog updateDialog = null;
	
	public static int SELECT_CAREER = 10;
	
	String careerType = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_my_info);
		initView();
	}
	
	private void initView() {
		
//		initTopBarForBoth("编辑资料", R.drawable.base_action_bar_send_selector, "保存", new HeaderLayout.onRightImageButtonClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// 检查哪些有更改，并检查网络连接，以及更改资料
//				checkAndUpdate();
//			}
//		});
		
		currentUser = userManager.getCurrentUser(User.class);
		
		rl_edit_birthday = (RelativeLayout) findViewById(R.id.rl_edit_birthday_layout);
		rl_edit_game = (RelativeLayout) findViewById(R.id.rl_edit_game_layout);
		rl_edit_game_difficulty = (RelativeLayout) findViewById(R.id.rl_edit_game_difficulty);
		rl_edit_love_status = (RelativeLayout) findViewById(R.id.rl_edit_love_status_layout);
		rl_edit_hometown = (RelativeLayout) findViewById(R.id.rl_edit_profile9);
		
		rl_edit_birthday.setOnClickListener(this);
		rl_edit_game.setOnClickListener(this);
		rl_edit_game_difficulty.setOnClickListener(this);
		rl_edit_love_status.setOnClickListener(this);
		rl_edit_hometown.setOnClickListener(this);
		rl_edit_career = (RelativeLayout) findViewById(R.id.rl_edit_profile6);
		rl_edit_career.setOnClickListener(this);
		
		et_nick = (EditTextWithDel) findViewById(R.id.info_edit_nick_details);
		et_personalized_signature = (EditTextWithDel) findViewById(R.id.info_edit_personalized_signature_details);
		et_company = (EditTextWithDel) findViewById(R.id.info_edit_company_details);
		et_school = (EditTextWithDel) findViewById(R.id.info_edit_school_details);
		et_book = (EditTextWithDel) findViewById(R.id.info_edit_book_details);
		et_movie = (EditTextWithDel) findViewById(R.id.info_edit_movie_details);
		et_music = (EditTextWithDel) findViewById(R.id.info_edit_music_details);
		et_interests = (EditTextWithDel) findViewById(R.id.info_edit_interests_details);
		et_usually_appear = (EditTextWithDel) findViewById(R.id.info_edit_usually_appear_details);
		
		tv_birthday = (TextView) findViewById(R.id.info_edit_birthday_details);
		tv_career = (TextView) findViewById(R.id.info_edit_career_details);
		tv_game = (TextView) findViewById(R.id.info_edit_game_details);
		tv_game_difficulty = (TextView) findViewById(R.id.info_edit_game_difficulty_details);
		tv_love_status = (TextView) findViewById(R.id.info_edit_love_status_details);
		tv_hometown = (TextView) findViewById(R.id.info_edit_hometown_details);
		
		Boolean netAvaliable = CommonUtils.isNetworkAvailable(this);
		if (!netAvaliable) {
			ShowToast(R.string.network_tips);
		}
		
		// 没网的情况下获取不到currentUser
		if (currentUser != null) {
			et_nick.setText(currentUser.getNick());
			tv_birthday.setText(currentUser.getBirthday());
			
			if (currentUser.getPersonalizedSignature() == null || currentUser.getPersonalizedSignature().equals("未填写")) {
				et_personalized_signature.setText("");
			}
			else {
				et_personalized_signature.setText(currentUser.getPersonalizedSignature());
			}
			
			tv_game.setText(currentUser.getGameType());
			tv_game_difficulty.setText(currentUser.getGameDifficulty());
			tv_love_status.setText(currentUser.getLove());
			
			if (currentUser.getCareer() == null || currentUser.getCareer().equals("未填写")) {
				tv_career.setText("请选择所属行业");
			}else {
				tv_career.setText(currentUser.getCareer());
			}
			
			if (currentUser.getCompany() == null || currentUser.getCompany().equals("未填写")) {
				et_company.setText("");
			}else {
				et_company.setText(currentUser.getCompany());
			}
			
			if (currentUser.getSchool() == null || currentUser.getSchool().equals("未填写")) {
				et_school.setText("");
			}else {
				et_school.setText(currentUser.getSchool());
			}
			
			if (currentUser.getHometown() == null || currentUser.getHometown().equals("未填写")) {
				tv_hometown.setText("请选择你的家乡");
			}else {
				tv_hometown.setText(currentUser.getHometown());
			}
			
			if (currentUser.getBook() == null || currentUser.getBook().equals("未填写")) {
				et_book.setText("");
			}else {
				et_book.setText(currentUser.getBook());
			}
			
			if (currentUser.getMovie() == null || currentUser.getMovie().equals("未填写")) {
				et_movie.setText("");
			}else {
				et_movie.setText(currentUser.getMovie());
			}
			
			if (currentUser.getMusic() == null || currentUser.getMusic().equals("未填写")) {
				et_music.setText("");
			}else {
				et_music.setText(currentUser.getMusic());
			}
			
			
			if (currentUser.getInterests() == null || currentUser.getInterests().equals("未填写")) {
				et_interests.setText("");
			}
			else {
				et_interests.setText(currentUser.getInterests());
			}
			
			if (currentUser.getUsuallyAppear() == null || currentUser.getUsuallyAppear().equals("未填写")) {
				et_usually_appear.setText("");
			}else {
				et_usually_appear.setText(currentUser.getUsuallyAppear());
			}
		}else {
			ShowToast("获取资料失败！");
		}
		
	}
	
	public void saveMyInfo(View view) {
		checkAndUpdate();
	}
	
	/*
	 * 检查并更新资料
	 */
	private void checkAndUpdate() {
		 boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		 if (!isNetConnected) {
			 ShowToast(R.string.network_tips);
				return;
		}
		
		 // 昵称是否小于6
		 if (et_nick.getText().toString().length() > 6) {
			ShowToast(R.string.register_nick_too_long_error);
			return;
		}
		 
		 // 昵称是否未空
		 if (TextUtils.isEmpty(et_nick.getText().toString())) {
			ShowToast(R.string.toast_error_nick_null);
			return;
		}
		 
		// 更新资料
		User user = new User();
		user.setNick(et_nick.getText().toString());
		user.setBirthday(tv_birthday.getText().toString());
		
		if (et_personalized_signature.getText().toString().equals("")) {
			user.setPersonalizedSignature("未填写");
		}
		else {
			user.setPersonalizedSignature(et_personalized_signature.getText().toString());
		}
		
		user.setGameType(tv_game.getText().toString());
		user.setGameDifficulty(tv_game_difficulty.getText().toString());
		user.setLove(tv_love_status.getText().toString());
		
		if (tv_career.getText().toString().equals("请选择所属行业")) {
			user.setCareer("未填写");
		}else {
			user.setCareer(tv_career.getText().toString());
		}
		
		if (et_company.getText().toString().equals("")) {
			user.setCompany("未填写");
		}else {
			user.setCompany(et_company.getText().toString());
		}
		
		if (et_school.getText().toString().equals("")) {
			user.setSchool("未填写");
		}else {
			user.setSchool(et_school.getText().toString());
		}
		
		if (tv_hometown.getText().toString().equals("请选择你的家乡")) {
			user.setHometown("未填写");
		}else {
			user.setHometown(tv_hometown.getText().toString());
		}
		
		if (et_book.getText().toString().equals("")) {
			user.setBook("未填写");
		}else {
			user.setBook(et_book.getText().toString());
		}
		
		if (et_movie.getText().toString().equals("")) {
			user.setMovie("未填写");
		}else {
			user.setMovie(et_movie.getText().toString());
		}
		if (et_music.getText().toString().equals("")) {
			user.setMusic("未填写");
		}else {
			user.setMusic(et_music.getText().toString());
		}
		if (et_interests.getText().toString().equals("")) {
			user.setInterests("未填写");
		}else {
			user.setInterests(et_interests.getText().toString());
		}
		
		if (et_usually_appear.getText().toString().equals("")) {
			user.setUsuallyAppear("未填写");
		}else {
			user.setUsuallyAppear(et_usually_appear.getText().toString());
		}
		
		updateDialog = new CustomProgressDialog(EditMyInfoActivity.this, "正在更新资料...");
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.setCancelable(false);
		updateDialog.show();
		
		updateUserData(user, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("更新成功");
				updateDialog.dismiss();
				// 返回到资料界面

				setResult(RESULT_OK, getIntent()); // intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
				getIntent().putExtra("update", true);
				finish(); //此处一定要调用finish()方法

			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		// 生日
		case R.id.rl_edit_birthday_layout:
			seleteBirth();
			break;
			
		// 游戏
		case R.id.rl_edit_game_layout:
			showGameChooseDialog();
			break;
			
		// 游戏难度
		case R.id.rl_edit_game_difficulty:
			showGameDiffiultySelectDialog();
			break;
						
		// 情感状况
		case R.id.rl_edit_love_status_layout:
			showLoveChooseDialog();
			break;
						
		// 家乡
		case R.id.rl_edit_profile9:
			selectHometown();
			break;
			
		// 职业
		case R.id.rl_edit_profile6:
			showCareerSelectDialog();
			break;
		}
	}
	
	private void showCareerSelectDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.careerList, "确定", "取消", "选择行业", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				careerType = CustomApplcation.careerList.get(selectItem);

				if (careerType.equals("无")) {
					tv_career.setText("请选择所属行业");
				}else {
					tv_career.setText(careerType);
				}
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
	
	
	
	/*
	 * 选择家乡
	 */
	private void selectHometown() {
		Intent i = new Intent(EditMyInfoActivity.this, ProvinceListActivity.class);
		startActivityForResult(i, 1);
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (version >= 5) {
			overridePendingTransition(R.anim.zoomin, R.anim.zoomout); // 
			// overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
			// overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == Activity.RESULT_OK) {
			// 选择家乡
            if(requestCode == 1) {
            	String cityname;
        		if (data == null) {

        			return;
        		}
        		cityname = data.getStringExtra("cityname");
        		tv_hometown.setText(cityname);
           
            }
        }
		
		
	}
	
	/*
	 * 选择生日
	 */
	private void seleteBirth(){
		
		final DateChooseDialog dateChooseDialog = new DateChooseDialog(EditMyInfoActivity.this, "确定", "取消", "选择生日", false);
		dateChooseDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				tv_birthday.setText(dateChooseDialog.getDate());
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
	
	// 选择游戏
	private void showGameChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.gameList, "确定", "取消", "解锁游戏", true);
		
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
				default:
					break;
				}
				tv_game.setText(gameType);
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
	
	/*
	 * 选择游戏难度
	 * 
	 */
	
	private void showGameDiffiultySelectDialog() {
			
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.gameDifficultyList, "确定", "取消", "游戏难度", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					gameDifficulty = "简单";
					break;
				case 1:
					gameDifficulty = "一般";
					break;
				case 2:
					gameDifficulty = "困难";
					break;
				default:
					break;
				}
				tv_game_difficulty.setText(gameDifficulty);
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
	
	/*
	 * 选择情感状况
	 * author:Deep
	 */
	private void showLoveChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.loveList, "确定", "取消", "情感状况", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					tv_love_status.setText("热恋");
					break;
				case 1:
					tv_love_status.setText("单身");
					break;
				case 2:
					tv_love_status.setText("失恋");
					break;
				case 3:
					tv_love_status.setText("保密");
					break;
				default:
					break;
				}
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
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
