package com.bmob.im.demo.adapter;  
      
import java.util.ArrayList;
import java.util.List;  
      

















import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.GameCenterActivity;
import com.deep.animation.ExpandAnimation;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

import android.R.integer;
import android.app.Activity;
import android.content.Context;  
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageButton;
import android.widget.ImageView;  
import android.widget.TextView;  
      
public class GameCardAdapter extends BaseAdapter   
{  
	int recentPlay = 0;
	
    private List<GameCard> mCards;  
    private Context mContext; 
    
          
    public GameCardAdapter(Context mContext,List<GameCard> mCards)  
    {   
         this.mContext = mContext;  
         this.mCards = mCards; 
    }  
    
    /** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<GameCard> list) {
		this.mCards = list;
		notifyDataSetChanged();
	}
    
    @Override  
    public int getCount()   
    {  
        return mCards.size();  
    }  
      
    @Override  
    public Object getItem(int Index)   
    {  
        return mCards.get(Index);  
    }  
      
    @Override  
    public long getItemId(int Index)   
    {  
        return Index;  
    }  
    
    @Override  
    public View getView(final int Index, View convertView, ViewGroup mParent)   
    {  
        final ViewHolder mHolder = new ViewHolder();  
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_list_card_item, null);
		}
          
        mHolder.gameName = (TextView)convertView.findViewById(R.id.card_item_title);  
        mHolder.game_icon=(ImageView)convertView.findViewById(R.id.card_item_icon);  
        mHolder.gameRuleDetails = (TextView) convertView.findViewById(R.id.card_item_game_rule_details);
        mHolder.gameWinMethod = (TextView) convertView.findViewById(R.id.card_item_game_win_method_details);
        mHolder.play_game = (ImageButton) convertView.findViewById(R.id.card_item_play);
        
        mHolder.toolbar = convertView.findViewById(R.id.game_item_details_layout);
        
        if (!CustomApplcation.sex) {
			mHolder.play_game.setImageResource(R.drawable.base_game_card_list_play_female_selector);
		}
        
        //记住啊，这里是setImageResource()方法，不是setBackgroundResource(),否则图像会变形啊  
        mHolder.game_icon.setImageResource(mCards.get(Index).getDrawable()); 
        mHolder.gameName.setText(mCards.get(Index).getGameName());
        mHolder.gameRuleDetails.setText(mCards.get(Index).getGameRuleDetails());
        mHolder.gameWinMethod.setText(mCards.get(Index).getGameWinMethod());
        
        convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				

				// Creating the expand animation for the item
				ExpandAnimation expandAni = new ExpandAnimation(mHolder.toolbar, 500);

				// Start the animation on the toolbar
				mHolder.toolbar.startAnimation(expandAni);
				
			}
		});
        
        mHolder.play_game.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putString("from", "me");
				intent.putExtras(data);
				switch (Index) {
				// 水果连连看
				case 0:
					intent.setClass(mContext, GameFruitActivity.class);
					recentPlay = 1;
					break;
				// 猜数字
				case 1:
					intent.setClass(mContext, GuessNumberActivity.class);
					recentPlay = 2;
					break;
				// Mixed color
				case 2:
					intent.setClass(mContext, MixedColorMenuActivity.class);
					recentPlay = 3;
					break;

				default:
					break;
				}
				
				User user = new User();
				user.setRecentPlayGame(recentPlay);
				
				((GameCenterActivity)mContext).updateUserData(user, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}
				});
				
				mContext.startActivity(intent);
			}
		});
        
        return convertView;  
    }  
      
    private static class ViewHolder  
    {  
        TextView gameName;  
        ImageView game_icon;
        TextView gameRuleDetails;
        TextView gameWinMethod;
        ImageButton play_game;
        View toolbar;
    }  
}  