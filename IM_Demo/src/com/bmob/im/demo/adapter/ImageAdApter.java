package com.bmob.im.demo.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdApter extends BaseAdapter {
	
	private Context mContext;
	private List<String> mData;

	public ImageAdApter(Context myContext, List<String> mData) {
		super();
		this.mContext = myContext;
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

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        ImageView imageView = new ImageView(this.mContext);  
        
        Log.i("FFFFFFFFFFFFFFFF", mData.get(position));
        
        ImageLoader.getInstance().displayImage(mData.get(position), imageView); 
        
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);  
        //设置这个ImageView对象的宽高  
        imageView.setLayoutParams(new Gallery.LayoutParams(240, 427)); 
        
        return imageView;  
	}
	
	//根据中央的位移量，利用getScale返回views的大小  
    public float getScale(boolean focused,int offset){  
        return Math.max(0, 1.0f/(float)Math.pow(2, Math.abs(offset)));  
    } 

}
