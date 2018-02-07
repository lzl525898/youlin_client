package com.nfs.youlin.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.nfs.youlin.R.color;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.utils.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class NetworkService extends BroadcastReceiver{
	public static boolean networkBool=false;
	private Context context;
	private final int port = 80;
	boolean isAlive2;
	Toast toast;
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context=context;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				networkBool=connect(IHttpRequestUtils.SRV_URL,port);
				final Message msg=new Message();
				msg.what=100;
			    handler.sendMessage(msg);
			}
		}).start();
		
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 100) {
				if (!networkBool) {
					try {
						MainActivity.networkLayout.setVisibility(View.VISIBLE);
						MainActivity.networkLayout.setBackgroundColor(color.black);
						MainActivity.networkLayout.setAlpha(0.5f);
						MainActivity.networkTv.setText("网络任性,出现问题,请检查设置");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Loger.i("youlin","没网");
				} else {
					// Loger.i("youlin","有网");
					try {
						int type = App.getAddrStatus(context);
						
						if (type == 1) {
							MainActivity.networkLayout.setVisibility(View.GONE);
						} else {//等待审核  0// 审核失败 2
							MainActivity.networkLayout.setVisibility(View.VISIBLE);
//							MainActivity.networkLayout.setBackgroundResource(color.address);
							MainActivity.networkLayout.setBackgroundColor(0xfffa5b11);
							MainActivity.networkLayout.setAlpha(0.6f);
							if(App.getNoAddrStatus(context)){
								MainActivity.networkTv.setText("您的地址还未经过验证,请及时验证");
							}else{
								MainActivity.networkTv.setText("您还没有设置地址");
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					if (FriendCircleFragment.allList != null) {
//						if (FriendCircleFragment.allList.size() <= 0) {
//							Intent intent = new Intent();
//							intent.setAction("com.nfs.youlin.find.FindFragment");
//							try {
//								AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(context);
//								if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getFamily_id()!=-1){
//									context.sendBroadcast(intent);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}
					
				}
			}
		};
	};
	
	private boolean connect(String host, int port) {  
//		if (host == null) host = NetworkService.host;
//		boolean success=false;  
//        Process p =null;  
//		try {
//			p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " + host);
//			int status = p.waitFor();
//			if (status == 0) {
//				success = true;
//			} else {
//				success = false;
//			}
//		} catch (IOException e) {
//			Loger.i("TEST", "IOException=>"+e.getMessage());
//			success = false;
//		} catch (InterruptedException e) {
//			Loger.i("TEST", "InterruptedException=>"+e.getMessage());
//			success = false;
//		} finally {
//			p.destroy();
//		}
//		return success;
        if (port == 0) port = 80;  
        Socket connect = new Socket();
        try {  
            connect.connect(new InetSocketAddress(host, port), 8*1000);  
            return connect.isConnected();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                connect.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return false;  
    } 
}
