package com.bmob.im.demo.ui;

import cn.bmob.im.util.BmobLog;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.PhotoWallAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;

import C.From;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;

public class PhotoWallActivity extends BaseActivity {
	
	/**
	 * ����չʾ��Ƭǽ��GridView
	 */
	private GridView mPhotoWall;

	/**
	 * GridView��������
	 */
	private PhotoWallAdapter mAdapter;

	private int mImageThumbSize;
	private int mImageThumbSpacing;
	
	public String from = "";
	
	private String picPath = null;
	
	private static final String TAG = "uploadImage";
	
	/**
	 * ѡ���ļ�
	 */
	public static final int TO_SELECT_PHOTO = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_wall);
		
		from = getIntent().getStringExtra("from");
		
		if (from.equals("me")) {
			initTopBarForBoth("��Ƭǽ", R.drawable.base_action_bar_upload_selector, new onRightImageButtonClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(PhotoWallActivity.this,SelectPictureActivity.class);
					startActivityForResult(intent, TO_SELECT_PHOTO);
				}
			});
		}
		else if(from.equals("other")){
			initTopBarForLeft("��Ƭǽ");
		}
		
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		mPhotoWall = (GridView) findViewById(R.id.photo_wall);
		
		mAdapter = new PhotoWallAdapter(this, 0, (String[])(mApplication.myWallPhoto).toArray(new String[0]),
				mPhotoWall);
		mPhotoWall.setAdapter(mAdapter);
		mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					
					@Override
					public void onGlobalLayout() {
						final int numColumns = (int) Math.floor(mPhotoWall
								.getWidth()
								/ (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							int columnWidth = (mPhotoWall.getWidth() / numColumns)
									- mImageThumbSpacing;
							mAdapter.setItemHeight(columnWidth);
							mPhotoWall.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
		{
			picPath = data.getStringExtra(SelectPictureActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "����ѡ���ͼƬ="+picPath);
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			
			uploadPhoto(picPath);
			
			// imageView.setImageBitmap(bm);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void uploadPhoto(String filePath) {
		
		BTPFileResponse response = BmobProFile.getInstance(PhotoWallActivity.this).upload(filePath, new UploadListener() {

            @Override
            public void onSuccess(String fileName,String url) {
                // TODO Auto-generated method stub
//                dialog.dismiss();
                ShowToast("�ļ����ϴ��ɹ���"+fileName);
                
                // �Ѵ��ص��ļ������ļ�URL���棬�������µ��߳�ˢ����Ƭǽ����
                User u = (User) userManager.getCurrentUser(User.class);
                if (!u.getPhotoWallFile().equals("")) {
                	u.setPhotoWallFile(u.getPhotoWallFile() + ";" + fileName + "_" + url);
				}
                else {
                	u.setPhotoWallFile(fileName + "_" + url);
				}
                
                
                
            }

            @Override
            public void onProgress(int ratio) {
                // TODO Auto-generated method stub
                BmobLog.i("PhotoWallActivity -onProgress :"+ratio);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
                ShowToast("�ϴ�����"+errormsg);
            }
        });
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.fluchCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˳�����ʱ�������е���������
		mAdapter.cancelAllTasks();
	}
}
