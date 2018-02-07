package com.nfs.youlin.activity.personal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.impl.conn.Wire;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.activity.BaiduMapActivity;
import com.easemob.chatuidemo.activity.ForwardMessageActivity;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.VerificationActivity;
import com.nfs.youlin.controler.SMSReceiver;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.dao.NeighborsDBOpenHelper;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushTagManager;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SMSVCode;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.UserAddressHelper;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.utils.neighbor_list;
import com.nfs.youlin.view.YLProgressDialog;
import com.nfs.youlin.view.number_fragment;
import com.nfs.youlin.view.rooft_fragment;
import com.umeng.analytics.MobclickAgent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.widget.Toast;

import com.easemob.chatuidemo.DemoApplication;
import com.google.zxing.common.detector.WhiteRectangleDetector;

public class write_address extends FragmentActivity implements StatusChangeListener {
	public static String city = "未定义";
	public static String village = "未定义";
	public static String block = "未定义";
	public static String rooft = "未定义";
	public static String number = "未定义";
	public static String city_code = "未定义";
	public static String city_id = "0";
	public static String village_id = "100";
	public static String block_id = "0";
	public static String rooft_id = "0";
	public static String number_id = "0";
	private String familycommunityid = "0000000000";
	private String familyid = "0000000000";
	private String ne_status = "0";
	private boolean iscurrentaddr;
	private List<Map<String, Object>> list;
	private static final int GET_CURRENT_CITY = 1;
	private static final int GET_CURRENT_VILLAGE = 2;
	public static ListView listview1;
	public static ListView listview2;
	public static List<Map<String, Object>> searchresult = new ArrayList<Map<String, Object>>();
	final private rooft_fragment rooftfragment = new rooft_fragment();
	final private number_fragment numberfragment = new number_fragment();
	public static UserAddressHelper dbHelper;
	public String setaddrmethod;
	private YLProgressDialog ylProgressDialog;
	private boolean setaddrRequestSet = false;
	private String flag = null;
	private String AddrRepeat = "undefine";

	private Neighbor neighbor;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private boolean bRequestSet = false;
	private int REQUEST_NEIGHBOR = 1;
	private int REQUEST_SETCURRENTADDR = 2;
	public static List<JSONObject> jsonObjList;
	private List<JSONObject> BuildngjsonObjList;
	private Thread requestTHttphgread;
	protected static final int REQUEST_OK = 100;
	private StatusChangeutils statusutils;
	// private TextView test22;
	// 2015-09-23- lzl

