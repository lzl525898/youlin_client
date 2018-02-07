package com.nfs.youlin.http;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;  
import java.net.UnknownHostException;  
import java.security.KeyManagementException;  
import java.security.KeyStore;  
import java.security.KeyStoreException;  
import java.security.NoSuchAlgorithmException;  
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  
  
import org.apache.http.HttpVersion;  
import org.apache.http.conn.ClientConnectionManager;  
import org.apache.http.conn.params.ConnManagerParams;  
import org.apache.http.conn.params.ConnPerRouteBean;  
import org.apache.http.conn.scheme.PlainSocketFactory;  
import org.apache.http.conn.scheme.Scheme;  
import org.apache.http.conn.scheme.SchemeRegistry;  
import org.apache.http.conn.ssl.SSLSocketFactory;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;  
import org.apache.http.params.BasicHttpParams;  
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.baidu.mapapi.common.Logger;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.InitTransparentActivity;  
/** 
 * @author Administrator 
 *   注意所导入的包，故将引入的包也贴上，防止错误。 
 */  
public class HttpClientHelper {  
  
    private static DefaultHttpClient httpClient;  
    private static final String VERSION = "1.1";  
    /** http请求最大并发连接数 */  
    private static final int DEFAULT_MAX_CONNECTIONS = 10;  
    /** 超时时间，默认10秒 */  
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;  
    /** 默认的套接字缓冲区大小 */  
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;  
    private static int maxConnections = DEFAULT_MAX_CONNECTIONS;  
    private static int socketTimeout = DEFAULT_SOCKET_TIMEOUT;  
  
    private HttpClientHelper() {  
    }  
  
    public static synchronized DefaultHttpClient getHttpClient() {  
        if (null == httpClient) {  
            // 初始化工作  
            try {
            	SSLSocketFactory sf1;
				InputStream ins = InitTransparentActivity.am.open("youlin.cer");
				try {
				        //读取证书
				        CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");  //问1
				        Certificate cer = cerFactory.generateCertificate(ins);
				        //创建一个证书库，并将证书导入证书库
				        KeyStore trustStore = KeyStore.getInstance("PKCS12", "BC");   //问2
				        trustStore.load(null, null);
				        trustStore.setCertificateEntry("trust", cer);
				    	//把咱的证书库作为信任证书库
				        sf1 = new SSLSocketFactory(trustStore);
				        
				} finally {
				        ins.close();
				}
            	
//            	KeyStore trustStore = KeyStore.getInstance("BKS");
//            	InputStream ins = InitTransparentActivity.am.openRawResource(R.raw.youlin);
//	            // 用 keystore 的密码跟证书初始化 trusted
//            	trustStore.load(ins, "密码".toCharArray());
//	            ins.close();
	                
//                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//                trustStore.load(null, null);
//                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //允许所有主
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params,HTTP.DEFAULT_CONTENT_CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);
                ConnManagerParams.setTimeout(params, 10000);
                HttpConnectionParams.setConnectionTimeout(params, 10000);
                HttpConnectionParams.setSoTimeout(params, 10000);
                // 设置http https支持
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", sf1, 443));
                ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);
                httpClient = new DefaultHttpClient(conManager, params);
            } catch (Exception e) {  
                e.printStackTrace();  
                return new DefaultHttpClient();  
            }  
        }  
        return httpClient;  
    }  
}  
  
class SSLSocketFactoryEx extends SSLSocketFactory {  
    SSLContext sslContext = SSLContext.getInstance("TLS");  
  
    public SSLSocketFactoryEx(KeyStore truststore)  
            throws NoSuchAlgorithmException, KeyManagementException,  
            KeyStoreException, UnrecoverableKeyException {  
        super(truststore);  
        TrustManager tm = new X509TrustManager() {  
            @Override  
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
                return null;  
            }  
  
            @Override  
            public void checkClientTrusted(  
                    java.security.cert.X509Certificate[] chain, String authType)  
                    throws java.security.cert.CertificateException {  
  
            }  
  
            @Override  
            public void checkServerTrusted(  
                    java.security.cert.X509Certificate[] chain, String authType)  
                    throws java.security.cert.CertificateException {  
  
            }  
        };  
  
        sslContext.init(null, new TrustManager[] { tm }, null);  
    }  
  
    @Override  
    public Socket createSocket(Socket socket, String host, int port,  
            boolean autoClose) throws IOException, UnknownHostException {  
        return sslContext.getSocketFactory().createSocket(socket, host, port,  
                autoClose);  
    }  
  
    @Override  
    public Socket createSocket() throws IOException {  
        return sslContext.getSocketFactory().createSocket();  
    }  
}  
