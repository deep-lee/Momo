package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.view.dialog.MyDialog;

import A.thing;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputBinding;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuessNumberActivity extends Activity implements OnClickListener{
	
	EditText input;
	Button[] numberBtn = new Button[10];
	Button clear;
	Button sure;
	
	View showIcon, mainView;
	ImageButton play;
	
	int randomNum = 0;
	int guessNum = 0;
	int guessTime = 0;
	
	int numHasInput = 0;
	int requireNum = 2;
	
	TextView lefTextView, trueOrFalseView;
	
	private int[] numBtuId = {
		R.id.game_guess_zore, R.id.game_guess_one, R.id.game_guess_two, 
		R.id.game_guess_three, R.id.game_guess_four, R.id.game_guess_five, 
		R.id.game_guess_six, R.id.game_guess_theven, R.id.game_guess_eight,
		R.id.game_guess_nine
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_guess_number);
		
		initView();
		createRandomNum();
		
	}
	
	private void initView() {
		input = (EditText) findViewById(R.id.game_guess_number_input);
		
		showIcon = findViewById(R.id.game_icon);
		mainView = findViewById(R.id.game_main_view);
		
		play = (ImageButton) findViewById(R.id.game_guess_number_play_btn);
		clear = (Button) findViewById(R.id.game_guess_clear);
		sure = (Button) findViewById(R.id.game_guess_sure);
		
		mainView.setVisibility(View.INVISIBLE);
		showIcon.setVisibility(View.VISIBLE);
		
		play.setOnClickListener(this);
		clear.setOnClickListener(this);
		sure.setOnClickListener(this);
		
		
		Animation scale = AnimationUtils.loadAnimation(this,R.anim.scale_anim);
		showIcon.startAnimation(scale);
		
		for (int i = 0; i < 10; i++) {
			numberBtn[i] = (Button) findViewById(numBtuId[i]);
			numberBtn[i].setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.game_guess_clear:
			input.setText("");
			numHasInput = 0;
			break;
		case R.id.game_guess_zore:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "0");
			}
			numHasInput++;
			break;
		case R.id.game_guess_one:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "1");
			}
			numHasInput++;
			break;
		case R.id.game_guess_two:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "2");
			}
			numHasInput++;
			break;
		case R.id.game_guess_three:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "3");
			}
			numHasInput++;
			break;
		case R.id.game_guess_four:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "4");
			}
			numHasInput++;
			break;
		case R.id.game_guess_five:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "5");
			}
			numHasInput++;
			break;
		case R.id.game_guess_six:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "6");
			}
			numHasInput++;
			break;
		case R.id.game_guess_theven:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "7");
			}
			numHasInput++;
			break;
		case R.id.game_guess_eight:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "8");
			}
			numHasInput++;
			break;
		case R.id.game_guess_nine:
			if (numHasInput < requireNum) {
				input.setText(input.getText().toString() + "9");
			}
			numHasInput++;
			break;
			
		case R.id.game_guess_number_play_btn:
			Animation scaleOut = AnimationUtils.loadAnimation(this,R.anim.scale_anim_out);
        	Animation transIn = AnimationUtils.loadAnimation(this,R.anim.trans_in);
        	showIcon.startAnimation(scaleOut);
        	showIcon.setVisibility(View.INVISIBLE);
        	
        	mainView.setVisibility(View.VISIBLE);
        	mainView.startAnimation(transIn);
        	
        	break;
        	
		case R.id.game_guess_sure:
			guessTime++;
			if(guessTime <= 5)
			{
				guessNum = Integer.parseInt(input.getText().toString());
				caculate();
			}
			
			break;


		default:
			break;
		}
	}
	
	
	public void quit() {
		// TODO Auto-generated method stub
		this.finish();
	}
	
	public int getGuessTime() {
		return guessTime;
	}

	public void setGuessTime(int guessTime) {
		this.guessTime = guessTime;
	}

	public void createRandomNum() {
		randomNum = (int)(Math.random() * 100);
		
		Toast.makeText(GuessNumberActivity.this, "" + randomNum, Toast.LENGTH_LONG).show();
	}
	
	private void caculate() {
		if (guessTime <= 5) {
			if (guessNum > randomNum) {
				if (guessTime == 5) {
					showDialog(5 - guessTime, 1, true);
				}
				else {
					showDialog(5 - guessTime, 1, false);
				}
			}
			else if (guessNum < randomNum) {
				if (guessTime == 5) {
					showDialog(5 - guessTime, -1, true);
				}
				else {
					showDialog(5 - guessTime, -1, false);
				}
			}
			else {
				if (guessTime == 5) {
					showDialog(5 - guessTime, 0, true);
				}
				else {
					showDialog(5 - guessTime, 0, false);
				}
			}
		}
	}
	
	private void showDialog(int leftTime, int isBigger, Boolean isOver) {
		LinearLayout showResult = (LinearLayout) getLayoutInflater().inflate(R.layout.guess_dialog, null);
		lefTextView = (TextView) showResult.findViewById(R.id.guess_dialog_left_time);
		trueOrFalseView = (TextView) showResult.findViewById(R.id.guess_dialog_true_or_false);
		
		Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("猜测结果");
		dialog.setView(showResult);
		
		
		if (!isOver) {
			
			dialog.setPositiveButton("退出游戏", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					quit();
				}
			});
			
			lefTextView.setText("你还有" + leftTime + "机会");
			if (isBigger == 1) {
				trueOrFalseView.setText("你猜大了");
				dialog.setNegativeButton("继续猜测", new AlertDialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						input.setText("");
						numHasInput = 0;
					}
					
				});
			}
			else if (isBigger == -1) {
				trueOrFalseView.setText("你猜小了");
				dialog.setNegativeButton("继续猜测", new AlertDialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						input.setText("");
						numHasInput = 0;
					}
					
				});
			}
			else if(isBigger == 0){
				lefTextView.setVisibility(View.INVISIBLE);
				trueOrFalseView.setText("你猜对了！");
				
				dialog.setNegativeButton("查看资料", new AlertDialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		}
		else {
			
			dialog.setPositiveButton("退出游戏", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					quit();
				}
			});
			
			if(isBigger == 0){
				lefTextView.setVisibility(View.INVISIBLE);
				trueOrFalseView.setText("你猜对了！");
				
				dialog.setNegativeButton("查看资料", new AlertDialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
			else {
				lefTextView.setVisibility(View.INVISIBLE);
				trueOrFalseView.setText("你输了！");
				
				dialog.setNegativeButton("重新开始", new AlertDialog.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						input.setText("");
						numHasInput = 0;
						createRandomNum();
						guessTime = 0;
						
					}
					
				});
			}
		}
		
		AlertDialog result = dialog.create();
		result.setCancelable(false);
		result.setCanceledOnTouchOutside(false);
		result.show();
		
	}
}
