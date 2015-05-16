package com.bmob.im.demo.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.ImageAdApter;
import com.bmob.im.demo.bean.DefaultGameFile;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.DownloadService;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.dd.library.CircularProgressButton;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class GameDetailsActivity extends BaseMainActivity implements OnClickListener{
	
	TextView tv_title;
	ImageView iv_game_icon;
	TextView tv_game_name, tv_game_description, tv_game_info;
	CircularProgressButton btn_play;
	Gallery gallery;
	GameInfo gameInfo;
	
	Boolean first_launch = true;
	
	int recentPlay = 0;
	
	ImageAdApter mAdApter;
	
	CustomProgressDialog progress;
	
	List<String> imageUrl;
	
	@SuppressLint("HandlerLeak")
	Handler loadHandler = new Handler(){
		
		public void handleMessage(Message msg) {   
            switch (msg.what) {   
            
            	case 0:
            		
            		initGallery();
    				progress.dismiss();
    				progress = null;
            		break;
            
            }   
            super.handleMessage(msg);  
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_details);
		
		initView();
	}
	
	public void initView() {
		gameInfo = (GameInfo) getIntent().getExtras().getSerializable("gameInfo");
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_game_icon = (ImageView) findViewById(R.id.game_list_item_icon);
		tv_game_name = (TextView) findViewById(R.id.game_list_item_name);
		tv_game_description = (TextView) findViewById(R.id.game_list_item_description);
		tv_game_info = (TextView) findViewById(R.id.game_info);
		btn_play = (CircularProgressButton) findViewById(R.id.btn_play);
		gallery = (Gallery) findViewById(R.id.gallery);
		
		switch (gameInfo.game_status) {
		case 0:
			btn_play.setText("下载");
			btn_play.setTag(0);
			break;
			
		case 1:
			btn_play.setText("安装");
			btn_play.setTag(1);
			break;
			
		case 2:
			btn_play.setText("Play");
			btn_play.setTag(2);
			break;
			
		case 3:
			btn_play.setText("Play");
			btn_play.setTag(3);
			break;

		default:
			break;
		}
		
		btn_play.setOnClickListener(this);
		
		tv_title.setText(gameInfo.getGame_name());
		tv_game_description.setText("5000人正在玩");
		
		Bitmap bitmap = getBitmapFromAsset(GameDetailsActivity.this, gameInfo.getGame_icon());
		iv_game_icon.setImageBitmap(bitmap);
		tv_game_name.setText(gameInfo.getGame_name());
		tv_game_info.setText(gameInfo.getGame_rule() + "\n" + gameInfo.getGame_win_method());
		
		progress = new CustomProgressDialog(GameDetailsActivity.this, "正在加载...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		loadImage();
		
		
	}
	
	public void loadImage() {
		
		new Thread(){
			
			@Override
			public void run(){
				
				imageUrl = new ArrayList<String>();
				String package_name = gameInfo.getPackage_name();
				
				if (package_name.equals("gamelianliankan") || package_name.equals("gameguessnumber") || package_name.equals("gamemixedcolor")) {
					BmobQuery<DefaultGameFile> query = new BmobQuery<DefaultGameFile>();
					query.addWhereEqualTo("packageName", package_name);
					query.findObjects(GameDetailsActivity.this, new FindListener<DefaultGameFile>() {
						
						@Override
						public void onSuccess(List<DefaultGameFile> arg0) {
							// TODO Auto-generated method stub
							
							
							DefaultGameFile defaultGameFile = arg0.get(0);
							imageUrl.add(defaultGameFile.getGameDisplay1().getFileUrl(GameDetailsActivity.this));
							imageUrl.add(defaultGameFile.getGameDisplay2().getFileUrl(GameDetailsActivity.this));
							imageUrl.add(defaultGameFile.getGameDisplay3().getFileUrl(GameDetailsActivity.this));
							imageUrl.add(defaultGameFile.getGameDisplay4().getFileUrl(GameDetailsActivity.this));
							
							for (int i = 0; i < imageUrl.size(); i++) {
								Log.i("TTTTTTTTTTTTTTTTTTTTTT", imageUrl.get(i));
							}
							
							Message message = new Message();
							message.what = 0;
							loadHandler.sendMessage(message);
							
						}
						
						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
				}else {
					BmobQuery<GameFile> query = new BmobQuery<GameFile>();
					query.addWhereEqualTo("packageName", package_name);
					query.findObjects(GameDetailsActivity.this, new FindListener<GameFile>() {
						
						@Override
						public void onSuccess(List<GameFile> arg0) {
							// TODO Auto-generated method stub
							
							GameFile gameFile = arg0.get(0);
							imageUrl.add(gameFile.getGameDisplay1().getFileUrl(GameDetailsActivity.this));
							imageUrl.add(gameFile.getGameDisplay2().getFileUrl(GameDetailsActivity.this));
							imageUrl.add(gameFile.getGameDisplay3().getFileUrl(GameDetailsActivity.this));
							imageUrl.add(gameFile.getGameDisplay4().getFileUrl(GameDetailsActivity.this));
							
							
							Message message = new Message();
							message.what = 0;
							loadHandler.sendMessage(message);
						}
						
						@Override
						public void onError(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				
				
			}
			
		}.start();
		
	}
	
	public void initGallery(){
		
		Log.i("AAAAAAAAAAAAAAAAA", "正在初始化Gallery");
		
		mAdApter = new ImageAdApter(GameDetailsActivity.this, imageUrl);
		gallery.setAdapter(mAdApter);
	}
	
	private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream is=null;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }
	
	// Press the back button in mobile phone
		@Override
		public void onBackPressed() {
			super.onBackPressed();
			overridePendingTransition(0, R.anim.base_slide_right_out);
		}
		
		public void back(View view){
	        
	        finish();
	        overridePendingTransition(0, R.anim.base_slide_right_out);
	    }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_play:
				switch ((Integer)btn_play.getTag()) {
				case 0:
					// 下载
					downloadApk();
					break;
				case 1:
					// 安装
					String apkDir = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
					
					File tempFile = new File(apkDir + gameInfo.getGame_name() + "_" 
							+ gameInfo.getNotificationId() + ".apk");
					
					Instanll(tempFile, GameDetailsActivity.this);
					break;
					
				case 2:
					// play
					playGame();
					break;
					
				case 3:
					playGame();
					break;

				default:
					break;
				}
				break;

			default:
				break;
			}
		}
		
		public void playGame() {
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putString("from", "me");
			intent.putExtras(data);
			
			String package_name = gameInfo.getPackage_name();
			if (package_name.equals("gamelianliankan")) {
				intent.setClass(GameDetailsActivity.this, GameFruitActivity.class);
				recentPlay = 1;
			}else if (package_name.equals("gameguessnumber")) {
				intent.setClass(GameDetailsActivity.this, GuessNumberActivity.class);
				recentPlay = 2;
			}else if (package_name.equals("gamemixedcolor")) {
				intent.setClass(GameDetailsActivity.this, MixedColorMenuActivity.class);
				recentPlay = 3;
			}else {
				intent = new  Intent(package_name + ".MYACTION" , Uri  
				        .parse("info://调用其他应用程序的Activity" ));  
				//  传递数据   
				intent.putExtra("value" , -1); 
			}
			
			User user = new User();
			user.setRecentPlayGame(recentPlay);
			
			updateUserData(user, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
			});
			
			startActivity(intent);
			
		}
		
		// 安装下载后的apk文件
	 	private void Instanll(File file, Context context) {
	 		Intent intent = new Intent(Intent.ACTION_VIEW);
	 		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 		intent.setAction(android.content.Intent.ACTION_VIEW);
	 		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
	 		context.startActivity(intent);
	 	}
		
		public void downloadApk() {
			
			// 下载apk
			// 判断当前是WI-FI还是移动网络
			int netType = CustomApplcation.getInstance().getCurrentNetType();
			
			// Toast.makeText(mContext, "网络类型：" + netType, Toast.LENGTH_LONG).show();
			
			switch (netType) {
			case 0:
				
				ShowToast(R.string.network_tips);
				break;
				
			case 1:
				DialogTips dialogTips = new DialogTips(GameDetailsActivity.this, "移动网络", "当前非WI-FI连接，是否继续下载？", "确认", true, true);
				dialogTips.SetOnSuccessListener(new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						downLoadFile();
					}
				});
				
				dialogTips.SetOnCancelListener(new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				
				dialogTips.show();
				break;
				
			case 2:
				downLoadFile();
				break;

			default:
				break;
			}
			
		}
		
		public void downLoadFile() {
			
			String packageName = gameInfo.getPackage_name();
			
			BmobQuery<GameFile> query = new BmobQuery<GameFile>();
	    	
			query.addWhereEqualTo("packageName", packageName);
			
			query.findObjects(GameDetailsActivity.this, new FindListener<GameFile>() {

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
					ShowToast("下载失败！");
				}

				@Override
				public void onSuccess(List<GameFile> arg0) {
					// TODO Auto-generated method stub
					
					BmobFile file = arg0.get(0).getFile();
					
					// 开启线程，下载文件		
					DownloadService.downNewFile(file.getFileUrl(GameDetailsActivity.this),
							gameInfo.getNotificationId(), gameInfo.getGame_name());
					
				}
			});
			
		}

		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			
			//  判断是否已经安装或者下载
			if (!first_launch) {
				String package_naeme = gameInfo.getPackage_name();
				if (!package_naeme.equals("gamelianliankan") && !package_naeme.equals("gameguessnumber") || !package_naeme.equals("gamemixedcolor")) {
					
					int game_status = CustomApplcation.getInstance().getGameStatus(gameInfo.package_name, gameInfo.game_name, gameInfo.getNotificationId());
					
					if (game_status != gameInfo.game_status) {
						gameInfo.setGame_status(game_status);
					}
					
					switch (game_status) {
					case 0:
						btn_play.setText("下载");
						btn_play.setTag(0);
						break;
						
					case 1:
						btn_play.setText("安装");
						btn_play.setTag(1);
						break;
						
					case 2:
						btn_play.setText("Play");
						btn_play.setTag(2);
						break;
						
					case 3:
						btn_play.setText("Play");
						btn_play.setTag(3);
						break;

					default:
						break;
					}
					
				}
				
			}
			
		}
		
		public void updateUserData(User user,UpdateListener listener){
			User current = (User) userManager.getCurrentUser(User.class);
			user.setObjectId(current.getObjectId());
			user.update(this, listener);
		}
		
		// 判断应用是否安装
		public boolean isAppInstalled(Context context,String packagename)
		{
			   PackageInfo packageInfo; 
			    	
			   try {
			                
				   packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
			             
			   }catch (NameNotFoundException e) {
			    		
			    packageInfo = null;
			    		
			    		e.printStackTrace();
			    		
			    	}
			             
			    	if(packageInfo ==null){
			    		
			    		return false;
			    		
			    	}else{
			    		
			    		return true;
			    		
			    	}
			    }
			    
				public Boolean isApkDownloaded(String fileName) {
					
					String apkDir = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
					File rootFile = new File(apkDir);
					
					if (!rootFile.exists()) {
						return false;
					}
					
					File tempFile = new File(apkDir + fileName + ".apk");
					if (tempFile.exists()){
						return true;
					}else {
						return false;
					}
					
				}
				
				
				@Override
				protected void onPause() {
					// TODO Auto-generated method stub
					super.onPause();
					
					first_launch = false;
					
				}
		
		
}
