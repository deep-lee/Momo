package com.bmob.im.demo.adapter;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/** �����б�
  * @ClassName: UserFriendAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-12 ����3:03:40
  */
@SuppressLint("DefaultLocale")
public class UserFriendAdapter extends BaseAdapter implements SectionIndexer {
	private Context ct;
	private List<User> data;

	public UserFriendAdapter(Context ct, List<User> datas) {
		this.ct = ct;
		this.data = datas;
	}

	/** ��ListView���ݷ����仯ʱ,���ô˷���������ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<User> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(User user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(
					R.layout.item_user_friend, null);
			viewHolder = new ViewHolder();
			viewHolder.user_item_line = convertView.findViewById(R.id.item_user_line);
			viewHolder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.tv_friend_name);
			viewHolder.avatar = (ImageView) convertView
					.findViewById(R.id.img_friend_avatar);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		User friend = data.get(position);
		final String name = friend.getNick();
		final String avatar = friend.getAvatar();

		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, viewHolder.avatar, ImageLoadOptions.getOptions());
		} else {
			viewHolder.avatar.setImageDrawable(ct.getResources().getDrawable(R.drawable.default_head));
		}
		viewHolder.name.setText(name);
		
		

		// ����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);
		// �����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
		if (position == getPositionForSection(section)) {
			viewHolder.alpha.setVisibility(View.VISIBLE);
			viewHolder.alpha.setText(friend.getSortLetters());
		} else {
			viewHolder.alpha.setVisibility(View.INVISIBLE);
			viewHolder.user_item_line.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView alpha;// ����ĸ��ʾ
		ImageView avatar;
		TextView name;
		View user_item_line;
	}

	/**
	 * ����ListView�ĵ�ǰλ�û�ȡ���������ĸ��Char asciiֵ
	 */
	public int getSectionForPosition(int position) {
		return data.get(position).getSortLetters().charAt(0);
	}

	/**
	 * ���ݷ��������ĸ��Char asciiֵ��ȡ���һ�γ��ָ�����ĸ��λ��
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = data.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section){
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
	
	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	/**
	 * ��������侭γ�����꣨doubleֵ���������������룬
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return ���룺��λΪ��
	 */
	public static double DistanceOfTwoPoints(double lat1, double lng1,double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

}