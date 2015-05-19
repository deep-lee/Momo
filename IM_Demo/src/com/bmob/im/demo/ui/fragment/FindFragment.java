package com.bmob.im.demo.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.AddPopWindow;
import com.bmob.im.demo.ui.AttractionsRomaActivity2;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.GameCommunityActivity;
import com.bmob.im.demo.ui.LifeCircleActivity;
import com.bmob.im.demo.ui.ShakeForNearPeopleActivity;


/*
 * 2015年5月6日
 * @author:deeplee
 * 
 */

public class FindFragment extends FragmentBase implements OnClickListener {
	
	
	Context mContext;
	
	ImageView iv_add;
	
	ImageView icon_life_circle;
	
	RelativeLayout rl_life_circle, rl_nears_people, rl_attractions_roma, rl_sign_in,
					rl_game_community, rl_eat_drink_play_happy;
	

	public FindFragment() {
		super();
	}

	public FindFragment(Context mContext) {
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
		
        return inflater.inflate(R.layout.fragment_find, container, false);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
		// TODO Auto-generated method stub
		
//		initTopBarForOnlyTitle("发现");
		
		iv_add = (ImageView) findViewById(R.id.iv_add);
		
		iv_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AddPopWindow addPopWindow = new AddPopWindow(getActivity());
                addPopWindow.showPopupWindow(iv_add);
            }

        });
		
		rl_life_circle = (RelativeLayout) findViewById(R.id.fragment_find_life_circle);
		rl_nears_people = (RelativeLayout) findViewById(R.id.fragment_find_life_nears_friends);
		rl_attractions_roma = (RelativeLayout) findViewById(R.id.fragment_find_attractions_roma);
		rl_sign_in = (RelativeLayout) findViewById(R.id.fragment_find_sign_in);
		rl_game_community = (RelativeLayout) findViewById(R.id.fragment_find_game_community);
		rl_eat_drink_play_happy = (RelativeLayout) findViewById(R.id.fragment_find_eat_drink_play_happy);
		
		rl_life_circle.setOnClickListener(this);
		rl_nears_people.setOnClickListener(this);
		rl_attractions_roma.setOnClickListener(this);
		rl_sign_in.setOnClickListener(this);
		rl_game_community.setOnClickListener(this);
		rl_eat_drink_play_happy.setOnClickListener(this);
		
		icon_life_circle = (ImageView) findViewById(R.id.find_life_circle_icon);
		
	}

	
  
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		// 生活圈
		case R.id.fragment_find_life_circle:
			Intent lifeCircleIntent = new Intent();
			lifeCircleIntent.setClass(mContext, LifeCircleActivity.class);
			mContext.startActivity(lifeCircleIntent);
			break;
			
		case R.id.fragment_find_life_nears_friends:
			
			Intent nearsIntent = new Intent();
			nearsIntent.setClass(mContext, ShakeForNearPeopleActivity.class);
			mContext.startActivity(nearsIntent);
			
			break;
			
		case R.id.fragment_find_attractions_roma:
			Intent romaIntent = new Intent();
			romaIntent.setClass(mContext, AttractionsRomaActivity2.class);
			mContext.startActivity(romaIntent);
			break;
			
		case R.id.fragment_find_sign_in:
			
			break;
			
		case R.id.fragment_find_game_community:
			Intent gameCommunityIntent = new Intent();
			gameCommunityIntent.setClass(mContext, GameCommunityActivity.class);
			mContext.startActivity(gameCommunityIntent);
			break;
			
		case R.id.fragment_find_eat_drink_play_happy:
			
			break;

		default:
			break;
		}
	}
}


