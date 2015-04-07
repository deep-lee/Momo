package com.bmob.im.demo.ui;

import java.io.Serializable;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageShowActivity extends Activity {
	ImageView imageShow;
	Bitmap bitmap;
	
	Handler handler = new Handler(){
		
		 public void handleMessage(Message msg) {   
            switch (msg.what) {   
            	case 0:
            		imageShow.setImageBitmap(bitmap);
            		break;
            
            }   
            super.handleMessage(msg);   
       } 
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_show);
		
		imageShow = (ImageView) findViewById(R.id.image_show);
//		final byte[] imageByte = getIntent().getByteArrayExtra("imageByte");
		
		Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
		
		float sx = getIntent().getFloatExtra("sx", 1);
		float sy = getIntent().getFloatExtra("sy", 1);
		
		imageShow.setScaleX(sx);
		imageShow.setScaleY(sy);
		imageShow.setImageBitmap(bitmap);
		
		
		
//		Thread newThread;
//		newThread = new Thread(new Runnable() {
//		    @Override
//		    public void run() {
//		        //这里写入子线程需要做的工作
//		    	bitmap = Bytes2Bimap(imageByte);
//				
//				Message message = new Message();
//				message.what = 0;
//				handler.sendMessage(message);
//		    }
//		});
//		newThread.start();
		
//		new Thread(){
//			@Override
//			public void	run(){
//				bitmap = Bytes2Bimap(imageByte);
//				
//				Message message = new Message();
//				message.what = 0;
//				handler.sendMessage(message);
//			}
//		};
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return true;
	}
	
	 Bitmap Bytes2Bimap(byte[] b) {  
	     if (b.length != 0) {  
	         return BitmapFactory.decodeByteArray(b, 0, b.length);  
	     } else {  
	         return null;  
	     }  
	 } 
}
