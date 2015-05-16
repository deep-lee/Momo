package com.deep.db;



import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.GameInfo;
import com.deep.db.GameDBHelper.TotalGameTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;



public class GameDatabaseUtil {
	 private static final String TAG = "GameDatabaseUtil";

	    private static GameDatabaseUtil instance;

	    /** 数据库帮助类 **/
	    private GameDBHelper dbHelper;

	    public synchronized static GameDatabaseUtil getInstance(Context context) {
	        if(instance == null) {
	            instance=new GameDatabaseUtil(context);
	        }
	        return instance;
	    }

	    /**
	     * 初始化
	     * @param context
	     */
	    private GameDatabaseUtil(Context context) {
	        dbHelper=new GameDBHelper(context);
	    }

	    /**
	     * 销毁
	     */
	    public static void destory() {
	        if(instance != null) {
	            instance.onDestory();
	        }
	    }

	    /**
	     * 销毁
	     */
	    public void onDestory() {
	        instance=null;
	        if(dbHelper != null) {
	            dbHelper.close();
	            dbHelper=null;
	        }
	    }
	    
	    
	    // 判断当前数据库是否为空
	    public Boolean isDBNull() {
	    	Cursor cursor = null;
	    	cursor = dbHelper.query(GameDBHelper.TABLE_NAME, null, null, null, null, null, null);
	    	if(cursor != null && cursor.getCount() > 0) {
	    		
	    		if(cursor != null) {
    	            cursor.close();
    	            dbHelper.close();
    	        }
	    		
            	return false;
	    	}
	    	else {
	    		
	    		if(cursor != null) {
		            cursor.close();
		            dbHelper.close();
		        }
	    		
				return true;
			}
		}
	    
	    public void insertGame(GameInfo gameinfo) {
			
	    	Cursor cursor = null;
	    	
	    	// 先查询看数据库里面有没有这条纪录
	    	String where = GameDBHelper.TotalGameTable.OBJECT_ID + " = '" + gameinfo.getObject_id() + "'";
	    	
            cursor = dbHelper.query(GameDBHelper.TABLE_NAME, null, where, null, null, null, null);
            
            // 有这条纪录
            if (cursor != null && cursor.getCount() > 0) {
				// 看版本号是否一致，不一致就更新版本号和游戏状态
            	
            	Log.i("SSSSSSSSSSSSSSSSSSS", " 有记录:" + gameinfo.getGame_name());
            	
            	cursor.moveToFirst();
            	String game_version = cursor.getString(cursor.getColumnIndex(GameDBHelper.TotalGameTable.GAME_VERSION));
            	// 版本不一致就需要更新数据
            	if (!game_version.equals(gameinfo.getGame_version())) {
            		ContentValues cv = new ContentValues();
    		    	cv.put(TotalGameTable.OBJECT_ID, gameinfo.getObject_id());
    		    	cv.put(TotalGameTable.GAME_NAME, gameinfo.getGame_name());
    		    	cv.put(TotalGameTable.PACKAGE_NAME, gameinfo.getPackage_name());
    		    	cv.put(TotalGameTable.GAME_ICON, gameinfo.getGame_icon());
    		    	cv.put(TotalGameTable.GAME_RULER, gameinfo.getGame_rule());
    		    	cv.put(TotalGameTable.GAME_WIN_METHOD, gameinfo.getGame_win_method());
    		    	
    		    	int gameStatus = gameinfo.getGame_status();
    		    	if (gameStatus == 2) {
						gameStatus = 3;
					}
    		    	
    		    	cv.put(TotalGameTable.GAME_STATUS, gameStatus);
    		    	cv.put(TotalGameTable.GAME_VERSION, gameinfo.getGame_version());
    		    	
    		    	dbHelper.update(GameDBHelper.TABLE_NAME, cv, where, null);
    		    	
    		    	Log.i("GGGGGGGGGGGGGGGGGGG", " 需要更新:" + gameinfo.getGame_name());
				}
            	else {
            		Log.i("GGGGGGGGGGGGGGGGGGG", " 不需要更新:" + gameinfo.getGame_name());
				}
			}else {  // 没有这条纪录，就插入纪录
            	
            	Log.i("TTTTTTTTTTTTTTTTTTT", " 正在插入:" + gameinfo.getGame_name());
            	ContentValues cv = new ContentValues();
		    	cv.put(TotalGameTable.OBJECT_ID, gameinfo.getObject_id());
		    	cv.put(TotalGameTable.GAME_NAME, gameinfo.getGame_name());
		    	cv.put(TotalGameTable.PACKAGE_NAME, gameinfo.getPackage_name());
		    	cv.put(TotalGameTable.GAME_ICON, gameinfo.getGame_icon());
		    	cv.put(TotalGameTable.GAME_RULER, gameinfo.getGame_rule());
		    	cv.put(TotalGameTable.GAME_WIN_METHOD, gameinfo.getGame_win_method());
		    	
		    	int gameStatus = gameinfo.getGame_status();
		    	
		    	cv.put(TotalGameTable.GAME_STATUS, gameStatus);
		    	cv.put(TotalGameTable.GAME_VERSION, gameinfo.getGame_version());
		    	
		    	dbHelper.insert(GameDBHelper.TABLE_NAME, null, cv);
			}
            
            if(cursor != null) {
	            cursor.close();
	            dbHelper.close();
	        }
	    	
		}
	    
