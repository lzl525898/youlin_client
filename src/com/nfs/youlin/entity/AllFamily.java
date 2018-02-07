package com.nfs.youlin.entity;

import android.content.Context;

public class AllFamily {
	private int    _id 						 = -1;
	private int    entity_type 			     = -1;
	private int    ne_status 			     = -1;
	private int    nem_status 				 = -1;
	private int    is_family_member 		 = 0;
	private int    is_attention 			 = 0;
	private int    family_background_color 	 = 0;
	private int    family_member_count 		 = 0;
	private long   family_id 				 = -1;				// 地址信息唯一id
	private long   family_address_id 		 = -1;				
	private long   family_city_id 			 = -1;				// 城市号
	private long   family_block_id 			 = -1;
	private String   family_city_code 	     = null;
	private long   building_num_id           = -1;
	private long   apt_num_id                = -1;
	
	private long   family_community_id 		 = -1;				// 社区唯一id
	private long   login_account 			 = 0;				
	private long   primary_flag 			 = 0;
	private long   belong_family_id 		 = 0;
	private String family_name 				 = null;			// 单元+门牌
	private String family_display_name 	     = null;
	private String family_address 			 = null;			// 地址详细信息
	private String family_desc 				 = null;
	private String family_portrait 			 = null;
	private String family_city 				 = null;			// 城市名
	private String family_block 			 = null;
	private String family_community 		 = null;			// 社区名称
	private String family_community_nickname = null;			
	private String family_building_num 		 = null;			// 楼栋单元
	private String family_apt_num 			 = null;			// 门牌号
	private String user_alias 				 = null;			// 用户昵称
	private String user_avatar               = null;			// 用户头像
	
	public long getapt_num_id() {
		return apt_num_id;
	}

	public void setapt_num_id(long apt_num_id) {
		this.apt_num_id = apt_num_id;
	}
	
	public long getbuilding_num_id() {
		return building_num_id;
	}

	public void setbuilding_num_id(long building_num_id) {
		this.building_num_id = building_num_id;
	}
	public String getfamily_city_code() {
		return family_city_code;
	}

	public void setfamily_city_code(String family_city_code) {
		this.family_city_code = family_city_code;
	}
	public String getUser_avatar() {
		return user_avatar;
	}

	public void setUser_avatar(String user_avatar) {
		this.user_avatar = user_avatar;
	}

	private Context context = null;
	
	public AllFamily(Context context){
		this.context = context;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}
	///////////////////
	public int getEntity_type() {
		return entity_type;
	}

	public void setEntity_type(int entity_type) {
		this.entity_type = entity_type;
	}

	public int getNe_status() {
		return ne_status;
	}

	public void setNe_status(int ne_status) {
		this.ne_status = ne_status;
	}

	public int getNem_status() {
		return nem_status;
	}

	public void setNem_status(int nem_status) {
		this.nem_status = nem_status;
	}

	public int getIs_family_member() {
		return is_family_member;
	}

	public void setIs_family_member(int is_family_member) {
		this.is_family_member = is_family_member;
	}

	public int getIs_attention() {
		return is_attention;
	}

	public void setIs_attention(int is_attention) {
		this.is_attention = is_attention;
	}

	public int getFamily_background_color() {
		return family_background_color;
	}

	public void setFamily_background_color(int family_background_color) {
		this.family_background_color = family_background_color;
	}

	public int getFamily_member_count() {
		return family_member_count;
	}

	public void setFamily_member_count(int family_member_count) {
		this.family_member_count = family_member_count;
	}

	public long getFamily_id() {
		return family_id;
	}

	public void setFamily_id(long family_id) {
		this.family_id = family_id;
	}

	public long getFamily_address_id() {
		return family_address_id;
	}

	public void setFamily_address_id(long family_address_id) {
		this.family_address_id = family_address_id;
	}

	public long getFamily_city_id() {
		return family_city_id;
	}

	public void setFamily_city_id(long family_city_id) {
		this.family_city_id = family_city_id;
	}

	public long getFamily_block_id() {
		return family_block_id;
	}

	public void setFamily_block_id(long family_block_id) {
		this.family_block_id = family_block_id;
	}

	public long getFamily_community_id() {
		return family_community_id;
	}

	public void setFamily_community_id(long family_community_id) {
		this.family_community_id = family_community_id;
	}

	public long getLogin_account() {
		return login_account;
	}

	public void setLogin_account(long login_account) {
		this.login_account = login_account;
	}
	//////////////
	public long getPrimary_flag() {
		return primary_flag;
	}

	public void setPrimary_flag(long primary_flag) {
		this.primary_flag = primary_flag;
	}

	public long getBelong_family_id() {
		return belong_family_id;
	}

	public void setBelong_family_id(long belong_family_id) {
		this.belong_family_id = belong_family_id;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		if(family_name == null){
			this.family_name = this.getFamily_building_num() + "-" + this.getFamily_apt_num();
		}else{
			this.family_name = family_name;
		}
	}

	public String getFamily_display_name() {
		return family_display_name;
	}

	public void setFamily_display_name(String family_display_name) {
		this.family_display_name = family_display_name;
	}

	public String getFamily_address() {
		return family_address;
	}

	public void setFamily_address(String family_address) {
		if(family_address == null){
			this.family_address = this.getFamily_city() + this.getFamily_community()
					+ this.getFamily_building_num() +"-"+ this.getFamily_apt_num();
		}else{
			this.family_address = family_address;
		}
	}

	public String getFamily_desc() {
		return family_desc;
	}

	public void setFamily_desc(String family_desc) {
		this.family_desc = family_desc;
	}

	public String getFamily_portrait() {
		return family_portrait;
	}

	public void setFamily_portrait(String family_portrait) {
		this.family_portrait = family_portrait;
	}

	public String getFamily_city() {
		return family_city;
	}

	public void setFamily_city(String family_city) {
		this.family_city = family_city;
	}

	public String getFamily_block() {
		return family_block;
	}

	public void setFamily_block(String family_block) {
		this.family_block = family_block;
	}

	public String getFamily_community() {
		return family_community;
	}

	public void setFamily_community(String family_community) {
		this.family_community = family_community;
	}

	public String getFamily_community_nickname() {
		return family_community_nickname;
	}

	public void setFamily_community_nickname(String family_community_nickname) {
		this.family_community_nickname = family_community_nickname;
	}

	public String getFamily_building_num() {
		return family_building_num;
	}

	public void setFamily_building_num(String family_building_num) {
		this.family_building_num = family_building_num;
	}

	public String getFamily_apt_num() {
		return family_apt_num;
	}

	public void setFamily_apt_num(String family_apt_num) {
		this.family_apt_num = family_apt_num;
	}

	public String getUser_alias() {
		return user_alias;
	}

	public void setUser_alias(String user_alias) {
		this.user_alias = user_alias;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public void saveFamilyInfos(){
		this.setFamily_address(null);
		this.setFamily_name(null);
	}
}
