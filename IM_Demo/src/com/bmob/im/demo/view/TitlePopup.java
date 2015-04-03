package com.bmob.im.demo.view;

import java.util.ArrayList;

import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.FragmentBase;
import com.bmob.im.demo.ui.fragment.NearByFragment;
import com.bmob.im.demo.util.ActionItem;
import com.bmob.im.demo.util.MenuUtil;

import A.in;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


public class TitlePopup extends PopupWindow {
	private Context mContext;


	protected final int LIST_PADDING = 10;
	

	private Rect mRect = new Rect();

	private int[] mLocation = new int[2];
	

	private int mScreenWidth,mScreenHeight;


	private boolean mIsDirty;
	

	private int popupGravity = Gravity.NO_GRAVITY;	
	

	private OnItemOnClickListener mItemOnClickListener;
	

	private ListView mListView;
	

	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();		

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	
	int nearsSex;
	static NearByFragment fragment = null;
	
	
	public TitlePopup(Context context){

		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, fragment);
	}
	
	public TitlePopup(Context context, int width, int height, NearByFragment fragment2){
		this.mContext = context;
		

		setFocusable(true);

		setTouchable(true);	

		setOutsideTouchable(true);
		

		mScreenWidth = MenuUtil.getScreenWidth(mContext);
		mScreenHeight = MenuUtil.getScreenHeight(mContext);
		

		setWidth(width);
		setHeight(height);
		
		setBackgroundDrawable(new BitmapDrawable());
		
		fragment = fragment2;
		

		setContentView(LayoutInflater.from(mContext).inflate(R.layout.title_popup, null));
		
		mLocation = HeaderLayout.mLocation;
		

		sharedPreferences = mContext.getSharedPreferences("test", Activity.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		nearsSex = sharedPreferences.getInt("nearsSex", 2);
		
		initUI();
	}
		

	private void initUI(){
		mListView = (ListView) getContentView().findViewById(R.id.title_list);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,long arg3) {

			// Toast.makeText(mContext, index + "", Toast.LENGTH_LONG).show();
			
			if (nearsSex == index) {
				
			}else {
				editor.putInt("nearsSex", index);
				nearsSex = index;
				editor.commit();
				
				fragment.nearBySexChanged(index);

			}
				
			dismiss();
				if(mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index), index);
			}
		}); 
	}

	public void show(View view){

		view.getLocationOnScreen(mLocation);
		

		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),mLocation[1] + view.getHeight());
		

		if(mIsDirty){
			populateActions();
		}
		

		showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);
	}

	private void populateActions(){
		mIsDirty = false;
		
		mListView.setAdapter(new BaseAdapter() {			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = null;
				
				if(convertView == null){
					textView = new TextView(mContext);
					textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
					textView.setTextSize(14);

					textView.setGravity(Gravity.CENTER);

					textView.setPadding(0, 10, 0, 10);

					textView.setSingleLine(true);
				}else{
					textView = (TextView) convertView;
				}
				
				ActionItem item = mActionItems.get(position);
				

				textView.setText(item.mTitle);

				textView.setCompoundDrawablePadding(10);

                textView.setCompoundDrawablesWithIntrinsicBounds(item.mDrawable, null , null, null);
				
                return textView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}
			
			@Override
			public int getCount() {
				return mActionItems.size();
			}
		}) ;
	}

	public void addAction(ActionItem action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}
	

	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}

	public ActionItem getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}			

	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}

	public static interface OnItemOnClickListener{
		public void onItemClick(ActionItem item , int position);
	}
}
