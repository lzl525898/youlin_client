package com.easemob.chatuidemo.utils;

import com.easemob.chatuidemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.graphics.Bitmap;

public class App {
    public static final String URL = "https://123.57.9.62/youlin/api1.0/";    //123.57.9.62  123.56.181.71   172.16.50.179
    
    public static String REGISTERED_USER = "registered_user";
    
    public static DisplayImageOptions options_account = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.account) //设置图片在下载期间显示的图片  
            .showImageForEmptyUri(R.drawable.account)//设置图片Uri为空或是错误的时候显示的图片  
            .showImageOnFail(R.drawable.account)
            .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
            //.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
            .build();
    public static DisplayImageOptions options_default_avatar = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_avatar) //设置图片在下载期间显示的图片  
            .showImageForEmptyUri(R.drawable.default_avatar)//设置图片Uri为空或是错误的时候显示的图片  
            .showImageOnFail(R.drawable.default_avatar)
            .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
            //.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置 
            .build();
}
