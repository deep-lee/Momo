package com.bmob.im.demo.ui.fragment;


import java.io.File;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.SlideAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.AttractionsRomaActivity2;
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
import android.widget.Toast;


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
				// �������ǩ���༭ҳ��
				Intent intent = new Intent();
				intent.setClass(context, EditPersonalizedSignatureActivity.class);
				startActivity(intent);
			}
		});
        
        if (user != null) {
        	
        	avatarStr = user.getAvatar();
        	refreshAvatar(avatarStr);
            slideNick.setText(user.getNick());
            
            // ����ǩ��
            if (user.getPersonalizedSignature().equals("δ��д")) {
    			slide_personalized_signature.setText("˵��ʲô��...");
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
        //�˵�����¼�
        mCategories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				switch (position) {
				// �ҵ���Ϸ
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
					
				// ����
				case 2:
					Intent intent = new Intent();
					intent.setClass(context, SlideSetActivity.class);
					startActivity(intent);
					break;
					
					// �ص�����
				case 3:
					Intent romaIntent = new Intent();
					romaIntent.setClass(context, AttractionsRomaActivity2.class);
					startActivity(romaIntent);
					
					break;	
					
					// ����
				case 4:
					
					break;
					// �˳���¼
				case 5:
					showLogoutDialog();
					break;
				}
			}
        	
		});
    }

    private void showLogoutDialog() {
    	DialogTips dialogTips = new DialogTips(context, "ȷ���˳���½��", "�˳�", "ȡ��", "�˳�", false);
    	
    	dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CustomApplcation.getInstance().logout();
				((MainActivity)context).finish();
				CustomApplcation.myWallPhoto.clear();
				// ɾ��ͼƬ���������
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
	 * ����ͷ�� refreshAvatar
	 * 
	 * @return void
	 * @throws
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, slideAvator,
					ImageLoadOptions.getOptions());
			
			
		
		} else {
			
			// ������ʾĬ�ϵ�ͷ��
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
		
		// Toast.makeText(context, "���½���", Toast.LENGTH_LONG).show();
		
		user = userManager.getCurrentUser(User.class);
		
		// ����ͷ��͸���ǩ�����ǳ�
		if (!user.getAvatar().equals(avatarStr)) {
			refreshAvatar(user.getAvatar());
		}
		
		if (user.getPersonalizedSignature().equals("δ��д")) {
			slide_personalized_signature.setText("˵��ʲô��...");
		}else {
			slide_personalized_signature.setText(user.getPersonalizedSignature());
		}
		
		slideNick.setText(user.getNick());
		
	} 
    
    
    
    
}
