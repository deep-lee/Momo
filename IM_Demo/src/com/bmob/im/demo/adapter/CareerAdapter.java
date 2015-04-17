package com.bmob.im.demo.adapter;

import com.bmob.im.demo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CareerAdapter extends BaseAdapter{
	
	int selected;
	
	private int iconId[] = {
		R.drawable.ic_industry_it,	
		R.drawable.ic_industry_gong,
		R.drawable.ic_industry_shang,
		R.drawable.ic_industry_jin,
		R.drawable.ic_industry_wen,
		R.drawable.ic_industry_yis,
		R.drawable.ic_industry_yi,
		R.drawable.ic_industry_fa,
		R.drawable.ic_industry_jiao,
		R.drawable.ic_industry_zheng,
		R.drawable.ic_industry_xue
	};
	
	private int []careerName = {
			R.string.career_list_item_IT,
			R.string.career_list_item_Gong,
			R.string.career_list_item_Shang,
			R.string.career_list_item_Jin,
			R.string.career_list_item_Wen,
			R.string.career_list_item_YiShu,
			R.string.career_list_item_YiLiao,
			R.string.career_list_item_Fa,
			R.string.career_list_item_Jiao,
			R.string.career_list_item_Zheng,
			R.string.career_list_item_Xue,
			R.string.career_list_item_No
			
	};
	
	
	private Context context;
    
    private LayoutInflater layoutInflater;

    
    public CareerAdapter(Context context, int selected)
    {
        this.context = context;
        this.selected = selected;
        layoutInflater = LayoutInflater.from(context);
        
    }
    
    //得到总的数量
    public int getCount() 
    {
        // TODO Auto-generated method stub
        return careerName.length;
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
            convertView = layoutInflater.inflate(R.layout.career_list_item, null);
        }
        
       // 得到条目中的子组件
       ImageView icon = (ImageView) convertView.findViewById(R.id.career_list_item_icon);
       TextView name = (TextView) convertView.findViewById(R.id.career_list_item_title);
       ImageView select = (ImageView) convertView.findViewById(R.id.career_list_item_select_btn);
        
       name.setText(careerName[position]);
       
       if (position == selected) {
    	   select.setImageResource(R.drawable.register_sex_select_p);
       }
       
       if (position != 11) {
    	   icon.setImageResource(iconId[position]);
       }
       else {
    	   icon.setVisibility(View.GONE);
//    	   select.setImageResource(R.drawable.register_sex_select_p);
       }
       
//       convertView.setOnClickListener(new View.OnClickListener() {
//		
//    	   @Override
//    	   public void onClick(View v) {
//    		   // TODO Auto-generated method stub
//    		   
//    	   }
//       });
        
       return convertView;
    }
}
