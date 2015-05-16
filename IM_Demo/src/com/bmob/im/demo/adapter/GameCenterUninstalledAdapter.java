package com.bmob.im.demo.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.ui.GameDetailsActivity;
import com.bmob.im.demo.util.DownloadService;

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

public class GameCenterUninstalledAdapter extends BaseAdapter {
	
	public static int NET_NOT_AVAIABLE = 0;
	public static int NET_MOBILE = 1;
	public static int NET_WIFI = 2;
	
	int recentPlay = 0;
	
    private List<GameInfo> mData;  
    private Context mContext; 
    
    private BmobQuery<GameFile> query;
    
    public GameCenterUninstalledAdapter(Context mContext, List<GameInfo> uninstalledGameData) {
		super();
		this.mContext = mContext;  
        this.mData = uninstalledGameData; 
        
        query = new BmobQuery<GameFile>();
        
        Intent intent  = new Intent(mContext,DownloadService.class);
        mContext.startService(intent);
	}

    /** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<GameInfo> list) {
		this.mData = list;
		notifyDataSetChanged();
	}
  
	  @Override  
	  public int getCount()   
	  {  
	      return mData.size();  
	  }  
	    
	  @Override  
	  public Object getItem(int Index)   
	  {  
	      return mData.get(Index);  
	  }  
	    
	  @Override  
	  public long getItemId(int Index)   
	  {  
	      return Index;  
	  }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder mHolder = new ViewHolder();
    	
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_center_list_item, null);
		}
        
        mHolder.layout_all = (RelativeLayout) convertView.findViewById(R.id.game_center_list_layout_all);
        mHolder.game_icon = (ImageView) convertView.findViewById(R.id.game_list_item_icon);
        mHolder.gameName = (TextView) convertView.findViewById(R.id.game_list_item_name);
        mHolder.gameDescription = (TextView) convertView.findViewById(R.id.game_list_item_description);
        
        Bitmap bitmap = getBitmapFromAsset(mContext, mData.get(position).getGame_icon());
        mHolder.game_icon.setImageBitmap(bitmap);
        mHolder.gameName.setText(mData.get(position).getGame_name());
        mHolder.gameDescription.setText("50000人正在玩");
        
        mHolder.layout_all.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入相应的游戏介绍界面
				
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

