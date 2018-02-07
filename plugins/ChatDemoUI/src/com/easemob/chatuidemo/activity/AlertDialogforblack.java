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
package com.easemob.chatuidemo.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.task.DownloadImageTask;
import com.easemob.chatuidemo.utils.App;
import com.easemob.chatuidemo.utils.HttpClientHelper;
import com.easemob.chatuidemo.utils.ImageCache;
import com.easemob.chatuidemo.utils.Loger;
import com.easemob.chatuidemo.utils.MD5Util;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.ImageUtils;
import com.umeng.analytics.MobclickAgent;

public class AlertDialogforblack extends BaseActivity {
    private TextView mTextView;
    private Button mButton;
    private Button okButton;
    private int position;
    private ImageView imageView;
    private EditText editText;
    private boolean isEditextShow;
    public static final int REMOVE_BLACK_USER = 4111;
    private int REMOVE_BLACK_USER111 = 42111;
    private static final int ADDBLACKHINT = 33111;
    private TelephonyManager telephonyManager;
    private String imeiCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alartdialogforblack);
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imeiCode = telephonyManager.getDeviceId();
        mTextView = (TextView) findViewById(R.id.title);
        mButton = (Button) findViewById(R.id.btn_cancel);
        okButton = (Button) findViewById(R.id.black_ok);
        imageView = (ImageView) findViewById(R.id.image);
        editText = (EditText) findViewById(R.id.edit);
        //提示内容
        String msg = getIntent().getStringExtra("msg");
        //提示标题
        String title = getIntent().getStringExtra("title");
        position = getIntent().getIntExtra("position", -1);
        //是否显示取消标题
        boolean isCanceTitle=getIntent().getBooleanExtra("titleIsCancel", false);
        //是否显示取消按钮
        boolean isCanceShow = getIntent().getBooleanExtra("cancel", false);
        //是否显示文本编辑框
        isEditextShow = getIntent().getBooleanExtra("editTextShow",false);
        //转发复制的图片的path
        String path = getIntent().getStringExtra("forwardImage");
        //
        String edit_text = getIntent().getStringExtra("edit_text");
        String removeid = getIntent().getStringExtra("removeid");
        String userid = getIntent().getStringExtra("currentuser");