	void buildprocessdialog() {
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this, "加载中...", 0);
		// pd1 = new ProgressDialog(this);
		// pd1.setTitle("正在提交地址信息");
		// pd1.setMessage("正在玩命为您加载，请稍侯...");
		// pd1.setCancelable(false);
		// pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// pd1.setIndeterminate(false);
	}

	// private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
	//
	// @Override
	// public void onReceive(Context arg0, Intent arg1) {
	// // TODO Auto-generated method stub
	// Loger.d("test5", "write_address boardcastresever unused");
	// }
	// };
	// public write_address writeaddr = new write_address();
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.write_address);
		buildprocessdialog();
		// test22 = (TextView)findViewById(R.id.test22);
		// dbHelper = new UserAddressHelper(this, "useraddress.db3", 1);
		final Intent intent = getIntent();
		setaddrmethod = intent.getStringExtra("setaddrmethod");
		if (setaddrmethod.equals("write")) {
			write_address.searchresult.clear();
			Loger.d("test3", "buildnumlist=" + selectvillageActivity.buildnumlist.size());
			if (((!write_address.village_id.equals("0")) && (!write_address.village.equals("未定义")))
					|| ((!write_address.block_id.equals("0")) && (!write_address.block.equals("")))) {

			} else {
				selectvillageActivity.buildnumlist.clear();
			}
			for (int i = 0; i < selectvillageActivity.buildnumlist.size(); i++) {
				write_address.searchresult.add(selectvillageActivity.buildnumlist.get(i));
			}
			if (write_address.searchresult.size() > 0) {
				Intent intent1 = new Intent(write_address.this, Searchresultlistactivity.class);
				intent1.putExtra("startactivity", "rooft_fragment");
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent1);
			}
		}
		// this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.address_title);
		Button back = (Button) findViewById(R.id.backbutton1);
		Button complete = (Button) findViewById(R.id.addrsetend);
		listview1 = (ListView) findViewById(R.id.addrset1);
		listview2 = (ListView) findViewById(R.id.addrset2);
		//
		// Drawable drawable1 =
		// getResources().getDrawable(R.drawable.nav_fanhui_xin_tiao);
		// drawable1.setBounds(0, 1, 28, 46);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
		// back.setCompoundDrawables(drawable1, null, null, null);//只放左边
		// IntentFilter filter = new IntentFilter();
		// filter.addAction("com.nfs.youlin.view.rooft_fragment");
		// filter.addAction("com.nfs.youlin.view.number_fragment");
		// filter.setPriority(999);
		// this.registerReceiver(mBroadcastReceiver, filter);
		statusutils = new StatusChangeutils();
		statusutils.statuschangelistener("ADDRESSLIST", this);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonTools.goBack();
			}
		});
		complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String Usernamenick = loadUsernamePrefrence();
				if (write_address.city.equals("未定义") || write_address.city.equals("未设置")) {
					Toast.makeText(write_address.this, "请设置所在城市", Toast.LENGTH_SHORT).show();
				} else if (write_address.village.equals("未定义") || write_address.village.equals("未设置")) {
					Toast.makeText(write_address.this, "请设置所在小区", Toast.LENGTH_SHORT).show();
				}

				else if (write_address.number.equals("未定义") || write_address.rooft.equals("未定义")) {
					String num = number_fragment.numbermessage.getText().toString();
					String vil = rooft_fragment.rooftmessage.getText().toString();
					final AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(write_address.this);

					final List<Object> Pointuseraddress = allFamilyDaoDBImpl.findPointTypeObject(App.sUserLoginId);
					for (int i = 0; i < Pointuseraddress.size(); i++) {
						if (((AllFamily) Pointuseraddress.get(i)).getFamily_address()
								.equals(write_address.city + write_address.village + vil + "-" + num)) {
							AddrRepeat = "repeat";
						}
					}

					if (vil.length() < 1) {
						Toast.makeText(write_address.this, "请设置所在单元", Toast.LENGTH_SHORT).show();
						rooft_fragment.setimage.setVisibility(View.VISIBLE);
						rooft_fragment.setimage.setBackgroundResource(R.drawable.tanhao);
					} else if (num.length() < 1) {
						Toast.makeText(write_address.this, "请设置门牌号", Toast.LENGTH_SHORT).show();
						number_fragment.setimage1.setVisibility(View.VISIBLE);
						number_fragment.setimage1.setBackgroundResource(R.drawable.tanhao);
					} else if (AddrRepeat.equals("repeat")) {
						AddrRepeat = "undefine";
						Toast.makeText(write_address.this, "此地址已被添加", Toast.LENGTH_SHORT).show();
					} else {
						write_address.rooft = vil;
						write_address.number = num;
						final AllFamily aFamily = new AllFamily(write_address.this);
						String[] useraddr = new String[] { city, village, rooft, number };
						aFamily.setUser_alias(Usernamenick);
						if (NetworkService.networkBool) {
							for (int i = 0; i < number_fragment.aptlist.size(); i++) {
								Loger.d("test4", "number_fragment number circle" + i);
								if (number_fragment.aptlist.get(i).get("name").equals(num)) {
									number_id = number_fragment.aptlist.get(i).get("_id").toString();
								}
							}
							write_address.rooft_id = number_fragment.selectedbuild_id;
							write_address.rooft = number_fragment.selectedbuild;
				/*
				 * 未设置地址
				 */
				if (setaddrmethod.equals("write")) {
								ylProgressDialog.show();
								Loger.d("test4", "write ID=" + App.sUserLoginId + "city=" + city);
								Loger.i("test4", "SET familyid=" + familyid + "SET familycommunityid=" + village_id);
								RequestParams reference = new RequestParams();
								// reference.put("login_account",
								// App.sUserLoginId);
								reference.put("city", city);
								reference.put("community", village);
								reference.put("primary_flag", 1);
								if (!block_id.equals("0"))
									reference.put("block_name", block);
								reference.put("buildnum", rooft);
								reference.put("aptnum", number);
								reference.put("city_id", Integer.parseInt(city_id));// city_id
								reference.put("city_code", city_code);
								reference.put("community_id", Integer.parseInt(village_id));// village_id
								Loger.i("test4", "SET1 familyid=" + familyid + "SET familycommunityid=" + village_id);
								reference.put("block_id", Integer.parseInt(block_id));
								reference.put("buildnum_id", Integer.parseInt(rooft_id));// rooft_id
								reference.put("aptnum_id", Integer.parseInt(number_id));// number_id
								reference.put("user_id", App.sUserLoginId);
								reference.put("tag", "addfamily");
								reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
								reference.put("push_user_id", App.sPushUserID);
								reference.put("push_channel_id", App.sPushChannelID);
								Loger.i("TEST", "App.sPushUserID==>" + App.sPushUserID);
								Loger.i("TEST", "App.sPushChannelID==>" + App.sPushChannelID);
								SharedPreferences preferences = getSharedPreferences(App.REGISTERED_USER,
										Context.MODE_PRIVATE);
								int addressTag = preferences.getInt("address_tag", 0);
								reference.put("addr_cache", addressTag);
								Loger.i("LYM", "1666666666----->" + addressTag);
								AsyncHttpClient client = new AsyncHttpClient();
								client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
										new JsonHttpResponseHandler() {
									@Override
									public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
										Loger.i("TEST", "write address onSuccess");
										JSONObject jsonContext = response;
										// String flag = null;
										flag = null;
										try {
											Loger.i("LYM", "write flag=" + flag + " " + response);
											flag = jsonContext.getString("flag");
											String addressTag = jsonContext.getString("addr_flag");

											if (flag.equals("full")) {
												setaddrRequestSet = false;
												ylProgressDialog.dismiss();
												Toast.makeText(write_address.this, jsonContext.getString("yl_msg"),
														Toast.LENGTH_SHORT).show();// 返回失败吐司
											}
											if (flag.equals("ok") && addressTag.equals("ok")) {
												String curfr_Id = jsonContext.getString("frecord_id");
												// 返回成功后 返回值 familycommunityid
												// familyid 写数据库 及配置文件
												familyid = jsonContext.getString("family_id");
												if (familyid.equals("0")) {
													ne_status = jsonContext.getString("ne_status");
													aFamily.setNe_status(Integer.parseInt(ne_status));
												} else {
													aFamily.setNe_status(0);
												}
												familycommunityid = village_id;
												aFamily.setPrimary_flag(1);
												int entityType = Integer.parseInt(jsonContext.getString("entity_type"));
												App.setAddrStatus(getApplicationContext(), entityType);
												aFamily.setEntity_type(entityType);
												Loger.i("test4", "get familyid=" + familyid + "get familycommunityid="
														+ familycommunityid);
												aFamily.setFamily_city(city);
												aFamily.setFamily_community(village);
												aFamily.setFamily_building_num(rooft);
												aFamily.setFamily_apt_num(number);
												aFamily.setFamily_city_id(Long.parseLong(city_id));
												aFamily.setfamily_city_code(city_code);
												aFamily.setapt_num_id(Long.parseLong(number_id));
												aFamily.setbuilding_num_id(Long.parseLong(rooft_id));
												aFamily.setFamily_block_id(Long.parseLong(block_id));
												aFamily.setFamily_id(Long.parseLong(familyid));
												aFamily.setFamily_community_id(Long.parseLong(familycommunityid));
												aFamily.setLogin_account(App.sUserLoginId);
												try {
													aFamily.setFamily_address_id(Long.parseLong(curfr_Id));
												} catch (NumberFormatException e) {
													Toast.makeText(getApplicationContext(), "Number=>" + e.getMessage(),
															Toast.LENGTH_SHORT).show();
												}
												aFamily.saveFamilyInfos();
												allFamilyDaoDBImpl.saveObject(aFamily);
												allFamilyDaoDBImpl.releaseDatabaseRes();
												setaddrRequestSet = true;
												Loger.d("TEST", "write address 000000000000000000");
											} else {
												setaddrRequestSet = false;
												ylProgressDialog.dismiss();
												Toast.makeText(write_address.this, "地址设置失败", Toast.LENGTH_SHORT).show();// 返回失败吐司
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
											Loger.i("TEST", e.getMessage());
										}
										super.onSuccess(statusCode, headers, response);
									}

									@Override
									public void onFailure(int statusCode, Header[] headers, String responseString,
											Throwable throwable) {
										Loger.i("TEST", responseString);
										// test22.setText(responseString);
										setaddrRequestSet = false;
										new ErrorServer(write_address.this, responseString);
										ylProgressDialog.dismiss();
										super.onFailure(statusCode, headers, responseString, throwable);
									}
								});
								new AsyncTask<Void, Void, Void>() {
									@Override
									protected Void doInBackground(Void... params) {
										// 网络请求 注册地址
										// 参数{city,village,rooft,number}
										// login_account
										Long currenttime = System.currentTimeMillis();
										while (!setaddrRequestSet) {
											if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME
													+ 5000)) {
												setaddrRequestSet = true;
											}
										}
										return null;
									}

									@Override
									protected void onPostExecute(Void result) {
										super.onPostExecute(result);
										if (setaddrRequestSet && flag != null && flag.equals("ok")) {
											setaddrRequestSet = false;
											MainActivity.currentcity = city;
											MainActivity.currentvillage = village; // write

											saveSharePrefrence(city, village, rooft + "-" + number, familyid, block_id,
													familycommunityid);
											// saveIfPushSuccess(getIfPushSuccess()+1);

											Intent intent2 = new Intent("youlin.initFriend.action");
											sendBroadcast(intent2);

											Intent inten3 = new Intent("youlin.square.action");
											sendBroadcast(inten3);
											/*************
											 * (city+village+rooft+number,
											 * familyid)要写入数据库 table_users表
											 */
											AccountDaoDBImpl accountdao = new AccountDaoDBImpl(write_address.this);
											Account account = accountdao
													.findAccountByLoginID(String.valueOf(App.sUserLoginId));
											account.setUser_family_id(Long.parseLong(familyid));
											account.setUser_family_address(city + village + rooft + "-" + number);
											accountdao.modifyObject(account);
											accountdao.releaseDatabaseRes();
											App.sFamilyId = Long.parseLong(familyid);
											App.sFamilyCommunityId = Long.parseLong(familycommunityid);
											App.sFamilyBlockId = Long.parseLong(block_id);
											write_address.this.setResult(RESULT_OK, intent);
											MainActivity.setcurrentaddressTextView(village);
											// 设置PushTag
											YLPushTagManager pushTagManager = new YLPushTagManager(
													getApplicationContext());
											pushTagManager.setPushTag();
											App.sDeleteTagFinish = true;
											if (Long.parseLong(familyid) > 0) {
												neighbor = new Neighbor(write_address.this);
												aNeighborDaoDBImpl = new NeighborDaoDBImpl(write_address.this);
												if (allFamilyDaoDBImpl.getCurrentAddrDetail("1")
														.getEntity_type() == 1) {
													gethttpdata(number, aFamily.getFamily_block_id(),
															aFamily.getFamily_community_id(), IHttpRequestUtils.YOULIN,
															REQUEST_NEIGHBOR);
													requestTHttphgread = new Thread(new Runnable() {
														@Override
														public void run() {
															// TODO
															// Auto-generated
															// method stub
															runOnUiThread(new Runnable() {
																@Override
																public void run() {
																	ylProgressDialog.show();
																}
															});

															Long currenttime = System.currentTimeMillis();
															while (!bRequestSet) {
																if ((System.currentTimeMillis()
																		- currenttime) > App.WAITFORHTTPTIME) {
																	bRequestSet = true;
																}
															}

															Message msg = new Message();
															msg.what = REQUEST_OK;
															Bundle data = new Bundle();
															data.putLong("family_id", Long.parseLong(familyid));
															msg.setData(data);
															handler.sendMessage(msg);
															return;
														}
													});
													requestTHttphgread.start();
												} else {
													Toast toast = Toast.makeText(getApplicationContext(),
															"该地址信息未审核通过！\n 不能为您显示邻居", Toast.LENGTH_LONG);
													toast.setGravity(Gravity.CENTER, 0, 0);
													toast.show();
													aNeighborDaoDBImpl.deleteObject(Long.parseLong(familyid));
													Intent intent = new Intent();
													intent.setAction("com.nfs.youlin.find.FindFragment");
													intent.putExtra("family_id", Long.parseLong(familyid));
													write_address.this.sendOrderedBroadcast(intent, null);
												}
											} else {
												Toast toast = Toast.makeText(getApplicationContext(),
														"该地址信息未审核通过！\n 不能为您显示邻居", Toast.LENGTH_LONG);
												toast.setGravity(Gravity.CENTER, 0, 0);
												toast.show();
												aNeighborDaoDBImpl.deleteObject(Long.parseLong(familyid));
												Intent intent = new Intent();
												intent.setAction("com.nfs.youlin.find.FindFragment");
												intent.putExtra("family_id", Long.parseLong(familyid));
												write_address.this.sendOrderedBroadcast(intent, null);
											}
											rooft_id = "0";
											number_id = "0";
											ne_status = "0";
											selectvillageActivity.buildnumlist.clear();
											finish();
										} else {
											Toast.makeText(write_address.this, "地址设置失败", Toast.LENGTH_SHORT);
											selectvillageActivity.buildnumlist.clear();
										}
										block_id = "0";
										village_id = "0";
										Loger.d("test4", "set village_id=0");
										ylProgressDialog.dismiss();
										// startActivity(new
										// Intent(write_address.this,addressshow.class));
									}
								}.execute();
							} else if (setaddrmethod.equals("add")) {
								ylProgressDialog.show();
								Loger.d("TEST", "add ID=" + App.sUserLoginId);
								RequestParams reference = new RequestParams();
								reference.put("city", city);
								reference.put("community", village);
								if (!block_id.equals("0"))
									reference.put("block_name", block);
								reference.put("buildnum", rooft);
								reference.put("aptnum", number);
								reference.put("city_id", Integer.parseInt(city_id));// city_id
								reference.put("city_code", city_code);
								reference.put("community_id", Integer.parseInt(village_id));// village_id
								reference.put("block_id", Integer.parseInt(block_id));
								reference.put("buildnum_id", Integer.parseInt(rooft_id));// rooft_id
								reference.put("aptnum_id", Integer.parseInt(number_id));// number_id
								reference.put("user_id", App.sUserLoginId);
								reference.put("tag", "addfamily");
								reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
								reference.put("push_user_id", App.sPushUserID);
								reference.put("push_channel_id", App.sPushChannelID);
								Loger.i("TEST", "App.sPushUserID==>" + App.sPushUserID);
								Loger.i("TEST", "App.sPushChannelID==>" + App.sPushChannelID);
								Loger.i("TEST",
										"doInBackground city_id=" + city_id + "community_id=" + village_id + "block_id"
												+ block_id + "buildnum_id=" + rooft_id + "aptnum_id=" + number_id
												+ "city_code=" + city_code);
								Loger.i("TEST", "doInBackground city=" + city + "community=" + village + "buildnum="
										+ rooft + "aptnum=" + number);
								SharedPreferences preferences = getSharedPreferences(App.REGISTERED_USER,
										Context.MODE_PRIVATE);
								int addressTag = preferences.getInt("address_tag", 0);
								reference.put("addr_cache", addressTag);
								Loger.i("TEST", "222222222222222222-->" + addressTag);
								AsyncHttpClient client = new AsyncHttpClient();
								client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
										new JsonHttpResponseHandler() {
									@Override
									public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
										Loger.i("TEST", "onSuccess");
										JSONObject jsonContext = response;
										flag = null;
										try {
											Loger.i("LYM", "add flag=" + response);
											flag = jsonContext.getString("flag");
											String addressTag = jsonContext.getString("addr_flag");
											if (flag.equals("full")) {
												setaddrRequestSet = false;
												ylProgressDialog.dismiss();
												Toast.makeText(write_address.this, jsonContext.getString("yl_msg"),
														Toast.LENGTH_SHORT).show();// 返回失败吐司
											}
											if (flag.equals("ok") && addressTag.equals("ok")) {
												String curfr_Id = jsonContext.getString("frecord_id");
												// 返回成功后 返回值 familycommunityid
												// familyid 写数据库 及配置文件
												familyid = jsonContext.getString("family_id");
												if (familyid.equals("0")) {
													ne_status = jsonContext.getString("ne_status");
													aFamily.setNe_status(Integer.parseInt(ne_status));
												} else {
													aFamily.setNe_status(0);
												}
												familycommunityid = village_id;
												int entityType = Integer.parseInt(jsonContext.getString("entity_type"));
												App.setAddrStatus(getApplicationContext(), entityType);
												aFamily.setEntity_type(entityType);
												Loger.i("TEST", "get familyid=" + familyid + "get familycommunityid="
														+ familycommunityid);
												aFamily.setFamily_city(city);
												aFamily.setFamily_community(village);
												aFamily.setFamily_building_num(rooft);
												aFamily.setFamily_apt_num(number);
												aFamily.setFamily_city_id(Long.parseLong(city_id));
												aFamily.setfamily_city_code(city_code);
												aFamily.setapt_num_id(Long.parseLong(number_id));
												aFamily.setbuilding_num_id(Long.parseLong(rooft_id));
												aFamily.setFamily_block_id(Long.parseLong(block_id));
												aFamily.setFamily_id(Long.parseLong(familyid));
												aFamily.setFamily_community_id(Long.parseLong(familycommunityid));
												aFamily.setLogin_account(App.sUserLoginId);
												try {
													aFamily.setFamily_address_id(Long.parseLong(curfr_Id));
												} catch (NumberFormatException e) {
													Toast.makeText(getApplicationContext(), "Number=>" + e.getMessage(),
															Toast.LENGTH_SHORT).show();
												}
												aFamily.saveFamilyInfos();
												allFamilyDaoDBImpl.saveObject(aFamily);
												allFamilyDaoDBImpl.releaseDatabaseRes();
												// bindPushManager(MainActivity.bPushBindStatus);
												setaddrRequestSet = true;
												// saveIfPushSuccess(getIfPushSuccess()+1);
											} else {
												setaddrRequestSet = false;
												ylProgressDialog.dismiss();
												Toast.makeText(write_address.this, "地址设置失败", Toast.LENGTH_SHORT).show();// 返回失败吐司
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch
											e.printStackTrace();
											Loger.i("test4", e.getMessage());
										}
										super.onSuccess(statusCode, headers, response);
									}

									@Override
									public void onFailure(int statusCode, Header[] headers, String responseString,
											Throwable throwable) {
										Loger.i("TEST", responseString);
										setaddrRequestSet = false;
										new ErrorServer(write_address.this, responseString);
										ylProgressDialog.dismiss();
										super.onFailure(statusCode, headers, responseString, throwable);
									}
								});
								new AsyncTask<Void, Void, Void>() {
									@Override
									protected Void doInBackground(Void... params) {
										// 网络请求 注册地址
										// 参数{city,village,rooft,number}
										// login_account
										// Thread.sleep(2000);
										Long currenttime = System.currentTimeMillis();
										while (!setaddrRequestSet) {
											if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
												setaddrRequestSet = true;
											}
										}

										return null;
									}

									@Override
									protected void onPostExecute(Void result) {
										super.onPostExecute(result);
										// 返回成功后 返回值 familycommunityid familyid
										// 写数据库 及配置文件

										if (setaddrRequestSet && flag != null && flag.equals("ok")) {
											setaddrRequestSet = false;
											write_address.this.setResult(RESULT_OK, intent);
											// MainActivity.setcurrentaddressTextView(village);
											rooft_id = "0";
											number_id = "0";
											ne_status = "0";
											selectvillageActivity.buildnumlist.clear();
											finish();
										} else {
											Toast.makeText(write_address.this, "地址设置失败", Toast.LENGTH_SHORT).show();
										}
										block_id = "0";
										village_id = "0";
										ylProgressDialog.dismiss();
									}
									// startActivity(new
									// Intent(write_address.this,addressshow.class));
								}.execute();

							} else if (setaddrmethod.equals("change")) {
								String strfamilyId = intent.getStringExtra("addressitemindex");
								String strfamilySubId = intent.getStringExtra("addresssubitemindex");
								// Loger.d("test4",
								// "strfamilyId="+strfamilyId+"strfamilySubId="+strfamilySubId);
								final AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(
										Long.parseLong(strfamilyId), Integer.parseInt(strfamilySubId),
										App.sUserLoginId);
								final String OldFamilyaddress = allFamily.getFamily_address();
								if (allFamily.getPrimary_flag() == 1) {
									iscurrentaddr = true;
								} else {
									iscurrentaddr = false;
								}
								// Loger.d("test4",
								// "allFamily.getFamily_address="+allFamily.getFamily_address());
								// Loger.d("test4",
								// "allFamily.makeFamily_address="+MainActivity.currentcity
								// + MainActivity.currentvillage
								// + loadAddrdetailPrefrence());
								ylProgressDialog.show();
								RequestParams reference = new RequestParams();
								Loger.d("test4", "change familyID=" + strfamilyId + "strfamilySubId="
										+ Integer.parseInt(strfamilySubId) + "user_id=" + App.sUserLoginId);
								reference.put("city", city);
								reference.put("community", village);
								if (!block_id.equals("0"))
									reference.put("block", block);
								reference.put("buildnum", rooft);
								reference.put("aptnum", number);
								reference.put("city_code", city_code);
								reference.put("city_id", Integer.parseInt(city_id));// city_id
								reference.put("community_id", Integer.parseInt(village_id));// village_id
								reference.put("block_id", Integer.parseInt(block_id));
								reference.put("buildnum_id", Integer.parseInt(rooft_id));// rooft_id
								reference.put("aptnum_id", Integer.parseInt(number_id));// number_id
								Loger.d("test4", "Integer.parseInt(number_id)=" + Integer.parseInt(number_id));
								reference.put("ne_status", Integer.parseInt(strfamilySubId));
								reference.put("user_id", App.sUserLoginId);
								reference.put("family_id", strfamilyId);
								reference.put("tag", "changefamily");
								reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
								Loger.d("test4",
										"doInBackground new  city_id=" + city_id + "city_code=" + city_code
												+ "community_id=" + village_id + "block_id" + block_id + "buildnum_id="
												+ rooft_id + "aptnum_id=" + Integer.parseInt(number_id));
								Loger.d("test4", "doInBackground new city=" + city + "community=" + village
										+ "buildnum=" + rooft + "aptnum=" + number);
								SharedPreferences preferences = getSharedPreferences(App.REGISTERED_USER,
										Context.MODE_PRIVATE);
								int addressTag = preferences.getInt("address_tag", 0);
								reference.put("addr_cache", addressTag);
								AsyncHttpClient client = new AsyncHttpClient();
								client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
										new JsonHttpResponseHandler() {
									@Override
									public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
										Loger.i("test4", "onSuccess");
										JSONObject jsonContext = response;
										try {
											Loger.i("LYM", "change flag=" + response);
											flag = jsonContext.getString("flag");
											String addressTag = jsonContext.getString("addr_flag");
											if (flag.equals("ok") && addressTag.equals("ok")) {
												String curfr_Id = jsonContext.getString("frecord_id");
												familyid = jsonContext.getString("family_id");
												if (familyid.equals("0")) {
													ne_status = jsonContext.getString("ne_status");
													allFamily.setNe_status(Integer.parseInt(ne_status));
												} else {
													allFamily.setNe_status(0);
												}
												familycommunityid = village_id;
												// allFamily.setEntity_type(Integer.parseInt(jsonContext.getString("entity_type")));
												// Loger.i("test4",
												// "change
												// familyid="+familyid+"change
												// familycommunityid="+familycommunityid+"ne_status="+ne_status+"entity_type"+jsonContext.getString("entity_type"));

												allFamily.setFamily_city(city);
												allFamily.setFamily_community(village);
												allFamily.setFamily_building_num(rooft);
												allFamily.setFamily_apt_num(number);
												allFamily.setFamily_city_id(Long.parseLong(city_id));
												allFamily.setfamily_city_code(city_code);
												allFamily.setFamily_block_id(Long.parseLong(block_id));
												allFamily.setFamily_id(Long.parseLong(familyid));
												allFamily.setFamily_community_id(Long.parseLong(familycommunityid));
												allFamily.setLogin_account(App.sUserLoginId);
												try {
													allFamily.setFamily_address_id(Long.parseLong(curfr_Id));
												} catch (NumberFormatException e) {
													Toast.makeText(getApplicationContext(), "Number=>" + e.getMessage(),
															Toast.LENGTH_SHORT).show();
												}
												allFamily.saveFamilyInfos();
												allFamilyDaoDBImpl.modifyObject(allFamily, OldFamilyaddress);
												allFamilyDaoDBImpl.releaseDatabaseRes();
												setaddrRequestSet = true;
												Loger.i("test4",
														"change familyid=" + familyid + "change familycommunityid="
																+ familycommunityid + "ne_status=" + ne_status);
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
											Loger.i("test4", e.getMessage());
										}
										super.onSuccess(statusCode, headers, response);
									}

									@Override
									public void onFailure(int statusCode, Header[] headers, String responseString,
											Throwable throwable) {
										Loger.i("test4", responseString);
										// test22.setText(responseString);
										setaddrRequestSet = false;
										ylProgressDialog.dismiss();
										new ErrorServer(write_address.this, responseString);
										super.onFailure(statusCode, headers, responseString, throwable);
									}
								});
								new AsyncTask<Void, Void, Void>() {
									@Override
									protected Void doInBackground(Void... params) {
										// 网络请求 注册地址
										// 参数{city,village,rooft,number}
										// login_account
										Long currenttime = System.currentTimeMillis();
										while (!setaddrRequestSet) {
											if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
												setaddrRequestSet = true;
											}
										}
										return null;
									}

									@Override
									protected void onPostExecute(Void result) {
										super.onPostExecute(result);
										if (setaddrRequestSet && flag != null && flag.equals("ok")) {

											setaddrRequestSet = false;
											// saveSharePrefrence(city,
											// village,rooft+number,familyid,familycommunityid);
											// /*************(city+village+rooft+number,familyid)要写入数据库
											// table_users表*/
											if (iscurrentaddr) {
												MainActivity.currentcity = city;
												MainActivity.currentvillage = village; // write
												saveSharePrefrence(city, village, rooft + "-" + number, familyid,
														block_id, familycommunityid);
												App.setAddrStatus(getApplicationContext(), allFamily.getEntity_type());
												/*************
												 * (city+village+rooft+number,
												 * familyid)要写入数据库 table_users表
												 */
												AccountDaoDBImpl accountdao = new AccountDaoDBImpl(write_address.this);
												Account account = accountdao.findAccountByPhone(App.sUserPhone);
												account.setUser_family_id(Long.parseLong(familyid));
												account.setUser_family_address(city + village + rooft + "-" + number);
												accountdao.modifyObject(account);
												accountdao.releaseDatabaseRes();
												App.sFamilyId = Long.parseLong(familyid);
												App.sFamilyCommunityId = Long.parseLong(familycommunityid);
												App.sFamilyBlockId = Long.parseLong(block_id);

												MainActivity.setcurrentaddressTextView(village);
												// 设置PushTag
												YLPushTagManager pushTagManager = new YLPushTagManager(
														getApplicationContext());
												pushTagManager.setPushTag();
												App.sDeleteTagFinish = true;
											}
											write_address.this.setResult(RESULT_OK, intent);
											rooft_id = "0";
											number_id = "0";
											ne_status = "0";
											selectvillageActivity.buildnumlist.clear();
											finish();
										} else {
											Toast.makeText(write_address.this, "添加地址失败", Toast.LENGTH_SHORT);
										}
										ylProgressDialog.dismiss();
									}
								}.execute();
							}
						} else
							Toast.makeText(write_address.this, "请先开启网络", Toast.LENGTH_SHORT).show();
					}
				} else if (NetworkService.networkBool) {
					if (setaddrmethod.equals("change")) {
						String num = number_fragment.numbermessage.getText().toString();
						String vil = rooft_fragment.rooftmessage.getText().toString();
						final AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(write_address.this);

						final List<Object> Pointuseraddress = allFamilyDaoDBImpl.findPointTypeObject(App.sUserLoginId);
						for (int i = 0; i < Pointuseraddress.size(); i++) {
							if (((AllFamily) Pointuseraddress.get(i)).getFamily_address()
									.equals(write_address.city + write_address.village + vil + "-" + num)) {
								AddrRepeat = "repeat";
							}
						}
						if (vil.length() < 1) {
							Toast.makeText(write_address.this, "请设置所在单元", Toast.LENGTH_SHORT).show();
							rooft_fragment.setimage.setVisibility(View.VISIBLE);
							rooft_fragment.setimage.setBackgroundResource(R.drawable.tanhao);
							return;	
						} else if (num.length() < 1) {
							Toast.makeText(write_address.this, "请设置门牌号", Toast.LENGTH_SHORT).show();
							number_fragment.setimage1.setVisibility(View.VISIBLE);
							number_fragment.setimage1.setBackgroundResource(R.drawable.tanhao);
							return;	
						} else if (AddrRepeat.equals("repeat")) {
							AddrRepeat = "undefine";
							Toast.makeText(write_address.this, "此地址已被添加", Toast.LENGTH_SHORT).show();
							return;	
						} else {
							write_address.rooft = vil;
							write_address.number = num;
							final AllFamily aFamily = new AllFamily(write_address.this);
							String[] useraddr = new String[] { city, village, rooft, number };
							aFamily.setUser_alias(Usernamenick);
							if (NetworkService.networkBool) {
								for (int i = 0; i < number_fragment.aptlist.size(); i++) {
									Loger.d("test4", "number_fragment number circle" + i);
									if (number_fragment.aptlist.get(i).get("name").equals(num)) {
										number_id = number_fragment.aptlist.get(i).get("_id").toString();
									}
								}
								write_address.rooft_id = number_fragment.selectedbuild_id;
								write_address.rooft = number_fragment.selectedbuild;
							} else {
								Toast.makeText(write_address.this, "请先开启网络", Toast.LENGTH_SHORT).show();
								return;
							}
						}

						write_address.rooft = vil;
						write_address.number = num;
						for (int i = 0; i < number_fragment.aptlist.size(); i++) {
							Loger.d("test4", "number_fragment number circle" + i);
							if (number_fragment.aptlist.get(i).get("name").equals(num)) {
								number_id = number_fragment.aptlist.get(i).get("_id").toString();
							}
						}
						if (!number_fragment.selectedbuild_id.equals("0")) {
							write_address.rooft_id = number_fragment.selectedbuild_id;
						}
						Loger.d("test4", number_fragment.selectedbuild_id);

						String strfamilyId = intent.getStringExtra("addressitemindex");
						String strfamilySubId = intent.getStringExtra("addresssubitemindex");
						// Loger.d("test4",
						// "strfamilyId="+strfamilyId+"strfamilySubId="+strfamilySubId);
//						final AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(write_address.this);
						final AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(Long.parseLong(strfamilyId),
								Integer.parseInt(strfamilySubId), App.sUserLoginId);
						final String OldFamilyaddress = allFamily.getFamily_address();
						if (allFamily.getPrimary_flag() == 1) {
							iscurrentaddr = true;
						} else {
							iscurrentaddr = false;
						}
						// Loger.d("test4",
						// "allFamily.getFamily_address="+allFamily.getFamily_address());
						// Loger.d("test4",
						// "allFamily.makeFamily_address="+MainActivity.currentcity
						// + MainActivity.currentvillage
						// + loadAddrdetailPrefrence());
						ylProgressDialog.show();
						RequestParams reference = new RequestParams();
						Loger.d("test6", "change familyID=" + strfamilyId + "strfamilySubId="
								+ Integer.parseInt(strfamilySubId) + "user_id=" + App.sUserLoginId);
						reference.put("city", city);
						reference.put("community", village);
						if (!block_id.equals("0"))
							reference.put("block", block);
						reference.put("buildnum", rooft);
						reference.put("aptnum", number);
						reference.put("city_code", city_code);
						reference.put("city_id", write_address.city_id);// city_id
						reference.put("community_id", write_address.village_id);// village_id
						reference.put("block_id", write_address.block_id);
						reference.put("buildnum_id", write_address.rooft_id);// rooft_id
						reference.put("aptnum_id", write_address.number_id);// number_id
						Loger.d("test4", "Integer.parseInt(number_id)=" + Integer.parseInt(number_id));
						reference.put("ne_status", Integer.parseInt(strfamilySubId));
						reference.put("user_id", App.sUserLoginId);
						reference.put("family_id", strfamilyId);
						reference.put("tag", "changefamily");

						// doInBackground new
						// city_id=1city_code=48community_id=1block_id0buildnum_id=3aptnum_id=44doInBackground
						// new
						// city=哈尔滨市community=方德之家buildnum=OS应用开发一部aptnum=701
						reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
						Loger.d("test6",
								"doInBackground new  city_id=" + city_id + "city_code=" + city_code + "community_id="
										+ village_id + "block_id" + block_id + "buildnum_id=" + rooft_id + "aptnum_id="
										+ number_id + "doInBackground new city=" + city + "community=" + village
										+ "buildnum=" + rooft + "aptnum=" + number);
						SharedPreferences preferences = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
						int addressTag = preferences.getInt("address_tag", 0);
						reference.put("addr_cache", addressTag);
						AsyncHttpClient client = new AsyncHttpClient();
						client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
								new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
								Loger.i("LYM", "onSuccess" + response.toString());
								JSONObject jsonContext = response;
								try {
									flag = jsonContext.getString("flag");
									String addressTag = jsonContext.getString("addr_flag");
									Loger.i("test4", "change flag=" + flag);
									if (flag.equals("ok") && addressTag.equals("ok")) {
										String curfr_Id = jsonContext.getString("frecord_id");
										familyid = jsonContext.getString("family_id");
										if (familyid.equals("0")) {
											ne_status = jsonContext.getString("ne_status");
											allFamily.setNe_status(Integer.parseInt(ne_status));
										} else {
											allFamily.setNe_status(0);
										}
										familycommunityid = village_id;
										// allFamily.setEntity_type(Integer.parseInt(jsonContext.getString("entity_type")));
										// Loger.i("test4",
										// "change familyid="+familyid+"change
										// familycommunityid="+familycommunityid+"ne_status="+ne_status+"entity_type"+jsonContext.getString("entity_type"));

										allFamily.setFamily_city(city);
										allFamily.setFamily_community(village);
										allFamily.setFamily_building_num(rooft);
										allFamily.setFamily_apt_num(number);
										allFamily.setFamily_city_id(Long.parseLong(city_id));
										allFamily.setfamily_city_code(city_code);
										allFamily.setapt_num_id(Long.parseLong(number_id));
										allFamily.setbuilding_num_id(Long.parseLong(rooft_id));
										allFamily.setFamily_block_id(Long.parseLong(block_id));
										Loger.d("test6", "change familyid" + familyid);
										allFamily.setFamily_id(Long.parseLong(familyid));
										allFamily.setFamily_community_id(Long.parseLong(familycommunityid));
										allFamily.setEntity_type(0);
										allFamily.setLogin_account(App.sUserLoginId);
										try {
											allFamily.setFamily_address_id(Long.parseLong(curfr_Id));
										} catch (NumberFormatException e) {
											Toast.makeText(getApplicationContext(), "Number=>" + e.getMessage(),
													Toast.LENGTH_SHORT).show();
										}
										allFamily.saveFamilyInfos();
										allFamilyDaoDBImpl.modifyObject(allFamily, OldFamilyaddress);
										allFamilyDaoDBImpl.releaseDatabaseRes();
										setaddrRequestSet = true;
										Loger.i("test4", "change familyid=" + familyid + "change familycommunityid="
												+ familycommunityid + "ne_status=" + ne_status);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch
									// block
									e.printStackTrace();
									Loger.i("test4", e.getMessage());
								}
								super.onSuccess(statusCode, headers, response);
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, String responseString,
									Throwable throwable) {
								Loger.i("test4", responseString);
								// test22.setText(responseString);
								setaddrRequestSet = false;
								ylProgressDialog.dismiss();
								new ErrorServer(write_address.this, responseString);
								super.onFailure(statusCode, headers, responseString, throwable);
							}
						});
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {
								// 网络请求 注册地址 参数{city,village,rooft,number}
								// login_account
								Long currenttime = System.currentTimeMillis();
								while (!setaddrRequestSet) {
									if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
										setaddrRequestSet = true;
									}
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								if (setaddrRequestSet && flag != null && flag.equals("ok")) {

									setaddrRequestSet = false;
									// saveSharePrefrence(city,
									// village,rooft+number,familyid,familycommunityid);
									// /*************(city+village+rooft+number,familyid)要写入数据库
									// table_users表*/
									if (iscurrentaddr) {
										MainActivity.currentcity = city;
										MainActivity.currentvillage = village; // write
										saveSharePrefrence(city, village, rooft + "-" + number, familyid, block_id,
												familycommunityid);
										App.setAddrStatus(getApplicationContext(), allFamily.getEntity_type());
										/*************
										 * (city+village+rooft+number,familyid)
										 * 要写入数据库 table_users表
										 */
										AccountDaoDBImpl accountdao = new AccountDaoDBImpl(write_address.this);
										Account account = accountdao.findAccountByPhone(App.sUserPhone);
										account.setUser_family_id(Long.parseLong(familyid));
										account.setUser_family_address(city + village + rooft + "-" + number);
										accountdao.modifyObject(account);
										accountdao.releaseDatabaseRes();
										App.sFamilyId = Long.parseLong(familyid);
										App.sFamilyCommunityId = Long.parseLong(familycommunityid);
										App.sFamilyBlockId = Long.parseLong(block_id);

										MainActivity.setcurrentaddressTextView(village);
										// 设置PushTag
										YLPushTagManager pushTagManager = new YLPushTagManager(getApplicationContext());
										pushTagManager.setPushTag();
										App.sDeleteTagFinish = true;
									}
									write_address.this.setResult(RESULT_OK, intent);
									rooft_id = "0";
									number_id = "0";
									ne_status = "0";
									selectvillageActivity.buildnumlist.clear();
									finish();
								} else {
									Toast.makeText(write_address.this, "地址更改失败", Toast.LENGTH_SHORT);
								}
								ylProgressDialog.dismiss();
							}
						}.execute();
					}
					selectvillageActivity.buildnumlist.clear();
				} else {
					Toast.makeText(write_address.this, "请先开启网络", Toast.LENGTH_SHORT).show();
				}
			}
		});
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		tx.replace(R.id.addrset3, rooftfragment);
		// tx.addToBackStack(null);
		tx.replace(R.id.addrset4, numberfragment);
		tx.commit();
		if (setaddrmethod.equals("change")) {
			String strfamilyId = intent.getStringExtra("addressitemindex");
			String strfamilySubId = intent.getStringExtra("addresssubitemindex");
			Loger.d("test4", "strfamilyId=" + strfamilyId + "strfamilySubId=" + strfamilySubId);
			final AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(write_address.this);
			final AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(Long.parseLong(strfamilyId),
					Integer.parseInt(strfamilySubId), App.sUserLoginId);
			write_address.city = allFamily.getFamily_city();
			write_address.city_id = String.valueOf(allFamily.getFamily_city_id());
			write_address.village = String.valueOf(allFamily.getFamily_community());
			write_address.village_id = String.valueOf(allFamily.getFamily_community_id());
			write_address.block = allFamily.getFamily_block();
			write_address.block_id = String.valueOf(allFamily.getFamily_block_id());
			write_address.city_code = allFamily.getfamily_city_code();
			write_address.rooft = allFamily.getFamily_building_num();
			write_address.rooft_id = String.valueOf(allFamily.getbuilding_num_id());
			write_address.number_id = String.valueOf(allFamily.getapt_num_id());
			Loger.d("test4", "city_code=" + city_code + "rooft_id=" + rooft_id + "number_id=" + number_id);
			Loger.d("test4", "write_address.city=" + write_address.city + " village=" + write_address.village);

			write_address.number = allFamily.getFamily_apt_num();

			Handler mHandler = new Handler();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					rooft_fragment.rooftmessage.setText(String.valueOf(allFamily.getFamily_building_num()));
					number_fragment.numbermessage.setText(allFamily.getFamily_apt_num());
				}
			});

			if (block_id.equals("0")) {
				getbuildinghttpdata("community_id", write_address.village_id, IHttpRequestUtils.YOULIN);
			} else {
				getbuildinghttpdata("block_id", block_id, IHttpRequestUtils.YOULIN);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ylProgressDialog.show();
						}
					});
					Long currenttime = System.currentTimeMillis();
					while (!bRequestSet) {
						if ((System.currentTimeMillis() - currenttime) > 4000 + 5000) {
							bRequestSet = true;
						}
					}
					Message msg = new Message();
					msg.what = REQUEST_OK;
					buildhandler.sendMessage(msg);
					return;
				}
			}).start();
			final String[] strs1 = new String[] { "选城市" };
			String[] strs2 = new String[] { allFamily.getFamily_city() };
			write_address.city = allFamily.getFamily_city();
			list = getData(strs1, strs2);
			SimpleAdapter simpleadapter1 = new SimpleAdapter(this, list, R.layout.citydetail,
					new String[] { "setcity", "city", "img" }, new int[] { R.id.space, R.id.setspace, R.id.setimg });
			listview1.setAdapter(simpleadapter1);

			final String[] strs3 = new String[] { "填小区" };
			String[] strs4 = null;
			write_address.village = allFamily.getFamily_community();
			strs4 = new String[] { allFamily.getFamily_community() };
			list = getData(strs3, strs4);
			simpleadapter1 = new SimpleAdapter(this, list, R.layout.village_detail, new String[] { "setcity", "city" },
					new int[] { R.id.village, R.id.setvillage });
			listview2.setAdapter(simpleadapter1);
			listview1.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					// TODO Auto-generated method stub
					Loger.d("hyytest", strs1[position] + "被选中");
					Intent intent = new Intent(write_address.this, SelectaddrActivity.class);
					intent.putExtra("oldcity", write_address.city);
					startActivityForResult(intent, GET_CURRENT_CITY);
				}
			});
			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					// TODO Auto-generated method stub
					Loger.d("hyytest", strs3[position] + "被选中");
					if (write_address.this.city_id.equals("未定义")) {
						Toast.makeText(write_address.this, "请先选择城市！", Toast.LENGTH_SHORT);
					} else {
						Intent intent = new Intent(write_address.this, selectvillageActivity.class);
						intent.putExtra("selectedcity", write_address.city);
						// intent.putExtra("selectedcityCode",
						// write_address.this.city_code);
						startActivityForResult(intent, GET_CURRENT_VILLAGE);
					}

				}
			});
			allFamilyDaoDBImpl.releaseDatabaseRes();
		} else {
			final String[] strs1 = new String[] { "选城市" };
			String[] strs2 = new String[] { MainActivity.currentcity };
			if (!MainActivity.currentcity.equals("未设置")) {
				write_address.city = MainActivity.currentcity;
			}
			list = getData(strs1, strs2);
			SimpleAdapter simpleadapter1 = new SimpleAdapter(this, list, R.layout.citydetail,
					new String[] { "setcity", "city", "img" }, new int[] { R.id.space, R.id.setspace, R.id.setimg });
			listview1.setAdapter(simpleadapter1);
			final String[] strs3 = new String[] { "填小区" };
			String[] strs4 = null;
			if (((!write_address.village_id.equals("0")) && (!write_address.village.equals("未定义")))
					|| ((!write_address.block_id.equals("0")) && (!write_address.block.equals("")))) {
				write_address.village = MainActivity.currentvillage;
				strs4 = new String[] { write_address.village };
			} else {
				strs4 = new String[] { "未设置" };
			}
			list = getData(strs3, strs4);
			simpleadapter1 = new SimpleAdapter(this, list, R.layout.village_detail, new String[] { "setcity", "city" },
					new int[] { R.id.village, R.id.setvillage });
			listview2.setAdapter(simpleadapter1);

			listview1.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					// TODO Auto-generated method stub
					Loger.d("hyytest", strs1[position] + "被选中");
					Intent intent = new Intent(write_address.this, SelectaddrActivity.class);
					intent.putExtra("oldcity", MainActivity.currentcity);
					startActivityForResult(intent, GET_CURRENT_CITY);
				}
			});
			listview2.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					// TODO Auto-generated method stub
					Loger.d("hyytest", strs3[position] + "被选中");
					if (write_address.this.city_id.equals("未定义")) {
						Toast.makeText(write_address.this, "请先选择城市！", Toast.LENGTH_SHORT);
					} else {
						Intent intent = new Intent(write_address.this, selectvillageActivity.class);
						intent.putExtra("selectedcity", write_address.city);
						// intent.putExtra("selectedcityCode",
						// write_address.this.city_code);
						startActivityForResult(intent, GET_CURRENT_VILLAGE);
					}

				}
			});
		}

	}

	private List<Map<String, Object>> AddData(List<Map<String, Object>> oldlist, String strname, String strid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", strname);
		map.put("_id", strid);
		map.put("street", strid);
		oldlist.add(map);
		return oldlist;
	}

	private Handler buildhandler = new Handler() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REQUEST_OK: {
				Loger.i("test3", "BuildngjsonObjList->" + BuildngjsonObjList.size());
				ylProgressDialog.dismiss();
				if (BuildngjsonObjList.size() < 0) {
					break;
				}
				selectvillageActivity.buildnumlist.clear();
				for (int i = 0; i < BuildngjsonObjList.size(); i++) {

					try {
						String buildingname = BuildngjsonObjList.get(i).getJSONObject("fields")
								.getString("building_name");
						String buildingid = BuildngjsonObjList.get(i).getString("pk");
						Loger.d("test3", "buildingname=" + buildingname + "buildingid=" + buildingid);
						AddData(selectvillageActivity.buildnumlist, buildingname, buildingid);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Loger.i("test3", "555555555555555");
						e.printStackTrace();
					}
				}

			}
				break;
			default:
				break;
			}
		}
	};

	private void getbuildinghttpdata(String request_index, String request_name, String http_addr) {
		BuildngjsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		params.put(request_index, request_name);
		params.put("tag", "addr");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL + http_addr, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
				BuildngjsonObjList.clear();
				try {
					org.json.JSONObject obj = null;
					for (int i = 0; i < jsonContext.length(); i++) {
						BuildngjsonObjList.add(jsonContext.getJSONObject(i));
					}
					Loger.d("test4", "BuildngjsonObjList = " + BuildngjsonObjList);
					bRequestSet = true;
				} catch (org.json.JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "OK(error)->" + e.getMessage());
					bRequestSet = false;
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, String responseString,
					Throwable throwable) {
				// TODO Auto-generated method stub
				Loger.i("TEST", responseString + "\r\n" + throwable.toString() + "\r\n-----\r\n" + statusCode);
				BuildngjsonObjList.clear();
				bRequestSet = false;
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		city = "未定义";
		village = "未定义";
		rooft = "未定义";
		number = "未定义";
		// DemoApplication.getInstance().addresslist = addresslist ;
		// DemoApplication.getInstance().addressadapter.open();
		// DemoApplication.getInstance().addressadapter.setaddresslist(addresslist);
		// DemoApplication.getInstance().addressadapter.close();
	}

	private List<Map<String, Object>> getData(String[] strs, String[] strs2) {
		// List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < strs.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("setcity", strs[i]);
			map.put("city", strs2[i]);
			map.put("img", R.drawable.dingwei);
			list.add(map);

		}

		return list;
	}

	public void saveSharePrefrence(String city, String village, String detail, String familyid, String blockid,
			String fimalycommunityid) {
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("blockid", blockid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.commit();
	}

	private String loadUsernamePrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("username", "未设置");
	}

	private String loadAddrdetailPrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == GET_CURRENT_CITY) {
				Loger.d("hyytest", "GET_CURRENT_CITY result RESULT_OK");
				try {
					double latitude = data.getDoubleExtra("latitude", 0);
					Loger.d("hyytest", "latitude = " + latitude);
					double longitude = data.getDoubleExtra("longitude", 0);
					Loger.d("hyytest", "longitude = " + longitude);
					String locationAddress = data.getStringExtra("address");
					Loger.d("hyytest", "locationAddress = " + locationAddress);
					String city = data.getStringExtra("city");
					Loger.d("hyytest", "city = " + city);
					String citycode = data.getStringExtra("city_id");
					String[] strs1 = new String[] { "选城市" };
					String[] strs2 = new String[] { "" };
					strs2[0] = city;
					list = getData(strs1, strs2);
					final SimpleAdapter simpleadapter1 = new SimpleAdapter(this, list, R.layout.citydetail,
							new String[] { "setcity", "city", "img" },
							new int[] { R.id.space, R.id.setspace, R.id.setimg });
					listview1.setAdapter(simpleadapter1);
					this.city = city;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// this.city_id = "2";
				// this.city_code = "0451";
				number_fragment.numbermessage.setText("");
				rooft_fragment.rooftmessage.setText("");
				write_address.village = "未定义";
				write_address.rooft = "未定义";
				write_address.number = "未定义";
				write_address.city_code = "未定义";
				write_address.block = "未定义";
				write_address.city_id = "0";
				write_address.village_id = "0";
				write_address.rooft_id = "0";
				write_address.number_id = "0";
				write_address.block_id = "0";
				this.familycommunityid = "0000000000";
				this.familyid = "0000000000";
				Intent intent = new Intent(write_address.this, selectvillageActivity.class);
				intent.putExtra("selectedcity", write_address.city);
				// intent.putExtra("selectedcityCode",
				// write_address.this.city_code);
				startActivityForResult(intent, GET_CURRENT_VILLAGE);
			}
			if (requestCode == GET_CURRENT_VILLAGE) {
				Loger.d("hyytest", "GET_CURRENT_VILLAGE result RESULT_OK");
				String village = data.getStringExtra("village");
				String village_id = data.getStringExtra("village_id");
				String block = data.getStringExtra("block_name");
				String block_id = data.getStringExtra("block_id");
				Loger.d("hyytest", "village = " + village);
				String[] strs3 = new String[] { "填小区" };
				String[] strs4 = new String[] { "" };
				strs4[0] = village;
				list = getData(strs3, strs4);
				final SimpleAdapter simpleadapter1 = new SimpleAdapter(this, list, R.layout.village_detail,
						new String[] { "setcity", "city" }, new int[] { R.id.village, R.id.setvillage });
				listview2.setAdapter(simpleadapter1);
				write_address.village = village;
				write_address.village_id = village_id;

				write_address.block = block;
				write_address.block_id = block_id;
				try {
					rooft_fragment.rooftmessage.setText("");
					number_fragment.numbermessage.setText("");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Loger.d("test3",
						"block =" + block + "block_id=" + block_id + "village=" + village + "village_id=" + village_id);

			}
		}

	}

	private void gethttpdata(String apt_num, Long param2, Long param3, String http_addr, int request_index) {
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		Loger.d("test3", "apt_num=" + apt_num + "block_id=" + param2 + "community_id=" + param3);
		if (request_index == REQUEST_NEIGHBOR) {
			params.put("apt_num", apt_num);
			if (!param2.equals("0") && param2 != null)
				params.put("block_id", param2);
			params.put("community_id", param3);
		}
		params.put("tag", "neighbors");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		client.post(IHttpRequestUtils.URL + http_addr, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				String flag = null;
				// String community_id = null;
				// String community_name = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set neighbor flag->" + flag);

					if (flag.equals("none_f_o1")) {
						bRequestSet = true;
					} else {
						bRequestSet = false;
					}
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					bRequestSet = false;
					Loger.i("test3", "JSONObject->" + e.getMessage());
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
				Loger.d("test3", jsonContext.toString());
				try {
					org.json.JSONObject obj = null;
					if (jsonContext.length() > 0) {
						for (int i = 0; i < jsonContext.length(); i++) {
							jsonObjList.add(jsonContext.getJSONObject(i));
							obj = jsonContext.getJSONObject(i);
						}
						bRequestSet = true;
						flag = "ok";
					}

				} catch (org.json.JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "OK(error)->" + e.getMessage());
					bRequestSet = false;
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, String responseString,
					Throwable throwable) {
				// TODO Auto-generated method stub
				bRequestSet = false;
				ylProgressDialog.dismiss();
				new ErrorServer(write_address.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REQUEST_OK:
				if (requestTHttphgread != null) {
					requestTHttphgread.interrupt();
					requestTHttphgread = null;
				}
				Long Belong_family_id = msg.getData().getLong("family_id");
				if (bRequestSet && flag != null && flag.equals("ok")) {
					bRequestSet = false;
					flag = "";
					try {
						ylProgressDialog.dismiss();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					Loger.d("test3", "Belong_family_id=" + Belong_family_id);
					Loger.i("test3", "jsonObjList->" + jsonObjList.size());
					if (jsonObjList.size() > 0) {
						aNeighborDaoDBImpl.deleteObject(Belong_family_id);
					}
					for (int i = 0; i < jsonObjList.size(); i++) {

						try {
							if (Long.parseLong(jsonObjList.get(i).getString("family_id")) > 0) {
								neighbor.setAptnum(jsonObjList.get(i).getString("aptnum"));
								neighbor.setUser_id(Long.parseLong(jsonObjList.get(i).getString("user_id")));
								neighbor.setLogin_account(App.sUserLoginId);
								neighbor.setUser_family_id(Long.parseLong(jsonObjList.get(i).getString("family_id")));
								neighbor.setBelong_family_id(Belong_family_id);
								neighbor.setUser_name(jsonObjList.get(i).getString("user_nick"));
								neighbor.setUser_portrait(jsonObjList.get(i).getString("user_portrait"));
								neighbor.setBuilding_num(jsonObjList.get(i).getString("building_num"));
								neighbor.setProfession(jsonObjList.get(i).getString("user_profession"));
								neighbor.setBriefdesc(jsonObjList.get(i).getString("user_signature"));
								neighbor.setAddrstatus(jsonObjList.get(i).getString("user_public_status"));
							} else {
								continue;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						aNeighborDaoDBImpl.saveObject(neighbor);

					}

				}
				Intent intent = new Intent();
				intent.setAction("com.nfs.youlin.find.FindFragment");
				intent.putExtra("family_id", Belong_family_id);
				write_address.this.sendOrderedBroadcast(intent, null);
				selectvillageActivity.buildnumlist.clear();
				finish();
			}
		}
	};

	@Override
	public void setstatuschanged(int status) {
		// TODO Auto-generated method stub
		if (status == 1) {
			if (write_address.searchresult.size() > 0) {
				Intent intent = new Intent(write_address.this, Searchresultlistactivity.class);
				intent.putExtra("startactivity", "rooft_fragment");
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}

		} else if (status == 2) {
			final SimpleAdapter adapter = new SimpleAdapter(write_address.this, searchresult, R.layout.villagelist,
					new String[] { "name" }, new int[] { R.id.villagename });
			if (write_address.searchresult.size() > 0) {
				Intent intent = new Intent(write_address.this, Searchresultlistactivity.class);
				intent.putExtra("startactivity", "number_fragment");
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(write_address.this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(write_address.this);
		MobclickAgent.onPause(this);
	}

	@Override
	public void setstatuschanged(int status, Bundle data) {
		// TODO Auto-generated method stub

	}
}
