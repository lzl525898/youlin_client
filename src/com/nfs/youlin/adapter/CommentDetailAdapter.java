package com.nfs.youlin.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.utils.SmileUtils;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.InitTransparentActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.personal.FriendInformationActivity;
import com.nfs.youlin.activity.personal.PersonalInformationActivity;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.HttpGet;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MyJsonObject;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.view.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.TextView.BufferType;

public class CommentDetailAdapter extends BaseAdapter{
	private Context context;
	private List<Map<String, Object>> list;
	public  int count=10;
	public  static boolean isPlaying = false;
	AnimationDrawable voiceAnimation;
	public static MediaPlayer mediaPlayer;
	private static PowerManager.WakeLock wakeLock;
	ViewHolder holder;
	long topicId;
	private int listposition;
	private int parentclass;
	MyJsonObject jsonObject=new MyJsonObject();
	public static Map<Integer, Boolean> isChecked;
	ImageLoader imageLoader;
	private MyTrustManager xtm = new MyTrustManager();
	private MyHostnameVerifier hnv = new MyHostnameVerifier();
	
	public CommentDetailAdapter(Context context,List<Map<String, Object>> list,long topicId,int listposition,int parentclass){
		imageLoader = ImageLoader.getInstance();
		this.context=context;
		this.list=list;
		this.topicId=topicId;
		this.listposition=listposition;
		this.parentclass=parentclass;
		isChecked = new HashMap<Integer, Boolean>();
		Loger.i("youlin","list.size()--->"+list.size());
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
			if(convertView==null){
				holder=new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_comment_detail_item, null);
				holder.headImg=(ImageView)convertView.findViewById(R.id.comment_head_img);
				holder.nameTv=(TextView)convertView.findViewById(R.id.comment_name_tv);
				holder.commentTv = (TextView) convertView.findViewById(R.id.comment_content_tv);
				holder.commentTv2 = (TextView) convertView.findViewById(R.id.comment_content_tv_2);
				holder.timeTv=(TextView)convertView.findViewById(R.id.comment_time_tv);
				holder.commentImg=(ImageView)convertView.findViewById(R.id.comment_content_img);
				holder.commentVoice=(Button)convertView.findViewById(R.id.comment_content_voice);
				holder.voiceTime=(TextView)convertView.findViewById(R.id.comment_content_voice_time);
				holder.replyTv=(TextView)convertView.findViewById(R.id.comment_reply_tv);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			//Loger.i("youlin", "22222222222222222--->"+list.size()+" "+list.get(position).get("content"));
			String myUserLogin = String.valueOf(App.sUserLoginId);
			if(myUserLogin.equals(list.get(position).get("senderId"))){
				holder.replyTv.setText("删除");
				holder.replyTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(context);
						if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
						CustomDialog.Builder builder = new CustomDialog.Builder(context);
						//builder.setTitle("删除回复");
						builder.setMessage("确定要删除该回复吗？");							
						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								final ProgressDialog dialog2=new ProgressDialog(context);
								dialog2.setMessage("删除中...");
								dialog2.show();
								String commentId=(String) list.get(position).get("commId");
//								if(NetworkService.networkBool){
//									Intent intent=new Intent("youlin.del.mycomm.action");
//									intent.putExtra("comment_id", commentId);
//									context.sendBroadcast(intent);
//								}
								RequestParams delParams = new RequestParams();
								if(App.sFamilyCommunityId!=null){
									delParams.put("community_id", App.sFamilyCommunityId);
								}else{
									delParams.put("community_id", 0);
								}
								delParams.put("comment_id",commentId);
								delParams.put("user_id", App.sUserLoginId);
								delParams.put("topic_id",topicId);
								delParams.put("tag", "delcomm");
								delParams.put("apitype",IHttpRequestUtils.APITYPE[5]);
								AsyncHttpClient delRequest = new AsyncHttpClient();
								delRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
										delParams, new JsonHttpResponseHandler(){
									public void onSuccess(int statusCode, Header[] headers,
											JSONObject response) {
										dialog2.dismiss();
										try {
											String flag=response.getString("flag");
											if(flag.equals("ok")){
												Intent intent=new Intent("youlin.del.comm.action");
												intent.putExtra("type", 1);
												intent.putExtra("position", position);
												context.sendBroadcast(intent);
											}else if(flag.equals("none")){
												Toast.makeText(context, "该帖子已不可见", 0).show();
												ForumtopicDaoDBImpl forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
												forumtopicDaoDBImplObj.deleteObject(topicId);
												Loger.i("TEST", "删除帖子成功");
												Intent intent = new Intent();  
												intent.setAction("youlin.delete.topic.action");
												intent.putExtra("ID", listposition);
												intent.putExtra("type", parentclass);
												context.sendBroadcast(intent); 
												((Activity)context).finish();
											}else if(flag.equals("no")){
												Toast.makeText(context, "删除帖子失败", 0).show();
												Loger.i("TEST", "删除帖子失败");
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										super.onSuccess(statusCode, headers, response);
									}
									@Override
									public void onFailure(int statusCode, Header[] headers,
											String responseString, Throwable throwable) {
										Loger.i("TEST", responseString);
										dialog2.dismiss();
										super.onFailure(statusCode, headers, responseString, throwable);
									}
								});
								dialog.dismiss();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						builder.create().show();
						}else{
							Toast.makeText(context, "您的地址未经过验证", Toast.LENGTH_SHORT).show();
						}
						
					}
				});
			}else{
				final AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(context);
				holder.replyTv.setText("回复");
			    holder.replyTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
								String replayName = (String.valueOf(list.get(position).get("name")).split("[@]"))[0];
								String relayContent = "回复 "+replayName+":";
								String relayUserId = String.valueOf(list.get(position).get("senderId"));
								Intent intent = new Intent();  
								intent.setAction("com.nfs.youlin.replay");  
								intent.putExtra("relayInfo", relayContent);
								intent.putExtra("relayUserId", relayUserId);
								context.sendBroadcast(intent);
						}else{
							Toast.makeText(context, "您的地址未经过验证", Toast.LENGTH_SHORT).show();
						}
						}
					});
				
			}
