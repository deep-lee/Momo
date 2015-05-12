package com.bmob.im.demo.adapter;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.bmob.im.demo.ContactsInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ContactFriendAdapter extends BaseAdapter {
	
	List<ContactsInfo> mData;
	Context mContent;
	
	private LayoutInflater layoutInflater;
	
	User user;

	public ContactFriendAdapter(Context mContent, List<ContactsInfo> mData) {
		super();
		this.mData = mData;
		this.mContent = mContent;
		layoutInflater = LayoutInflater.from(mContent);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_add_friends_from_contact, null);
		}
		
		final ContactsInfo contactsInfo = (ContactsInfo) getItem(position);
		
		ImageView iv_contact_avatar;
		TextView tv_contact_nick;
		TextView tv_contact_name;
		Button btn_add;
		
		iv_contact_avatar = (ImageView) convertView.findViewById(R.id.system_contact_avatar);
		tv_contact_nick = (TextView) convertView.findViewById(R.id.system_contact_nick);
		tv_contact_name = (TextView) convertView.findViewById(R.id.system_contact_name);
		btn_add = (Button) convertView.findViewById(R.id.system_contact_add);
		
		iv_contact_avatar.setImageBitmap(contactsInfo.getAvatar());
		tv_contact_nick.setText(contactsInfo.getContactNick());
		tv_contact_name.setText("通讯录好友:" + contactsInfo.getContactsName());
		
		if (contactsInfo.getAvatar_url() != null && !contactsInfo.getAvatar_url().equals("")) {
			ImageLoader.getInstance().displayImage(contactsInfo.getAvatar_url(), iv_contact_avatar,
					ImageLoadOptions.getOptions());
		
		} else {
			
			// 否则显示默认的头像
			iv_contact_avatar.setImageResource(R.drawable.default_avatar);
		}
		
		btn_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				user = null;
				
				//根据账号查找数据
				BmobQuery<User> query = new BmobQuery<User>();
				query.addWhereEqualTo("username", contactsInfo.getContactsPhone());
				query.findObjects(mContent, new FindListener<User>() {
					
					@Override
					public void onSuccess(List<User> arg0) {
						// TODO Auto-generated method stub
						user = arg0.get(0);
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				
				if (user == null) {
					Toast.makeText(mContent, "发送请求失败！", Toast.LENGTH_LONG).show();
					return;
				}
				
				// 发送添加请求
				final CustomProgressDialog progress = new CustomProgressDialog(mContent, "正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				//发送tag请求
				BmobChatManager.getInstance(mContent).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						Toast.makeText(mContent, "发送请求成功，等待对方验证!", Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						Toast.makeText(mContent, "发送请求失败，请重新添加!", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		
		return convertView;
	}

}
