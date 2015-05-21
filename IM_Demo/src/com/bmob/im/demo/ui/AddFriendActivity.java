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

/** ��Ӻ���
  * @ClassName: AddFriendActivity
  * @Description: TODO
  * @author smile
  * @date 2014-6-5 ����5:26:41
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
//		initTopBarForLeft("���Һ���");
				
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
//		// ���Ȳ�������ظ���
//		mListView.setPullLoadEnable(false);
//		// ����������
//		mListView.setPullRefreshEnable(false);
//		// ���ü�����
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
			progress = new CustomProgressDialog(AddFriendActivity.this, "��������...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		
//		userManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>() {
//
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				BmobLog.i("��ѯ����:"+arg1);
//				if(users!=null){
//					users.clear();
//				}
//				btn_search.setProgress(-1);
//				ShowToast("��ѯ�����û�������");
////				mListView.setPullLoadEnable(false);
////				refreshPull();
//				//�����ܱ�֤ÿ�β�ѯ���Ǵ�ͷ��ʼ
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
//						ShowToast("�û��������!");
//					}else{
////						mListView.setPullLoadEnable(true);
//					}
//				}else{
//					BmobLog.i("��ѯ�ɹ�:�޷���ֵ");
//					if(users!=null){
//						users.clear();
//					}
//					ShowToast("��ѯ�ɹ�:�޷���ֵ���û�������");
//				}
//				if(!isUpdate){
//					progress.dismiss();
//				}else{
////					refreshPull();
//				}
//				//�����ܱ�֤ÿ�β�ѯ���Ǵ�ͷ��ʼ
//				curPage = 0;
//			}
//		});
		
		// ����ͨ���ǳƻ����û�������
		userManager.queryUser(searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				BmobLog.i("��ѯ����:"+arg1);
				if(users!=null){
					users.clear();
				}
				btn_search.setProgress(-1);
				ShowToast("��ѯ�����û�������");
				shakeAnimation.playOn(et_find_name);
//				mListView.setPullLoadEnable(false);
//				refreshPull();
				//�����ܱ�֤ÿ�β�ѯ���Ǵ�ͷ��ʼ
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
						ShowToast("�û��������!");
					}else{
//						mListView.setPullLoadEnable(true);
					}
				}else{
					BmobLog.i("�޷���ֵ");
					if(users!=null){
						users.clear();
					}
					ShowToast("�û�������");
					
					shakeAnimation.playOn(et_find_name);
				}
				if(!isUpdate){
					progress.dismiss();
				}else{
//					refreshPull();
				}
				//�����ܱ�֤ÿ�β�ѯ���Ǵ�ͷ��ʼ
				// curPage = 0;
			}
		});
		
	}
	
	/** ��ѯ����
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
				ShowLog("���������û�����:"+arg1);
//				mListView.setPullLoadEnable(false);
//				refreshLoad();
			}

		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub	
		
		 // ����Ϸ
		
		// �Ѿ��Ǻ����˾Ϳ��Կ�������
		if (flag) {
			ShowToast("FRIEND");
			BmobChatUser user = (BmobChatUser) adapter.getItem(position);
			Intent intent =new Intent(this,SetMyInfoActivity2.class);
			intent.putExtra("from", "add");
			intent.putExtra("username", user.getUsername());
			startAnimActivity(intent);	
		}
		// �����Ǻ��ѣ���ʱ���ܿ����ϣ�Ҫ������Ϸ
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
					if (user.getGameDifficulty().equals("��")) {
						gamedifficultNum = 0;
					}else if (user.getGameDifficulty().equals("һ��")) {
						gamedifficultNum = 1;
					}else if (user.getGameDifficulty().equals("����")) {
						gamedifficultNum = 2;
					}
					
					String gameType = user.getGameType();
					
					if (gameType.equals("ˮ��������")) {
						intent.setClass(AddFriendActivity.this, GameFruitActivity.class);
					}
					else if (gameType.equals("������")) {
						intent.setClass(AddFriendActivity.this, GuessNumberActivity.class);
					}
					else if (gameType.equals("mixed color")) {
						intent.setClass(AddFriendActivity.this, MixedColorMenuActivity.class);
					}else if(gameType.equals("oh my egg")){
						
						Boolean flag = CustomApplcation.isAppInstalled(AddFriendActivity.this, "com.nsu.ttgame.ohmyeggs");
						
						if (flag) {
							intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
							        .parse("info://��������Ӧ�ó����Activity" ));  
							//  ��������   
							intent.putExtra("value", gamedifficultNum);  
						}
						else {
							
							DialogTips dialogTips = new DialogTips(AddFriendActivity.this, 
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
						Boolean flag = CustomApplcation.isAppInstalled(AddFriendActivity.this, "com.jk.fingerGame");
						
						if (flag) {
							intent = new  Intent("com.jk.fingerGame.MYACTION" , Uri  
							        .parse("info://��������Ӧ�ó����Activity" ));  
							//  ��������   
							intent.putExtra("value", gamedifficultNum);  
						}
						else {
							
							DialogTips dialogTips = new DialogTips(AddFriendActivity.this, 
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
				intent.setClass(AddFriendActivity.this, SetMyInfoActivity2.class);
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
									ActivityUtil.show(AddFriendActivity.this, "���ѳ�Ϊoh my egg�����ң�������Ϸ�����ɹ���");
								}
								
								@Override
								public void onFailure(int arg0, String arg1) {
									// TODO Auto-generated method stub
									ActivityUtil.show(AddFriendActivity.this, "������Ϸ����ʧ�ܣ�");
								}
							});
							
						}
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ActivityUtil.show(AddFriendActivity.this, "������Ϸ����ʧ�ܣ�");
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
								ActivityUtil.show(AddFriendActivity.this, "���ѳ�Ϊ��ȭ���ƴ�����ң�������Ϸ�����ɹ���");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ActivityUtil.show(AddFriendActivity.this, "������Ϸ����ʧ�ܣ�");
							}
						});
						
					}
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					ActivityUtil.show(AddFriendActivity.this, "������Ϸ����ʧ�ܣ�");
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
		case R.id.btn_search://����
			
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
					ShowToast("�������û���");
					shakeAnimation.playOn(et_find_name);
					btn_search.setProgress(-1);
				}
				
			}
			
			
			break;
			
//		// ���ͨѶ¼����
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
//					ShowToast("���ݼ������");
//					mListView.setPullLoadEnable(false);
//					refreshLoad();
//				}
//			}
//			
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				ShowLog("��ѯ������������ʧ��"+arg1);
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
