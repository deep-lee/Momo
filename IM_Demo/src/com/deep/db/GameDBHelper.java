package com.deep.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDBHelper extends SQLiteOpenHelper{
	
	public static final String DATA_BASE_NAME = "total_game_db";
	public static final int DATA_BASE_VERSION = 1;
	public static final String TABLE_NAME = "total_game";

	private SQLiteDatabase mDb;
	
	public GameDBHelper(Context context) {
		super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		onCreateFavTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	interface TotalGameTable{
		String _ID = "_id";
		String GAME_NAME = "game_name";
		String OBJECT_ID = "objectid";
		String PACKAGE_NAME = "package_name";
		String GAME_ICON = "game_icon";
		String GAME_RULER = "game_rule";
		String GAME_WIN_METHOD = "game_win_method";
		String GAME_STATUS = "game_status";
		String GAME_VERSION = "game_version";
		String GAME_NOTIFICATION_ID = "game_notification_id";
	}
	
	private void onCreateFavTable(SQLiteDatabase db){
		  StringBuilder favStr=new StringBuilder();
	      favStr.append("CREATE TABLE IF NOT EXISTS ")
	      		.append(GameDBHelper.TABLE_NAME)
	      		.append(" ( ").append(TotalGameTable._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
	      		.append(TotalGameTable.GAME_NAME).append(" varchar(100),")
	      		.append(TotalGameTable.OBJECT_ID).append(" varchar(50),")
	      		.append(TotalGameTable.PACKAGE_NAME).append(" varchar(100),")
	      		.append(TotalGameTable.GAME_ICON).append(" varchar(100),")
	      		.append(TotalGameTable.GAME_RULER).append(" varchar(100),")
	      		.append(TotalGameTable.GAME_WIN_METHOD).append(" varchar(100),")
	      		.append(TotalGameTable.GAME_VERSION).append(" varchar(20),")
	      		.append(TotalGameTable.GAME_NOTIFICATION_ID).append(" Integer,")
	      		.append(TotalGameTable.GAME_STATUS).append(" Integer);");
	            
	      db.execSQL(favStr.toString());
	}
	
	
    /**
     * 获取数据库操作对象
     * @param isWrite 是否可写
     * @return
     */
    public synchronized SQLiteDatabase getDatabase(boolean isWrite) {

        if(mDb == null || !mDb.isOpen()) {
            if(isWrite) {
                try {
                    mDb=getWritableDatabase();
                } catch(Exception e) {
                    // 当数据库不可写时
                    mDb=getReadableDatabase();
                    return mDb;
                }
            } else {
                mDb=getReadableDatabase();
            }
        }
        return mDb;
    }
    
    public int delete(String table, String whereClause, String[] whereArgs) {
        getDatabase(true);
        return mDb.delete(table, whereClause, whereArgs);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        getDatabase(true);
        return mDb.insertOrThrow(table, nullColumnHack, values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        getDatabase(true);
        return mDb.update(table, values, whereClause, whereArgs);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        getDatabase(false);
        return mDb.rawQuery(sql, selectionArgs);
    }

    public void execSQL(String sql) {
        getDatabase(true);
        mDb.execSQL(sql);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
    // final
        String orderBy) {
        getDatabase(false);
        return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

}
