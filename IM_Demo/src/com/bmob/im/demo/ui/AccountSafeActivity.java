package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class AccountSafeActivity extends BaseActivity implements OnClickListener{
	
	RelativeLayout rl_change_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_safe);
		
		initView();
	}
	
	public void initView() {
		rl_change_password = (RelativeLayout) findViewById(R.id.layout_change_password);
		rl_change_password.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_change_password:
			Intent intent = new Intent();
			intent.setClass(AccountSafeActivity.this, ChangePasswordActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
