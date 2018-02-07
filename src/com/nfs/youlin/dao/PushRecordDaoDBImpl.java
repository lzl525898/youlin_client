package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import u.aly.cu;

public class PushRecordDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	
	private final String INSERT_SQL = "insert into "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD + "(user_id,type,content_type,record_id,content,click_url,"
			                        + "push_time,login_account,community_id,table_version) values(?,?,?,?,?,?,?,?,?,?)";
	private final String MODIFY_SQL = "update "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" "
								    +"set user_id = ?, "
								    +"type = ?, "
		    						+"content_type = ?, "
			                        +"record_id = ?, "
		    						+"content = ?, "
		    						+"click_url = ?, "
		    						+"push_time = ?, "
									+"login_account = ?,"
		    						+"community_id = ?,"
									+"table_version = ? "
									+"where _id = ?";
	private final String SELECT_PUSH_COMMENT = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where login_account = ? and user_id = ? group by _id order by _id desc";
	private final String SELECT_PUSH_NEWS = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where login_account = ? and user_id = ? group by _id order by _id desc";
	private final String SELECT_PUSH_ALL = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? group by _id order by _id desc";
	private final String SELECT_NORMAL_PUSH_ALL = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account in (?,?,?) group by _id order by _id desc";
	private final String SELECT_PROPERTY_PUSH_ALL = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account in (?,?) group by _id order by _id desc";
	private final String SELECT_ADMIN_PUSH_ALL = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account in (?,?,?,?) group by _id order by _id desc";
	private final String SELECT_REPORT_BYID = "select record_id,content,type,community_id from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account = 1001";
	private final String SELECT_COMMENT_BYID = "select record_id,content,type,community_id from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account = 1010";
	private final String SELECT_SAYHELLO_BYID = "select record_id,content,type,community_id from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account = ?";
	private final String SELECT_BY_RECORD_ID = "select * from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where record_id = ?";
	private final String DELETE_PUSH_BYID = "delete from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where record_id = ?";
	private final String DELETE_PUSH_ALL = "delete from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ?";
	private final String DELETE_NEWS_ALL = "delete from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" where user_id = ? and login_account = ?";
	private final String UPDATE_NEWS_STATUS = "update "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" set type = ? where user_id = ? and login_account = 2001";
	
	public PushRecordDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}

	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		int newsIndex = 0;
		boolean reportIndex = false;
		boolean replayCommIndex = false;
		JSONObject reportObject = null;
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
		List<Long> reportTopicId = null;
		List<Long> replayCommentId = null;
		List<Object> records = null;
		PushRecord pushRecord = null;
		Cursor cursor = null;
		try {
			records = new ArrayList<Object>();
			reportTopicId = new ArrayList<Long>();
			replayCommentId = new ArrayList<Long>();
			cursor = this.db.rawQuery(SELECT_PUSH_ALL, new String[]{});
			while(cursor.moveToNext()){
				pushRecord =new PushRecord(context);
				pushRecord.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				int loginAccount = cursor.getInt(cursor.getColumnIndex("login_account"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				pushRecord.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				pushRecord.setType(cursor.getInt(cursor.getColumnIndex("type")));
				pushRecord.setContent_type(cursor.getInt(cursor.getColumnIndex("content_type")));
				pushRecord.setRecord_id(cursor.getLong(cursor.getColumnIndex("record_id")));
				pushRecord.setPush_time(cursor.getLong(cursor.getColumnIndex("push_time")));
				pushRecord.setClick_url(cursor.getString(cursor.getColumnIndex("click_url")));
				pushRecord.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				pushRecord.setContent(content);
				pushRecord.setLogin_account(loginAccount);
				reportObject = new JSONObject(content);
				long topicId = 0;
				try {
					topicId = reportObject.getLong("topicId");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					topicId = 0;
					e.printStackTrace();
				}
				if(loginAccount==2001){//2001表示新闻
					newsIndex++;
				} 
				if(loginAccount==1001){//1001表示举报
					if(-1 != reportTopicId.indexOf(topicId)){//证明已经存在了
						reportIndex = true;
					}else{
						reportTopicId.add(topicId);
					}
				}
				if(loginAccount==1010){//1010表示回复
					if(-1 != replayCommentId.indexOf(topicId)){//证明已经存在了
						replayCommIndex = true;
					}else{
						replayCommentId.add(topicId);
					}
				}
				if((loginAccount==2001 && (newsIndex>1)) || 
				   (loginAccount==1001 && (reportIndex==true)) || 
				   (loginAccount==1010 && (replayCommIndex==true))){//屏蔽更多新闻、更多举报、更多回复推送
					reportIndex = false;
					continue;
				}else{
					records.add(pushRecord);
				}
			}
			this.db.setTransactionSuccessful();
			newsIndex = 0;
			reportIndex = false;
			replayCommIndex = false;
		} catch (Exception e) {
			Loger.i("TEST", "findAllObject0-ERR:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportTopicId.clear();
			reportTopicId = null;
			replayCommentId.clear();
			replayCommentId = null;
			this.db.endTransaction();
		}
		if(records!=null){
			return records;
		}
		return null;
	}
	
	public List<Object> findAllObject(String userId, int type){
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
		List<Object> records = null;
		PushRecord pushRecord = null;
		Cursor cursor = null;
		try {
			records = new ArrayList<Object>();
			switch(type){
			case 0:
			case 1:
				cursor = this.db.rawQuery(SELECT_NORMAL_PUSH_ALL, new String[]{userId,userId,String.valueOf(1002),String.valueOf(1004)});
				break;
			case 2:
			case 3:
				cursor = this.db.rawQuery(SELECT_ADMIN_PUSH_ALL, new String[]{userId,userId,String.valueOf(1001),String.valueOf(1002),String.valueOf(1004)});
				break;
			case 4:
			case 5:
				cursor = this.db.rawQuery(SELECT_PROPERTY_PUSH_ALL, new String[]{userId,userId,String.valueOf(1003)});
				break;
			case 6:
				cursor = this.db.rawQuery(SELECT_ADMIN_PUSH_ALL, new String[]{userId,String.valueOf(1001),String.valueOf(1002)});
				break;
			default:
				break;
			}
			while(cursor.moveToNext()){
				pushRecord =new PushRecord(context);
				pushRecord.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				pushRecord.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				pushRecord.setType(cursor.getInt(cursor.getColumnIndex("type")));
				pushRecord.setContent_type(cursor.getInt(cursor.getColumnIndex("content_type")));
				pushRecord.setRecord_id(cursor.getLong(cursor.getColumnIndex("record_id")));
				pushRecord.setContent(cursor.getString(cursor.getColumnIndex("content")));
				pushRecord.setPush_time(cursor.getLong(cursor.getColumnIndex("push_time")));
				pushRecord.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				pushRecord.setClick_url(cursor.getString(cursor.getColumnIndex("click_url")));
				pushRecord.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				records.add(pushRecord);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findAllObject1-ERR:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		if(records!=null){
			return records;
		}
		return null;
	}
	
	public List<Object> findCommentObjs(String sType, String sUserId){
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
		List<Object> records = null;
		PushRecord pushRecord = null;
		Cursor cursor = null;
		try {
			records = new ArrayList<Object>();
			cursor = this.db.rawQuery(SELECT_PUSH_COMMENT, new String[]{sType,sUserId});
			while(cursor.moveToNext()){
				pushRecord =new PushRecord(context);
				pushRecord.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				pushRecord.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				pushRecord.setType(cursor.getInt(cursor.getColumnIndex("type")));
				pushRecord.setContent_type(cursor.getInt(cursor.getColumnIndex("content_type")));
				pushRecord.setRecord_id(cursor.getLong(cursor.getColumnIndex("record_id")));
				pushRecord.setContent(cursor.getString(cursor.getColumnIndex("content")));
				pushRecord.setPush_time(cursor.getLong(cursor.getColumnIndex("push_time")));
				pushRecord.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				pushRecord.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				pushRecord.setClick_url(cursor.getString(cursor.getColumnIndex("click_url")));
				records.add(pushRecord);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findCommentObjs-ERR:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		if(records!=null){
			return records;
		}
		return null;
	}
	
	public List<Object> findAppointObjs(String sType, String sUserId){
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
		List<Object> records = null;
		PushRecord pushRecord = null;
		Cursor cursor = null;
		try {
			records = new ArrayList<Object>();
			cursor = this.db.rawQuery(SELECT_PUSH_NEWS, new String[]{sType,sUserId});
			while(cursor.moveToNext()){
				pushRecord =new PushRecord(context);
				pushRecord.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				pushRecord.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				pushRecord.setType(cursor.getInt(cursor.getColumnIndex("type")));
				pushRecord.setContent_type(cursor.getInt(cursor.getColumnIndex("content_type")));
				pushRecord.setRecord_id(cursor.getLong(cursor.getColumnIndex("record_id")));
				pushRecord.setContent(cursor.getString(cursor.getColumnIndex("content")));
				pushRecord.setPush_time(cursor.getLong(cursor.getColumnIndex("push_time")));
				pushRecord.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				pushRecord.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				pushRecord.setClick_url(cursor.getString(cursor.getColumnIndex("click_url")));
				records.add(pushRecord);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findAppointObjs-ERR:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		if(records!=null){
			return records;
		}
		return null;
	}
	
	public PushRecord findObjByRecordId(String recordId){
		PushRecord recordObj = null;
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
			cursor = this.db.rawQuery(SELECT_BY_RECORD_ID, new String[]{recordId});
			while(cursor.moveToNext()){
				recordObj =new PushRecord(context);
				recordObj.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				recordObj.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				recordObj.setType(cursor.getInt(cursor.getColumnIndex("type")));
				recordObj.setContent_type(cursor.getInt(cursor.getColumnIndex("content_type")));
				recordObj.setRecord_id(cursor.getLong(cursor.getColumnIndex("record_id")));
				recordObj.setContent(cursor.getString(cursor.getColumnIndex("content")));
				recordObj.setPush_time(cursor.getLong(cursor.getColumnIndex("push_time")));
				recordObj.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				recordObj.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				recordObj.setClick_url(cursor.getString(cursor.getColumnIndex("click_url")));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			Loger.i("TEST", "findObjByRecordId-ERR:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			this.db.endTransaction();
		}
		return recordObj;
	}
	
	public List<Object> findAllObject(String userId) {
		int newsIndex = 0;
		boolean reportIndex = false;
		boolean replayCommIndex = false;
		JSONObject reportObject = null;
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
		List<Long> replayCommentId = null;
		List<Long> reportTopicId = null;
		List<Object> records = null;
		PushRecord pushRecord = null;
		Cursor cursor = null;
		try {
			records = new ArrayList<Object>();
			reportTopicId = new ArrayList<Long>();
			replayCommentId = new ArrayList<Long>();
			cursor = this.db.rawQuery(SELECT_PUSH_ALL, new String[]{userId});
			while(cursor.moveToNext()){
				pushRecord =new PushRecord(context);
				int loginAccount = cursor.getInt(cursor.getColumnIndex("login_account"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				pushRecord.setUser_id(cursor.getLong(cursor.getColumnIndex("user_id")));
				pushRecord.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				pushRecord.setType(cursor.getInt(cursor.getColumnIndex("type")));
				pushRecord.setContent_type(cursor.getInt(cursor.getColumnIndex("content_type")));
				pushRecord.setRecord_id(cursor.getLong(cursor.getColumnIndex("record_id")));
				pushRecord.setPush_time(cursor.getLong(cursor.getColumnIndex("push_time")));
				pushRecord.setClick_url(cursor.getString(cursor.getColumnIndex("click_url")));
				pushRecord.setCommunity_id(cursor.getLong(cursor.getColumnIndex("community_id")));
				pushRecord.setContent(content);
				pushRecord.setLogin_account(loginAccount);
				reportObject = new JSONObject(content);
				long topicId = 0;
				try {
					topicId = reportObject.getLong("topicId");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					topicId = 0;
					e.printStackTrace();
				}
				if(loginAccount==2001){//2001表示新闻
					newsIndex++;
				} 
				if(loginAccount==1001){//1001表示举报
					if(-1 != reportTopicId.indexOf(topicId)){//证明已经存在了
						reportIndex = true;
					}else{
						reportTopicId.add(topicId);
					}
				}
				if(loginAccount==1010){//1010表示回复
					if(-1 != replayCommentId.indexOf(topicId)){//证明已经存在了
						replayCommIndex = true;
					}else{
						replayCommentId.add(topicId);
					}
				}
				if((loginAccount==2001 && (newsIndex>1)) || 
				   (loginAccount==1001 && (reportIndex==true)) ||
				   (loginAccount==1010 && (replayCommIndex==true))){//屏蔽更多新闻、更多举报、更多回复
					reportIndex = false;
					continue;
				}else{
					records.add(pushRecord);
				}
			}
			this.db.setTransactionSuccessful();
			newsIndex = 0;
			reportIndex = false;
		} catch (Exception e) {
			Loger.i("TEST", "findAllObject-ERR:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportTopicId.clear();
			reportTopicId = null;
			replayCommentId.clear();
			replayCommentId = null;
			try {
				this.db.endTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(records!=null){
			return records;
		}
		return null;
	}
	
	@Override
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		PushRecord pushRecord = (PushRecord)obj;
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
			this.db.execSQL(INSERT_SQL, new Object[]{pushRecord.getUser_id(),
													 pushRecord.getType(),
													 pushRecord.getContent_type(),
													 pushRecord.getRecord_id(),
													 pushRecord.getContent(),
													 pushRecord.getClick_url(),
													 pushRecord.getPush_time(),
													 pushRecord.getLogin_account(),
													 pushRecord.getCommunity_id(),
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
		// TODO Auto-generated method stub
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
			this.db.execSQL(DELETE_PUSH_BYID, new Object[]{id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteObject(long recordId){
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
			this.db.execSQL(DELETE_PUSH_BYID, new Object[]{recordId});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteNewsObj(String newsType, String sUserId){
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
			this.db.execSQL(DELETE_NEWS_ALL, new Object[]{sUserId,newsType});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteNewsObj:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteSayHelloObjs(long topicId, String sUserId, String sSayHelloUserId){
		StringBuffer stringBuffer = new StringBuffer();
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
		List<Long> reportList = new ArrayList<Long>();
		Cursor cursor = null;
		JSONObject commJson = null;
		try{
			cursor = this.db.rawQuery(SELECT_SAYHELLO_BYID, new String[]{sUserId,sSayHelloUserId});
			while(cursor.moveToNext()){
				try {
					commJson = new JSONObject(cursor.getString(cursor.getColumnIndex("content")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long recordId = cursor.getLong(cursor.getColumnIndex("record_id"));
				long tId = 0;
				try {
					tId = commJson.getLong("topicId");
				} catch (JSONException e) {
					Loger.i("TEST", "JSONException=>"+e.getMessage());
					tId = 0;
				}
				Loger.i("TEST", "deleteCommentObjs==>tId=>"+tId);
				if(tId==topicId){
					Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
					reportList.add(recordId);
				}
			}
			Loger.i("TEST", "deleteCommentObjs===>reportList==>size==>"+reportList.size());
			this.db.setTransactionSuccessful();
			for(Long id : reportList){
				Loger.i("TEST", "reportList.values()==>"+id);
				stringBuffer.append(id);
				stringBuffer.append(",");
			}
			stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length()-1);
			Loger.i("TEST","stringBuffer===>"+stringBuffer);
		}catch (SQLException e) {
			Loger.i("TEST", "deleteCommentObjs0:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportList.clear();
			reportList = null;
			db.endTransaction();
		}
		String DELETE_COMM_STATUS = "delete from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+
				" where record_id in ("+stringBuffer+")";
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_COMM_STATUS, new Object[]{});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteReportObjs1:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteCommentObjs(long topicId, String sUserId){
		StringBuffer stringBuffer = new StringBuffer();
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
		List<Long> reportList = new ArrayList<Long>();
		Cursor cursor = null;
		JSONObject commJson = null;
		try{
			cursor = this.db.rawQuery(SELECT_COMMENT_BYID, new String[]{sUserId});
			while(cursor.moveToNext()){
				try {
					commJson = new JSONObject(cursor.getString(cursor.getColumnIndex("content")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long recordId = cursor.getLong(cursor.getColumnIndex("record_id"));
				long tId = 0;
				try {
					tId = commJson.getLong("topicId");
				} catch (JSONException e) {
					Loger.i("TEST", "JSONException=>"+e.getMessage());
					tId = 0;
				}
				Loger.i("TEST", "deleteCommentObjs==>tId=>"+tId);
				if(tId==topicId){
					Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
					reportList.add(recordId);
				}
			}
			Loger.i("TEST", "deleteCommentObjs===>reportList==>size==>"+reportList.size());
			this.db.setTransactionSuccessful();
			for(Long id : reportList){
				Loger.i("TEST", "reportList.values()==>"+id);
				stringBuffer.append(id);
				stringBuffer.append(",");
			}
			stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length()-1);
			Loger.i("TEST","stringBuffer===>"+stringBuffer);
		}catch (SQLException e) {
			Loger.i("TEST", "deleteCommentObjs0:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportList.clear();
			reportList = null;
			db.endTransaction();
		}
		String DELETE_COMM_STATUS = "delete from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+
				" where record_id in ("+stringBuffer+")";
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_COMM_STATUS, new Object[]{});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteReportObjs1:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	@SuppressLint("UseSparseArrays")
	public void deleteReportObjs(long topicId, String sUserId){
		StringBuffer stringBuffer = new StringBuffer();
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
		List<Long> reportList = new ArrayList<Long>();
		Cursor cursor = null;
		JSONObject reportJson = null;
		try{
			cursor = this.db.rawQuery(SELECT_REPORT_BYID, new String[]{sUserId});
			while(cursor.moveToNext()){
				try {
					reportJson = new JSONObject(cursor.getString(cursor.getColumnIndex("content")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long recordId = cursor.getLong(cursor.getColumnIndex("record_id"));
				long tId = 0;
				try {
					tId = reportJson.getLong("topicId");
				} catch (JSONException e) {
					Loger.i("TEST", "JSONException=>"+e.getMessage());
					tId = 0;
				}
				Loger.i("TEST", "deleteReportObjs==>tId=>"+tId);
				if(tId==topicId){
					Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
					reportList.add(recordId);
				}
			}
			this.db.setTransactionSuccessful();
			for(Long id : reportList){
				Loger.i("TEST", "reportList.values()==>"+id);
				stringBuffer.append(id);
				stringBuffer.append(",");
			}
			stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length()-1);
			Loger.i("TEST","stringBuffer===>"+stringBuffer);
		}catch (SQLException e) {
			Loger.i("TEST", "deleteReportObjs0:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportList.clear();
			reportList = null;
			db.endTransaction();
		}
		String DELETE_REPORT_STATUS = "delete from "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+
				" where record_id in ("+stringBuffer+")";
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_REPORT_STATUS, new Object[]{});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteReportObjs:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void deleteAllObjects(String sUserId){
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
			this.db.execSQL(DELETE_PUSH_ALL, new Object[]{sUserId});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "deleteAllObjects:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void modifyObject(Object obj) {
		// TODO Auto-generated method stub
		PushRecord pushRecord = (PushRecord)obj;
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
			this.db.execSQL(MODIFY_SQL, new Object[]{pushRecord.getUser_id(),
													 pushRecord.getType(),
													 pushRecord.getContent_type(),
													 pushRecord.getRecord_id(),
													 pushRecord.getContent(),
													 pushRecord.getClick_url(),
													 pushRecord.getPush_time(),
													 pushRecord.getLogin_account(),
													 pushRecord.getCommunity_id(),
													 NeighborsDBOpenHelper.DB_VERSION,
													 pushRecord.get_id()});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "modifyObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void modifyNewsObjs(int readStatus, long userId){
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
			this.db.execSQL(UPDATE_NEWS_STATUS, new Object[]{readStatus,userId});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "modifyNewsObjs:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	
	public void modifySayHelloObjs(int readStatus, long topicId, String sUserId, String sayHelloUserId){
		StringBuffer stringBuffer = new StringBuffer();
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
		List<Long> reportList = new ArrayList<Long>();
		Cursor cursor = null;
		JSONObject reportJson = null;
		try{
			cursor = this.db.rawQuery(SELECT_SAYHELLO_BYID, new String[]{sUserId,sayHelloUserId});
			while(cursor.moveToNext()){
				try {
					reportJson = new JSONObject(cursor.getString(cursor.getColumnIndex("content")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				long recordId = cursor.getLong(cursor.getColumnIndex("record_id"));
				long tId = 0;
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				try {
					tId = reportJson.getLong("topicId");
				} catch (JSONException e) {
					Loger.i("TEST", "JSONException=>"+e.getMessage());
					tId = 0;
				}
				Loger.i("TEST", "modifySayHelloObjs==>tId=>"+tId);
				if(1==readStatus){
					if((tId==topicId)&&(2==type)){
						Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
						reportList.add(recordId);
					}
				}else{
					if((tId==topicId)&&(1==type)){
						Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
						reportList.add(recordId);
					}
				}
			}
			this.db.setTransactionSuccessful();
			for(Long id : reportList){
				Loger.i("TEST", "commList.values()==>"+id);
				stringBuffer.append(id);
				stringBuffer.append(",");
			}
			try {
				stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length()-1);
			} catch (Exception e) {
				Loger.i("TEST","stringBuffer===Exception>"+e.getMessage());
			}
			Loger.i("TEST","stringBuffer===>"+stringBuffer);
		}catch (SQLException e) {
			Loger.i("TEST", "modifySayHelloObjs0:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportList.clear();
			reportList = null;
			db.endTransaction();
		}
		if(stringBuffer.length()<=0){
			return;
		}else{
			String UPDATE_REPORT_STATUS = "update "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+
					" set type = ? where record_id in ("+stringBuffer.toString()+")";
			Loger.i("TEST", "UPDATE_COMMENT_STATUS==>"+UPDATE_REPORT_STATUS);
			this.db.beginTransaction();
			try {
				this.db.execSQL(UPDATE_REPORT_STATUS, new Object[]{readStatus});
				this.db.setTransactionSuccessful();
			} catch (SQLException e) {
				Loger.i("TEST", "modifySayHelloObjs1:"+e.getMessage());
				e.printStackTrace();
			}finally{
				db.endTransaction();
			}
		}
	}
	
	public void modifyCommentObjs(int readStatus, long topicId, String sUserId){
		StringBuffer stringBuffer = new StringBuffer();
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
		List<Long> reportList = new ArrayList<Long>();
		Cursor cursor = null;
		JSONObject reportJson = null;
		try{
			cursor = this.db.rawQuery(SELECT_COMMENT_BYID, new String[]{sUserId});
			while(cursor.moveToNext()){
				try {
					reportJson = new JSONObject(cursor.getString(cursor.getColumnIndex("content")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				long recordId = cursor.getLong(cursor.getColumnIndex("record_id"));
				long tId = 0;
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				try {
					tId = reportJson.getLong("topicId");
				} catch (JSONException e) {
					Loger.i("TEST", "JSONException=>"+e.getMessage());
					tId = 0;
				}
				Loger.i("TEST", "modifyCommentObjs==>tId=>"+tId);
				if(1==readStatus){
					if((tId==topicId)&&(2==type)){
						Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
						reportList.add(recordId);
					}
				}else{
					if((tId==topicId)&&(1==type)){
						Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
						reportList.add(recordId);
					}
				}
			}
			this.db.setTransactionSuccessful();
			for(Long id : reportList){
				Loger.i("TEST", "commList.values()==>"+id);
				stringBuffer.append(id);
				stringBuffer.append(",");
			}
			try {
				stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length()-1);
			} catch (Exception e) {
				Loger.i("TEST","stringBuffer===Exception>"+e.getMessage());
			}
			Loger.i("TEST","stringBuffer===>"+stringBuffer);
		}catch (SQLException e) {
			Loger.i("TEST", "modifyCommentObjs0:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportList.clear();
			reportList = null;
			db.endTransaction();
		}
		if(stringBuffer.length()<=0){
			return;
		}else{
			String UPDATE_REPORT_STATUS = "update "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+
					" set type = ? where record_id in ("+stringBuffer.toString()+")";
			Loger.i("TEST", "UPDATE_COMMENT_STATUS==>"+UPDATE_REPORT_STATUS);
			this.db.beginTransaction();
			try {
				this.db.execSQL(UPDATE_REPORT_STATUS, new Object[]{readStatus});
				this.db.setTransactionSuccessful();
			} catch (SQLException e) {
				Loger.i("TEST", "modifyCommentObjs1:"+e.getMessage());
				e.printStackTrace();
			}finally{
				db.endTransaction();
			}
		}
	}
	
	@SuppressLint("UseSparseArrays")
	public void modifyReportObjs(int readStatus, long topicId, String sUserId){
		StringBuffer stringBuffer = new StringBuffer();
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
		List<Long> reportList = new ArrayList<Long>();
		Cursor cursor = null;
		JSONObject reportJson = null;
		try{
			cursor = this.db.rawQuery(SELECT_REPORT_BYID, new String[]{sUserId});
			while(cursor.moveToNext()){
				try {
					reportJson = new JSONObject(cursor.getString(cursor.getColumnIndex("content")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long recordId = cursor.getLong(cursor.getColumnIndex("record_id"));
				long tId = 0;
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				try {
					tId = reportJson.getLong("topicId");
				} catch (JSONException e) {
					Loger.i("TEST", "JSONException=>"+e.getMessage());
					tId = 0;
				}
				Loger.i("TEST", "modifyReportObjs==>tId=>"+tId);
				if(1==readStatus){
					if((tId==topicId)&&(2==type)){
						Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
						reportList.add(recordId);
					}
				}else{
					if((tId==topicId)&&(1==type)){
						Loger.i("TEST", "tId=>"+tId+"  recordId=>"+recordId);
						reportList.add(recordId);
					}
				}
			}
			this.db.setTransactionSuccessful();
			for(Long id : reportList){
				Loger.i("TEST", "reportList.values()==>"+id);
				stringBuffer.append(id);
				stringBuffer.append(",");
			}
			try {
				stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length()-1);
			} catch (Exception e) {
				Loger.i("TEST", "stringBuffer===>Exception==>"+e.getMessage());
				return;
			}
			Loger.i("TEST","stringBuffer===>"+stringBuffer);
		}catch (SQLException e) {
			Loger.i("TEST", "modifyReportObjs0:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			reportList.clear();
			reportList = null;
			db.endTransaction();
		}
		if(stringBuffer.length()<=0){
			return;
		}else{
			String UPDATE_REPORT_STATUS = "update "+NeighborsDBOpenHelper.TABLE_PUSH_RECORD+
					" set type = ? where record_id in ("+stringBuffer.toString()+")";
			Loger.i("TEST", "UPDATE_REPORT_STATUS==>"+UPDATE_REPORT_STATUS);
			this.db.beginTransaction();
			try {
				this.db.execSQL(UPDATE_REPORT_STATUS, new Object[]{readStatus});
				this.db.setTransactionSuccessful();
			} catch (SQLException e) {
				Loger.i("TEST", "modifyReportObjs1:"+e.getMessage());
				e.printStackTrace();
			}finally{
				db.endTransaction();
			}
		}
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
