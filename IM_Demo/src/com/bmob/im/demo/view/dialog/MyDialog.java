package com.bmob.im.demo.view.dialog;


import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.GameFruitActivity;
import com.bmob.im.demo.view.GameView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyDialog extends Dialog implements OnClickListener{

	private GameView gameview;
	private Context context;
	

	public MyDialog(Context context, GameView gameview, String msg, int time, Boolean isWin) {
		super(context,R.style.dialog);
		this.gameview = gameview;
		this.context = context;
		this.setContentView(R.layout.dialog_view);
		TextView text_msg = (TextView) findViewById(R.id.text_message);
		TextView text_time = (TextView) findViewById(R.id.text_time);
		ImageButton btn_menu = (ImageButton) findViewById(R.id.menu_imgbtn);
		ImageButton btn_next = (ImageButton) findViewById(R.id.next_imgbtn);
		ImageButton btn_replay = (ImageButton) findViewById(R.id.replay_imgbtn);
		
		text_msg.setText(msg);
		text_time.setText(text_time.getText().toString().replace("$", String.valueOf(time)));
		btn_menu.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btn_replay.setOnClickListener(this);
		this.setCancelable(false);
		
		if (isWin) {
			btn_replay.setVisibility(View.INVISIBLE);
		}
		else {
			btn_next.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch(v.getId()){
		
		// 退出到地图界面
		case R.id.menu_imgbtn:
			final Dialog dialog = new AlertDialog.Builder(context)
            .setIcon(R.drawable.buttons_bg20)
            .setTitle(R.string.quit)
            .setMessage(R.string.sure_quit)
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					((GameFruitActivity)context).quit();
				}

            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int whichButton) {
                	gameview.startPlay();
                }
            })
            .create();
			dialog.show();
			break;
			// 重玩
		case R.id.replay_imgbtn:
			gameview.startPlay();
			break;
			// 进入陌生人信息界面
		case R.id.next_imgbtn:
			// gameview.startNextPlay();
			
			
			break;
		}
	}
}
