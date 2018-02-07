/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ListView;

import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CustomActivityOnCrash;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;


public class DemoApplication extends Application {

	public static Context applicationContext;
	private static DemoApplication instance;
//	public dbadapter addressadapter =  new dbadapter(this);
//	  public  List<Map<String, Object>> addresslist = new ArrayList<Map<String, Object>>();
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	private ProgressDialog  updatapd1;
	public void setupdateDialog(ProgressDialog pd1){
	    updatapd1 = pd1;
	}
	public ProgressDialog getupdateDialog(){
	    return updatapd1;
	}
	@Override
	public void onCreate() {
		super.onCreate();
        applicationContext = this;
        SDKInitializer.initialize(this);
        instance = this;
//        addressadapter.open();
//        addresslist = addressadapter.getaddresslist();
//        addressadapter.close();
//        for(int i =0;i< addresslist.size();i++){
//            Loger.d("hyytest", "city "+i+addresslist.get(i).get("city"));
//        }
//            
//        Loger.d("hyytest", "addresslist length="+addresslist.size());
//                try {
//                        Class name = Class.forName("com.android.internal.os.RuntimeInit");
//                        Field field = name.getDeclaredField("mCrashing");
//                        field.setAccessible(true);
//                        field.set(null, true);
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchFieldException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IllegalArgumentException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
                
        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         * 
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         * for example:
         * 例子：
         * 
         * public class DemoHXSDKHelper extends HXSDKHelper
         * 
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
        hxSDKHelper.onInit(applicationContext);
        initImageLoader();
        CustomActivityOnCrash.install(this);
	}
 
	public static DemoApplication getInstance() {
		return instance;
	}
 
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
	    return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
	    hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(emCallBack);
	}
	
	public void initImageLoader(){
	    File cacheDir = new File(DemoApplication.this.getCacheDir()+File.separator+"imageloader/Cache");
	    ImageLoaderConfiguration config;
	    config = new ImageLoaderConfiguration  
	            .Builder(DemoApplication.this)  
	            .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
	            .threadPoolSize(3)//线程池内加载的数量  
	            .threadPriority(Thread.NORM_PRIORITY - 2)  
	            .denyCacheImageMultipleSizesInMemory()  
	            .memoryCache(new LruMemoryCache(5 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
	            .memoryCacheSize(5 * 1024 * 1024)    
	            .discCacheSize(50 * 1024 * 1024)    
	            .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  
	            .tasksProcessingOrder(QueueProcessingType.LIFO)  
	            .discCacheFileCount(100) //缓存的文件数量  
	            .discCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径  
	            .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
	            .imageDownloader(new BaseImageDownloader(DemoApplication.this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
	            .writeDebugLogs() // Remove for release app  
	            .build();//开始构建 
	    ImageLoader.getInstance().init(config);
	}
}
