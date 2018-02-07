package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NeighborDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	
	private final String INSERT_SQL = "insert into " + NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+"("
			  +"user_id,user_name,user_family_id,user_portrait,user_phone_number,distance,briefdesc," 
			  +"profession,addrstatus,building_num,aptnum,belong_family_id,data_type,user_type,"
			  +"login_account,table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	// data_type = 3表示物业  data_type = 1，2  留用表示设置备注
	private final String SELECT_BELONG_FAMILY_ID_NEIGHBOR_OBJ = "select * from " + 
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR + " where belong_family_id = ? and user_id != ? group by user_id order by _id"; 
	private final String SELECT_BELONG_FAMILY_ID_AND_USERID_NEIGHBOR_OBJ = "select * from " + 
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR + " where user_id = ? group by user_family_id order by _id";
	private final String DELETE_NEIGHBOR_BYID = "delete from " + 
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR + " where user_id = ?";
	private final String DELETE_NEIGHBOR_BYFAMILYID = "delete from "+
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+" where belong_family_id = ?";
	private final String DELETE_NEIGHBOR_BYUSERID = "delete from "+
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+" where user_id = ?";
	private final String UPDATE_NEIGHBOR_BYUSERID = "update "+
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+" set user_name = ?"+ "where user_id = ?";
	private final String SET_NEIGHBOR_BRIEF = "update "+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+
			" set data_type=? briefdesc=? where user_id=?";
	private final String SET_NEIGHBOR_NAMEBRIEF = "update "+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+
			" set data_type=? user_name=? where user_id=?";
	// data_type = 3表示物业  data_type = 1，2  留用表示设置备注
	public NeighborDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	
	@Override
	public List<Object> findAllObject() {
		return null;
	}
	public void setneighbornameforbrief(int user_id,String brief){
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}	
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}
		Cursor cursor = null;
		String toString_User = String.valueOf(user_id);
		try {
			cursor = this.db.rawQuery(SET_NEIGHBOR_NAMEBRIEF, new String[]{"2",brief,toString_User});
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborDaoDBImpl->setneighborbrief"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}

	}
	public void setneighborbrief(long user_id,String brief){
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}	
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}
		Cursor cursor = null;
		String toString_User = String.valueOf(user_id);
		try {
			cursor = this.db.rawQuery(SET_NEIGHBOR_BRIEF, new String[]{"2",brief,toString_User});
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborDaoDBImpl->setneighborbrief"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}

	}
	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		Neighbor neighbor = (Neighbor) obj;
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}	
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{neighbor.getUser_id(),
													 neighbor.getUser_name(),
													 neighbor.getUser_family_id(),
													 neighbor.getUser_portrait(),
													 neighbor.getUser_phone_number(),
													 neighbor.getDistance(),
													 neighbor.getBriefdesc(),
													 neighbor.getProfession(),
													 neighbor.getAddrstatus(),
													 neighbor.getBuilding_num(),
													 neighbor.getAptnum(),
													 neighbor.getBelong_family_id(),
													 neighbor.getData_type(),
													 neighbor.getUser_type(),
													 neighbor.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborDaoDBImpl saveObject:"+e.getMessage());
			e.printStackTrace();
		} finally{
			db.endTransaction();
		}
	}

	@Override
	public void deleteObject(int family_id) {
		
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}	
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}
		try {
			this.db.execSQL(DELETE_NEIGHBOR_BYFAMILYID, new Object[]{family_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "NeighborDaoDBImpl deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteObject(long family_id) {
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_NEIGHBOR_BYFAMILYID, new Object[]{family_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "NeighborDaoDBImpl deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	public void deleteObjectByuserId(long user_id) {
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_NEIGHBOR_BYUSERID, new Object[]{user_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "NeighborDaoDBImpl deleteObject:"+e.getMessage());
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
	/*找到指定用户的某个邻居  根据邻居的user_id****************/
	public List<Object> findPointFamilyandUserObject(long user_id){
		List<Object> neighborsList = new ArrayList<Object>();
	//	String toString_ID = String.valueOf(belong_family_id);
		String toString_User = String.valueOf(user_id);
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}	
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_BELONG_FAMILY_ID_AND_USERID_NEIGHBOR_OBJ, new String[]{toString_User});
			while(cursor.moveToNext()){
				Neighbor neighbor = new Neighbor(context);
				neighbor.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				neighbor.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				neighbor.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
				neighbor.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
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
				neighbor.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				neighbor.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				neighborsList.add(neighbor);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborDaoDBImpl->findAppointObject"+e.getMessage());
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
	public List<Object> findPointFamilyObject(Long belong_family_id,Long user_id) {
		// TODO Auto-generated method stub
		List<Object> neighborsList = new ArrayList<Object>();
		String toString_ID = String.valueOf(belong_family_id);
		String toString_user = String.valueOf(user_id);
		String toString_family = String.valueOf(belong_family_id);
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}	
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_BELONG_FAMILY_ID_NEIGHBOR_OBJ, new String[]{toString_ID,toString_user});
			while(cursor.moveToNext()){
				Neighbor neighbor = new Neighbor(context);
				neighbor.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				neighbor.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				neighbor.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
				neighbor.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
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
				neighbor.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				neighbor.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				if(neighbor.getUser_type()!=4){
					neighborsList.add(neighbor);
				}
				Loger.i("NEW2", "11111111111111111111--->"+cursor.getString(cursor.getColumnIndex("user_name")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborDaoDBImpl->findAppointObject"+e.getMessage());
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
	@Override
	public List<Object> findPointTypeObject(int belong_family_id) {
		// TODO Auto-generated method stub
		List<Object> neighborsList = new ArrayList<Object>();
		String toString_ID = String.valueOf(belong_family_id);
		try {
			if(this.db == null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}	
			this.db.beginTransaction();
		} catch (Exception e1) {
			this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
			this.db = this.dbOpenHelper.getReadableDatabase();
			this.db.beginTransaction();
		}
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_BELONG_FAMILY_ID_NEIGHBOR_OBJ, new String[]{toString_ID});
			while(cursor.moveToNext()){
				Neighbor neighbor = new Neighbor(context);
				neighbor.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
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
				neighbor.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				neighbor.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				neighborsList.add(neighbor);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NeighborDaoDBImpl->findAppointObject"+e.getMessage());
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
	public void updateNeighborOne(String name,long user_id){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(UPDATE_NEIGHBOR_BYUSERID, new Object[]{name,user_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
}
