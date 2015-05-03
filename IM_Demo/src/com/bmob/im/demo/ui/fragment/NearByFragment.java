package com.bmob.im.demo.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.ui.NearPeopleMapActivity;
import com.bmob.im.demo.util.ShakeListener;
import com.bmob.im.demo.util.ShakeListener.OnShakeListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInUpLeftAnimator;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInUpRightAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutUpLeftAnimator;
import com.daimajia.androidanimations.library.rotating_exits.RotateOutUpRightAnimator;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;

public class NearByFragment extends FragmentBase implements OnClickListener{
	
	private FragmentManager fragmentManager;
    private DialogFragment mMenuDialogFragment;
	
	public ShakeListener mShakeListener = null;
	Vibrator mVibrator;
//	private RelativeLayout mImgUp;
//	private RelativeLayout mImgDn;
	
	ImageView iv_nears_people1, iv_nears_people2, iv_nears_people3;
	
	YoYo.AnimationComposer rotateOutRight, rotateOutLeft, rotateOut;
	
	YoYo.AnimationComposer rotateInRight, rotateInLeft, rotateIn;
	
	Context mContext;
//	TitlePopup titlePopup;
	
	public static Boolean flagShake = false;
	
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
		
        return inflater.inflate(R.layout.fragment_shake_2, container, false);
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
        close.setResource(CustomApplcation.sex? R.drawable.icn_close : R.drawable.icn_close_female);

        MenuObject send = new MenuObject("只看女生");
        send.setResource(CustomApplcation.sex? R.drawable.icon_info_female : R.drawable.icon_info_female_female);

        MenuObject like = new MenuObject("只看男生");
        like.setResource(CustomApplcation.sex? R.drawable.icon_info_male : R.drawable.icon_info_male_female);

        MenuObject addFr = new MenuObject("查看全部");
        addFr.setResource(CustomApplcation.sex? R.drawable.ic_nears_all_people : R.drawable.ic_nears_all_people_female);
  
        MenuObject addFav = new MenuObject("清除地理位置信息");
        addFav.setResource(CustomApplcation.sex? R.drawable.ic_nears_clean_position_info : R.drawable.ic_nears_clean_position_info_female);
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
		
		// 女性主题
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
		}
		
		
		setNearsSex(nearsSex);
		
		
		mVibrator = (Vibrator)((MainActivity)mContext).getApplication().getSystemService(Context.VIBRATOR_SERVICE);
		
//		mImgUp = (RelativeLayout) findViewById(R.id.fragment_shakeImgUp);
//		mImgDn = (RelativeLayout) findViewById(R.id.fragment_shakeImgDown);
		
		iv_nears_people1 = (ImageView) findViewById(R.id.iv_icon_nears_people_1);
		iv_nears_people2 = (ImageView) findViewById(R.id.iv_icon_nears_people_2);
		iv_nears_people3 = (ImageView) findViewById(R.id.iv_icon_nears_people_3);
		
		if (!CustomApplcation.sex) {
			iv_nears_people1.setImageResource(R.drawable.icon_near_people1_female);
			iv_nears_people2.setImageResource(R.drawable.icon_near_people2_female);
			iv_nears_people3.setImageResource(R.drawable.icon_near_people3_female);
		}
		
		rotateOutRight = new YoYo.AnimationComposer(new RotateOutUpRightAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		rotateOutLeft = new YoYo.AnimationComposer(new RotateOutUpLeftAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		rotateOut = new YoYo.AnimationComposer(new RotateOutAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		rotateInRight = new YoYo.AnimationComposer(new RotateInUpRightAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		rotateInLeft = new YoYo.AnimationComposer(new RotateInUpLeftAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		rotateIn = new YoYo.AnimationComposer(new RotateInAnimator())
		.duration(1000)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		fragmentManager = ((MainActivity)mContext).getSupportFragmentManager();
        // initToolbar();
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance((int) getResources().getDimension(R.dimen.tool_bar_height), getMenuObjects());
		
		
		
		mShakeListener = new ShakeListener((MainActivity)mContext);
		flag = true;

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
					intent.setClass(mContext, NearPeopleMapActivity.class);
					intent.putExtra("nearsSex", nearsSex);
					// ShowToast("SEX:" + nearsSex);
					mContext.startActivity(intent);
					
					}
				},1500);
			}
		});
	}

	public void startAnim () {
//		AnimationSet animup = new AnimationSet(true);
//		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
//		mytranslateanimup0.setDuration(500);
//		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
//		mytranslateanimup1.setDuration(500);
//		mytranslateanimup1.setStartOffset(500);
//		animup.addAnimation(mytranslateanimup0);
//		animup.addAnimation(mytranslateanimup1);
//		//mImgUp.startAnimation(animup);
//		
//		AnimationSet animdn = new AnimationSet(true);
//		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,+0.5f);
//		mytranslateanimdn0.setDuration(500);
//		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-0.5f);
//		mytranslateanimdn1.setDuration(500);
//		mytranslateanimdn1.setStartOffset(500);
//		animdn.addAnimation(mytranslateanimdn0);
//		animdn.addAnimation(mytranslateanimdn1);
//		//mImgDn.startAnimation(animdn);	
		
		rotateOutRight.playOn(iv_nears_people3);
		rotateOutLeft.playOn(iv_nears_people1);
		rotateOut.playOn(iv_nears_people2);
		
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
  
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (iv_nears_people1 != null && iv_nears_people2 != null && iv_nears_people3 != null) {
			rotateInLeft.playOn(iv_nears_people1);
			rotateInRight.playOn(iv_nears_people3);
			rotateIn.playOn(iv_nears_people2);
		}
	}
}
