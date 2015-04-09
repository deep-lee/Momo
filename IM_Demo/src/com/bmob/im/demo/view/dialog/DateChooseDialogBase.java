package com.bmob.im.demo.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai.android.picker.DatePicker;
import com.bmob.im.demo.R;

/**
 *�Զ���Ի������
 *֧�֣��Ի���ȫ����ʾ���ơ�title��ʾ���ƣ�һ��button������
 */
public abstract class DateChooseDialogBase extends Dialog {
	protected OnClickListener onSuccessListener;
	protected Context mainContext;
	protected OnClickListener onCancelListener;//�ṩ��ȡ����ť
	protected OnDismissListener onDismissListener;
	
	protected Button positiveButton, negativeButton;
	private boolean isFullScreen = false;
	
	private boolean hasTitle = true;//�Ƿ���title
	
	private int width = 0, height = 0, x = 0, y = 0;
	private int iconTitle = 0;
	private String title;
	private String namePositiveButton, nameNegativeButton;
	private final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

	private boolean isCancel = true;//Ĭ���Ƿ�ɵ��back����/����ⲿ����ȡ���Ի���
	
	DatePicker datePicker;
	
	
	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	/**
	 * ���캯��
	 * @param context ����Ӧ����Activity
	 */
	public DateChooseDialogBase(Context context) {
		super(context, R.style.alert);
		this.mainContext = context;
	}
	
	/** 
	 * �����¼�
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.date_picker_dialog);
		this.onBuilding();
		// ���ñ������Ϣ
		LinearLayout dialog_top = (LinearLayout)findViewById(R.id.common_date_dialog_top);
		View title_red_line = (View)findViewById(R.id.common_date_title_red_line);
		
		//�Ƿ���title
		if(hasTitle){
			dialog_top.setVisibility(View.VISIBLE);
			title_red_line.setVisibility(View.VISIBLE);
		}else{
			dialog_top.setVisibility(View.GONE);
			title_red_line.setVisibility(View.GONE);
		}
		TextView titleTextView = (TextView)findViewById(R.id.common_date_dialog_title);
		titleTextView.setText(this.getTitle());
		

		datePicker = (DatePicker) findViewById(R.id.common_date_datePicker);
		
		// ���ð�ť�¼�����
		positiveButton = (Button)findViewById(R.id.common_date_dialog_positivebutton);
		negativeButton = (Button)findViewById(R.id.common_date_dialog_negativebutton);
		if(namePositiveButton != null && namePositiveButton.length()>0){
			positiveButton.setText(namePositiveButton);
			positiveButton.setOnClickListener(GetPositiveButtonOnClickListener());
		} else {
			positiveButton.setVisibility(View.GONE);
			findViewById(R.id.dialog_leftspacer).setVisibility(View.VISIBLE);
			findViewById(R.id.dialog_rightspacer).setVisibility(View.VISIBLE);
		}
		if(nameNegativeButton != null && nameNegativeButton.length()>0){
			negativeButton.setText(nameNegativeButton);
			negativeButton.setOnClickListener(GetNegativeButtonOnClickListener());
		} else {
			negativeButton.setVisibility(View.GONE);
		}
		
		// ���öԻ����λ�úʹ�С
		LayoutParams params = this.getWindow().getAttributes();  
		if(this.getWidth()>0)
			params.width = this.getWidth();  
		if(this.getHeight()>0)
			params.height = this.getHeight();  
		if(this.getX()>0)
			params.width = this.getX();  
		if(this.getY()>0)
			params.height = this.getY();  
		
		// �������Ϊȫ��
		if(isFullScreen) {
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
		}
		
		//���õ��dialog�ⲿ�����ȡ��
		if(isCancel){
			setCanceledOnTouchOutside(true);
			setCancelable(true);
		}else{
			setCanceledOnTouchOutside(false);
			setCancelable(false);
		}
	    getWindow().setAttributes(params);  
		this.setOnDismissListener(GetOnDismissListener());
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	/**
	 * ��ȡOnDismiss�¼��������ͷ���Դ
	 * @return OnDismiss�¼�����
	 */
	protected OnDismissListener GetOnDismissListener() {
		return new OnDismissListener(){
			public void onDismiss(DialogInterface arg0) {
				DateChooseDialogBase.this.onDismiss();
				DateChooseDialogBase.this.setOnDismissListener(null);
				mainContext = null;
				positiveButton = null;
				negativeButton = null;
				if(onDismissListener != null){
					onDismissListener.onDismiss(null);
				}
			}			
		};
	}

