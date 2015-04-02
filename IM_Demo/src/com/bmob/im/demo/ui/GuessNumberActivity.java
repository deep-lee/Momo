package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.SoundPlay;
import com.bmob.im.demo.view.dialog.MyDialog;

import A.thing;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
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
	
	String from;
	String username;
	
	TextView lefTextView, trueOrFalseView;
	
	public MediaPlayer player;
	public static SoundPlay soundPlay;
	
	int gamedifficulty = 1;
	
	int totalTimes = 5;
	
	private int[] numBtuId = {
		R.id.game_guess_zore, R.id.game_guess_one, R.id.game_guess_two, 
		R.id.game_guess_three, R.id.game_guess_four, R.id.game_guess_five, 
		R.id.game_guess_six, R.id.game_guess_theven, R.id.game_guess_eight,
		R.id.game_guess_nine
	};
	
	public static final int ID_SOUND_AO = 0;
	public static final int ID_SOUND_LOSE = 1;
	public static final int ID_SOUND_WIN = 2;


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
		
		Bundle data = getIntent().getExtras();
		
		from = data.getString("from");
		
		if (from.equals("other")) {
			username = data.getString("username");
			gamedifficulty = data.getInt("gamedifficulty");
			
			switch (gamedifficulty) {
			case 0:
				setTotalTimes(10);
				break;
			case 1:
				setTotalTimes(8);
				break;
			case 2:
				setTotalTimes(5);
				break;

			default:
				break;
			}
		}
		
		play.setOnClickListener(this);
		clear.setOnClickListener(this);
		sure.setOnClickListener(this);
		
		player = MediaPlayer.create(this, R.raw.back2new); 
		
		initSound(this);
		
		
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
			
			if (numHasInput == 0) {
				return;
			}else {
				guessTime++;
				if(guessTime <= totalTimes)
				{
					guessNum = Integer.parseInt(input.getText().toString());
					caculate();
				}
			}
			break;


		default:
			break;
		}
	}
	
	public static void initSound(Context context){
		 soundPlay = new SoundPlay();
	        soundPlay.initSounds(context);
	        soundPlay.loadSfx(context, R.raw.audio_ao, ID_SOUND_AO);
	        soundPlay.loadSfx(context, R.raw.audio_boos, ID_SOUND_LOSE);
	        soundPlay.loadSfx(context, R.raw.audio_congratulations, ID_SOUND_WIN);
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
		if (guessTime <= totalTimes) {
			if (guessNum > randomNum) {
				if (guessTime == totalTimes) {
					showDialog(totalTimes - guessTime, 1, true);
				}
				else {
					showDialog(totalTimes - guessTime, 1, false);
				}
			}
			else if (guessNum < randomNum) {
				if (guessTime == totalTimes) {
					showDialog(totalTimes - guessTime, -1, true);
				}
				else {
					showDialog(totalTimes - guessTime, -1, false);
				}
			}
			else {
				if (guessTime == totalTimes) {
					showDialog(totalTimes - guessTime, 0, true);
				}
				else {
					showDialog(totalTimes - guessTime, 0, false);
				}
			}
		}
	}
	
	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	private void showDialog(int leftTime, final int isBigger, final Boolean isOver) {
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
					showQuitDialog(isOver,isBigger);
					dialog.dismiss();
				}
			});
			
			lefTextView.setText("你还有" + leftTime + "机会");
			if (isBigger == 1) {
				trueOrFalseView.setText("你猜大了");
				soundPlay.play(ID_SOUND_AO, 0);
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
				soundPlay.play(ID_SOUND_AO, 0);
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
				soundPlay.play(ID_SOUND_WIN, 0);
				
				if (from.equals("me")) {
					dialog.setNegativeButton("再来一局", new AlertDialog.OnClickListener(){

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
				else if(from.equals("other")){
					dialog.setNegativeButton("查看资料", new AlertDialog.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(GuessNumberActivity.this, SetMyInfoActivity.class);
							intent.putExtra("from", "add");
							intent.putExtra("username", username);
							
							startActivity(intent);
							
							finish();
						}
						
					});
				}
			}
		}
		else {
			
			dialog.setPositiveButton("退出游戏", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showQuitDialog(isOver,isBigger);
					dialog.dismiss();
				}
			});
			
			if(isBigger == 0){
				lefTextView.setVisibility(View.INVISIBLE);
				trueOrFalseView.setText("你猜对了！");
				soundPlay.play(ID_SOUND_WIN, 0);
				
				if (from.equals("me")) {
					dialog.setNegativeButton("再来一局", new AlertDialog.OnClickListener(){

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
				else if(from.equals("other")){
					dialog.setNegativeButton("查看资料", new AlertDialog.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							Intent intent = new Intent();
							intent.setClass(GuessNumberActivity.this, SetMyInfoActivity.class);
							intent.putExtra("from", "add");
							intent.putExtra("username", username);
							
							startActivity(intent);
							
							finish();
						}
						
					});
				}
			}
			else {
				lefTextView.setVisibility(View.INVISIBLE);
				trueOrFalseView.setText("你输了！");
				
				soundPlay.play(ID_SOUND_LOSE, 0);
				
				if (from.equals("me")) {
					dialog.setNegativeButton("再来一次", new AlertDialog.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							input.setText("");
							numHasInput = 0;
							createRandomNum();
							guessTime = 0;
							
						}
						
					});
					
				}else if (from.equals("other")) {
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
		}
		
		AlertDialog result = dialog.create();
		result.setCancelable(false);
		result.setCanceledOnTouchOutside(false);
		result.show();
		
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	final Dialog dialog = new AlertDialog.Builder(this)
            .setIcon(R.drawable.buttons_bg20)
            .setTitle(R.string.quit)
            .setMessage(R.string.sure_quit)
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					quit();
				}

            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int whichButton) {
                	
                }
            })
            .create();
			dialog.show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
	
	private void showQuitDialog(final Boolean isOver, final int isBigger) {
		
		final Dialog dialog1 = new AlertDialog.Builder(this).setIcon(R.drawable.buttons_bg20)
	            .setTitle(R.string.quit)
	            .setMessage(R.string.sure_quit)
	            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						quit();
					}

	            })
	            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface arg0, int whichButton) {
	                	if (!isOver) {
							if (isBigger == 0) {
								input.setText("");
								numHasInput = 0;
								createRandomNum();
								guessTime = 0;
							}
							else {
								input.setText("");
								numHasInput = 0;
							}
						}
	                	else {
	                		input.setText("");
							numHasInput = 0;
							createRandomNum();
							guessTime = 0;
						}
	                }
	            })
	            .create();
				dialog1.show();
	}
	
}
