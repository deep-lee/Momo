package com.deep.ui.fragment.update;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GridImageAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.NearPeopleMapActivity;
import com.bmob.im.demo.util.ShakeListener;
import com.bmob.im.demo.util.ShakeListener.OnShakeListener;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;

public class NearsUpdateFragment extends FragmentBase {
	
	private Context mContext;
	
	GridImageAdapter mAdapter;
	
	ImageView[] imageView = new ImageView[15];
	
	int random = 0;
	
	LinearLayout layout_all;
	
	Bitmap srcBitmap, bmp;
	int brightness = 100;
	
	Boolean status = false;
	
	Paint paint;
	Canvas canvas;
	
	ColorMatrix cMatrix;
	
	public ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	
	StringBuilder fileName = new StringBuilder("ic_welcome_photo_");
	
	YoYo.AnimationComposer shakeAnimation;
	
	@SuppressLint("HandlerLeak")
	Handler updatehandler = new Handler(){
		public void handleMessage(Message msg) { 
            switch (msg.what) {   
            
            	case 0:
            		User user = new User();
            		user.setStatus(true);
            		updateUserData(user, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Log.i("TTTTTTTTTTTTTTTT", "更新成功");
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.i("TTTTTTTTTTTTTTTT", "更新失败");
						}
					});
            		break;
            }   
            super.handleMessage(msg);  
		}
		
	};
	
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
	
	public NearsUpdateFragment() {
		super();
	}
	
	public NearsUpdateFragment(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_nears_update, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	public void initView() {
		mVibrator = (Vibrator) getActivity().getApplication().getSystemService(Context.VIBRATOR_SERVICE);
		
		shakeAnimation = new YoYo.AnimationComposer(new ShakeAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
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
		
		srcBitmap = getBitmapFromAsset(getActivity(), fileName.toString());
		
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
		
        status = CustomApplcation.getInstance().getCurrentUser().getStatus();
		if (status == null) {
			status = true;
		}
		
		mShakeListener = new ShakeListener(getActivity());

        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
				closeShakeListeber();
				
				// ShowToast("现在是附近的人，不是景点漫游");
				
				// 更新用户的信息
				if (!status) {
					Message message = new Message();
					message.what = 0;
					updatehandler.sendMessage(message);
				}
				
				startVibrato();
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
					mVibrator.cancel();
					Intent intent = new Intent();
					intent.setClass(getActivity(), NearPeopleMapActivity.class);
					intent.putExtra("nearsSex", nearsSex);
					startAnimActivity(intent);
					
					}
				},1500);
			}
		});
	}
	
	public void startVibrato(){	
		MediaPlayer player;
		player = MediaPlayer.create(getActivity(), R.raw.awe);
		player.setLooping(false);
        player.start();
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); 
	}
	
	public void startAnim () {
		   
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
		
		srcBitmap = getBitmapFromAsset(getActivity(), fileName.toString());
		
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		openShakeListener();
	}

	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(getActivity(), listener);
	}

}