	/**
	 * ��ȡȷ�ϰ�ť�����¼�����
	 * @return ȷ�ϰ�ť�����¼�����
	 */
	protected View.OnClickListener GetPositiveButtonOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				if(OnClickPositiveButton())
					DateChooseDialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 * ��ȡȡ����ť�����¼�����
	 * @return ȡ����ť�����¼�����
	 */
	protected View.OnClickListener GetNegativeButtonOnClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				OnClickNegativeButton();
				DateChooseDialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 * ��ȡ����ı��¼�����������EditText�ı�Ĭ��ȫѡ
	 * @return ����ı��¼�����
	 */
	protected OnFocusChangeListener GetOnFocusChangeListener() {
		return new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && v instanceof EditText) {
					((EditText) v).setSelection(0, ((EditText) v).getText().length());
				}
			}
		};
	}
	
	/**
	 * ���óɹ��¼������������ṩ�������ߵĻص�����
	 * @param listener �ɹ��¼�����
	 */
	public void SetOnSuccessListener(OnClickListener listener){
		onSuccessListener = listener;
	}
	
	/**
	 * ���ùر��¼������������ṩ�������ߵĻص�����
	 * @param listener �ر��¼�����
	 */
	public void SetOnDismissListener(OnDismissListener listener){
		onDismissListener = listener;
	}

	/**�ṩ��ȡ����ť������ʵ���ඨ��
	 * @param listener
	 */
	public void SetOnCancelListener(OnClickListener listener){
		onCancelListener = listener;
	}
	
	/**
	 * �����������������ඨ�ƴ�������
	 */
	protected abstract void onBuilding();

	/**
	 * ȷ�ϰ�ť�����������������ඨ��
	 */
	protected abstract boolean OnClickPositiveButton();

	/**
	 * ȡ����ť�����������������ඨ��
	 */
	protected abstract void OnClickNegativeButton();

	/**
	 * �رշ������������ඨ��
	 */
	protected abstract void onDismiss();

	/**
	 * @return �Ի������
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title �Ի������
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param iconTitle ����ͼ�����ԴId
	 */
	public void setIconTitle(int iconTitle) {
		this.iconTitle = iconTitle;
	}

	/**
	 * @return ����ͼ�����ԴId
	 */
	public int getIconTitle() {
		return iconTitle;
	}

	/**
	 * @return �Ƿ�ȫ��
	 */
	public boolean getIsFullScreen() {
		return isFullScreen;
	}

	/**
	 * @param isFullScreen �Ƿ�ȫ��
	 */
	public void setIsFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}


	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	
	/**
	 * @return �Ի�����
	 */
	protected int getWidth() {
		return width;
	}

	/**
	 * @param width �Ի�����
	 */
	protected void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return �Ի���߶�
	 */
	protected int getHeight() {
		return height;
	}

	/**
	 * @param height �Ի���߶�
	 */
	protected void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return �Ի���X����
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x �Ի���X����
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return �Ի���Y����
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y �Ի���Y����
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return ȷ�ϰ�ť����
	 */
	protected String getNamePositiveButton() {
		return namePositiveButton;
	}

	/**
	 * @param namePositiveButton ȷ�ϰ�ť����
	 */
	protected void setNamePositiveButton(String namePositiveButton) {
		this.namePositiveButton = namePositiveButton;
	}

	/**
	 * @return ȡ����ť����
	 */
	protected String getNameNegativeButton() {
		return nameNegativeButton;
	}

	/**
	 * @param nameNegativeButton ȡ����ť����
	 */
	protected void setNameNegativeButton(String nameNegativeButton) {
		this.nameNegativeButton = nameNegativeButton;
	}
}