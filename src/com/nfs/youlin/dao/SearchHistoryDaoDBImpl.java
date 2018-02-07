package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chatuidemo.dbadapter;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.SearchHistory;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SearchHistoryDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
//	search_type integer, 
//	search_object_id bigint, 
//	search_word text, 
//	search_time bigint, 
//	login_account bigint, 
//	table_version integer
	private final String INSERT_SQL = "insert into "+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+ "("
			+ "search_type,search_object_id,search_word,search_time,login_account,table_version) values(?,?,?,?,?,?)";
	
	public SearchHistoryDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
//    //---打开数据库--- 
//    public SearchHistoryDaoDBImpl open() throws SQLException{
//        db = this.dbOpenHelper.getReadableDatabase();
//        return this;
//    }
//    
//    //---关闭数据库---
//    public void close() {  
//    	this.dbOpenHelper.close(); 
//    }+" where"+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+".[search_type]="+searchtype
	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		List< Object> Search_List = new ArrayList<Object>();
		
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		Cursor cursor = db.rawQuery("select * from "+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+" ORDER BY _id DESC", null);
		if(cursor != null){
			while(cursor.moveToNext()){
				
				SearchHistory searchHistory = new SearchHistory(this.context);
				searchHistory.setSearch_item_id(cursor.getInt(cursor.getColumnIndex("_id")));
				searchHistory.setLogin_account(cursor.getInt(cursor.getColumnIndex("login_account")));
				searchHistory.setSearch_object_id(cursor.getInt(cursor.getColumnIndex("search_object_id")));
				searchHistory.setSearch_time(cursor.getInt(cursor.getColumnIndex("search_time")));
				searchHistory.setSearch_type(cursor.getInt(cursor.getColumnIndex("search_type")));
				searchHistory.setSearch_word(cursor.getString(cursor.getColumnIndex("search_word")));
				Search_List.add(searchHistory);
			}
		}
		this.db.setTransactionSuccessful();
		this.db.endTransaction();
		cursor.close();
		return Search_List;
	}

	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		SearchHistory searchHistory = (SearchHistory)obj;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{searchHistory.getSearch_type(),
													 searchHistory.getSearch_object_id(),
													 searchHistory.getSearch_word(),
													 searchHistory.getSearch_time(),
													 searchHistory.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "SearchHistoryDaoDBImpl->saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void deleteObject(int id) {
		// TODO Auto-generated method stub
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL("delete from "+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+" where _id = ?", new Object[]{id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "AllFamilyDaoDBImpl saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void modifyObject(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseDatabaseRes() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<Object> findPointTypeObject(int searchtype) {
		// TODO Auto-generated method stub
		List< Object> Search_List = new ArrayList<Object>();
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		Cursor cursor = db.rawQuery("select * from "+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+
				" where "+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+".[search_type]="+searchtype+" ORDER BY _id DESC", null);
		if(cursor != null){
			while(cursor.moveToNext()){
				
				SearchHistory searchHistory = new SearchHistory(this.context);
				searchHistory.setSearch_item_id(cursor.getInt(cursor.getColumnIndex("_id")));
				searchHistory.setLogin_account(cursor.getInt(cursor.getColumnIndex("login_account")));
				searchHistory.setSearch_object_id(cursor.getInt(cursor.getColumnIndex("search_object_id")));
				searchHistory.setSearch_time(cursor.getInt(cursor.getColumnIndex("search_time")));
				searchHistory.setSearch_type(cursor.getInt(cursor.getColumnIndex("search_type")));
				searchHistory.setSearch_word(cursor.getString(cursor.getColumnIndex("search_word")));
				Search_List.add(searchHistory);
			}
		}
		this.db.setTransactionSuccessful();
		this.db.endTransaction();
		cursor.close();
		return Search_List;
	}

}
