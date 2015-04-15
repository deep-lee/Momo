package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;

import C.From;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.bmob.im.demo.R.id;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.InfoScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SetMyInfoActivity2 extends BaseActivity implements InfoScrollView.OnScrollListener, OnClickListener{
	
	private InfoScrollView sv;
	
	// ������ͷ����Ƭ1����Ƭ2����Ƭ3���Ա�
	ImageView iv_head_bg, iv_avatar, iv_photo1, iv_photo2, iv_photo3, iv_sex;
	
	// ��Ӧ�����ļ�����item
	TextView tv_age, tv_distance, tv_last_update_time, tv_nick, tv_account, tv_personalized_signature, tv_game, 
			 tv_game_difficulty, tv_love_status, tv_career, tv_company, tv_school, tv_hometown, tv_book, 
			 tv_movie, tv_music, tv_interests, tv_usually_appear, tv_register_time;
	
	// �༭����Ӻ��ѣ�����Ự����ӵ�������
	Button btn_edit, btn_add, btn_chat, btn_black;
	
	// ��������ʾ
	RelativeLayout black_list_tips;
	

	// from�ж����Լ������ϻ��Ǳ��˵����Ͻ���
	String from = "";
	// ����Ǳ��˵����Ͻ��棬��ȡ���˵�username
	String username = "";
	User user;
	
	List<MYTask> photoTask;
	
	public ArrayList<String> otherWallPhoto;
	
	
	Handler otherPhotoHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {   
            switch (msg.what) {
            
            case 1:
            	downLoadOtherPhoto();
            	break;
            }   
            super.handleMessage(msg);   
       }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_my_info_2);
		
		// me �ҵ�����
		// add ��������
		// other �ҵĺ���
		from = getIntent().getStringExtra("from"); //me add other
		username = getIntent().getStringExtra("username");
		
		inintView();
	}

	private void inintView() {
		
		photoTask = new ArrayList<MYTask>();
		
		otherWallPhoto = new ArrayList<String>();
		
		sv = (InfoScrollView)findViewById(R.id.sv_profile);
		iv_head_bg = (ImageView)findViewById(R.id.iv_profile_bg);
		sv.setOnScrollListener(this);
		sv.getView();
		
		iv_avatar = (ImageView) findViewById(R.id.iv_profile_avartar);
		iv_photo1 = (ImageView) findViewById(R.id.info_photo_1);
		iv_photo2 = (ImageView) findViewById(R.id.info_photo_2);
		iv_photo3 = (ImageView) findViewById(R.id.info_photo_3);
		iv_sex = (ImageView) findViewById(R.id.info_sex_iv);
		
		tv_age = (TextView) findViewById(R.id.info_age_tv);
		tv_distance = (TextView) findViewById(R.id.info_tv_last_location_distance);
		tv_last_update_time = (TextView) findViewById(R.id.info_tv_last_update);
		tv_nick = (TextView) findViewById(R.id.info_nick_details);
		tv_account = (TextView) findViewById(R.id.info_account_details);
		tv_personalized_signature = (TextView) findViewById(R.id.info_personalized_signature_details);
		tv_game = (TextView) findViewById(R.id.info_game_details);
		tv_game_difficulty = (TextView) findViewById(R.id.info_game_difficulty_details);
		tv_love_status = (TextView) findViewById(R.id.info_love_status_details);
		tv_career = (TextView) findViewById(R.id.info_career_details);
		tv_company = (TextView) findViewById(R.id.info_company_details);
		tv_school = (TextView) findViewById(R.id.info_school_details);
		tv_hometown = (TextView) findViewById(R.id.info_hometown_details);
		
		tv_book = (TextView) findViewById(R.id.info_book_details);
		tv_movie = (TextView) findViewById(R.id.info_movie_details);
		tv_music = (TextView) findViewById(R.id.info_music_details);
		tv_interests = (TextView) findViewById(R.id.info_interests_details);
		
		tv_usually_appear = (TextView) findViewById(R.id.info_usually_appear_details);
		tv_register_time = (TextView) findViewById(R.id.info_register_time_details);
		
		btn_edit = (Button) findViewById(R.id.btn_edit_info);
		btn_add = (Button) findViewById(R.id.info_btn_add_friend);
		btn_chat = (Button) findViewById(R.id.info_btn_chat);
		btn_black = (Button) findViewById(R.id.info_btn_black);
		black_list_tips = (RelativeLayout) findViewById(R.id.info_layout_black_tips);
		
		// ������ҵ��Լ�������
		if (from.equals("me")) {
			btn_add.setVisibility(View.GONE);
			btn_chat.setVisibility(View.GONE);
			btn_black.setVisibility(View.GONE);
			btn_edit.setVisibility(View.VISIBLE);
		}
		else{
			//���ܶԷ��ǲ�����ĺ��ѣ������Է�����Ϣ--BmobIM_V1.1.2�޸�
			btn_chat.setEnabled(true);
			btn_chat.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			btn_edit.setVisibility(View.GONE);
			
			// �Ӹ��������б���Ӻ���--��Ϊ��ȡ�������˵ķ����������Ƿ���ʾ���ѵ�����������������Ҫ�ж�������û��Ƿ����Լ��ĺ���
			
			if (from.equals("add")) {
				if (mApplication.getContactList().containsKey(username)) {// �Ǻ���
					btn_chat.setVisibility(View.VISIBLE);
					btn_chat.setEnabled(true);
					btn_chat.setOnClickListener(this);
					btn_black.setVisibility(View.VISIBLE);
					btn_black.setOnClickListener(this);
				} else { // ���Ǻ��ѣ��Ϳ�����Ӻ���
//					btn_chat.setVisibility(View.GONE);
					btn_black.setVisibility(View.GONE);
					btn_add.setEnabled(true);
					btn_add.setVisibility(View.VISIBLE);
					btn_add.setOnClickListener(this);
				}
			}
			// �ҵĺ���
			else {
				btn_black.setVisibility(View.VISIBLE);
				btn_black.setOnClickListener(this);
			}
		}
		
		initOtherData(username);
	}
	
	// ��ʼ���ҵ�����
	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("hight = "+user.getHight()+",sex= "+user.getSex());
		
		initOtherData(user.getUsername());
	}
	
	 // ��ʼ����������
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
					btn_black.setEnabled(true);
					btn_add.setEnabled(true);
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
		
		// ������Ƭǽ
		// ������ҵ����ϵĻ���ǰ��MainActivity�Ѿ���ȡ����Ƭ��Ϣ
		if (from.equals("me")) {
			if (CustomApplcation.myWallPhoto.size() > 0) {
				downLoadPhoto();
			}
			else {
				iv_photo1.setVisibility(View.GONE);
				iv_photo2.setVisibility(View.GONE);
				iv_photo3.setVisibility(View.GONE);
			}
		}
		// ������ҵĻ��л��߸�������
		else if (from.equals("other") || from.equals("add")) {
			initOtherWallPhoto();
		}
		
		
		
		// �����Ա�
		// ��
		if (user.getSex()) {
			iv_sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_info_male));
		}
		else {
			iv_sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_info_female));
		}
		
		Calendar calendar = Calendar.getInstance();
		int age;
		String birthday = user.getBirthday();
		String []array = birthday.split("-");
		// ��������
		tv_age.setText(calendar.get(Calendar.YEAR) - Integer.parseInt(array[0]) + "");
		
		// ���þ���
		if (from.equals("me")) {
			tv_distance.setText("0.00m");
		}else {
			
			// ��ȡ��ϵ�˵ĵ���λ����Ϣ
			BmobGeoPoint location = user.getLocation();
			String currentLat = CustomApplcation.getInstance().getLatitude();
			String currentLong = CustomApplcation.getInstance().getLongtitude();
			if(location!=null && !currentLat.equals("") && !currentLong.equals("")){
				
				// ���븽������֮��ľ���
				double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat),Double.parseDouble(currentLong),user.getLocation().getLatitude(), 
						user.getLocation().getLongitude());
				tv_distance.setText(String.valueOf(distance)+"��");
			}else{
				tv_distance.setText("δ֪");
			}
		}
		
		// ���µ�½ʱ��
		tv_last_update_time.setText(user.getUpdatedAt());
		
		
		// �ǳ�
		tv_nick.setText(user.getNick());
		
		// �û���
		tv_account.setText(user.getUsername());
		
		// ����ǩ��
		tv_personalized_signature.setText(user.getPersonalizedSignature());
		
		// ��Ϸ
		tv_game.setText(user.getGameType());
		
		// ��Ϸ�Ѷ�
		tv_game_difficulty.setText(user.getGameDifficulty());
		
		// ���״̬
		tv_love_status.setText(user.getLove());
		
		// ְҵ
		tv_career.setText(user.getCareer());
		
		// ��˾
		tv_company.setText(user.getCompany());
		
		// ѧУ
		tv_school.setText(user.getSchool());
		
		// ����
		tv_hometown.setText(user.getHometown());
		
		// �鼮
		tv_book.setText(user.getBook());
		
		// ��Ӱ
		tv_movie.setText(user.getMovie());
		
		// ����
		tv_music.setText(user.getMusic());
		
		// ��Ȥ����
		tv_interests.setText(user.getInterests());
		
		// ����û��
		tv_usually_appear.setText(user.getUsuallyAppear());
		
		// ע��ʱ��
		tv_register_time.setText(user.getCreatedAt());
		
		
