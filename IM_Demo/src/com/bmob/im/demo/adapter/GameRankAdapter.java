package com.bmob.im.demo.adapter;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.GameRankInfo;
import com.bmob.im.demo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameRankAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<GameRankInfo> mData;
	
	

	public GameRankAdapter(Context mContext, List<GameRankInfo> mData) {
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
    	
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_rank_list_item, null);
		}
        
        ImageView iv_user_icon = (ImageView) convertView.findViewById(R.id.game_user_icon);
		TextView tv_game_name_and_score = (TextView) convertView.findViewById(R.id.game_rank_game_name);
		TextView tv_user_nick = (TextView) convertView.findViewById(R.id.game_rank_user_nick);
		
        if (mData.get(position).getBestUsername() != null && mData.get(position).getBestUserNick() != null) {
			
			String str = "";
			if (mData.get(position).getBestUserNick() == null) {
				Log.i("FFFFFFFFFFFFFF", "昵称为空");
			}
			else {
				Log.i("FFFFFFFFFFFFFF", mData.get(position).getBestUserNick());
				str = mData.get(position).getBestUserNick();
			}
			
			tv_user_nick.setText(str);
			if (mData.get(position).getBestUserAvatar() != null) {
				ImageLoader.getInstance().displayImage(mData.get(position).getBestUserAvatar(), iv_user_icon);
			}
			tv_game_name_and_score.setText(mData.get(position).getGameName() + "( " + mData.get(position).getBestScore() + " )");
			
		}else {
			iv_user_icon.setImageResource(R.drawable.default_avatar);
			tv_user_nick.setText("还没有人是冠军哦！赶紧加油吧！");
			tv_game_name_and_score.setText(mData.get(position).getGameName());
			
		}
        
        return convertView;
	}
	
	
	public void updateData(List<GameRankInfo> mData) {
		this.mData = mData;
		notifyDataSetChanged();
	}

}