//		if (parent.getChildCount() == position) {
				
				final String headUrl = String.valueOf(list.get(position).get("headUrl"));
//				Picasso.with(context) //
//						.load(headUrl) //
//						.placeholder(R.drawable.default_normal_avatar) //
//						.error(R.drawable.default_normal_avatar) //
//						.fit() //
//						.tag(context).into(holder.headImg);
				imageLoader.displayImage(headUrl, holder.headImg, App.options_account);
				holder.headImg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(Long.parseLong(list.get(position).get("senderId").toString())==App.sUserLoginId){
							Intent intent = new Intent(context,PersonalInformationActivity.class);
							intent.putExtra("type", 1);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
						}else{
							Intent intent = new Intent(context,FriendInformationActivity.class);
							intent.putExtra("sender_id",Long.parseLong(list.get(position).get("senderId").toString()));
							intent.putExtra("display_name",String.valueOf(list.get(position).get("name")));
							intent.putExtra("sender_portrait",headUrl);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
						}
//						
//						Intent intent = new Intent(context,CircleDetailGalleryPictureActivity.class);
//						intent.putExtra("type", 1);
//						intent.putExtra("url",headUrl);
//						context.startActivity(intent);
					}
				});
				holder.nameTv.setText(String.valueOf(list.get(position).get("name")));
				String type=String.valueOf(list.get(position).get("type"));
				if(type.equals("2")){
					holder.commentTv2.setVisibility(View.VISIBLE);
					holder.commentTv2.setText(Html.fromHtml("<font color=\"#FF7F00\">"+"@"+String.valueOf(list.get(position).get("remarkName"))+":"+"</font>"));
				}else{
					holder.commentTv2.setVisibility(View.GONE);
				}
				String contentStr=String.valueOf(list.get(position).get("content"));
				if(contentStr.isEmpty()||contentStr.equals("null")){ 
					holder.commentTv.setText("");
					holder.commentTv.setVisibility(View.GONE);
				}else{
					holder.commentTv.setVisibility(View.VISIBLE);
					Spannable span = SmileUtils.getSmiledText(context, contentStr);
					// 设置内容
					holder.commentTv.setText(span, BufferType.SPANNABLE);
				}
				//holder.timeTv.setText(String.valueOf(list.get(position).get("time")));
				holder.timeTv.setText(TimeToStr.getTimeElapse(Long.parseLong(String.valueOf(list.get(position).get("time"))), App.CurrentSysTime));
				final String commentUrl = String.valueOf(list.get(position).get("replyImg"));
