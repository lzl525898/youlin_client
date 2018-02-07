package com.nfs.youlin.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import com.nfs.youlin.utils.Loger;
import android.app.ProgressDialog;
import android.os.Environment;
import android.util.Log;

public class DownLoadManager {
	public static int sDownloadSchedule;
	private static long downloadurllong;
	private MyTrustManager xtm = new MyTrustManager();
	private MyHostnameVerifier hnv = new MyHostnameVerifier();
	public File getFileFromServer(String path, ProgressDialog pd) throws Exception{
		//如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		Loger.i("LYM", "3333333333333333-->"+path);
		DownLoadManager.sDownloadSchedule = 0;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//			SSLContext sslContext = null;
//			try {
//				sslContext = SSLContext.getInstance("SSL");
//				X509TrustManager[] xtmArray = new X509TrustManager[] {xtm};
//				sslContext.init(null, xtmArray, new java.security.SecureRandom());
//			} catch (GeneralSecurityException e) {
//				e.printStackTrace();
//			}
//			if (sslContext != null) {
//				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//			}
//			HttpsURLConnection.setDefaultHostnameVerifier(hnv);
			URL url = new URL(path);
			HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			//conn.setRequestMethod("POST");
			downloadurllong = conn.getContentLength();
			//获取到文件的大小 
//			if(null!=pd){
//			   pd.setMax(100);
//			}
			long total=0;
			InputStream is = conn.getInputStream();
			File indexfile = new File(Environment.getExternalStorageDirectory(), "youlinindex.txt");
			File file = new File(Environment.getExternalStorageDirectory(), "youlin.apk");
			RandomAccessFile Randomfile = new RandomAccessFile(file, "rw");
			Randomfile.setLength(conn.getContentLength());
			
			if(indexfile.exists() && file.exists()){
				String oldapkinfo = readFileByLines(Environment.getExternalStorageDirectory()+"/"+"youlinindex.txt");
				if(oldapkinfo != null){
					Loger.d("test4", oldapkinfo);
					String[] oldinfo = oldapkinfo.split("--");
					if(Long.parseLong(oldinfo[1]) == downloadurllong){
						Loger.d("test4", "Long.parseLong(oldinfo[1]) == downloadurllong="+downloadurllong);
						total = Long.parseLong(oldinfo[0]);
						DownLoadManager.sDownloadSchedule = (int) (((float)total/downloadurllong)*100);
						skipFully(is, Long.parseLong(oldinfo[0]));
						Randomfile.seek(Long.parseLong(oldinfo[0]));
//						downloadurllong -= Long.parseLong(oldinfo[0]);
						
					}
				}
				
			}
			
			
			byte[] buffer = new byte[1024];
			int len ;
			while((len =is.read(buffer))!=-1){
				Randomfile.write(buffer, 0, len);
				total+= len;
				//获取当前下载量
				byte[] srtbyte = (String.valueOf(total)+"--"+String.valueOf(downloadurllong)).getBytes();	
				DownLoadManager.sDownloadSchedule = (int) (((float)total/downloadurllong)*100);
				indexfile = new File(Environment.getExternalStorageDirectory(), "youlinindex.txt");
				FileOutputStream fos = new FileOutputStream(indexfile,false);
				
				fos.write(srtbyte);
				fos.close();
				Loger.d("test4", ""+DownLoadManager.sDownloadSchedule);
			}
			Randomfile.close();
			is.close();
			
			return file;
		}else{
			Loger.i("TEST", "没有SD卡......");
			return null;
		}
	}
	
	public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
        	Loger.d("test4","以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            String returnString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	Loger.d("test4","line " + line + ": " + tempString);
            	returnString = tempString;
                line++;
            }
            reader.close();
            return returnString;
        } catch (IOException e) {
            e.printStackTrace();
            Loger.d("test4",e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }
	public static void skipFully(InputStream in,long bytes) throws IOException{
		long remainning = bytes;
		long len = 0;
		while(remainning > 0){
			len = in.skip(remainning);
			remainning -= len;
		}
	}
	private class MyHostnameVerifier implements HostnameVerifier{  
		  
        @Override  
        public boolean verify(String hostname, SSLSession session) {  
                // TODO Auto-generated method stub  
                return true;  
        }  
	}
	private class MyTrustManager implements X509TrustManager{  
		  
        @Override  
        public void checkClientTrusted(X509Certificate[] chain, String authType)  
                        throws CertificateException {  
                // TODO Auto-generated method stub  
                  
        }  

        @Override  
        public void checkServerTrusted(X509Certificate[] chain, String authType)  
                        throws CertificateException {  
                // TODO Auto-generated method stub  
                  
        }  

        @Override  
        public X509Certificate[] getAcceptedIssuers() {  
                // TODO Auto-generated method stub  
                return null;  
        }          
	}
}
