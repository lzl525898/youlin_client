package com.nfs.youlin.dao;

import java.util.List;





import com.nfs.youlin.entity.ForumMedia;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ForumMediaDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	
	private final String INSERT_SQL = "insert into " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_MEDIA + "("
			+ "cache_key,object_id,object_main_id,object_type,media_type,media_url,media_file_key,"
			+ "title,description,link,send_status,login_account,table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
//	+ "cache_key integer, "
//	+ "object_id bigint, "
//	+ "object_main_id integer, "
//	+ "object_type integer, "
//	+ "media_type integer default 0, "
//	+ "media_url text, "
//	+ "media_file_key text, "
//	+ "title text, "
//	+ "description text, "
//	+ "link text, "
//	+ "send_status integer default 0, "
//	+ "login_account bigint, "
	
	public ForumMediaDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}

	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		ForumMedia forumMedia = (ForumMedia) obj;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{forumMedia.getCache_key(),
													 forumMedia.getObject_id(),
													 forumMedia.getObject_main_id(),
													 forumMedia.getObject_type(),
													 forumMedia.getMedia_type(),
													 forumMedia.getMedia_url(),
													 forumMedia.getMedia_file_key(),
													 forumMedia.getTitle(),
													 forumMedia.getDescription(),
													 forumMedia.getLink(),
													 forumMedia.getSend_status(),
													 forumMedia.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "ForumMedia->saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	@Override
	public void deleteObject(int id) {
		// TODO Auto-generated method stub
		
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
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}
}
