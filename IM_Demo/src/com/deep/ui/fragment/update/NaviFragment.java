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

    private MainActivity2 mActivity;
    private TextView navi_recent;
    private TextView navi_contact;
    private TextView navi_nears;
    private TextView navi_find;
    private ImageView navi_setting;
    
    private ImageView navi_avatar;
    private TextView navi_nick;
    
    private User currentUser;

    RecentUpdateFragment mRecentUpdateFragment;
    ContactUpdateFragment mContactUpdateFragment;
    NearsUpdateFragment mNearsUpdateFragment;
    FindUpdateFragment mFindUpdateFragment;
    SettingUpdateFragment mSettingUpdateFragment;

    private FragmentManager fragmentManager;

    YoYo.AnimationComposer rotateInAnimation, fadeInLeftAnimation;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View rootView;// ����Fragment view

    /**
     * ��ʾ��ߵ�����fragment
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
     * ��ʼ�������õ���¼�
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
    	
    	navi_avatar = (ImageView) rootView.findViewById(R.id.fragment_navi_avatar_iv);
    	navi_nick = (TextView) rootView.findViewById(R.id.fragment_navi_nick);
    	
    	ImageLoader.getInstance().displayImage(currentUser.getAvatar(), navi_avatar);
    	navi_nick.setText(currentUser.getNick());

    	navi_recent.setSelected(true);// Ĭ��ѡ�в˵�
    	navi_contact.setSelected(false);
    	navi_nears.setSelected(false);
    	navi_find.setSelected(false);

        OnTabSelected(RECENTFRAGMENT);

        navi_recent.setOnClickListener(this);
        navi_contact.setOnClickListener(this);
        navi_nears.setOnClickListener(this);
        navi_find.setOnClickListener(this);
        navi_setting.setOnClickListener(this);
    }

    /**
     * ����������л� ͬʱ���ı���
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.tv_navi_chat:

        	navi_recent.setSelected(true);// �˵�����Ϊ��ѡ��״̬����������Ϊ��ѡ��״̬
        	navi_contact.setSelected(false);
        	navi_nears.setSelected(false);
        	navi_find.setSelected(false);
        	navi_setting.setSelected(false);
            
            OnTabSelected(RECENTFRAGMENT);
            break;
        case R.id.tv_navi_contacts:

        	navi_recent.setSelected(false);
        	navi_contact.setSelected(true);
            navi_nears.setSelected(false);
            navi_find.setSelected(false);
            navi_setting.setSelected(false);

            OnTabSelected(CONTACTFRAGMENT);
            break;
        case R.id.tv_navi_nears:// 

        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(true);
            navi_find.setSelected(false);
            navi_setting.setSelected(false);

            OnTabSelected(NEARSFRAGMENT);
            break;
        case R.id.tv_navi_find:

        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(false);
            navi_find.setSelected(true);
            navi_setting.setSelected(false);

            OnTabSelected(FINDFRAGMENT);
            break;
            
        case R.id.fragment_navi_setting_iv:
        	navi_recent.setSelected(false);
        	navi_contact.setSelected(false);
            navi_nears.setSelected(false);
            navi_find.setSelected(false);
            navi_setting.setSelected(true);

            OnTabSelected(SETTINGFRAGMENT);
        	break;
        }
        
        mActivity.getSlidingMenu().toggle();
    }
    
    //ѡ�е����ж�Ӧ��tabѡ��
    private void OnTabSelected(int index) {
    	
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        switch (index) {
        case RECENTFRAGMENT:
        	hideFragments(transaction);
          if (null == mRecentUpdateFragment) {
        	  mRecentUpdateFragment = new RecentUpdateFragment(getActivity());
              transaction.add(R.id.center, mRecentUpdateFragment);
          } else {
              transaction.show(mRecentUpdateFragment);
          }
            break;
        case CONTACTFRAGMENT: 

        	hideFragments(transaction);
        	if (null == mContactUpdateFragment) {
        		mContactUpdateFragment = new ContactUpdateFragment(getActivity());
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
        }
        
        transaction.commit();
    }

    /**
     * ������fragment����Ϊ����״̬
     * 
     * @param transaction
     *            ���ڶ�Fragmentִ�в���������
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
    }
    
    public void showOpenAnimation() {
    	rotateInAnimation.playOn(navi_setting);
	}
    
}
