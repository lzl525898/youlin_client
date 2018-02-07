package com.nfs.youlin.activity.personal;

import cn.jpush.android.api.JPushInterface;

import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.find.AddrVerifiyActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.push.YLPushTagManager;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class moreaddrdetail extends Activity{
	private final String[] moreitem0 = new String[]{"设为当前地址","修改地址","删除地址","取消"};
	private final String[] moreitem1 = new String[]{"修改地址","删除地址","取消"};
	private final String[] kuaisuyanzheng0 = new String[]{"设为当前地址","修改地址","删除地址","快速验证","取消"};
	private final String[] kuaisuyanzheng1 = new String[]{"修改地址","删除地址","快速验证","取消"};
	private int whichaddress;
	private  int oldindex = 0;
	private String familyId;
	private String familySubId;
	private long familyRId;
	private int familyEnStatus;
	private int familyPrimary;
	private static final int CHANGE_ADDRESS_REQUEST =5;
	private YLProgressDialog ylProgressDialog;
	private boolean dRequestSet = false;
	private boolean cRequestSet = false;
	private boolean bRequestSet = false;
	private AllFamilyDaoDBImpl allFDaoDBImpl;
	public static List<JSONObject> jsonObjList;
	private String flag = null;
	private Neighbor neighbor;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private int REQUEST_NEIGHBOR = 1;
	private Thread requestTHttphgread;
	protected static final int REQUEST_OK = 100;
	void buildprocessdialog(){
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
//		pd1 = new ProgressDialog(this);
//		pd1.setTitle("正在提交地址信息");
//		pd1.setMessage("正在玩命为您加载，请稍侯...");
//		pd1.setCancelable(false);
//		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd1.setIndeterminate(false);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        this.setContentView(R.layout.moreaddrdetail);
	        buildprocessdialog();
	        final Intent intent = getIntent();
	        whichaddress = intent.getIntExtra("addressitemindex",0);
	        familyId = intent.getStringExtra("familyId");
	        familySubId = intent.getStringExtra("familySubId");
	        familyEnStatus = intent.getIntExtra("familyEnType", -1);
	        familyRId = intent.getLongExtra("familyRecordId", -1);
	        /****************** database ***********************/
	        allFDaoDBImpl = new AllFamilyDaoDBImpl(this);
			final List<Object> familyObjs = allFDaoDBImpl
					.findPointTypeObject(App.sUserLoginId);
			/****************** database ***********************/
	        Loger.d("test5", "whichaddress="+whichaddress);
	        Loger.d("test5", "familyId="+familyId);
	        Loger.d("test5", "familySubId="+familySubId);
	        Loger.d("test5", "familyRecrodId="+familyRId);
	        getData(familyObjs);
		ListView moredetail = (ListView)findViewById(R.id.moredetail);
	//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.arrayitem,moreitem);

		familyPrimary = (int) ((AllFamily)familyObjs.get(whichaddress)).getPrimary_flag();
		
		List<Map<String, Object>> list;
		if(1==familyEnStatus){
			if(1==familyPrimary){
				list = getData(moreitem1);
			}else{
				list = getData(moreitem0);
			}
		}else{
			if(1==familyPrimary){
				list = getData(kuaisuyanzheng1);
			}else{
				list = getData(kuaisuyanzheng0);
			}
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.arrayitem, new String[]{"text"},new int[]{R.id.arrayitem1} );
		moredetail.setAdapter(adapter);
		final AccountDaoDBImpl accountdao = new AccountDaoDBImpl(this);
		final Account account = new Account(this);
		moredetail.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				if(1==familyEnStatus){
					if(1==familyPrimary){
//						if (moreitem1[position].equals("地址详情")) {
//							startActivity(new Intent(moreaddrdetail.this, AddressDetailActivity.class));
//						}
						if (moreitem1[position].equals("删除地址")) {
							CustomDialog.Builder builder = new CustomDialog.Builder(moreaddrdetail.this);
							//builder.setTitle("提示");
							builder.setMessage("删除后无法恢复，是否删除地址");
							builder.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									ylProgressDialog.show();
									gethttpdata(Long.parseLong(familyId), Integer.parseInt(familySubId), App.sUserLoginId,
											familyRId,IHttpRequestUtils.YOULIN);
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// 网络请求 删除地址 参数{familyid} login_account
											Long currenttime = System.currentTimeMillis();
											while (!dRequestSet) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													dRequestSet = true;
												}
											}
											return null;
										}

										@Override
										protected void onPostExecute(Void result) {
											super.onPostExecute(result);
											if (dRequestSet && flag != null && flag.equals("ok")) {
												dRequestSet = false;
												flag = "";
												AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(
														moreaddrdetail.this);

												AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(
														Long.parseLong(familyId), Integer.parseInt(familySubId),
														App.sUserLoginId);
												allFamilyDaoDBImpl.deleteObjectByFamilyRecordId(familyRId);
												final String OldFamilyaddress = allFamily.getFamily_address();
												allFamilyDaoDBImpl.releaseDatabaseRes();
												Loger.d("test5",
														"familyaddress = " + OldFamilyaddress + "loadFamilyaddress = "
																+ MainActivity.currentcity + MainActivity.currentvillage
																+ loadAddrdetailPrefrence()+"old family primary="+allFamily.getPrimary_flag());
												if (allFamily.getPrimary_flag() == 1) {
													NeighborDaoDBImpl neighbordao = new NeighborDaoDBImpl(moreaddrdetail.this);
													neighbordao.deleteObject(App.sFamilyId);
													App.setAddrStatus(getApplicationContext(), 0);
													// old
													// 删除当前PushTag
													YLPushInitialization pushInitialization = new YLPushInitialization(
															getApplicationContext());
													pushInitialization.deleteTags(getApplicationContext());

													MainActivity.currentcity = "未设置";
													MainActivity.currentvillage = "未设置";
													App.sFamilyBlockId = 0L;
													App.sFamilyCommunityId = 0L;
													App.sFamilyId = 0L;
													saveSharePrefrence(null, null, null, null, null, null);
													/******************************************/
													Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
													if(selfAccount==null){
														account.setUser_family_id(App.sFamilyId);
														account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
														accountdao.saveObject(account);
													}else{
														selfAccount.setUser_family_id(App.sFamilyId);
														selfAccount.setUser_family_address("");
														accountdao.modifyObject(selfAccount);
														Loger.d("test5", "delete current addr "+App.sFamilyId);
													}
													accountdao.releaseDatabaseRes();
													Loger.d("test5",
															"MainActivity.currentvillage=" + MainActivity.currentvillage+"App.sFamilyId="+App.sFamilyId);
													MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
													Intent intent = new Intent();
													intent.setAction("com.nfs.youlin.find.FindFragment");
													intent.putExtra("family_id", App.sFamilyId);
													sendOrderedBroadcast(intent, null);
													Intent squareIntent=new Intent("youlin.square.action");
													sendBroadcast(squareIntent);
												}
											}
											ylProgressDialog.dismiss();
											// DemoApplication.getInstance().addresslist.remove(whichaddress);
											moreaddrdetail.this.setResult(RESULT_OK, intent);
											finish();

										}
									}.execute();

								}
							});
							builder.setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							builder.create().show();
						} else if (moreitem1[position].equals("修改地址")) {
							// DemoApplication.getInstance().addresslist.remove(whichaddress);
							if (((AllFamily) familyObjs.get(whichaddress)).getEntity_type() != 1) {
								intent.putExtra("familyId", familyId);
								intent.putExtra("familySubId", familySubId);
								// intent.putExtra("position",whichaddress );
								Loger.d("test4", "修改地址 familyId = " + familyId);
								Loger.d("test4", "修改地址 familySubId = " + familySubId);
								moreaddrdetail.this.setResult(RESULT_FIRST_USER, intent);
							} else {
								Toast toast = Toast.makeText(moreaddrdetail.this, "审核通过地址不能修改", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}

							finish();
						} else if (moreitem1[position].equals("取消")) {
							finish();
						}
					}else{
						if (moreitem0[position].equals("设为当前地址")) {
							final long block_id = ((AllFamily) familyObjs.get(whichaddress)).getFamily_block_id();
							final long family_id = ((AllFamily) familyObjs.get(whichaddress)).getFamily_id();
							int old_ne_status = ((AllFamily) familyObjs.get(oldindex)).getNe_status();
							final String old_family_address = ((AllFamily) familyObjs.get(oldindex)).getFamily_address();
							final String new_family_address = ((AllFamily) familyObjs.get(whichaddress)).getFamily_address();
							final AllFamily old_family = (AllFamily) familyObjs.get(oldindex);
							final AllFamily new_family = (AllFamily) familyObjs.get(whichaddress);
							int ne_status = ((AllFamily) familyObjs.get(whichaddress)).getNe_status();
							
							gethttpdata(block_id, family_id, ne_status, App.sFamilyBlockId, App.sFamilyId, old_ne_status,
									IHttpRequestUtils.YOULIN, whichaddress);
							new AsyncTask<Void, Void, Void>() {
								@Override
								protected Void doInBackground(Void... params) {
									// 网络请求 注册地址 参数{city,village,rooft,number}
									// login_account
									// while(cRequestSet){;}
									Long currenttime = System.currentTimeMillis();
									while (!cRequestSet) {
										if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
											cRequestSet = true;
										}
									}
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (cRequestSet && flag != null && flag.equals("ok")) {
										Loger.d("test3", "set address currentindex=" + whichaddress);
										cRequestSet = false;
										flag = "";
										MainActivity.currentcity = ((AllFamily) familyObjs.get(whichaddress)).getFamily_city();
										MainActivity.currentvillage = ((AllFamily) familyObjs.get(whichaddress))
												.getFamily_community();
										App.sFamilyId = ((AllFamily) familyObjs.get(whichaddress)).getFamily_id();
										App.sFamilyCommunityId = ((AllFamily) familyObjs.get(whichaddress))
												.getFamily_community_id();
										App.sFamilyBlockId = ((AllFamily) familyObjs.get(whichaddress)).getFamily_block_id();
										MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
										saveSharePrefrence(MainActivity.currentcity, MainActivity.currentvillage,
												((AllFamily) familyObjs.get(whichaddress)).getFamily_building_num() + "-"
														+ ((AllFamily) familyObjs.get(whichaddress)).getFamily_apt_num(),
												String.valueOf(App.sFamilyId), String.valueOf(App.sFamilyCommunityId),
												String.valueOf(App.sFamilyBlockId));
										int nEntityType = ((AllFamily) familyObjs.get(whichaddress)).getEntity_type();
										App.setAddrStatus(getApplicationContext(), nEntityType);
										// 设置PushTag
										YLPushTagManager pushTagManager = new YLPushTagManager(getApplicationContext());
										pushTagManager.setPushTag();
										old_family.setPrimary_flag(0);
										allFDaoDBImpl.modifyObject(old_family, old_family_address);
										new_family.setPrimary_flag(1);
										allFDaoDBImpl.modifyObject(new_family, new_family_address);
										Loger.d("test4", "old_family_address=" + old_family_address);
										Intent squareIntent=new Intent("youlin.square.action");
										sendBroadcast(squareIntent);
									}
									/****************
									 * 写到本地数据库 table_users
									 ********************/
									Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
									if(selfAccount==null){
										account.setUser_family_id(App.sFamilyId);
										account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
										accountdao.saveObject(account);
									}else{
										selfAccount.setUser_family_id(App.sFamilyId);
										selfAccount.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
										accountdao.modifyObject(selfAccount);
									}
									accountdao.releaseDatabaseRes();
									aNeighborDaoDBImpl = new NeighborDaoDBImpl(moreaddrdetail.this);
									/*************** 请求邻居 *********************/
									if (((AllFamily) familyObjs.get(oldindex)).getEntity_type() == 1) {
										neighbor = new Neighbor(moreaddrdetail.this);

										if (!((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num().equals("0"))
											gethttpdata(((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num(), block_id,
													((AllFamily) familyObjs.get(oldindex)).getFamily_community_id(),
													IHttpRequestUtils.YOULIN, REQUEST_NEIGHBOR);
										else {
											bRequestSet = true;
											Toast.makeText(moreaddrdetail.this, "此地址未审核通过\n无法显示邻居信息", Toast.LENGTH_SHORT);
										}
									} else {
										aNeighborDaoDBImpl.deleteObject(family_id);
										Toast toast = Toast.makeText(getApplicationContext(), "该地址信息未审核通过！\n 不能为您显示邻居",
												Toast.LENGTH_LONG);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
										Intent intent = new Intent();
										intent.setAction("com.nfs.youlin.find.FindFragment");
										intent.putExtra("family_id", family_id);
										moreaddrdetail.this.sendOrderedBroadcast(intent, null);
										moreaddrdetail.this.setResult(RESULT_OK, intent);
										finish();
									}
								}
							}.execute();
							requestTHttphgread = new Thread(new Runnable() {
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
										if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
											bRequestSet = true;
										}
									}

									Message msg = new Message();
									msg.what = REQUEST_OK;
									Bundle data = new Bundle();
									data.putLong("family_id", family_id);
									msg.setData(data);
									handler.sendMessage(msg);
									return;
								}
							});
							requestTHttphgread.start();

						}
//						if (moreitem0[position].equals("地址详情")) {
//							startActivity(new Intent(moreaddrdetail.this, AddressDetailActivity.class));
//						}
						if (moreitem0[position].equals("删除地址")) {
							CustomDialog.Builder builder = new CustomDialog.Builder(moreaddrdetail.this);
							//builder.setTitle("提示");
							builder.setMessage("删除后无法恢复，是否删除地址");
							builder.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									ylProgressDialog.show();
									gethttpdata(Long.parseLong(familyId), Integer.parseInt(familySubId), App.sUserLoginId,
											familyRId,IHttpRequestUtils.YOULIN);
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// 网络请求 删除地址 参数{familyid} login_account
											Long currenttime = System.currentTimeMillis();
											while (!dRequestSet) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													dRequestSet = true;
												}
											}
											return null;
										}

										@Override
										protected void onPostExecute(Void result) {
											super.onPostExecute(result);
											if (dRequestSet && flag != null && flag.equals("ok")) {
												dRequestSet = false;
												flag = "";
												AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(
														moreaddrdetail.this);

												AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(
														Long.parseLong(familyId), Integer.parseInt(familySubId),
														App.sUserLoginId);
												allFamilyDaoDBImpl.deleteObjectByFamilyRecordId(familyRId);
												final String OldFamilyaddress = allFamily.getFamily_address();
												allFamilyDaoDBImpl.releaseDatabaseRes();
												Loger.d("test5",
														"familyaddress = " + OldFamilyaddress + "loadFamilyaddress = "
																+ MainActivity.currentcity + MainActivity.currentvillage
																+ loadAddrdetailPrefrence()+"old family primary="+allFamily.getPrimary_flag());
												if (allFamily.getPrimary_flag() == 1) {
													NeighborDaoDBImpl neighbordao = new NeighborDaoDBImpl(moreaddrdetail.this);
													neighbordao.deleteObject(App.sFamilyId);
													App.setAddrStatus(getApplicationContext(), 0);
													// old
													// 删除当前PushTag
													YLPushInitialization pushInitialization = new YLPushInitialization(
															getApplicationContext());
													pushInitialization.deleteTags(getApplicationContext());

													MainActivity.currentcity = "未设置";
													MainActivity.currentvillage = "未设置";
													App.sFamilyBlockId = 0L;
													App.sFamilyCommunityId = 0L;
													App.sFamilyId = 0L;
													saveSharePrefrence(null, null, null, null, null, null);
													/******************************************/
													Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
													if(selfAccount==null){
														account.setUser_family_id(App.sFamilyId);
														account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
														accountdao.saveObject(account);
													}else{
														selfAccount.setUser_family_id(App.sFamilyId);
														selfAccount.setUser_family_address("");
														accountdao.modifyObject(selfAccount);
														Loger.d("test5", "delete current addr "+App.sFamilyId);
													}
													accountdao.releaseDatabaseRes();
													Loger.d("test5",
															"MainActivity.currentvillage=" + MainActivity.currentvillage+"App.sFamilyId="+App.sFamilyId);
													MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
													Intent intent = new Intent();
													intent.setAction("com.nfs.youlin.find.FindFragment");
													intent.putExtra("family_id", App.sFamilyId);
													moreaddrdetail.this.sendOrderedBroadcast(intent, null);
												}
											}
											ylProgressDialog.dismiss();
											// DemoApplication.getInstance().addresslist.remove(whichaddress);
											moreaddrdetail.this.setResult(RESULT_OK, intent);
											finish();

										}
									}.execute();

								}
							});
							builder.setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							builder.create().show();
						} else if (moreitem0[position].equals("修改地址")) {
							// DemoApplication.getInstance().addresslist.remove(whichaddress);
							if (((AllFamily) familyObjs.get(whichaddress)).getEntity_type() != 1) {
								intent.putExtra("familyId", familyId);
								intent.putExtra("familySubId", familySubId);
								// intent.putExtra("position",whichaddress );
								Loger.d("test4", "修改地址 familyId = " + familyId);
								Loger.d("test4", "修改地址 familySubId = " + familySubId);
								moreaddrdetail.this.setResult(RESULT_FIRST_USER, intent);
							} else {
								Toast toast = Toast.makeText(moreaddrdetail.this, "审核通过地址不能修改", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}

							finish();
						} else if (moreitem0[position].equals("取消")) {
							finish();
						}
					}
				}else{
					if(1==familyPrimary){
						if(kuaisuyanzheng1[position].equals("快速验证")){
							if(Long.parseLong(familyId)==0){
								Toast.makeText(getApplicationContext(), "此地址不可快速审核,\n请联系物业！", Toast.LENGTH_SHORT).show();
								return;
							}
							AllFamilyDaoDBImpl daoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
							AllFamily family = daoDBImpl.getCurrentAddrByFailyId(String.valueOf(familyId));
							if((int)family.getPrimary_flag()!=1){
								Toast.makeText(getApplicationContext(), "请先设置为当前地址！", Toast.LENGTH_SHORT).show();
								family = null;
								daoDBImpl = null;
								return;
							}else{
								Intent intent=new Intent(moreaddrdetail.this,AddrVerifiyActivity.class);
								intent.putExtra("familyRecordId", family.getFamily_address_id());
								intent.putExtra("familyId", family.getFamily_id());
								intent.putExtra("fromAddr", 10003);
								startActivityForResult(intent,10003);
								family = null;
								daoDBImpl = null;
								finish();
							}
							return;
						}
//						if (kuaisuyanzheng1[position].equals("地址详情")) {
//							startActivity(new Intent(moreaddrdetail.this, AddressDetailActivity.class));
//						}
						if (kuaisuyanzheng1[position].equals("删除地址")) {
							CustomDialog.Builder builder = new CustomDialog.Builder(moreaddrdetail.this);
							//builder.setTitle("提示");
							builder.setMessage("删除后无法恢复，是否删除地址");
							builder.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									ylProgressDialog.show();
									gethttpdata(Long.parseLong(familyId), Integer.parseInt(familySubId), App.sUserLoginId,
											familyRId,IHttpRequestUtils.YOULIN);
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// 网络请求 删除地址 参数{familyid} login_account
											Long currenttime = System.currentTimeMillis();
											while (!dRequestSet) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													dRequestSet = true;
												}
											}
											return null;
										}

										@Override
										protected void onPostExecute(Void result) {
											super.onPostExecute(result);
											if (dRequestSet && flag != null && flag.equals("ok")) {
												dRequestSet = false;
												flag = "";
												AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(
														moreaddrdetail.this);

												AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(
														Long.parseLong(familyId), Integer.parseInt(familySubId),
														App.sUserLoginId);
												allFamilyDaoDBImpl.deleteObjectByFamilyRecordId(familyRId);
												final String OldFamilyaddress = allFamily.getFamily_address();
												allFamilyDaoDBImpl.releaseDatabaseRes();
												Loger.d("test5",
														"familyaddress = " + OldFamilyaddress + "loadFamilyaddress = "
																+ MainActivity.currentcity + MainActivity.currentvillage
																+ loadAddrdetailPrefrence()+"old family primary="+allFamily.getPrimary_flag());
												
												if (allFamily.getPrimary_flag() == 1) {
													NeighborDaoDBImpl neighbordao = new NeighborDaoDBImpl(
															moreaddrdetail.this);
													neighbordao.deleteObject(App.sFamilyId);
													App.setAddrStatus(getApplicationContext(), 0);
													// old
													// 删除当前PushTag
													YLPushInitialization pushInitialization = new YLPushInitialization(
															getApplicationContext());
													pushInitialization.deleteTags(getApplicationContext());

													MainActivity.currentcity = "未设置";
													MainActivity.currentvillage = "未设置";
													App.sFamilyBlockId = 0L;
													App.sFamilyCommunityId = 0L;
													App.sFamilyId = 0L;
													saveSharePrefrence(null, null, null, null, null, null);
													/******************************************/
													Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
													if(selfAccount==null){
														account.setUser_family_id(App.sFamilyId);
														account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
														accountdao.saveObject(account);
													}else{
														selfAccount.setUser_family_id(App.sFamilyId);
														selfAccount.setUser_family_address("");
														accountdao.modifyObject(selfAccount);
														Loger.d("test5", "delete current addr "+App.sFamilyId);
													}
													accountdao.releaseDatabaseRes();
													Loger.d("test5",
															"MainActivity.currentvillage=" + MainActivity.currentvillage);
													MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
													Intent intent = new Intent();
													intent.setAction("com.nfs.youlin.find.FindFragment");
													intent.putExtra("family_id", App.sFamilyId);
													sendOrderedBroadcast(intent, null);
													Intent squareIntent=new Intent("youlin.square.action");
													sendBroadcast(squareIntent);
												}
											}
											ylProgressDialog.dismiss();
											// DemoApplication.getInstance().addresslist.remove(whichaddress);
											moreaddrdetail.this.setResult(RESULT_OK, intent);
											finish();

										}
									}.execute();

								}
							});
							builder.setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							builder.create().show();
						} else if (kuaisuyanzheng1[position].equals("修改地址")) {
							// DemoApplication.getInstance().addresslist.remove(whichaddress);
							if (((AllFamily) familyObjs.get(whichaddress)).getEntity_type() != 1) {
								intent.putExtra("familyId", familyId);
								intent.putExtra("familySubId", familySubId);
								// intent.putExtra("position",whichaddress );
								Loger.d("test4", "修改地址 familyId = " + familyId);
								Loger.d("test4", "修改地址 familySubId = " + familySubId);
								moreaddrdetail.this.setResult(RESULT_FIRST_USER, intent);
							} else {
								Toast toast = Toast.makeText(moreaddrdetail.this, "审核通过地址不能修改", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}

							finish();
						} else if (kuaisuyanzheng1[position].equals("取消")) {
							finish();
						}
					}else{
						if(kuaisuyanzheng0[position].equals("快速验证")){
							if(Long.parseLong(familyId)==0){
								Toast.makeText(getApplicationContext(), "此地址不可快速审核,\n请联系物业！", Toast.LENGTH_SHORT).show();
								return;
							}
							AllFamilyDaoDBImpl daoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
							AllFamily family = daoDBImpl.getCurrentAddrByFailyId(String.valueOf(familyId));
							if((int)family.getPrimary_flag()!=1){
								Toast.makeText(getApplicationContext(), "请先设置为当前地址！", Toast.LENGTH_SHORT).show();
								family = null;
								daoDBImpl = null;
								return;
							}else{
								Intent intent=new Intent(moreaddrdetail.this,AddrVerifiyActivity.class);
								intent.putExtra("familyRecordId", family.getFamily_address_id());
								intent.putExtra("familyId", family.getFamily_id());
								intent.putExtra("fromAddr", 10003);
								startActivityForResult(intent,10003);
								family = null;
								daoDBImpl = null;
								finish();
							}
							return;
						}
						if (kuaisuyanzheng0[position].equals("设为当前地址")) {
							final long block_id = ((AllFamily) familyObjs.get(whichaddress)).getFamily_block_id();
							final long family_id = ((AllFamily) familyObjs.get(whichaddress)).getFamily_id();
							int old_ne_status = ((AllFamily) familyObjs.get(oldindex)).getNe_status();
							final String old_family_address = ((AllFamily) familyObjs.get(oldindex)).getFamily_address();
							final String new_family_address = ((AllFamily) familyObjs.get(whichaddress))
									.getFamily_address();
							final AllFamily old_family = (AllFamily) familyObjs.get(oldindex);
							final AllFamily new_family = (AllFamily) familyObjs.get(whichaddress);
							int ne_status = ((AllFamily) familyObjs.get(whichaddress)).getNe_status();

							gethttpdata(block_id, family_id, ne_status, App.sFamilyBlockId, App.sFamilyId, old_ne_status,
									IHttpRequestUtils.YOULIN, whichaddress);
							new AsyncTask<Void, Void, Void>() {
								@Override
								protected Void doInBackground(Void... params) {
									// 网络请求 注册地址 参数{city,village,rooft,number}
									// login_account
									// while(cRequestSet){;}
									Long currenttime = System.currentTimeMillis();
									while (!cRequestSet) {
										if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
											cRequestSet = true;
										}
									}
									return null;
								}

								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (cRequestSet && flag != null && flag.equals("ok")) {
										Loger.d("test3", "set address currentindex=" + whichaddress);
										cRequestSet = false;
										flag = "";
										MainActivity.currentcity = ((AllFamily) familyObjs.get(whichaddress))
												.getFamily_city();
										MainActivity.currentvillage = ((AllFamily) familyObjs.get(whichaddress))
												.getFamily_community();
										App.sFamilyId = ((AllFamily) familyObjs.get(whichaddress)).getFamily_id();
										App.sFamilyCommunityId = ((AllFamily) familyObjs.get(whichaddress))
												.getFamily_community_id();
										App.sFamilyBlockId = ((AllFamily) familyObjs.get(whichaddress))
												.getFamily_block_id();
										MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
										saveSharePrefrence(MainActivity.currentcity, MainActivity.currentvillage,
												((AllFamily) familyObjs.get(whichaddress)).getFamily_building_num() + "-"
														+ ((AllFamily) familyObjs.get(whichaddress)).getFamily_apt_num(),
												String.valueOf(App.sFamilyId), String.valueOf(App.sFamilyCommunityId),
												String.valueOf(App.sFamilyBlockId));
										int nEntityType = ((AllFamily) familyObjs.get(whichaddress)).getEntity_type();
										App.setAddrStatus(getApplicationContext(), nEntityType);
										// 设置PushTag
										YLPushTagManager pushTagManager = new YLPushTagManager(getApplicationContext());
										pushTagManager.setPushTag();
										old_family.setPrimary_flag(0);
										allFDaoDBImpl.modifyObject(old_family, old_family_address);
										new_family.setPrimary_flag(1);
										allFDaoDBImpl.modifyObject(new_family, new_family_address);
										Loger.d("test4", "old_family_address=" + old_family_address);
										Intent squareIntent=new Intent("youlin.square.action");
										sendBroadcast(squareIntent);
									}
									/****************
									 * 写到本地数据库 table_users
									 ********************/
									Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
									if(selfAccount==null){
										account.setUser_family_id(App.sFamilyId);
										account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
										accountdao.saveObject(account);
									}else{
										selfAccount.setUser_family_id(App.sFamilyId);
										selfAccount.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
												+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
										accountdao.modifyObject(selfAccount);
									}
									accountdao.releaseDatabaseRes();
									aNeighborDaoDBImpl = new NeighborDaoDBImpl(moreaddrdetail.this);
									/*************** 请求邻居 *********************/
									if (((AllFamily) familyObjs.get(oldindex)).getEntity_type() == 1) {
										neighbor = new Neighbor(moreaddrdetail.this);

										if (!((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num().equals("0"))
											gethttpdata(((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num(),
													block_id,
													((AllFamily) familyObjs.get(oldindex)).getFamily_community_id(),
													IHttpRequestUtils.YOULIN, REQUEST_NEIGHBOR);
										else {
											bRequestSet = true;
											Toast.makeText(moreaddrdetail.this, "此地址未审核通过\n无法显示邻居信息", Toast.LENGTH_SHORT);
										}
									} else {
										aNeighborDaoDBImpl.deleteObject(family_id);
										Toast toast = Toast.makeText(getApplicationContext(), "该地址信息未审核通过！\n 不能为您显示邻居",
												Toast.LENGTH_LONG);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
										Intent intent = new Intent();
										intent.setAction("com.nfs.youlin.find.FindFragment");
										intent.putExtra("family_id", family_id);
										moreaddrdetail.this.sendOrderedBroadcast(intent, null);
										moreaddrdetail.this.setResult(RESULT_OK, intent);
										finish();
									}

								}
							}.execute();
							requestTHttphgread = new Thread(new Runnable() {
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
										if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
											bRequestSet = true;
										}
									}

									Message msg = new Message();
									msg.what = REQUEST_OK;
									Bundle data = new Bundle();
									data.putLong("family_id", family_id);
									msg.setData(data);
									handler.sendMessage(msg);
									return;
								}
							});
							requestTHttphgread.start();

						}
