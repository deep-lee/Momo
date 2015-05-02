package com.bmob.im.demo.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;

import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.PhotoWallAdapter;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.dialog.DialogTips;

import C.From;
import android.R.integer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoWallActivity extends ActivityBase {
	
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
	
	byte[] imageByte;
	Bitmap bitmap;
	
	Handler handler = new Handler(){
		
		 public void handleMessage(Message msg) {   
             switch (msg.what) {   
             
             	case 0:
             		Intent intent = new Intent();
    				intent.setClass(PhotoWallActivity.this, ImageShowActivity.class);
//    				intent.putExtra("imageByte", imageByte);
    				intent.putExtra("bitmap", bitmap);
    				startActivity(intent);
             		break;
             
             }   
             super.handleMessage(msg);   
        } 
	};

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
		
		// Ů������
		if (!CustomApplcation.sex) {
			setActionBgForFemale();
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
		
		
		mPhotoWall.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				showDeleteDialog(position, CustomApplcation.myWallPhoto.size());
				
				return true;
			}
		});
		
		
		mPhotoWall.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final ImageView imageView = (ImageView)view.findViewById(R.id.photo);
				
				imageView.setDrawingCacheEnabled(true);
				
				bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
				imageView.setDrawingCacheEnabled(false);
				
				// ͼƬ��ʵ��Ⱥ͸߶�
				int dw = imageView.getDrawable().getBounds().width();  
                int dh = imageView.getDrawable().getBounds().height();  
                // ImageView�Ŀ�Ⱥ͸߶�
                int iv_w = imageView.getWidth();
                int iv_h = imageView.getHeight();
                
                float sx = (float)dw / iv_w;
                float sy = (float)dh / iv_h;
                
				
				Intent intent = new Intent();
				intent.setClass(PhotoWallActivity.this, ImageShowActivity.class);
				intent.putExtra("bitmap", bitmap);
				intent.putExtra("sx", sx);
				intent.putExtra("sy", sy);
				startActivity(intent);
				
				
			}
		});
	}
	
	public void showDeleteDialog(final int position, int all) {
		int currentPhotoNum = position + 1;
		DialogTips dialog = new DialogTips(PhotoWallActivity.this, "" + currentPhotoNum + "/" + all, "ɾ����Ƭ", "ȷ��", true, true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				
				CustomApplcation.myWallPhoto.remove(position);
				
				User user = new User();
				String photoFile = "";
				int size = CustomApplcation.myWallPhoto.size();
				for (int i = 0; i < size; i++) {
					if (i < (size - 1)) {
						photoFile = photoFile + CustomApplcation.myWallPhoto.get(i) + ";";
						
					}
					else {
						photoFile = photoFile + CustomApplcation.myWallPhoto.get(i);
					}
				}
				user.setPhotoWallFile(photoFile);
				
				updateUserData(user, new UpdateListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						CustomApplcation.numOfPhoto--;
						ShowToast("ɾ���ɹ�");
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						ShowToast("ɾ��ʧ��");
					}
				});
				
				onResume();
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
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
				    	
				    	ShowToast(bmobFile.getFileUrl(PhotoWallActivity.this));
				    	ShowLog(bmobFile.getFileUrl(PhotoWallActivity.this));
				    	
				    	User user = new User();
				    	User current = userManager.getCurrentUser(User.class);
				    	if ((current.getPhotoWallFile() == null) || (current.getPhotoWallFile().equals(""))) {
				    		user.setPhotoWallFile(bmobFile.getFileUrl(PhotoWallActivity.this));
						}
				    	else {
				    		user.setPhotoWallFile(current.getPhotoWallFile() + ";" + bmobFile.getFileUrl(PhotoWallActivity.this));
				    		
				    		
						}
				    	
				    	// �����û�����Ϣ
				        updateUserData(user, new UpdateListener() {
				    		
				    		@Override
				    		public void onSuccess() {
				    			// TODO Auto-generated method stub
				    			 ShowToast("��Ƭǽ��Ϣ���³ɹ�");
				    			 
				    			 mApplication.myWallPhoto.add(bmobFile.getFileUrl(PhotoWallActivity.this));
				    			 
				    			 // ˢ��adapter
				    			 // changeAdapter();
				    			 
				    			// mAdapter.notifyDataSetChanged();
				    			 onResume();
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

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.setData();
		mAdapter.notifyDataSetChanged();
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
	
	private void updateUserData(User user,UpdateListener listener){
		User current = (User) userManager.getCurrentUser(User.class);
		user.setObjectId(current.getObjectId());
		user.update(this, listener);
	}
	
	Bitmap drawable2Bitmap(Drawable drawable) {  
        if (drawable instanceof BitmapDrawable) {  
            return ((BitmapDrawable) drawable).getBitmap();  
        } else if (drawable instanceof NinePatchDrawable) {  
            Bitmap bitmap = Bitmap  
                    .createBitmap(  
                            drawable.getIntrinsicWidth(),  
                            drawable.getIntrinsicHeight(),  
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                    : Bitmap.Config.RGB_565);  
            Canvas canvas = new Canvas(bitmap);  
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                    drawable.getIntrinsicHeight());  
            drawable.draw(canvas);  
            return bitmap;  
        } else {  
            return null;  
        }  
    }  
	
	byte[] Bitmap2Bytes(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    }
}
