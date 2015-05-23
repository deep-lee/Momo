package com.bmob.im.demo.ui;

import cn.bmob.v3.listener.SaveListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.Feedback;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendFeedbackActivity extends BaseActivity implements OnClickListener{
	
	EditText et_feedback_content;
	EditText et_feedback_contact_method;
	Button btn_commit;
	
	String msg = "";
	String contact = "";
	InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_feedback);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		initView();
	}
	
	public void initView() {
		et_feedback_contact_method = (EditText) findViewById(R.id.feedback_contact_edit);
		et_feedback_content = (EditText) findViewById(R.id.feedback_content_edit);
		btn_commit = (Button) findViewById(R.id.feedback_submit_button);
		btn_commit.setOnClickListener(this);
		
		et_feedback_content.requestFocus();
		imm.showSoftInput(et_feedback_content, InputMethodManager.SHOW_FORCED);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.feedback_submit_button:
			sendFeedBack();
			break;

		default:
			break;
		}
	}
	
	public void sendFeedBack() {
		String content = et_feedback_content.getText().toString();
		String contactMethod = et_feedback_contact_method.getText().toString();
		if(!TextUtils.isEmpty(content)){
			if(content.equals(msg) && contactMethod.equals(contact)){
				Toast.makeText(this, "�����ظ��ύ����", Toast.LENGTH_SHORT).show();
			}else {
				msg = content;
				contact = contactMethod;
				// ���ͷ�����Ϣ
				saveFeedbackMsg(content, contactMethod);
				Toast.makeText(this, "���ķ�����Ϣ�ѷ���", Toast.LENGTH_SHORT).show();
				
			}
		}else {
			Toast.makeText(this, "����д�������ݻ���ϵ��ʽ", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * ���淴����Ϣ��������
	 * @param msg ������Ϣ
	 */
	private void saveFeedbackMsg(String msg, String contactMethod){
		Feedback feedback = new Feedback();
		feedback.setContent(msg);
		feedback.setContacts(contactMethod);
		feedback.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i("bmob", "������Ϣ�ѱ��浽������");
				et_feedback_contact_method.setText("");
				et_feedback_content.setText("");
				finish();
			}
			
			@Override
			public void onFailure(int code, String arg0) {
				// TODO Auto-generated method stub
				Log.e("bmob", "���淴����Ϣʧ�ܣ�"+arg0);
			}
		});
	}

}
