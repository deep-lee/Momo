package com.deep.ui.update;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.AddFriendAdapter;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.AddFriendsFromAddressBookActivity;
import com.bmob.im.demo.ui.BaseActivity;
import com.bmob.im.demo.ui.ChatActivity;
import com.bmob.im.demo.ui.SetMyInfoActivity2;
import com.bmob.im.demo.util.ActivityUtil;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimationComposer;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ContactSearchActivity extends BaseActivity implements OnItemClickListener{
	
	private TextView tv_add_from_system_contact;
	private EditText et_search;
	private TextView tv_cancle;
	private ListView mListView;
	
	private InputMethodManager inputMethodManager;
	
	private AddFriendAdapter mAdapter;
	
	public static Boolean flag = false;
	
	List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	
	YoYo.AnimationComposer shakeAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Transparent);    
		setContentView(R.layout.activity_contact_search);
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initView();
	}
	
	public void initView() {
		
		shakeAnimation = new AnimationComposer(new ShakeAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		et_search = (EditText) findViewById(R.id.contact_header_search_et);
		tv_add_from_system_contact = (TextView) findViewById(R.id.contact_add_friend_from_system_contact);
		tv_cancle = (TextView) findViewById(R.id.contact_header_search_cancle_btn);
		mListView = (ListView) findViewById(R.id.contact_fragment_search_list);
		tv_cancle.setClickable(true);
		
		tv_add_from_system_contact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ContactSearchActivity.this, AddFriendsFromAddressBookActivity.class);
				startAnimActivity(intent);
			}
		});
		
		tv_cancle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				
				perfomSearch();
				
				return false;
			}
		});
		
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (s.toString().equals("")) {
					mListView.setVisibility(View.INVISIBLE);
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
		
		
		mAdapter = new AddFriendAdapter(ContactSearchActivity.this, users);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(this);
		
		et_search.requestFocus();
		inputMethodManager.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);
	}
	
	
	String searchName ="";
	public void perfomSearch() {
		// ����
		if (!isNetAvailable()) {
			ShowToast(R.string.network_tips);
			return;
		}
		else {
			users.clear();
			searchName = et_search.getText().toString();
			if(searchName!=null && !searchName.equals("")){
				
				initSearchList(false);
			}else{
				ShowToast("�������û���");
				shakeAnimation.playOn(et_search);
			}
		}
	}
	
	int curPage = 0;
	CustomProgressDialog progress ;
	private void initSearchList(final boolean isUpdate){
		if(!isUpdate){
			progress = new CustomProgressDialog(ContactSearchActivity.this, "��������...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
		// ����ͨ���ǳƻ����û�������
		userManager.queryUser(searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i("��ѯ����:"+arg1);
				if(users!=null){
					users.clear();
				}
				ShowToast("��ѯ�����û�������");
				shakeAnimation.playOn(et_search);
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				
				if (CollectionUtils.isNotNull(arg0)) {
					if(isUpdate){
						users.clear();
					}
					mListView.setVisibility(View.VISIBLE);
					mAdapter.addAll(arg0);
				}else{
					BmobLog.i("�޷���ֵ");
					if(users!=null){
						users.clear();
					}
					ShowToast("�û�������");
					
					shakeAnimation.playOn(et_search);
				}
				if(!isUpdate){
					progress.dismiss();
				}
			}
		});
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// ����Ϸ
		
				// �Ѿ��Ǻ����˾Ϳ��Կ�������
				if (flag) {
					
					BmobChatUser user = (BmobChatUser) mAdapter.getItem(position);
					Intent intent3 = new Intent(ContactSearchActivity.this, ChatActivity.class);
					intent3.putExtra("user", user);
					startAnimActivity(intent3);
				}
				// �����Ǻ��ѣ���ʱ���ܿ����ϣ�Ҫ������Ϸ
				else {
					
					// ShowToast("NOT FRIEND");
					BmobChatUser user = (BmobChatUser) mAdapter.getItem(position);
					userManager.queryUser(user.getUsername(), new FindListener<User>() {

						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							ShowToast(R.string.network_tips);
							return;
						}

						@Override
						public void onSuccess(List<User> arg0) {
							// TODO Auto-generated method stub
							User user = arg0.get(0);
							Intent intent = new Intent();
							
							int gamedifficultNum = 0;
							if (user.getGameDifficulty().equals("��")) {
								gamedifficultNum = 0;
							}else if (user.getGameDifficulty().equals("һ��")) {
								gamedifficultNum = 1;
							}else if (user.getGameDifficulty().equals("����")) {
								gamedifficultNum = 2;
							}
							
							String gameType = user.getGameType();
							
							if (gameType.equals("ˮ��������")) {
								intent.setClass(ContactSearchActivity.this, GameFruitActivity.class);
							}
							else if (gameType.equals("������")) {
								intent.setClass(ContactSearchActivity.this, GuessNumberActivity.class);
							}
							else if (gameType.equals("mixed color")) {
								intent.setClass(ContactSearchActivity.this, MixedColorMenuActivity.class);
							}else if(gameType.equals("oh my egg")){
								
								Boolean flag = CustomApplcation.isAppInstalled(ContactSearchActivity.this, "com.nsu.ttgame.ohmyeggs");
								
								if (flag) {
									intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
									        .parse("info://��������Ӧ�ó����Activity" ));  
									//  ��������   
									intent.putExtra("value", gamedifficultNum);  
								}
								else {
									
									DialogTips dialogTips = new DialogTips(ContactSearchActivity.this, 
											"�Է����õ���Ϸ�ǣ�" + gameType + "������û�а�װ����Ϸ���뵽��Ϸ���Ľ��а�װ��", "ȷ��");
									dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											
										}
									});
									dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											
										}
									});
									
									dialogTips.show();
									
								}
							}
							else if (gameType.equals("��ȭ���ƴ")) {
								Boolean flag = CustomApplcation.isAppInstalled(ContactSearchActivity.this, "com.jk.fingerGame");
								
								if (flag) {
									intent = new  Intent("com.jk.fingerGame.MYACTION" , Uri  
									        .parse("info://��������Ӧ�ó����Activity" ));  
									//  ��������   
									intent.putExtra("value", gamedifficultNum);  
								}
								else {
									
									DialogTips dialogTips = new DialogTips(ContactSearchActivity.this, 
											"�Է����õ���Ϸ�ǣ�" + gameType + "������û�а�װ����Ϸ���뵽��Ϸ���Ľ��а�װ��", "ȷ��");
									dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											
										}
									});
									dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											
										}
									});
									
									dialogTips.show();

								}
							}

							Bundle data = new Bundle();
							data.putString("from", "other");
							data.putString("username", user.getUsername());
							data.putString("gamedifficulty", user.getGameDifficulty());
							
							intent.putExtras(data);
							
							ShowToast(user.getGameType() + "");
							if (gameType.equals("oh my egg") || gameType.equals("��ȭ���ƴ")) {
								startActivityForResult(intent, 1); 
							}else {
								startActivity(intent);
							}
							
						}
					});
				}
	}
	
	@Override   
	protected  void  onActivityResult(int  requestCode, int  resultCode, Intent data)  {  
		
		switch (requestCode) {
		case 1:
			
			int gameResult = data.getExtras().getInt("result");
			
			// Ӯ��
			if (gameResult == 1) {
				BmobChatUser user = users.get(0);
				
				if (((User) user).getGameType().equals("oh my egg")) {
					updateOhMyEggGameBest(data.getExtras().getLong("gameTime"));
				}
				else if(((User) user).getGameType().equals("��ȭ���ƴ")){
					updateFingerGameBest(data.getExtras().getInt("gameScore"));
				}
				
				Intent intent = new Intent();
				intent.setClass(ContactSearchActivity.this, SetMyInfoActivity2.class);
				intent.putExtra("from", "add");
				intent.putExtra("username", user.getUsername());
				startActivity(intent);
			}
			// ����
			else if(gameResult == 0){
				
			}
			
			break;

		default:
			break;
		}
	      
	} 
	
	public void updateOhMyEggGameBest(final long time) {
		new Thread(){
			
			@Override
			public void run(){
				
				BmobQuery<GameFile> query = new BmobQuery<GameFile>();
				query.addWhereEqualTo("packageName", "com.nsu.ttgame.ohmyeggs");
				query.addWhereGreaterThan("bestScore", time);
				query.findObjects(ContactSearchActivity.this, new FindListener<GameFile>() {
					
					@Override
					public void onSuccess(List<GameFile> arg0) {
						// TODO Auto-generated method stub
						if (arg0.size() != 0) {
							GameFile gameFile = arg0.get(0);
							gameFile.setBestScore((int)time);
							gameFile.setBestUser(CustomApplcation.getInstance().getCurrentUser());
							
							gameFile.update(ContactSearchActivity.this, new UpdateListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									ActivityUtil.show(ContactSearchActivity.this, "���ѳ�Ϊoh my egg�����ң�������Ϸ�����ɹ���");
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									ActivityUtil.show(ContactSearchActivity.this, "������Ϸ����ʧ�ܣ�");
								}
							});
							
						}
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ActivityUtil.show(ContactSearchActivity.this, "������Ϸ����ʧ�ܣ�");
					}
				});
			}
			
		}.start();
	}

	public void updateFingerGameBest(final int mark) {
	new Thread(){
		
		@Override
		public void run(){
			
			BmobQuery<GameFile> query = new BmobQuery<GameFile>();
			query.addWhereEqualTo("packageName", "com.jk.fingerGame");
			query.addWhereLessThan("bestScore", mark);
			query.findObjects(ContactSearchActivity.this, new FindListener<GameFile>() {
				
				@Override
				public void onSuccess(List<GameFile> arg0) {
					// TODO Auto-generated method stub
					if (arg0.size() != 0) {
						GameFile gameFile = arg0.get(0);
						gameFile.setBestScore(mark);
						gameFile.setBestUser(CustomApplcation.getInstance().getCurrentUser());
						
						gameFile.update(ContactSearchActivity.this, new UpdateListener() {
							
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								ActivityUtil.show(ContactSearchActivity.this, "���ѳ�Ϊ��ȭ���ƴ�����ң�������Ϸ�����ɹ���");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ActivityUtil.show(ContactSearchActivity.this, "������Ϸ����ʧ�ܣ�");
							}
						});
						
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ActivityUtil.show(ContactSearchActivity.this, "������Ϸ����ʧ�ܣ�");
				}
			});
		}
		
		}.start();
	}
}
