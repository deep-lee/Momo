package com.bmob.im.demo.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.bmob.im.demo.GameInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.GameDetailsActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameCommunityAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<GameInfo> mData;
	
	

	public GameCommunityAdapter(Context mContext, List<GameInfo> mData) {
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder mHolder = new ViewHolder();
    	
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_community_list_item, null);
		}
        
        mHolder.layout_all = (RelativeLayout) convertView.findViewById(R.id.game_community_list_item_layout_all);
        mHolder.game_icon = (ImageView) convertView.findViewById(R.id.game_community_list_item_icon);
        mHolder.gameName = (TextView) convertView.findViewById(R.id.game_community_list_item_name);
        mHolder.gameDescription = (TextView) convertView.findViewById(R.id.game_community_list_item_description);
        
        Bitmap bitmap = getBitmapFromAsset(mContext, mData.get(position).getGame_icon());
        mHolder.game_icon.setImageBitmap(bitmap);
        mHolder.gameName.setText(mData.get(position).getGame_name());
        mHolder.gameDescription.setText("50000人正在玩");
        
        mHolder.layout_all.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入游戏界面
				
				Intent intent = new Intent();
				
				intent.setClass(mContext, GameDetailsActivity.class);
				
				Bundle data = new Bundle();
				data.putString("from", "me");
				data.putSerializable("gameInfo", mData.get(position));
				intent.putExtras(data);
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	private static class ViewHolder  
    {  
		RelativeLayout layout_all;
        TextView gameName;  
        ImageView game_icon;
        TextView gameDescription;
    } 
	
	private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream is=null;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }

}
