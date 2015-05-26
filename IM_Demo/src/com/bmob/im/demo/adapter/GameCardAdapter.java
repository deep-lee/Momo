package com.bmob.im.demo.adapter;  
      
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;  

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameInfo;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.GameCenterActivity;
import com.bmob.im.demo.util.DownloadService;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.deep.animation.ExpandAnimation;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

import android.annotation.SuppressLint;
import android.content.Context;  
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageButton;
import android.widget.ImageView;  
import android.widget.TextView;  
import android.widget.Toast;
      
public class GameCardAdapter extends BaseAdapter   
{  
	public static int NET_NOT_AVAIABLE = 0;
	public static int NET_MOBILE = 1;
	public static int NET_WIFI = 2;
	
	int recentPlay = 0;
	
    private List<GameInfo> mData;  
    private Context mContext; 
    
    private BmobQuery<GameFile> query;
    
          
    public GameCardAdapter(Context mContext)  
    {   
         this.mContext = mContext;  
         this.mData = CustomApplcation.gameList; 
         
         query = new BmobQuery<GameFile>();
         
         Intent intent  = new Intent(mContext,DownloadService.class);
         mContext.startService(intent);
         
    }  
    
    /** 当ListView数据发生变化时,调用此方法来更新ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<GameInfo> list) {
		this.mData = list;
		notifyDataSetChanged();
	}
    
    @Override  
    public int getCount()   
    {  
        return mData.size();  
    }  
      
    @Override  
    public Object getItem(int Index)   
    {  
        return mData.get(Index);  
    }  
      
    @Override  
    public long getItemId(int Index)   
    {  
        return Index;  
    }  
    
    @SuppressLint("InflateParams")
	@Override  
    public View getView(final int Index, View convertView, ViewGroup mParent)   
    {  
    	final ViewHolder mHolder = new ViewHolder();
    	
        if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_list_card_item, null);
		}
          
        mHolder.gameName = (TextView)convertView.findViewById(R.id.card_item_title);  
        mHolder.game_icon=(ImageView)convertView.findViewById(R.id.card_item_icon);  
        mHolder.gameRuleDetails = (TextView) convertView.findViewById(R.id.card_item_game_rule_details);
        mHolder.gameWinMethod = (TextView) convertView.findViewById(R.id.card_item_game_win_method_details);
        mHolder.play_game = (ImageButton) convertView.findViewById(R.id.card_item_play);
        mHolder.toolbar = convertView.findViewById(R.id.game_item_details_layout);
        
        
        // 已下载，已安装
        if (mData.get(Index).getGame_status() == 2) {
    		mHolder.play_game.setImageResource(R.drawable.base_game_card_list_play_selector);
        	
        	 mHolder.play_game.setTag(2);
		}

        // 未下载，未安装游戏
        if (mData.get(Index).getGame_status() == 0) {
        	
			mHolder.play_game.setImageResource(R.drawable.base_game_card_list_download_selector);
        				
			mHolder.play_game.setTag(0);
			
			// Toast.makeText(mContext, "未安装", Toast.LENGTH_LONG).show();
		}
        
        // 已下载，未安装
        else if(mData.get(Index).getGame_status() == 1){
			
        	mHolder.play_game.setImageResource(R.drawable.base_game_card_list_install_selector);
        	        	
        	mHolder.play_game.setTag(1);
        	
		}
        
        Bitmap bitmap = getBitmapFromAsset(mContext, mData.get(Index).getGame_icon());
        
        //记住啊，这里是setImageResource()方法，不是setBackgroundResource(),否则图像会变形啊  
        mHolder.game_icon.setImageBitmap(bitmap);
        mHolder.gameName.setText(mData.get(Index).getGame_name());
        mHolder.gameRuleDetails.setText(mData.get(Index).getGame_rule());
        mHolder.gameWinMethod.setText(mData.get(Index).getGame_win_method());
        
        convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				

				// Creating the expand animation for the item
				ExpandAnimation expandAni = new ExpandAnimation(mHolder.toolbar, 500);

				// Start the animation on the toolbar
				mHolder.toolbar.startAnimation(expandAni);
				
			}
		});
        
        mHolder.play_game.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				switch ((Integer)v.getTag()) {
				
				// 已安装
				case 2:
					
					Intent intent = new Intent();
					Bundle data = new Bundle();
					data.putString("from", "me");
					intent.putExtras(data);
					switch (Index) {
					// 水果连连看
					case 0:
						intent.setClass(mContext, GameFruitActivity.class);
						recentPlay = 1;
						break;
					// 猜数字
					case 1:
						intent.setClass(mContext, GuessNumberActivity.class);
						recentPlay = 2;
						break;
					// Mixed color
					case 2:
						intent.setClass(mContext, MixedColorMenuActivity.class);
						recentPlay = 3;
						break;
						
					// Oh my egg
					case 3:
						
						intent = new  Intent("com.nsu.ttgame.ohmyeggs.MYACTION" , Uri  
						        .parse("info://调用其他应用程序的Activity" ));  
						//  传递数据   
						intent.putExtra("value" , -1);  
//						startActivityForResult(intent, 1);  

						break;
						
					case 4:
						
						intent = new  Intent("com.jk.fingerGame.MYACTION" , Uri  
						        .parse("info://调用其他应用程序的Activity" ));  
						//  传递数据   
						intent.putExtra("value" , -1);  
//						startActivityForResult(intent, 1);  

						break;

					default:
						break;
					}
					
					User user = new User();
					user.setRecentPlayGame(recentPlay);
					
					((GameCenterActivity)mContext).updateUserData(user, new UpdateListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
					
					if (Index == 3 || Index == 4) {
						mContext.startActivity(intent);
					}else {
						mContext.startActivity(intent);
					}
					
					break;
					
					
				// 未下载
				case 0:
					
					Log.i("LLLLLLLLLLLLLLLL", "点击了下载按钮！");
					
					// 下载apk
					// 判断当前是WI-FI还是移动网络
					int netType = CustomApplcation.getInstance().getCurrentNetType();
					
					switch (netType) {
					case 0:
						
						((GameCenterActivity)mContext).ShowToast(R.string.network_tips);
						break;
						
					case 1:
						DialogTips dialogTips = new DialogTips(mContext, "移动网络", "当前非WI-FI连接，是否继续下载？", "确认", true, true);
						dialogTips.SetOnSuccessListener(new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								downLoadFile(mHolder, Index);
							}
						});
						
						dialogTips.SetOnCancelListener(new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
						
						dialogTips.show();
						break;
						
					case 2:
						downLoadFile(mHolder, Index);
						break;

					default:
						break;
					}
					
					break;
					
				// 已下载，未安装
				case 1:
					
					String apkDir = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
					
					File tempFile = new File(apkDir + CustomApplcation.gameList.get(Index) + "_" 
							+ mData.get(Index).getNotificationId() + ".apk");
					
					Instanll(tempFile, mContext);
							
					break;

				default:
					break;
				}
				
			}
		});
        
        return convertView;  
    }  
      
    private static class ViewHolder  
    {  
        TextView gameName;  
        ImageView game_icon;
        TextView gameRuleDetails;
        TextView gameWinMethod;
        ImageButton play_game;
        View toolbar;
    }  
    
    private void downLoadFile(final ViewHolder mHolder, final int index) {
    	
    	String packageName = mData.get(index).getPackage_name();
    	
		query.addWhereEqualTo("packageName", packageName);
		
		query.findObjects(mContext, new FindListener<GameFile>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				Toast.makeText(mContext, "下载失败！", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(List<GameFile> arg0) {
				// TODO Auto-generated method stub
				
				BmobFile file = arg0.get(0).getFile();
				
				// 开启线程，下载文件		
				DownloadService.downNewFile(file.getFileUrl(mContext), mData.get(index).getNotificationId(), mData.get(index).getGame_name());
				
			}
		});
		
	}
    
    // 安装下载后的apk文件
 	private void Instanll(File file, Context context) {
 		Intent intent = new Intent(Intent.ACTION_VIEW);
 		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 		intent.setAction(android.content.Intent.ACTION_VIEW);
 		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
 		context.startActivity(intent);
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
    

}  