	    public void updateGameInfo(GameInfo gameInfo) {

	    	Cursor cursor = null;
	    	
	    	// 先查询看数据库里面有没有这条纪录
	    	String where = GameDBHelper.TotalGameTable.OBJECT_ID + " = '" + gameInfo.getObject_id() + "'";
	    	
            cursor = dbHelper.query(GameDBHelper.TABLE_NAME, null, where, null, null, null, null);
            
            // 有这条纪录
            if (cursor != null && cursor.getCount() > 0) {
				// 看版本号是否一致，不一致就更新版本号和游戏状态
            	cursor.moveToFirst();
            	int game_status = cursor.getInt(cursor.getColumnIndex(GameDBHelper.TotalGameTable.GAME_STATUS));
            	// 版本不一致就需要更新数据
            	if (game_status != gameInfo.getGame_status()) {
            		
            		ContentValues cv = new ContentValues();
    		    	cv.put(TotalGameTable.OBJECT_ID, gameInfo.getObject_id());
    		    	cv.put(TotalGameTable.GAME_NAME, gameInfo.getGame_name());
    		    	cv.put(TotalGameTable.PACKAGE_NAME, gameInfo.getPackage_name());
    		    	cv.put(TotalGameTable.GAME_ICON, gameInfo.getGame_icon());
    		    	cv.put(TotalGameTable.GAME_RULER, gameInfo.getGame_rule());
    		    	cv.put(TotalGameTable.GAME_WIN_METHOD, gameInfo.getGame_win_method());
    		    	
    		    	int gameStatus = gameInfo.getGame_status();
    		    	
    		    	cv.put(TotalGameTable.GAME_STATUS, gameStatus);
    		    	cv.put(TotalGameTable.GAME_VERSION, gameInfo.getGame_version());
    		    	
    		    	dbHelper.update(GameDBHelper.TABLE_NAME, cv, where, null);
				}
			}
//            // 没有这条纪录，就插入纪录
//            else {
//            	
//            	ContentValues cv = new ContentValues();
//		    	cv.put(TotalGameTable.OBJECT_ID, gameInfo.getObject_id());
//		    	cv.put(TotalGameTable.GAME_NAME, gameInfo.getGame_name());
//		    	cv.put(TotalGameTable.PACKAGE_NAME, gameInfo.getPackage_name());
//		    	cv.put(TotalGameTable.GAME_ICON, gameInfo.getGame_icon());
//		    	cv.put(TotalGameTable.GAME_RULER, gameInfo.getGame_rule());
//		    	cv.put(TotalGameTable.GAME_WIN_METHOD, gameInfo.getGame_win_method());
//		    	
//		    	int gameStatus = gameInfo.getGame_status();
//		    	
//		    	cv.put(TotalGameTable.GAME_STATUS, gameStatus);
//		    	cv.put(TotalGameTable.GAME_VERSION, gameInfo.getGame_version());
//		    	
//		    	dbHelper.insert(GameDBHelper.TABLE_NAME, null, cv);
//			}
            
            if(cursor != null) {
	            cursor.close();
	            dbHelper.close();
	        }
		}
	    
	    public void queryAllGame() {
	    	Cursor cursor = null;
	    	
            cursor = dbHelper.query(GameDBHelper.TABLE_NAME, null, null, null, null, null, null);
            
            if (cursor != null && cursor.getCount() > 0) {
            	
            	cursor.moveToFirst();
            	
            	do {
            		
                	GameInfo gameInfo = new GameInfo();
                	gameInfo.setId(cursor.getInt(cursor.getColumnIndex(TotalGameTable._ID)));
                	gameInfo.setGame_icon(cursor.getString(cursor.getColumnIndex(TotalGameTable.GAME_ICON)));
                	gameInfo.setGame_name(cursor.getString(cursor.getColumnIndex(TotalGameTable.GAME_NAME)));
                	gameInfo.setGame_rule(cursor.getString(cursor.getColumnIndex(TotalGameTable.GAME_RULER)));
                	gameInfo.setGame_status(cursor.getInt(cursor.getColumnIndex(TotalGameTable.GAME_STATUS)));
                	gameInfo.setGame_version(cursor.getString(cursor.getColumnIndex(TotalGameTable.GAME_VERSION)));
                	gameInfo.setGame_win_method(cursor.getString(cursor.getColumnIndex(TotalGameTable.GAME_WIN_METHOD)));
                	gameInfo.setObject_id(cursor.getString(cursor.getColumnIndex(TotalGameTable.OBJECT_ID)));
                	gameInfo.setPackage_name(cursor.getString(cursor.getColumnIndex(TotalGameTable.PACKAGE_NAME)));
                	
                	CustomApplcation.gameList.add(gameInfo);
            		
				} while (cursor.moveToNext());
			}
		}
	    
	    
	    public void deleteTable() {
	    	Cursor cursor = null;
	    	
            cursor = dbHelper.query(GameDBHelper.TABLE_NAME, null, null, null, null, null, null);
            
            dbHelper.delete(GameDBHelper.TABLE_NAME, null, null);
		}
}