//				Loger.i("youlin","111111111111111-->"+App.CurrentSysTime+" "+String.valueOf(list.get(position).get("time")));
//				Picasso.with(context)
//						.load(commentUrl)
//						.placeholder(R.drawable.bg_error)
//						.error(R.drawable.bg_error)
//						.fit()
//						.tag(context) //
//						.into(holder.commentImg);
				imageLoader.displayImage(commentUrl,holder.commentImg,App.options_error);
				if (commentUrl.equals("null")) {
					holder.commentImg.setVisibility(View.GONE);
				} else {
					holder.commentImg.setVisibility(View.VISIBLE);
					holder.commentImg.setScaleType(ScaleType.CENTER_CROP);
					holder.commentImg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(context,CircleDetailGalleryPictureActivity.class);
							intent.putExtra("ID", 1);
							intent.putExtra("url", commentUrl);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
						}
					});
				}
				final String voiceUrl = String.valueOf(list.get(position).get("voice"));
				final String voiceTimeStr = String.valueOf(list.get(position).get("voiceTime"));
				if(voiceUrl.equals("null")){
					holder.commentVoice.setVisibility(View.GONE);
					holder.voiceTime.setVisibility(View.GONE);
				}else{
					holder.commentVoice.setVisibility(View.VISIBLE);
					holder.voiceTime.setVisibility(View.VISIBLE);
					holder.voiceTime.setText(voiceTimeStr+"\"");
					Loger.i("youlin", "123232438493843");
					if (isChecked.get(position)==null || isChecked.get(position)) {
						Loger.i("youlin", "0000000000000");
						holder.commentVoice.setBackgroundResource(R.drawable.yuyin);
					}else {
						Loger.i("youlin", "111111111111");
						holder.commentVoice.setBackgroundResource(R.drawable.zanting);
					}
					Loger.i("youlin", "22222222222222");
					holder.commentVoice.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Loger.i("youlin", "1111111111111111111111111111");
							File voiceFile=new File(context.getCacheDir()+File.separator,"voice");
							if(!voiceFile.exists()){
								 voiceFile.mkdir();
							}
							
							final String voiceName=voiceUrl.split("[/]")[10];
							if (isPlaying) {
								for (int i = 0; i < list.size(); i++){
									isChecked.put(i, true);
								}
								Loger.i("youlin", "222222222222222222222222222");
								holder.commentVoice.setBackgroundResource(R.drawable.yuyin);
								stopPlayVoice();
							}else{
								isChecked.put(position, false);
								holder.commentVoice.setBackgroundResource(R.drawable.zanting);
								Loger.i("youlin", "33333333333333333333333333333");
								CommentDetailAdapter.this.notifyDataSetChanged();
								downVoice(voiceUrl,voiceName,position);
							}
						}
					});
				}
			
//		}
		

		return convertView;
	}

	private static class ViewHolder {
		private ImageView headImg;
		private TextView nameTv;
		private TextView commentTv;
		private TextView commentTv2;
		private TextView timeTv;
		private ImageView commentImg;
		private Button commentVoice;
		private TextView voiceTime;
		private TextView replyTv;
	}
	public void playVoice(String filePath,final int position) {
		keepScreenOn(context,true);
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
		audioManager.setMode(AudioManager.MODE_NORMAL);
		audioManager.setSpeakerphoneOn(true);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.release();
					for (int i = 0; i < list.size(); i++){
						isChecked.put(i, true);
					}
					holder.commentVoice.setBackgroundResource(R.drawable.yuyin);
					CommentDetailAdapter.this.notifyDataSetChanged();
					isPlaying = false;
					keepScreenOn(context,false);
				}
			});
			isPlaying = true;
			mediaPlayer.start();
		} catch (Exception e) {
		}
		
	}
	
	public void stopPlayVoice() {
		//voiceAnimation.stop();
		//holder.commentVoice.setImageResource(R.drawable.chatfrom_voice_playing);
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			mediaPlayer.seekTo(0);
			mediaPlayer.release();
		}
		isPlaying = false;
		CommentDetailAdapter.this.notifyDataSetChanged();
		keepScreenOn(context,false);
	}
	public void downVoice(final String url,final String voiceName,final int position) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					File file = new File(context.getCacheDir()+File.separator+"voice/"+voiceName);
					
					SSLContext sslContext = null;
					try {
						sslContext = SSLContext.getInstance("SSL");
						X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
						sslContext.init(null, xtmArray, new java.security.SecureRandom());
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
					}
					if (sslContext != null) {
						HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
					}
					HttpsURLConnection.setDefaultHostnameVerifier(hnv);
					if(!file.exists()){
					URL uri = null;
					try {
						uri = new URL(url);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					HttpsURLConnection conn = null;
					try {
						conn = (HttpsURLConnection) uri.openConnection();
						//conn.setSSLSocketFactory(sslSocketFactory);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					conn.setConnectTimeout(5000);
					//获取到文件的大小 
					InputStream is = null;
					try {
						is = conn.getInputStream();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Loger.i("TEST", "download file->"+file.getPath());
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BufferedInputStream bis = new BufferedInputStream(is);
					byte[] buffer = new byte[1024];
					int len ;
					int total=0;
					try {
						while((len = bis.read(buffer))!=-1){
							fos.write(buffer, 0, len);
							total+= len;
							//获取当前下载量

						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						fos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						bis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//return file;
					}
					playVoice(context.getCacheDir()+File.separator+"voice/"+voiceName,position);
				
				}else{
					Loger.i("TEST", "没有SD卡......");
					//return null;
				}
				
				
			}
		}).start();
	}

	private void showAnimation(ViewHolder holder) {
		// play voice, and start animation
		//holder.commentVoice.setImageResource(R.anim.voice_from_icon);
//		voiceAnimation = (AnimationDrawable) holder.commentVoice.getDrawable();
//		voiceAnimation.start();
	}
	public static void keepScreenOn(Context context, boolean on) {
	      if (on) {
	          PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	          wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
	          wakeLock.acquire();
	      } else {
	          if (wakeLock != null) {
	              wakeLock.release();
	              wakeLock = null;
	          }
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
