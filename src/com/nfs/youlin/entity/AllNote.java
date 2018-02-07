package com.nfs.youlin.entity;

import android.content.Context;

public class AllNote {
	private int note_content_type     = 0;
	private int note_res_send_type    = 0;
	private int note_send_status      = 1;
	private int note_read_status      = 0;
	private int note_is_local         = 0;
	private int note_object_type      = -1;
	private int note_sender_family_id = -1;
	private int note_sender_id        = -1;
	
	private long note_id              = -1;
	private long note_object_id       = -1;
	private long belong_family_id     = 0;
	private long login_account        = -1;
	
	private String note_content       = null;
	private String note_time          = null;
	
	private Context context;
	
	public AllNote(Context context){
		this.context = context;
	}

	public int getNote_content_type() {
		return note_content_type;
	}

	public void setNote_content_type(int note_content_type) {
		this.note_content_type = note_content_type;
	}

	public int getNote_res_send_type() {
		return note_res_send_type;
	}

	public void setNote_res_send_type(int note_res_send_type) {
		this.note_res_send_type = note_res_send_type;
	}

	public int getNote_send_status() {
		return note_send_status;
	}

	public void setNote_send_status(int note_send_status) {
		this.note_send_status = note_send_status;
	}

	public int getNote_read_status() {
		return note_read_status;
	}

	public void setNote_read_status(int note_read_status) {
		this.note_read_status = note_read_status;
	}

	public int getNote_is_local() {
		return note_is_local;
	}

	public void setNote_is_local(int note_is_local) {
		this.note_is_local = note_is_local;
	}

	public int getNote_object_type() {
		return note_object_type;
	}

	public void setNote_object_type(int note_object_type) {
		this.note_object_type = note_object_type;
	}

	public int getNote_sender_family_id() {
		return note_sender_family_id;
	}

	public void setNote_sender_family_id(int note_sender_family_id) {
		this.note_sender_family_id = note_sender_family_id;
	}

	public int getNote_sender_id() {
		return note_sender_id;
	}

	public void setNote_sender_id(int note_sender_id) {
		this.note_sender_id = note_sender_id;
	}

	public long getNote_id() {
		return note_id;
	}

	public void setNote_id(long note_id) {
		this.note_id = note_id;
	}

	public long getNote_object_id() {
		return note_object_id;
	}

	public void setNote_object_id(long note_object_id) {
		this.note_object_id = note_object_id;
	}

	public long getBelong_family_id() {
		return belong_family_id;
	}

	public void setBelong_family_id(long belong_family_id) {
		this.belong_family_id = belong_family_id;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public String getNote_content() {
		return note_content;
	}

	public void setNote_content(String note_content) {
		this.note_content = note_content;
	}

	public String getNote_time() {
		return note_time;
	}

	public void setNote_time(String note_time) {
		this.note_time = note_time;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
