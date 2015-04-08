package com.bmob.im.demo.view.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.SingleChoicAdapter;

/**
 *�Զ���Ի������
 *֧�֣��Ի���ȫ����ʾ���ơ�title��ʾ���ƣ�һ��button������
 */
public abstract class SingleDialogBase extends Dialog {
	
	protected OnClickListener onSuccessListener;
	protected Context mainContext;
	protected OnClickListener onCancelListener;//�ṩ��ȡ����ť
	protected OnDismissListener onDismissListener;
	
	protected View view;
	protected Button positiveButton, negativeButton;
	private boolean isFullScreen = false;
	
	protected ListView mListView;
	
	private boolean hasTitle = true;//�Ƿ���title
	
	protected List<String> mList;
	
	private int width = 0, height = 0, x = 0, y = 0;
	private int iconTitle = 0;
	private String message, title;
	private String namePositiveButton, nameNegativeButton;
	private final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

	private boolean isCancel = true;//Ĭ���Ƿ�ɵ��back����/����ⲿ����ȡ���Ի���
	
	protected SingleChoicAdapter<String> mSingleChoicAdapter;
	
	
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
	public SingleDialogBase(Context context, List<String> mList) {
		super(context, R.style.alert);
		this.mainContext = context;
		this.mList = mList;
	}
	
	/** 
	 * �����¼�
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.single_choice_dialog_base);
		this.onBuilding();
		// ���ñ������Ϣ
		LinearLayout dialog_top = (LinearLayout)findViewById(R.id.single_choice_dialog_top);
		View title_red_line = (View)findViewById(R.id.single_choice_title_red_line); // ��������ĺ���
		
		//�Ƿ���title
		if(hasTitle){
			dialog_top.setVisibility(View.VISIBLE);
			title_red_line.setVisibility(View.VISIBLE);
		}else{
			dialog_top.setVisibility(View.GONE);
			title_red_line.setVisibility(View.GONE);
		}
		TextView titleTextView = (TextView)findViewById(R.id.single_choice_dialog_title);
		titleTextView.setText(this.getTitle());
		
//		TextView messageTextView = (TextView)findViewById(R.id.dialog_message);
//		messageTextView.setText(this.getMessage());
		
		mListView = (ListView)findViewById(R.id.single_choice_listView);
		

		// ���ð�ť�¼�����
		positiveButton = (Button)findViewById(R.id.single_choice_dialog_positivebutton);
		negativeButton = (Button)findViewById(R.id.single_choice_dialog_negativebutton);
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
		
		initData();
	}
	
	protected void initData() {
		// TODO Auto-generated method stub
		mSingleChoicAdapter = new SingleChoicAdapter<String>(mainContext, mList, R.drawable.selector_checkbox2);
		
		mListView.setAdapter(mSingleChoicAdapter);
		mListView.setOnItemClickListener(mSingleChoicAdapter);   
		
		setListViewHeightBasedOnChildren(mListView);
	
	}

	/**
	 * ��ȡOnDismiss�¼��������ͷ���Դ
	 * @return OnDismiss�¼�����
	 */
	protected OnDismissListener GetOnDismissListener() {
		return new OnDismissListener(){
			public void onDismiss(DialogInterface arg0) {
				SingleDialogBase.this.onDismiss();
				SingleDialogBase.this.setOnDismissListener(null);
				view = null;
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
					SingleDialogBase.this.dismiss();
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
				SingleDialogBase.this.dismiss();
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
	 * @return �Ի���View
	 */
	protected View getView() {
		return view;
	}

	/**
	 * @param view �Ի���View
	 */
	protected void setView(View view) {
		this.view = view;
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
	
	public static void setListViewHeightBasedOnChildren(ListView listView) { 
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null ) { 
                // pre-condition 
                return; 
        } 
        
        Log.e("", "listAdapter.getCount() = " + listAdapter.getCount());
  
        int totalHeight = 0; 
        int tmp = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { 
                View listItem = listAdapter.getView(i, null, listView); 
                listItem.measure(0, 0); 
                totalHeight += listItem.getMeasuredHeight();   
                tmp = listItem.getMeasuredHeight();
        } 
        totalHeight += 10;
        ViewGroup.LayoutParams params = listView.getLayoutParams(); 
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
        listView.setLayoutParams(params); 
    }
}