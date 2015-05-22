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
		// 搜索
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
				ShowToast("请输入用户名");
				shakeAnimation.playOn(et_search);
			}
		}
	}
	
	int curPage = 0;
	CustomProgressDialog progress ;
	private void initSearchList(final boolean isUpdate){
		if(!isUpdate){
			progress = new CustomProgressDialog(ContactSearchActivity.this, "正在搜索...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
		// 可以通过昵称或者用户名查找
		userManager.queryUser(searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i("查询错误:"+arg1);
				if(users!=null){
					users.clear();
				}
				ShowToast("查询错误，用户不存在");
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
					BmobLog.i("无返回值");
					if(users!=null){
						users.clear();
					}
					ShowToast("用户不存在");
					
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
		// 玩游戏
		
				// 已经是好友了就可以看资料了
				if (flag) {
					
					BmobChatUser user = (BmobChatUser) mAdapter.getItem(position);
					Intent intent3 = new Intent(ContactSearchActivity.this, ChatActivity.class);
					intent3.putExtra("user", user);
					startAnimActivity(intent3);
				}
				// 还不是好友，此时不能看资料，要先玩游戏
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
							if (user.getGameDifficulty().equals("简单")) {
								gamedifficultNum = 0;
							}else if (user.getGameDifficulty().equals("一般")) {
								gamedifficultNum = 1;
							}else if (user.getGameDifficulty().equals("困难")) {
								gamedifficultNum = 2;
							}
							
							String gameType = user.getGameType();
							
							if (gameType.equals("水果连连看")) {
								intent.setClass(ContactSearchActivity.this, GameFruitActivity.class);
							}
							else if (gameType.equals("猜数字")) {
								intent.setClass(ContactSearchActivity.this, GuessNumberActivity.class);
							}
							else if (gameType.equals("mixed color")) {
								intent.setClass(ContactSearchActivity.this, MixedColorMenuActivity.class);
							}else if(gameType.equals("oh my egg")){
								
								Boolean flag = CustomApplcation.isAppInstalled(ContactSearchActivity.this, "com.nsu.ttgame.ohmyeggs");
								
								if (flag) {
									intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
									        .parse("info://调用其他应用程序的Activity" ));  
									//  传递数据   
									intent.putExtra("value", gamedifficultNum);  
								}
								else {
									
									DialogTips dialogTips = new DialogTips(ContactSearchActivity.this, 
											"对方设置的游戏是：" + gameType + "，您还没有安装该游戏！请到游戏中心进行安装！", "确认");
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
							else if (gameType.equals("猜拳大比拼")) {
								Boolean flag = CustomApplcation.isAppInstalled(ContactSearchActivity.this, "com.jk.fingerGame");
								
								if (flag) {
									intent = new  Intent("com.jk.fingerGame.MYACTION" , Uri  
									        .parse("info://调用其他应用程序的Activity" ));  
									//  传递数据   
									intent.putExtra("value", gamedifficultNum);  
								}
								else {
									
									DialogTips dialogTips = new DialogTips(ContactSearchActivity.this, 
											"对方设置的游戏是：" + gameType + "，您还没有安装该游戏！请到游戏中心进行安装！", "确认");
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
							if (gameType.equals("oh my egg") || gameType.equals("猜拳大比拼")) {
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
			
			// 赢了
			if (gameResult == 1) {
				BmobChatUser user = users.get(0);
				
				if (((User) user).getGameType().equals("oh my egg")) {
					updateOhMyEggGameBest(data.getExtras().getLong("gameTime"));
				}
				else if(((User) user).getGameType().equals("猜拳大比拼")){
					updateFingerGameBest(data.getExtras().getInt("gameScore"));
				}
				
				Intent intent = new Intent();
				intent.setClass(ContactSearchActivity.this, SetMyInfoActivity2.class);
				intent.putExtra("from", "add");
				intent.putExtra("username", user.getUsername());
				startActivity(intent);
			}
			// 输了
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
									ActivityUtil.show(ContactSearchActivity.this, "你已成为oh my egg最佳玩家，更新游戏排名成功！");
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									ActivityUtil.show(ContactSearchActivity.this, "更新游戏排名失败！");
								}
							});
							
						}
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ActivityUtil.show(ContactSearchActivity.this, "更新游戏排名失败！");
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
								ActivityUtil.show(ContactSearchActivity.this, "你已成为猜拳大比拼最佳玩家，更新游戏排名成功！");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ActivityUtil.show(ContactSearchActivity.this, "更新游戏排名失败！");
							}
						});
						
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ActivityUtil.show(ContactSearchActivity.this, "更新游戏排名失败！");
				}
			});
		}
		
		}.start();
	}
}
