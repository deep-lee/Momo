package com.bmob.im.demo.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.AddFriendAdapter;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ActivityUtil;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.YoYo.AnimationComposer;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;
import com.dd.library.CircularProgressButton;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

/** 添加好友
  * @ClassName: AddFriendActivity
  * @Description: TODO
  * @author smile
  * @date 2014-6-5 下午5:26:41
  */
public class AddFriendActivity extends ActivityBase implements OnClickListener, OnItemClickListener{
	
	EditText et_find_name;
	CircularProgressButton btn_search;
	
	List<BmobChatUser> users = new ArrayList<BmobChatUser>();
	ListView mListView;
	AddFriendAdapter adapter;
	
	TextView tv_add_from_tongxunlu;
	
	public static Boolean flag = false;
	
	YoYo.AnimationComposer shakeAnimation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		initView();
	}
	
	private void initView(){
//		initTopBarForLeft("查找好友");
				
		et_find_name = (EditText)findViewById(R.id.et_find_name);
		btn_search = (CircularProgressButton)findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		    
		tv_add_from_tongxunlu = (TextView) findViewById(R.id.tv_add_from_tongxunlu);
		
		shakeAnimation = new AnimationComposer(new ShakeAnimator())
		.duration(500)
		.interpolate(new AccelerateDecelerateInterpolator());
		
		initListView();
	}

	private void initListView() {
		mListView = (ListView) findViewById(R.id.list_search);
//		// 首先不允许加载更多
//		mListView.setPullLoadEnable(false);
//		// 不允许下拉
//		mListView.setPullRefreshEnable(false);
//		// 设置监听器
//		mListView.setXListViewListener(this);
//		//
//		mListView.pullRefreshing();
		
		adapter = new AddFriendAdapter(this, users);
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(this);
	}
	
	int curPage = 0;
	CustomProgressDialog progress ;
	private void initSearchList(final boolean isUpdate){
		if(!isUpdate){
			progress = new CustomProgressDialog(AddFriendActivity.this, "正在搜索...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
//		userManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>() {
//
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				BmobLog.i("查询错误:"+arg1);
//				if(users!=null){
//					users.clear();
//				}
//				btn_search.setProgress(-1);
//				ShowToast("查询错误，用户不存在");
////				mListView.setPullLoadEnable(false);
////				refreshPull();
//				//这样能保证每次查询都是从头开始
//				curPage = 0;
//			}
//
//			@Override
//			public void onSuccess(List<BmobChatUser> arg0) {
//				// TODO Auto-generated method stub
//				btn_search.setProgress(0);
//				
//				if (CollectionUtils.isNotNull(arg0)) {
//					if(isUpdate){
//						users.clear();
//					}
//					adapter.addAll(arg0);
//					if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
////						mListView.setPullLoadEnable(false);
//						ShowToast("用户搜索完成!");
//					}else{
////						mListView.setPullLoadEnable(true);
//					}
//				}else{
//					BmobLog.i("查询成功:无返回值");
//					if(users!=null){
//						users.clear();
//					}
//					ShowToast("查询成功:无返回值，用户不存在");
//				}
//				if(!isUpdate){
//					progress.dismiss();
//				}else{
////					refreshPull();
//				}
//				//这样能保证每次查询都是从头开始
//				curPage = 0;
//			}
//		});
		
		// 可以通过昵称或者用户名查找
		userManager.queryUser(searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i("查询错误:"+arg1);
				if(users!=null){
					users.clear();
				}
				btn_search.setProgress(-1);
				ShowToast("查询错误，用户不存在");
				shakeAnimation.playOn(et_find_name);
//				mListView.setPullLoadEnable(false);
//				refreshPull();
				//这样能保证每次查询都是从头开始
				// curPage = 0;
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				btn_search.setProgress(0);
				
				if (CollectionUtils.isNotNull(arg0)) {
					if(isUpdate){
						users.clear();
					}
					adapter.addAll(arg0);
					if(arg0.size()<BRequest.QUERY_LIMIT_COUNT){
//						mListView.setPullLoadEnable(false);
						ShowToast("用户搜索完成!");
					}else{
//						mListView.setPullLoadEnable(true);
					}
				}else{
					BmobLog.i("无返回值");
					if(users!=null){
						users.clear();
					}
					ShowToast("用户不存在");
					
					shakeAnimation.playOn(et_find_name);
				}
				if(!isUpdate){
					progress.dismiss();
				}else{
//					refreshPull();
				}
				//这样能保证每次查询都是从头开始
				// curPage = 0;
			}
		});
		
	}
	
	/** 查询更多
	  * @Title: queryMoreNearList
	  * @Description: TODO
	  * @param @param page 
	  * @return void
	  * @throws
	  */
	private void queryMoreSearchList(int page){
		userManager.queryUserByPage(true, page, searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				if (CollectionUtils.isNotNull(arg0)) {
					adapter.addAll(arg0);
				}
//				refreshLoad();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowLog("搜索更多用户出错:"+arg1);
//				mListView.setPullLoadEnable(false);
//				refreshLoad();
			}

		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub	
		
		 // 玩游戏
		
		// 已经是好友了就可以看资料了
		if (flag) {
			ShowToast("FRIEND");
			BmobChatUser user = (BmobChatUser) adapter.getItem(position);
			Intent intent =new Intent(this,SetMyInfoActivity2.class);
			intent.putExtra("from", "add");
			intent.putExtra("username", user.getUsername());
			startAnimActivity(intent);	
		}
		// 还不是好友，此时不能看资料，要先玩游戏
		else {
			
			ShowToast("NOT FRIEND");
			BmobChatUser user = (BmobChatUser) adapter.getItem(position);
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
//					ShowToast(arg0.get(0).getBirthday());
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
						intent.setClass(AddFriendActivity.this, GameFruitActivity.class);
					}
					else if (gameType.equals("猜数字")) {
						intent.setClass(AddFriendActivity.this, GuessNumberActivity.class);
					}
					else if (gameType.equals("mixed color")) {
						intent.setClass(AddFriendActivity.this, MixedColorMenuActivity.class);
					}else if(gameType.equals("oh my egg")){
						
						Boolean flag = CustomApplcation.isAppInstalled(AddFriendActivity.this, "com.nsu.ttgame.ohmyeggs");
						
						if (flag) {
							intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
							        .parse("info://调用其他应用程序的Activity" ));  
							//  传递数据   
							intent.putExtra("value", gamedifficultNum);  
						}
						else {
							
							DialogTips dialogTips = new DialogTips(AddFriendActivity.this, 
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
						Boolean flag = CustomApplcation.isAppInstalled(AddFriendActivity.this, "com.jk.fingerGame");
						
						if (flag) {
							intent = new  Intent("com.jk.fingerGame.MYACTION" , Uri  
							        .parse("info://调用其他应用程序的Activity" ));  
							//  传递数据   
							intent.putExtra("value", gamedifficultNum);  
						}
						else {
							
							DialogTips dialogTips = new DialogTips(AddFriendActivity.this, 
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
				intent.setClass(AddFriendActivity.this, SetMyInfoActivity2.class);
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
				query.findObjects(AddFriendActivity.this, new FindListener<GameFile>() {
					
					@Override
					public void onSuccess(List<GameFile> arg0) {
						// TODO Auto-generated method stub
						if (arg0.size() != 0) {
							GameFile gameFile = arg0.get(0);
							gameFile.setBestScore((int)time);
							gameFile.setBestUser(CustomApplcation.getInstance().getCurrentUser());
							
							gameFile.update(AddFriendActivity.this, new UpdateListener() {
								
								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									ActivityUtil.show(AddFriendActivity.this, "你已成为oh my egg最佳玩家，更新游戏排名成功！");
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									ActivityUtil.show(AddFriendActivity.this, "更新游戏排名失败！");
								}
							});
							
						}
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ActivityUtil.show(AddFriendActivity.this, "更新游戏排名失败！");
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
			query.findObjects(AddFriendActivity.this, new FindListener<GameFile>() {
				
				@Override
				public void onSuccess(List<GameFile> arg0) {
					// TODO Auto-generated method stub
					if (arg0.size() != 0) {
						GameFile gameFile = arg0.get(0);
						gameFile.setBestScore(mark);
						gameFile.setBestUser(CustomApplcation.getInstance().getCurrentUser());
						
						gameFile.update(AddFriendActivity.this, new UpdateListener() {
							
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								ActivityUtil.show(AddFriendActivity.this, "你已成为猜拳大比拼最佳玩家，更新游戏排名成功！");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ActivityUtil.show(AddFriendActivity.this, "更新游戏排名失败！");
							}
						});
						
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ActivityUtil.show(AddFriendActivity.this, "更新游戏排名失败！");
				}
			});
		}
		
	}.start();
}
	
	String searchName ="";
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_search://搜索
			
			if (btn_search.getProgress() == -1) {
				btn_search.setProgress(0);
				return;
			}
			
			if (!isNetAvailable()) {
				ShowToast(R.string.network_tips);
				btn_search.setProgress(-1);
				return;
			}
			else {
				
				users.clear();
				searchName = et_find_name.getText().toString();
				if(searchName!=null && !searchName.equals("")){
					
					btn_search.setProgress(50);
					
					initSearchList(false);
				}else{
					ShowToast("请输入用户名");
					shakeAnimation.playOn(et_find_name);
					btn_search.setProgress(-1);
				}
				
			}
			
			
			break;
			
//		// 添加通讯录好友
//		case R.id.tv_add_from_tongxunlu:
//			
//			
//			break;

		default:
			break;
		}
	}
	
	public void addFromSystemCOntact(View view) {
		// ShowToast("clicked");
		Intent intent = new Intent();
		intent.setClass(AddFriendActivity.this, AddFriendsFromAddressBookActivity.class);
		startAnimActivity(intent);
	}

//	@Override
//	public void onRefresh() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onLoadMore() {
//		// TODO Auto-generated method stub
//		userManager.querySearchTotalCount(searchName, new CountListener() {
//			
//			@Override
//			public void onSuccess(int arg0) {
//				// TODO Auto-generated method stub
//				if(arg0 >users.size()){
//					curPage++;
//					queryMoreSearchList(curPage);
//				}else{
//					ShowToast("数据加载完成");
//					mListView.setPullLoadEnable(false);
//					refreshLoad();
//				}
//			}
//			
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				ShowLog("查询附近的人总数失败"+arg1);
//				refreshLoad();
//			}
//		});
//	}
//	
//	private void refreshLoad(){
//		if (mListView.getPullLoading()) {
//			mListView.stopLoadMore();
//		}
//	}
//	
//	private void refreshPull(){
//		if (mListView.getPullRefreshing()) {
//			mListView.stopRefresh();
//		}
//	}
	

}
