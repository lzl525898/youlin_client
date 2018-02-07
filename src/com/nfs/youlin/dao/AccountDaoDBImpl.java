package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nfs.youlin.entity.Account;
import com.nfs.youlin.utils.Loger;

public class AccountDaoDBImpl implements INeighborsDao {
	private NeighborsDBOpenHelper dbOpenHelper;
	
	private Context context;
	private SQLiteDatabase db;
	private final String INSERT_SQL = "insert into "+NeighborsDBOpenHelper.TABLE_NAME_USERS+"(user_public_status,user_vocation,user_level,user_id,user_name,user_portrait,user_gender," 
									  +"user_phone_number,user_family_id,user_family_address,user_birthday,"
									  +"user_email,login_account,user_type,user_json,user_time,table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private final String SELECT_USER_ID = "select user_id from "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" order by id asc limit 1";
	
	private final String SELECT_SINGLE_ACCOUNT = "select * from "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" where user_phone_number = ?";
	
	private final String SELECT_ACCOUNT_BYNICK = "select * from "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" where user_name = ? group by user_id order by id";
	
	private final String SELECT_ACCOUNT_BYID   = "select * from "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" where user_id = ? group by user_id order by id";
	
	private final String DELETE_ACCOUNT_BYID   = "delete from "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" where user_id = ?";
	
	private final String DELETE_ACCOUNT_BYPH   = "delete from "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" where user_phone_number = ?";
	
	private final String DELETE_ACCOUNT_ALL    = "delete from "+NeighborsDBOpenHelper.TABLE_NAME_USERS;
	
	private final String MODIFY_SQL = "update "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" set user_public_status = ?, "
														    +"user_vocation = ?, "
														    +"user_level = ?, "
															+"user_id = ?, "
															+"user_name = ?, "
															+"user_portrait = ?,"
															+"user_gender = ?,"
															+"user_family_id = ?,"
															+"user_family_address = ?,"
															+"user_birthday = ?,"
															+"user_email = ?,"
															+"login_account = ?,"
															+"user_type = ?,"
															+"user_json = ?,"
															+"user_time = ?,"
															+"table_version = ? "
															+"where login_account = ?";
	
