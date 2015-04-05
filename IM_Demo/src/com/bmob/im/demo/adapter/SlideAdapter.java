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
//		"我的游戏",
//		"设置",
//		"景点漫游",
//		"关于",
//		"退出登录"
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
    
    //构造方法，参数list传递的就是这一组数据的信息
    public SlideAdapter(Context context)
    {
        this.context = context;
        
        layoutInflater = LayoutInflater.from(context);
        
    }
    
    //得到总的数量
    public int getCount() 
    {
        // TODO Auto-generated method stub
        return 5;
    }

    //根据ListView位置返回View
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    //根据ListView位置得到List中的ID
    public long getItemId(int position) 
    {
        // TODO Auto-generated method stub
        return position;
    }

    //根据位置得到View对象
    public View getView(int position, View convertView, ViewGroup parent) 
    {    
        if(convertView == null)
        {    
            convertView = layoutInflater.inflate(R.layout.slide_item, null);
        }
        
        //得到条目中的子组件
       ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
       ImageView name = (ImageView) convertView.findViewById(R.id.item_name);
        
       name.setImageResource(itemName[position]);
       icon.setImageResource(iconId[position]);
        
       return convertView;
    }
}
