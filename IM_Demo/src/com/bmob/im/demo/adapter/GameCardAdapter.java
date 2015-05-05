package com.bmob.im.demo.adapter;  
      
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;  

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameCard;
import com.bmob.im.demo.R;
import com.bmob.im.demo.R.id;
import com.bmob.im.demo.bean.GameFile;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.ui.GameCenterActivity;
import com.bmob.im.demo.util.DownloadService;
import com.dd.library.CircularProgressButton;
import com.deep.animation.ExpandAnimation;
import com.deep.momo.game.ui.GameFruitActivity;
import com.deep.momo.game.ui.GuessNumberActivity;
import com.deep.momo.game.ui.MixedColorMenuActivity;

import android.annotation.SuppressLint;
import android.content.Context;  
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
	int recentPlay = 0;
	
    private List<GameCard> mCards;  
    private Context mContext; 
    
    private BmobQuery<GameFile> query;
    
    
    
//    @SuppressLint("HandlerLeak")
//	public Handler updateHandler = new Handler(){
//    	
//    	@Override
//		public void handleMessage(Message msg) {   
//            switch (msg.what) {
//            
//            case 1:
//
//            	Bundle data = msg.getData();
//            	float progress = data.getFloat("progress");
//            	
//            	mHolder.download_progress_tv.setText(progress + "%");
//            	
//            	break;
//            }   
//            super.handleMessage(msg);   
//       }
//    	
//    };
    
          
    public GameCardAdapter(Context mContext)  
    {   
         this.mContext = mContext;  
         this.mCards = CustomApplcation.gameCardList; 
         
         query = new BmobQuery<GameFile>("GameFile");
         
         Intent intent  = new Intent(mContext,DownloadService.class);
         mContext.startService(intent);
         
    }  
    
    /** ��ListView���ݷ����仯ʱ,���ô˷���������ListView
	  * @Title: updateListView
	  * @Description: TODO
	  * @param @param list 
	  * @return void
	  * @throws
	  */
	public void updateListView(List<GameCard> list) {
		this.mCards = list;
		notifyDataSetChanged();
	}
    
    @Override  
    public int getCount()   
    {  
        return mCards.size();  
    }  
      
    @Override  
    public Object getItem(int Index)   
    {  
        return mCards.get(Index);  
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
        mHolder.download_progress_tv = (TextView) convertView.findViewById(R.id.card_item_download_progress_tv);
        mHolder.toolbar = convertView.findViewById(R.id.game_item_details_layout);
        
        
        // �����أ��Ѱ�װ
        if (mCards.get(Index).getGameStatus() == 2) {
        	if (!CustomApplcation.sex) {
    			mHolder.play_game.setImageResource(R.drawable.base_game_card_list_play_female_selector);
    		}else {
    			mHolder.play_game.setImageResource(R.drawable.base_game_card_list_play_selector);
			}
        	
        	 mHolder.play_game.setTag(2);
		}

        // δ���أ�δ��װ��Ϸ
        if (mCards.get(Index).getGameStatus() == 0) {
        	
        	// Ů��
			if (!CustomApplcation.sex) {
				mHolder.play_game.setImageResource(R.drawable.base_game_card_list_download_female_selector);
			}
			else {
				mHolder.play_game.setImageResource(R.drawable.base_game_card_list_download_selector);
			}
        	
			
			mHolder.play_game.setTag(0);
			
			// Toast.makeText(mContext, "δ��װ", Toast.LENGTH_LONG).show();
		}
        
        // �����أ�δ��װ
        else if(mCards.get(Index).getGameStatus() == 1){
			
        	// Ů��
        	if (!CustomApplcation.sex) {
        		mHolder.play_game.setImageResource(R.drawable.base_game_card_list_install_female_selector);
        	}
        	else {
        		mHolder.play_game.setImageResource(R.drawable.base_game_card_list_install_selector);
        	}
        	        	
        			
        	mHolder.play_game.setTag(1);
        	
		}
        
        
        //��ס����������setImageResource()����������setBackgroundResource(),����ͼ�����ΰ�  
        mHolder.game_icon.setImageResource(mCards.get(Index).getDrawable()); 
        mHolder.gameName.setText(mCards.get(Index).getGameName());
        mHolder.gameRuleDetails.setText(mCards.get(Index).getGameRuleDetails());
        mHolder.gameWinMethod.setText(mCards.get(Index).getGameWinMethod());
        
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
				
				// �Ѱ�װ
				case 2:
					
					Intent intent = new Intent();
					Bundle data = new Bundle();
					data.putString("from", "me");
					intent.putExtras(data);
					switch (Index) {
					// ˮ��������
					case 0:
						intent.setClass(mContext, GameFruitActivity.class);
						recentPlay = 1;
						break;
					// ������
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
						        .parse("info://��������Ӧ�ó����Activity" ));  
						//  ��������   
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
					
					if (Index == 3) {
						mContext.startActivity(intent);
					}else {
						mContext.startActivity(intent);
					}
					
					break;
					
					
				// δ����
				case 0:
					
					// ����apk
					downLoadFile(mHolder, Index);
					
					break;
					
				// �����أ�δ��װ
				case 1:
					
					String apkDir = Environment.getExternalStorageDirectory().getPath() + "/Bmob_IM_test/GameAPK/";
					
					File tempFile = new File(apkDir + CustomApplcation.gameList.get(Index) + "_" 
							+ CustomApplcation.notificationId.get(Index) + ".apk");
					
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
        TextView download_progress_tv;
        View toolbar;
    }  
    
    private void downLoadFile(final ViewHolder mHolder, final int index) {
    	
    	String packageName = CustomApplcation.gamePackageName.get(index);
    	
		query.addWhereEqualTo("packageName", packageName);
		
		query.findObjects(mContext, new FindListener<GameFile>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				Toast.makeText(mContext, "����ʧ�ܣ�", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(List<GameFile> arg0) {
				// TODO Auto-generated method stub
				
				// Toast.makeText(mContext, "���У�" + arg0.size() + "������", Toast.LENGTH_LONG).show();
				
				// String fileUrl = arg0.get(0).getFileUrl(mContext);
				
				BmobFile file = arg0.get(0).getFile();
				
				// Toast.makeText(mContext, file.getFileUrl(mContext), Toast.LENGTH_LONG).show();
				
				// �����̣߳������ļ�		
				
				DownloadService.downNewFile(file.getFileUrl(mContext), CustomApplcation.notificationId.get(index), CustomApplcation.gameList.get(index));
				
				
			}
		});
		
	}
    
    // ��װ���غ��apk�ļ�
 	private void Instanll(File file, Context context) {
 		Intent intent = new Intent(Intent.ACTION_VIEW);
 		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 		intent.setAction(android.content.Intent.ACTION_VIEW);
 		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
 		context.startActivity(intent);
 	}
    

}  