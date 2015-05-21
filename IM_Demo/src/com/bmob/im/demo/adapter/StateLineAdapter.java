package com.bmob.im.demo.adapter;

import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.QiangYu;
import com.deep.db.DatabaseUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StateLineAdapter extends BaseAdapter {
	
	private Context mContext; 
	
	private List<QiangYu> mData;

	public StateLineAdapter(Context mContext, List<QiangYu> mData) {
		super();
		this.mContext = mContext;
		this.mData = mData;
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
		
		final ViewHolder mHolder = new ViewHolder();
    	
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.states_time_line_item, null);
		}
        
        mHolder.tv_content = (TextView) convertView.findViewById(R.id.state_item_content);
        mHolder.tv_month = (TextView) convertView.findViewById(R.id.state_item_month);
        mHolder.tv_time = (TextView) convertView.findViewById(R.id.state_item__time);
        mHolder.tv_year = (TextView) convertView.findViewById(R.id.state_item_year);
        mHolder.iv_image = (ImageView) convertView.findViewById(R.id.image_1);
        
        mHolder.tv_content.setText(mData.get(position).getContent());
        
        String []createdAt = mData.get(position).getCreatedAt().split(" ");
        String []date = createdAt[0].split("-");
        
        mHolder.tv_year.setText(date[0]);
        mHolder.tv_month.setText(date[1]);
        mHolder.tv_time.setText(createdAt[1]);
        ImageLoader.getInstance().displayImage(mData.get(position).getContentfigureurl().getFileUrl(mContext), mHolder.iv_image);
        
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView tv_month;
		TextView tv_year;
		TextView tv_time;
		ImageView iv_image;
		TextView tv_content;
	}

}

