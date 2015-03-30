package com.bmob.im.demo.ui.fragment;

import java.net.ContentHandler;
import java.util.Arrays;
import java.util.List;














import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.FindCallback;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LeftFragment extends Fragment {

	private View mView;  
    private ListView mCategories;  
    BmobUserManager userManager;
    
    Context context;
    
    ImageView slideAvator;
    TextView slideNick;
    
    private List<String> mDatas = Arrays  
            .asList("我的游戏","设置","帮助");  
    private ListAdapter mAdapter;  
    
    
  
    public LeftFragment(Context context) {
		super();
		this.context = context;
	}

	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        if (mView == null)  
        {  
            initView(inflater, container);  
        }  
        return mView;  
    }  
  
    private void initView(LayoutInflater inflater, ViewGroup container)  
    {  
        mView = inflater.inflate(R.layout.slide_fragment, container, false); 
        
        userManager = BmobUserManager.getInstance(context);
        
        User user = userManager.getCurrentUser(User.class);
        
        slideAvator = (ImageView) mView.findViewById(R.id.slide_avator);
        slideNick = (TextView) mView.findViewById(R.id.slide_nick);
        
        refreshAvatar(user.getAvatar());
        slideNick.setText(user.getNick());
        
        mCategories = (ListView) mView  
                .findViewById(R.id.slide_listview);  
        mAdapter = new ArrayAdapter<String>(getActivity(),  
                android.R.layout.simple_list_item_1, mDatas);  
        mCategories.setAdapter(mAdapter);  
        //菜单点击事件
        mCategories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				switch (position) {
				case 0:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;
				case 1:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;
				case 2:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;			
				}
			}
        	
		});
    }
    
    
	
	/**
	 * 更新头像 refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, slideAvator,
					ImageLoadOptions.getOptions());
		} else {
			
			// 否则显示默认的头像
			slideAvator.setImageResource(R.drawable.default_head);
		}
	}
}
