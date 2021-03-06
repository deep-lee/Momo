package com.deep.ui.fragment.update;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.PhotoWallFallActivity;
import com.bmob.im.demo.ui.StateTimeLineActivity;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.CircularProgressView;
import com.bmob.im.demo.view.InfoScrollView;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.deep.ui.update.MainActivity2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

public class PersonInfoUpdateFragment extends FragmentBase implements OnClickListener, InfoScrollView.OnScrollListener{
	private Context mContext;
	
	private InfoScrollView sv;
	
	// 背景，头像，照片1，照片2，照片3，性别
	ImageView iv_head_bg, iv_avatar, iv_photo1, iv_photo2, iv_photo3, iv_sex;
		
		// 对应布局文件各个item
	TextView tv_age, tv_distance, tv_last_update_time, tv_nick, tv_account, tv_personalized_signature, tv_game, 
				 tv_game_difficulty, tv_love_status, tv_career, tv_company, tv_school, tv_hometown, tv_book, 
				 tv_movie, tv_music, tv_interests, tv_usually_appear, tv_register_time, tv_recent_play;
		
	// 照片墙
	RelativeLayout photoWallLayout;
		
	RelativeLayout layout_all;
	
	User user;
	
	RelativeLayout rl_personalized_signature, rl_career, rl_company, rl_school, rl_hometown, rl_book, rl_movie, rl_music, 
						rl_recent_play, rl_interests,rl_usually_appear, rl_states;
		
	CircularProgressView []progressView;
		
	public static Boolean update = false;
	
	// 我的头像地址
	File dir;
		
	Uri updatedAtatar = null;
		
	private String mCurrentPhotoPath;
	
	LinearLayout layout_choose, layout_photo, layout_cancle;
		
	List<MYTask> photoTask;
		
	Thread []updateThread;
		
	Handler updateAvatarHandler = new Handler(){
		public void handleMessage(Message msg) {   
	           switch (msg.what) {   
	            
	            case 0:
	    			updateAvatarprogressDialog.dismiss();
	    			updateAvatarprogressDialog = null;
	    			refreshUserAvatar();
	            	break;
	            
	           }   
	           super.handleMessage(msg);   
	      }
	};
	
	public PersonInfoUpdateFragment() {
		super();
	}
	
