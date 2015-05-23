package com.bmob.im.demo.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.layout;
import com.bmob.im.demo.bean.ChatBg;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.deep.ui.update.MainActivity2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ShowChatBgActivity extends BaseActivity {
	
	private ImageView iv_chat_bg;
	private Button btn_select;
	
	private ChatBg chatBg;
	
	CustomProgressDialog progress;
	
	SharedPreferences mySharedPreferences;
	//ʵ����SharedPreferences.Editor���󣨵ڶ����� 
	SharedPreferences.Editor editor; 
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
    				Bundle data = msg.getData();
    				String chat_bg_address = data.getString("chat_bg_address");
    				CustomApplcation.chatBgAddress = chat_bg_address;
    				
    				progress.dismiss();
    				ShowToast("���óɹ���");
    				
    				editor.putString("chat_bg_address", chat_bg_address);
    				editor.commit();
    				
    				User user = new User();
    				user.setChatBg(chatBg);
    				updateUserData(user, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							// ShowToast("���³ɹ���");
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							// ShowToast("����ʧ�ܣ�");
						}
					});
    				
    				finish();
    				
            		break;
            
            }   
            super.handleMessage(msg);   
       }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_chat_bg);
		
		initView();
	}
	
	public void initView() {
		
		mySharedPreferences = getSharedPreferences("test", 
				Activity.MODE_PRIVATE); 
		editor = mySharedPreferences.edit(); 
		
		chatBg = (ChatBg) getIntent().getSerializableExtra("chatBg");
		
		iv_chat_bg = (ImageView) findViewById(R.id.chat_bg_show_image);
		btn_select = (Button) findViewById(R.id.select_chat_bg);
		
		chatBg.getFile().loadImage(ShowChatBgActivity.this, iv_chat_bg);
		
		btn_select.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// down��������Ϊѡ����ͼƬ
				
				progress = new CustomProgressDialog(ShowChatBgActivity.this, "��������...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				downLoadChatBgToSD(chatBg);
			}
		});
	}
	
	public void downLoadChatBgToSD(final ChatBg chatBg) {
		
		new Thread(){
			@Override
			public void run() {
				String urlStr = chatBg.getFile().getFileUrl(ShowChatBgActivity.this);
				String dirName = "";
				dirName = Environment.getExternalStorageDirectory()+"/Find/";
				File f = new File(dirName);
				if(!f.exists())
				{
				    f.mkdir();
				}
				
				String newFilename = chatBg.getObjectId();
				newFilename = dirName + newFilename;
				File file = new File(newFilename);
				//���Ŀ���ļ��Ѿ����ڣ���ɾ�����������Ǿ��ļ���Ч��
				if(file.exists())
				{
				    file.delete();
				}
				
				try {
			         // ����URL   
			         URL url = new URL(urlStr);   
			         // ������   
			         URLConnection con = url.openConnection();
			         //����ļ��ĳ���
			         int contentLength = con.getContentLength();
			         System.out.println("���� :"+contentLength);
			         // ������   
			         InputStream is = con.getInputStream();  
			         // 1K�����ݻ���   
			         byte[] bs = new byte[1024];   
			         // ��ȡ�������ݳ���   
			         int len;   
			         // ������ļ���   
			         OutputStream os = new FileOutputStream(newFilename);   
			         // ��ʼ��ȡ   
			         while ((len = is.read(bs)) != -1) {   
			             os.write(bs, 0, len);   
			         }  
			         // ��ϣ��ر���������   
			         os.close();  
			         is.close();
			         
			         Message message = new Message();
			         message.what = 0;
			         Bundle data = new Bundle();
			         data.putString("chat_bg_address", file.getAbsolutePath());
			         message.setData(data);
			         handler.sendMessage(message);
			            
				} catch (Exception e) {
				        e.printStackTrace();
				        ShowToast("����ʧ�ܣ�");
				}
			}
			
		}.start();
	}
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
}
