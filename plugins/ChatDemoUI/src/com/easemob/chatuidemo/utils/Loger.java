package com.easemob.chatuidemo.utils;

import android.util.Log;
public class Loger {  
    private static int LOGLEVEL = 0; //发布时写成0
    private static int VERBOSE = 1;  
    private static int DEBUG = 2;  
    private static int INFO = 3;  
    private static int WARN = 4;  
    private static int ERROR = 5;  
      
    public static void v(String tag,String msg){  
        if (LOGLEVEL > VERBOSE) {  
            Log.v(tag, msg);  
        }  
    }  
    public static void d(String tag,String msg){  
        if (LOGLEVEL > DEBUG) {  
            Log.d(tag, msg);  
        }  
    }  
    public static void i(String tag,String msg){  
        if (LOGLEVEL > INFO) {  
            Log.i(tag, msg);  
        }  
    }  
    public static void w(String tag,String msg){  
        if (LOGLEVEL > WARN) {  
            Log.w(tag, msg);  
        }  
    }  
    public static void e(String tag,String msg){  
        if (LOGLEVEL > ERROR) {  
            Log.e(tag, msg);  
        }  
    }  
  
}  