//        List<String> blacklist = EMContactManager.getInstance().getBlackListUsernames();
//        for(int i=0;i<blacklist.size();i++){
//            if(blacklist.get(i).equals(removeid)){
//                okButton.setText("移出黑名单");
//                break;
//            }
//        }
        if(msg != null)
            ((TextView)findViewById(R.id.alert_message)).setText(msg);
        if(title != null)
            mTextView.setText(title);
        if(isCanceTitle){
            mTextView.setVisibility(View.GONE);
        }
        if(isCanceShow)
            mButton.setVisibility(View.VISIBLE);
        if(path != null){
             //优先拿大图，没有去取缩略图
            if(!new File(path).exists())
                path = DownloadImageTask.getThumbnailImagePath(path);
            imageView.setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.alert_message)).setVisibility(View.GONE);
            if(ImageCache.getInstance().get(path) != null){
                imageView.setImageBitmap(ImageCache.getInstance().get(path));
            }else{
                Bitmap bm = ImageUtils.decodeScaleImage(path, 150, 150);
                imageView.setImageBitmap(bm);
                ImageCache.getInstance().put(path, bm);
            }
            
        }
        if(isEditextShow){
            editText.setVisibility(View.VISIBLE);
            editText.setText(edit_text);
        }
    }
    
    public void ok(View view){
//        if(okButton.getText().toString().equals("加入黑名单")){
        	final HttpClient httpClient = HttpClientHelper.getHttpClient(AlertDialogforblack.this);
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("请求服务");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            new Thread(){
                @Override
                public void run() {
                    try {
                        HttpPost post = new HttpPost(App.URL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        String removeUserId = AlertDialogforblack.this.getIntent().getStringExtra("removeid");
                        params.add(new BasicNameValuePair("user_id", removeUserId));
                        params.add(new BasicNameValuePair("tag","checkusertype"));
                        params.add(new BasicNameValuePair("apitype","users"));
                        
                        StringBuilder builderWithInfo = new StringBuilder();
                        builderWithInfo.append("user_id");
                        builderWithInfo.append(removeUserId);
                        builderWithInfo.append("tag");
                        builderWithInfo.append("checkusertype");
                        builderWithInfo.append("apitype");
                        builderWithInfo.append("users");
                        String saltCode = String.valueOf(System.currentTimeMillis());
                        String hashCode = "";
                        try {
                            hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
                        } catch (NoSuchAlgorithmException e1) {
                            hashCode = "9573";
                            e1.printStackTrace();
                        }
                        params.add(new BasicNameValuePair("salt",saltCode));
                        params.add(new BasicNameValuePair("hash",hashCode));
                        params.add(new BasicNameValuePair("keyset","user_id:tag:apitype:"));
                        params.add(new BasicNameValuePair("tokenvalue",imeiCode));
                        
                        post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                        Loger.i("LYM", "2000000000--->");
                        HttpResponse httprequest = httpClient.execute(post);
                        Loger.i("LYM", "20011111111111111--->"+httprequest.getStatusLine().getStatusCode());
                        if(httprequest.getStatusLine().getStatusCode() == 200){
                            String msg = EntityUtils.toString(httprequest.getEntity());
                            JSONObject obj;
                            try {
                                obj = new JSONObject(msg);
                                Loger.i("LYM", "msg11111111111111--->"+msg);
                                if("ok".equals(obj.get("flag"))){
                                	Loger.d("test4",obj.getString("type") );
                                	if("0".equals(obj.getString("type"))){
                                		Intent intent = new Intent(AlertDialogforblack.this,AlertDialog.class);
                                		intent.putExtra("msg", "加入黑名单，你将不再收到对方的消息，并且你们相互看不到对方朋友圈的更新");
                                		intent.putExtra("cancel", true);
                                		intent.putExtra("type", 0);
                                		startActivityForResult(intent, ADDBLACKHINT);
//                                		setResult(RESULT_OK,new Intent().putExtra("position", position).
//                                                putExtra("edittext", editText.getText().toString())
//                                                /*.putExtra("voicePath", voicePath)*/);
//                                        if(position != -1)
//                                            ChatActivity.resendPos = position;
//                                        finish();
                                	}else if("2".equals(obj.getString("type"))){
                                		Intent intent = new Intent(AlertDialogforblack.this,AlertDialog.class);
                                		intent.putExtra("msg", "将管理员加入黑名单已禁用，如需此操作请等待下一版本更新！");
                                		intent.putExtra("cancel", false);
                                		intent.putExtra("type", 2);
                                		startActivityForResult(intent, ADDBLACKHINT);
                                	}else if("4".equals(obj.getString("type"))){
                                		Intent intent1 = new Intent(AlertDialogforblack.this,AlertDialog.class);
                                		intent1.putExtra("msg", "此用户是小区物业，把物业加入黑名单可能影响您接受小区相关信息，请再次确认是否将其加入黑名单！");
                                		intent1.putExtra("cancel", false);
                                		intent1.putExtra("type", 4);
                                		startActivityForResult(intent1, ADDBLACKHINT);
                                	}
                                	pd.dismiss();
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            
                            Loger.d("test5", "remove return msg="+msg);
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 1");
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 2");
                        e.printStackTrace();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 3");
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 4");
                        e.printStackTrace();
                    }
                    
                };
            }.start();
            
//        }else{
//            final HttpClient httpClient = new DefaultHttpClient();
//            final ProgressDialog pd = new ProgressDialog(this);
//            pd.setMessage(getString(R.string.be_removing));
//            pd.setCanceledOnTouchOutside(false);
//            pd.show();
//            Loger.d("test5", "remove black user="+AlertDialogforblack.this.getIntent().getStringExtra("removeid"));
//            new Thread(){
//                @Override
//                public void run() {
//                    try {
//                        HttpPost post = new HttpPost("http://123.57.9.62/youlin/api1.0/");
////                        HttpPost post = new HttpPost("http://172.16.50.197/youlin/api1.0/");
//                        List<NameValuePair> params = new ArrayList<NameValuePair>();
//                        params.add(new BasicNameValuePair("black_user_id", AlertDialogforblack.this.getIntent().getStringExtra("removeid")));
//                        params.add(new BasicNameValuePair("user_id",AlertDialogforblack.this.getIntent().getStringExtra("currentuser") ));
//                        params.add(new BasicNameValuePair("action_id","2"));
//                        params.add(new BasicNameValuePair("tag","blacklist"));
//                        params.add(new BasicNameValuePair("apitype","users"));
//                        post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
//                        HttpResponse httprequest = httpClient.execute(post);
//                        if(httprequest.getStatusLine().getStatusCode() == 200){
//                            String msg = EntityUtils.toString(httprequest.getEntity());
//                            JSONObject obj;
//                            try {
//                                obj = new JSONObject(msg);
//                                if("ok".equals(obj.get("flag"))){
//                                    try {
//                                        // 移出黑民单
//                                        EMContactManager.getInstance().deleteUserFromBlackList(AlertDialogforblack.this.getIntent().getStringExtra("removeid") );
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                pd.dismiss();
//                                            }
//                                        });
//                                        Loger.d("test4", "AlertDialogforblack requestCode == REMOVE_BLACK_USER"+AlertDialogforblack.this.getIntent().getStringExtra("removeid"));
//                                        
//                                    } catch (EaseMobException e) {
//                                        e.printStackTrace();
//                                        try {
//                                            HttpClient httpClient=new DefaultHttpClient();
//                                            HttpPost httpPost=new HttpPost("http://123.57.9.62/youlin/api1.0/");
//                                            List<NameValuePair> params2=new ArrayList<NameValuePair>();
//                                            params2.add(new BasicNameValuePair("black_user_id", AlertDialogforblack.this.getIntent().getStringExtra("removeid")));
//                                            params2.add(new BasicNameValuePair("user_id", AlertDialogforblack.this.getIntent().getStringExtra("currentuser")));
//                                            params2.add(new BasicNameValuePair("action_id", "1"));
//                                            params2.add(new BasicNameValuePair("tag", "blacklist"));
//                                            params2.add(new BasicNameValuePair("apitype", "users"));
//                                            httpPost.setEntity(new UrlEncodedFormEntity(params2,HTTP.UTF_8));
//                                            HttpResponse httpResponse=httpClient.execute(httpPost);
//                                            if(httpResponse.getStatusLine().getStatusCode()==200){
//                                                String msg2=EntityUtils.toString(httpResponse.getEntity());
//                                                JSONObject jsonObject=new JSONObject(msg2);
//                                                if(jsonObject.getString("flag").equals("ok")){
//                                                    Loger.i("youlin", "加入成功");
//                                                }else{
//                                                    Loger.i("youlin", "加入失败");
//                                                }
//                                            }
//                                        } catch (UnsupportedEncodingException e1) {
//                                            // TODO Auto-generated catch block
//                                            e1.printStackTrace();
//                                        } catch (ClientProtocolException e1) {
//                                            // TODO Auto-generated catch block
//                                            e1.printStackTrace();
//                                        } catch (ParseException e1) {
//                                            // TODO Auto-generated catch block
//                                            e1.printStackTrace();
//                                        } catch (IOException e1) {
//                                            // TODO Auto-generated catch block
//                                            e1.printStackTrace();
//                                        } catch (JSONException e1) {
//                                            // TODO Auto-generated catch block
//                                            e1.printStackTrace();
//                                        }
//                                        runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                pd.dismiss();
//                                                Toast.makeText(getApplicationContext(), R.string.Removed_from_the_failure, 0).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                            
//                            Loger.d("test5", "remove return msg="+msg);
//                        }
//                    } catch (UnsupportedEncodingException e) {
//                        // TODO Auto-generated catch block
//                        Loger.d("test5", "remove return msg="+"catch 1");
//                        e.printStackTrace();
//                    } catch (ClientProtocolException e) {
//                        // TODO Auto-generated catch block
//                        Loger.d("test5", "remove return msg="+"catch 2");
//                        e.printStackTrace();
//                    } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        Loger.d("test5", "remove return msg="+"catch 3");
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        Loger.d("test5", "remove return msg="+"catch 4");
//                        e.printStackTrace();
//                    }
//                    setResult(REMOVE_BLACK_USER,new Intent().putExtra("userid", AlertDialogforblack.this.getIntent().getStringExtra("removeid"))
//                            /*.putExtra("voicePath", voicePath)*/);
//                    if(position != -1)
//                        ChatActivity.resendPos = position;
//                    finish();
//                };
//            }.start();
//        }
        
        
    }
    
    public void cancel(View view){
        finish();
    }
    public void list(View view){
        Intent intent = getIntent();
        String userid = intent.getStringExtra("currentuser");
        startActivityForResult(new Intent(AlertDialogforblack.this, BlacklistActivity.class).putExtra("currentuser",userid),REMOVE_BLACK_USER);
//        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return super.onTouchEvent(event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(resultCode == RESULT_OK){
            Loger.d("test4", "resultCode == RESULT_OK");
            if(requestCode == REMOVE_BLACK_USER){
                Loger.d("test4", "AlertDialogforblack requestCode == REMOVE_BLACK_USER"+data.getStringExtra("userid"));
                setResult(REMOVE_BLACK_USER,new Intent().putExtra("userid", data.getStringExtra("userid"))
                        /*.putExtra("voicePath", voicePath)*/);
                if(position != -1)
                    ChatActivity.resendPos = position;
                finish();
            }
            if(requestCode == ADDBLACKHINT){
                if(data.getIntExtra("type", -1)==2){
                    Loger.d("test4", "usertype==2"+"position="+position);
                    if(position != -1)
                        ChatActivity.resendPos = position;
                    finish();
                }else{
                    Loger.d("test4", "usertype==0"+"position="+position);
                    setResult(RESULT_OK,new Intent().putExtra("position", position).
                            putExtra("edittext", editText.getText().toString())
                            /*.putExtra("voicePath", voicePath)*/);
                    if(position != -1)
                        ChatActivity.resendPos = position;
                    finish();
                }
            	
            }
        }
        if(resultCode == RESULT_CANCELED){
            if(position != -1)
                ChatActivity.resendPos = position;
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
     
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
