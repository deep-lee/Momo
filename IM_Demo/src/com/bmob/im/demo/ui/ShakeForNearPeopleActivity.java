package com.bmob.im.demo.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GridImageAdapter;
import com.bmob.im.demo.util.ShakeListener;
import com.bmob.im.demo.util.ShakeListener.OnShakeListener;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ShakeForNearPeopleActivity extends BaseActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener{
	
	private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
    
    SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	
	ImageView iv_nears_sex_icon;
	
	public int nearsSex = 2;
	
	GridImageAdapter mAdapter;
	
	ImageView[] imageView = new ImageView[15];
	
	int random = 0;
	
	ImageView iv_select_sex_show;
	
	LinearLayout layout_all;
	
	Bitmap srcBitmap, bmp;
	int brightness = 100;
	
	Paint paint;
	Canvas canvas;
	
	ColorMatrix cMatrix;
	
	public ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	
	StringBuilder fileName = new StringBuilder("ic_welcome_photo_");
	
	YoYo.AnimationComposer shakeAnimation;
	
	private int step = 3;
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			brightness -= step;
			if (brightness >= 0) {
				handler.removeCallbacks(runnable);
				
				cMatrix = new ColorMatrix();  
		        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,  
		                0, 0, brightness,// 改变亮度   
		                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 }); 
		        
		        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));  
		        
		        canvas = new Canvas(bmp);  
		        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了   
		        canvas.drawBitmap(srcBitmap, 0, 0, paint);  
		        imageView[random].setImageBitmap(bmp); 

			}
			else {
				setNextImageView();
				fileName = null;
			}
			handler.postDelayed(this, 30);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake_for_near_people);
		
		sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		nearsSex = sharedPreferences.getInt("nearsSex", 2);
		
		shakeAnimation = new YoYo.AnimationComposer(new ShakeAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		mVibrator = (Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE);
		
		layout_all = (LinearLayout) findViewById(R.id.image_layout_all);
		
		imageView[0] = (ImageView) findViewById(R.id.imageview_show_1);
		imageView[1] = (ImageView) findViewById(R.id.imageview_show_2);
		imageView[2] = (ImageView) findViewById(R.id.imageview_show_3);
		imageView[3] = (ImageView) findViewById(R.id.imageview_show_4);
		imageView[4] = (ImageView) findViewById(R.id.imageview_show_5);
		imageView[5] = (ImageView) findViewById(R.id.imageview_show_6);
		imageView[6] = (ImageView) findViewById(R.id.imageview_show_7);
		imageView[7] = (ImageView) findViewById(R.id.imageview_show_8);
		imageView[8] = (ImageView) findViewById(R.id.imageview_show_9);
		imageView[9] = (ImageView) findViewById(R.id.imageview_show_10);
		imageView[10] = (ImageView) findViewById(R.id.imageview_show_11);
		imageView[11] = (ImageView) findViewById(R.id.imageview_show_12);
		imageView[12] = (ImageView) findViewById(R.id.imageview_show_13);
		imageView[13] = (ImageView) findViewById(R.id.imageview_show_14);
		imageView[14] = (ImageView) findViewById(R.id.imageview_show_15);
		
		random = (int)(Math.random()*(15));
		fileName.append(random + 1).append(".jpg");
		
		srcBitmap = getBitmapFromAsset(ShakeForNearPeopleActivity.this, fileName.toString());
		
		bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(),  
                Config.ARGB_8888);  
          
        cMatrix = new ColorMatrix();  
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,  
                0, 0, brightness,// 改变亮度   
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });  

        paint = new Paint();  
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));  
        
        canvas = new Canvas(bmp);  
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了   
        canvas.drawBitmap(srcBitmap, 0, 0, paint);  
        imageView[random].setImageBitmap(bmp); 
        
        handler.postDelayed(runnable, 1500);
		
		iv_nears_sex_icon = (ImageView) findViewById(R.id.iv_nears_sex_icon);
		
		switch (nearsSex) {
		// 女
		case 0:
			iv_nears_sex_icon.setVisibility(View.VISIBLE);
			iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_female);
			break;
		
		// 男
		case 1:
			iv_nears_sex_icon.setVisibility(View.VISIBLE);
			iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_male);
			break;
		
		// 男女
		case 2:
			iv_nears_sex_icon.setVisibility(View.INVISIBLE);
			break;
		}
		
		
		fragmentManager = getSupportFragmentManager();
        // initToolbar();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), getMenuObjects());
		
		iv_select_sex_show = (ImageView) findViewById(R.id.iv_set_nears_sex_show);
		
		iv_select_sex_show.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
			}
		});
		
		
		
		mShakeListener = new ShakeListener(ShakeForNearPeopleActivity.this);

        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
				closeShakeListeber();
				
				// ShowToast("现在是附近的人，不是景点漫游");
				
				startVibrato();
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
					mVibrator.cancel();
					// mShakeListener.start();
					Intent intent = new Intent();
					intent.setClass(ShakeForNearPeopleActivity.this, NearPeopleMapActivity.class);
					intent.putExtra("nearsSex", nearsSex);
					// ShowToast("SEX:" + nearsSex);
					startAnimActivity(intent);
					
					}
				},1500);
			}
		});
    }  
	
	private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<MenuObject>();

        MenuObject close = new MenuObject();
        close.setResource(CustomApplcation.sex? R.drawable.icn_close : R.drawable.icn_close_female);
        close.setBgResource(R.drawable.menu_object_bg);

        MenuObject send = new MenuObject("只看女生");
        send.setResource(CustomApplcation.sex? R.drawable.icon_info_female : R.drawable.icon_info_female_female);
        send.setBgResource(R.drawable.menu_object_bg);

        MenuObject like = new MenuObject("只看男生");
        like.setResource(CustomApplcation.sex? R.drawable.icon_info_male : R.drawable.icon_info_male_female);
        like.setBgResource(R.drawable.menu_object_bg);

        MenuObject addFr = new MenuObject("查看全部");
        addFr.setResource(CustomApplcation.sex? R.drawable.ic_nears_all_people : R.drawable.ic_nears_all_people_female);
        addFr.setBgResource(R.drawable.menu_object_bg);
  
        MenuObject addFav = new MenuObject("清除地理位置信息");
        addFav.setResource(CustomApplcation.sex? R.drawable.ic_nears_clean_position_info : R.drawable.ic_nears_clean_position_info_female);
        addFav.setBgResource(R.drawable.menu_object_bg);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        
        return menuObjects;
    }
	
	public void startVibrato(){	
		MediaPlayer player;
		player = MediaPlayer.create(ShakeForNearPeopleActivity.this, R.raw.awe);
		player.setLooping(false);
        player.start();
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); 
	}
	
	public void startAnim () {
		
//		for (int i = 0; i < imageView.length; i++) {
//			shakeAnimation.playOn(imageView[i]);
//		}
		   
		shakeAnimation.playOn(layout_all);
		
	}
	
	public void openShakeListener() {
		mShakeListener.start();
	}
	
	public void closeShakeListeber() {
		mShakeListener.stop();
	}
	
	private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream is=null;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }
	
	public void setNextImageView() {
		fileName = new StringBuilder("ic_welcome_photo_");
		
		brightness = 100;
		
		random = (int)(Math.random()*(15));
		fileName.append(random + 1).append(".jpg");
		
		srcBitmap = getBitmapFromAsset(ShakeForNearPeopleActivity.this, fileName.toString());
		
		bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(),  
                Config.ARGB_8888);  
          
        cMatrix = new ColorMatrix();  
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,  
                0, 0, brightness,// 改变亮度   
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });  

        paint = new Paint();  
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));  
        
        canvas = new Canvas(bmp);  
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了   
        canvas.drawBitmap(srcBitmap, 0, 0, paint);  
        imageView[random].setImageBitmap(bmp); 
        
        handler.post(runnable);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);
		setContentView(R.layout.view_null);
	}

	@Override
	public void back(View view) {
		// TODO Auto-generated method stub
		super.back(view);
		
		handler.removeCallbacks(runnable);
	}

	@Override
	public void onMenuItemLongClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMenuItemClick(View clickedView, int position) {
		// TODO Auto-generated method stub
		position--;
		// ShowToast("" + position);
		// TODO Auto-generated method stub
		if (position >= 0 && position <= 2) {
			if (nearsSex == position) {
				
			}else {
				editor.putInt("nearsSex", position);
				nearsSex = position;
				editor.commit();
				
				// 改变图标
//				nearByFragment.nearBySexChanged(position);
				
				switch (position) {
				// 女
				case 0:
					iv_nears_sex_icon.setVisibility(View.VISIBLE);
					iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_female);
					break;
				
				// 男
				case 1:
					iv_nears_sex_icon.setVisibility(View.VISIBLE);
					iv_nears_sex_icon.setImageResource(R.drawable.icon_nears_sex_male);
					break;
				
				// 男女
				case 2:
					iv_nears_sex_icon.setVisibility(View.INVISIBLE);
					break;

				default:
					break;
				}

			}
		}
		
		// 清除地理位置信息
		if (position == 3) {
			
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		openShakeListener();
	}
}
