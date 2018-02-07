package com.nfs.youlin.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NeighborsDBOpenHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "neighbors.db";
	
	public final static int DB_VERSION = 1;
	
	public final static String TABLE_NAME_USERS          = "table_users";
	public final static String TABLE_ALL_FAMILY          = "table_all_family";
	public final static String TABLE_NAME_NEIGHBOR       = "table_neighbor";
	public final static String TABLE_NAME_NEIGHBOR_GROUP = "table_neighbor_group";
	public final static String TABLE_NAME_FORUM_TOPIC    = "table_forum_topic";
	public final static String TABLE_NAME_FORUM_COMMENT  = "table_forum_comment"; 
	public final static String TABLE_NAME_FORUM_MEDIA    = "table_forum_media" ;
	public final static String TABLE_NAME_ALL_NOTE       = "table_all_note";
	public final static String TABLE_NAME_SEARCH_HISTORY = "table_search_history";
	public final static String TABLE_DOOR_PLATE          = "tabel_doorplate";
	public final static String TABLE_PUSH_RECORD         = "table_push_record";
	public final static String TABLE_NEWS_RECEIVE         = "table_news_receive";
	
	private final String CREATE_TABLE_NEWS_RECEIVE= "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+" ("
			+"_id integer primary key autoincrement, "
			+"news_first integer, "
			+"news_title text, "
			+"news_pic text, "
			+"news_url text, "
			+"news_belongs integer, "
			+"news_id integer, "
			+"news_send_time integer, "
			+"news_push_time integer, "
			+"news_others text, "
			+"table_version integer)";
	private final String CREATE_INDEX_NEWS_RECEIVE_LA = "CREATE INDEX news_belongs_index on "+
			NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+"(news_belongs)";
	
	private final String CREATE_TABLE_PUSH_RECORD = "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_PUSH_RECORD+" ("
			+"_id integer primary key autoincrement, "
			+"user_id bigint, "
			+"type integer, "
			+"content_type integer, "
			+"record_id bigint, "
			+"content text, "
			+"click_url text, "
			+"push_time bigint, "
			+"login_account bigint, "
			+"community_id bigint, "
			+"table_version integer)";
	private final String CREATE_INDEX_PUSH_LA = "CREATE INDEX push_record_index on "+
			NeighborsDBOpenHelper.TABLE_PUSH_RECORD+"(login_account)";
	
	private final String CREATE_TABLE_USERS= "CREATE TABLE "+NeighborsDBOpenHelper.TABLE_NAME_USERS+" ("
			+"id integer primary key autoincrement, " 
			+"user_public_status integer default 0, "
			+"user_vocation text, "
			+"user_level text, " 
			+"user_id bigint, " 
			+"user_name text, " 
			+"user_portrait text, "
			+"user_gender integer default 3, "
			+"user_phone_number text, "
			+"user_family_id bigint, " 
			+"user_family_address text, "
			+"user_birthday integer, "
			+"user_email text, "
			+"user_type integer default 0, "
			+"user_time bigint default 0, "
			+"user_json text, "
			+"login_account bigint, "
			+"table_version integer)";
	private final String CREATE_INDEX_USERS_LA = "CREATE INDEX account_index on "+
			NeighborsDBOpenHelper.TABLE_NAME_USERS+"(login_account)";
	private final String CREATE_INDEX_USERS_LU ="CREATE INDEX user_index on "+
			NeighborsDBOpenHelper.TABLE_NAME_USERS+"(login_account, user_id)";
	
	private final String CREATE_TABLE_ALL_FAMILY = "CREATE TABLE "+
			NeighborsDBOpenHelper.TABLE_ALL_FAMILY+" ("
			+"_id integer primary key autoincrement, " 
			+"family_id bigint, "     //家庭住址id
			+"family_name text, "     //家庭住址  楼号、门牌号
			+"family_display_name text, "  //显示的名字
			+"family_address text, "       //居住详细地址
			+"family_address_id bigint, "  //地址id
			+"family_desc text, "   
			+"family_portrait text, "
			+"family_background_color integer default 0, "
			+"family_city text, "         
			+"family_city_id bigint, "
			+"family_city_code text, "   // new add
			+"family_block text, "
			+"family_block_id bigint, "
			+"family_community_id bigint, "    //小区id
			+"family_community text, "         //所属小区
			+"family_community_nickname text, " 
			+"family_building_num text, "      //小区楼号
			+"family_building_id bigint, "
			+"family_apt_num text, "        //门牌号
			+"family_apt_id bigint, "        //门牌号
			+"is_family_member integer default 0, "   
			+"is_attention integer default 0, "
			+"family_member_count integer default 0, "
			+"entity_type integer, " 
			+"ne_status integer, " 
			+"nem_status integer, " 
			+"primary_flag integer default 0, " 
			+"belong_family_id bigint default 0, "  //所属
			+"user_alias text, "     //用户名
			+"user_avatar text, "    //头像
			+"login_account bigint, "   //登陆账户
			+"table_version integer) ";
	private final String CREATE_DOORPLATE = "CREATE TABLE "+
			NeighborsDBOpenHelper.TABLE_DOOR_PLATE+" ("
			+"_id integer primary key autoincrement, " 
			+"doorplate_id bigint, "     //家庭住址id
			+"apt_number intger, "     //家庭住址  楼号、门牌号
			+"displayname text, "  //显示的名字
			+"avatarpath text, "       //居住详细地址
			+"user_count intger, "  //地址id
			+"livingStatus intger, "   
			+"belong_community_id bigint, "
			+"login_account bigint, "
			+"table_version integer) ";
	private final String CREATE_INDEX_DOOR_LA = "CREATE INDEX door_community_index on "+
			NeighborsDBOpenHelper.TABLE_DOOR_PLATE+" (belong_community_id)";
	
	private final String CREATE_INDEX_ALL_FAMILY_LA = "CREATE INDEX family_account_index on "+
			NeighborsDBOpenHelper.TABLE_ALL_FAMILY+"(login_account)";
	private final String CREATE_INDEX_ALL_FAMILY_LU = "CREATE INDEX family_index on "+
			NeighborsDBOpenHelper.TABLE_ALL_FAMILY+"(login_account, belong_family_id)";
	
	private final String CREATE_TABLE_NEIGHBOR = "CREATE TABLE "+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR+" ("
			+"_id integer primary key autoincrement, "
			+"user_id bigint, "
			+"user_name text, "
			+"user_family_id bigint, "
			+"user_portrait text, "
			+"user_phone_number text, "
			+"distance integer, "
			+"briefdesc text, "						// 个性签名
			+"profession text, "					// 职业
			+"addrstatus text, "					// 地址范围 -》是否同楼栋
			+"building_num text, "					// 小区 楼号
			+"aptnum text, "						// 门牌号
			+"belong_family_id bigint default 0, "
			+"data_type int, "
			+"user_type int, "
			+"login_account bigint, "
			+"table_version integer)";
	
	private final String CREATE_TABLE_NEIGHBOR_GROUP = "CREATE TABLE "+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR_GROUP+" (" 
			+"_id integer primary key autoincrement, "
			+"neighbor_group_id bigint, "
			+"neighbor_group_name text, "
			+"neighbor_group_avatar text, "
			+"neighbor_group_creater_id bigint, "
			+"neighbor_group_creater_family_id bigint, "
			+"neighbor_group_type integer default 1, "
			+"neighbor_group_description text, "
			+"neighbor_group_member_count integer, "
			+"neighbor_group_private_flag integer default 1, "
			+"neighbor_group_add_type integer default 0, "
			+"neighbor_group_display_mode integer default 0, "
			+"neighbor_group_follow_flag integer default 0, "
			+"neighbor_group_shield_flag integer default 0, "
			+"neighbor_group_bg_color integer default 0, "
			+"neighbor_group_create_time bigint, "
			+"community_id bigint default 0, "
			+"postPrivilege integer default 0, "
			+"deleteable integer default 1, "
			+"belong_family_id bigint default 0, "
			+"user_display_name text, "
			+"ne_display_name text, "
			+"subscribed_status integer default 0, "
			+"key_words text, "
			+"visible_scope integer default 0, "
			+"category_name text, "
			+"category_id integer, "
			+"manager_flag integer default 0, "
			+"login_account bigint, "
			+"table_version integer)";
	
	private final String CREATE_INDEX_NEIGHBOR_GROUP_LA = "CREATE INDEX neighbor_account_index on "+
			NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR_GROUP+"(login_account)";
	private final String CREATE_INDEX_NEIGHBOR_GROUP_LU ="CREATE INDEX neighbor_belong_family_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_NEIGHBOR_GROUP+"(login_account, belong_family_id)";
	
	//MY
	private final String CREATE_TABLE_FORUM_TOPIC = "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC+" (" 
			+ "_id integer primary key autoincrement, "
			+ "cache_key integer, "
			+ "topic_id bigint UNIQUE, "   //话题id
			+ "forum_id bigint, "          //小区id community
			+ "forum_name text, "          //小区名字
			+ "circle_type integer, "
			+ "sender_nc_role integer, "
			+ "sender_id bigint, "         //发帖者id
			+ "sender_community_id bigint, "
			+ "sender_name text, "         //发帖者名字
			+ "sender_lever text, "
			+ "sender_portrait text, "     //发帖者头像
			+ "sender_family_id bigint, "  //发帖者
			+ "sender_family_address text, "
			+ "display_name text, "         //标题下面显示
			+ "topic_time bigint, "         //
			+ "topic_title text, "          //话题标题
			+ "topic_content text, "        //话题内容
			+ "topic_category_type integer, "    //
			+ "comment_num integer default 0, " //回复数
			+ "like_num integer default 0, "    //赞的人数
			+ "send_status integer default 0, " //
			+ "like_status integer default 0, "  //是否已赞
			+ "visiable_type integer, "
			+ "login_account bigint, "
			+ "topic_url text, "
			+ "forward_path text, "
			+ "forward_refer_id integer default 0, "
			+ "object_type integer default 0, "
			+ "object_data text, "
			+ "hot_flag text, "
			+ "view_num integer, "        //被查看次数
			+ "media_files_json text, "
			+ "comments_summary text, "   //话题的所有内容
			+ "table_version integer)";
	
	private final String CREATE_INDEX_FORUM_TOPIC_LA = "CREATE INDEX forum_account_index on " + 
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + "(login_account)";
	private final String CREATE_INDEX_FORUM_TOPIC_LU = "CREATE INDEX forum_index on " +
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + "(login_account, forum_id)";
	private final String CREATE_INDEX_FORUM_TOPIC_LC = "CREATE INDEX forum_cache_index on " + 
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + "(login_account, cache_key)";
	private final String CREATE_TRIGGER_FORUM_TOPIC = " CREATE TRIGGER trigger_delete_topic DELETE ON " + 
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_TOPIC + " BEGIN DELETE FROM " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_COMMENT +
			"WHERE login_account = old.login_account AND cache_key = old.cache_key AND topic_id = old.topic_id; DELETE FROM " +
			NeighborsDBOpenHelper.TABLE_NAME_FORUM_MEDIA + "WHERE login_account = old.login_account AND object_id = old._id; END";

	private final String CREATE_TABLE_FORUM_MEDIA = "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_MEDIA +" ("
			+ "_id integer primary key autoincrement, "
			+ "cache_key integer, "
			+ "object_id bigint, "
			+ "object_main_id integer, "
			+ "object_type integer, "
			+ "media_type integer default 0, "
			+ "media_url text, "
			+ "media_file_key text, "
			+ "title text, "
			+ "description text, "
			+ "link text, "
			+ "send_status integer default 0, "
			+ "login_account bigint, "
			+ "table_version integer)";
	private final String CREATE_INDEX_FORUM_MEDIA_LA = "CREATE INDEX media_account_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_FORUM_MEDIA+"(login_account)";
	
	private final String CREATE_TABLE_FORUM_COMMENT = "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_NAME_FORUM_COMMENT + " ("
			+ "_id integer primary key autoincrement, "
			+ "cache_key integer, "
			+ "comment_id bigint, "
			+ "topic_id bigint, "
			+ "sender_nc_role_id integer, "
			+ "sender_id bigint, "
			+ "sender_name text, "
			+ "sender_portrait text, "
			+ "sender_family_id bigint, "
			+ "sender_family_address text, "
			+ "sender_level text, "
			+ "display_name text, "
			+ "comment_content text, "
			+ "comment_time bigint, "
			+ "comment_image_url text, "
			+ "comment_content_type integer default 0, "
			+ "send_status integer default 0, "
			+ "read_status integer default 0, "
			+ "login_account bigint, "
			+ "media_type integer default 0, "
			+ "media_url text, "
			+ "table_version integer)";
	private final String CREATE_INDEX_FORUM_COMMENT_LA = "CREATE INDEX comment_account_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_FORUM_COMMENT+"(login_account)";
	
	private final String CREATE_TABLE_ALL_NOTE = "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_NAME_ALL_NOTE + " ("
			+ "note_id bigint, "
			+ "note_content_type integer default 0, "
			+ "note_content text, "
			+ "note_time timestamp, "
			+ "note_res_send_type integer default 0, "
			+ "note_send_status integer default 1, "
			+ "note_read_status integer default 0, "
			+ "note_is_local integer default 0, "
			+ "note_object_type integer, "
			+ "note_object_id bigint, "
			+ "note_sender_family_id integer, "
			+ "note_sender_id integer, "
			+ "belong_family_id bigint default 0, "
			+ "login_account bigint, "
			+ "table_version integer)";
	private final String CREATE_INDEX_ALL_NOTE_LA = "CREATE INDEX note_account_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_ALL_NOTE+"(login_account)";
	private final String CREATE_INDEX_ALL_NOTE_LU = "CREATE INDEX note_belong_family_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_ALL_NOTE+"(login_account, belong_family_id)";
	private final String CREATE_INDEX_ALL_NOTE_LC = "CREATE INDEX note_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_ALL_NOTE+"(login_account, belong_family_id, note_object_type, note_object_id)";
	
	private final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE " + NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY + " ("
			+ "_id integer primary key autoincrement, "
			+ "search_type integer, "
			+ "search_object_id bigint, "
			+ "search_word text, "
			+ "search_time bigint, "
			+ "login_account bigint, "
			+ "table_version integer)";
	private final String CREATE_INDEX_SEARCH_HISTORY_LA = "CREATE INDEX search_history_index on "
			+NeighborsDBOpenHelper.TABLE_NAME_SEARCH_HISTORY+"(login_account)";
	
	public NeighborsDBOpenHelper(Context context, CursorFactory factory) {
		super(context, DB_NAME, factory, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_USERS);
		db.execSQL(CREATE_INDEX_USERS_LA);
		db.execSQL(CREATE_INDEX_USERS_LU);
		
		db.execSQL(CREATE_DOORPLATE);
		db.execSQL(CREATE_INDEX_DOOR_LA);
		
		db.execSQL(CREATE_TABLE_ALL_FAMILY);
		db.execSQL(CREATE_INDEX_ALL_FAMILY_LA);
		db.execSQL(CREATE_INDEX_ALL_FAMILY_LU);
		
		db.execSQL(CREATE_TABLE_NEIGHBOR);
		
		db.execSQL(CREATE_TABLE_NEIGHBOR_GROUP);
		db.execSQL(CREATE_INDEX_NEIGHBOR_GROUP_LA);
		db.execSQL(CREATE_INDEX_NEIGHBOR_GROUP_LU);
		
		db.execSQL(CREATE_TABLE_FORUM_MEDIA);
		db.execSQL(CREATE_INDEX_FORUM_MEDIA_LA);
		
		db.execSQL(CREATE_TABLE_FORUM_COMMENT);
		db.execSQL(CREATE_INDEX_FORUM_COMMENT_LA);
		
		db.execSQL(CREATE_TABLE_FORUM_TOPIC);
		db.execSQL(CREATE_INDEX_FORUM_TOPIC_LA);
		db.execSQL(CREATE_INDEX_FORUM_TOPIC_LU);
		db.execSQL(CREATE_INDEX_FORUM_TOPIC_LC);
//		db.execSQL(CREATE_TRIGGER_FORUM_TOPIC);
		
		db.execSQL(CREATE_TABLE_ALL_NOTE);
		db.execSQL(CREATE_INDEX_ALL_NOTE_LA);
		db.execSQL(CREATE_INDEX_ALL_NOTE_LC);
		db.execSQL(CREATE_INDEX_ALL_NOTE_LU);
		
		db.execSQL(CREATE_TABLE_SEARCH_HISTORY);
		db.execSQL(CREATE_INDEX_SEARCH_HISTORY_LA);
		
		db.execSQL(CREATE_TABLE_PUSH_RECORD);
		db.execSQL(CREATE_INDEX_PUSH_LA);
		
		db.execSQL(CREATE_TABLE_NEWS_RECEIVE);
		db.execSQL(CREATE_INDEX_NEWS_RECEIVE_LA);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Loger.i("TEST", "当前数据库版本号->"+oldVersion+"   新版本号->"+newVersion); 
	}

}
