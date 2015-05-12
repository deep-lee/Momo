package com.deep.momo.game.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.ActivityBase;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MyGameActivity extends ActivityBase implements OnClickListener{
	
	View game1, game2, game3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_game);
		
		initView();
	}
	
	private void initView() {
		initTopBarForLeft("Œ“µƒ”Œœ∑");
		game1 = findViewById(R.id.layout_game_1);
		game2 = findViewById(R.id.layout_game_2);
		game3 = findViewById(R.id.layout_game_3);
		
		game1.setOnClickListener(this);
		game2.setOnClickListener(this);
		game3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_game_1:
			Intent intent1 = new Intent();
			intent1.setClass(MyGameActivity.this, GameFruitActivity.class);
			Bundle data = new Bundle();
			data.putString("from", "me");
			intent1.putExtras(data);
			startActivity(intent1);
			break;

		case R.id.layout_game_2:
			
			Intent intent2 = new Intent();
			intent2.setClass(MyGameActivity.this, GuessNumberActivity.class);
			Bundle data2 = new Bundle();
			data2.putString("from", "me");
			intent2.putExtras(data2);
			startActivity(intent2);
			
			break;
		case R.id.layout_game_3:

			Intent intent3 = new Intent();
			intent3.setClass(MyGameActivity.this, MixedColorMenuActivity.class);
			Bundle data3 = new Bundle();
			data3.putString("from", "me");
			intent3.putExtras(data3);
			startActivity(intent3);
			
			break;
		default:
			break;
		}
	}
}
