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
	 * 用于展示照片墙的GridView
	 */
	private GridView mPhotoWall;

	/**
	 * GridView的适配器
	 */
	private PhotoWallAdapter mAdapter;

	private int mImageThumbSize;
	private int mImageThumbSpacing;
	
	public String from = "";
	
	private String picPath = null;
	
	private static final String TAG = "uploadImage";
	
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_wall);
		
		from = getIntent().getStringExtra("from");
		
		if (from.equals("me")) {
			initTopBarForBoth("照片墙", R.drawable.base_action_bar_upload_selector, new onRightImageButtonClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(PhotoWallActivity.this,SelectPictureActivity.class);
					startActivityForResult(intent, TO_SELECT_PHOTO);
				}
			});
		}
		else if(from.equals("other")){
			initTopBarForLeft("照片墙");
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
			Log.i(TAG, "最终选择的图片="+picPath);
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
                ShowToast("文件已上传成功："+fileName);
                
                // 把传回的文件名和文件URL保存，并启用新的线程刷新照片墙界面
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
                ShowToast("上传出错："+errormsg);
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
		// 退出程序时结束所有的下载任务
		mAdapter.cancelAllTasks();
	}
}
