package com.bmob.im.demo.adapter;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.ChatBg;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatBgGroupDetailsAdapter extends BaseAdapter {
	
	private List<ChatBg> mData;
	private Context mContext;
	
	private LayoutInflater layoutInflater;

	public ChatBgGroupDetailsAdapter(List<ChatBg> mData, Context mContext) {
		super();
		this.mData = mData;
		this.mContext = mContext;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.chat_bg_group_details_list_item, null);
		}
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.chat_bg_group_details_item_image);
		TextView tv = (TextView) convertView.findViewById(R.id.chat_bg_group_details_item_name);
		
		BmobFile file = mData.get(position).getFile();
		
		file.loadImageThumbnail(mContext, iv, 200, 300);
		tv.setText(mData.get(position).getPhotoName());
		
		return convertView;
	}

}
