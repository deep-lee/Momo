package com.bmob.im.demo.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.ui.NearPeopleMapActivity;
import com.bmob.im.demo.util.ActionItem;
import com.bmob.im.demo.util.ShakeListener;
import com.bmob.im.demo.util.ShakeListener.OnShakeListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.TitlePopup;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

public class NearByFragment extends FragmentBase implements OnClickListener{
	
	private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
	
	public ShakeListener mShakeListener = null;
	Vibrator mVibrator;
	private RelativeLayout mImgUp;
	private RelativeLayout mImgDn;
	
	Context mContext;
//	TitlePopup titlePopup;
	
	public static Boolean flagShake = false;
	
	public int nearsSex;
	
	public Boolean flag = false;
	
	
	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public NearByFragment() {
		super();
	}

	public NearByFragment(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
        return inflater.inflate(R.layout.fragment_shake, container, false);
	}
	
    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<MenuObject>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("只看女生");
        send.setResource(R.drawable.icon_info_female);

        MenuObject like = new MenuObject("只看男生");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icon_info_male);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("查看全部");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_nears_all_people));
        addFr.setDrawable(bd);
  
        MenuObject addFav = new MenuObject("清除地理位置信息");
        addFav.setResource(R.drawable.ic_nears_clean_position_info);
//
//        MenuObject block = new MenuObject("Block user");
//        block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
//        menuObjects.add(block);
        return menuObjects;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initData();
		initView();
	}
	

	@Override
    public void onHiddenChanged(boolean hidden) {
    	// TODO Auto-generated method stub
    	super.onHiddenChanged(hidden);
    	if(hidden)
    	{
    		ShowToast("关闭附近的人监听器");
    		closeShakeListeber();
    	}
    	else {
//    		initData();
//    		initView();
    		
    		openShakeListener();
		}
    }
	
	public void nearBySexChanged(int sex) {
		setNearsSex(sex);
		nearsSex = sex;
		ShowToast("SEX:" + nearsSex);
	}
	
	private void initData(){
		
		flag = true;
//		titlePopup = new TitlePopup(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, this);
//		
//		titlePopup.addAction(new ActionItem(mContext, R.string.only_female));
//		titlePopup.addAction(new ActionItem(mContext, R.string.only_male));
//		titlePopup.addAction(new ActionItem(mContext, R.string.all_female_male));
	}
	
	

	private void initView() {
		// TODO Auto-generated method stub
		
		initTopBarForRight("附近的人", R.drawable.base_action_bar_nears_sex_selector, new onRightImageButtonClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// titlePopup.show(v);
				
				if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
			}
		});
		
		mVibrator = (Vibrator)((MainActivity)mContext).getApplication().getSystemService(Context.VIBRATOR_SERVICE);
		
		mImgUp = (RelativeLayout) findViewById(R.id.fragment_shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.fragment_shakeImgDown);
		
		fragmentManager = ((MainActivity)mContext).getSupportFragmentManager();
        // initToolbar();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), getMenuObjects());
		
		
		
		mShakeListener = new ShakeListener((MainActivity)mContext);
		flag = true;

        mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
				closeShakeListeber();
				
				
				ShowToast("现在是附近的人，不是景点漫游");
				
				startVibrato();
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run(){
					mVibrator.cancel();
					// mShakeListener.start();
					Intent intent = new Intent();
					intent.setClass(mContext, NearPeopleMapActivity.class);
					intent.putExtra("nearsSex", nearsSex);
					// ShowToast("SEX:" + nearsSex);
					mContext.startActivity(intent);
					
					}
				},2000);
			}
		});
	}

	public void startAnim () {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimup0.setDuration(500);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimup1.setDuration(500);
		mytranslateanimup1.setStartOffset(500);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);
		
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
		mytranslateanimdn0.setDuration(500);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
		mytranslateanimdn1.setDuration(500);
		mytranslateanimdn1.setStartOffset(500);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);	
	}
	public void startVibrato(){	
		MediaPlayer player;
		player = MediaPlayer.create(mContext, R.raw.awe);
		player.setLooping(false);
        player.start();
		mVibrator.vibrate( new long[]{500,200,500,200}, -1); 
	}
	
	public void openShakeListener() {
		mShakeListener.start();
		flag = true;
	}
	
	public void closeShakeListeber() {
		mShakeListener.stop();
		flag = false;
	}

	
}
