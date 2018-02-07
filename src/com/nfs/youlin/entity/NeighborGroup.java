package com.nfs.youlin.entity;

import android.content.Context;

public class NeighborGroup {
	private int 	_id 							 = -1;
	private int 	neighbor_group_type 			 = 1;
	private int 	neighbor_group_member_count 	 = -1;
	private int 	neighbor_group_private_flag 	 = 1;
	private int 	neighbor_group_add_type 		 = 0;
	private int 	neighbor_group_display_mode 	 = 0;
	private int 	neighbor_group_follow_flag		 = 0;
	private int 	neighbor_group_shield_flag 		 = 0;
	private int 	neighbor_group_bg_color 		 = 0;
	private int 	postPrivilege 					 = 0;
	private int 	deleteable 						 = 1;
	private int 	subscribed_status 				 = 0;
	private int 	visible_scope 					 = 0;
	private int 	category_id 					 = 0;
	private int 	manager_flag 					 = 0;
	private long 	community_id 					 = 0;
	private long 	neighbor_group_id 				 = -1;
	private long 	neighbor_group_creater_id 		 = -1;
	private long 	neighbor_group_creater_family_id = -1;
	private long 	neighbor_group_create_time       = -1;
	private long 	belong_family_id 				 = 0;
	private long    login_account 					 = -1;
	private String  neighbor_group_name 			 = null;
	private String  neighbor_group_avatar 			 = null;
	private String  neighbor_group_description 		 = null;
	private String  user_display_name 				 = null;
	private String  ne_display_name 				 = null;
	private String  key_words 						 = null;
	private String  category_name 					 = null;
	private Context context;
	
	public NeighborGroup(Context cnotext){
		this.context = context;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getNeighbor_group_type() {
		return neighbor_group_type;
	}

	public void setNeighbor_group_type(int neighbor_group_type) {
		this.neighbor_group_type = neighbor_group_type;
	}

	public int getNeighbor_group_member_count() {
		return neighbor_group_member_count;
	}

	public void setNeighbor_group_member_count(int neighbor_group_member_count) {
		this.neighbor_group_member_count = neighbor_group_member_count;
	}

	public int getNeighbor_group_private_flag() {
		return neighbor_group_private_flag;
	}

	public void setNeighbor_group_private_flag(int neighbor_group_private_flag) {
		this.neighbor_group_private_flag = neighbor_group_private_flag;
	}

	public int getNeighbor_group_add_type() {
		return neighbor_group_add_type;
	}

	public void setNeighbor_group_add_type(int neighbor_group_add_type) {
		this.neighbor_group_add_type = neighbor_group_add_type;
	}

	public int getNeighbor_group_display_mode() {
		return neighbor_group_display_mode;
	}

	public void setNeighbor_group_display_mode(int neighbor_group_display_mode) {
		this.neighbor_group_display_mode = neighbor_group_display_mode;
	}

	public int getNeighbor_group_follow_flag() {
		return neighbor_group_follow_flag;
	}

	public void setNeighbor_group_follow_flag(int neighbor_group_follow_flag) {
		this.neighbor_group_follow_flag = neighbor_group_follow_flag;
	}

	public int getNeighbor_group_shield_flag() {
		return neighbor_group_shield_flag;
	}

	public void setNeighbor_group_shield_flag(int neighbor_group_shield_flag) {
		this.neighbor_group_shield_flag = neighbor_group_shield_flag;
	}

	public int getNeighbor_group_bg_color() {
		return neighbor_group_bg_color;
	}

	public void setNeighbor_group_bg_color(int neighbor_group_bg_color) {
		this.neighbor_group_bg_color = neighbor_group_bg_color;
	}

	public int getPostPrivilege() {
		return postPrivilege;
	}

	public void setPostPrivilege(int postPrivilege) {
		this.postPrivilege = postPrivilege;
	}

	public long getCommunity_id() {
		return community_id;
	}

	public void setCommunity_id(long community_id) {
		this.community_id = community_id;
	}

	public int getDeleteable() {
		return deleteable;
	}

	public void setDeleteable(int deleteable) {
		this.deleteable = deleteable;
	}

	public int getSubscribed_status() {
		return subscribed_status;
	}

	public void setSubscribed_status(int subscribed_status) {
		this.subscribed_status = subscribed_status;
	}

	public int getVisible_scope() {
		return visible_scope;
	}

	public void setVisible_scope(int visible_scope) {
		this.visible_scope = visible_scope;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public int getManager_flag() {
		return manager_flag;
	}

	public void setManager_flag(int manager_flag) {
		this.manager_flag = manager_flag;
	}

	public long getNeighbor_group_id() {
		return neighbor_group_id;
	}

	public void setNeighbor_group_id(long neighbor_group_id) {
		this.neighbor_group_id = neighbor_group_id;
	}

	public long getNeighbor_group_creater_id() {
		return neighbor_group_creater_id;
	}

	public void setNeighbor_group_creater_id(long neighbor_group_creater_id) {
		this.neighbor_group_creater_id = neighbor_group_creater_id;
	}

	public long getNeighbor_group_creater_family_id() {
		return neighbor_group_creater_family_id;
	}

	public void setNeighbor_group_creater_family_id(
			long neighbor_group_creater_family_id) {
		this.neighbor_group_creater_family_id = neighbor_group_creater_family_id;
	}

	public long getNeighbor_group_create_time() {
		return neighbor_group_create_time;
	}

	public void setNeighbor_group_create_time(long neighbor_group_create_time) {
		this.neighbor_group_create_time = neighbor_group_create_time;
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

	public String getNeighbor_group_name() {
		return neighbor_group_name;
	}

	public void setNeighbor_group_name(String neighbor_group_name) {
		this.neighbor_group_name = neighbor_group_name;
	}

	public String getNeighbor_group_avatar() {
		return neighbor_group_avatar;
	}

	public void setNeighbor_group_avatar(String neighbor_group_avatar) {
		this.neighbor_group_avatar = neighbor_group_avatar;
	}

	public String getNeighbor_group_description() {
		return neighbor_group_description;
	}

	public void setNeighbor_group_description(String neighbor_group_description) {
		this.neighbor_group_description = neighbor_group_description;
	}

	public String getUser_display_name() {
		return user_display_name;
	}

	public void setUser_display_name(String user_display_name) {
		this.user_display_name = user_display_name;
	}

	public String getNe_display_name() {
		return ne_display_name;
	}

	public void setNe_display_name(String ne_display_name) {
		this.ne_display_name = ne_display_name;
	}

	public String getKey_words() {
		return key_words;
	}

	public void setKey_words(String key_words) {
		this.key_words = key_words;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
