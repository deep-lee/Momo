package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;





import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;


public class SlideSetActivity extends BaseActivity implements View.OnClickListener{

	RelativeLayout accountSafeLayout, accountBangdingLayout, hidingModeLayout, messageNitifyLayout, personDress, emojStoreLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_set);
		initView();
		
	}
	
	private void initView() {
		
		initTopBarForLeft("����");
		
		accountSafeLayout = (RelativeLayout) findViewById(R.id.slide_layout_account_safe);
		accountBangdingLayout = (RelativeLayout) findViewById(R.id.slide_layout_account_bangding);
		hidingModeLayout = (RelativeLayout) findViewById(R.id.slide_layout_hiding_mode);
		messageNitifyLayout = (RelativeLayout) findViewById(R.id.slide_layout_message_notify);
		personDress = (RelativeLayout) findViewById(R.id.slide_layout_person_dress);
		emojStoreLayout = (RelativeLayout) findViewById(R.id.slide_layout_emoj_store);
		
		accountBangdingLayout.setOnClickListener(this);
		accountSafeLayout.setOnClickListener(this);
		hidingModeLayout.setOnClickListener(this);
		messageNitifyLayout.setOnClickListener(this);
		personDress.setOnClickListener(this);
		emojStoreLayout.setOnClickListener(this);
		
		
	}

	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent();
		switch (v.getId()) {
		
		// �˻���ȫ
		case R.id.slide_layout_account_safe:
			
			break;
		// �˻���
		case R.id.slide_layout_account_bangding:
						
			break;
		// ����ģʽ
		case R.id.slide_layout_hiding_mode:
						
			break;
		// ��Ϣ����
		case R.id.slide_layout_message_notify:
			intent.setClass(SlideSetActivity.this, SetMessageNotifyActivity.class);		
			startActivity(intent);
			break;
		// ����װ��
		case R.id.slide_layout_person_dress:
						
			break;
		// �����̳�
		case R.id.slide_layout_emoj_store:
						
			break;
		}
		
		
	}
}
