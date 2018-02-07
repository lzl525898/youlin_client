package com.nfs.youlin.entity;

import android.content.Context;

public class Neighbor {
	private int     _id               = -1;
	private int     distance          = 0;
	private int     data_type         = 1;
	private int     user_type         = 1;
	private long    user_id           = -1;
	private long    login_account     = -1;
	private long    belong_family_id  = 0;
	private long    family_id         = 0;
	private String  user_name         = null;
	private String  user_portrait     = null;
	private String  user_phone_number = null;
	private String  briefdesc         = null;
	private String  profession        = "";
	private String  addrstatus        = "0";
	private String  building_num      = null;
	private String  aptnum            = null;
	private Context context;
	
	public Neighbor(Context context){
		this.context = context;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getData_type() {
		return data_type;
	}

	public void setData_type(int data_type) {
		this.data_type = data_type;
	}
	public int getUser_type() {
		return user_type;
	}

	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}

	public long getBelong_family_id() {
		return belong_family_id;
	}

	public void setBelong_family_id(long belong_family_id) {
		this.belong_family_id = belong_family_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public long getUser_family_id() {
		return family_id;
	}

	public void setUser_family_id(long family_id) {
		this.family_id = family_id;
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

	public String getBriefdesc() {
		return briefdesc;
	}

	public void setBriefdesc(String briefdesc) {
		this.briefdesc = briefdesc;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getAddrstatus() {
		return addrstatus;
	}

	public void setAddrstatus(String addrstatus) {
		this.addrstatus = addrstatus;
	}

	public String getBuilding_num() {
		return building_num;
	}

	public void setBuilding_num(String building_num) {
		this.building_num = building_num;
	}

	public String getAptnum() {
		return aptnum;
	}

	public void setAptnum(String aptnum) {
		this.aptnum = aptnum;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
