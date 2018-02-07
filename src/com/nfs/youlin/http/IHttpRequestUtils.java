package com.nfs.youlin.http;

public interface IHttpRequestUtils {
	public final  int TIME_OUT = 4000;
	public final  String SRV_URL                 = "123.57.9.62";    //"www.baidu.com";//"123.57.9.62"//"172.16.50.179"
	public final  String URL 					 = "https://123.57.9.62/";  //123.57.9.62 //172.16.50.186  //172.16.50.197 //123.56.181.71 //www.youlinzj.com
	public final  String YOULIN                  = "youlin/api1.0/";
	public final  String[] APITYPE               = {"users","comsrv","address","feedback","apiproperty","comm","push"};
	public final  String HTTP_URL				 = "http://123.57.9.62/";  
	public final  String DOWN_URL                = HTTP_URL+"yl";
	public final  String WWW                     = "www.youlinzj.com";
	public final  String XINGZUOURL				 = URL+"media/youlin/res/default/xingzuo/";
	public void setHttpUrl(String url);
}
