package com.nfs.youlin.dao;

import java.util.List;

import com.nfs.youlin.entity.AllNote;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AllNoteDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
//	note_id bigint, 
//	note_content_type integer default 0, 
//	note_content text, 
//	note_time timestamp, 
//	note_res_send_type integer default 0, 
//	note_send_status integer default 1, 
//	note_read_status integer default 0, 
//	note_is_local integer default 0, 
//	note_object_type integer, 
//	note_object_id bigint, 
//	note_sender_family_id integer, 
//	note_sender_id integer, 
//	belong_family_id bigint default 0, 
//	login_account bigint, 
	private final String INSERT_SQL = "insert into "+NeighborsDBOpenHelper.TABLE_NAME_ALL_NOTE + "("
			+ "note_id,note_content_type,note_content,note_time,note_res_send_type,note_send_status,"
			+ "note_read_status,note_is_local,note_object_type,note_object_id,note_sender_family_id,"
			+ "note_sender_id,belong_family_id,login_account,table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public AllNoteDaoDBImpl(Context context){
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
		AllNote allNote = (AllNote)obj;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{allNote.getNote_id(),
													 allNote.getNote_content_type(),
													 allNote.getNote_content(),
													 allNote.getNote_time(),
													 allNote.getNote_res_send_type(),
													 allNote.getNote_send_status(),
													 allNote.getNote_read_status(),
													 allNote.getNote_is_local(),
													 allNote.getNote_object_type(),
													 allNote.getNote_object_id(),
													 allNote.getNote_sender_family_id(),
													 allNote.getNote_sender_id(),
													 allNote.getBelong_family_id(),
													 allNote.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "AllFamilyDaoDBImpl saveObject:"+e.getMessage());
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
