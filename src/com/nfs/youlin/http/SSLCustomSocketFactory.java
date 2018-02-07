package com.nfs.youlin.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import org.apache.http.conn.ssl.SSLSocketFactory;

import com.nfs.youlin.R;

import android.content.Context;
import android.util.Log;

public class SSLCustomSocketFactory extends SSLSocketFactory{
	private static final String TAG = "LYM";  
	  
    private static final String KEY_PASS = "";  
  
    public SSLCustomSocketFactory(KeyStore trustStore) throws Throwable {  
        super(trustStore);  
    }  
  
    public static SSLSocketFactory getSocketFactory(Context context) {  
        try {  
            //InputStream ins = context.getResources().openRawResource();  
  
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
            //try {  
            //    trustStore.load(ins, KEY_PASS.toCharArray());  
            //}  
            //finally {  
            //    ins.close();  
            //}  
            SSLSocketFactory factory = new SSLCustomSocketFactory(trustStore);  
            return factory;  
        } catch (Throwable e) {  
            Log.d(TAG, e.getMessage());  
            e.printStackTrace();  
        }  
        return null;  
    }
	
}
