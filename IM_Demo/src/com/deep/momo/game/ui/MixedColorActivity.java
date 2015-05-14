package com.deep.momo.game.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.ActivityBase;
import com.bmob.im.demo.view.dialog.DialogTips;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class MixedColorActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_mixed_color);
		
	}
	
	public void quit() {
		this.finish();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	DialogTips dialogTips = new DialogTips(MixedColorActivity.this, "确认退出游戏？", "确认", "取消", "退出", false);
        	dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					quit();
				}
			});
        	dialogTips.show();
        	dialogTips = null;
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
