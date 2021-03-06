package com.bmob.im.demo.ui.fragment;


import java.io.File;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.SlideAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.AboutActivity;
import com.bmob.im.demo.ui.EditPersonalizedSignatureActivity;
import com.bmob.im.demo.ui.GameCenterActivity;
import com.bmob.im.demo.ui.LoginActivity;
import com.bmob.im.demo.ui.MainActivity;
import com.bmob.im.demo.ui.SetMyInfoActivity2;
import com.bmob.im.demo.ui.SlideSetActivity;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


@SuppressLint("ValidFragment")
public class LeftFragment extends Fragment {

	private View mView;  
    private ListView mCategories;  
    BmobUserManager userManager;
    
    Context context;
    
    ImageView slideAvator;
    TextView slideNick;
    TextView slide_personalized_signature;
    RelativeLayout slide_personal_layout;
    
    String avatarStr = "";
    
    User user;
    
    
    List<Map<String, Object>> list;

    
    public LeftFragment() {

	}
  
   
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
        
        user = userManager.getCurrentUser(User.class);
        
        slideAvator = (ImageView) mView.findViewById(R.id.slide_avator);
        slideNick = (TextView) mView.findViewById(R.id.slide_nick);
        slide_personalized_signature = (TextView) mView.findViewById(R.id.slide_personal);
        
        slide_personal_layout = (RelativeLayout) mView.findViewById(R.id.slide_personal_layout);
        
        slide_personal_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入个性签名编辑页面
				Intent intent = new Intent();
				intent.setClass(context, EditPersonalizedSignatureActivity.class);
				startActivity(intent);
			}
		});
        
        if (user != null) {
        	
        	avatarStr = user.getAvatar();
        	refreshAvatar(avatarStr);
            slideNick.setText(user.getNick());
            
            // 个性签名
            if (user.getPersonalizedSignature() == null || user.getPersonalizedSignature().equals("未填写")) {
    			slide_personalized_signature.setText("说点什么吧...");
    		}else {
    			slide_personalized_signature.setText(user.getPersonalizedSignature());
    		}
            
		}
        
        mCategories = (ListView) mView  
                .findViewById(R.id.slide_listview);  
//        mAdapter = new ArrayAdapter<String>(getActivity(),  
//                android.R.layout.simple_list_item_1, mDatas);  
        
        SlideAdapter mAdapter = new SlideAdapter(context);   
        
        mCategories.setAdapter(mAdapter);  
        //菜单点击事件
        mCategories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				switch (position) {
				// 我的游戏
				case 0:
					Intent gameIntent = new Intent();
					gameIntent.setClass(context, GameCenterActivity.class);
					startActivity(gameIntent);
					break;
					
				case 1:
					Intent personIntent = new Intent();
					personIntent.setClass(context, SetMyInfoActivity2.class);
					personIntent.putExtra("from", "me");
					startActivity(personIntent);
					
					break;
					
				// 设置
				case 2:
					Intent intent = new Intent();
					intent.setClass(context, SlideSetActivity.class);
					startActivity(intent);
					break;	
					
					// 关于
				case 3:
					Intent aboutIntent = new Intent();
					aboutIntent.setClass(getActivity(), AboutActivity.class);
					aboutIntent.putExtra("flag", false);
					startActivity(aboutIntent);
					break;
					// 退出登录
				case 4:
					showLogoutDialog();
					break;
				}
			}
        	
		});
    }

    private void showLogoutDialog() {
    	DialogTips dialogTips = new DialogTips(context, "确认退出登陆？", "退出", "取消", "退出", false);
    	
    	dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CustomApplcation.getInstance().logout();
				((MainActivity)context).finish();
				CustomApplcation.myWallPhoto.clear();
				// 删除图片缓存的内容
				String imageDir = Environment.getExternalStorageDirectory().getPath()
						+ "/Bmob_IM_test/PhotoWallFalls/";
				File file = new File(imageDir);
				delete(file);
				
				startActivity(new Intent(context, LoginActivity.class));
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
	

    public static void delete(File file) {  
        if (file.isFile()) {  
            file.delete();  
            return;  
        }  
  
        if(file.isDirectory()){  
            File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {  
                file.delete();  
                return;  
            }  
      
            for (int i = 0; i < childFiles.length; i++) {  
                delete(childFiles[i]);  
            }  
            file.delete();  
        }  
   }


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// Toast.makeText(context, "更新界面", Toast.LENGTH_LONG).show();
		
		user = userManager.getCurrentUser(User.class);
		
		// 更新头像和个性签名和昵称
		if (!user.getAvatar().equals(avatarStr)) {
			refreshAvatar(user.getAvatar());
		}
		
		if (user.getPersonalizedSignature() == null || user.getPersonalizedSignature().equals("未填写")) {
			slide_personalized_signature.setText("说点什么吧...");
		}else {
			slide_personalized_signature.setText(user.getPersonalizedSignature());
		}
		
		slideNick.setText(user.getNick());
		
	} 
}
