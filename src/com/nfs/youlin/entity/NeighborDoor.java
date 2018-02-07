package com.nfs.youlin.entity;

import android.content.Context;

public class NeighborDoor {
	private Long doorplate_id;
	private int apt_number;
	private String diaplay_name;
	private String avatarpath;
	private int user_count;
	private int living_Status;
	private Long belong_community_id;
	private Long login_account;
	private Context context;
	public NeighborDoor(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public Long get_doorplate_id() {
		return doorplate_id;
	}
	public void set_doorplate_id(Long doorplate_id){
		this.doorplate_id = doorplate_id;
	}
	public int get_apt_number(){
		return this.apt_number;
	}
	public void set_apt_number(int apt_number){
		this.apt_number = apt_number;
	}
	public String get_diaplay_name(){
		return diaplay_name;
	}
	public void set_diaplay_name(String diaplay_name){
		this.diaplay_name = diaplay_name;
	}
	public String get_avatarpath(){
		return avatarpath;
	}
	public void set_avatarpath(String avatarpath){
		this.avatarpath = avatarpath;
	}
	public int get_user_count(){
		return this.user_count;
	}
	public void set_user_count(int user_count){
		this.user_count = user_count;
	}
	public int get_living_Status(){
		return living_Status;
	}
	public void set_living_Status(int living_Status){
		this.living_Status=living_Status;
	}
	public Long get_belong_community_id(){
		return belong_community_id;
	}
	public void set_belong_community_id(Long belong_community_id){
		this.belong_community_id = belong_community_id;
	}
	public Long get_login_account(){
		return login_account;
	}
	public void set_login_account(Long login_account){
		this.login_account = login_account;
	}
}
