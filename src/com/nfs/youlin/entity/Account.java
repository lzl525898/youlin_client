package com.nfs.youlin.entity;

import android.content.Context;

public class Account {
	
	private int    id                  = -1;					
	private int    user_gender 		   = -1;
	private Long   user_family_id 	   = -1L;
	private int    user_public_status  = 4;  	//	0 表示 公开 1 表示不公开
	private int    user_type           = 0;

	private String user_name 		   = null;
	private String user_portrait       = null;
	private String user_phone_number   = null;
	private String user_family_address = null;
	private String user_email 		   = null;
	private String user_vocation       = null;
	private String user_level          = null;
	private String user_json           = null;
	private long   user_id             = -1;	
	private long   user_birthday       = -1;
	private long   login_account 	   = -1;
	private long   user_time           = 0;
	
	private Context context = null;
	
	public Account(Context context){
		this.context = context;
	}
	
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public long getUser_time() {
		return user_time;
	}


	public void setUser_time(long user_time) {
		this.user_time = user_time;
	}


	public int getUser_type() {
		return user_type;
	}

    
	public String getUser_json() {
		return user_json;
	}


	public void setUser_json(String user_json) {
		this.user_json = user_json;
	}


	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}

	public String getUser_level() {
		return user_level;
	}

	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}

	public long getUser_birthday() {
		return user_birthday;
	}

	public void setUser_birthday(long user_birthday) {
		this.user_birthday = user_birthday;
	}

	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public int getUser_gender() {
		return user_gender;
	}
	public void setUser_gender(int user_gender) {
		this.user_gender = user_gender;
	}
	public Long getUser_family_id() {
		return user_family_id;
	}
	public void setUser_family_id(Long user_family_id) {
		this.user_family_id = user_family_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_portrait() {
		return user_portrait;
	}
	public void setUser_portrait(String user_portrait) {
		this.user_portrait = user_portrait;
	}
	public String getUser_phone_number() {
		return user_phone_number;
	}
	public void setUser_phone_number(String user_phone_number) {
		this.user_phone_number = user_phone_number;
	}
	public String getUser_family_address() {
		return user_family_address;
	}
	public void setUser_family_address(String user_family_address) {
		this.user_family_address = user_family_address;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public long getLogin_account() {
		return login_account;
	}
	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public int getUser_public_status() {
		return user_public_status;
	}

	public void setUser_public_status(int user_public_status) {
		this.user_public_status = user_public_status;
	}

	public String getUser_vocation() {
		return user_vocation;
	}

	public void setUser_vocation(String user_vocation) {
		this.user_vocation = user_vocation;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
