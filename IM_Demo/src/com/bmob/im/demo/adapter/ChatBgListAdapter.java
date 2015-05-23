package com.bmob.im.demo.adapter;

import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.ChatBgGroup;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatBgListAdapter extends BaseAdapter {
	
	private List<ChatBgGroup> mData;
	private Context mContext;
	private LayoutInflater layoutInflater;
	public ChatBgListAdapter(List<ChatBgGroup> mData, Context mContext) {
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
		ViewHolder viewHolder = new ViewHolder();
		
		if(convertView == null)
	    {    
	        convertView = layoutInflater.inflate(R.layout.chat_bg_list_item, null);
	        viewHolder.iv_group_show = (ImageView) convertView.findViewById(R.id.chat_bg_item_iv);
	        viewHolder.tv_group_name = (TextView) convertView.findViewById(R.id.chat_bg_group_name);
	        viewHolder.tv_num_of_photo = (TextView) convertView.findViewById(R.id.chat_bg_group_num_of_photo);
	    }
		
		ImageLoader.getInstance().displayImage(mData.get(position).getGroupShow().getFileUrl(mContext), viewHolder.iv_group_show);
		viewHolder.tv_group_name.setText(mData.get(position).getGroupName());
		viewHolder.tv_num_of_photo.setText("(" + mData.get(position).getNumOfPhoto() + "’≈’’∆¨)");
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView iv_group_show;
		TextView tv_group_name;
		TextView tv_num_of_photo;
	}
	

}
