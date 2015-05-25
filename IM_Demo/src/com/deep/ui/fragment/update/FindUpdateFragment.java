package com.deep.ui.fragment.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.AttractionsRomaActivity2;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.GameCenterActivity;
import com.bmob.im.demo.ui.GameCommunityActivity;
import com.bmob.im.demo.ui.LifeCircleActivity;
import com.bmob.im.demo.ui.ShakeForNearPeopleActivity;
import com.deep.ui.update.MainActivity2;

public class FindUpdateFragment extends FragmentBase implements OnClickListener{
	
	Context mContext;
	
	RelativeLayout rl_life_circle, rl_attractions_roma, rl_sign_in,
	rl_game_community, rl_eat_drink_play_happy, rl_game_center;

	public FindUpdateFragment() {
		super();
	}

	public FindUpdateFragment(Context mContext) {
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
		
        return inflater.inflate(R.layout.fragment_find_update, container, false);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	private void initView(){
		// TODO Auto-generated method stub
		
		MainActivity2.setSexSelecteStatus(false);
		MainActivity2.setSexShowStatus(false);
		
		rl_life_circle = (RelativeLayout) findViewById(R.id.fragment_find_life_circle);
		rl_game_center = (RelativeLayout) findViewById(R.id.fragment_find_game_center);
		rl_attractions_roma = (RelativeLayout) findViewById(R.id.fragment_find_attractions_roma);
		rl_sign_in = (RelativeLayout) findViewById(R.id.fragment_find_sign_in);
		rl_game_community = (RelativeLayout) findViewById(R.id.fragment_find_game_community);
		rl_eat_drink_play_happy = (RelativeLayout) findViewById(R.id.fragment_find_eat_drink_play_happy);
		
		rl_life_circle.setOnClickListener(this);
		rl_game_center.setOnClickListener(this);
		rl_attractions_roma.setOnClickListener(this);
		rl_sign_in.setOnClickListener(this);
		rl_game_community.setOnClickListener(this);
		rl_eat_drink_play_happy.setOnClickListener(this);
		
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
		
		// Éú»îÈ¦
		case R.id.fragment_find_life_circle:
			Intent lifeCircleIntent = new Intent();
			lifeCircleIntent.setClass(getActivity(), LifeCircleActivity.class);
			mContext.startActivity(lifeCircleIntent);
			break;
			
		case R.id.fragment_find_game_center:
			
			Intent nearsIntent = new Intent();
			nearsIntent.setClass(getActivity(), GameCenterActivity.class);
			mContext.startActivity(nearsIntent);
			
			break;
			
		case R.id.fragment_find_attractions_roma:
			Intent romaIntent = new Intent();
			romaIntent.setClass(getActivity(), AttractionsRomaActivity2.class);
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
			
		case R.id.fragment_find_shake_tv:
			Intent topNearsIntent = new Intent();
			topNearsIntent.setClass(getActivity(), ShakeForNearPeopleActivity.class);
			startAnimActivity(topNearsIntent);
			break;

		default:
			break;
		}
	}
}
