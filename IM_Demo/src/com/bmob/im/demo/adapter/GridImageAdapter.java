package com.bmob.im.demo.adapter;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.ShakeForNearPeopleActivity;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/*
 * 
 * 2015年5月8日
 * deeplee
 */
public class GridImageAdapter extends BaseAdapter {
	
	private Context mContext;  
	int width;
	
	//展示图片  
    private Integer[] mThumbIds = {  
            R.drawable.ic_welcome_photo_1, R.drawable.ic_welcome_photo_2,  
            R.drawable.ic_welcome_photo_3, R.drawable.ic_welcome_photo_4,  
            R.drawable.ic_welcome_photo_5, R.drawable.ic_welcome_photo_6,  
            R.drawable.ic_welcome_photo_7, R.drawable.ic_welcome_photo_8,  
            R.drawable.ic_welcome_photo_9, R.drawable.ic_welcome_photo_10,  
            R.drawable.ic_welcome_photo_11, R.drawable.ic_welcome_photo_12,  
            R.drawable.ic_welcome_photo_13, R.drawable.ic_welcome_photo_14,  
            R.drawable.ic_welcome_photo_15
    };  

	public GridImageAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		
		DisplayMetrics dm = new DisplayMetrics();
		((ShakeForNearPeopleActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;		//宽度height = dm.heightPixels ;//高度
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mThumbIds[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//定义一个ImageView,显示在GridView里  
        ImageView imageView;  
        if(convertView == null){  
            imageView=new ImageView(mContext);  
            imageView.setLayoutParams(new GridView.LayoutParams(width / 3, width / 3));  
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  
        }else{  
            imageView = (ImageView) convertView;  
        }  
        imageView.setImageResource(mThumbIds[position]);  
        return imageView;  
	}

}


