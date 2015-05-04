package com.bmob.im.demo.ui;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.GameCardAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CharacterParser;
import com.bmob.im.demo.util.PinyinComparator;
import com.bmob.im.demo.view.ClearEditText;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.ListView;


public class GameCenterActivity extends ActivityBase {
	
	private ListView mListView;  
	
	private ClearEditText mClearEditText;
	
	public static User user;
	
	int currentList = 0;
	
	GameCardAdapter mAdapter;
	CustomProgressDialog dialog;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		setContentView(R.layout.activity_game_center);
		
		initTopBarForLeft("游戏中心");
		
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		// 女性主题
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
			setActionBarRightBtnForFemale();
		}
		
		user = userManager.getCurrentUser(User.class);
		
		mClearEditText = (ClearEditText) findViewById(R.id.et_game_search);
		
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				// 根据输入框中的值来过滤数据并更新ListView
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
		
		 mListView=(ListView) findViewById(R.id.game_list_view);  
	     mAdapter = new GameCardAdapter(this, CustomApplcation.gameCardList);  
	     mListView.setAdapter(mAdapter);  
	     
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GameCard> filterDateList = new ArrayList<GameCard>();
		if (TextUtils.isEmpty(filterStr)) {
			// 当搜索的内容为空时，用所有的好友填充列表
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
		// 根据a-z进行排序
		// Collections.sort(filterDateList, pinyinComparator);
		mAdapter.updateListView(filterDateList);
	}
	
	public void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
