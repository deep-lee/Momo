package com.bmob.im.demo.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.EmoViewPagerAdapter;
import com.bmob.im.demo.adapter.EmoteAdapter;
import com.bmob.im.demo.adapter.MessageChatAdapter;
import com.bmob.im.demo.adapter.NewRecordPlayClickListener;
import com.bmob.im.demo.bean.FaceText;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.AudioUtils;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.FaceTextUtils;
import com.bmob.im.demo.util.ScrollUtils;
import com.bmob.im.demo.view.EmoticonsEditText;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.xlist.XListView;
import com.bmob.im.demo.view.xlist.XListView.IXListViewListener;

/**
 * 聊天界面
 * 
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-3 下午4:33:11
 */
/**
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-23 下午3:28:49
 */
@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
public class ChatActivity extends BaseMainActivity implements OnClickListener,
		IXListViewListener, EventListener {

	// 表情 发送 添加 键盘 语音 
	private Button btn_chat_emo, btn_chat_send, btn_chat_add,btn_chat_keyboard, btn_chat_voice;

	XListView mListView;
	
	private Handler mHandler = new Handler();
	
	ImageView iv_voice;
	private ViewGroup trashcan;
	ImageView gai;
	private ViewGroup voice_note_layout;
	private TextView mRecordTime;
	private FrameLayout scroller_view;
	private View ivVoiceCancel;
	private String mTimerFormat;
	Animation cancle_animation;
	int pop_flag = 0;
	Animation mic_fling;
	Animation trash_lid_start;
	Animation slide_in_right;
	Animation slide_in_left;
	Animation slide_out_left;
	Animation slide_out_right;
	private long startVoiceT, endVoiceT;
	private Animation push_up_in;
	private Animation push_up_out;
	private Animation grow_from_top;
	private Animation mic_twinkle;
	
	/**
	 * 滑动类
	 */
	private Scroller scroller;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	
	/**
	 * 手指按下X的坐标
	 */
	private int downY;
	/**
	 * 手指按下Y的坐标
	 */
	private int downX;
	private ScrollUtils utils ;
	int raw_x ;
	private boolean flagmv=false;
	private AudioUtils audioUtils;
	private int flag = 1;
	
	TextView tv_title;

	// 可输入表情的输入框
	EmoticonsEditText edit_user_comment;
	
	private View main_chat_bg;

	// 聊天对象的Id
	String targetId = "";
	
	// 聊天对象
	BmobChatUser targetUser;

	// 消息有多少页
	private static int MsgPagerNum;

	// 更多界面（发图片 地理位置信息）、表情界面 更多界面
	private LinearLayout layout_more, layout_emo, layout_add;

	// 表情界面多页滑动
	private ViewPager pager_emo;

	// 更多界面的图片、照相、位置选项
	private TextView tv_picture, tv_camera, tv_location;

	// 语音有关
	RelativeLayout layout_record;
	TextView tv_voice_tips;
	ImageView iv_record;

	// 话筒动画
	private Drawable[] drawable_Anims;

	// 聊天纪录管理
	BmobRecordManager recordManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 默认不弹出软键盘
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC); //注册的默认 音频通道
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		
		screenWidth = dm.widthPixels;
		scroller = new Scroller(this);
		utils = new ScrollUtils();
		audioUtils = new AudioUtils(this);
		
		// 聊天管理
		manager = BmobChatManager.getInstance(this);
		MsgPagerNum = 0;
		
		// 组装聊天对象
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		if (targetUser != null) {
			targetId = targetUser.getObjectId();
		}
		
		//注册广播接收器
		initNewMessageBroadCast();
		initView();
	}
	
	private void initRecordManager(){
		// 语音相关管理器
		recordManager = BmobRecordManager.getInstance(this);
		// 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {
	
			@Override
			public void onVolumnChanged(int value) {
				// TODO Auto-generated method stub
				iv_record.setImageDrawable(drawable_Anims[value]);
			}
	
			/*
			 * localPath表示录音的本地位置
			 * recordTime表示录音的时间
			 * @see cn.bmob.im.inteface.OnRecordChangeListener#onTimeChanged(int, java.lang.String)
			 */
			@Override
			public void onTimeChanged(int recordTime, String localPath) {
				// TODO Auto-generated method stub
				BmobLog.i("voice", "已录音长度:" + recordTime);
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
					// 需要重置按钮
//					btn_speak.setPressed(false);
//					btn_speak.setClickable(false);
					// 取消录音框
					layout_record.setVisibility(View.INVISIBLE);
					// 发送语音消息
					sendVoiceMessage(localPath, recordTime);
					// 是为了防止过了录音时间后，会多发一条语音出去的情况。
					handler.postDelayed(new Runnable() {
	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							// btn_speak.setClickable(true);
						}
					}, 1000);
				}else{
					
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mListView = (XListView) findViewById(R.id.mListView);
		
		main_chat_bg = findViewById(R.id.chat_main_bg);
		
		if (CustomApplcation.chatBgAddress.equals("default")) {
			main_chat_bg.setBackgroundColor(getResources().getColor(R.color.theme_bg_color));
		}
		else {
			File mFile = new File(CustomApplcation.chatBgAddress);
	        //若该文件存在
	        if (mFile.exists()) {
	            Bitmap bitmap = BitmapFactory.decodeFile(CustomApplcation.chatBgAddress);
	            main_chat_bg.setBackground(new BitmapDrawable(bitmap));
	        }
	        else {
				main_chat_bg.setBackgroundColor(getResources().getColor(R.color.theme_bg_color));
			}
		}
		
		tv_title = (TextView) findViewById(R.id.tv_title);
		if (targetUser != null) {
			tv_title.setText(targetUser.getNick());
		}
		
		
		// 初始化界面下方的控件
		initBottomView();
		
		initAnimation();
		
		// 初始化聊天纪录的XListView
		initXListView();
		
		// 初始化语音布局
		initVoiceView();
	}

	/**
	 * 初始化语音布局
	 * 
	 * @Title: initVoiceView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceView() {
		layout_record = (RelativeLayout) findViewById(R.id.layout_record);
		tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
		iv_record = (ImageView) findViewById(R.id.iv_record);
		// btn_speak.setOnTouchListener(new VoiceTouchListen());
		
		
		// 初始化语音动画资源
		initVoiceAnimRes();
		// 
		initRecordManager();
	}

	/**
	 * 长按说话
	 * @ClassName: VoiceTouchListen
	 * @Description: TODO
	 * @author smile
	 * @date 2014-7-1 下午6:10:16
	 */
	class VoiceTouchListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.checkSdCard()) {
					ShowToast("发送语音需要sdcard支持！");
					return false;
				}
				try {
					v.setPressed(true);
					layout_record.setVisibility(View.VISIBLE);
					tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
					// 开始录音，参数事聊天对象的Id
					recordManager.startRecording(targetId);
				} catch (Exception e) {
				}
				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					tv_voice_tips
							.setText(getString(R.string.voice_cancel_tips));
					tv_voice_tips.setTextColor(Color.RED);
				} else {
					tv_voice_tips.setText(getString(R.string.voice_up_tips));
					tv_voice_tips.setTextColor(Color.WHITE);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				layout_record.setVisibility(View.INVISIBLE);
				try {
					if (event.getY() < 0) {// 放弃录音
						recordManager.cancelRecording();
						BmobLog.i("voice", "放弃发送语音");
					} else {
						int recordTime = recordManager.stopRecording();
						if (recordTime > 1) {
							// 发送语音文件
							BmobLog.i("voice", "发送语音");
							sendVoiceMessage(
									recordManager.getRecordFilePath(targetId),
									recordTime);
						} else {// 录音时间过短，则提示录音过短的提示
							layout_record.setVisibility(View.GONE);
							showShortToast().show();
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * 发送语音消息
	 * @Title: sendImageMessage
	 * @Description: TODO
	 * @param @param localPath
	 * @return void
	 * @throws
	 */
	private void sendVoiceMessage(String local, int length) {
		manager.sendVoiceMessage(targetUser, local, length,
				new UploadListener() {

					@Override
					public void onStart(BmobMsg msg) {
						// TODO Auto-generated method stub
						refreshMessage(msg);
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onFailure(int error, String arg1) {
						// TODO Auto-generated method stub
						ShowLog("上传语音失败 -->arg1：" + arg1);
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	Toast toast;

	/**
	 * 显示录音时间过短的Toast
	 * @Title: showShortToast
	 * @return void
	 * @throws
	 */
	private Toast showShortToast() {
		if (toast == null) {
			toast = new Toast(this);
		}
		View view = LayoutInflater.from(this).inflate(
				R.layout.include_chat_voice_short, null);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(50);
		return toast;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (flagmv) {
			utils.addVelocityTracker(event);
			utils.getScrollVelocity();
			int x= (int) event.getX();
			raw_x = (int) event.getRawX();
			int[] loc_bt_voice = new int[2];
			btn_chat_voice.getLocationOnScreen(loc_bt_voice);
			int bt_voice_x = loc_bt_voice[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				
					if (!CommonUtils.checkSdCard()) {
						ShowToast("发送语音需要sdcard支持！");
						return false;
					}
					try {
						btn_chat_voice.setPressed(true);
						// layout_record.setVisibility(View.VISIBLE);
						// tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
						// 开始录音，参数事聊天对象的Id
						recordManager.startRecording(targetId);
					} catch (Exception e) {
						
					}
				
					//在播放录音就停止该播放
					utils.addVelocityTracker(event);
					utils.scrollByDistanceX(scroller_view);
					audioUtils.playSound(1);
					_layoutNormal2CancelAnimation();
					// 假如scroller滚动还没有结束，我们直接返回
					if (!scroller.isFinished()) {
						return super.onTouchEvent(event);
					}
					downX = (int) event.getRawX();
					downY = (int) event.getY();
					startVoiceT = System.currentTimeMillis();
					flag = 2;
					updateTimerView();
					flagmv = false;
					//设置名称开始录音
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
				_layoutCancel2NormalAnimation();
//				int time = (int) ((endVoiceT - startVoiceT) / 1000);
//				if (time < 60) {
//					//获取路径
//					if (time <= 1) {
//						flag = 1;
//						flagmv = false;
//						return false;
//					}
//					//不发送语音
//				}
//				// 松开手势时执行录制完成
				
				
				btn_chat_voice.setPressed(false);
				// layout_record.setVisibility(View.INVISIBLE);
				try {
						int recordTime = recordManager.stopRecording();
						if (recordTime > 1) {
							// 发送语音文件
							BmobLog.i("voice", "发送语音");
							sendVoiceMessage(
									recordManager.getRecordFilePath(targetId),
									recordTime);
						} else {// 录音时间过短，则提示录音过短的提示
							// layout_record.setVisibility(View.GONE);
							showShortToast().show();
						}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				flagmv = false;
				flag = 1;
				
				return true;
				
				
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				int deltaX = downX -raw_x;
				if(event.getX() + bt_voice_x> bt_voice_x
						&& event.getX() + bt_voice_x< bt_voice_x + btn_chat_voice.getWidth()){
					//手指在录音按钮范围内滑动时，文字不触发滑动事件
				}else{//否则触发滑动事件
					scroller_view.scrollBy(deltaX, 0);// 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
				}
				downX =raw_x;//记住上次触摸屏的位置
				if ((raw_x < screenWidth/2 + 50)&&flag==2) {// 手指滑到屏幕一半时执行取消事件
					
					recordManager.cancelRecording();
					BmobLog.i("voice", "放弃发送语音");
					
					utils.scrollByDistanceX(scroller_view);
					utils.recycleVelocityTracker();
					_layoutCancel2NormalAnimation();
					audioUtils.playSound(2);
					_trashcanAndMicAnimation();
					flag = 1;
					return false;
				} else {
					
				}
			}
			flagmv = false;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 初始化语音动画资源
	 * @Title: initVoiceAnimRes
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceAnimRes() {
		drawable_Anims = new Drawable[] {
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6) };
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
		return list;
	}

	/**
	 * 界面刷新
	 * @Title: initOrRefresh
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initOrRefresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
				
				// 从数据哭中查询历史消息，并加载
				int size = initMsgData().size();
				
				// 需要将未读的消息倒叙显示在界面上
				for(int i=(news-1);i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
				}
				
				// 设定XListView选定的item
				mListView.setSelection(mAdapter.getCount() - 1);
			} else {
				// Do nothing
				mAdapter.notifyDataSetChanged();
			}
		} else {
			mAdapter = new MessageChatAdapter(this, initMsgData());
			mListView.setAdapter(mAdapter);
		}
	}

	private void initAddView() {
		tv_picture = (TextView) findViewById(R.id.tv_picture);
		tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_picture.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		tv_camera.setOnClickListener(this);
	}

	private void initBottomView() {
		
		// 滑动取消发送
		scroller_view = (FrameLayout) findViewById(R.id.voice_note_slide_to_cancel_scroller);  
		
		// 语音提示Layout
		voice_note_layout = (ViewGroup) findViewById(R.id.voice_note_layout);
				
		// 语音取消提示
		ivVoiceCancel = (View) findViewById(R.id.voice_note_slide_to_cancel_animation);
				
			// 录音时间
		mRecordTime = (TextView) findViewById(R.id.voice_note_info);
				
		// 取消语音的垃圾桶
		trashcan = (ViewGroup) findViewById(R.id.voice_cancel_trashcan);
				
		// 时间格式
		mTimerFormat = getResources().getString(R.string.timer_format);
				
		// 代表当前的语音，丢进垃圾桶，在表情按钮之上
		iv_voice = (ImageView) findViewById(R.id.voice_cancel_animation);
				
		// 垃圾桶盖
		gai = (ImageView) findViewById(R.id.voice_cancel_trashcan_lid);
		
		// 最左边
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
		btn_chat_add.setOnClickListener(this);
		btn_chat_emo.setOnClickListener(this);
		// 最右边
		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
		btn_chat_voice.setOnClickListener(this);
		btn_chat_keyboard.setOnClickListener(this);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		// 最下面
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		
		// 初始化更多（照片、拍照、发送位置）的界面
		initAddView();
		// 初始化表情界面
		initEmoView();
		
		
		btn_chat_voice.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				flagmv = true;
				return ChatActivity.this.onTouchEvent(event);
			}
		});
		
		scroller_view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return ChatActivity.this.onTouchEvent(event);
			}

		}); 

		// 最中间
		// 语音框
		// btn_speak = (Button) findViewById(R.id.btn_speak);
		// 输入框
		edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		edit_user_comment.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(s)) {
					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
					btn_chat_voice.setVisibility(View.GONE);
				} else {
					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
						btn_chat_voice.setVisibility(View.VISIBLE);
						btn_chat_send.setVisibility(View.GONE);
						btn_chat_keyboard.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		edit_user_comment.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				
				final String msg = edit_user_comment.getText().toString();
				if (msg.equals("")) {
					ShowToast("请输入发送消息!");
					return false;
				}
				boolean isNetConnected = CommonUtils.isNetworkAvailable(ChatActivity.this);
				if (!isNetConnected || targetId == null) {
					ShowToast(R.string.network_tips);
					// return;
				}
				// 组装BmobMessage对象
				
				// 创建文本消息，第二个参数是目标聊天用户的Id，第三个是消息的内容
				BmobMsg message = BmobMsg.createTextSendMsg(ChatActivity.this, targetId, msg);
				// 默认发送完成，将数据保存到本地消息表和最近会话表中
				manager.sendTextMessage(targetUser, message);
				// 刷新界面
				refreshMessage(message);
				
				return false;
			}
		});

	}
	
	// 初始化动画
	private void initAnimation() {
		cancle_animation = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.voice_cancle);
		slide_in_right = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.slide_in_right);
		slide_in_left = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.slide_in_left);
		slide_out_left = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.slide_out_left);
		slide_out_right = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.slide_out_right);
		mic_fling = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.actin);
		trash_lid_start = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.lid_start);
		push_up_in = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.push_up_in);
		push_up_out = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.push_up_out);
		grow_from_top = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.grow_from_top);
		mic_twinkle = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.mic_twinkle);
	}
	
	
	/**
	 * 正常布局切换到取消布局
	 */
	private void _layoutNormal2CancelAnimation() {
		voice_note_layout.startAnimation(slide_in_right);//录音布局右滑进入
		voice_note_layout.setVisibility(View.VISIBLE);
//		text_entry.startAnimation(slide_out_left);// 文本布局左滑出去
//		text_entry.setVisibility(View.GONE);
		
		// 表情，输入框和更多按钮左滑出去
		btn_chat_add.startAnimation(slide_out_left);
		btn_chat_emo.startAnimation(slide_out_left);
		edit_user_comment.startAnimation(slide_out_left);
		btn_chat_add.setVisibility(View.GONE);
		btn_chat_emo.setVisibility(View.GONE);
		edit_user_comment.setVisibility(View.GONE);
		
		ivVoiceCancel.startAnimation(cancle_animation);// 波纹动画
		iv_voice.setVisibility(View.VISIBLE);
		iv_voice.startAnimation(mic_twinkle);//mic闪烁动画
	}
	
	/**
	 * 垃圾桶和话筒动画
	 */
	private void _trashcanAndMicAnimation(){
		iv_voice.startAnimation(mic_fling); // mic抛掷动画
		btn_chat_emo.setVisibility(View.INVISIBLE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				gai.startAnimation(trash_lid_start);// 桶盖动画
			}
		}, 600);
		trashcan.startAnimation(push_up_in);//垃圾桶上滑进入
		trashcan.setVisibility(View.VISIBLE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				trashcan.startAnimation(push_up_out);//垃圾桶下滑出去
				trashcan.setVisibility(View.GONE);
			}
		}, 1500);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				btn_chat_emo.startAnimation(grow_from_top);
				btn_chat_emo.setVisibility(View.VISIBLE);
			}
		}, 1600);
	}
	
	/**
	 * 取消布局切换到正常布局
	 */
	private void _layoutCancel2NormalAnimation() {
		voice_note_layout.setVisibility(View.GONE);
		voice_note_layout.startAnimation(slide_out_right);//录音布局右滑出去
//		text_entry.setVisibility(View.VISIBLE);
//		text_entry.startAnimation(slide_in_left);// 文本布局左滑进入
		
		btn_chat_emo.setVisibility(View.VISIBLE);
		btn_chat_add.setVisibility(View.VISIBLE);
		edit_user_comment.setVisibility(View.VISIBLE);
		
		btn_chat_emo.startAnimation(slide_in_left);
		btn_chat_add.startAnimation(slide_in_left);
		edit_user_comment.startAnimation(slide_in_left);
		
		ivVoiceCancel.clearAnimation();//取消波纹动画
		iv_voice.clearAnimation();
		iv_voice.setVisibility(View.GONE);
	}

	/**
	 * 弹出popup提示
	 */
	private void showpopup() {
		pop_flag = 1;
		LinearLayout layout = new LinearLayout(this);
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		tv.setText("按住录音，松开发送");
		tv.setTextColor(Color.WHITE);
		layout.addView(tv);
		final PopupWindow popupWindow = new PopupWindow(layout,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(false);
		popupWindow.setOutsideTouchable(false);// 使popup点击窗口外侧不消失
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_inline_error_above_holo_light));
		popupWindow.setTouchable(false);
		int[] location = new int[2];
		btn_chat_voice.getLocationOnScreen(location);
		popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		popupWindow.showAtLocation(btn_chat_voice, Gravity.TOP, location[0],location[1] - btn_chat_voice.getWidth());
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				popupWindow.dismiss();
				pop_flag = 0;
			}
		}, 2000);
	}
	private void updateTimerView() {// 时间更新
		endVoiceT = System.currentTimeMillis();
		int time = (int) ((endVoiceT - startVoiceT) / 1000);
		String timeStr = String.format(mTimerFormat, time / 60, time % 60);
		mRecordTime.setText(timeStr);
		if (time < 60) {
			mHandler.postDelayed(mUpdateTimer, 500);
		} else {

		}
	}
	private Runnable mUpdateTimer = new Runnable() {
		@Override
		public void run() {
			updateTimerView();
		}
	};

	List<FaceText> emos;

	/**
	 * 初始化表情布局
	 * @Title: initEmoView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initEmoView() {
		pager_emo = (ViewPager) findViewById(R.id.pager_emo);
		emos = FaceTextUtils.faceTexts;

		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 2; ++i) {
			views.add(getGridView(i));
		}
		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
	}

	private View getGridView(final int i) {
		View view = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(21, emos.size()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this,
				list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
						int start = edit_user_comment.getSelectionStart();
						CharSequence content = edit_user_comment.getText()
								.insert(start, key);
						edit_user_comment.setText(content);
						// 定位光标位置
						CharSequence info = edit_user_comment.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
				} catch (Exception e) {

				}

			}
		});
		return view;
	}

	MessageChatAdapter mAdapter;
	
	private void initXListView() {
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		mListView.pullRefreshing();
		mListView.setDividerHeight(0);
		
		// 加载数据
		initOrRefresh();
		
		// 设置当前选定的item
		mListView.setSelection(mAdapter.getCount() - 1);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
				// 隐藏软键盘
				hideSoftInputView();
				layout_more.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
				btn_chat_voice.setVisibility(View.VISIBLE);
				btn_chat_keyboard.setVisibility(View.GONE);
				btn_chat_send.setVisibility(View.GONE);
				return false;
			}
		});

		// 重发按钮的点击事件
		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
				new MessageChatAdapter.onInternalClickListener() {

					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						// 重发消息
						showResendDialog(parentV, v, values);
					}
				});
	}

	/**
	 * 显示重发按钮 showResendDialog
	 * @Title: showResendDialog
	 * @Description: TODO
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	public void showResendDialog(final View parentV, View v, final Object values) {
		DialogTips dialog = new DialogTips(this, "确定重发该消息", "确定", "取消", "提示",
				true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
						|| ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 图片和语音类型的采用
					resendFileMsg(parentV, values);
				} else {
					resendTextMsg(parentV, values);
				}
				dialogInterface.dismiss();
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	/**
	 * 重发文本消息
	 */
	private void resendTextMsg(final View parentV, final Object values) {
		BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(
				targetUser, (BmobMsg) values, new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowLog("发送成功");
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.VISIBLE);
						((TextView) parentV.findViewById(R.id.tv_send_status))
								.setText("已发送");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowLog("发送失败:" + arg1);
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 重发图片消息
	 * @Title: resendImageMsg
	 * @Description: TODO
	 * @param @param parentV
	 * @param @param values
	 * @return void
	 * @throws
	 */
	private void resendFileMsg(final View parentV, final Object values) {
		BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(
				targetUser, (BmobMsg) values, new UploadListener() {

					@Override
					public void onStart(BmobMsg msg) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.INVISIBLE);
						if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
							parentV.findViewById(R.id.tv_send_status)
									.setVisibility(View.GONE);
							parentV.findViewById(R.id.tv_voice_length)
									.setVisibility(View.VISIBLE);
						} else {
							// 图片
							parentV.findViewById(R.id.tv_send_status)
									.setVisibility(View.VISIBLE);
							((TextView) parentV
									.findViewById(R.id.tv_send_status))
									.setText("已发送");
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.INVISIBLE);
					}
				});
		mAdapter.notifyDataSetChanged();
	}
	
	/*
	 * 点击事件
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_user_comment:// 点击文本输入框
			mListView.setSelection(mListView.getCount() - 1);
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_emo:// 点击笑脸图标
			if (layout_more.getVisibility() == View.GONE) {
				showEditState(true);
			} else {
				if (layout_add.getVisibility() == View.VISIBLE) {
					layout_add.setVisibility(View.GONE);
					layout_emo.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}

			break;
		case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
			if (layout_more.getVisibility() == View.GONE) {
				layout_more.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.VISIBLE);
				layout_emo.setVisibility(View.GONE);
				hideSoftInputView();
			} else {
				if (layout_emo.getVisibility() == View.VISIBLE) {
					layout_emo.setVisibility(View.GONE);
					layout_add.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}

			break;
		case R.id.btn_chat_voice:// 语音按钮
//			edit_user_comment.setVisibility(View.GONE);
//			layout_more.setVisibility(View.VISIBLE);
//			btn_chat_voice.setVisibility(View.GONE);
//			btn_chat_keyboard.setVisibility(View.VISIBLE);
//			btn_speak.setVisibility(View.VISIBLE);
			btn_chat_voice.setVisibility(View.VISIBLE);
			layout_more.setVisibility(View.INVISIBLE);
			hideSoftInputView();
			audioUtils.playSound(1);
			audioUtils.playSound(2);
			if (pop_flag == 0) {// 防止popup被多次点击重复出现
				showpopup();
			}
			break;
		case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
			showEditState(false);
			break;
		case R.id.btn_chat_send:// 发送文本
			final String msg = edit_user_comment.getText().toString();
			if (msg.equals("")) {
				ShowToast("请输入发送消息!");
				return;
			}
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected || targetId == null) {
				ShowToast(R.string.network_tips);
				// return;
			}
			// 组装BmobMessage对象
			
			// 创建文本消息，第二个参数是目标聊天用户的Id，第三个是消息的内容
			BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
			// 默认发送完成，将数据保存到本地消息表和最近会话表中
			manager.sendTextMessage(targetUser, message);
			// 刷新界面
			refreshMessage(message);

			break;
		case R.id.tv_camera:// 拍照
			selectImageFromCamera();
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		case R.id.tv_location:// 位置
			selectLocationFromMap();
			break;
		default:
			break;
		}
	}

	/**
	 * 启动地图
	 * 
	 * @Title: selectLocationFromMap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void selectLocationFromMap() {
		Intent intent = new Intent(this, LocationActivity.class);
		intent.putExtra("type", "select");
		startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCATION);
	}

	// 拍照后得到的图片地址
	private String localCameraPath = "";

	/**
	 * 启动相机拍照 startCamera
	 * 
	 * @Title: startCamera
	 * @throws
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
		if (!dir.exists()) {
			// 如果目录不存在，就新建一个目录
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		localCameraPath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		
		//  请求拍照，并传入照片地址
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				BmobConstants.REQUESTCODE_TAKE_CAMERA);
	}

	/**
	 * 从本地选择图片
	 * @Title: selectImage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCAL);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case BmobConstants.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
				ShowLog("本地图片的地址：" + localCameraPath);
				
				// 发生图片消息
				sendImageMessage(localCameraPath);
				break;
			case BmobConstants.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null
								|| localSelectPath.equals("null")) {
							ShowToast("找不到您想要的图片");
							return;
						}
						sendImageMessage(localSelectPath);
					}
				}
				break;
			case BmobConstants.REQUESTCODE_TAKE_LOCATION:// 地理位置
				double latitude = data.getDoubleExtra("x", 0);// 维度
				double longtitude = data.getDoubleExtra("y", 0);// 经度
				String address = data.getStringExtra("address");
				if (address != null && !address.equals("")) {
					sendLocationMessage(address, latitude, longtitude);
				} else {
					ShowToast("无法获取到您的位置信息!");
				}

				break;
			}
		}
	}

	/**
	 * 发送位置信息
	 * @Title: sendLocationMessage
	 * @Description: TODO
	 * @param @param address
	 * @param @param latitude
	 * @param @param longtitude
	 * @return void
	 * @throws
	 */
	private void sendLocationMessage(String address, double latitude,
			double longtitude) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
			layout_emo.setVisibility(View.GONE);
		}
		// 组装BmobMessage对象
		BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId,
				address, latitude, longtitude);
		// 默认发送完成，将数据保存到本地消息表和最近会话表中
		manager.sendTextMessage(targetUser, message);
		// 刷新界面
		refreshMessage(message);
	}

	/**
	 * 默认先上传本地图片，之后才显示出来 sendImageMessage
	 * @Title: sendImageMessage
	 * @Description: TODO
	 * @param @param localPath
	 * @return void
	 * @throws
	 */
	private void sendImageMessage(String local) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
			layout_emo.setVisibility(View.GONE);
		}
		
		if (isNetAvailable() && targetId != null) {
			manager.sendImageMessage(targetUser, local, new UploadListener() {

				@Override
				public void onStart(BmobMsg msg) {
					// TODO Auto-generated method stub
					ShowLog("开始上传onStart：" + msg.getContent() + ",状态："
							+ msg.getStatus());
					refreshMessage(msg);
				}

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFailure(int error, String arg1) {
					// TODO Auto-generated method stub
					ShowLog("上传失败 -->arg1：" + arg1);
					mAdapter.notifyDataSetChanged();
				}
			});
		}
		
	}

	/**
	 * 根据是否点击笑脸来显示文本输入框的状态
	 * @Title: showEditState
	 * @Description: TODO
	 * @param @param isEmo: 用于区分文字和表情
	 * @return void
	 * @throws
	 */
	private void showEditState(boolean isEmo) {
		edit_user_comment.setVisibility(View.VISIBLE);
		btn_chat_keyboard.setVisibility(View.GONE);
		btn_chat_voice.setVisibility(View.VISIBLE);
		//btn_speak.setVisibility(View.GONE);
		edit_user_comment.requestFocus();
		if (isEmo) {
			layout_more.setVisibility(View.VISIBLE);
			layout_more.setVisibility(View.VISIBLE);
			layout_emo.setVisibility(View.VISIBLE);
			layout_add.setVisibility(View.GONE);
			
			// 隐藏软键盘
			hideSoftInputView();
		} else {
			layout_more.setVisibility(View.GONE);
			showSoftInputView();
		}
	}

	// 显示软键盘
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(edit_user_comment, 0);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 新消息到达，重新刷新界面
		initOrRefresh();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(targetId);
		//清空消息未读数-这个要在刷新之后
		MyMessageReceiver.mNewNum=0;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 监听推送的消息
		// 停止录音
		if (recordManager.isRecording()) {
			recordManager.cancelRecording();
			layout_record.setVisibility(View.GONE);
		}
		// 停止播放录音
		if (NewRecordPlayClickListener.isPlaying
				&& NewRecordPlayClickListener.currentPlayListener != null) {
			NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
				if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				mAdapter.add(m);
				// 定位
				mListView.setSelection(mAdapter.getCount() - 1);
				//取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
		}
	};

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	
	NewBroadcastReceiver  receiver;
	
	private void initNewMessageBroadCast(){
		// 注册接收消息广播
		receiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}
	
	/**
	 * 新消息广播接收者
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			
			// 收到这个广播的时候，message已经在消息表中，可直接获取
			if(TextUtils.isEmpty(from)&&TextUtils.isEmpty(msgId)&&TextUtils.isEmpty(msgTime)){
				BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
				if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
					return;
				
				//将消息添加到当前页面
				mAdapter.add(msg);
				// 消息定位
				mListView.setSelection(mAdapter.getCount() - 1);
				//取消当前聊天对象的未读标示
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}
	
	/**
	 * 每次发生或者接受一条消息之后需要刷新界面
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshMessage(BmobMsg msg) {
		// 更新界面
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}

	
	// 收到消息
	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		handler.sendMessage(handlerMsg);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation invite) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
		// 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
		if (conversionId.split("&")[1].equals(targetId)) {
			// 修改界面上指定消息的阅读状态
			for (BmobMsg msg : mAdapter.getList()) {
				if (msg.getConversationId().equals(conversionId)
						&& msg.getMsgTime().equals(msgTime)) {
					msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
				}
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onRefresh() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MsgPagerNum++;
				int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
				BmobLog.i("记录总数：" + total);
				int currents = mAdapter.getCount();
				if (total <= currents) {
					ShowToast("聊天记录加载完了哦!");
				} else {
					List<BmobMsg> msgList = initMsgData();
					mAdapter.setList(msgList);
					mListView.setSelection(mAdapter.getCount() - currents - 1);
				}
				mListView.stopRefresh();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
	}

	// 下方三个按键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout_more.getVisibility() == 0) {
				layout_more.setVisibility(View.GONE);
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hideSoftInputView();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
		}
		
	}
	
	public void back(View view) {
		finish();
	}

}
