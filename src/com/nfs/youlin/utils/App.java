package com.nfs.youlin.utils;

import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;

public class App extends Application {
	// 用于存放倒计时时间
	public static String shareStr="我正在使用优邻App，帮您快速方便地找到邻居，分享家庭闲置工具，物业通知。";
	public static Map<String, Long> map;
	public static Map<String, Long> forgetMap;
	public static int sUserType = -1;
	public static long sUserLoginId = -1;
	public static String sNewPushRecordCount = null;
	public static String sUserPhone = null;
	public static String USER_INFOMATION = "user_info";
	public static String SMS_VERIFICATION_USER = "sms_verification_user";
	public static String REGISTERED_USER = "registered_user";
	public static String VIBRATION = "vibration_status";
	public static String VOICE = "voice_status";
	public static String ENTITY_STATUS = "addr_state";
	public static String ADDRESSOCCUP = "address_occup";
	public static String imei = null;
	public static List<String> pushTags = null;
	public static boolean sDeleteTagFinish = false;
	public static boolean sDeviceLoginStatus = false;
//	public static boolean sCommStatus = false;
	public static final String PUSH_ALIAS_SUFFIX = "youlin_tag_";
	public static final String PUSH_TAG_COMMUNITY_TOPIC = "community_topic_"; //普通用户群组
	public static final String PUSH_TITLE_NEW_TOPIC = "push_new_topic";
	public static final String PUSH_TITLE_DEL_TOPIC = "push_del_topic";
	public static final String PUSH_TITLE_NEW_COMMENT = "push_new_comm";
	public static final String PUSH_TITLE_DEL_COMMENT = "push_del_comm";
	public static final String PUSH_COMM_REPORT_ADMIN = "push_community_admin";
	public static final String PUSH_TAG_COMM_REPORT_ADMIN = "community_admin_";//管理员群组
	public static final String PUSH_TAG_PROPERTY_ADMIN = "property_notice_";//物业群组
	public static final String PUSH_PROPERTY_NEW_NOTICE = "push_new_notice";
	public static final String PUSH_PROPERTY_NEW_REPAIR = "push_new_repair";
	public static final String PUSH_PROPERTY_RUN_REPAIR = "push_run_repair";
	public static final String PUSH_ADD_BLACK = "push_add_black";
	public static final String PUSH_DEL_BLACK = "push_del_black";
	public static final String PUSH_UPDATE_TOPIC = "push_update_topic";
	public static final String PUSH_UPDATE_PASSWD = "push_new_passwd";
	public static final String PUSH_COMMUNITY_NEWS = "push_community_news";
	public static final String PUSH_FORCE_RETURN = "push_force_return";
	public static final String PUSH_NEWS_NOTICE = "push_news_";
	public static String UPDATE_APK_PORSITION = "";
	public static boolean UPDATE_APK_STATUS = false;
	public static int UPDATE_APK_TYPE = 0;
	//nfs-hlj#youlinapp  //197:  nfs-hlj#neighborschat
	//64de302e0f10c70af07b0ed4   //4fe1b4a29de6baae388574b0
	public static Long sFamilyId = 00000000000L;
	public static Long sFamilyCommunityId = 0000000000L;
	public static Long sFamilyBlockId = 0000000000L;
	public static Long sUserAppTime = 0000000000L;
	public static final long WAITFORHTTPTIME = 4000;
	public static String sPushUserID = null;
	public static String sPushChannelID = null;
	public static String sPhoneIMEI = null;
	public static final String sAdminInvitationCode = "";
	public static final String sPropertyInvitationCode = "";
	public static boolean sLoadNewTopicStatus = false;
	public static int NORMAL_TYPE = 2;
	public static int GONGGAO_TYPE = 3;
	public static int BAOXIU_TYPE = 4;
	public static int JIANYI_TYPE = 5;
	public static long CurrentSysTime;
	public static boolean Detailactivityisshown = false;
	public static void setAddrStatus(Context context, int nStatus){
		Editor sharedata = context.getSharedPreferences(App.ENTITY_STATUS,Context.MODE_PRIVATE).edit();
		sharedata.putInt("entity_type", nStatus);
		sharedata.commit();
	}
	
	public static int getAddrStatus(Context context){
		SharedPreferences sharedata = context.getSharedPreferences(App.ENTITY_STATUS, Context.MODE_PRIVATE);
		return sharedata.getInt("entity_type", 0);
	}
	public static void setNoAddrStatus(Context context, Boolean bStatus){
		Editor sharedata = context.getSharedPreferences(App.ENTITY_STATUS,Context.MODE_PRIVATE).edit();
		sharedata.putBoolean("entity_type_no", bStatus);
		sharedata.commit();
	}
	
	public static Boolean getNoAddrStatus(Context context){
		SharedPreferences sharedata = context.getSharedPreferences(App.ENTITY_STATUS, Context.MODE_PRIVATE);
		return sharedata.getBoolean("entity_type_no",true);
	}
	public static void setNewsRecviceStatus(Context context, boolean bStatus){
		Editor sharedata = context.getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putBoolean("new_receive", bStatus);
		sharedata.commit();
	}
	
	public static boolean getNewsRecviceStatus(Context context){
		SharedPreferences sharedata = context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getBoolean("new_receive", false);
	}
	public static DisplayImageOptions  options_error = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.bg_error) //设置图片在下载期间显示的图片  
            .showImageForEmptyUri(R.drawable.bg_error)//设置图片Uri为空或是错误的时候显示的图片  
            .showImageOnFail(R.drawable.bg_error)
            .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
            //.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
            .build();
	public static DisplayImageOptions  options_error_no_memory = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.bg_error) //设置图片在下载期间显示的图片  
            .showImageForEmptyUri(R.drawable.bg_error)//设置图片Uri为空或是错误的时候显示的图片  
            .showImageOnFail(R.drawable.bg_error)
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
            //.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
            .build();
	public static DisplayImageOptions options_account = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.account) //设置图片在下载期间显示的图片  
			.showImageForEmptyUri(R.drawable.account)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(R.drawable.account)
			.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
			//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
			.build();
	public static DisplayImageOptions options_news_pic = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bg_news_pic) //设置图片在下载期间显示的图片  
			.showImageForEmptyUri(R.drawable.bg_news_pic)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(R.drawable.bg_news_pic)
			.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
			//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
			.build();
	public static DisplayImageOptions options_plugin_camera_no_pictures = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.plugin_camera_no_pictures) //设置图片在下载期间显示的图片  
			.showImageForEmptyUri(R.drawable.plugin_camera_no_pictures)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(R.drawable.plugin_camera_no_pictures)
			.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
			//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
			.build();
	public static DisplayImageOptions options_weather = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bg_error) //设置图片在下载期间显示的图片  
			.showImageForEmptyUri(R.drawable.bg_error)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(R.drawable.bg_error)
			.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
			.bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型//  
			//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
			.build();
	public static DisplayImageOptions options_blackava = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.blackava) //设置图片在下载期间显示的图片  
			.showImageForEmptyUri(R.drawable.blackava)//设置图片Uri为空或是错误的时候显示的图片  
			.showImageOnFail(R.drawable.blackava)
			.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
			//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
			.build();
	public static DisplayImageOptions options_picture_head = new DisplayImageOptions.Builder()
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.NONE)//设置图片以如何的编码方式显示  
			.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
			//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
			.build();
}
