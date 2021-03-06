package com.deep.momo.game.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.ActivityBase;
import com.bmob.im.demo.view.dialog.DialogTips;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class MixedColorMenuActivity extends Activity implements OnClickListener {



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_mixed_color_menu);

		ImageButton startButton = (ImageButton) findViewById(R.id.game_mixed_color_play);
		startButton.setOnClickListener(this);


	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.game_mixed_color_play:
			i = new Intent(this, MixedColorActivity.class);
			i.putExtras(getIntent().getExtras());
			break;
		}
		if (i != null) {
			startActivity(i);
			finish();
		}
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	DialogTips dialogTips = new DialogTips(MixedColorMenuActivity.this, "确认退出游戏？", "确认", "取消", "退出", false);
        	dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();;
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
