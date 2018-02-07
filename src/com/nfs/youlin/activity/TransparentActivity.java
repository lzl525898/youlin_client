package com.nfs.youlin.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity.MyConnectionListener;
import com.nfs.youlin.activity.MainActivity.MyContactListener;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.CustomDialog;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

@SuppressWarnings("deprecation")
public class TransparentActivity extends Activity{
	
	private MyConnectionListener connectionListener = null;
	private String jsonInfo;
	private long newFamilyId;
	public static boolean sbooleanExitWithPasswd = false; //表明从修改密码退出
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init_transparent);
		Intent intent = getIntent();
		String infoWithIntent = null;
		try {
			infoWithIntent = intent.getStringExtra("passwd");
		} catch (Exception e3) {
			e3.printStackTrace();
			infoWithIntent = null;
		}
		if (infoWithIntent != null) {
			if(infoWithIntent.equals("passwd")){//修改密码
				String msgContext = "你的账号密码被更改需要重新登录。如非本人操作，则密码可能泄露，建议执行找回密码操作修改密码。";
				YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
				ylPushInitialization.setTags(getApplicationContext(), new HashSet<String>());
				JPushInterface.stopPush(MainActivity.sMainActivity);
				final CustomDialog.Builder builder = new CustomDialog.Builder(TransparentActivity.this);
				builder.setCancelable(false);
				builder.setTitle("下线通知");
				builder.setMessage(msgContext);
				builder.setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!NetworkService.networkBool) {
							Toast.makeText(TransparentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
							return;
						}
						dialog.dismiss();
						try {
							final YLProgressDialog pd = YLProgressDialog.createDialogwithcircle(TransparentActivity.this,
									"加载中...", 0);
							pd.setCancelable(false);
							pd.show();
							new AsyncTask<Void, Void, Void>() {
								protected Void doInBackground(Void... params) {
									Loger.i("TEST", "开始执行注销，释放相应的数据.....");
									ReleaseApplication();
									return null;
								}

								protected void onPostExecute(Void result) {
									MainActivity.sbooleanDisconnected = false;
									// TokenService.sBooleanRunTokenFromSrv = false;
									// TokenService.sBooleanShowToastFromSrv =
									// false;
									pd.dismiss();
									Intent intent = new Intent(TransparentActivity.this, InitTransparentActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									MainActivity.sMainActivity.finish();
									TransparentActivity.sbooleanExitWithPasswd = true;
									TransparentActivity.this.startActivity(intent);
									// TransparentActivity.this.finish();
								}
							}.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				builder.createWithPwd().show();
			}
		} else {
			long curTime = System.currentTimeMillis();
			Date date = new Date(curTime);
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			String curTimeToString = dateFormat.format(date);
			String msgContext = "你的账号于" + curTimeToString + "在另一台手机登录。如非本人操作，则密码可能泄露，建议执行找回密码操作修改密码。";
			final String oldCommunityId = String.valueOf(App.sFamilyCommunityId);
			YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
			ylPushInitialization.setTags(getApplicationContext(), new HashSet<String>());
			JPushInterface.stopPush(MainActivity.sMainActivity);
			Loger.i("TEST", "在其他账户上登录了,释放资源");
			// TokenService.sBooleanRunTokenFromSrv = true;
			SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
			final String userName = sharedata.getString("phone", null);
			final String encryption = sharedata.getString("encryption", null);
			final CustomDialog.Builder builder = new CustomDialog.Builder(TransparentActivity.this);
			builder.setCancelable(false);
			builder.setTitle("下线通知");
			builder.setMessage(msgContext);
			builder.setNegativeButton("重新登录", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if (!NetworkService.networkBool) {
						Toast.makeText(TransparentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
						return;
					}
					dialog.dismiss();
					final YLProgressDialog pd = YLProgressDialog.createDialogwithcircle(TransparentActivity.this,
							"加载中...", 0);
					pd.setCancelable(false);
					pd.show();
					// 重新登录
					Loger.i("TEST", "重新登录");
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							AllFamilyDaoDBImpl oldallFamilyDaoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
							try {
								oldallFamilyDaoDBImpl.deleteAllObject();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							AccountDaoDBImpl oldaccountDaoDBImpl = new AccountDaoDBImpl(getApplicationContext());
							try {
								oldaccountDaoDBImpl.deleteAllObjects();
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							final NeighborsHttpRequest httpRequest = new NeighborsHttpRequest(TransparentActivity.this);
							Bundle updateShareXmlBundle = new Bundle();
							httpRequest.setHttpUrl(IHttpRequestUtils.URL);
							httpRequest.getAccountInfo(userName, encryption);
							Long currenttime = System.currentTimeMillis();
							while (!httpRequest.cRequestSet) {
								if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
									httpRequest.cRequestSet = true;
								}
							}
							if (httpRequest.cRequestSet && httpRequest.flag != null) {// 正确获取数据
								AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(TransparentActivity.this);
								Bundle bundle = null;
								int accountLen = httpRequest.acountBundleList.size();
								if (accountLen == 1) {
									bundle = httpRequest.acountBundleList.get(0);
									String curCommunityId = bundle.getString("user_community_id");
									if (!curCommunityId.equals(oldCommunityId)) {// 证明已经切换了小区
										MainActivity.sbooleanUpdateJPush = true;
										jsonInfo = bundle.getString("user_json");
										newFamilyId = bundle.getLong("user_family_id");
									}
								} else {
									AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(
											TransparentActivity.this);
									AllFamily allFamily = null;
									Bundle familyBundle = null;
									bundle = httpRequest.acountBundleList.get(0);

									for (int i = 1; i < accountLen; i++) {
										allFamily = new AllFamily(TransparentActivity.this);
										familyBundle = httpRequest.acountBundleList.get(i);
										String familyId = familyBundle.getString("family_id");
										if (familyId == null || familyId.isEmpty() || familyId.length() <= 0
												|| familyId.equals("null")) {
											familyId = "0";
										}
										allFamily.setFamily_id(Long.parseLong(familyId));
										String familyName = familyBundle.getString("family_name");
										allFamily.setFamily_name(familyName);
										allFamily.setFamily_address(familyBundle.getString("family_address"));
										String cityName = familyBundle.getString("city_name");
										allFamily.setFamily_city(cityName);
										long city_id;
										try {
											city_id = Long.parseLong(familyBundle.getString("city_id"));
										} catch (NumberFormatException e) {
											// TODO Auto-generated catch block
											city_id = 0;
											e.printStackTrace();
										}
										allFamily.setFamily_city_id(city_id);
										allFamily.setFamily_block(familyBundle.getString("block_name"));
										String blockId = familyBundle.getString("block_id");
										if (blockId == null || blockId.isEmpty() || blockId.length() <= 0
												|| blockId.equals("null")) {
											blockId = "0";
										}
										allFamily.setFamily_block_id(Long.parseLong(blockId));
										String communityName = familyBundle.getString("community_name");
										allFamily.setFamily_community(communityName);
										long community_id = 0;
										try {
											community_id = Long.parseLong(familyBundle.getString("community_id"));
										} catch (NumberFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										allFamily.setFamily_community_id(community_id);
										allFamily.setFamily_building_num(familyBundle.getString("family_building_num"));
										allFamily.setFamily_apt_num(familyBundle.getString("family_apt_num"));
										String isfamilyMember = familyBundle.getString("is_family_member");
										if (isfamilyMember == null || isfamilyMember.isEmpty()
												|| isfamilyMember.length() <= 0 || isfamilyMember.equals("null")) {
											isfamilyMember = "0";
										}
										allFamily.setIs_family_member(Integer.parseInt(isfamilyMember));
										String familyMemberCount = familyBundle.getString("family_member_count");
										if (familyMemberCount == null || familyMemberCount.isEmpty()
												|| familyMemberCount.length() <= 0
												|| familyMemberCount.equals("null")) {
											familyMemberCount = "0";
										}
										allFamily.setFamily_member_count(Integer.parseInt(familyMemberCount));
										String primaryFlag = familyBundle.getString("primary_flag");
										if (primaryFlag == null || primaryFlag.isEmpty() || primaryFlag.length() <= 0
												|| primaryFlag.equals("null")) {
											primaryFlag = "0";
										}
										allFamily.setPrimary_flag(Integer.parseInt(primaryFlag));
										allFamily.setUser_alias(bundle.getString("user_nick"));
										allFamily.setUser_avatar(bundle.getString("user_portrait"));
										allFamily.setLogin_account(Long.parseLong(bundle.getString("user_id")));
										String entityType = familyBundle.getString("entity_type");
										if (entityType == null || entityType.isEmpty() || entityType.length() <= 0
												|| entityType.equals("null")) {
											entityType = "0";
										}
										allFamily.setEntity_type(Integer.parseInt(entityType));
										String neStatus = familyBundle.getString("ne_status");
										if (neStatus == null || neStatus.isEmpty() || neStatus.length() <= 0
												|| neStatus.equals("null")) {
											neStatus = "0";
										}
										allFamily.setNe_status(Integer.parseInt(neStatus));
										if ("1".equals(primaryFlag)) {
											updateShareXmlBundle.putString("blockid", blockId);
											updateShareXmlBundle.putString("detail", familyName);
											updateShareXmlBundle.putString("village", communityName);
											updateShareXmlBundle.putString("familycommunityid",
													String.valueOf(community_id));
											updateShareXmlBundle.putString("city", cityName);
										}
										String city_code = familyBundle.getString("city_code");
										if (city_code == null || city_code.isEmpty() || city_code.length() <= 0
												|| city_code.equals("null")) {
											city_code = "0";
										}
										allFamily.setfamily_city_code(city_code);
										
										String num_id = familyBundle.getString("apt_num_id");
										if (num_id == null || num_id.isEmpty() || num_id.length() <= 0
												|| num_id.equals("null")) {
											num_id = "0";
										}
										allFamily.setapt_num_id(Long.parseLong(num_id));
										
										String building_id = familyBundle.getString("building_num_id");
										if (building_id == null || building_id.isEmpty() || building_id.length() <= 0
												|| building_id.equals("null")) {
											building_id = "0";
										}
										allFamily.setbuilding_num_id(Long.parseLong(building_id));
										allFamily.setFamily_address_id(Long.parseLong(familyBundle.getString("fr_id")));
										allFamilyDaoDBImpl.saveObject(allFamily);
									}
									allFamilyDaoDBImpl.releaseDatabaseRes();
									uploadAllfamilySharePrefrence(updateShareXmlBundle);
								}
								String tmpPhone = accountDaoDBImpl.findAccountByLoginID(bundle.getString("user_id"))
										.getUser_phone_number();
								Account account = null;
								if (tmpPhone == null || tmpPhone.isEmpty() || tmpPhone.length() <= 0) {
									account = new Account(TransparentActivity.this);
									account.setUser_id(Long.parseLong(bundle.getString("user_id")));
									account.setLogin_account(Long.parseLong(bundle.getString("user_id")));
									account.setUser_portrait(bundle.getString("user_portrait"));
									account.setUser_phone_number(bundle.getString("user_phone_number"));
									String user_family_id = bundle.getString("user_family_id");
									if (user_family_id == null || user_family_id.isEmpty()
											|| user_family_id.length() <= 0 || user_family_id.equals("null")) {
										user_family_id = "0";
									}
									account.setUser_family_id(Long.parseLong(user_family_id));
									account.setUser_family_address(bundle.getString("user_family_address"));
									account.setUser_name(bundle.getString("user_nick"));
									String user_gender = bundle.getString("user_gender");
									if (user_gender == null || user_gender.isEmpty() || user_gender.length() <= 0
											|| user_gender.equals("null")) {
										user_gender = "0";
									}
									account.setUser_gender(Integer.parseInt(user_gender));
									account.setUser_email(bundle.getString("user_email"));
									String user_birthday = bundle.getString("user_birthday");
									if (user_birthday == null || user_birthday.isEmpty() || user_birthday.length() <= 0
											|| user_birthday.equals("null")) {
										user_birthday = "0";
									}
									account.setUser_birthday(Long.parseLong(user_birthday));
									String user_public_status = bundle.getString("user_public_status");
									if (user_public_status == null || user_public_status.isEmpty()
											|| user_public_status.length() <= 0 || user_public_status.equals("null")) {
										user_public_status = "0";
									}
									account.setUser_public_status(Integer.parseInt(user_public_status));
									String userType = bundle.getString("user_type");
									if (userType == null || userType.isEmpty() || userType.length() <= 0
											|| userType == "null") {
										userType = "0";
									}
									account.setUser_type(Integer.parseInt(userType));
									App.sUserType = Integer.parseInt(userType);
									account.setUser_json(bundle.getString("user_json"));
									account.setUser_vocation(bundle.getString("user_profession"));
									account.setUser_time(Long.parseLong(bundle.getString("user_time")));
									String newsReceiveStatus = bundle.getString("user_news_receive");
									if (newsReceiveStatus == null || newsReceiveStatus.isEmpty()
											|| newsReceiveStatus.length() <= 0 || newsReceiveStatus == "null") {
										newsReceiveStatus = "0";
									}
									if ("2".equals(newsReceiveStatus)) {
										App.setNewsRecviceStatus(TransparentActivity.this, false);
									} else {
										App.setNewsRecviceStatus(TransparentActivity.this, true);
									}
								}
								if (account != null) {
									Loger.i("TEST", "向数据库中插入新数据");
									saveSignature(bundle.getString("user_signature"));
									saveSharePrefrence(bundle.getString("user_phone_number"));
									accountDaoDBImpl.deleteAllObjects();
									accountDaoDBImpl.saveObject(account);
								}
								EasemobHandler easemobHandler = new EasemobHandler(TransparentActivity.this);
								easemobHandler.userLogin(bundle.getString("user_id"));
								DemoApplication.getInstance().setUserName(bundle.getString("user_id"));
								uploadUserSharePrefrence(bundle);
								uploadSMSSharePrefrence(userName);
								getUserLoginID(userName);
								EMContactManager.getInstance().setContactListener(new MyContactListener());
								// 注册一个监听连接状态的listener
								connectionListener = new MyConnectionListener();
								EMChatManager.getInstance().addConnectionListener(connectionListener);
							}
							MainActivity.sMainInitData = 1;
							while (true) {
								if (MainActivity.sMainInitData > 0) {
									break;
								}
							}
							return null;
						}

						protected void onPostExecute(Void result) {
							// 开始重新设置服务器imei
							String imeiString = YLPushUtils.getImei(getApplicationContext(), null);
							RequestParams imeiParams = new RequestParams();
							imeiParams.put("user_id", App.sUserLoginId);
							imeiParams.put("user_phone_number", App.sUserPhone);
							imeiParams.put("imei", imeiString);
							imeiParams.put("tag", "upload");
							imeiParams.put("apitype", IHttpRequestUtils.APITYPE[0]);
							AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
							asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, imeiParams,
									new JsonHttpResponseHandler() {
								public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
									try {
										String flag = response.getString("flag");
										Loger.i("TEST", "UPLOAD_INFO flag->" + flag);
										if ("ok".equals(flag)) {
											Loger.i("TEST", "success->" + flag);
											Loger.i("TEST", "开始设置PushTag");
											Loger.i("TEST", "MainActivity开始设置当前地址");
											MainActivity.setcurrentaddressTextView(loadVillagePrefrence());
											YLPushInitialization.InitPush(TransparentActivity.this);
											if (YLPushInitialization.isPushStop(TransparentActivity.this)) {
												YLPushInitialization.InitPush(TransparentActivity.this);
												Loger.i("TEST", "重新初始化App的push");
											}
											if (YLPushInitialization.isPushStop(TransparentActivity.this)) {
												Loger.i("TEST", "重启App的push");
												YLPushInitialization.resumePush(TransparentActivity.this);
											}
											Loger.i("TEST", "MainActivity开始设置极光绑定");
											// YLPushInitialization
											// pushInitialization = new
											// YLPushInitialization(MainActivity.this);
											// pushInitialization.InitAlias(String.valueOf(App.sUserLoginId));
											// final String count =
											// Integer.toString(getSysUnreadMsgCountTotal());
											try {
												if (App.sNewPushRecordCount == null || App.sNewPushRecordCount.isEmpty()
														|| App.sNewPushRecordCount == "null") {
													App.sNewPushRecordCount = "0";
												}
												MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
											} catch (Exception e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											if (MainActivity.sbooleanUpdateJPush) {
												// 重新绑定jpush
												SetPushTagWithChange(jsonInfo);
												// 重新刷新邻居圈
												Intent intent = new Intent();
												intent.setAction("com.nfs.youlin.find.FindFragment");
												intent.putExtra("family_id", newFamilyId);
												TransparentActivity.this.sendOrderedBroadcast(intent, null);
												MainActivity.sbooleanUpdateJPush = false;
											}
											Loger.i("TEST", "MainActivity全部初始化完成");
											pd.cancel();
											TransparentActivity.this.finish();
											MainActivity.sbooleanDisconnected = false;
											// TokenService.sBooleanRunTokenFromSrv
											// = false;
											// TokenService.sBooleanShowToastFromSrv
											// = false;
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										Loger.i("TEST", "UPLOAD_INFO->" + e.getMessage());
										e.printStackTrace();
									}
									super.onSuccess(statusCode, headers, response);
								};

								public void onFailure(int statusCode, Header[] headers, String responseString,
										Throwable throwable) {
									Loger.i("TEST", "UPLOAD_INFO-Error->" + responseString);
									super.onFailure(statusCode, headers, responseString, throwable);
								};
							});
						};
					}.execute();
				}
			});
			builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (!NetworkService.networkBool) {
						Toast.makeText(TransparentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
						return;
					}
					dialog.dismiss();
					try {
						final YLProgressDialog pd = YLProgressDialog.createDialogwithcircle(TransparentActivity.this,
								"加载中...", 0);
						pd.setCancelable(false);
						pd.show();
						new AsyncTask<Void, Void, Void>() {
							protected Void doInBackground(Void... params) {
								Loger.i("TEST", "开始执行注销，释放相应的数据.....");
								ReleaseApplication();
								return null;
							}

							protected void onPostExecute(Void result) {
								MainActivity.sbooleanDisconnected = false;
								// TokenService.sBooleanRunTokenFromSrv = false;
								// TokenService.sBooleanShowToastFromSrv =
								// false;
								pd.dismiss();
								Intent intent = new Intent(TransparentActivity.this, InitTransparentActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								MainActivity.sMainActivity.finish();
								intent.putExtra("exit", "exit");
								TransparentActivity.this.startActivity(intent);
								// TransparentActivity.this.finish();
							}
						}.execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			builder.create().show();
		}
	}
	
	private void SetPushTagWithChange(String json){
		Loger.i("TEST", "切换后的json地址==》\n"+json);
		JSONArray jsonArray = null;
		Set<String> tagList = null;
		String adminTag = null;
		String newsTag = App.PUSH_NEWS_NOTICE + String.valueOf(App.sFamilyCommunityId);
		if("null".equals(json) && App.sFamilyCommunityId>0){
			tagList = new HashSet<String>();
			adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + String.valueOf(App.sFamilyCommunityId);
			tagList.add(adminTag);
			tagList.add(newsTag);
			YLPushInitialization ylPushInitialization = new YLPushInitialization(TransparentActivity.this);
			ylPushInitialization.setTags(TransparentActivity.this, tagList);
			tagList.clear();
			jsonArray = null;
			tagList = null;
			return;
		}
		try {
			try {
				jsonArray = new JSONArray(json);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "setPushTag=>ERROR=>"+e.getMessage());
				e.printStackTrace();
				return;
			}
			String nUserCommunityId = jsonArray.getJSONObject(0).getString("communityId");
			if(String.valueOf(App.sFamilyCommunityId).equals(nUserCommunityId)){//证明是当前小区的管理者
				tagList = new HashSet<String>();
				String nUserType = jsonArray.getJSONObject(0).getString("userType");
				if(nUserType==null){
					adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + nUserCommunityId;
					tagList.add(adminTag);
					tagList.add(newsTag);
					Loger.i("TEST","2当前所在组==>"+adminTag);
					YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
					ylPushInitialization.setTags(getApplicationContext(), tagList);
					tagList.clear();
					jsonArray = null;
					tagList = null;
					return;
				}
				YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
				switch(Integer.parseInt(nUserType)){
				case 0:
				case 1://申请管理员状态
					adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + nUserCommunityId;
					tagList.add(newsTag);
					tagList.add(adminTag);
					Loger.i("TEST","3当前所在组==>"+adminTag);
					ylPushInitialization.setTags(getApplicationContext(), tagList);
					tagList.clear();
					break;
				case 2://管理员状态
				case 3://管理员状态
					adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + nUserCommunityId;
					tagList.add(adminTag);
					tagList.add(newsTag);
					Loger.i("TEST","4当前所在组==>"+adminTag);
					ylPushInitialization.setTags(getApplicationContext(), tagList);
					tagList.clear();
					break;
				case 4://物业状态
				case 5://物业状态
					Loger.i("TEST","5当前所在组==>"+adminTag);
					adminTag = App.PUSH_TAG_PROPERTY_ADMIN + nUserCommunityId;
					tagList.add(adminTag);
					tagList.add(newsTag);
					Loger.i("TEST","5当前所在组==>"+adminTag);
					ylPushInitialization.setTags(getApplicationContext(), tagList);
					tagList.clear();
					break;
				case 6://物业+管理员
					adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + nUserCommunityId;
					tagList.add(adminTag);
					tagList.add(newsTag);
					Loger.i("TEST","6当前所在组==>"+adminTag);
					adminTag = App.PUSH_TAG_PROPERTY_ADMIN + nUserCommunityId;
					tagList.add(adminTag);
					Loger.i("TEST","7当前所在组==>"+adminTag);
					ylPushInitialization.setTags(getApplicationContext(), tagList);
					tagList.clear();
					break;
				default:
					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			jsonArray = null;
			tagList = null;
		}
	}
	
	private void getUserLoginID(String phone){
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(TransparentActivity.this);
		Account account = accountDaoDBImpl.findAccountByPhone(phone);
		accountDaoDBImpl.releaseDatabaseRes();
		long userLoginId = account.getUser_id();
		Loger.i("TEST", "userid=====>"+userLoginId);
		if(userLoginId>0){
			Loger.i("TEST", "Login LoginId->"+ userLoginId);
			Loger.i("TEST", "Login phone->"+ account.getUser_phone_number());
			App.sUserLoginId = userLoginId;
			App.sUserPhone   = account.getUser_phone_number();
		}
	}
	
	private void uploadSMSSharePrefrence(String phone){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", phone);
		sharedata.commit();
	}
	
	private void uploadUserSharePrefrence(Bundle bundle){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("familyid", bundle.getString("user_family_id"));
		sharedata.putString("phone", bundle.getString("user_phone_number"));
		sharedata.putString("username", bundle.getString("user_nick"));
		sharedata.putString("encryption", bundle.getString("user_password"));
		sharedata.putString("account", bundle.getString("user_id"));
		String strType = bundle.getString("user_type");
		if(strType==null || strType.isEmpty() || strType.length()<=0 || strType=="null"){
			sharedata.putString("atype", "0");
		}else{
			sharedata.putString("atype", strType);
		}
		Loger.i("TEST", "ATYPE==>"+strType);
		sharedata.commit();
	}
	
	private void saveSharePrefrence(String phone){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", phone);
		sharedata.commit();
	}
	
	private void saveSignature(String strInfo){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("signature", strInfo);
		sharedata.commit();
	}
	
	private void uploadAllfamilySharePrefrence(Bundle bundle){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("blockid", bundle.getString("blockid"));
		sharedata.putString("detail", bundle.getString("detail"));
		sharedata.putString("village", bundle.getString("village"));
		sharedata.putString("familycommunityid", bundle.getString("familycommunityid"));
		try {
			App.sFamilyCommunityId = Long.parseLong(bundle.getString("familycommunityid"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			App.sFamilyCommunityId = 0L;
			e.printStackTrace();
		}
		try {
			App.sFamilyBlockId = Long.parseLong(bundle.getString("blockid"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			App.sFamilyBlockId = 0L;
			e.printStackTrace();
		}
		sharedata.putString("city", bundle.getString("city"));
		sharedata.commit();
	}
	
	public void saveSharePrefrence(String city, String village,String detail,String familyid,
			String fimalycommunityid,String blockid,String username){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.putString("blockid", blockid);
		sharedata.putString("username", username);
		sharedata.putString("recordCount","0"); 
		sharedata.putString("atype","0");
		sharedata.putInt("aliasStatus",0);
		sharedata.putString("encryption","0");
		sharedata.putString("account","0");
		sharedata.putString("signature","null");
		sharedata.commit();
	}
	
	private String loadVillagePrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("village", "未设置");
	}
	
	private void ReleaseApplication(){
		// 注销推送功能
//		JPushInterface.stopPush(MainActivity.sMainActivity);
		MainActivity.currentcity = "未设置";
		MainActivity.currentvillage = "未设置";
		App.sUserType = -1;
		App.sUserLoginId = -1;
		App.sNewPushRecordCount = null;
		App.sUserPhone = null;
		App.imei = null;
		App.pushTags = null;
		App.sDeleteTagFinish = false;
		App.sDeviceLoginStatus = false;
		App.sFamilyId = 00000000000L;
		App.sFamilyCommunityId = 0000000000L;
		App.sFamilyBlockId = 0000000000L;
		App.sUserAppTime = 0000000000L;
		App.sPushUserID = null;
		App.sPushChannelID = null;
		App.sLoadNewTopicStatus = false;
		App.NORMAL_TYPE = 2;
		App.GONGGAO_TYPE = 3;
		App.BAOXIU_TYPE = 4;
		App.JIANYI_TYPE = 5;
		saveSharePrefrence(null, null, null, null, null, null, null);
		AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
		try {
			allFamilyDaoDBImpl.deleteAllObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(getApplicationContext());
		accountDaoDBImpl.deleteAllObjects();
		String filePath = "/data/data/" + getPackageName().toString() + "/shared_prefs";
		String fileName1 = App.SMS_VERIFICATION_USER + ".xml";
		String fileName2 = App.VIBRATION + ".xml";
		String fileName3 = App.VOICE + ".xml";
		String fileName4 = App.ADDRESSOCCUP + ".xml";
		CommonTools commonTools = new CommonTools(getApplicationContext());
		commonTools.delTargetFile(filePath, fileName1);
		commonTools.delTargetFile(filePath, fileName2);
		commonTools.delTargetFile(filePath, fileName3);
		commonTools.delTargetFile(filePath, fileName4);
		MainActivity.sMainInitData = 0;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
}
