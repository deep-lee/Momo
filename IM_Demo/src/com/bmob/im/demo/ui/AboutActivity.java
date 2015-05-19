package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class AboutActivity extends BaseActivity implements OnClickListener{
	
	RelativeLayout rl_welcome_page, rl_help_and_feedback, rl_check_for_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initView();
	}
	
	public void initView() {
		rl_welcome_page = (RelativeLayout) findViewById(R.id.about_layout_welcome_page);
		rl_help_and_feedback = (RelativeLayout) findViewById(R.id.about_layout_help_and_feedback);
		rl_check_for_update = (RelativeLayout) findViewById(R.id.about_layout_check_for_update);
		
		rl_welcome_page.setOnClickListener(this);
		rl_help_and_feedback.setOnClickListener(this);
		rl_check_for_update.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.about_layout_welcome_page:
			Intent welcomePageIntent = new Intent();
			welcomePageIntent.setClass(AboutActivity.this, FirstGuildActivity.class);
			welcomePageIntent.putExtra("flag", true);
			startActivity(welcomePageIntent);
			break;
			
		case R.id.about_layout_help_and_feedback:
			
			break;
			
		case R.id.about_layout_check_for_update:
			
			break;

		default:
			break;
		}
	}
	
	
}