//						if (kuaisuyanzheng0[position].equals("地址详情")) {
//							startActivity(new Intent(moreaddrdetail.this, AddressDetailActivity.class));
//						}
						if (kuaisuyanzheng0[position].equals("删除地址")) {
							CustomDialog.Builder builder = new CustomDialog.Builder(moreaddrdetail.this);
							//builder.setTitle("提示");
							builder.setMessage("删除后无法恢复，是否删除地址");
							builder.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									ylProgressDialog.show();
									gethttpdata(Long.parseLong(familyId), Integer.parseInt(familySubId), App.sUserLoginId,
											familyRId,IHttpRequestUtils.YOULIN);
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// 网络请求 删除地址 参数{familyid} login_account
											Long currenttime = System.currentTimeMillis();
											while (!dRequestSet) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													dRequestSet = true;
												}
											}
											return null;
										}

										@Override
										protected void onPostExecute(Void result) {
											super.onPostExecute(result);
											if (dRequestSet && flag != null && flag.equals("ok")) {
												dRequestSet = false;
												flag = "";
												AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(
														moreaddrdetail.this);

												AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(
														Long.parseLong(familyId), Integer.parseInt(familySubId),
														App.sUserLoginId);
												allFamilyDaoDBImpl.deleteObjectByFamilyRecordId(familyRId);
												final String OldFamilyaddress = allFamily.getFamily_address();
												allFamilyDaoDBImpl.releaseDatabaseRes();
												Loger.d("test5",
														"familyaddress = " + OldFamilyaddress + "loadFamilyaddress = "
																+ MainActivity.currentcity + MainActivity.currentvillage
																+ loadAddrdetailPrefrence()+"old family primary="+allFamily.getPrimary_flag());
												
												if (allFamily.getPrimary_flag() == 1) {
													NeighborDaoDBImpl neighbordao = new NeighborDaoDBImpl(
															moreaddrdetail.this);
													neighbordao.deleteObject(App.sFamilyId);
													App.setAddrStatus(getApplicationContext(), 0);
													// old
													// 删除当前PushTag
													YLPushInitialization pushInitialization = new YLPushInitialization(
															getApplicationContext());
													pushInitialization.deleteTags(getApplicationContext());

													MainActivity.currentcity = "未设置";
													MainActivity.currentvillage = "未设置";
													App.sFamilyBlockId = 0L;
													App.sFamilyCommunityId = 0L;
													App.sFamilyId = 0L;
													saveSharePrefrence(null, null, null, null, null, null);
													/******************************************/
													Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
													if(selfAccount==null){
														account.setUser_family_id(App.sFamilyId);
														account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_building_num() + "-"
																+ ((AllFamily) familyObjs.get(oldindex)).getFamily_apt_num());
														accountdao.saveObject(account);
													}else{
														selfAccount.setUser_family_id(App.sFamilyId);
														selfAccount.setUser_family_address("");
														accountdao.modifyObject(selfAccount);
														Loger.d("test5", "delete current addr "+App.sFamilyId);
													}
													accountdao.releaseDatabaseRes();
													Loger.d("test5",
															"MainActivity.currentvillage=" + MainActivity.currentvillage);
													MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
													Intent intent = new Intent();
													intent.setAction("com.nfs.youlin.find.FindFragment");
													intent.putExtra("family_id", App.sFamilyId);
													moreaddrdetail.this.sendOrderedBroadcast(intent, null);
												}
											}
											ylProgressDialog.dismiss();
											// DemoApplication.getInstance().addresslist.remove(whichaddress);
											moreaddrdetail.this.setResult(RESULT_OK, intent);
											finish();

										}
									}.execute();

								}
							});
							builder.setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							builder.create().show();
						} else if (kuaisuyanzheng0[position].equals("修改地址")) {
							// DemoApplication.getInstance().addresslist.remove(whichaddress);
							if (((AllFamily) familyObjs.get(whichaddress)).getEntity_type() != 1) {
								intent.putExtra("familyId", familyId);
								intent.putExtra("familySubId", familySubId);
								// intent.putExtra("position",whichaddress );
								Loger.d("test4", "修改地址 familyId = " + familyId);
								Loger.d("test4", "修改地址 familySubId = " + familySubId);
								moreaddrdetail.this.setResult(RESULT_FIRST_USER, intent);
							} else {
								Toast toast = Toast.makeText(moreaddrdetail.this, "审核通过地址不能修改", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}

							finish();
						} else if (kuaisuyanzheng0[position].equals("取消")) {
							finish();
						}
					}
				}
			}
		});
	}
	
	private List<Map<String, Object>> getData(List<Object> Objslist) {  
	    List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
		list.clear();
	        for (int i = 0; i < Objslist.size(); i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            String currentaddr = ((AllFamily) Objslist.get(i)).getFamily_address();
	            if(currentaddr.length()>20){
	            	currentaddr = currentaddr.substring(0, 21)+"...";
	            }
	            map.put("address_text",currentaddr ); 
	            map.put("family_id",String.valueOf(((AllFamily) Objslist.get(i)).getFamily_id()));
	            map.put("verify_flag", ((AllFamily) Objslist.get(i)).getEntity_type());
	            map.put("check_flag", "0");
	            if (((AllFamily) Objslist.get(i)).getFamily_address().equals(
	    				MainActivity.currentcity + MainActivity.currentvillage
	    						+ loadAddrdetailPrefrence())) {
	            	oldindex = i;
	    			map.put("check_flag", "1");
	    		}
	            list.add(map);      
	        }  
	          
	        return list;  
	    } 
	public void saveSharePrefrence(String city, String village,String detail,String familyid,String fimalycommunityid,String blockid){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.putString("blockid", blockid);
		sharedata.commit();
	}
	public String loadFamilyIdPrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("familyid", "未设置");
	}
	
	private List<Map<String, Object>> getData(String[] strs) {
		// List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < strs.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", strs[i]);
			list.add(map);
		}
		return list;
	}

		private void gethttpdata(String apt_num,Long param2,Long param3,String http_addr,int request_index){
			/***********************************************/
			jsonObjList = new ArrayList<JSONObject>();
			RequestParams params = new RequestParams();
			AsyncHttpClient client = new AsyncHttpClient();
			Loger.d("test3","apt_num="+apt_num+"block_id="+param2+"community_id="+param3 );
			if(request_index == REQUEST_NEIGHBOR){
				params.put("user_id", App.sUserLoginId);
				params.put("apt_num", apt_num);
				if(!param2.equals("0") && param2 != null)
					params.put("block_id", param2);
				params.put("community_id", param3);
			}
			params.put("tag", "neighbors");
			params.put("apitype", IHttpRequestUtils.APITYPE[0]);
			client.post(IHttpRequestUtils.URL+http_addr,
					params, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode,
						org.apache.http.Header[] headers,
						org.json.JSONObject response) {
					// TODO Auto-generated method stub
					org.json.JSONObject jsonContext = response;
					String flag = null;
//					String community_id = null;
//					String community_name = null;
					try {
						flag = jsonContext.getString("flag");
						Loger.i("test3", "set neighbor flag->" + flag);
						
						if(flag.equals("none_f_o1")){
							bRequestSet = true;
						}else{
							bRequestSet = false;
						}
					} catch (org.json.JSONException e) {
						e.printStackTrace();
						bRequestSet = false;
						Loger.i("test3","JSONObject->"+ e.getMessage());
					}
					super.onSuccess(statusCode, headers,response);
				}
				@Override
				public void onSuccess(int statusCode,
						org.apache.http.Header[] headers,
						org.json.JSONArray response) {
					// TODO Auto-generated method stub
					org.json.JSONArray jsonContext = response;
					Loger.d("test3", jsonContext.toString());
					try {
						org.json.JSONObject obj = null;
						if(jsonContext.length()>0){
							for (int i = 0; i < jsonContext.length(); i++) {
								jsonObjList.add(jsonContext.getJSONObject(i));
								obj = jsonContext.getJSONObject(i);
							}
							bRequestSet = true;
							flag = "ok";
						}
						
					} catch (org.json.JSONException e) {
						// TODO Auto-generated catch block
						Loger.i("TEST","OK(error)->" + e.getMessage());
						bRequestSet = false;
						e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onFailure(int statusCode,
							org.apache.http.Header[] headers,
							String responseString,
							Throwable throwable) {
						// TODO Auto-generated method stub
						bRequestSet = false;
						ylProgressDialog.dismiss();
						new ErrorServer(moreaddrdetail.this, responseString.toString());
						super.onFailure(statusCode, headers,
								responseString, throwable);
					}
				});

		}
	private void gethttpdata(long block_id,long family_id ,int ne_status,long old_block_id,long old_family_id,int old_ne_status,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
			Loger.d("test3", "--"+block_id+"--"+"--"+family_id+"--"+"--"+ne_status+"--"+"--"+App.sUserLoginId+"--"+"--"+old_block_id+"--"+"--"+old_family_id+"--"+"--"+old_ne_status+"--");
			if(family_id >0 || ne_status >0){
				if(block_id >0)
					params.put("block_id", block_id);
				if(family_id > 0)
					params.put("family_id", family_id);
				else
					params.put("ne_status", ne_status);
			}
			params.put("user_id", App.sUserLoginId);
			
			if(old_family_id >0 || old_ne_status >0){
				if(old_block_id >0)
					params.put("old_block_id", old_block_id);
				if(old_ne_status >0)
					params.put("old_ne_status", old_ne_status);
				if(old_family_id >0)
					params.put("old_family_id", old_family_id);
			}	
			params.put("tag", "curaddr");
			params.put("apitype", IHttpRequestUtils.APITYPE[0]);
			SharedPreferences preferences=getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
			int addressTag=preferences.getInt("address_tag", 0);
			params.put("addr_cache",addressTag);
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				Loger.i("LYM", "set currentaddress flag->"+response);
				try {
					flag = jsonContext.getString("flag");
					String addressTag = jsonContext.getString("addr_flag");
				
					if(flag.equals("ok") && addressTag.equals("ok")){
						cRequestSet = true;
						oldindex = position;
					}else{
						cRequestSet = false;
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					cRequestSet = false;
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
				Loger.i("LYM", "set currentaddress flag JSONArray->"+response);
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					new ErrorServer(moreaddrdetail.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	private void gethttpdata(long familyid,int familysubid,long userid,long frId,String http_addr){
		/***********************************************/
		RequestParams params = new RequestParams();
		AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(moreaddrdetail.this);
		final AllFamily allFamily = allFamilyDaoDBImpl.findDesignObject_0(familyid,familysubid,userid);
		final String OldFamilyaddress = allFamily.getFamily_address();
		params.put("family_id", familyid);
		params.put("user_id", userid);
		params.put("nestatus", familysubid);
		params.put("record_id", familyRId);
		SharedPreferences preferences=getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		int addressTag=preferences.getInt("address_tag", 0);
		params.put("addr_cache",addressTag);
		params.put("tag", "delfamily");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		if(allFamily.getPrimary_flag() == 1){
			params.put("flag", 1);
		}else{
			params.put("flag", 0);
		}
		Loger.d("test4","allFamily.getPrimary_flag()="+allFamily.getPrimary_flag() );
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				try {
					flag = jsonContext.getString("flag");
					String addressTag = jsonContext.getString("addr_flag");
					Loger.i("LYM", "delete flag->" + flag+ " "+addressTag);
					if(flag.equals("ok") && addressTag.equals("ok")){
						dRequestSet = true;
					}else{
						dRequestSet = false;
						Toast.makeText(moreaddrdetail.this, "地址删除失败", Toast.LENGTH_SHORT);//返回失败吐司
					}
					
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
				Loger.d("test3", jsonContext.toString());
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					Loger.i("TEST",
							responseString + "\r\n" + throwable.toString()
							+"\r\n-----\r\n" + statusCode);
					dRequestSet = false;
					
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	
	private String loadAddrdetailPrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}
	 private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REQUEST_OK:
					if(requestTHttphgread!=null){
						requestTHttphgread.interrupt();
						requestTHttphgread = null;
					}
					Long Belong_family_id = msg.getData().getLong("family_id");
					if (bRequestSet && flag != null && flag.equals("ok")){
						bRequestSet = false;
						flag = "";
						Loger.d("test3","Belong_family_id="+Belong_family_id);
						Loger.i("test3","jsonObjList->"+jsonObjList.size());
						if(jsonObjList.size()>0){
							aNeighborDaoDBImpl.deleteObject(Belong_family_id);
						}
						for(int i=0 ;i<jsonObjList.size();i++){
							
							try {
								if(Long.parseLong(jsonObjList.get(i).getString("family_id"))>0){
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
									neighbor.setUser_type(Integer.parseInt(jsonObjList.get(i).getString("user_type")));
								}else{
									continue;
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							aNeighborDaoDBImpl.saveObject(neighbor);
						}
						ylProgressDialog.dismiss();
					}
					Intent intent = new Intent();
					intent.setAction("com.nfs.youlin.find.FindFragment");
					intent.putExtra("family_id",Belong_family_id );
					moreaddrdetail.this.sendOrderedBroadcast(intent, null);
					moreaddrdetail.this.setResult(RESULT_OK, intent);
					finish();
				}
			}
	 };
	 
	 	@Override
		protected void onResume() {
		// TODO Auto-generated method stub
			super.onResume();
			JPushInterface.onResume(getApplicationContext());
			MobclickAgent.onResume(this);
		}
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			JPushInterface.onPause(getApplicationContext());
			MobclickAgent.onPause(this);
		}
}
