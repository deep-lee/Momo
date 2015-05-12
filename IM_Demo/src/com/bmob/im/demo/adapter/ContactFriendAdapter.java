package com.bmob.im.demo.adapter;

import java.util.List;
import com.bmob.im.demo.ContactsInfo;
import com.bmob.im.demo.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ContactFriendAdapter extends BaseAdapter {
	
	List<ContactsInfo> mData;
	Context mContent;
	
	private LayoutInflater layoutInflater;

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
		
		ContactsInfo contactsInfo = (ContactsInfo) getItem(position);
		
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
		tv_contact_name.setText("Í¨Ñ¶Â¼ºÃÓÑ:" + contactsInfo.getContactsName());
		
		btn_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return convertView;
	}

}