//		tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");
//		tv_set_birthday.setText(user.getBirthday());
//		
//		
//		tv_set_game.setText(user.getGameType());
//		
//		tv_set_game_difficulty.setText(user.getGameDifficulty());
		
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
				btn_black.setVisibility(View.GONE);
				black_list_tips.setVisibility(View.VISIBLE);
			} else {
				btn_black.setVisibility(View.VISIBLE);
				black_list_tips.setVisibility(View.GONE);
			}
		}
	}
	
	private void initOtherWallPhoto() {
		
		Thread newThread; // ����һ�����߳�
		newThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	// ����д�����߳���Ҫ���Ĺ���
		    	String allPhoto = user.getPhotoWallFile();
		    	if ((allPhoto != null) && (!allPhoto.equals(""))) {
		    		String otherPhotoOrigin[] = allPhoto.split(";");
		    		
		    		
		    		for (int i = 0; i < otherPhotoOrigin.length; i++) {
						otherWallPhoto.add(otherPhotoOrigin[i]);
						// ShowToast(otherPhotoOrigin[i]);
					}
		    		
				}
		    	
		    	Message message = new Message();
		    	message.what = 1;
		    	otherPhotoHandler.sendMessage(message);
		    }
		});
		newThread.start(); //�����߳�
		    
	}
	
	/*
	 * ���طǱ�����Ƭ
	 * author��deeplee
	 */
	private void downLoadOtherPhoto() {
		if (otherWallPhoto.size() > 0) {
			if (otherWallPhoto.size() >= 1 && !otherWallPhoto.get(0).equals("") && otherWallPhoto.get(0) != null) {
				MYTask task1 = new MYTask(iv_photo1);
				task1.execute(otherWallPhoto.get(0));
				photoTask.add(task1);
			}
			else {
				iv_photo1.setVisibility(View.GONE);
			}
			if (otherWallPhoto.size() >= 2 && !otherWallPhoto.get(1).equals("") && otherWallPhoto.get(1) != null) {
				MYTask task2 = new MYTask(iv_photo2);
				task2.execute(otherWallPhoto.get(1));
				photoTask.add(task2);
			}
			else {
				iv_photo2.setVisibility(View.GONE);
			}
			
			if (otherWallPhoto.size() >= 3 && !otherWallPhoto.get(2).equals("") && otherWallPhoto.get(2) != null) {
				MYTask task3 = new MYTask(iv_photo3);
				task3.execute(otherWallPhoto.get(2));
				photoTask.add(task3);
			}
			else {
				iv_photo3.setVisibility(View.GONE);
			}
		}
		else {
			iv_photo1.setVisibility(View.GONE);
			iv_photo2.setVisibility(View.GONE);
			iv_photo3.setVisibility(View.GONE);
		}
	}
	
	/*
	 * ������Ƭ
	 * author��deeplee
	 */
	private void downLoadPhoto() {
		
		if (CustomApplcation.myWallPhoto.size() >= 1 && !CustomApplcation.myWallPhoto.get(0).equals("") && CustomApplcation.myWallPhoto.get(0) != null) {
			MYTask task1 = new MYTask(iv_photo1);
			task1.execute(CustomApplcation.myWallPhoto.get(0));
			photoTask.add(task1);
		}
		else {
			iv_photo1.setVisibility(View.GONE);
		}
		if (CustomApplcation.myWallPhoto.size() >= 2 && !CustomApplcation.myWallPhoto.get(1).equals("") && CustomApplcation.myWallPhoto.get(1) != null) {
			MYTask task2 = new MYTask(iv_photo2);
			task2.execute(CustomApplcation.myWallPhoto.get(1));
			photoTask.add(task2);
		}
		else {
			iv_photo2.setVisibility(View.GONE);
		}
		
		if (CustomApplcation.myWallPhoto.size() >= 3 && !CustomApplcation.myWallPhoto.get(2).equals("") && CustomApplcation.myWallPhoto.get(2) != null) {
			MYTask task3 = new MYTask(iv_photo3);
			task3.execute(CustomApplcation.myWallPhoto.get(2));
			photoTask.add(task3);
		}
		else {
			iv_photo3.setVisibility(View.GONE);
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
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			
			// ������ʾĬ�ϵ�ͷ��
			iv_avatar.setImageResource(R.drawable.default_head);
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
	public void onBottom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTop() {
		setHeadBgMargin(0);
		
	}

	@Override
	public void onScroll(int height) {
		setHeadBgMargin(0-(int)(height*speed));
		
	}

	@Override
	public void onScrollStop() {
		// TODO Auto-generated method stub
		
	}
	int headViewHeight = 0;
	RelativeLayout.LayoutParams lp;
	float speed = 0.5f;
	private void setHeadBgMargin(int top){
		
		if(headViewHeight == 0)
			headViewHeight = getResources().getDimensionPixelSize(R.dimen.image_profile_headbg_height);
		
		if(lp == null)
			lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,headViewHeight);
		lp.setMargins(0, top, 0, 0);
		iv_head_bg.setLayoutParams(lp);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * ��������侭γ�����꣨doubleֵ���������������룬
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return ���룺��λΪ��
	 */
	public static double DistanceOfTwoPoints(double lat1, double lng1,double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
	
	/**
     * ʹ���첽����Ĺ��� 1����������̳�AsyncTask ��ע��������������
     * 2����һ��������ʾҪִ�е�����ͨ���������·�����ڶ���������ʾ���ȵĿ̶ȣ�������������ʾ����ִ�еķ��ؽ��
     * 
     * @author liende
     * 
     */
    public class MYTask extends AsyncTask<String, Void, Bitmap> {
    	
    	ImageView imageView;
    	
        public MYTask(ImageView imageView) {
			super();
			this.imageView = imageView;
		}

		/**
         * ��ʾ����ִ��֮ǰ�Ĳ���
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        /**
         * ��Ҫ����ɺ�ʱ�Ĳ���
         */
        @Override
        protected Bitmap doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            // ʹ������������HttpClient�����Ƕ��������ݵ���ȡ
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(arg0[0]);
            Bitmap bitmap = null;
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    byte[] data = EntityUtils.toByteArray(httpEntity);
                    bitmap = BitmapFactory
                            .decodeByteArray(data, 0, data.length);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return bitmap;
        }

        /**
         * ��Ҫ�Ǹ���UI�Ĳ���
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
