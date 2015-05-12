package com.bmob.im.demo.ui;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GameCardAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CharacterParser;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class GameCenterActivity extends ActivityBase {
	
	private ListView mListView;  
	
//	private ClearEditText mClearEditText;
	
	public static User user;
	
	int currentList = 0;
	
	GameCardAdapter mAdapter;
	CustomProgressDialog dialog;
	
	public ImageView iv_search;
	
	Boolean flag = false;
	
	ImageView iv_back;
	
	public EditText et_search;
	public TextView title;
	
	private InputMethodManager inputMethodManager;
	
	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		setContentView(R.layout.activity_game_center);
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
//		initTopBarForLeft("��Ϸ����");
		
		
		characterParser = CharacterParser.getInstance();
		
		
		user = userManager.getCurrentUser(User.class);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_search = (ImageView) findViewById(R.id.iv_search);
		et_search = (EditText) findViewById(R.id.game_center_search_et);
		title = (TextView) findViewById(R.id.tv_title);
		
		iv_search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iv_search.setVisibility(View.INVISIBLE);
				title.setVisibility(View.INVISIBLE);
				et_search.setVisibility(View.VISIBLE);
				
				et_search.requestFocus();
				
				inputMethodManager.showSoftInput(et_search, InputMethodManager.SHOW_FORCED);
				
				flag = true;
			}
		});
		
		iv_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag) {
					iv_search.setVisibility(View.VISIBLE);
					title.setVisibility(View.VISIBLE);
					et_search.setVisibility(View.INVISIBLE);
					et_search.setText("");
					filterData("");
					
					et_search.clearFocus(); 
					
					inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
					
					flag = false;
				}else {
					finish();
				}
			}
		});
		
		et_search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
				// ����������е�ֵ���������ݲ�����ListView
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
						
			}
		});
		
//		mClearEditText = (ClearEditText) findViewById(R.id.et_game_search);
//		
//		
//		// �������������ֵ�ĸı�����������
//		mClearEditText.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
//				// ����������е�ֵ���������ݲ�����ListView
//				filterData(s.toString());
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//						
//			}
//		});
		
		 mListView=(ListView) findViewById(R.id.game_list_view);  
	     mAdapter = new GameCardAdapter(this);  
	     mListView.setAdapter(mAdapter);  
	     
	}
	
	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GameCard> filterDateList = new ArrayList<GameCard>();
		if (TextUtils.isEmpty(filterStr)) {
			// ������������Ϊ��ʱ�������еĺ�������б�
			filterDateList = CustomApplcation.gameCardList;
		} else {
			filterDateList.clear();
			for (GameCard sortModel : CustomApplcation.gameCardList) {
				String name = sortModel.getGameName();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1
							|| characterParser.getSelling(name).startsWith(
									filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		// ����a-z��������
		// Collections.sort(filterDateList, pinyinComparator);
		mAdapter.updateListView(filterDateList);
	}
	
	public void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// ShowToast("OnResume");
		
		if (isAppInstalled(GameCenterActivity.this, "com.nsu.ttgame.ohmyeggs")) {
			CustomApplcation.gameCardList.get(3).setGameStatus(2);
			
			ShowToast("Installed");
		}else {
			if (isApkDownloaded(CustomApplcation.gameList.get(3) + "_" + CustomApplcation.notificationId.get(3))) {
				CustomApplcation.gameCardList.get(3).setGameStatus(1);
			}
			else {
				CustomApplcation.gameCardList.get(3).setGameStatus(0);
			}
		}
		
		// mAdapter.updateListView(CustomApplcation.gameCardList);
		mAdapter.notifyDataSetChanged();
		
	}
	
	
	// �ж�Ӧ���Ƿ�װ
		public boolean isAppInstalled(Context context,String packagename)
	    {
	    	PackageInfo packageInfo; 
	    	
	    	try {
	                
	    		packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
	             
	    	}catch (NameNotFoundException e) {
	    		
	    		packageInfo = null;
	    		
	    		e.printStackTrace();
	    		
	    	}
	             
	    	if(packageInfo ==null){
	    		
	    		return false;
	    		
	    	}else{
	    		
	    		return true;
	    		
	    	}
	    }
	    
		public Boolean isApkDownloaded(String fileName) {
			
			String apkDir = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
			File rootFile = new File(apkDir);
			
			if (!rootFile.exists()) {
				return false;
			}
			
			File tempFile = new File(apkDir + fileName + ".apk");
			if (tempFile.exists()){
				return true;
			}else {
				return false;
			}
			
		}
	
	
}
