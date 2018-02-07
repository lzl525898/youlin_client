package com.nfs.youlin.entity;

import android.content.Context;

public class ForumMedia {
	private int _id 			  = -1;
	private int cache_key 		  = -1;
	private int media_type 		  = 0;
	private int object_type 	  = -1;
	private int send_status 	  = 0;
	private int object_main_id    = -1;
	
	private long object_id 		  = -1;
	private long login_account    = -1;
	
	private String link  		  = null;
	private String title 		  = null;
	private String media_url 	  = null;
	private String description    = null;
	private String media_file_key = null;
	
	private Context context;
	
	public ForumMedia(Context context){
		this.context = context;
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

	public void setCache_key(int cache_key) {
		this.cache_key = cache_key;
	}

	public int getMedia_type() {
		return media_type;
	}

	public void setMedia_type(int media_type) {
		this.media_type = media_type;
	}

	public int getObject_type() {
		return object_type;
	}

	public void setObject_type(int object_type) {
		this.object_type = object_type;
	}

	public int getSend_status() {
		return send_status;
	}

	public void setSend_status(int send_status) {
		this.send_status = send_status;
	}

	public int getObject_main_id() {
		return object_main_id;
	}

	public void setObject_main_id(int object_main_id) {
		this.object_main_id = object_main_id;
	}

	public long getObject_id() {
		return object_id;
	}

	public void setObject_id(long object_id) {
		this.object_id = object_id;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMedia_url() {
		return media_url;
	}

	public void setMedia_url(String media_url) {
		this.media_url = media_url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMedia_file_key() {
		return media_file_key;
	}

	public void setMedia_file_key(String media_file_key) {
		this.media_file_key = media_file_key;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
