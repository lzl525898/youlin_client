package com.nfs.youlin.activity;

public class UpdataInfo {
	private String version;
	private String url;
	private String description;
	private String force;
	private String url_server;
	private String apk_size;
	private String apk_detail;
	public String getApk_detail() {
		return apk_detail;
	}
	public void setApk_detail(String apk_detail) {
		this.apk_detail = apk_detail;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setSize(String size){
		apk_size = size;
	}
	public String getSize(){
		return apk_size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getForce() {
		return force;
	}
	public void setForce(String force) {
		this.force = force;
	}
	public String getUrl_server() {
		return url_server;
	}
	public void setUrl_server(String url_server) {
		this.url_server = url_server;
	}
	
}
