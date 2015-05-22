package com.bmob.im.demo.adapter;

import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.ChatActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.im.bean.BmobChatUser;
import a.thing;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<BmobChatUser> mData;
	
	private LayoutInflater layoutInflater;

	public SearchListAdapter(Context mContext, List<BmobChatUser> mData) {
		super();
		this.mContext = mContext;
		this.mData = mData;
		layoutInflater = LayoutInflater.from(mContext);
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
	
	public void updateListView(List<BmobChatUser> mData) {
		this.mData = mData;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 if(convertView == null)
	     {    
	            convertView = layoutInflater.inflate(R.layout.search_list_item, null);
	     }
	        
	        //得到条目中的子组件
	       ImageView iv_avatar = (ImageView) convertView.findViewById(R.id.search_list_item_avatar);
	       TextView tv_nick = (TextView) convertView.findViewById(R.id.search_list_item_nick);
	        
	       ImageLoader.getInstance().displayImage(mData.get(position).getAvatar(), iv_avatar);
	       tv_nick.setText(mData.get(position).getNick());
	       
	       convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, ChatActivity.class);
				intent.putExtra("user", mData.get(position));
				mContext.startActivity(intent);
			}
		});
	       
	       return convertView;
	}

}
