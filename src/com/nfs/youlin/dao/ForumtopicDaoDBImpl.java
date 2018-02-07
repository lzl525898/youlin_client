package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ForumtopicDaoDBImpl implements INeighborsDao 
{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	
	public int[] modifyType = {1,2,3}; //"like->","view->2","comment->3"
	
	private final String INSERT_SQL = "replace into " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + "("
			+ "cache_key, topic_id, forum_id, forum_name, circle_type, sender_nc_role, sender_id,sender_community_id,"
			+ "sender_name, sender_lever, sender_portrait, sender_family_id, sender_family_address,"
			+ "display_name, topic_time, topic_title, topic_content, topic_category_type , comment_num,"
			+ "like_num, send_status, like_status, visiable_type, login_account, topic_url, forward_path,"
			+ "forward_refer_id, object_type, object_data, hot_flag,view_num, media_files_json,"
			+ "comments_summary, table_version) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private final String DELETE_FORUM_TOPIC_ALL = "delete from " +
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC;
	private final String DELETE_FORUM_TOPIC_SIX = "delete from" +
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC +" where topic_id > ?";
	private final String DELETE_FORUM_TOPIC_BYID = "delete from " + 
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + " where topic_id = ?";
	
	private final String UPDATE_COMM = "update " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
			            " set comment_num = ?, comments_summary = ? where topic_id = ?";
	
	private final String MODIFY_SQL = "update " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
						" set like_num = ?, like_status = ?, view_num = ?, comment_num = ?  where topic_id = ?";
	
	private final String SELECT_TARGET_TOPIC_BYID = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
						" where topic_id = ? GROUP BY topic_id";
	
	public ForumtopicDaoDBImpl(Context context) {
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	
	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public ForumTopic findTargetObject(long topic_id){
		ForumTopic forum_topic = null;
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_TARGET_TOPIC_BYID, new String[]{String.valueOf(topic_id)});
			if(cursor != null){
				forum_topic = new ForumTopic(this.context);
				while(cursor.moveToNext()){
					forum_topic.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
					forum_topic.setCache_key(cursor.getInt(cursor.getColumnIndex("cache_key")));
					forum_topic.setCircle_type(cursor.getInt(cursor.getColumnIndex("circle_type")));
					forum_topic.setSender_nc_role(cursor.getInt(cursor.getColumnIndex("sender_nc_role")));
					forum_topic.setTopic_category_type(cursor.getInt(cursor.getColumnIndex("topic_category_type")));
					forum_topic.setComment_num(cursor.getInt(cursor.getColumnIndex("comment_num")));
					forum_topic.setLike_num(cursor.getInt(cursor.getColumnIndex("like_num")));
					forum_topic.setSend_status(cursor.getInt(cursor.getColumnIndex("send_status")));
					forum_topic.setLike_status(cursor.getInt(cursor.getColumnIndex("like_status")));
					forum_topic.setVisiable_type(cursor.getInt(cursor.getColumnIndex("visiable_type")));
					forum_topic.setForward_refer_id(cursor.getInt(cursor.getColumnIndex("forward_refer_id")));
					forum_topic.setObject_type(cursor.getInt(cursor.getColumnIndex("object_type")));
					forum_topic.setHot_flag(cursor.getInt(cursor.getColumnIndex("hot_flag")));
					forum_topic.setView_num(cursor.getInt(cursor.getColumnIndex("view_num")));
					forum_topic.setTopic_id(cursor.getLong(cursor.getColumnIndex("topic_id")));
					forum_topic.setForum_id(cursor.getLong(cursor.getColumnIndex("forum_id")));
					forum_topic.setSender_id(cursor.getLong(cursor.getColumnIndex("sender_id")));
					forum_topic.setSender_community_id(cursor.getLong(cursor.getColumnIndex("sender_community_id")));
					forum_topic.setSender_family_id(cursor.getLong(cursor.getColumnIndex("sender_family_id")));
					forum_topic.setTopic_time(cursor.getLong(cursor.getColumnIndex("topic_time")));
					forum_topic.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
					forum_topic.setForum_name(cursor.getString(cursor.getColumnIndex("forum_name")));
					forum_topic.setSender_name(cursor.getString(cursor.getColumnIndex("sender_name")));
					forum_topic.setSender_lever(cursor.getString(cursor.getColumnIndex("sender_lever")));
					forum_topic.setSender_portrait(cursor.getString(cursor.getColumnIndex("sender_portrait")));
					forum_topic.setSender_family_address(cursor.getString(cursor.getColumnIndex("sender_family_address")));
					forum_topic.setDisplay_name(cursor.getString(cursor.getColumnIndex("display_name")));
					forum_topic.setTopic_title(cursor.getString(cursor.getColumnIndex("topic_title")));
					forum_topic.setTopic_content(cursor.getString(cursor.getColumnIndex("topic_content")));
					forum_topic.setTopic_url(cursor.getString(cursor.getColumnIndex("topic_url")));
					forum_topic.setForward_path(cursor.getString(cursor.getColumnIndex("forward_path")));
					forum_topic.setObject_data(cursor.getString(cursor.getColumnIndex("object_data")));
					forum_topic.setMeadia_files_json(cursor.getString(cursor.getColumnIndex("media_files_json")));
					forum_topic.setComments_summary(cursor.getString(cursor.getColumnIndex("comments_summary")));
				}
			}
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "Forum_topicDaoDBImpl findTargetObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
			db.endTransaction();
		}
		return forum_topic;
	}
	
	//Pull_Up
	public List<Object> findAllObject(int pagesize, long topicId) {
		Loger.i("TEST", "开始下拉数据库查询");
		List<Object> forum_topicList = new ArrayList<Object>();
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		db.beginTransaction();
		String SQL_QT;
		if(topicId!=0){
			SQL_QT = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
				" where sender_community_id = ? and topic_id < ? GROUP BY topic_id ORDER BY topic_id DESC LIMIT 0,?";
		}else{
			SQL_QT = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
					" where sender_community_id = ? GROUP BY topic_id ORDER BY topic_id DESC LIMIT 0,?";
		}
		Cursor cursor = null;
		String currentCommunity = getCurrentCommunityId();
		Loger.i("TEST", "DAO当前CommunityID->"+currentCommunity);
		String currentItem = String.valueOf(topicId);
		String pageSize = String.valueOf(pagesize);
		try {
			if(topicId!=0){
				cursor = this.db.rawQuery(SQL_QT, new String[]{currentCommunity, currentItem, pageSize});
			}else{
				cursor = this.db.rawQuery(SQL_QT, new String[]{currentCommunity, pageSize});
			}
			while(cursor.moveToNext()){
				ForumTopic forum_topic = new ForumTopic(context);
				forum_topic.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				forum_topic.setCache_key(cursor.getInt(cursor.getColumnIndex("cache_key")));
				forum_topic.setCircle_type(cursor.getInt(cursor.getColumnIndex("circle_type")));
				forum_topic.setSender_nc_role(cursor.getInt(cursor.getColumnIndex("sender_nc_role")));
				forum_topic.setTopic_category_type(cursor.getInt(cursor.getColumnIndex("topic_category_type")));
				forum_topic.setComment_num(cursor.getInt(cursor.getColumnIndex("comment_num")));
				forum_topic.setLike_num(cursor.getInt(cursor.getColumnIndex("like_num")));
				forum_topic.setSend_status(cursor.getInt(cursor.getColumnIndex("send_status")));
				forum_topic.setLike_status(cursor.getInt(cursor.getColumnIndex("like_status")));
				forum_topic.setVisiable_type(cursor.getInt(cursor.getColumnIndex("visiable_type")));
				forum_topic.setForward_refer_id(cursor.getInt(cursor.getColumnIndex("forward_refer_id")));
				forum_topic.setObject_type(cursor.getInt(cursor.getColumnIndex("object_type")));
				forum_topic.setHot_flag(cursor.getInt(cursor.getColumnIndex("hot_flag")));
				forum_topic.setView_num(cursor.getInt(cursor.getColumnIndex("view_num")));
				forum_topic.setTopic_id(cursor.getLong(cursor.getColumnIndex("topic_id")));
				forum_topic.setForum_id(cursor.getLong(cursor.getColumnIndex("forum_id")));
				forum_topic.setSender_id(cursor.getLong(cursor.getColumnIndex("sender_id")));
				forum_topic.setSender_community_id(cursor.getLong(cursor.getColumnIndex("sender_community_id")));
				forum_topic.setSender_family_id(cursor.getLong(cursor.getColumnIndex("sender_family_id")));
				forum_topic.setTopic_time(cursor.getLong(cursor.getColumnIndex("topic_time")));
				forum_topic.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				forum_topic.setForum_name(cursor.getString(cursor.getColumnIndex("forum_name")));
				forum_topic.setSender_name(cursor.getString(cursor.getColumnIndex("sender_name")));
				forum_topic.setSender_lever(cursor.getString(cursor.getColumnIndex("sender_lever")));
				forum_topic.setSender_portrait(cursor.getString(cursor.getColumnIndex("sender_portrait")));
				forum_topic.setSender_family_address(cursor.getString(cursor.getColumnIndex("sender_family_address")));
				forum_topic.setDisplay_name(cursor.getString(cursor.getColumnIndex("display_name")));
				forum_topic.setTopic_title(cursor.getString(cursor.getColumnIndex("topic_title")));
				forum_topic.setTopic_content(cursor.getString(cursor.getColumnIndex("topic_content")));
				forum_topic.setTopic_url(cursor.getString(cursor.getColumnIndex("topic_url")));
				forum_topic.setForward_path(cursor.getString(cursor.getColumnIndex("forward_path")));
				forum_topic.setObject_data(cursor.getString(cursor.getColumnIndex("object_data")));
				forum_topic.setMeadia_files_json(cursor.getString(cursor.getColumnIndex("media_files_json")));
				forum_topic.setComments_summary(cursor.getString(cursor.getColumnIndex("comments_summary")));
				forum_topic.setFlag(false);
				forum_topicList.add(forum_topic);
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
		return forum_topicList;
	}
	//new topic
	public List<Object> findNewObject(long topicId) {
		List<Object> forum_topicList = new ArrayList<Object>();
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		String SQL_QT = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
				" where sender_community_id = ? AND topic_id > ? GROUP BY topic_id ORDER BY topic_id DESC";
		db.beginTransaction();
		Cursor cursor = null;
		String currentCommunity = getCurrentCommunityId();
		Loger.i("TEST", "DAO当前CommunityID->"+currentCommunity);
		String currentItem = String.valueOf(0);
		String newTopicId = String.valueOf(topicId);
		try {
			cursor = this.db.rawQuery(SQL_QT, new String[]{currentCommunity,newTopicId});
			while(cursor.moveToNext()){
				ForumTopic forum_topic = new ForumTopic(context);
				forum_topic.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				forum_topic.setCache_key(cursor.getInt(cursor.getColumnIndex("cache_key")));
				forum_topic.setCircle_type(cursor.getInt(cursor.getColumnIndex("circle_type")));
				forum_topic.setSender_nc_role(cursor.getInt(cursor.getColumnIndex("sender_nc_role")));
				forum_topic.setTopic_category_type(cursor.getInt(cursor.getColumnIndex("topic_category_type")));
				forum_topic.setComment_num(cursor.getInt(cursor.getColumnIndex("comment_num")));
				forum_topic.setLike_num(cursor.getInt(cursor.getColumnIndex("like_num")));
				forum_topic.setSend_status(cursor.getInt(cursor.getColumnIndex("send_status")));
				forum_topic.setLike_status(cursor.getInt(cursor.getColumnIndex("like_status")));
				forum_topic.setVisiable_type(cursor.getInt(cursor.getColumnIndex("visiable_type")));
				forum_topic.setForward_refer_id(cursor.getInt(cursor.getColumnIndex("forward_refer_id")));
				forum_topic.setObject_type(cursor.getInt(cursor.getColumnIndex("object_type")));
				forum_topic.setHot_flag(cursor.getInt(cursor.getColumnIndex("hot_flag")));
				forum_topic.setView_num(cursor.getInt(cursor.getColumnIndex("view_num")));
				forum_topic.setTopic_id(cursor.getLong(cursor.getColumnIndex("topic_id")));
				forum_topic.setForum_id(cursor.getLong(cursor.getColumnIndex("forum_id")));
				forum_topic.setSender_id(cursor.getLong(cursor.getColumnIndex("sender_id")));
				forum_topic.setSender_community_id(cursor.getLong(cursor.getColumnIndex("sender_community_id")));
				forum_topic.setSender_family_id(cursor.getLong(cursor.getColumnIndex("sender_family_id")));
				forum_topic.setTopic_time(cursor.getLong(cursor.getColumnIndex("topic_time")));
				forum_topic.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				forum_topic.setForum_name(cursor.getString(cursor.getColumnIndex("forum_name")));
				forum_topic.setSender_name(cursor.getString(cursor.getColumnIndex("sender_name")));
				forum_topic.setSender_lever(cursor.getString(cursor.getColumnIndex("sender_lever")));
				forum_topic.setSender_portrait(cursor.getString(cursor.getColumnIndex("sender_portrait")));
				forum_topic.setSender_family_address(cursor.getString(cursor.getColumnIndex("sender_family_address")));
				forum_topic.setDisplay_name(cursor.getString(cursor.getColumnIndex("display_name")));
				forum_topic.setTopic_title(cursor.getString(cursor.getColumnIndex("topic_title")));
				forum_topic.setTopic_content(cursor.getString(cursor.getColumnIndex("topic_content")));
				forum_topic.setTopic_url(cursor.getString(cursor.getColumnIndex("topic_url")));
				forum_topic.setForward_path(cursor.getString(cursor.getColumnIndex("forward_path")));
				forum_topic.setObject_data(cursor.getString(cursor.getColumnIndex("object_data")));
				forum_topic.setMeadia_files_json(cursor.getString(cursor.getColumnIndex("media_files_json")));
				forum_topic.setComments_summary(cursor.getString(cursor.getColumnIndex("comments_summary")));
				forum_topic.setFlag(false);
				forum_topicList.add(0,forum_topic);
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
		return forum_topicList;
	}
	//Pull_Down
	public List<Object> findAllObject(int pagesize) {

		List<Object> forum_topicList = new ArrayList<Object>();
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}		
		String SQL_QT = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
				" where sender_community_id = ? GROUP BY topic_id ORDER BY topic_id DESC LIMIT ?,?";
		db.beginTransaction();
		Cursor cursor = null;
		String currentCommunity = getCurrentCommunityId();
		Loger.i("TEST", "DAO当前CommunityID->"+currentCommunity);
		String currentItem = String.valueOf(0);
		String pageSize = String.valueOf(pagesize);
		try {
			cursor = this.db.rawQuery(SQL_QT, new String[]{currentCommunity,currentItem, pageSize});
			while(cursor.moveToNext()){
				ForumTopic forum_topic = new ForumTopic(context);
				forum_topic.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				forum_topic.setCache_key(cursor.getInt(cursor.getColumnIndex("cache_key")));
				forum_topic.setCircle_type(cursor.getInt(cursor.getColumnIndex("circle_type")));
				forum_topic.setSender_nc_role(cursor.getInt(cursor.getColumnIndex("sender_nc_role")));
				forum_topic.setTopic_category_type(cursor.getInt(cursor.getColumnIndex("topic_category_type")));
				forum_topic.setComment_num(cursor.getInt(cursor.getColumnIndex("comment_num")));
				forum_topic.setLike_num(cursor.getInt(cursor.getColumnIndex("like_num")));
				forum_topic.setSend_status(cursor.getInt(cursor.getColumnIndex("send_status")));
				forum_topic.setLike_status(cursor.getInt(cursor.getColumnIndex("like_status")));
				forum_topic.setVisiable_type(cursor.getInt(cursor.getColumnIndex("visiable_type")));
				forum_topic.setForward_refer_id(cursor.getInt(cursor.getColumnIndex("forward_refer_id")));
				forum_topic.setObject_type(cursor.getInt(cursor.getColumnIndex("object_type")));
				forum_topic.setHot_flag(cursor.getInt(cursor.getColumnIndex("hot_flag")));
				forum_topic.setView_num(cursor.getInt(cursor.getColumnIndex("view_num")));
				forum_topic.setTopic_id(cursor.getLong(cursor.getColumnIndex("topic_id")));
				forum_topic.setForum_id(cursor.getLong(cursor.getColumnIndex("forum_id")));
				forum_topic.setSender_id(cursor.getLong(cursor.getColumnIndex("sender_id")));
				forum_topic.setSender_community_id(cursor.getLong(cursor.getColumnIndex("sender_community_id")));
				forum_topic.setSender_family_id(cursor.getLong(cursor.getColumnIndex("sender_family_id")));
				forum_topic.setTopic_time(cursor.getLong(cursor.getColumnIndex("topic_time")));
				forum_topic.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				forum_topic.setForum_name(cursor.getString(cursor.getColumnIndex("forum_name")));
				forum_topic.setSender_name(cursor.getString(cursor.getColumnIndex("sender_name")));
				forum_topic.setSender_lever(cursor.getString(cursor.getColumnIndex("sender_lever")));
				forum_topic.setSender_portrait(cursor.getString(cursor.getColumnIndex("sender_portrait")));
				forum_topic.setSender_family_address(cursor.getString(cursor.getColumnIndex("sender_family_address")));
				forum_topic.setDisplay_name(cursor.getString(cursor.getColumnIndex("display_name")));
				forum_topic.setTopic_title(cursor.getString(cursor.getColumnIndex("topic_title")));
				forum_topic.setTopic_content(cursor.getString(cursor.getColumnIndex("topic_content")));
				forum_topic.setTopic_url(cursor.getString(cursor.getColumnIndex("topic_url")));
				forum_topic.setForward_path(cursor.getString(cursor.getColumnIndex("forward_path")));
				forum_topic.setObject_data(cursor.getString(cursor.getColumnIndex("object_data")));
				forum_topic.setMeadia_files_json(cursor.getString(cursor.getColumnIndex("media_files_json")));
				forum_topic.setComments_summary(cursor.getString(cursor.getColumnIndex("comments_summary")));
				forum_topic.setFlag(false);
				forum_topicList.add(forum_topic);
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
		return forum_topicList;
	}
	public List<Object> findAllObject(int pagesize, int page,int category) {

		List<Object> forum_topicList = new ArrayList<Object>();
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}		
		String SQL_QT = "select * from " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + 
				" where topic_category_type = ? ORDER BY _id DESC LIMIT ?,?";
		db.beginTransaction();
		Cursor cursor = null;
		String currentItem = String.valueOf((page-1) * pagesize);
		String pageSize = String.valueOf(pagesize);
		String forumtype =  String.valueOf(category);
		try {
			cursor = this.db.rawQuery(SQL_QT, new String[]{forumtype,currentItem, pageSize});
			while(cursor.moveToNext()){
				ForumTopic forum_topic = new ForumTopic(context);
				forum_topic.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
				forum_topic.setCache_key(cursor.getInt(cursor.getColumnIndex("cache_key")));
				forum_topic.setCircle_type(cursor.getInt(cursor.getColumnIndex("circle_type")));
				forum_topic.setSender_nc_role(cursor.getInt(cursor.getColumnIndex("sender_nc_role")));
				forum_topic.setTopic_category_type(cursor.getInt(cursor.getColumnIndex("topic_category_type")));
				forum_topic.setComment_num(cursor.getInt(cursor.getColumnIndex("comment_num")));
				forum_topic.setLike_num(cursor.getInt(cursor.getColumnIndex("like_num")));
				forum_topic.setSend_status(cursor.getInt(cursor.getColumnIndex("send_status")));
				forum_topic.setLike_status(cursor.getInt(cursor.getColumnIndex("like_status")));
				forum_topic.setVisiable_type(cursor.getInt(cursor.getColumnIndex("visiable_type")));
				forum_topic.setForward_refer_id(cursor.getInt(cursor.getColumnIndex("forward_refer_id")));
				forum_topic.setObject_type(cursor.getInt(cursor.getColumnIndex("object_type")));
				forum_topic.setHot_flag(cursor.getInt(cursor.getColumnIndex("hot_flag")));
				forum_topic.setView_num(cursor.getInt(cursor.getColumnIndex("view_num")));
				forum_topic.setTopic_id(cursor.getLong(cursor.getColumnIndex("topic_id")));
				forum_topic.setForum_id(cursor.getLong(cursor.getColumnIndex("forum_id")));
				forum_topic.setSender_id(cursor.getLong(cursor.getColumnIndex("sender_id")));
				forum_topic.setSender_community_id(cursor.getLong(cursor.getColumnIndex("sender_community_id")));
				forum_topic.setSender_family_id(cursor.getLong(cursor.getColumnIndex("sender_family_id")));
				forum_topic.setTopic_time(cursor.getLong(cursor.getColumnIndex("topic_time")));
				forum_topic.setLogin_account(cursor.getLong(cursor.getColumnIndex("login_account")));
				forum_topic.setForum_name(cursor.getString(cursor.getColumnIndex("forum_name")));
				forum_topic.setSender_name(cursor.getString(cursor.getColumnIndex("sender_name")));
				forum_topic.setSender_lever(cursor.getString(cursor.getColumnIndex("sender_lever")));
				forum_topic.setSender_portrait(cursor.getString(cursor.getColumnIndex("sender_portrait")));
				forum_topic.setSender_family_address(cursor.getString(cursor.getColumnIndex("sender_family_address")));
				forum_topic.setDisplay_name(cursor.getString(cursor.getColumnIndex("display_name")));
				forum_topic.setTopic_title(cursor.getString(cursor.getColumnIndex("topic_title")));
				forum_topic.setTopic_content(cursor.getString(cursor.getColumnIndex("topic_content")));
				forum_topic.setTopic_url(cursor.getString(cursor.getColumnIndex("topic_url")));
				forum_topic.setForward_path(cursor.getString(cursor.getColumnIndex("forward_path")));
				forum_topic.setObject_data(cursor.getString(cursor.getColumnIndex("object_data")));
				forum_topic.setMeadia_files_json(cursor.getString(cursor.getColumnIndex("media_files_json")));
				forum_topic.setComments_summary(cursor.getString(cursor.getColumnIndex("comments_summary")));
				forum_topic.setFlag(false);
				forum_topicList.add(forum_topic);
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
		return forum_topicList;
	}

	@Override
	public void saveObject(Object obj) {
		try {
			ForumTopic forum_topic = (ForumTopic) obj;
			if(this.db == null && dbOpenHelper!=null){
				this.db = this.dbOpenHelper.getReadableDatabase();
			}
			
			this.db.beginTransaction();
			try{
				this.db.execSQL(INSERT_SQL, new Object[]{
						forum_topic.getCache_key(),
						forum_topic.getTopic_id(),
						forum_topic.getForum_id(),
						forum_topic.getForum_name(),
						forum_topic.getCircle_type(),
						forum_topic.getSender_nc_role(),
						forum_topic.getSender_id(),
						forum_topic.getSender_community_id(),
						forum_topic.getSender_name(),
						forum_topic.getSender_lever(),
						forum_topic.getSender_portrait(),
						forum_topic.getSender_family_id(),
						forum_topic.getSender_family_address(),
						forum_topic.getDisplay_name(),
						forum_topic.getTopic_time(),
						forum_topic.getTopic_title(),
						forum_topic.getTopic_content(),
						forum_topic.getTopic_category_type(),
						forum_topic.getComment_num(),
						forum_topic.getLike_num(),
						forum_topic.getSend_status(),
						forum_topic.getLike_status(),
						forum_topic.getVisiable_type(),
						forum_topic.getLogin_account(),
						forum_topic.getTopic_url(),
						forum_topic.getForward_path(),
						forum_topic.getForward_refer_id(),
						forum_topic.getObject_type(),
						forum_topic.getObject_data(),
						forum_topic.getHot_flag(),
						forum_topic.getView_num(),
						forum_topic.getMeadia_files_json(),
						forum_topic.getComments_summary(),
						NeighborsDBOpenHelper.DB_VERSION});
				this.db.setTransactionSuccessful();
			}catch (SQLException e) {
				Loger.i("TEST", "Forum_topicDaoDBImpl saveObject:"+e.getMessage());
				e.printStackTrace();
			}finally{
				db.endTransaction();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteObject(int id) {
		Loger.i("TEST", "run deleteObject(int id)...");
	}
	
	public void deleteAllObjects(){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_FORUM_TOPIC_ALL, new Object[]{});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "Forum_topicDaoDBImpl deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	public void deleteSixObjects(long topic_id){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_FORUM_TOPIC_SIX, new Object[]{topic_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "Forum_topicDaoDBImpl deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}
	public void deleteObject(long topic_id){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		this.db.beginTransaction();
		try {
			this.db.execSQL(DELETE_FORUM_TOPIC_BYID, new Object[]{topic_id});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "Forum_topicDaoDBImpl deleteObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void modifyObject(Object obj) {
		// TODO Auto-generated method stub
	}
	
	//like, view, comment is change 
	public void modifyObject(Object obj, int type){
		
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		ForumTopic forum_topic = (ForumTopic) obj;
		if(1 == type){//"like"
			if(forum_topic.getLike_status() == 0){
				forum_topic.setLike_num(forum_topic.getLike_num() + 1);
				forum_topic.setLike_status(1);
			}else if(forum_topic.getLike_status() == 1){
				forum_topic.setLike_num(forum_topic.getLike_num() - 1);
				forum_topic.setLike_status(0);
			}
			
		}else if(2 == type){//"view"
			forum_topic.setView_num(forum_topic.getView_num() + 1);
		}else if(3 == type){//"comment"
			forum_topic.setComment_num(forum_topic.getComment_num() + 1);
		}
		
		this.db.beginTransaction();
		try {
			this.db.execSQL(MODIFY_SQL, new Object[]{forum_topic.getLike_num(),
													 forum_topic.getLike_status(),
													 forum_topic.getView_num(),
													 forum_topic.getComment_num(),
													 forum_topic.getTopic_id()});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "modifyObject:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
		}
	}

	public void updateComment(Object obj){
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}
		ForumTopic forum_topic = (ForumTopic) obj;
		this.db.beginTransaction();
		try {
			this.db.execSQL(UPDATE_COMM, new Object[]{forum_topic.getComment_num(),
					                                  forum_topic.getComments_summary(),
													  forum_topic.getTopic_id()});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			Loger.i("TEST", "updateComment:"+e.getMessage());
			e.printStackTrace();
		}finally{
			db.endTransaction();
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

	@Override
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	private String getCurrentCommunityId(){
		SharedPreferences sharedata = this.context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String communityId = sharedata.getString("familycommunityid", "0");
		if(communityId=="" || communityId==null || communityId=="null"){
			communityId = "0";
		}
		return communityId;
	}
	
}
