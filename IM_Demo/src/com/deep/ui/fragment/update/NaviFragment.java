package com.deep.ui.fragment.update;


import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.rotating_entrances.RotateInAnimator;
import com.deep.ui.update.MainActivity2;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class NaviFragment  extends Fragment implements OnClickListener{
	
	private static final int RECENTFRAGMENT = 0;
    private static final int CONTACTFRAGMENT = 1;
    private static final int NEARSFRAGMENT = 2;
    private static final int FINDFRAGMENT = 3;
    private static final int SETTINGFRAGMENT = 4;
    private static final int PERSONINFOFRAGMENT = 5;

    private MainActivity2 mActivity;
    private TextView navi_recent;
    private TextView navi_contact;
    private TextView navi_nears;
    private TextView navi_find;
    private ImageView navi_setting;
    private ImageView navi_avatar;
    private TextView navi_nick;
    public static TextView tv_navi_chat_tips;
    public static TextView tv_navi_contact_tips;
    public static TextView tv_navi_nears_tips;
    public static TextView tv_navi_find_tips;
    
    private User currentUser;

    RecentUpdateFragment mRecentUpdateFragment;
    ContactUpdateFragment mContactUpdateFragment;
    NearsUpdateFragment mNearsUpdateFragment;
    FindUpdateFragment mFindUpdateFragment;
    SettingUpdateFragment mSettingUpdateFragment;
    PersonInfoUpdateFragment mPersonInfoUpdateFragment;

    private FragmentManager fragmentManager;

    YoYo.AnimationComposer rotateInAnimation, fadeInLeftAnimation;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View rootView;// 缓存Fragment view

    /**
     * 显示左边导航栏fragment
     */
    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_navi, null);
        }

        fragmentManager = getFragmentManager();

        init();

        return rootView;

    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = (MainActivity2) activity;
        super.onAttach(activity);
    }

    /**
     * 初始化，设置点击事件
     */
    private void init() {
    	
    	currentUser = CustomApplcation.getInstance().getCurrentUser();
    	
    	rotateInAnimation = new YoYo.AnimationComposer(new RotateInAnimator())
    	.duration(1000)
    	.interpolate(new AccelerateDecelerateInterpolator());
    	
    	navi_recent = (TextView) rootView.findViewById(R.id.tv_navi_chat);
    	navi_contact = (TextView) rootView.findViewById(R.id.tv_navi_contacts);
    	navi_nears = (TextView) rootView.findViewById(R.id.tv_navi_nears);
    	navi_find = (TextView) rootView.findViewById(R.id.tv_navi_find);
    	navi_setting = (ImageView) rootView.findViewById(R.id.fragment_navi_setting_iv);
    	tv_navi_chat_tips = (TextView) rootView.findViewById(R.id.tv_navi_chat_tips);
    	tv_navi_contact_tips = (TextView) rootView.findViewById(R.id.tv_navi_contact_tips);
    	tv_navi_nears_tips = (TextView) rootView.findViewById(R.id.tv_navi_nears_tips);
    	tv_navi_find_tips = (TextView) rootView.findViewById(R.id.tv_navi_find_tips);
    	
    	navi_avatar = (ImageView) rootView.findViewById(R.id.fragment_navi_avatar_iv);
    	navi_nick = (TextView) rootView.findViewById(R.id.fragment_navi_nick);
    	
    	ImageLoader.getInstance().displayImage(currentUser.getAvatar(), navi_avatar);
    	navi_nick.setText(currentUser.getNick());

    	navi_recent.setSelected(true);// 默认选中菜单
    	navi_contact.setSelected(false);
    	navi_nears.setSelected(false);
    	navi_find.setSelected(false);

        OnTabSelected(RECENTFRAGMENT);

        navi_recent.setOnClickListener(this);
        navi_contact.setOnClickListener(this);
        navi_nears.setOnClickListener(this);
        navi_find.setOnClickListener(this);
        navi_setting.setOnClickListener(this);
        navi_avatar.setOnClickListener(this);
    }

    /**
     * 点击导航栏切换 同时更改标题
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.tv_navi_chat:

        	navi_recent.setSelected(true);// 菜单设置为被选中状态，其余设置为非选中状态
        	navi_contact.setSelected(false);
        	navi_nears.setSelected(false);
        	navi_find.setSelected(false);
        	navi_setting.setSelected(false);
        	navi_avatar.setSelected(false);
            
            OnTabSelected(RECENTFRAGMENT);
            MainActivity2.currentTabIndex = 0;
            break;
        case R.id.tv_navi_contacts:

        	navi_recent.setSelected(false);
        	navi_contact.setSelected(true);
            navi_nears.setSelected(false);
            navi_find.setSelected(false);
            navi_setting.setSelected(false);
            navi_avatar.setSelected(false);

            OnTabSelected(CONTACTFRAGMENT);
            MainActivity2.currentTabIndex = 1;
            break;
        case R.id.tv_navi_nears:// 

        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(true);
            navi_find.setSelected(false);
            navi_setting.setSelected(false);
            navi_avatar.setSelected(false);

            OnTabSelected(NEARSFRAGMENT);
            MainActivity2.currentTabIndex = 2;
            break;
        case R.id.tv_navi_find:

        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(false);
            navi_find.setSelected(true);
            navi_setting.setSelected(false);
            navi_avatar.setSelected(false);

            OnTabSelected(FINDFRAGMENT);
            MainActivity2.currentTabIndex = 3;
            break;
            
        case R.id.fragment_navi_setting_iv:
        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(false);
            navi_find.setSelected(false);
            navi_setting.setSelected(true);
            navi_avatar.setSelected(false);

            OnTabSelected(SETTINGFRAGMENT);
            MainActivity2.currentTabIndex = 4;
        	break;
        	
        case R.id.fragment_navi_avatar_iv:
        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(false);
            navi_find.setSelected(false);
            navi_setting.setSelected(false);
            navi_avatar.setSelected(true);

            OnTabSelected(PERSONINFOFRAGMENT);
            MainActivity2.currentTabIndex = 5;
        	break;
        }
        
        mActivity.getSlidingMenu().toggle();
    }
    
    //选中导航中对应的tab选项
    private void OnTabSelected(int index) {
    	
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        switch (index) {
        case RECENTFRAGMENT:
        	hideFragments(transaction);
          if (null == mRecentUpdateFragment) {
        	  mRecentUpdateFragment = new RecentUpdateFragment(getActivity(), this);
              transaction.add(R.id.center, mRecentUpdateFragment);
          } else {
              transaction.show(mRecentUpdateFragment);
          }
            break;
        case CONTACTFRAGMENT: 

        	hideFragments(transaction);
        	if (null == mContactUpdateFragment) {
        		mContactUpdateFragment = new ContactUpdateFragment(getActivity(), this);
                transaction.add(R.id.center, mContactUpdateFragment);
            } else {
                transaction.show(mContactUpdateFragment);
            }
            break;
        case NEARSFRAGMENT:
			hideFragments(transaction);
	        if(null == mNearsUpdateFragment){
	        	mNearsUpdateFragment = new NearsUpdateFragment(getActivity());
	        	transaction.add(R.id.center, mNearsUpdateFragment);
	        }else{
	        	transaction.show(mNearsUpdateFragment);
	        }
            break;
        case FINDFRAGMENT:

        	hideFragments(transaction);
	        if(null == mFindUpdateFragment){
	        	mFindUpdateFragment = new FindUpdateFragment(getActivity());
	        	transaction.add(R.id.center, mFindUpdateFragment);
	        }else{
	        	transaction.show(mFindUpdateFragment);
	        }
            break;
            
        case SETTINGFRAGMENT:
        	hideFragments(transaction);
	        if(null == mSettingUpdateFragment){
	        	mSettingUpdateFragment = new SettingUpdateFragment(getActivity());
	        	transaction.add(R.id.center, mSettingUpdateFragment);
	        }else{
	        	transaction.show(mSettingUpdateFragment);
	        }
        	break;
        	
        case PERSONINFOFRAGMENT:
        	hideFragments(transaction);
	        if(null == mPersonInfoUpdateFragment){
	        	mPersonInfoUpdateFragment = new PersonInfoUpdateFragment(getActivity());
	        	transaction.add(R.id.center, mPersonInfoUpdateFragment);
	        }else{
	        	transaction.show(mPersonInfoUpdateFragment);
	        }
        	break;
        }
        
        transaction.commit();
    }

    /**
     * 将所有fragment都置为隐藏状态
     * 
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
    	if(mRecentUpdateFragment != null){
    		transaction.hide(mRecentUpdateFragment);
    	}
    	if(mContactUpdateFragment != null){
    		transaction.hide(mContactUpdateFragment);
    	}
    	if(mNearsUpdateFragment != null){
    		transaction.hide(mNearsUpdateFragment);
    	}
    	if(mFindUpdateFragment != null){
    		transaction.hide(mFindUpdateFragment);
    	}
    	if(mSettingUpdateFragment != null){
    		transaction.hide(mSettingUpdateFragment);
    	}
    	if(mPersonInfoUpdateFragment != null){
    		transaction.hide(mPersonInfoUpdateFragment);
    	}
    }
    
    public void showOpenAnimation() {
    	rotateInAnimation.playOn(navi_setting);
	}

	public RecentUpdateFragment getmRecentUpdateFragment() {
		return mRecentUpdateFragment;
	}

	public void setmRecentUpdateFragment(RecentUpdateFragment mRecentUpdateFragment) {
		this.mRecentUpdateFragment = mRecentUpdateFragment;
	}

	public ContactUpdateFragment getmContactUpdateFragment() {
		return mContactUpdateFragment;
	}

	public void setmContactUpdateFragment(
			ContactUpdateFragment mContactUpdateFragment) {
		this.mContactUpdateFragment = mContactUpdateFragment;
	}

	public NearsUpdateFragment getmNearsUpdateFragment() {
		return mNearsUpdateFragment;
	}

	public void setmNearsUpdateFragment(NearsUpdateFragment mNearsUpdateFragment) {
		this.mNearsUpdateFragment = mNearsUpdateFragment;
	}

	public FindUpdateFragment getmFindUpdateFragment() {
		return mFindUpdateFragment;
	}

	public void setmFindUpdateFragment(FindUpdateFragment mFindUpdateFragment) {
		this.mFindUpdateFragment = mFindUpdateFragment;
	}

	public SettingUpdateFragment getmSettingUpdateFragment() {
		return mSettingUpdateFragment;
	}

	public void setmSettingUpdateFragment(
			SettingUpdateFragment mSettingUpdateFragment) {
		this.mSettingUpdateFragment = mSettingUpdateFragment;
	}

	public PersonInfoUpdateFragment getmPersonInfoUpdateFragment() {
		return mPersonInfoUpdateFragment;
	}

	public void setmPersonInfoUpdateFragment(
			PersonInfoUpdateFragment mPersonInfoUpdateFragment) {
		this.mPersonInfoUpdateFragment = mPersonInfoUpdateFragment;
	}
    
    public void setChatTipState(Boolean flag){
    	if (flag) {
			tv_navi_chat_tips.setVisibility(View.VISIBLE);
		}else {
			tv_navi_chat_tips.setVisibility(View.INVISIBLE);
		}
    }
    
    public void setContactTipState(Boolean flag){
    	if (flag) {
			tv_navi_contact_tips.setVisibility(View.VISIBLE);
		}else {
			tv_navi_contact_tips.setVisibility(View.INVISIBLE);
		}
    }
    
    public void setNearsTipState(Boolean flag){
    	if (flag) {
			tv_navi_nears_tips.setVisibility(View.VISIBLE);
		}else {
			tv_navi_nears_tips.setVisibility(View.INVISIBLE);
		}
    }
    
    public void setFindTipState(Boolean flag){
    	if (flag) {
			tv_navi_find_tips.setVisibility(View.VISIBLE);
		}else {
			tv_navi_find_tips.setVisibility(View.INVISIBLE);
		}
    }
    
}
