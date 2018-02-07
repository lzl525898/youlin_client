package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class AllFamilyDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	
	private final String INSERT_SQL = "insert into "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+"(family_id,family_name,family_display_name,family_address," 
			 +"family_address_id,family_desc,family_portrait,family_background_color,family_city,family_city_id,family_city_code,family_apt_id,family_building_id,family_block,"
			 +"family_block_id,family_community_id,family_community,family_community_nickname,family_building_num,family_apt_num,"
			 +"is_family_member,is_attention,family_member_count,entity_type,ne_status,nem_status,primary_flag,belong_family_id,"
			 +"user_alias,user_avatar,login_account,table_version) values"
			 +"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private final String GET_CUR_ADDR_COUNT = "select count(*) as addr_count from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where primary_flag = 1";
	private final String SELECT_CURRENT_BYFD = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+ " where family_id = ?";
	private final String SELECT_CURRENT_PRIMARY_ADDR = "select * from " + NeighborsDBOpenHelper.TABLE_ALL_FAMILY + " where primary_flag = 1";
	private final String SELECT_CURRENT_TYPE = "select entity_type from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+ " where primary_flag = 1";
    private final String SELECT_CURRENT_ADDR = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+ " where primary_flag = ?";
	private final String DELETE_ALL_FAMILY_BYID = "delete from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where family_id = ? and ne_status = ?";
	private final String DELETE_FAMILY_BY_FR_ID = "delete from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where family_address_id = ?";
	private final String DELETE_ALL_FAMILY = "delete from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY; 
	private final String SELECT_VERIFY_FAMILY_OBJ_BY_ID = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" " +
	                     "where family_id = ? and login_account = ?";
	private final String SELECT_VERIFY_FAMILY_OBJ_BY_NE = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" " +
            			 "where ne_status = ? and login_account = ?";
	private final String SELECT_SIGNAL_FAMILY_OBJ_0  = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" " +
						 "where is_family_member = 0 and family_id = ? and ne_status = ? and login_account = ?";
	private final String SELECT_SIGNAL_FAMILY_OBJ_1  = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" " +
			 			 "where is_family_member = 1 and family_id = ? and login_account = ? group by family_id";
	private final String SELECT_COMMUNITY_NAME_BYID = "select family_community from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where family_community_id = ? group by family_community_id";
	private final String SELECT_ALL_FAMILY_OBJ = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where is_family_member == 0 order by _id"; 
	private final String SELECT_POINT_FAMILY_OBJ = "select * from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where login_account = ? order by _id";
	private final String MODIFY_SQL = "update "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" set "
			+"family_id = ?, " 
			+"family_name = ?, " 
			+"family_display_name = ?," 
			+"family_address = ?," 
			+"family_address_id = ?," 
			+"family_desc = ?,"
			+"family_portrait = ?,"
			+"family_background_color = ?,"
			+"family_city = ?, "
			+"family_city_id = ?, "
			+"family_city_code = ?, "
			+"family_apt_id = ?, "
			+"family_building_id = ?, "
			+"family_block = ?, "
			+"family_block_id = ?,"
			+"family_community = ?,"
			+"family_community_id = ?,"
			+"family_community_nickname = ?,"
			+"family_building_num = ?,"
			+"family_apt_num = ?,"
			+"is_family_member = ?,"
			+"is_attention = ?, "
			+"family_member_count = ?, "
			+"entity_type = ?, "
			+"ne_status = ?,"
			+"nem_status = ?,"
			+"primary_flag = ?,"
			+"belong_family_id = ?,"
			+"user_alias = ?,"
			+"user_avatar = ?,"
			+"login_account = ?,"
			+"table_version = ? "
			+"where family_address = ?";
	
	public AllFamilyDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	
	//返回当前数据库中地址个数
	public boolean isCurrentAddrExist(){
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
		int nCount = 0;
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(GET_CUR_ADDR_COUNT, null);
			while(cursor.moveToNext()){
				nCount = cursor.getInt(cursor.getColumnIndex("addr_count"));
			}
			this.db.setTransactionSuccessful();
			Loger.i("TEST", "当前数据库中的已验证地址个数为==>"+nCount);
		} catch (Exception e) {
			nCount = 0;
			Loger.i("TEST", "getAllFamilyID=>"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		if(nCount>0){
			return true;
		}else{
			return false;
		}
	}
	
	
	//返回family_id的列表
	//获取已经创建的家庭的family_id列表
	public List<Long> getAllFamilyID(){
		List<Long> familyIdList = new ArrayList<Long>();
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
		String SQL_QT = "select family_id from "+NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" where is_family_member = 0 group by is_family_member";
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SQL_QT, null);
			while(cursor.moveToNext()){
				familyIdList.add(cursor.getLong(cursor.getColumnIndex("family_id")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "getAllFamilyID=>"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return familyIdList;
	}
	
	//返回需要审核的地址详细信息
	public AllFamily findVerifyObject(int nType, String inString, String login){
		AllFamily allFamily = new AllFamily(context);
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
			if(0==nType){
				cursor = this.db.rawQuery(SELECT_VERIFY_FAMILY_OBJ_BY_ID, new String[]{inString,login});
			}else{
				cursor = this.db.rawQuery(SELECT_VERIFY_FAMILY_OBJ_BY_NE, new String[]{inString,login});
			}
			while(cursor.moveToNext()){
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Loger.i("TEST", "AllFamilyDao->findVerifyObject"+e.getMessage());
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return allFamily;
	}
	
	
	//返回AllFamily对象
	//获取对应 family_id 并且 家庭成员为0的家庭信息
	public AllFamily findDesignObject_0(long family_id,int family_sub_id,long user_id){
		AllFamily allFamily = new AllFamily(context);
		String toString_ID = String.valueOf(family_id);
		String toString_USERID = String.valueOf(user_id);
		String toString_SUBID = String.valueOf(family_sub_id);
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
			cursor = this.db.rawQuery(SELECT_SIGNAL_FAMILY_OBJ_0, new String[]{toString_ID,toString_SUBID,toString_USERID});
			while(cursor.moveToNext()){
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Loger.i("TEST", "AllFamilyDao->findAppointObject"+e.getMessage());
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return allFamily;
	}
	//返回AllFamily对象
	//获取对应 family_id 并且 家庭成员不为0的家庭信息
	public AllFamily findDesignObject_1(long family_id,long user_id){
		AllFamily allFamily = new AllFamily(context);
		String toString_ID = String.valueOf(family_id);
		String toString_USERID = String.valueOf(user_id);
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
			cursor = this.db.rawQuery(SELECT_SIGNAL_FAMILY_OBJ_1, new String[]{toString_ID,toString_USERID});
			while(cursor.moveToNext()){
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Loger.i("TEST", "AllFamilyDao->findAppointObject"+e.getMessage());
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return allFamily;
	}
	
	public int getCurrentEntyType(){
		int type = 0;
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
			cursor = this.db.rawQuery(SELECT_CURRENT_TYPE, new String[]{});
			while(cursor.moveToNext()){
				type = cursor.getInt(cursor.getColumnIndex("entity_type"));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "AllFamilyDao->findCurrent"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return type;
	}
	
	public AllFamily getCurrentAddrByFailyId(String familyId){
		AllFamily allFamily = null;
		if(familyId==null){
			return null;
		}
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
			allFamily = new AllFamily(context);
			cursor = this.db.rawQuery(SELECT_CURRENT_BYFD, new String[]{familyId});
			while(cursor.moveToNext()){
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "AllFamilyDao->findCurrent"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return allFamily;
	}
	
	//获取用户当前住址对象
	public AllFamily getUserCurrentAddrDetail(){
		AllFamily allFamily = null;
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
			allFamily = new AllFamily(context);
			cursor = this.db.rawQuery(SELECT_CURRENT_PRIMARY_ADDR, new String[]{});
			while(cursor.moveToNext()){
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "AllFamilyDao->getUserCurrentAddrDetail"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return allFamily;
	}
	
	//获取对应id的小区名称
	public String getCommunityNameById(String communityId){
		String communityName = null;
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
			cursor = this.db.rawQuery(SELECT_COMMUNITY_NAME_BYID, new String[]{communityId});
			while(cursor.moveToNext()){
				communityName = cursor.getString(cursor.getColumnIndex("family_community"));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "AllFamilyDao->getCommunityNameById"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			try {
				db.endTransaction();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return communityName;
	}
	
	//返回当前使用的地址信息
	public AllFamily getCurrentAddrDetail(String addrDetail){
		AllFamily allFamily = null;
		if(addrDetail==null){
			return null;
		}
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
			allFamily = new AllFamily(context);
			cursor = this.db.rawQuery(SELECT_CURRENT_ADDR, new String[]{"1"});
			while(cursor.moveToNext()){
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "AllFamilyDao->findCurrent"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			try {
				db.endTransaction();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return allFamily;
	}
	
	//返回AllFamily对象的列表
	//获取所有家庭成员不为0的家庭信息对象
	@Override
	public List<Object> findAllObject() {
		List<Object> listAllFamily = new ArrayList<Object>();
//		if(this.db == null){
//			this.db = this.dbOpenHelper.getReadableDatabase();
//		}	
//		
//		this.db.beginTransaction();
//		Cursor cursor = null;
//		try {
//			cursor = this.db.rawQuery(SELECT_ALL_FAMILY_OBJ, new String[]{});
//			while(cursor.moveToNext()){
//				AllFamily allFamily = new AllFamily(context);
//				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
//				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
//				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
//				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
//				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
//				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
//				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
//				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
//				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
//				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
//				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
//				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
//				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
//				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
//				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
//				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
//				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
//				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
//				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
//				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
//				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
//				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
//				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
//				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
//				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
//				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
//				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
//				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
//				listAllFamily.add(allFamily);
//			}
//			db.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace();
//			Loger.i("TEST", "AllFamilyDao->findAllObject"+e.getMessage());
//		}finally{
//			if(cursor != null){
//				cursor.close();
//				cursor = null;
//			}
//			db.endTransaction();
//		}
		return listAllFamily;
	}
	//保存家庭信息至数据库中
	//将地址信息存储在一个AllFamily对象中，最后存储到数据内
	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		AllFamily allFamily = (AllFamily) obj;
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
		Loger.d("test4", "saveObject getfamily_city_code="+allFamily.getfamily_city_code()+"apt_num_id="+allFamily.getapt_num_id()+"building_num_id="+allFamily.getbuilding_num_id());
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{allFamily.getFamily_id(),
													 allFamily.getFamily_name(),
													 allFamily.getFamily_display_name(),
													 allFamily.getFamily_address(),
													 allFamily.getFamily_address_id(),
													 allFamily.getFamily_desc(),
													 allFamily.getFamily_portrait(),
													 allFamily.getFamily_background_color(),
													 allFamily.getFamily_city(),
													 allFamily.getFamily_city_id(),
													 allFamily.getfamily_city_code(),
													 allFamily.getapt_num_id(),
													 allFamily.getbuilding_num_id(),
													 allFamily.getFamily_block(),
													 allFamily.getFamily_block_id(),
													 allFamily.getFamily_community_id(),
													 allFamily.getFamily_community(),
													 allFamily.getFamily_community_nickname(),
													 allFamily.getFamily_building_num(),
													 allFamily.getFamily_apt_num(),
													 allFamily.getIs_family_member(),
													 allFamily.getIs_attention(),
													 allFamily.getFamily_member_count(),
													 allFamily.getEntity_type(),
													 allFamily.getNe_status(),
													 allFamily.getNem_status(),
													 allFamily.getPrimary_flag(),
													 allFamily.getBelong_family_id(),
													 allFamily.getUser_alias(),
													 allFamily.getUser_avatar(),
													 allFamily.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "AllFamilyDaoDBImpl saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	//删除指定地址信息
	//从数据库中通过 family_id来删除对应的地址信息
	@Override
	public void deleteObject(int famliy_id) {
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
			this.db.execSQL(DELETE_ALL_FAMILY_BYID, new Object[]{famliy_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "AllFamilyDaoDBImpl saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteAllObject(){
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
			this.db.execSQL(DELETE_ALL_FAMILY, new Object[]{});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "AllFamilyDaoDBImpl delete all:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
//	public void deleteObject(long famliy_id,int family_sub_id) {
//		if(this.db == null){
//			this.db = this.dbOpenHelper.getReadableDatabase();
//		}	
//		this.db.beginTransaction();
//		try {
//			this.db.execSQL(DELETE_ALL_FAMILY_BYID, new Object[]{famliy_id,family_sub_id});
//			this.db.setTransactionSuccessful();
//		} catch (SQLException e) {
//			Loger.i("TEST", "AllFamilyDaoDBImpl saveObject:"+e.getMessage());
//			e.printStackTrace();
//		}finally{
//			db.endTransaction();
//		}
//	}
	
	public void deleteObjectByFamilyRecordId(long familyRecordId){
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
			this.db.execSQL(DELETE_FAMILY_BY_FR_ID, new Object[]{familyRecordId});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteObjectByFamilyRecordId:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	//修改指定地址信息
	//从数据库中通过 family_id来修改对应的地址信息，需要将修改的地址信息对象当做参数传入
	public void modifyObject(Object obj,String familyaddress) {
		AllFamily allFamily = (AllFamily) obj;
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
			this.db.execSQL(MODIFY_SQL, new Object[]{allFamily.getFamily_id(),
													 allFamily.getFamily_name(),
													 allFamily.getFamily_display_name(),
													 allFamily.getFamily_address(),
													 allFamily.getFamily_address_id(),
													 allFamily.getFamily_desc(),
													 allFamily.getFamily_portrait(),
													 allFamily.getFamily_background_color(),
													 allFamily.getFamily_city(),
													 allFamily.getFamily_city_id(),
													 allFamily.getfamily_city_code(),
													 allFamily.getapt_num_id(),
													 allFamily.getbuilding_num_id(),
													 allFamily.getFamily_block(),
													 allFamily.getFamily_block_id(),
													 allFamily.getFamily_community(),
													 allFamily.getFamily_community_id(),
													 allFamily.getFamily_community_nickname(),
													 allFamily.getFamily_building_num(),
													 allFamily.getFamily_apt_num(),
													 allFamily.getIs_family_member(),
													 allFamily.getIs_attention(),
													 allFamily.getFamily_member_count(),
													 allFamily.getEntity_type(),
													 allFamily.getNe_status(),
													 allFamily.getNem_status(),
													 allFamily.getPrimary_flag(),
													 allFamily.getBelong_family_id(),
													 allFamily.getUser_alias(),
													 allFamily.getUser_avatar(),
													 allFamily.getLogin_account(),
													 NeighborsDBOpenHelper.DB_VERSION,
													 familyaddress});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "AllFamilyDaoDBImpl modifyObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	//释放资源
	//当读取和写入操作切换时需要调用此函数
	@Override
	public void releaseDatabaseRes() {
		// TODO Auto-generated method stub
		if(this.db != null){
			this.db.close();
			this.db = null;
		}
	}
	//找到所有
	public List<Object> findPointTypeObject(long user_id) {
		// TODO Auto-generated method stub
		
		List<Object> listAllFamily = new ArrayList<Object>();
		String toString_ID = String.valueOf(user_id);
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
			cursor = this.db.rawQuery(SELECT_POINT_FAMILY_OBJ, new String[]{toString_ID});
			while(cursor.moveToNext()){
				AllFamily allFamily = new AllFamily(context);
				allFamily.setFamily_id(cursor.getLong(cursor.getColumnIndex("family_id")));
				allFamily.setFamily_name(cursor.getString(cursor.getColumnIndex("family_name")));
				allFamily.setFamily_display_name(cursor.getString(cursor.getColumnIndex("family_display_name")));
				allFamily.setFamily_address(cursor.getString(cursor.getColumnIndex("family_address")));
				allFamily.setFamily_address_id(cursor.getLong(cursor.getColumnIndex("family_address_id")));
				allFamily.setFamily_desc(cursor.getString(cursor.getColumnIndex("family_desc")));
				allFamily.setFamily_portrait(cursor.getString(cursor.getColumnIndex("family_portrait")));
				allFamily.setFamily_background_color(cursor.getInt(cursor.getColumnIndex("family_background_color")));
				allFamily.setFamily_city(cursor.getString(cursor.getColumnIndex("family_city")));
				allFamily.setFamily_city_id(cursor.getLong(cursor.getColumnIndex("family_city_id")));
				
				allFamily.setfamily_city_code(cursor.getString(cursor.getColumnIndex("family_city_code")));
				allFamily.setFamily_block(cursor.getString(cursor.getColumnIndex("family_block")));
				allFamily.setFamily_block_id(cursor.getLong(cursor.getColumnIndex("family_block_id")));
				allFamily.setFamily_community(cursor.getString(cursor.getColumnIndex("family_community")));
				allFamily.setFamily_community_id(cursor.getLong(cursor.getColumnIndex("family_community_id")));
				allFamily.setFamily_community_nickname(cursor.getString(cursor.getColumnIndex("family_community_nickname")));
				allFamily.setFamily_building_num(cursor.getString(cursor.getColumnIndex("family_building_num")));
				allFamily.setFamily_apt_num(cursor.getString(cursor.getColumnIndex("family_apt_num")));
				
				allFamily.setbuilding_num_id(cursor.getLong(cursor.getColumnIndex("family_building_id")));
				allFamily.setapt_num_id(cursor.getLong(cursor.getColumnIndex("family_apt_id")));
				
				allFamily.setIs_family_member(cursor.getInt(cursor.getColumnIndex("is_family_member")));
				allFamily.setIs_attention(cursor.getInt(cursor.getColumnIndex("is_attention")));
				allFamily.setFamily_member_count(cursor.getInt(cursor.getColumnIndex("family_member_count")));
				allFamily.setEntity_type(cursor.getInt(cursor.getColumnIndex("entity_type")));
				allFamily.setNe_status(cursor.getInt(cursor.getColumnIndex("ne_status")));
				allFamily.setNem_status(cursor.getInt(cursor.getColumnIndex("nem_status")));
				allFamily.setPrimary_flag(cursor.getLong(cursor.getColumnIndex("primary_flag")));
				allFamily.setBelong_family_id(cursor.getLong(cursor.getColumnIndex("belong_family_id")));
				allFamily.setUser_alias(cursor.getString(cursor.getColumnIndex("user_alias")));
				allFamily.setUser_avatar(cursor.getString(cursor.getColumnIndex("user_avatar")));
				allFamily.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				listAllFamily.add(allFamily);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			Loger.i("TEST", "AllFamilyDao->findAllObject"+e.getMessage());
		}finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return listAllFamily;
	}

	@Override
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void modifyObject(Object obj) {
		// TODO Auto-generated method stub
	}

}
