package com.bmob.im.demo.ui;

import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.User;

import a.thing;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HidingModeActivity extends BaseActivity implements OnClickListener{
	
	ImageView iv_readable_to_everyone, iv_unreadable_to_unkwone_people;
	RelativeLayout rl_readable_to_everyone, rl_unreadable_to_unkwone_people;
	
	Drawable readable_to_everyone_unselected;
	Drawable readable_to_everyone_selected;
	Drawable unreadable_to_unkwone_people_unselected;
	Drawable unreadable_to_unkwone_people_selected;
	
	private User currentUser;
	
	private Boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hiding_mode);
		
		initView();
	}
	
	public void initView() {
		currentUser = CustomApplcation.getInstance().getCurrentUser();
		
		readable_to_everyone_unselected = getResources().getDrawable(R.drawable.register_sex_select_n);
		unreadable_to_unkwone_people_unselected = getResources().getDrawable(R.drawable.register_sex_select_n);
		readable_to_everyone_selected = getResources().getDrawable(R.drawable.register_sex_select_p);
		unreadable_to_unkwone_people_selected = getResources().getDrawable(R.drawable.register_sex_select_p);
		
		iv_readable_to_everyone = (ImageView) findViewById(R.id.hiding_mode_readable_to_everyone_selector);
		iv_unreadable_to_unkwone_people = (ImageView) findViewById(R.id.hiding_mode_unreadable_to_unknowkn_people_selector);
		
		rl_readable_to_everyone = (RelativeLayout) findViewById(R.id.hiding_mode_readable_tp_everyone_layout);
		rl_unreadable_to_unkwone_people = (RelativeLayout) findViewById(R.id.hiding_mode_unreadable_to_unknowkn_people);
		
		if (currentUser == null) {
			ShowToast(R.string.network_tips);
		}
		
		flag = currentUser.getQiangYuStatus();
		
		if (flag == null) {
			ShowToast(R.string.network_tips);
			flag = true;
		}
		
		// 如果是对所有人可见的
		if (flag) {
			iv_readable_to_everyone.setImageDrawable(readable_to_everyone_selected);
			iv_unreadable_to_unkwone_people.setImageDrawable(unreadable_to_unkwone_people_unselected);
		}else {
			iv_readable_to_everyone.setImageDrawable(readable_to_everyone_unselected);
			iv_unreadable_to_unkwone_people.setImageDrawable(unreadable_to_unkwone_people_selected);
		}
		
		rl_readable_to_everyone.setOnClickListener(this);
		rl_unreadable_to_unkwone_people.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hiding_mode_readable_tp_everyone_layout:
			if (!flag) {
				flag = true;
				iv_readable_to_everyone.setImageDrawable(readable_to_everyone_selected);
				iv_unreadable_to_unkwone_people.setImageDrawable(unreadable_to_unkwone_people_unselected);
				
				// 更新用户资料
				User user = new User();
				user.setQiangYuStatus(flag);
				
				updateUserData(user, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowToast("更新成功！");
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("更新失败！请检查网络链接！");
					}
				});
			}
			break;
			
		case R.id.hiding_mode_unreadable_to_unknowkn_people:
			if (flag) {
				flag = false;
				iv_readable_to_everyone.setImageDrawable(readable_to_everyone_unselected);
				iv_unreadable_to_unkwone_people.setImageDrawable(unreadable_to_unkwone_people_selected);
				
				// 更新用户资料
				User user = new User();
				user.setQiangYuStatus(flag);
				
				updateUserData(user, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowToast("更新成功！");
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("更新失败！请检查网络链接！");
					}
				});
			}
			break;

		default:
			break;
		}
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
