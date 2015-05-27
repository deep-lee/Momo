package com.bmob.im.demo.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.FirstGuideAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class FirstGuildActivity extends Activity implements OnClickListener, OnPageChangeListener, OnTouchListener{
	
	private ViewPager vp;  
    private FirstGuideAdapter vpAdapter;  
    private List<View> views;  
    
    //图片
    private static final int[] pics = { 
    	R.drawable.first_guide_1 ,
    	R.drawable.first_guide_2,
    	R.drawable.first_guide_3,
    	R.drawable.first_guide_4
    };  
      
    //下面小圆点
    private ImageView[] dots;  
      
    //
    private int currentIndex;  
    
    float point1_x = 0, point1_y = 0, point2_x = 0, point2_y = 0;
    
    Boolean flag = false;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 requestWindowFeature(Window.FEATURE_NO_TITLE); 
	       /*set it to be full screen*/ 
	     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,    
	     WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		setContentView(R.layout.activity_first_guild);
		
		initView();
	}
	
	public void initView() {
		
		flag = getIntent().getBooleanExtra("flag", false);
		
        views = new ArrayList<View>();  
         
        RelativeLayout.LayoutParams mParams = 
        		new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT);  
        
        for(int i=0; i<pics.length; i++) {  
            ImageView iv = new ImageView(this); 
            iv.setScaleType(ScaleType.CENTER_CROP);
            iv.setLayoutParams(mParams);        	
            iv.setImageBitmap(initbit(pics[i]));  
            views.add(iv); 
        }  
        
        //view paper
        vp = (ViewPager) findViewById(R.id.viewpager);  
        // 
        vpAdapter = new FirstGuideAdapter(views);  
        vp.setAdapter(vpAdapter);  
        // 
        vp.setOnPageChangeListener(this);  
        
        vp.setOnTouchListener(this);

        initDots();  
        
//        button.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				Intent intent=new Intent();
//				intent.setClass(FirstGuildActivity.this, LoginActivity.class);
//				startActivity(intent);
//				overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
//				finish();
//			}
//		});
	}
	
	//初始化小圆点
    
    private void initDots() {  
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
  
        dots = new ImageView[pics.length];  
  
        //
        for (int i = 0; i < pics.length; i++) {  
            dots[i] = (ImageView) ll.getChildAt(i);  
            dots[i].setEnabled(true);//
            dots[i].setOnClickListener(this);  
            dots[i].setTag(i);//  
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setEnabled(false);
        dots[currentIndex].setImageResource(R.drawable.page_indicator_focused);
    }  
   
    //加载大图片 （最省内存的方法） 返回bitmap
    public Bitmap initbit(int rec)
    {
    	//获取图片资源
    	InputStream is = this.getResources().openRawResource(rec);

        BitmapFactory.Options options = new  BitmapFactory.Options();

        options.inJustDecodeBounds =  false;
        //文件大小缩小8倍
         options.inSampleSize = 4;    
        
       /**
        * decodeStream最大的秘密在于其直接调用JNI>>nativeDecodeAsset()来完成decode，
        * 无需再使用java层的createBitmap，从而节省了java层的空间
        */
        Bitmap btp =  BitmapFactory.decodeStream(is, null,  options);        
    	return btp;
    }
    
    
    /** 
     * 当前view图片
     */  
    private void setCurView(int position)  
    {  
        if (position < 0 || position >= pics.length) {  
            return;  
        }  
  
        vp.setCurrentItem(position);  
    }  
  
    
    /** 
     * 当前选中的小圆点
     */  
    private void setCurDot(int positon)  
    {  
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {  
            return;  
        }  
  
        dots[positon].setEnabled(false);  
        dots[currentIndex].setEnabled(true);  
        
        for (int i = 0; i < dots.length; i++) {
			if (i == positon) {
				dots[i].setImageResource(R.drawable.page_indicator_focused);
			}
			else {
				dots[i].setImageResource(R.drawable.page_indicator_unfocused);
			}
		}
  
        currentIndex = positon;  
    }  
  
    @Override  
    public void onPageScrollStateChanged(int arg0) {  
        // TODO Auto-generated method stub  
          
    }  
  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
        // TODO Auto-generated method stub  
          
    }  
  
    @Override  
    public void onPageSelected(int arg0) {  
        //最后时候的按钮
        setCurDot(arg0);  
    }  
  
    
    @Override    
    public void onClick(View v) {  
        int position = (Integer)v.getTag();  
        setCurView(position);  
        setCurDot(position);  
    }

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		int action = MotionEventCompat.getActionMasked(event);
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			point1_x = event.getX();
			break;
			
		case MotionEvent.ACTION_MOVE:
			// point2_x = event.getX();
			break;
			
		case MotionEvent.ACTION_UP:
			point2_x = event.getX();
			if (currentIndex == 3 && point1_x - point2_x > 100) {
				
				if (!flag) {
					Intent intent=new Intent();
					intent.setClass(FirstGuildActivity.this, LoginActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
					finish();
				}
				else {
					finish();
				}
			}
			break;

		default:
			break;
		}
		
		return false;
	} 
}
