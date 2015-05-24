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
 * �������
 * 
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-3 ����4:33:11
 */
/**
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-23 ����3:28:49
 */
@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
public class ChatActivity extends BaseMainActivity implements OnClickListener,
		IXListViewListener, EventListener {

	// ���� ���� ��� ���� ���� 
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
	 * ������
	 */
	private Scroller scroller;
	/**
	 * ��Ļ���
	 */
	private int screenWidth;
	
	/**
	 * ��ָ����X������
	 */
	private int downY;
	/**
	 * ��ָ����Y������
	 */
	private int downX;
	private ScrollUtils utils ;
	int raw_x ;
	private boolean flagmv=false;
	private AudioUtils audioUtils;
	private int flag = 1;
	
	TextView tv_title;

	// ���������������
	EmoticonsEditText edit_user_comment;
	
	private View main_chat_bg;

	// ��������Id
	String targetId = "";
	
	// �������
	BmobChatUser targetUser;

	// ��Ϣ�ж���ҳ
	private static int MsgPagerNum;

	// ������棨��ͼƬ ����λ����Ϣ����������� �������
	private LinearLayout layout_more, layout_emo, layout_add;

	// ��������ҳ����
	private ViewPager pager_emo;

	// ��������ͼƬ�����ࡢλ��ѡ��
	private TextView tv_picture, tv_camera, tv_location;

	// �����й�
	RelativeLayout layout_record;
	TextView tv_voice_tips;
	ImageView iv_record;

	// ��Ͳ����
	private Drawable[] drawable_Anims;

	// �����¼����
	BmobRecordManager recordManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// Ĭ�ϲ����������
		
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC); //ע���Ĭ�� ��Ƶͨ��
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		
		screenWidth = dm.widthPixels;
		scroller = new Scroller(this);
		utils = new ScrollUtils();
		audioUtils = new AudioUtils(this);
		
		// �������
		manager = BmobChatManager.getInstance(this);
		MsgPagerNum = 0;
		
		// ��װ�������
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		if (targetUser != null) {
			targetId = targetUser.getObjectId();
		}
		
		//ע��㲥������
		initNewMessageBroadCast();
		initView();
	}
	
	private void initRecordManager(){
		// ������ع�����
		recordManager = BmobRecordManager.getInstance(this);
		// ����������С����--�����￪���߿����Լ�ʵ�֣���ʣ��10������µĸ��û�����ʾ������΢�ŵ���������
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {
	
			@Override
			public void onVolumnChanged(int value) {
				// TODO Auto-generated method stub
				iv_record.setImageDrawable(drawable_Anims[value]);
			}
	
			/*
			 * localPath��ʾ¼���ı���λ��
			 * recordTime��ʾ¼����ʱ��
			 * @see cn.bmob.im.inteface.OnRecordChangeListener#onTimeChanged(int, java.lang.String)
			 */
			@Override
			public void onTimeChanged(int recordTime, String localPath) {
				// TODO Auto-generated method stub
				BmobLog.i("voice", "��¼������:" + recordTime);
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1���ӽ�����������Ϣ
					// ��Ҫ���ð�ť
//					btn_speak.setPressed(false);
//					btn_speak.setClickable(false);
					// ȡ��¼����
					layout_record.setVisibility(View.INVISIBLE);
					// ����������Ϣ
					sendVoiceMessage(localPath, recordTime);
					// ��Ϊ�˷�ֹ����¼��ʱ��󣬻�෢һ��������ȥ�������
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
	        //�����ļ�����
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
		
		
		// ��ʼ�������·��Ŀؼ�
		initBottomView();
		
		initAnimation();
		
		// ��ʼ�������¼��XListView
		initXListView();
		
		// ��ʼ����������
		initVoiceView();
	}

	/**
	 * ��ʼ����������
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
		
		
		// ��ʼ������������Դ
		initVoiceAnimRes();
		// 
		initRecordManager();
	}

	/**
	 * ����˵��
	 * @ClassName: VoiceTouchListen
	 * @Description: TODO
	 * @author smile
	 * @date 2014-7-1 ����6:10:16
	 */
	class VoiceTouchListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.checkSdCard()) {
					ShowToast("����������Ҫsdcard֧�֣�");
					return false;
				}
				try {
					v.setPressed(true);
					layout_record.setVisibility(View.VISIBLE);
					tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
					// ��ʼ¼������������������Id
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
					if (event.getY() < 0) {// ����¼��
						recordManager.cancelRecording();
						BmobLog.i("voice", "������������");
					} else {
						int recordTime = recordManager.stopRecording();
						if (recordTime > 1) {
							// ���������ļ�
							BmobLog.i("voice", "��������");
							sendVoiceMessage(
									recordManager.getRecordFilePath(targetId),
									recordTime);
						} else {// ¼��ʱ����̣�����ʾ¼�����̵���ʾ
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
	 * ����������Ϣ
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
						ShowLog("�ϴ�����ʧ�� -->arg1��" + arg1);
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	Toast toast;

	/**
	 * ��ʾ¼��ʱ����̵�Toast
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
						ShowToast("����������Ҫsdcard֧�֣�");
						return false;
					}
					try {
						btn_chat_voice.setPressed(true);
						// layout_record.setVisibility(View.VISIBLE);
						// tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
						// ��ʼ¼������������������Id
						recordManager.startRecording(targetId);
					} catch (Exception e) {
						
					}
				
					//�ڲ���¼����ֹͣ�ò���
					utils.addVelocityTracker(event);
					utils.scrollByDistanceX(scroller_view);
					audioUtils.playSound(1);
					_layoutNormal2CancelAnimation();
					// ����scroller������û�н���������ֱ�ӷ���
					if (!scroller.isFinished()) {
						return super.onTouchEvent(event);
					}
					downX = (int) event.getRawX();
					downY = (int) event.getY();
					startVoiceT = System.currentTimeMillis();
					flag = 2;
					updateTimerView();
					flagmv = false;
					//�������ƿ�ʼ¼��
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
				_layoutCancel2NormalAnimation();
//				int time = (int) ((endVoiceT - startVoiceT) / 1000);
//				if (time < 60) {
//					//��ȡ·��
//					if (time <= 1) {
//						flag = 1;
//						flagmv = false;
//						return false;
//					}
//					//����������
//				}
//				// �ɿ�����ʱִ��¼�����
				
				
				btn_chat_voice.setPressed(false);
				// layout_record.setVisibility(View.INVISIBLE);
				try {
						int recordTime = recordManager.stopRecording();
						if (recordTime > 1) {
							// ���������ļ�
							BmobLog.i("voice", "��������");
							sendVoiceMessage(
									recordManager.getRecordFilePath(targetId),
									recordTime);
						} else {// ¼��ʱ����̣�����ʾ¼�����̵���ʾ
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
					//��ָ��¼����ť��Χ�ڻ���ʱ�����ֲ����������¼�
				}else{//���򴥷������¼�
					scroller_view.scrollBy(deltaX, 0);// ��ָ�϶�itemView����, deltaX����0���������С��0���ҹ�
				}
				downX =raw_x;//��ס�ϴδ�������λ��
				if ((raw_x < screenWidth/2 + 50)&&flag==2) {// ��ָ������Ļһ��ʱִ��ȡ���¼�
					
					recordManager.cancelRecording();
					BmobLog.i("voice", "������������");
					
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
	 * ��ʼ������������Դ
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
	 * ������Ϣ��ʷ�������ݿ��ж���
	 */
	private List<BmobMsg> initMsgData() {
		List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
		return list;
	}

	/**
	 * ����ˢ��
	 * @Title: initOrRefresh
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initOrRefresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// ���ڸ��µ���������������ڼ�������Ϣ����ʱ�ٻص�����ҳ���ʱ����Ҫ��ʾ��������Ϣ
				int news=  MyMessageReceiver.mNewNum;//�п��������ڼ䣬����N����Ϣ,�����Ҫ������ʾ�ڽ�����
				
				// �����ݿ��в�ѯ��ʷ��Ϣ��������
				int size = initMsgData().size();
				
				// ��Ҫ��δ������Ϣ������ʾ�ڽ�����
				for(int i=(news-1);i>=0;i--){
					mAdapter.add(initMsgData().get(size-(i+1)));// ������һ����Ϣ��������ʾ
				}
				
				// �趨XListViewѡ����item
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
		
		// ����ȡ������
		scroller_view = (FrameLayout) findViewById(R.id.voice_note_slide_to_cancel_scroller);  
		
		// ������ʾLayout
		voice_note_layout = (ViewGroup) findViewById(R.id.voice_note_layout);
				
		// ����ȡ����ʾ
		ivVoiceCancel = (View) findViewById(R.id.voice_note_slide_to_cancel_animation);
				
			// ¼��ʱ��
		mRecordTime = (TextView) findViewById(R.id.voice_note_info);
				
		// ȡ������������Ͱ
		trashcan = (ViewGroup) findViewById(R.id.voice_cancel_trashcan);
				
		// ʱ���ʽ
		mTimerFormat = getResources().getString(R.string.timer_format);
				
		// ����ǰ����������������Ͱ���ڱ��鰴ť֮��
		iv_voice = (ImageView) findViewById(R.id.voice_cancel_animation);
				
		// ����Ͱ��
		gai = (ImageView) findViewById(R.id.voice_cancel_trashcan_lid);
		
		// �����
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
		btn_chat_add.setOnClickListener(this);
		btn_chat_emo.setOnClickListener(this);
		// ���ұ�
		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
		btn_chat_voice.setOnClickListener(this);
		btn_chat_keyboard.setOnClickListener(this);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		// ������
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		
		// ��ʼ�����ࣨ��Ƭ�����ա�����λ�ã��Ľ���
		initAddView();
		// ��ʼ���������
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
				// ��������¼�ư�ťʱ����falseִ�и���OnTouch
				return ChatActivity.this.onTouchEvent(event);
			}

		}); 

		// ���м�
		// ������
		// btn_speak = (Button) findViewById(R.id.btn_speak);
		// �����
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
					ShowToast("�����뷢����Ϣ!");
					return false;
				}
				boolean isNetConnected = CommonUtils.isNetworkAvailable(ChatActivity.this);
				if (!isNetConnected || targetId == null) {
					ShowToast(R.string.network_tips);
					// return;
				}
				// ��װBmobMessage����
				
				// �����ı���Ϣ���ڶ���������Ŀ�������û���Id������������Ϣ������
				BmobMsg message = BmobMsg.createTextSendMsg(ChatActivity.this, targetId, msg);
				// Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
				manager.sendTextMessage(targetUser, message);
				// ˢ�½���
				refreshMessage(message);
				
				return false;
			}
		});

	}
	
	// ��ʼ������
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
	 * ���������л���ȡ������
	 */
	private void _layoutNormal2CancelAnimation() {
		voice_note_layout.startAnimation(slide_in_right);//¼�������һ�����
		voice_note_layout.setVisibility(View.VISIBLE);
//		text_entry.startAnimation(slide_out_left);// �ı������󻬳�ȥ
//		text_entry.setVisibility(View.GONE);
		
		// ���飬�����͸��ఴť�󻬳�ȥ
		btn_chat_add.startAnimation(slide_out_left);
		btn_chat_emo.startAnimation(slide_out_left);
		edit_user_comment.startAnimation(slide_out_left);
		btn_chat_add.setVisibility(View.GONE);
		btn_chat_emo.setVisibility(View.GONE);
		edit_user_comment.setVisibility(View.GONE);
		
		ivVoiceCancel.startAnimation(cancle_animation);// ���ƶ���
		iv_voice.setVisibility(View.VISIBLE);
		iv_voice.startAnimation(mic_twinkle);//mic��˸����
	}
	
	/**
	 * ����Ͱ�ͻ�Ͳ����
	 */
	private void _trashcanAndMicAnimation(){
		iv_voice.startAnimation(mic_fling); // mic��������
		btn_chat_emo.setVisibility(View.INVISIBLE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				gai.startAnimation(trash_lid_start);// Ͱ�Ƕ���
			}
		}, 600);
		trashcan.startAnimation(push_up_in);//����Ͱ�ϻ�����
		trashcan.setVisibility(View.VISIBLE);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				trashcan.startAnimation(push_up_out);//����Ͱ�»���ȥ
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
	 * ȡ�������л�����������
	 */
	private void _layoutCancel2NormalAnimation() {
		voice_note_layout.setVisibility(View.GONE);
		voice_note_layout.startAnimation(slide_out_right);//¼�������һ���ȥ
//		text_entry.setVisibility(View.VISIBLE);
//		text_entry.startAnimation(slide_in_left);// �ı������󻬽���
		
		btn_chat_emo.setVisibility(View.VISIBLE);
		btn_chat_add.setVisibility(View.VISIBLE);
		edit_user_comment.setVisibility(View.VISIBLE);
		
		btn_chat_emo.startAnimation(slide_in_left);
		btn_chat_add.startAnimation(slide_in_left);
		edit_user_comment.startAnimation(slide_in_left);
		
		ivVoiceCancel.clearAnimation();//ȡ�����ƶ���
		iv_voice.clearAnimation();
		iv_voice.setVisibility(View.GONE);
	}

	/**
	 * ����popup��ʾ
	 */
	private void showpopup() {
		pop_flag = 1;
		LinearLayout layout = new LinearLayout(this);
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		tv.setText("��ס¼�����ɿ�����");
		tv.setTextColor(Color.WHITE);
		layout.addView(tv);
		final PopupWindow popupWindow = new PopupWindow(layout,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(false);
		popupWindow.setOutsideTouchable(false);// ʹpopup���������಻��ʧ
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
	private void updateTimerView() {// ʱ�����
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
	 * ��ʼ�����鲼��
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
						// ��λ���λ��
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
		// ���Ȳ�������ظ���
		mListView.setPullLoadEnable(false);
		// ��������
		mListView.setPullRefreshEnable(true);
		// ���ü�����
		mListView.setXListViewListener(this);
		mListView.pullRefreshing();
		mListView.setDividerHeight(0);
		
		// ��������
		initOrRefresh();
		
		// ���õ�ǰѡ����item
		mListView.setSelection(mAdapter.getCount() - 1);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
				// ���������
				hideSoftInputView();
				layout_more.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
				btn_chat_voice.setVisibility(View.VISIBLE);
				btn_chat_keyboard.setVisibility(View.GONE);
				btn_chat_send.setVisibility(View.GONE);
				return false;
			}
		});

		// �ط���ť�ĵ���¼�
		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
				new MessageChatAdapter.onInternalClickListener() {

					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						// �ط���Ϣ
						showResendDialog(parentV, v, values);
					}
				});
	}

	/**
	 * ��ʾ�ط���ť showResendDialog
	 * @Title: showResendDialog
	 * @Description: TODO
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	public void showResendDialog(final View parentV, View v, final Object values) {
		DialogTips dialog = new DialogTips(this, "ȷ���ط�����Ϣ", "ȷ��", "ȡ��", "��ʾ",
				true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
						|| ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// ͼƬ���������͵Ĳ���
					resendFileMsg(parentV, values);
				} else {
					resendTextMsg(parentV, values);
				}
				dialogInterface.dismiss();
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}

	/**
	 * �ط��ı���Ϣ
	 */
	private void resendTextMsg(final View parentV, final Object values) {
		BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(
				targetUser, (BmobMsg) values, new PushListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						ShowLog("���ͳɹ�");
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.progress_load).setVisibility(
								View.INVISIBLE);
						parentV.findViewById(R.id.iv_fail_resend)
								.setVisibility(View.INVISIBLE);
						parentV.findViewById(R.id.tv_send_status)
								.setVisibility(View.VISIBLE);
						((TextView) parentV.findViewById(R.id.tv_send_status))
								.setText("�ѷ���");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowLog("����ʧ��:" + arg1);
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
	 * �ط�ͼƬ��Ϣ
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
							// ͼƬ
							parentV.findViewById(R.id.tv_send_status)
									.setVisibility(View.VISIBLE);
							((TextView) parentV
									.findViewById(R.id.tv_send_status))
									.setText("�ѷ���");
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
	 * ����¼�
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_user_comment:// ����ı������
			mListView.setSelection(mListView.getCount() - 1);
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_emo:// ���Ц��ͼ��
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
		case R.id.btn_chat_add:// ��Ӱ�ť-��ʾͼƬ�����ա�λ��
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
		case R.id.btn_chat_voice:// ������ť
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
			if (pop_flag == 0) {// ��ֹpopup����ε���ظ�����
				showpopup();
			}
			break;
		case R.id.btn_chat_keyboard:// ���̰�ť������͵������̲����ص�������ť
			showEditState(false);
			break;
		case R.id.btn_chat_send:// �����ı�
			final String msg = edit_user_comment.getText().toString();
			if (msg.equals("")) {
				ShowToast("�����뷢����Ϣ!");
				return;
			}
			boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
			if (!isNetConnected || targetId == null) {
				ShowToast(R.string.network_tips);
				// return;
			}
			// ��װBmobMessage����
			
			// �����ı���Ϣ���ڶ���������Ŀ�������û���Id������������Ϣ������
			BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
			// Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
			manager.sendTextMessage(targetUser, message);
			// ˢ�½���
			refreshMessage(message);

			break;
		case R.id.tv_camera:// ����
			selectImageFromCamera();
			break;
		case R.id.tv_picture:// ͼƬ
			selectImageFromLocal();
			break;
		case R.id.tv_location:// λ��
			selectLocationFromMap();
			break;
		default:
			break;
		}
	}

	/**
	 * ������ͼ
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

	// ���պ�õ���ͼƬ��ַ
	private String localCameraPath = "";

	/**
	 * ����������� startCamera
	 * 
	 * @Title: startCamera
	 * @throws
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
		if (!dir.exists()) {
			// ���Ŀ¼�����ڣ����½�һ��Ŀ¼
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		localCameraPath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		
		//  �������գ���������Ƭ��ַ
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				BmobConstants.REQUESTCODE_TAKE_CAMERA);
	}

	/**
	 * �ӱ���ѡ��ͼƬ
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
			case BmobConstants.REQUESTCODE_TAKE_CAMERA:// ��ȡ��ֵ��ʱ����ϴ�path·���µ�ͼƬ��������
				ShowLog("����ͼƬ�ĵ�ַ��" + localCameraPath);
				
				// ����ͼƬ��Ϣ
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
							ShowToast("�Ҳ�������Ҫ��ͼƬ");
							return;
						}
						sendImageMessage(localSelectPath);
					}
				}
				break;
			case BmobConstants.REQUESTCODE_TAKE_LOCATION:// ����λ��
				double latitude = data.getDoubleExtra("x", 0);// ά��
				double longtitude = data.getDoubleExtra("y", 0);// ����
				String address = data.getStringExtra("address");
				if (address != null && !address.equals("")) {
					sendLocationMessage(address, latitude, longtitude);
				} else {
					ShowToast("�޷���ȡ������λ����Ϣ!");
				}

				break;
			}
		}
	}

	/**
	 * ����λ����Ϣ
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
		// ��װBmobMessage����
		BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId,
				address, latitude, longtitude);
		// Ĭ�Ϸ�����ɣ������ݱ��浽������Ϣ�������Ự����
		manager.sendTextMessage(targetUser, message);
		// ˢ�½���
		refreshMessage(message);
	}

	/**
	 * Ĭ�����ϴ�����ͼƬ��֮�����ʾ���� sendImageMessage
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
					ShowLog("��ʼ�ϴ�onStart��" + msg.getContent() + ",״̬��"
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
					ShowLog("�ϴ�ʧ�� -->arg1��" + arg1);
					mAdapter.notifyDataSetChanged();
				}
			});
		}
		
	}

	/**
	 * �����Ƿ���Ц������ʾ�ı�������״̬
	 * @Title: showEditState
	 * @Description: TODO
	 * @param @param isEmo: �����������ֺͱ���
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
			
			// ���������
			hideSoftInputView();
		} else {
			layout_more.setVisibility(View.GONE);
			showSoftInputView();
		}
	}

	// ��ʾ�����
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
		// ����Ϣ�������ˢ�½���
		initOrRefresh();
		MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
		// �п��������ڼ䣬������������֪ͨ������ʱ����Ҫ���֪ͨ�����δ����Ϣ��
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(targetId);
		//�����Ϣδ����-���Ҫ��ˢ��֮��
		MyMessageReceiver.mNewNum=0;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// �������͵���Ϣ
		// ֹͣ¼��
		if (recordManager.isRecording()) {
			recordManager.cancelRecording();
			layout_record.setVisibility(View.GONE);
		}
		// ֹͣ����¼��
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
				if (!uid.equals(targetId))// ������ǵ�ǰ��������������Ϣ��������
					return;
				mAdapter.add(m);
				// ��λ
				mListView.setSelection(mAdapter.getCount() - 1);
				//ȡ����ǰ��������δ����ʾ
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
		}
	};

	public static final int NEW_MESSAGE = 0x001;// �յ���Ϣ
	
	NewBroadcastReceiver  receiver;
	
	private void initNewMessageBroadCast(){
		// ע�������Ϣ�㲥
		receiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		//���ù㲥�����ȼ������Mainacitivity,���������Ϣ����ʱ��������chatҳ�棬ֱ����ʾ��Ϣ����������ʾ��Ϣδ��
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);
	}
	
	/**
	 * ����Ϣ�㲥������
	 * 
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			
			// �յ�����㲥��ʱ��message�Ѿ�����Ϣ���У���ֱ�ӻ�ȡ
			if(TextUtils.isEmpty(from)&&TextUtils.isEmpty(msgId)&&TextUtils.isEmpty(msgTime)){
				BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
				if (!from.equals(targetId))// ������ǵ�ǰ��������������Ϣ��������
					return;
				
				//����Ϣ��ӵ���ǰҳ��
				mAdapter.add(msg);
				// ��Ϣ��λ
				mListView.setSelection(mAdapter.getCount() - 1);
				//ȡ����ǰ��������δ����ʾ
				BmobDB.create(ChatActivity.this).resetUnread(targetId);
			}
			// �ǵðѹ㲥���ս��
			abortBroadcast();
		}
	}
	
	/**
	 * ÿ�η������߽���һ����Ϣ֮����Ҫˢ�½���
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshMessage(BmobMsg msg) {
		// ���½���
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}

	
	// �յ���Ϣ
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
		// �˴�Ӧ�ù��˵����Ǻ͵�ǰ�û�������Ļ�ִ��Ϣ�����ˢ��
		if (conversionId.split("&")[1].equals(targetId)) {
			// �޸Ľ�����ָ����Ϣ���Ķ�״̬
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
				BmobLog.i("��¼������" + total);
				int currents = mAdapter.getCount();
				if (total <= currents) {
					ShowToast("�����¼��������Ŷ!");
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

	// �·����������¼�
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
