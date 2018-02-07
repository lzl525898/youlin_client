package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.NeighborGroup;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.renderscript.Sampler.Value;
import android.util.Log;

public class NeighborChatDaoBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	
	private final String SELECT_ALL_NEIGHBOR_GROUP = "select * from " 
						+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR_GROUP
						+" group by neighbor_group_id";
	private final String SELECT_SPEC_GROUP_NEIGHBOR = "select * from "
						+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR
						+" where belong_family_id = ?";
	private final String SELECT_SPEC_NEIGHBOR = "select * from "+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR
						+" where ";
	
	public NeighborChatDaoBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	
	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Neighbor> findAllNeighborMembers(long belong_family_id){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		String strBFamilyId = String.valueOf(belong_family_id);
		this.db.beginTransaction();
		List<Neighbor> neighborsList = new ArrayList<Neighbor>();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_SPEC_GROUP_NEIGHBOR, new String[]{strBFamilyId});
			Neighbor neighbor = new Neighbor(context);
			neighbor.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
			neighbor.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
			neighbor.setUser_portrait(cursor.getString(cursor.getColumnIndex("user_portrait")));
			neighbor.setUser_phone_number(cursor.getString(cursor.getColumnIndex("user_phone_number")));
			neighbor.setDistance(cursor.getInt(cursor.getColumnIndex("distance")));
			neighbor.setBriefdesc(cursor.getString(cursor.getColumnIndex("briefdesc")));
			neighbor.setProfession(cursor.getString(cursor.getColumnIndex("profession")));
			neighbor.setAddrstatus(cursor.getString(cursor.getColumnIndex("addrstatus")));
			neighbor.setBuilding_num(cursor.getString(cursor.getColumnIndex("building_num")));
			neighbor.setAptnum(cursor.getString(cursor.getColumnIndex("aptnum")));
			neighbor.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
			neighbor.setData_type(cursor.getInt(cursor.getColumnIndex("data_type")));
			neighbor.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			neighborsList.add(neighbor);
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborChatDaoBImpl findAllNeighborMembers:"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return neighborsList;
	}
	
	public Neighbor findNeighborMember(long belong_family_id, long user_id){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		Neighbor neighbor = new Neighbor(context);
		String toUserId = String.valueOf(user_id);
		String toFamilyId = String.valueOf(belong_family_id);
		this.db.beginTransaction();
		Cursor cursor = null;
		
		return neighbor;
	}
	
	public List<NeighborGroup> findAllNeighborGroup(){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		List<NeighborGroup> groupsList = new ArrayList<NeighborGroup>();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_ALL_NEIGHBOR_GROUP, new String[]{});
			while(cursor.moveToNext()){
				NeighborGroup group = new NeighborGroup(context);
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
				group.setCommunity_id(cursor.getInt(cursor.getColumnIndex("community_id")));
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
				groupsList.add(group);
				this.db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborChatDaoBImpl findAllNeighborGroup:"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return groupsList;
	}

	
	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		
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
