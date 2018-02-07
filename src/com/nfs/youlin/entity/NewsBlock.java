package com.nfs.youlin.entity;

import android.content.Context;

public class NewsBlock {
	private int news_first_flag=-1;
	private String news_title="";
	private String news_pic="";
	private String news_url="";
	private long news_belongs=-1;
	private int news_id=-1;
	private long news_send_time=-1;
	private long news_push_time=-1;
	
	private String news_others;
	private Context context;
	public NewsBlock(Context context){
		this.context = context;
	}
	
	
	public void setfirstflag(int news_first_flag){
		this.news_first_flag = news_first_flag;
	}
	public int getfirstflag(){
		return news_first_flag;
	}
	public void setnewsothers(String news_others){
		this.news_others = news_others;
	}
	public String getnewsothers(){
		return news_others;
	}
	public void setnewstitle(String news_title){
		this.news_title = news_title;
	}
	public String getnewstitle(){
		return news_title;
	}
	public void setnewspic(String news_pic){
		this.news_pic = news_pic;
	}
	public String getnewspic(){
		return news_pic;
	}
	public void setnewsurl(String news_url){
		this.news_url = news_url;
	}
	public String getnewsurl(){
		return news_url;
	}
	public void setnewsbelong(long news_belongs){
		this.news_belongs = news_belongs;
	}
	public long getnewsbelong(){
		return news_belongs;
	}
	public void setnewsid(int news_id){
		this.news_id = news_id;
	}
	public int getnewsid(){
		return news_id;
	}
	public void setnewssendtime(long news_send_time){
		this.news_send_time = news_send_time;
	}
	public long getnewssendtime(){
		return news_send_time;
	}
	public void setnewspushtime(long news_push_time){
		this.news_push_time = news_push_time;
	}
	public long getnewspushtime(){
		return news_push_time;
	}
}
