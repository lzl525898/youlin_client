package com.nfs.youlin.entity;

import android.content.Context;

public class SearchHistory {
	private int search_type = -1;
	private int search_item_id = -1;
	private long search_object_id = -1;
	private long search_time = -1;
	private long login_account = -1;
	
	private String search_word = null;
	
	private Context context;
	
	public SearchHistory(Context context){
		this.context = context;
	}
	public int getSearch_item_id() {
		return search_item_id;
	}
	public void setSearch_item_id(int search_item_id) {
		this.search_item_id = search_item_id;
	}
	public int getSearch_type() {
		return search_type;
	}

	public void setSearch_type(int search_type) {
		this.search_type = search_type;
	}

	public long getSearch_object_id() {
		return search_object_id;
	}

	public void setSearch_object_id(long search_object_id) {
		this.search_object_id = search_object_id;
	}

	public long getSearch_time() {
		return search_time;
	}

	public void setSearch_time(long search_time) {
		this.search_time = search_time;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public String getSearch_word() {
		return search_word;
	}

	public void setSearch_word(String search_word) {
		this.search_word = search_word;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
