package com.deep.momo.game.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.ActivityBase;
import com.bmob.im.demo.ui.SetMyInfoActivity2;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.momo.game.view.GameView;
import com.deep.momo.game.view.OnStateListener;
import com.deep.momo.game.view.OnTimerListener;
import com.deep.momo.game.view.OnToolsChangeListener;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class GameFruitActivity extends ActivityBase implements OnClickListener, OnTimerListener,OnStateListener,OnToolsChangeListener{
	
	private ImageButton btnPlay;
	private ImageButton btnRefresh;
	private ImageButton btnTip;
	private ImageView imgTitle;
	private GameView gameView;
	private SeekBar progress;
	private ImageView clock;
	private TextView textRefreshNum;
	private TextView textTipNum;
	
	private MediaPlayer player;
	
	String username = "";
	
	String from;
	
	String gamedifficulty = "简单";
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				// 赢了就可以进入个人信息页面
//				dialog = new MyDialog(GameFruitActivity.this,gameView,"你赢了",gameView.getTotalTime() - progress.getProgress(),true, from, username);
//				dialog.show();
				
				showWinDialog();
				
				break;
			case 1:
				// 输了再次返回地图界面
//				dialog = new MyDialog(GameFruitActivity.this,gameView,"你输了",gameView.getTotalTime() - progress.getProgress(),false, from, username);
//				dialog.show();
				
				showFailDialog();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//设置无标题  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game_fruit);
		
		Bundle data = getIntent().getExtras();
		
		
		
		 btnPlay = (ImageButton) findViewById(R.id.play_btn);
	     btnRefresh = (ImageButton) findViewById(R.id.refresh_btn);
	     btnTip = (ImageButton) findViewById(R.id.tip_btn);
	     imgTitle = (ImageView) findViewById(R.id.title_img);
	     gameView = (GameView) findViewById(R.id.game_view);
	     
	     from = data.getString("from");
			
			if (from.equals("other")) {
				username = data.getString("username");
				gamedifficulty = data.getString("gamedifficulty");
				
				if (gamedifficulty.equals("简单")) {
					gameView.setTotalTime(80);
					
				}else if (gamedifficulty.equals("一般")) {
					gameView.setTotalTime(60);
					
				}else if (gamedifficulty.equals("困难")) {
					gameView.setTotalTime(40);
				}
				
			}
	     
	     clock = (ImageView) findViewById(R.id.clock);
	     progress = (SeekBar) findViewById(R.id.timer);
	     textRefreshNum = (TextView) findViewById(R.id.text_refresh_num);
	     textTipNum = (TextView) findViewById(R.id.text_tip_num);
	     //XXX
	     progress.setMax(gameView.getTotalTime());
	        
	     btnPlay.setOnClickListener(this);
	     btnRefresh.setOnClickListener(this);
	     btnTip.setOnClickListener(this);
	     gameView.setOnTimerListener(this);
	     gameView.setOnStateListener(this);
	     gameView.setOnToolsChangedListener(this);
	     GameView.initSound(this);
	     
	   
	     
	     Animation scale = AnimationUtils.loadAnimation(this,R.anim.scale_anim);
	     imgTitle.startAnimation(scale);
	     btnPlay.startAnimation(scale);
	        
	     player = MediaPlayer.create(this, R.raw.bg);
	     player.setLooping(true);
	     player.start();
		
	}
	
	private void showFailDialog() {
		
		if (from.equals("me")) {
			
			DialogTips dialogTips = new DialogTips(GameFruitActivity.this, "你输了", "再试一次", "退出", "结果", false);
			dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					gameView.startPlay();
				}
			});
			
			dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showQuitDialog(false);
				}
			});
			
			dialogTips.show();
			dialogTips = null;
			
		}else if (from.equals("other")) {
			DialogTips dialogTips = new DialogTips(GameFruitActivity.this, "你输了", "再试一次", "退出", "结果", false);
			dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					gameView.startPlay();
				}
			});
			
			dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showQuitDialog(false);
				}
			});
			
			dialogTips.show();
			dialogTips = null;
		}
		
	}
	
	private void showWinDialog() {
		if (from.equals("me")) {
			
			DialogTips dialogTips = new DialogTips(GameFruitActivity.this, "你赢了", "增加难度", "退出", "结果", false);
			dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					gameView.startNextPlay();
				}
			});
			
			dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showQuitDialog(true);
				}
			});
			
			dialogTips.show();
			dialogTips = null;
			
		}else if (from.equals("other")) {
			DialogTips dialogTips = new DialogTips(GameFruitActivity.this, "你赢了", "查看资料", "退出", "结果", false);
			dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					gotoMomo();
					quit();
				}
			});
			
			dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showQuitDialog(true);
				}
			});
			
			dialogTips.show();
			dialogTips = null;
		}
	}
	
	private void gotoMomo() {
		Intent intent = new Intent();
		intent.setClass(GameFruitActivity.this, SetMyInfoActivity2.class);
		
		intent.putExtra("from", "add");
		intent.putExtra("username", username);
		
		startActivity(intent);
	}
	
	@Override
    protected void onPause() {
    	super.onPause();
    	gameView.setMode(GameView.PAUSE);
    }
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	gameView.setMode(GameView.QUIT);
	}

	public void quit() {
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
    	case R.id.play_btn:
    		Animation scaleOut = AnimationUtils.loadAnimation(this,R.anim.scale_anim_out);
        	Animation transIn = AnimationUtils.loadAnimation(this,R.anim.trans_in);
    		
    		btnPlay.startAnimation(scaleOut);
    		btnPlay.setVisibility(View.GONE);
    		imgTitle.setVisibility(View.GONE);
    		gameView.setVisibility(View.VISIBLE);
    		
    		btnRefresh.setVisibility(View.VISIBLE);
    		btnTip.setVisibility(View.VISIBLE);
    		progress.setVisibility(View.VISIBLE);
    		clock.setVisibility(View.VISIBLE);
    		textRefreshNum.setVisibility(View.VISIBLE);
    		textTipNum.setVisibility(View.VISIBLE);
    		
    		btnRefresh.startAnimation(transIn);
    		btnTip.startAnimation(transIn);
    		gameView.startAnimation(transIn);
    		player.pause();
    		gameView.startPlay();
    		break;
    	case R.id.refresh_btn:
    		Animation shake01 = AnimationUtils.loadAnimation(this,R.anim.shake);
    		btnRefresh.startAnimation(shake01);
    		gameView.refreshChange();
    		break;
    	case R.id.tip_btn:
    		Animation shake02 = AnimationUtils.loadAnimation(this,R.anim.shake);
    		btnTip.startAnimation(shake02);
    		gameView.autoClear();
    		break;
    	}
	}

	@Override
	public void onRefreshChanged(int count) {
		// TODO Auto-generated method stub
		textRefreshNum.setText(""+gameView.getRefreshNum());
	}

	@Override
	public void onTipChanged(int count) {
		// TODO Auto-generated method stub
		textTipNum.setText(""+gameView.getTipNum());
	}

	@Override
	public void OnStateChanged(int StateMode) {
		// TODO Auto-generated method stub
		switch(StateMode){
		case GameView.WIN:
			handler.sendEmptyMessage(0);
			break;
		case GameView.LOSE:
			handler.sendEmptyMessage(1);
			break;
		case GameView.PAUSE:
			player.stop();
	    	gameView.player.stop();
	    	gameView.stopTimer();
			break;
		case GameView.QUIT:
			player.release();
	    	gameView.player.release();
	    	gameView.stopTimer();
	    	break;
		}
	}

	@Override
	public void onTimer(int leftTime) {
		// TODO Auto-generated method stub
		Log.i("onTimer", leftTime+"");
		progress.setProgress(leftTime);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	DialogTips dialogTips = new DialogTips(GameFruitActivity.this, "确认退出游戏？", "确认", "取消", "退出", false);
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
	
	private void showQuitDialog(final Boolean flag) {
		
		DialogTips dialogTips = new DialogTips(GameFruitActivity.this, "确认退出？", "确认", "取消", "退出", false);
		
		dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				quit();
			}
		});
		
		dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				if (flag) {
					if (from.equals("me")) {
						gameView.startNextPlay();
					}
					else if (from.equals("other")) {
						gameView.startPlay();
					}
				}else {
					gameView.startPlay();
				}
			}
		});
		
		dialogTips.show();
	}

}