	public PersonInfoUpdateFragment(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_person_info_update, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		
		MainActivity2.setSexSelecteStatus(false);
		MainActivity2.setSexShowStatus(false);
		
		MainActivity2.showEditMyInfo();
		
		updateThread = new Thread[3];
		photoTask = new ArrayList<MYTask>();
		
		layout_all = (RelativeLayout) findViewById(R.id.info_layout_all);
		
		progressView = new CircularProgressView[3];
		
		progressView[0] = (CircularProgressView) findViewById(R.id.progress_view1);
		progressView[1] = (CircularProgressView) findViewById(R.id.progress_view2);
		progressView[2] = (CircularProgressView) findViewById(R.id.progress_view3);
		
		// Test loading animation
        startAnimationThreadStuff(0);
		
		photoWallLayout = (RelativeLayout) findViewById(R.id.rl_profile2);
		
		photoWallLayout.setOnClickListener(this);
		
		sv = (InfoScrollView)findViewById(R.id.sv_profile);
		iv_head_bg = (ImageView)findViewById(R.id.iv_profile_bg);
		sv.setOnScrollListener(this);
		sv.getView();
		
		iv_avatar = (ImageView) findViewById(R.id.iv_profile_avartar);
		
		iv_photo1 = (ImageView) findViewById(R.id.info_photo_1);
		iv_photo2 = (ImageView) findViewById(R.id.info_photo_2);
		iv_photo3 = (ImageView) findViewById(R.id.info_photo_3);
		iv_photo1.setVisibility(View.INVISIBLE);
		iv_photo2.setVisibility(View.INVISIBLE);
		iv_photo3.setVisibility(View.INVISIBLE);
		iv_sex = (ImageView) findViewById(R.id.info_sex_iv);
		
		dir = new File(BmobConstants.MyAvatarDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		// 如果是我的资料，点击头像可以进行更换头像
		iv_avatar.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAvatarPop();
			}
		});
		
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
		
		tv_recent_play = (TextView) findViewById(R.id.info_recent_play_details);
		
		rl_personalized_signature = (RelativeLayout) findViewById(R.id.rl_profile5);
		rl_career = (RelativeLayout) findViewById(R.id.rl_profile6);
		rl_company = (RelativeLayout) findViewById(R.id.rl_profile7);
		rl_school = (RelativeLayout) findViewById(R.id.rl_profile8);
		rl_hometown = (RelativeLayout) findViewById(R.id.rl_profile9);
		
		rl_book = (RelativeLayout) findViewById(R.id.rl_profile10);
		rl_movie = (RelativeLayout) findViewById(R.id.rl_profile11);
		rl_music = (RelativeLayout) findViewById(R.id.rl_profile12);
		rl_recent_play = (RelativeLayout) findViewById(R.id.info_hobbi_recent_play_layout);
		rl_interests = (RelativeLayout) findViewById(R.id.rl_profile13);
		
		rl_usually_appear = (RelativeLayout) findViewById(R.id.rl_profile14);
		
		rl_states = (RelativeLayout) findViewById(R.id.rl_states_layout);
		
		rl_states.setOnClickListener(this);

		initOtherData(userManager.getCurrentUser().getUsername());

	}
	
	private void startAnimationThreadStuff(long delay)
    {
        if(updateThread[0] != null && updateThread[0].isAlive())
            updateThread[0].interrupt();
        if(updateThread[1] != null && updateThread[1].isAlive())
            updateThread[1].interrupt();
        if(updateThread[2] != null && updateThread[2].isAlive())
            updateThread[2].interrupt();
        final Handler handler0 = new Handler();
        final Handler handler1 = new Handler();
        final Handler handler2 = new Handler();
        
        handler0.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start animation after a delay so there's no missed frames while the app loads up
                progressView[0].setProgress(0f);
                progressView[0].startAnimation(); // Alias for resetAnimation, it's all the same
                // Run thread to update progress every half second until full
                updateThread[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressView[0].getProgress() < progressView[0].getMaxProgress() && !Thread.interrupted()) {
                            // Must set progress in UI thread
                            handler0.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressView[0].setProgress(progressView[0].getProgress() + 10);
                                }
                            });
                            SystemClock.sleep(250);
                        }
                    }
                });
                updateThread[0].start();
            }
        }, delay);
        
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start animation after a delay so there's no missed frames while the app loads up
                progressView[1].setProgress(0f);
                progressView[1].startAnimation(); // Alias for resetAnimation, it's all the same
                // Run thread to update progress every half second until full
                updateThread[1] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressView[1].getProgress() < progressView[1].getMaxProgress() && !Thread.interrupted()) {
                            // Must set progress in UI thread
                            handler0.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressView[1].setProgress(progressView[1].getProgress() + 10);
                                }
                            });
                            SystemClock.sleep(250);
                        }
                    }
                });
                updateThread[1].start();
            }
        }, delay);
        
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start animation after a delay so there's no missed frames while the app loads up
                progressView[2].setProgress(0f);
                progressView[2].startAnimation(); // Alias for resetAnimation, it's all the same
                // Run thread to update progress every half second until full
                updateThread[2] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressView[2].getProgress() < progressView[2].getMaxProgress() && !Thread.interrupted()) {
                            // Must set progress in UI thread
                            handler0.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressView[2].setProgress(progressView[2].getProgress() + 10);
                                }
                            });
                            SystemClock.sleep(250);
                        }
                    }
                });
                updateThread[2].start();
            }
        }, delay);
        
        
    }
	
	
	
	// 初始化我的资料
	private void initMeData() {
		User user = userManager.getCurrentUser(User.class);
		BmobLog.i("hight = "+user.getHight()+",sex= "+user.getSex());
		
		initOtherData(user.getUsername());
	}
	
	// 初始化其他资料
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
					updateUser(user);
				} else {
					ShowLog("onSuccess 查无此人");
				}
			}
		});
	}
		
	PopupWindow avatorPop;

	public String filePath = "";
		
	@SuppressWarnings("deprecation")
	private void showAvatarPop() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_showavator,
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
					
				getImageFromCamera();
			}
		});
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShowLog("点击相册");
				pickImage();
					
			}
		});
		
		int mScreenWidth;
		
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;

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
	
	/*
	 * 
	 */
	protected void getImageFromCamera() {
		// 原图
		File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
				.format(new Date()));
		filePath = file.getAbsolutePath();// 获取相片的保存路径
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
	
	String path = "";
	
	CustomProgressDialog updateAvatarprogressDialog;
	private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            System.out.println(" handleCrop: Crop.getOutput(result) "+Crop.getOutput(result));
            
            if (avatorPop != null) {
				avatorPop.dismiss();
			}
            
            boolean isNetConnected = CommonUtils.isNetworkAvailable(getActivity());
            if (!isNetConnected) {
            	ShowToast(R.string.network_tips);
    			return;
			}else {
				// 上传头像
	            updateAvatarprogressDialog = new CustomProgressDialog(mContext, "正在更新头像...");
	            updateAvatarprogressDialog.setCanceledOnTouchOutside(false);
	            updateAvatarprogressDialog.show();
	            
	         	// 更新用户的头像
				uploadAvatar();
				
				updatedAtatar = Crop.getOutput(result);
			}
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(mContext, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
	
	private void refreshUserAvatar() {
        if (updatedAtatar != null) {
        	iv_avatar.setImageURI(updatedAtatar);
		}
        
	}
	
	/*
	 *  上传头像
	 */
	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(mContext, new UploadFileListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// 获取文件上传之后文件在服务器断对应的地址
				String url = bmobFile.getFileUrl(mContext);
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
				updateAvatarHandler.sendMessage(message);
				
			}

			@Override
			public void onFailure(int code, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像更新失败：" + msg);
			}
		});
	}
	
	/*
	 * 更新用户信息
	 */
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(mContext, listener);
	}


    private void beginCrop(Uri source) {
        String fileName = new SimpleDateFormat("yyMMddHHmmss").format(new Date())  + ".png";
        File cropFile = new File( dir, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        
        // 剪裁后的文件路径
        path = cropFile.getAbsolutePath();
        new Crop( source).output( outputUri).setCropType(true).start(getActivity());
    }
    
    private void updateUser(User user) {
		// 更改
		// 更新头像
		refreshAvatar(user.getAvatar());
		
		// 更新照片墙
		// 如果是我的资料的话，前面MainActivity已经获取了照片信息
		if (CustomApplcation.myWallPhoto.size() > 0) {
			downLoadPhoto();
		}
		else {
			iv_photo1.setVisibility(View.GONE);
			iv_photo2.setVisibility(View.GONE);
			iv_photo3.setVisibility(View.GONE);
		}

		// 设置性别
		// 男
		if (user.getSex()) {
			iv_sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_nears_sex_male));
		}
		else {
			iv_sex.setImageDrawable(getResources().getDrawable(R.drawable.icon_nears_sex_female));
		}
		
		Calendar calendar = Calendar.getInstance();

		String birthday = user.getBirthday();
		String []array = birthday.split("-");
		// 设置年龄
		tv_age.setText(calendar.get(Calendar.YEAR) - Integer.parseInt(array[0]) + "");
		
		tv_distance.setText("0.00m");
		
		// 最新登陆时间
		tv_last_update_time.setText(user.getUpdatedAt());
		
		// 昵称
		tv_nick.setText(user.getNick());
		
		// 用户名
		tv_account.setText(user.getUsername());
		
		// 个性签名
		if (user.getPersonalizedSignature() == null || user.getPersonalizedSignature().equals("未填写")) {
			rl_personalized_signature.setVisibility(View.GONE);
		}
		else {
			tv_personalized_signature.setText(user.getPersonalizedSignature());
		}
		
		// 游戏
		tv_game.setText(user.getGameType());
		
		// 游戏难度
		tv_game_difficulty.setText(user.getGameDifficulty());
		
		// 情感状态
		tv_love_status.setText(user.getLove());
		
		// 职业
		if (user.getCareer() == null || user.getCareer().equals("未填写")) {
			rl_career.setVisibility(View.GONE);
		}else {
			tv_career.setText(user.getCareer());
		}
		
		
		// 公司
		if (user.getCompany() == null || user.getCompany().equals("未填写")) {
			rl_company.setVisibility(View.GONE);
		}
		else {
			tv_company.setText(user.getCompany());
		}
		
		// 学校
		if (user.getSchool() == null || user.getSchool().equals("未填写")) {
			rl_school.setVisibility(View.GONE);
		}
		else {
			tv_school.setText(user.getSchool());
		}
		
		// 家乡
		if (user.getHometown() == null || user.getHometown().equals("未填写")) {
			rl_hometown.setVisibility(View.GONE);
		}
		else {
			tv_hometown.setText(user.getHometown());
		}
		
		// 书籍
		if (user.getBook() == null || user.getBook().equals("未填写")) {
			rl_book.setVisibility(View.GONE);
		}
		else {
			tv_book.setText(user.getBook());
		}
		
		// 电影
		if (user.getMovie() == null || user.getMovie().equals("未填写")) {
			rl_movie.setVisibility(View.GONE);
		}
		else {
			tv_movie.setText(user.getMovie());
		}
		
		// 音乐
		if (user.getMusic() == null || user.getMusic().equals("未填写")) {
			rl_music.setVisibility(View.GONE);
		}
		else {
			tv_music.setText(user.getMusic());
		}
		
		// 最近游戏
		if (user.getRecentPlayGame() == 0) {
			rl_recent_play.setVisibility(View.GONE);
		}
		else {
			tv_recent_play.setText(CustomApplcation.gameList.get(user.getRecentPlayGame() - 1).getGame_name());
		}
		
		// 兴趣爱好
		if (user.getInterests() == null || user.getInterests().equals("未填写")) {
			rl_interests.setVisibility(View.GONE);
		}
		else {
			tv_interests.setText(user.getInterests());
		}
		
		// 常出没地
		if (user.getUsuallyAppear() == null || user.getUsuallyAppear().equals("未填写")) {
			rl_usually_appear.setVisibility(View.GONE);
		}
		else {
			tv_usually_appear.setText(user.getUsuallyAppear());
		}
		
		// 注册时间
		tv_register_time.setText(user.getCreatedAt());
	}
    
    /*
	 * 从编辑页面保存之后，返回
	 */
	private void updateAfterEditUserInfo()
	{
		Calendar calendar = Calendar.getInstance();

		String birthday = user.getBirthday();
		String []array = birthday.split("-");
		// 设置年龄
		tv_age.setText(calendar.get(Calendar.YEAR) - Integer.parseInt(array[0]) + "");
		
		// 昵称
		tv_nick.setText(user.getNick());
				
		// 个性签名
		if (user.getPersonalizedSignature() == null || user.getPersonalizedSignature().equals("未填写")) {
			rl_personalized_signature.setVisibility(View.GONE);
		}
		else {
			rl_personalized_signature.setVisibility(View.VISIBLE);
			tv_personalized_signature.setText(user.getPersonalizedSignature());
		}
				
		// 游戏
		tv_game.setText(user.getGameType());
				
		// 游戏难度
		tv_game_difficulty.setText(user.getGameDifficulty());
				
		// 情感状态
		tv_love_status.setText(user.getLove());
				
		// 职业
		
		if (user.getCareer() == null || user.getCareer().equals("未填写")) {
			rl_career.setVisibility(View.GONE);
		}else {
			rl_career.setVisibility(View.VISIBLE);
			tv_career.setText(user.getCareer());
		}
				
				
		// 公司
		if (user.getCompany() == null || user.getCompany().equals("未填写")) {
			rl_company.setVisibility(View.GONE);
		}
		else {
			rl_company.setVisibility(View.VISIBLE);
			tv_company.setText(user.getCompany());
		}
				
		// 学校
		if (user.getSchool() == null || user.getSchool().equals("未填写")) {
			rl_school.setVisibility(View.GONE);
		}
		else {
			rl_school.setVisibility(View.VISIBLE);
			tv_school.setText(user.getSchool());
		}
				
		// 家乡
		if (user.getHometown() == null || user.getHometown().equals("未填写")) {
			rl_hometown.setVisibility(View.GONE);
		}
		else {
			rl_hometown.setVisibility(View.VISIBLE);
			tv_hometown.setText(user.getHometown());
		}
				
		// 书籍
		if (user.getBook() == null || user.getBook().equals("未填写")) {
			rl_book.setVisibility(View.GONE);
		}
		else {
			rl_book.setVisibility(View.VISIBLE);
			tv_book.setText(user.getBook());
		}
				
		// 电影
		if (user.getMovie() == null || user.getMovie().equals("未填写")) {
			rl_movie.setVisibility(View.GONE);
		}
		else {
			rl_movie.setVisibility(View.VISIBLE);
			tv_movie.setText(user.getMovie());
		}
				
		// 音乐
		if (user.getMusic() == null || user.getMusic().equals("未填写")) {
			rl_music.setVisibility(View.GONE);
		}
		else {
			rl_music.setVisibility(View.VISIBLE);
			tv_music.setText(user.getMusic());
		}
				
		// 兴趣爱好
		if (user.getInterests() == null || user.getInterests().equals("未填写")) {
			rl_interests.setVisibility(View.GONE);
		}
		else {
			rl_interests.setVisibility(View.VISIBLE);
			tv_interests.setText(user.getInterests());
		}
				
		// 常出没地
		if (user.getUsuallyAppear() == null || user.getUsuallyAppear().equals("未填写")) {
			rl_usually_appear.setVisibility(View.GONE);
		}
		else {
			rl_usually_appear.setVisibility(View.VISIBLE);
			tv_usually_appear.setText(user.getUsuallyAppear());
		}
	}
	
	/*
	 * 下载照片
	 * author：deeplee
	 */
	private void downLoadPhoto() {
		
		if (CustomApplcation.myWallPhoto.size() > 0) {
			if (CustomApplcation.myWallPhoto.size() >= 1 && !CustomApplcation.myWallPhoto.get(0).equals("") && CustomApplcation.myWallPhoto.get(0) != null) {
				MYTask task1 = new MYTask(iv_photo1);
				task1.execute(CustomApplcation.myWallPhoto.get(0));
				photoTask.add(task1);
			}
			else {
				progressView[0].setVisibility(View.INVISIBLE);
				iv_photo1.setVisibility(View.INVISIBLE);
			}
			if (CustomApplcation.myWallPhoto.size() >= 2 && !CustomApplcation.myWallPhoto.get(1).equals("") && CustomApplcation.myWallPhoto.get(1) != null) {
				MYTask task2 = new MYTask(iv_photo2);
				task2.execute(CustomApplcation.myWallPhoto.get(1));
				photoTask.add(task2);
			}
			else {
				progressView[1].setVisibility(View.INVISIBLE);
				iv_photo2.setVisibility(View.INVISIBLE);
			}
			
			if (CustomApplcation.myWallPhoto.size() >= 3 && !CustomApplcation.myWallPhoto.get(2).equals("") && CustomApplcation.myWallPhoto.get(2) != null) {
				MYTask task3 = new MYTask(iv_photo3);
				task3.execute(CustomApplcation.myWallPhoto.get(2));
				photoTask.add(task3);
			}
			else {
				progressView[2].setVisibility(View.INVISIBLE);
				iv_photo3.setVisibility(View.INVISIBLE);
			}
		}
		else {
			iv_photo1.setVisibility(View.INVISIBLE);
			iv_photo2.setVisibility(View.INVISIBLE);
			iv_photo3.setVisibility(View.INVISIBLE);
			
			progressView[1].setVisibility(View.INVISIBLE);
			progressView[2].setVisibility(View.INVISIBLE);
			progressView[0].setVisibility(View.INVISIBLE);
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
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			
			// 否则显示默认的头像
			iv_avatar.setImageResource(R.drawable.default_head);
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 如果是个人资料的话
		initMeData();
		downLoadPhoto();
		
		if (update) {
			user = userManager.getCurrentUser(User.class);
    		updateAfterEditUserInfo();
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
		switch (v.getId()) {
		// 照片墙
		case R.id.rl_profile2:
			Intent intent1 = new Intent();
			intent1.setClass(mContext, PhotoWallFallActivity.class);
			intent1.putExtra("from", "me");
			startActivity(intent1);
			break;
			
		// 状态时间线
		case R.id.rl_states_layout:
			Intent intent4 = new Intent();
			intent4.setClass(mContext, StateTimeLineActivity.class);
			intent4.putExtra("username", user != null? user.getUsername() : null);
			intent4.putExtra("flag", true);
			mContext.startActivity(intent4);
			break;
			
		
		default:
			break;
		}
	}

	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 距离：单位为米
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
     * 使用异步任务的规则： 1、申明的类继承AsyncTask 标注三个参数的类型
     * 2、第一个参数表示要执行的任务，通常是网络的路径；第二个参数表示进度的刻度，第三个参数表示任务执行的返回结果
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
         * 表示任务执行之前的操作
         */
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        /**
         * 主要是完成耗时的操作
         */
        @Override
        protected Bitmap doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            // 使用网络连接类HttpClient类王城对网络数据的提取
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
         * 主要是更新UI的操作
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            
            imageView.setVisibility(View.VISIBLE);
            
            switch (imageView.getId()) {
			case R.id.info_photo_1:
				progressView[0].setVisibility(View.INVISIBLE);
				break;
				
			case R.id.info_photo_2:
				progressView[1].setVisibility(View.INVISIBLE);
				break;
			case R.id.info_photo_3:
				progressView[2].setVisibility(View.INVISIBLE);
				break;
			}
        }

    }

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		
		if (hidden) {
			MainActivity2.hideEditMyInfo();
		}else {
			MainActivity2.showEditMyInfo();
		}
	}
}

