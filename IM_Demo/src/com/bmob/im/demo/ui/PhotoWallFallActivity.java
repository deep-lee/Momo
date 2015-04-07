package com.bmob.im.demo.ui;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.view.MyScrollView;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

public class PhotoWallFallActivity extends BaseActivity {

	public String from = "";
	
	private String picPath = null;
	private static final String TAG = "uploadImage";
	
	/**
	 * ѡ���ļ�
	 */
	public static final int TO_SELECT_PHOTO = 3;
	
	public MyScrollView photoWallView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo_wall_fall);
		
		photoWallView = (MyScrollView) findViewById(R.id.my_scroll_view);
		
		from = getIntent().getStringExtra("from");
		
		if (from.equals("me")) {
			initTopBarForBoth("��Ƭǽ", R.drawable.base_action_bar_upload_selector, new onRightImageButtonClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(PhotoWallFallActivity.this,SelectPictureActivity.class);
					startActivityForResult(intent, TO_SELECT_PHOTO);
				}
			});
		}
		else if(from.equals("other")){
			initTopBarForLeft("��Ƭǽ");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
		{
			picPath = data.getStringExtra(SelectPictureActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "����ѡ���ͼƬ="+picPath);
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			
			uploadPhoto(picPath);
			
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void uploadPhoto(String filePath) {
		
		// �������
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
		if(!isNetConnected){
			ShowToast(R.string.network_tips);
			return;
		}else {
			if (CustomApplcation.numOfPhoto < 100) {
				final BmobFile bmobFile = new BmobFile(new File(filePath));
				
				bmobFile.uploadblock(this, new UploadFileListener() {

				    @Override
				    public void onSuccess() {
				        // TODO Auto-generated method stub
				        // bmobFile.getUrl()---���ص��ϴ��ļ��ĵ�ַ������������
				        // bmobFile.getFileUrl(context)--���ص��ϴ��ļ���������ַ����������
				    	
				    	ShowToast(bmobFile.getFileUrl(PhotoWallFallActivity.this));
				    	ShowLog(bmobFile.getFileUrl(PhotoWallFallActivity.this));
				    	
				    	User user = new User();
				    	User current = userManager.getCurrentUser(User.class);
				    	if ((current.getPhotoWallFile() == null) || (current.getPhotoWallFile().equals(""))) {
				    		user.setPhotoWallFile(bmobFile.getFileUrl(PhotoWallFallActivity.this));
						}
				    	else {
				    		user.setPhotoWallFile(current.getPhotoWallFile() + ";" + bmobFile.getFileUrl(PhotoWallFallActivity.this));
						}
				    	
				    	// �����û�����Ϣ
				        updateUserData(user, new UpdateListener() {
				    		
				    		@Override
				    		public void onSuccess() {
				    			// TODO Auto-generated method stub
				    			 ShowToast("��Ƭǽ��Ϣ���³ɹ�");
				    			 
				    			 CustomApplcation.myWallPhoto.add(bmobFile.getFileUrl(PhotoWallFallActivity.this));
				    			 
				    			 //�Լ������Լ���activity
				    			 Intent intent = new Intent(PhotoWallFallActivity.this, PhotoWallFallActivity.class);
				    			 intent.putExtra("from", from);
				    			 startActivity(intent);
				    			 
				    			 //close this activity
				    			 quit();
				    			 
				    		}
				    		
				    		@Override
				    		public void onFailure(int arg0, String arg1) {
				    			// TODO Auto-generated method stub
				    			 ShowToast("��Ƭǽ��Ϣ���³ɹ�");
				    		}
				    	});
				    }

				    @Override
				    public void onProgress(Integer value) {
				        // TODO Auto-generated method stub
				        // ���ص��ϴ����ȣ��ٷֱȣ�
				    }

				    @Override
				    public void onFailure(int code, String msg) {
				        // TODO Auto-generated method stub
				        // toast("�ϴ��ļ�ʧ�ܣ�" + msg);
				    }
				});
			}else {
				ShowToast("��Ƭ�����Ѵ�100������");
				return;
			}
			
		}
		
	}

	public void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
	
	public void quit() {
		finish();
	}
}
