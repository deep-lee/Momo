package com.bmob.im.demo.view.dialog;

import java.util.List;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.SingleChoicAdapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


public class SingleChoiceDialog extends SingleDialogBase {
	
	Boolean hasNegative;
	Boolean hasTitle;
	
	
	
	
	public SingleChoiceDialog(Context context, List<String> list, String title, String buttonText, boolean hasNegative, boolean hasTitle) {
		super(context, list);
		super.setNamePositiveButton(buttonText);
		this.hasNegative = hasNegative;
		this.hasTitle = hasTitle;
		super.setTitle(title);
		// initData();
	}
	
	public SingleChoiceDialog(Context context, List<String> list, String buttonText,String negetiveText,String title,boolean isCancel) {
		super(context, list);
		super.setNamePositiveButton(buttonText);
		this.hasNegative = true;
		super.setNameNegativeButton(negetiveText);
		this.hasTitle = true;
		super.setTitle(title);
		super.setCancel(isCancel);
		
		// initData();
	}

	
	


	public int getSelectItem()
	{
		return mSingleChoicAdapter.getSelectItem();
	}

	@Override
	protected void onBuilding() {
		// TODO Auto-generated method stub
		super.setWidth(dip2px(mainContext, 300));
		if(hasNegative){
			super.setNameNegativeButton("È¡Ïû");
		}
		if(!hasTitle){
			super.setHasTitle(false);
		}
	}
	
	public int dip2px(Context context,float dipValue){
		float scale=context.getResources().getDisplayMetrics().density;		
		return (int) (scale*dipValue+0.5f);		
	}

	@Override
	protected boolean OnClickPositiveButton() {
		// TODO Auto-generated method stub
		if(onSuccessListener != null){
			onSuccessListener.onClick(this, 1);
		}
		return true;
	}

	@Override
	protected void OnClickNegativeButton() {
		// TODO Auto-generated method stub
		if(onCancelListener != null){
			onCancelListener.onClick(this, 0);
		}
	}

	@Override
	protected void onDismiss() {
		// TODO Auto-generated method stub

	}
	
	

}
