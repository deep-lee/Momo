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
		
//		initTopBarForBoth("�༭����", R.drawable.base_action_bar_send_selector, "����", new HeaderLayout.onRightImageButtonClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// �����Щ�и��ģ�������������ӣ��Լ���������
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
		
		// û��������»�ȡ����currentUser
		if (currentUser != null) {
			et_nick.setText(currentUser.getNick());
			tv_birthday.setText(currentUser.getBirthday());
			
			if (currentUser.getPersonalizedSignature() == null || currentUser.getPersonalizedSignature().equals("δ��д")) {
				et_personalized_signature.setText("");
			}
			else {
				et_personalized_signature.setText(currentUser.getPersonalizedSignature());
			}
			
			tv_game.setText(currentUser.getGameType());
			tv_game_difficulty.setText(currentUser.getGameDifficulty());
			tv_love_status.setText(currentUser.getLove());
			
			if (currentUser.getCareer() == null || currentUser.getCareer().equals("δ��д")) {
				tv_career.setText("��ѡ��������ҵ");
			}else {
				tv_career.setText(currentUser.getCareer());
			}
			
			if (currentUser.getCompany() == null || currentUser.getCompany().equals("δ��д")) {
				et_company.setText("");
			}else {
				et_company.setText(currentUser.getCompany());
			}
			
			if (currentUser.getSchool() == null || currentUser.getSchool().equals("δ��д")) {
				et_school.setText("");
			}else {
				et_school.setText(currentUser.getSchool());
			}
			
			if (currentUser.getHometown() == null || currentUser.getHometown().equals("δ��д")) {
				tv_hometown.setText("��ѡ����ļ���");
			}else {
				tv_hometown.setText(currentUser.getHometown());
			}
			
			if (currentUser.getBook() == null || currentUser.getBook().equals("δ��д")) {
				et_book.setText("");
			}else {
				et_book.setText(currentUser.getBook());
			}
			
			if (currentUser.getMovie() == null || currentUser.getMovie().equals("δ��д")) {
				et_movie.setText("");
			}else {
				et_movie.setText(currentUser.getMovie());
			}
			
			if (currentUser.getMusic() == null || currentUser.getMusic().equals("δ��д")) {
				et_music.setText("");
			}else {
				et_music.setText(currentUser.getMusic());
			}
			
			
			if (currentUser.getInterests() == null || currentUser.getInterests().equals("δ��д")) {
				et_interests.setText("");
			}
			else {
				et_interests.setText(currentUser.getInterests());
			}
			
			if (currentUser.getUsuallyAppear() == null || currentUser.getUsuallyAppear().equals("δ��д")) {
				et_usually_appear.setText("");
			}else {
				et_usually_appear.setText(currentUser.getUsuallyAppear());
			}
		}else {
			ShowToast("��ȡ����ʧ�ܣ�");
		}
		
	}
	
	public void saveMyInfo(View view) {
		checkAndUpdate();
	}
	
	/*
	 * ��鲢��������
	 */
	private void checkAndUpdate() {
		 boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		 if (!isNetConnected) {
			 ShowToast(R.string.network_tips);
				return;
		}
		
		 // �ǳ��Ƿ�С��6
		 if (et_nick.getText().toString().length() > 6) {
			ShowToast(R.string.register_nick_too_long_error);
			return;
		}
		 
		 // �ǳ��Ƿ�δ��
		 if (TextUtils.isEmpty(et_nick.getText().toString())) {
			ShowToast(R.string.toast_error_nick_null);
			return;
		}
		 
		// ��������
		User user = new User();
		user.setNick(et_nick.getText().toString());
		user.setBirthday(tv_birthday.getText().toString());
		
		if (et_personalized_signature.getText().toString().equals("")) {
			user.setPersonalizedSignature("δ��д");
		}
		else {
			user.setPersonalizedSignature(et_personalized_signature.getText().toString());
		}
		
		user.setGameType(tv_game.getText().toString());
		user.setGameDifficulty(tv_game_difficulty.getText().toString());
		user.setLove(tv_love_status.getText().toString());
		
		if (tv_career.getText().toString().equals("��ѡ��������ҵ")) {
			user.setCareer("δ��д");
		}else {
			user.setCareer(tv_career.getText().toString());
		}
		
		if (et_company.getText().toString().equals("")) {
			user.setCompany("δ��д");
		}else {
			user.setCompany(et_company.getText().toString());
		}
		
		if (et_school.getText().toString().equals("")) {
			user.setSchool("δ��д");
		}else {
			user.setSchool(et_school.getText().toString());
		}
		
		if (tv_hometown.getText().toString().equals("��ѡ����ļ���")) {
			user.setHometown("δ��д");
		}else {
			user.setHometown(tv_hometown.getText().toString());
		}
		
		if (et_book.getText().toString().equals("")) {
			user.setBook("δ��д");
		}else {
			user.setBook(et_book.getText().toString());
		}
		
		if (et_movie.getText().toString().equals("")) {
			user.setMovie("δ��д");
		}else {
			user.setMovie(et_movie.getText().toString());
		}
		if (et_music.getText().toString().equals("")) {
			user.setMusic("δ��д");
		}else {
			user.setMusic(et_music.getText().toString());
		}
		if (et_interests.getText().toString().equals("")) {
			user.setInterests("δ��д");
		}else {
			user.setInterests(et_interests.getText().toString());
		}
		
		if (et_usually_appear.getText().toString().equals("")) {
			user.setUsuallyAppear("δ��д");
		}else {
			user.setUsuallyAppear(et_usually_appear.getText().toString());
		}
		
		updateDialog = new CustomProgressDialog(EditMyInfoActivity.this, "���ڸ�������...");
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.setCancelable(false);
		updateDialog.show();
		
		updateUserData(user, new UpdateListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				ShowToast("���³ɹ�");
				updateDialog.dismiss();
				// ���ص����Ͻ���

				setResult(RESULT_OK, getIntent()); // intentΪA�����Ĵ���Bundle��intent����ȻҲ�����Լ������µ�Bundle
				getIntent().putExtra("update", true);
				finish(); //�˴�һ��Ҫ����finish()����

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
		
		// ����
		case R.id.rl_edit_birthday_layout:
			seleteBirth();
			break;
			
		// ��Ϸ
		case R.id.rl_edit_game_layout:
			showGameChooseDialog();
			break;
			
		// ��Ϸ�Ѷ�
		case R.id.rl_edit_game_difficulty:
			showGameDiffiultySelectDialog();
			break;
						
		// ���״��
		case R.id.rl_edit_love_status_layout:
			showLoveChooseDialog();
			break;
						
		// ����
		case R.id.rl_edit_profile9:
			selectHometown();
			break;
			
		// ְҵ
		case R.id.rl_edit_profile6:
			showCareerSelectDialog();
			break;
		}
	}
	
	private void showCareerSelectDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.careerList, "ȷ��", "ȡ��", "ѡ����ҵ", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				careerType = CustomApplcation.careerList.get(selectItem);

				if (careerType.equals("��")) {
					tv_career.setText("��ѡ��������ҵ");
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
	 * ѡ�����
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
			// ѡ�����
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
	 * ѡ������
	 */
	private void seleteBirth(){
		
		final DateChooseDialog dateChooseDialog = new DateChooseDialog(EditMyInfoActivity.this, "ȷ��", "ȡ��", "ѡ������", false);
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
	
	// ѡ����Ϸ
	private void showGameChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
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
	 * ѡ����Ϸ�Ѷ�
	 * 
	 */
	
	private void showGameDiffiultySelectDialog() {
			
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.gameDifficultyList, "ȷ��", "ȡ��", "��Ϸ�Ѷ�", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					gameDifficulty = "��";
					break;
				case 1:
					gameDifficulty = "һ��";
					break;
				case 2:
					gameDifficulty = "����";
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
	 * ѡ�����״��
	 * author:Deep
	 */
	private void showLoveChooseDialog() {
		
		final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(EditMyInfoActivity.this,
				CustomApplcation.loveList, "ȷ��", "ȡ��", "���״��", true);
		
		singleChoiceDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int selectItem = singleChoiceDialog.getSelectItem();
				
				switch (selectItem) {
				case 0:
					tv_love_status.setText("����");
					break;
				case 1:
					tv_love_status.setText("����");
					break;
				case 2:
					tv_love_status.setText("ʧ��");
					break;
				case 3:
					tv_love_status.setText("����");
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
