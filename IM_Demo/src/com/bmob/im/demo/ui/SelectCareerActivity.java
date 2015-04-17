package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.adapter.CareerAdapter;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class SelectCareerActivity extends Activity {
	
	EditText et_career;
	ListView lv_list_career;
	CareerAdapter careerAdapter;
	
	int currentSelect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_career);
		
		et_career = (EditText) findViewById(R.id.edit_my_info_career_select_et);
		lv_list_career = (ListView) findViewById(R.id.edit_my_info_career_select_listview);
	
		currentSelect = getIntent().getIntExtra("careerType", 11);
		
		careerAdapter = new CareerAdapter(SelectCareerActivity.this,currentSelect);
		
		lv_list_career.setAdapter(careerAdapter);
		
		lv_list_career.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
