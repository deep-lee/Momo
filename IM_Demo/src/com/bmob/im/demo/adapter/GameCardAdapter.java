package com.bmob.im.demo.adapter;  
      
import java.util.ArrayList;
import java.util.List;  
      










import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

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
    private List<GameCard> mCards;  
    private Context mContext; 
    
          
    public GameCardAdapter(Context mContext,List<GameCard> mCards)  
    {   
         this.mContext = mContext;  
         this.mCards = mCards; 
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
        ViewHolder mHolder = new ViewHolder();  
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_list_card_item, null);
		}
        
        mHolder.gameName = (TextView)convertView.findViewById(R.id.card_item_title);  
        mHolder.game_icon=(ImageView)convertView.findViewById(R.id.card_item_icon);  
        mHolder.gameRuleDetails = (TextView) convertView.findViewById(R.id.card_item_game_rule_details);
        mHolder.gameWinMethod = (TextView) convertView.findViewById(R.id.card_item_game_win_method_details);
        mHolder.play_game = (ImageButton) convertView.findViewById(R.id.card_item_play);
        
        
        //��ס����������setImageResource()����������setBackgroundResource(),����ͼ�����ΰ�  
        mHolder.game_icon.setImageResource(mCards.get(Index).getDrawable()); 
        mHolder.gameName.setText(mCards.get(Index).getGameName());
        mHolder.gameRuleDetails.setText(mCards.get(Index).getGameRuleDetails());
        mHolder.gameWinMethod.setText(mCards.get(Index).getGameWinMethod());
        
        mHolder.play_game.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putString("from", "me");
				intent.putExtras(data);
				switch (Index) {
				// ˮ��������
				case 0:
					intent.setClass(mContext, GameFruitActivity.class);
					break;
				// ������
				case 1:
					intent.setClass(mContext, GuessNumberActivity.class);
					break;
				// Mixed color
				case 2:
					intent.setClass(mContext, MixedColorMenuActivity.class);
					break;

				default:
					break;
				}
				
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
    }  
}  