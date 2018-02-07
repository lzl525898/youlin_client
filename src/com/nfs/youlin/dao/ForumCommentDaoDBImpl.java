package com.nfs.youlin.dao;

import java.util.List;

import com.nfs.youlin.entity.ForumComment;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ForumCommentDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
//	cache_key integer, 
//	comment_id bigint, 
//	topic_id bigint, 
//	sender_nc_role_id integer, 
//	sender_id bigint, 
//	sender_name text, 
//	sender_portrait text, 
//	sender_family_id bigint, 
//	sender_family_address text, 
//	sender_level text, 
//	display_name text, 
//	comment_content text, 
//	comment_time bigint, 
//	comment_image_url text, 
//	comment_content_type integer default 0, 
//	send_status integer default 0, 
//	read_status integer default 0, 
//	login_account bigint, 
//	media_type integer default 0, 
//	media_url text,
	private final String INSERT_SQL = "insert into " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_COMMENT + "("
			+ "cache_key,comment_id,topic_id,sender_nc_role_id,sender_id,sender_name,sender_portrait,"
			+ "sender_family_id,sender_family_address,sender_level,display_name,comment_content,comment_time,"
			+ "comment_image_url,comment_content_type,send_status,read_status,login_account,media_type,media_url,"
			+ "table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public ForumCommentDaoDBImpl(Context context){
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
		ForumComment forumComment = (ForumComment)obj;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{forumComment.getCache_key(),
													 forumComment.getComment_id(),
													 forumComment.getTopic_id(),
													 forumComment.getSender_nc_role_id(),
													 forumComment.getSender_id(),
													 forumComment.getSender_name(),
													 forumComment.getSender_portrait(),
													 forumComment.getSender_family_id(),
													 forumComment.getSender_family_address(),
													 forumComment.getSender_level(),
													 forumComment.getDisplay_name(),
													 forumComment.getComment_content(),
													 forumComment.getComment_time(),
													 forumComment.getComment_image_url(),
													 forumComment.getComment_content_type(),
													 forumComment.getSend_status(),
													 forumComment.getRead_status(),
													 forumComment.getLogin_account(),
													 forumComment.getMedia_type(),
													 forumComment.getMedia_url(),
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
