package com.nfs.youlin.entity;

import android.content.Context;

public class ForumComment {
	private int cache_key 				 = -1;
	private int send_status 			 = 0;
	private int read_status 			 = 0;
	private int media_type 				 = 0;
	private int sender_nc_role_id 		 = -1;
	private int comment_content_type 	 = 0;
	private long comment_id 			 = -1;
	private long topic_id  				 = -1;
	private long sender_id  			 = -1;
	private long comment_time 			 = -1;
	private long login_account 			 = -1;
	private long sender_family_id  		 = -1;
	private String sender_name 			 = null;
	private String media_url 			 = null;
	private String sender_family_address = null;
	private String sender_portrait       = null;
	private String sender_level          = null;
	private String display_name          = null;
	private String comment_content       = null;
	private String comment_image_url 	 = null;
	
	private Context context;
	
	public ForumComment(Context context){
		this.context = context;
	}

	public int getCache_key() {
		return cache_key;
	}

	public void setCache_key(int cache_key) {
		this.cache_key = cache_key;
	}

	public int getSend_status() {
		return send_status;
	}

	public void setSend_status(int send_status) {
		this.send_status = send_status;
	}

	public int getRead_status() {
		return read_status;
	}

	public void setRead_status(int read_status) {
		this.read_status = read_status;
	}

	public int getMedia_type() {
		return media_type;
	}

	public void setMedia_type(int media_type) {
		this.media_type = media_type;
	}

	public int getSender_nc_role_id() {
		return sender_nc_role_id;
	}

	public void setSender_nc_role_id(int sender_nc_role_id) {
		this.sender_nc_role_id = sender_nc_role_id;
	}

	public int getComment_content_type() {
		return comment_content_type;
	}

	public void setComment_content_type(int comment_content_type) {
		this.comment_content_type = comment_content_type;
	}

	public long getComment_id() {
		return comment_id;
	}

	public void setComment_id(long comment_id) {
		this.comment_id = comment_id;
	}

	public long getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(long topic_id) {
		this.topic_id = topic_id;
	}

	public long getSender_id() {
		return sender_id;
	}

	public void setSender_id(long sender_id) {
		this.sender_id = sender_id;
	}

	public long getComment_time() {
		return comment_time;
	}

	public void setComment_time(long comment_time) {
		this.comment_time = comment_time;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public long getSender_family_id() {
		return sender_family_id;
	}

	public void setSender_family_id(long sender_family_id) {
		this.sender_family_id = sender_family_id;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getMedia_url() {
		return media_url;
	}

	public void setMedia_url(String media_url) {
		this.media_url = media_url;
	}

	public String getSender_family_address() {
		return sender_family_address;
	}

	public void setSender_family_address(String sender_family_address) {
		this.sender_family_address = sender_family_address;
	}

	public String getSender_portrait() {
		return sender_portrait;
	}

	public void setSender_portrait(String sender_portrait) {
		this.sender_portrait = sender_portrait;
	}

	public String getSender_level() {
		return sender_level;
	}

	public void setSender_level(String sender_level) {
		this.sender_level = sender_level;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getComment_content() {
		return comment_content;
	}

	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}

	public String getComment_image_url() {
		return comment_image_url;
	}

	public void setComment_image_url(String comment_image_url) {
		this.comment_image_url = comment_image_url;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
