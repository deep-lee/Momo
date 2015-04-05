package com.bmob.im.demo.adapter;

import java.util.List;
import java.util.Map;
import com.bmob.im.demo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SlideAdapter extends BaseAdapter{
	
	private int iconId[] = {
		R.drawable.slide_icon_game,	
		R.drawable.slide_icon_set,
		R.drawable.slide_icon_roma,
		R.drawable.slide_icon_about,
		R.drawable.slide_icon_exit
	};
	
//	private String slideItemName[] = {
//		"�ҵ���Ϸ",
//		"����",
//		"��������",
//		"����",
//		"�˳���¼"
//	};
	
	private int itemName[] = {
			R.drawable.slide_list_item_game,	
			R.drawable.slide_list_item_set,
			R.drawable.slide_list_item_roma,
			R.drawable.slide_list_item_about,
			R.drawable.slide_list_item_exit
		};
	
	private Context context;
    
    private LayoutInflater layoutInflater;
    
    //���췽��������list���ݵľ�����һ�����ݵ���Ϣ
    public SlideAdapter(Context context)
    {
        this.context = context;
        
        layoutInflater = LayoutInflater.from(context);
        
    }
    
    //�õ��ܵ�����
    public int getCount() 
    {
        // TODO Auto-generated method stub
        return 5;
    }

    //����ListViewλ�÷���View
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    //����ListViewλ�õõ�List�е�ID
    public long getItemId(int position) 
    {
        // TODO Auto-generated method stub
        return position;
    }

    //����λ�õõ�View����
    public View getView(int position, View convertView, ViewGroup parent) 
    {    
        if(convertView == null)
        {    
            convertView = layoutInflater.inflate(R.layout.slide_item, null);
        }
        
        //�õ���Ŀ�е������
       ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
       ImageView name = (ImageView) convertView.findViewById(R.id.item_name);
        
       name.setImageResource(itemName[position]);
       icon.setImageResource(iconId[position]);
        
       return convertView;
    }
}
