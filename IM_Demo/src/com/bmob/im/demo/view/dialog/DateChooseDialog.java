package com.bmob.im.demo.view.dialog;

import java.util.Calendar;

import android.content.Context;

public class DateChooseDialog extends DateChooseDialogBase {
	
	public DateChooseDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	Boolean hasNegative;
	Boolean hasTitle;
//	Calendar mCalendar;

	public DateChooseDialog(Context context, String title, String buttonText, boolean hasNegative, boolean hasTitle) {
		super(context);
		super.setNamePositiveButton(buttonText);
		this.hasNegative = hasNegative;
		this.hasTitle = hasTitle;
		super.setTitle(title);

	}
	
	public DateChooseDialog(Context context, String buttonText,String negetiveText,String title,boolean isCancel) {
		super(context);
		super.setNamePositiveButton(buttonText);
		this.hasNegative = true;
		super.setNameNegativeButton(negetiveText);
		this.hasTitle = true;
		super.setTitle(title);
		super.setCancel(isCancel);

	}
	
	public String getDate() {
//		mCalendar = Calendar.getInstance();
//		mCalendar.set(Calendar.YEAR, datePicker.getYear());
//		mCalendar.set(Calendar.MONTH, datePicker.getMonth());
//		mCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDay());
		
		return datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDay();
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