	private List<String> getAllUserPhone(){
		List<String> userPhone = new ArrayList<String>();
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
		String SQL_QT = "select user_phone_number from "+NeighborsDBOpenHelper.TABLE_NAME_USERS;
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SQL_QT, null);
			while(cursor.moveToNext()){
				userPhone.add(cursor.getString(cursor.getColumnIndex("user_phone_number")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return userPhone;
	}
	
	public AccountDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	
	public long getUserId(){
		long userId = 0;
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
			cursor = this.db.rawQuery(SELECT_USER_ID, new String[]{});
			while(cursor.moveToNext()){
				userId = cursor.getLong(cursor.getColumnIndex("user_id"));
				this.db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			Loger.i("TEST", "getUserId:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return userId;
	}
	
	public Account findAccountByNickName(String nickName){
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
		Account account = null;
		Cursor cursor = null;
		try {
			account =new Account(context);
			cursor = this.db.rawQuery(SELECT_ACCOUNT_BYNICK, new String[]{(nickName)});
			while(cursor.moveToNext()){
				account.setId(cursor.getInt(cursor.getColumnIndex("id")));
				account.setUser_public_status(cursor.getInt(cursor.getColumnIndex("user_public_status")));
				account.setUser_vocation(cursor.getString(cursor.getColumnIndex("user_vocation")));
				account.setUser_level(cursor.getString(cursor.getColumnIndex("user_level")));
				account.setUser_gender(cursor.getInt(cursor.getColumnIndex("user_gender")));
				account.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				account.setUser_time(cursor.getLong(cursor.getColumnIndex("user_time")));
				account.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
				account.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				account.setUser_birthday(cursor.getLong(cursor.getColumnIndex("user_birthday")));
				account.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				account.setUser_email(cursor.getString(cursor.getColumnIndex("user_email")));
				account.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
				account.setUser_portrait(cursor.getString(cursor.getColumnIndex("user_portrait")));
				account.setUser_phone_number(cursor.getString(cursor.getColumnIndex("user_phone_number")));
				account.setUser_json(cursor.getString(cursor.getColumnIndex("user_json")));
				account.setUser_family_address(cursor.getString(cursor.getColumnIndex("user_family_address")));
				this.db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			Loger.i("TEST", "findAccountByNickName:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return account;
	}
	
	public Account findAccountByLoginID(String userLoginId){
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
		Account account = null; 
		Cursor cursor = null;
		try {
			account = new Account(context);
			cursor = this.db.rawQuery(SELECT_ACCOUNT_BYID, new String[]{(userLoginId)});
			while(cursor.moveToNext()){
				account.setId(cursor.getInt(cursor.getColumnIndex("id")));
				account.setUser_public_status(cursor.getInt(cursor.getColumnIndex("user_public_status")));
				account.setUser_vocation(cursor.getString(cursor.getColumnIndex("user_vocation")));
				account.setUser_level(cursor.getString(cursor.getColumnIndex("user_level")));
				account.setUser_gender(cursor.getInt(cursor.getColumnIndex("user_gender")));
				account.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				account.setUser_time(cursor.getLong(cursor.getColumnIndex("user_time")));
				account.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
				account.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				account.setUser_birthday(cursor.getLong(cursor.getColumnIndex("user_birthday")));
				account.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				account.setUser_email(cursor.getString(cursor.getColumnIndex("user_email")));
				account.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
				
				account.setUser_portrait(cursor.getString(cursor.getColumnIndex("user_portrait")));
				account.setUser_phone_number(cursor.getString(cursor.getColumnIndex("user_phone_number")));
				account.setUser_family_address(cursor.getString(cursor.getColumnIndex("user_family_address")));
				account.setUser_json(cursor.getString(cursor.getColumnIndex("user_json")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findAccountByLoginID:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return account;
	}
	
	public Account findAccountByPhone(String phoneNumber){
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
		Account account = null;
		Cursor cursor = null;
		try {
			account = new Account(context);
			cursor = this.db.rawQuery(SELECT_SINGLE_ACCOUNT, new String[]{(phoneNumber)});
			while(cursor.moveToNext()){
				account.setId(cursor.getInt(cursor.getColumnIndex("id")));
				account.setUser_public_status(cursor.getInt(cursor.getColumnIndex("user_public_status")));
				account.setUser_vocation(cursor.getString(cursor.getColumnIndex("user_vocation")));
				account.setUser_level(cursor.getString(cursor.getColumnIndex("user_level")));
				account.setUser_gender(cursor.getInt(cursor.getColumnIndex("user_gender")));
				account.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				account.setUser_time(cursor.getLong(cursor.getColumnIndex("user_time")));
				account.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
				account.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				account.setUser_birthday(cursor.getLong(cursor.getColumnIndex("user_birthday")));
				account.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				account.setUser_email(cursor.getString(cursor.getColumnIndex("user_email")));
				account.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
				account.setUser_portrait(cursor.getString(cursor.getColumnIndex("user_portrait")));
				account.setUser_phone_number(cursor.getString(cursor.getColumnIndex("user_phone_number")));
				account.setUser_json(cursor.getString(cursor.getColumnIndex("user_json")));
				account.setUser_family_address(cursor.getString(cursor.getColumnIndex("user_family_address")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findAccountByPhone:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return account;
	}

	@Override
	public List<Object> findAllObject() {
		List<Object> listAccount = new ArrayList<Object>();
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
		List<String> listPhone = getAllUserPhone();
		for(int i=0;i<listPhone.size();i++){
			Account account = new Account(context);
			Cursor cursor = null;
			try {
				cursor = this.db.rawQuery(SELECT_SINGLE_ACCOUNT, new String[]{(listPhone.get(i))});
				while(cursor.moveToNext()){
					account.setId(cursor.getInt(cursor.getColumnIndex("id")));
					account.setUser_public_status(cursor.getInt(cursor.getColumnIndex("user_public_status")));
					account.setUser_vocation(cursor.getString(cursor.getColumnIndex("user_vocation")));
					account.setUser_level(cursor.getString(cursor.getColumnIndex("user_level")));
					account.setUser_gender(cursor.getInt(cursor.getColumnIndex("user_gender")));
					account.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
					account.setUser_time(cursor.getLong(cursor.getColumnIndex("user_time")));
					account.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
					account.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
					account.setUser_birthday(cursor.getLong(cursor.getColumnIndex("user_birthday")));
					account.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
					account.setUser_email(cursor.getString(cursor.getColumnIndex("user_email")));
					account.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
					account.setUser_portrait(cursor.getString(cursor.getColumnIndex("user_portrait")));
					account.setUser_phone_number(cursor.getString(cursor.getColumnIndex("user_phone_number")));
					account.setUser_json(cursor.getString(cursor.getColumnIndex("user_json")));
					account.setUser_family_address(cursor.getString(cursor.getColumnIndex("user_family_address")));
				}
				listAccount.add(account);
				this.db.setTransactionSuccessful();
			} catch (Exception e) {
				Loger.i("TEST", "findAllObject:"+e.getMessage());
				e.printStackTrace();
			}finally{
				if(cursor!=null){
					cursor.close();
				}
				this.db.endTransaction();
			}
		}
		return listAccount;
	}
	
	public Account findDesignObject(String phone) {
		Account account = new Account(context);
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
			cursor = this.db.rawQuery(SELECT_SINGLE_ACCOUNT, new String[]{phone});
			while(cursor.moveToNext()){
				account.setUser_public_status(cursor.getInt(cursor.getColumnIndex("user_public_status")));
				account.setUser_vocation(cursor.getString(cursor.getColumnIndex("user_vocation")));
				account.setUser_level(cursor.getString(cursor.getColumnIndex("user_level")));
				account.setUser_gender(cursor.getInt(cursor.getColumnIndex("user_gender")));
				account.setUser_type(cursor.getInt(cursor.getColumnIndex("user_type")));
				account.setUser_time(cursor.getLong(cursor.getColumnIndex("user_time")));
				account.setUser_family_id(cursor.getLong(cursor.getColumnIndex("user_family_id")));
				account.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				account.setUser_birthday(cursor.getLong(cursor.getColumnIndex("user_birthday")));
				account.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				account.setUser_email(cursor.getString(cursor.getColumnIndex("user_email")));
				account.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
				account.setUser_portrait(cursor.getString(cursor.getColumnIndex("user_portrait")));
				account.setUser_phone_number(cursor.getString(cursor.getColumnIndex("user_phone_number")));
				account.setUser_json(cursor.getString(cursor.getColumnIndex("user_json")));
				account.setUser_family_address(cursor.getString(cursor.getColumnIndex("user_family_address")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findDesignObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return account;
	}

	@Override
	public void saveObject(Object obj) {
		Account account = (Account) obj;
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
			this.db.execSQL(INSERT_SQL, new Object[]{account.getUser_public_status(),
													 account.getUser_vocation(),
													 account.getUser_level(),
													 account.getUser_id(),
													 account.getUser_name(),
													 account.getUser_portrait(),
													 account.getUser_gender(),
													 account.getUser_phone_number(),
													 account.getUser_family_id(),
													 account.getUser_family_address(),
													 account.getUser_birthday(),
													 account.getUser_email(),
													 account.getLogin_account(),
													 account.getUser_type(),
													 account.getUser_json(),
													 account.getUser_time(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void deleteObject(int id) {
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
			this.db.execSQL(DELETE_ACCOUNT_BYID, new Object[]{id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteAllObjects(){
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
			this.db.execSQL(DELETE_ACCOUNT_ALL, new Object[]{});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteObject(String phone) {
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
			this.db.execSQL(DELETE_ACCOUNT_BYPH, new Object[]{phone});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	@Override
	public void modifyObject(Object obj) {
		Account account = (Account) obj;
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
			this.db.execSQL(MODIFY_SQL, new Object[]{account.getUser_public_status(),
													 account.getUser_vocation(),
													 account.getUser_level(),
													 account.getUser_id(),
													 account.getUser_name(),
													 account.getUser_portrait(),
													 account.getUser_gender(),
													 account.getUser_family_id(),
													 account.getUser_family_address(),
													 account.getUser_birthday(),
													 account.getUser_email(),
													 account.getLogin_account(),
													 account.getUser_type(),
													 account.getUser_json(),
													 account.getUser_time(),
													 NeighborsDBOpenHelper.DB_VERSION,
													 account.getLogin_account()});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "saveObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	@Override
	public void releaseDatabaseRes() {
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
