package com.nfs.youlin.entity;

import android.content.Context;

public class PushRecord {
	private int    _id           = -1;
	private int    type          = -1;
	private int    content_type  = -1;
	private long   record_id     = 0;
	private String content       = null;
	private String click_url     = null;
	private long   push_time     = -1;
	private long   login_account = -1;
	private long   user_id       = -1;
	private long   community_id  = -1;
	
	private Context context;
	
	public long getUser_id() {
		return user_id;
	}

	public void setCommunity_id(long community_id) {
		this.community_id = community_id;
	}
	
	public long getCommunity_id(){
		return this.community_id;
	}
	
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	
	public PushRecord(Context context){
		this.context = context;
	}
    
	public long getRecord_id() {
		return record_id;
	}

	public void setRecord_id(long record_id) {
		this.record_id = record_id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getContent_type() {
		return content_type;
	}

	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getClick_url() {
		return click_url;
	}

	public void setClick_url(String click_url) {
		this.click_url = click_url;
	}

	public long getPush_time() {
		return push_time;
	}

	public void setPush_time(long push_time) {
		this.push_time = push_time;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
