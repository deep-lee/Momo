package com.bmob.im.demo.ui.fragment;

import java.util.Arrays;
import java.util.List;






import com.bmob.im.demo.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LeftFragment extends Fragment {

	private View mView;  
    private ListView mCategories;  
    private List<String> mDatas = Arrays  
            .asList("个人星级","我的游戏","设置","帮助");  
    private ListAdapter mAdapter;  
  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        if (mView == null)  
        {  
            initView(inflater, container);  
        }  
        return mView;  
    }  
  
    private void initView(LayoutInflater inflater, ViewGroup container)  
    {  
        mView = inflater.inflate(R.layout.left_fragment, container, false);  
        mCategories = (ListView) mView  
                .findViewById(R.id.listView1);  
        mAdapter = new ArrayAdapter<String>(getActivity(),  
                android.R.layout.simple_list_item_1, mDatas);  
        mCategories.setAdapter(mAdapter);  
        //菜单点击事件
        mCategories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				switch (position) {
				case 0:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;
				case 1:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;
				case 2:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;
				case 3:
					Toast.makeText(getActivity(), position+"选中", Toast.LENGTH_LONG).show();
					//添加方法
					break;				
				}
			}
        	
		});
    }
}
