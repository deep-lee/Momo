package com.bmob.im.demo.adapter;

import com.bmob.im.demo.ui.fragment.GameTopicContentFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class GameTopicContentAdapter extends SmartFragmentStatePagerAdapter {

	private static int NUM_ITEMS = 1;

	public GameTopicContentAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		if(this.getRegisteredFragment(position)!=null){
			return getRegisteredFragment(position);
		}else{
			//return FavFragment.newInstance();//QiangContentFragment.newInstance(position);
			
			// �����������ǰ����û��QiangContentFragment�Ļ�����newһ����Ȼ�󷵻أ�position����������ǰ�ǵڼ���״̬
			return GameTopicContentFragment.newInstance(position);
		}
//		return MenuContentFragment.newInstance("pager" + position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return NUM_ITEMS;
	}

}
