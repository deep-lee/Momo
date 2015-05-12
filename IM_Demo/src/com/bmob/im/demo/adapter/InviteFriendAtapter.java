package com.bmob.im.demo.adapter;

import java.util.List;

import com.bmob.im.demo.ContactsInfo;
import com.bmob.im.demo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class InviteFriendAtapter extends BaseAdapter {
	
	List<ContactsInfo> mData;
	Context mContent;
	
	private LayoutInflater layoutInflater;

	public InviteFriendAtapter(Context mContent, List<ContactsInfo> mData) {
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
			convertView = layoutInflater.inflate(R.layout.item_invite_firend_form_contact, null);
		}
		
		final ContactsInfo contactsInfo = (ContactsInfo) getItem(position);
		
		ImageView iv_contact_avatar;
		TextView tv_contact_name;
		Button btn_invite;
		
		iv_contact_avatar = (ImageView) convertView.findViewById(R.id.system_contact_avatar);
		tv_contact_name = (TextView) convertView.findViewById(R.id.system_contact_name);
		btn_invite = (Button) convertView.findViewById(R.id.system_contact_invite);
		
		iv_contact_avatar.setImageBitmap(contactsInfo.getAvatar());
		tv_contact_name.setText(contactsInfo.getContactsName());
		
		btn_invite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 发送短信请求
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.putExtra("address", contactsInfo.getContactsPhone());
				intent.putExtra("sms_body", "我正在玩摇摇这款应用，你也一起来玩吧！下载链接：http://jiemimomo.bmob.cn/，么么哒，等你哟！");
				
				intent.setType("vnd.android-dir/mms-sms");
				mContent.startActivity(intent);
			}
		});
		
		return convertView;
	}

}
