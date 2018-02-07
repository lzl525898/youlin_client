package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import u.aly.cu;

import com.nfs.youlin.entity.NeighborGroup;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NeighborGroupDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;

	private final String INSERT_SQL = "insert into " + NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR_GROUP+"("
			  +"neighbor_group_id,neighbor_group_name,neighbor_group_avatar,neighbor_group_creater_id,neighbor_group_creater_family_id," 
			  +"neighbor_group_type,neighbor_group_description,neighbor_group_member_count,neighbor_group_private_flag,neighbor_group_add_type,"
			  +"neighbor_group_display_mode,neighbor_group_follow_flag,neighbor_group_shield_flag,neighbor_group_bg_color,neighbor_group_create_time,"
			  +"community_id,postPrivilege,deleteable,belong_family_id,user_display_name,ne_display_name,subscribed_status,key_words,visible_scope,"
			  +"category_name,category_id,manager_flag,login_account,table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private final String SELECT_ALL_OBJ = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR_GROUP
			  +" GROUP BY community_id";
	
	public NeighborGroupDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}

	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		List<Object> groupLists = null;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_ALL_OBJ, new String[]{});
			if(cursor!= null){
				groupLists = new ArrayList<Object>();
			}
			while(cursor.moveToNext()){
				NeighborGroup group = new NeighborGroup(this.context);
				group.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				group.setNeighbor_group_id(cursor.getLong(cursor.getColumnIndex("neighbor_group_id")));
				group.setNeighbor_group_name(cursor.getString(cursor.getColumnIndex("neighbor_group_name")));
				group.setNeighbor_group_avatar(cursor.getString(cursor.getColumnIndex("neighbor_group_avatar")));
				group.setNeighbor_group_creater_id(cursor.getLong(cursor.getColumnIndex("neighbor_group_creater_id")));
				group.setNeighbor_group_creater_family_id(cursor.getLong(cursor.getColumnIndex("neighbor_group_creater_family_id")));
				group.setNeighbor_group_type(cursor.getInt(cursor.getColumnIndex("neighbor_group_type")));
				group.setNeighbor_group_description(cursor.getString(cursor.getColumnIndex("neighbor_group_description")));
				group.setNeighbor_group_member_count(cursor.getInt(cursor.getColumnIndex("neighbor_group_member_count")));
				group.setNeighbor_group_private_flag(cursor.getInt(cursor.getColumnIndex("neighbor_group_private_flag")));
				group.setNeighbor_group_add_type(cursor.getInt(cursor.getColumnIndex("neighbor_group_add_type")));
				group.setNeighbor_group_display_mode(cursor.getInt(cursor.getColumnIndex("neighbor_group_display_mode")));
				group.setNeighbor_group_follow_flag(cursor.getInt(cursor.getColumnIndex("neighbor_group_follow_flag")));
				group.setNeighbor_group_shield_flag(cursor.getInt(cursor.getColumnIndex("neighbor_group_shield_flag")));
				group.setNeighbor_group_bg_color(cursor.getInt(cursor.getColumnIndex("neighbor_group_bg_color")));
				group.setNeighbor_group_create_time(cursor.getLong(cursor.getColumnIndex("neighbor_group_create_time")));
				group.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				group.setPostPrivilege(cursor.getInt(cursor.getColumnIndex("postPrivilege")));
				group.setDeleteable(cursor.getInt(cursor.getColumnIndex("deleteable")));
				group.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				group.setUser_display_name(cursor.getString(cursor.getColumnIndex("user_display_name")));
				group.setNe_display_name(cursor.getString(cursor.getColumnIndex("ne_display_name")));
				group.setSubscribed_status(cursor.getInt(cursor.getColumnIndex("subscribed_status")));
				group.setKey_words(cursor.getString(cursor.getColumnIndex("key_words")));
				group.setVisible_scope(cursor.getInt(cursor.getColumnIndex("visible_scope")));
				group.setCategory_name(cursor.getString(cursor.getColumnIndex("category_name")));
				group.setCategory_id(cursor.getInt(cursor.getColumnIndex("category_id")));
				group.setManager_flag(cursor.getInt(cursor.getColumnIndex("manager_flag")));
				group.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				groupLists.add(group);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborGroupDaoDBImpl findAllObject:"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor !=null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return groupLists;
	}

	public NeighborGroup findTargetObject(long neighbor_group_id) {
		NeighborGroup group = null;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_ALL_OBJ, new String[]{});
			while(cursor.moveToNext()){
				group = new NeighborGroup(this.context);
				group.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				group.setNeighbor_group_id(cursor.getLong(cursor.getColumnIndex("neighbor_group_id")));
				group.setNeighbor_group_name(cursor.getString(cursor.getColumnIndex("neighbor_group_name")));
				group.setNeighbor_group_avatar(cursor.getString(cursor.getColumnIndex("neighbor_group_avatar")));
				group.setNeighbor_group_creater_id(cursor.getLong(cursor.getColumnIndex("neighbor_group_creater_id")));
				group.setNeighbor_group_creater_family_id(cursor.getLong(cursor.getColumnIndex("neighbor_group_creater_family_id")));
				group.setNeighbor_group_type(cursor.getInt(cursor.getColumnIndex("neighbor_group_type")));
				group.setNeighbor_group_description(cursor.getString(cursor.getColumnIndex("neighbor_group_description")));
				group.setNeighbor_group_member_count(cursor.getInt(cursor.getColumnIndex("neighbor_group_member_count")));
				group.setNeighbor_group_private_flag(cursor.getInt(cursor.getColumnIndex("neighbor_group_private_flag")));
				group.setNeighbor_group_add_type(cursor.getInt(cursor.getColumnIndex("neighbor_group_add_type")));
				group.setNeighbor_group_display_mode(cursor.getInt(cursor.getColumnIndex("neighbor_group_display_mode")));
				group.setNeighbor_group_follow_flag(cursor.getInt(cursor.getColumnIndex("neighbor_group_follow_flag")));
				group.setNeighbor_group_shield_flag(cursor.getInt(cursor.getColumnIndex("neighbor_group_shield_flag")));
				group.setNeighbor_group_bg_color(cursor.getInt(cursor.getColumnIndex("neighbor_group_bg_color")));
				group.setNeighbor_group_create_time(cursor.getLong(cursor.getColumnIndex("neighbor_group_create_time")));
				group.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				group.setPostPrivilege(cursor.getInt(cursor.getColumnIndex("postPrivilege")));
				group.setDeleteable(cursor.getInt(cursor.getColumnIndex("deleteable")));
				group.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				group.setUser_display_name(cursor.getString(cursor.getColumnIndex("user_display_name")));
				group.setNe_display_name(cursor.getString(cursor.getColumnIndex("ne_display_name")));
				group.setSubscribed_status(cursor.getInt(cursor.getColumnIndex("subscribed_status")));
				group.setKey_words(cursor.getString(cursor.getColumnIndex("key_words")));
				group.setVisible_scope(cursor.getInt(cursor.getColumnIndex("visible_scope")));
				group.setCategory_name(cursor.getString(cursor.getColumnIndex("category_name")));
				group.setCategory_id(cursor.getInt(cursor.getColumnIndex("category_id")));
				group.setManager_flag(cursor.getInt(cursor.getColumnIndex("manager_flag")));
				group.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborGroupDaoDBImpl findAllObject:"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor !=null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return group;
	}
	
	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		NeighborGroup neighborGroup = (NeighborGroup) obj;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{neighborGroup.getNeighbor_group_id(),
													 neighborGroup.getNeighbor_group_name(),
													 neighborGroup.getNeighbor_group_avatar(),
													 neighborGroup.getNeighbor_group_creater_id(),
													 neighborGroup.getNeighbor_group_creater_family_id(),
													 neighborGroup.getNeighbor_group_type(),
													 neighborGroup.getNeighbor_group_description(),
													 neighborGroup.getNeighbor_group_member_count(),
													 neighborGroup.getNeighbor_group_private_flag(),
													 neighborGroup.getNeighbor_group_add_type(),
													 neighborGroup.getNeighbor_group_display_mode(),
													 neighborGroup.getNeighbor_group_follow_flag(),
													 neighborGroup.getNeighbor_group_shield_flag(),
													 neighborGroup.getNeighbor_group_bg_color(),
													 neighborGroup.getNeighbor_group_create_time(),
													 neighborGroup.getCommunity_id(),
													 neighborGroup.getPostPrivilege(),
													 neighborGroup.getDeleteable(),
													 neighborGroup.getBelong_family_id(),
													 neighborGroup.getUser_display_name(),
													 neighborGroup.getNe_display_name(),
													 neighborGroup.getSubscribed_status(),
													 neighborGroup.getKey_words(),
													 neighborGroup.getVisible_scope(),
													 neighborGroup.getCategory_name(),
													 neighborGroup.getCategory_id(),
													 neighborGroup.getManager_flag(),
													 neighborGroup.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "NeighborGroupDaoDBImpl saveObject:"+e.getMessage());
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
		if(this.db != null){
			this.db.close();
			this.db = null;
		}
	}

	@Override
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}
}
