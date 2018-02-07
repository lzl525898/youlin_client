package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.NeighborDoor;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DoorplateDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	String SELECT_NEIGHBOR_DOOR = "select * from "+ 
			NeighborsDBOpenHelper.TABLE_DOOR_PLATE+" where belong_community_id = ? and login_account = ?";
	String SAVE_NEIGHBOR_DOOR = "insert into "+NeighborsDBOpenHelper.TABLE_DOOR_PLATE+
			"(doorplate_id, apt_number, displayname, avatarpath, user_count, "
			+ "livingStatus, belong_community_id, login_account, table_version)"
			+ " values(?,?,?,?,?,?,?,?,?)";
	String DELETE_ALL_DOOR_BYUSER = "delete * from "+NeighborsDBOpenHelper.TABLE_DOOR_PLATE+" where login_account = ?";
			
	public DoorplateDaoDBImpl(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		return null;
	}
	public List<Object> findObjectsBycommunity(Long community_id,Long user_id) {
		// TODO Auto-generated method stub
		List<Object> doorList = new ArrayList<Object>();
		String searchtostring = String.valueOf(community_id);
		String useridtostring = String.valueOf(user_id);
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_NEIGHBOR_DOOR, new String[]{searchtostring,useridtostring});
			while(cursor.moveToNext()){
				NeighborDoor door = new NeighborDoor(this.context);
				door.set_doorplate_id(cursor.getLong(cursor.getColumnIndex("doorplate_id")));
				door.set_apt_number(cursor.getInt(cursor.getColumnIndex("apt_number")));
				door.set_diaplay_name(cursor.getString(cursor.getColumnIndex("displayname")));
				door.set_avatarpath(cursor.getString(cursor.getColumnIndex("avatarpath")));
				door.set_user_count(cursor.getInt(cursor.getColumnIndex("user_count")));
				door.set_living_Status(cursor.getInt(cursor.getColumnIndex("livingStatus")));
				door.set_belong_community_id(cursor.getLong(cursor.getColumnIndex("belong_community_id")));
				door.set_login_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				Loger.d("TEST",""+door.get_diaplay_name());
				doorList.add(door);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "DoorplateDaoDBImpl findAllNeighborMembers:"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return doorList;
	}

	@Override
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		NeighborDoor door = (NeighborDoor) obj;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		try{
			db.execSQL(SAVE_NEIGHBOR_DOOR, new Object[]{
					door.get_doorplate_id(),
					door.get_apt_number(),
					door.get_diaplay_name(),
					door.get_avatarpath(),
					door.get_user_count(),
					door.get_living_Status(),
					door.get_belong_community_id(),
					door.get_login_account(),
					NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "DoorplateDaoDBImpl saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void deleteObject(int id) {
		// TODO Auto-generated method stub

	}
	public void deleteObjectbyuserid(Long login_account) {
		// TODO Auto-generated method stub
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_ALL_DOOR_BYUSER, new Object[]{login_account});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "DoorplateDaoDBImpl deleteObject:"+e.getMessage());
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
		if(this.db != null){
			this.db.close();
			this.db = null;
		}
	}

}
