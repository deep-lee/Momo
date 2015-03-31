package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.view.BoardView;
import com.bmob.im.demo.view.GameView;
import com.bmob.im.demo.view.OnStateListener;
import com.bmob.im.demo.view.OnTimerListener;
import com.bmob.im.demo.view.OnToolsChangeListener;
import com.bmob.im.demo.view.dialog.MyDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class GameFruitActivity extends Activity implements OnClickListener, OnTimerListener,OnStateListener,OnToolsChangeListener{
	
	private ImageButton btnPlay;
	private ImageButton btnRefresh;
	private ImageButton btnTip;
	private ImageView imgTitle;
	private GameView gameView;
	private SeekBar progress;
	private MyDialog dialog;
	private ImageView clock;
	private TextView textRefreshNum;
	private TextView textTipNum;
	
	private MediaPlayer player;
	
	String username = "";
	
	String from;
	
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				// 赢了就可以进入个人信息页面
				dialog = new MyDialog(GameFruitActivity.this,gameView,"你赢了",gameView.getTotalTime() - progress.getProgress(),true, from, username);
				dialog.show();
				break;
			case 1:
				// 输了再次返回地图界面
				dialog = new MyDialog(GameFruitActivity.this,gameView,"你输了",gameView.getTotalTime() - progress.getProgress(),false, from, username);
				dialog.show();
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
		
		from = data.getString("from");
		
		if (from.equals("other")) {
			username = data.getString("username");
		}
		
		 btnPlay = (ImageButton) findViewById(R.id.play_btn);
	     btnRefresh = (ImageButton) findViewById(R.id.refresh_btn);
	     btnTip = (ImageButton) findViewById(R.id.tip_btn);
	     imgTitle = (ImageView) findViewById(R.id.title_img);
	     gameView = (GameView) findViewById(R.id.game_view);
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

}
