package com.nfs.youlin.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.easemob.chatuidemo.DemoApplication;
//import com.easemob.chatuidemo.dbadapter;
public class ShutdownReceiver extends BroadcastReceiver  
{  
  
    @Override  
    public void onReceive(Context context, Intent intent)  
    {  
        Loger.i("MainActivity", "启动关闭中...");  
      //  DemoApplication.getInstance().addressadapter.setaddresslist(DemoApplication.getInstance().addresslist);
       // DemoApplication.getInstance().addressadapter.close();
    }  
} 

