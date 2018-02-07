package com.nfs.youlin.entity;

import com.baidu.platform.comapi.map.B;

import android.content.Context;

public class ForumTopic {

	private int _id = -1;
	private int cache_key = -1;
	private int circle_type = -1;   
	private int sender_nc_role = -1;
	private int topic_category_type = -1;
	private int comment_num = 0;
	private int like_num = 0;
	private int send_status = 0;
	private int like_status = 0;
	private int visiable_type = -1;
	private int forward_refer_id = 0;
	private int object_type = 0;
	private int hot_flag = -1;
	private int view_num = -1;
	private String topic_delflag = "0";
	private long topic_id = -1;
	private long forum_id = -1;
	private long sender_id = -1;
	private long sender_family_id = -1;
	private long sender_community_id = -1;
	private long topic_time = -1;
	private long login_account = -1;
	
	private String forum_name = null;
	private String sender_name = null;
	private String sender_lever = null;
	private String sender_portrait = null;
	private String sender_family_address = null;
	private String display_name = null;
	private String topic_title = null;
	private String topic_content = null;
	private String topic_url = null;
	private String forward_path = null;
	private String object_data = null;
	private String meadia_files_json = null;
	private String comments_summary = null;
	private String user_address = null; //报修新加
	private long user_phone = -1; //  报修新加
	private Context context;
	private Boolean flag;
	public ForumTopic(Context context)
	{
		this.context = context;
	}
	public void setdeleteflag(String flag){
		topic_delflag = flag;
	}
	public String getdeleteflag(){
		return topic_delflag;
	}
	public long getSender_community_id() {
		return sender_community_id;
	}

	public void setSender_community_id(long sender_community_id) {
		this.sender_community_id = sender_community_id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getCache_key() {
		return cache_key;
	}

	public void setCache_key(int chache) {
		this.cache_key = chache;
	}

	public int getCircle_type() {
		return circle_type;
	}

	public void setCircle_type(int circle_type) {
		this.circle_type = circle_type;
	}

	public int getSender_nc_role() {
		return sender_nc_role;
	}

	public void setSender_nc_role(int sender_nc_role) {
		this.sender_nc_role = sender_nc_role;
	}

	public int getTopic_category_type() {
		return topic_category_type;
	}

	public void setTopic_category_type(int topic_category_type) {
		this.topic_category_type = topic_category_type;
	}

	public int getComment_num() {
		return comment_num;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

	public int getLike_num() {
		return like_num;
	}

	public void setLike_num(int like_num) {
		this.like_num = like_num;
	}
	public long getPhone_num() {
		return user_phone;
	}

	public void setPhone_num(long user_phone) {
		this.user_phone = user_phone;
	}
	public int getSend_status() {
		return send_status;
	}

	public void setSend_status(int send_status) {
		this.send_status = send_status;
	}

	public int getLike_status() {
		return like_status;
	}

	public void setLike_status(int like_status) {
		this.like_status = like_status;
	}

	public int getVisiable_type() {
		return visiable_type;
	}

	public void setVisiable_type(int visiable_type) {
		this.visiable_type = visiable_type;
	}

	public int getForward_refer_id() {
		return forward_refer_id;
	}

	public void setForward_refer_id(int forward_refer_id) {
		this.forward_refer_id = forward_refer_id;
	}

	public int getObject_type() {
		return object_type;
	}

	public void setObject_type(int object_type) {
		this.object_type = object_type;
	}
	public String getuser_address() {
		return user_address;
	}

	public void setuser_address(String user_address) {
		this.user_address = user_address;
	}
	public int getHot_flag() {
		return hot_flag;
	}

	public void setHot_flag(int hot_flag) {
		this.hot_flag = hot_flag;
	}

	public int getView_num() {
		return view_num;
	}

	public void setView_num(int view_num) {
		this.view_num = view_num;
	}

	public long getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(long topic_id) {
		this.topic_id = topic_id;
	}

	public long getForum_id() {
		return forum_id;
	}

	public void setForum_id(long forum_id) {
		this.forum_id = forum_id;
	}

	public long getSender_id() {
		return sender_id;
	}

	public void setSender_id(long sender_id) {
		this.sender_id = sender_id;
	}

	public long getSender_family_id() {
		return sender_family_id;
	}

	public void setSender_family_id(long sender_family_id) {
		this.sender_family_id = sender_family_id;
	}

	public long getTopic_time() {
		return topic_time;
	}

	public void setTopic_time(long topic_time) {
		this.topic_time = topic_time;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public String getForum_name() {
		return forum_name;
	}

	public void setForum_name(String forum_name) {
		this.forum_name = forum_name;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getSender_lever() {
		return sender_lever;
	}

	public void setSender_lever(String sender_lever) {
		this.sender_lever = sender_lever;
	}

	public String getSender_portrait() {
		return sender_portrait;
	}

	public void setSender_portrait(String sender_portrait) {
		this.sender_portrait = sender_portrait;
	}

	public String getSender_family_address() {
		return sender_family_address;
	}

	public void setSender_family_address(String sender_family_address) {
		this.sender_family_address = sender_family_address;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getTopic_title() {
		return topic_title;
	}

	public void setTopic_title(String topic_title) {
		this.topic_title = topic_title;
	}

	public String getTopic_content() {
		return topic_content;
	}

	public void setTopic_content(String topic_content) {
		this.topic_content = topic_content;
	}

	public String getTopic_url() {
		return topic_url;
	}

	public void setTopic_url(String topic_url) {
		this.topic_url = topic_url;
	}

	public String getForward_path() {
		return forward_path;
	}

	public void setForward_path(String forward_path) {
		this.forward_path = forward_path;
	}

	public String getObject_data(){
		return object_data;
	}
	
	public void setObject_data(String object_data){
		this.object_data = object_data;
	}
	
	public String getMeadia_files_json() {
		return meadia_files_json;
	}

	public void setMeadia_files_json(String meadia_files_json) {
		this.meadia_files_json = meadia_files_json;
	}

	public String getComments_summary() {
		return comments_summary;
	}

	public void setComments_summary(String comments_summary) {
		this.comments_summary = comments_summary;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	public void setFlag(Boolean flag){
		this.flag = flag;
	}
	public boolean getFlag(){
		return flag;
	}
}
