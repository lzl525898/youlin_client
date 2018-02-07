package com.nfs.youlin.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.Invite_friend_dealactivity;
import com.nfs.youlin.activity.personal.addressshow;
import com.nfs.youlin.activity.personal.write_address;
import com.nfs.youlin.activity.titlebar.barter.BarterActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivity;
import com.nfs.youlin.adapter.PopupMenuAddAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.CustomDialog;
import com.nfs.youlin.view.PopupMenuAddListView;

public class MainActionBarclicklisener  implements OnClickListener{

	private PopupWindow popupWindow;
	private LayoutInflater layoutInflater;
	//private  PopupMenuAddListView addListView;
	private  ListView addListView;
	private  int currentindex = 0;
	//public static int clickAddCount = 0;
	private static View addListViewButtom;
	public  MainActivity mMainActivity;
	private PopupWindow popupShareWindow;
	private View popupShareView;	
	private LinearLayout topLinearLayout;
	private RelativeLayout shareRrecordLayout;
	private ImageView contactsImageView;
	private ImageView tengxunImageView;
	private ImageView weixinImageView;
	List<Map<String,Object>> address_show = new ArrayList<Map<String,Object>>();
	private String SHARE_URL = IHttpRequestUtils.URL+"adminpush/index";
	private String shareInformation = "欢迎您使用优邻，下载地址:"+SHARE_URL;
	private String user_nick;
	private String icon_path;
	public MainActionBarclicklisener(MainActivity mainActivity) {
		// TODO Auto-generated constructor stub
		mMainActivity = mainActivity;
		MainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
		user_nick = new AccountDaoDBImpl(mMainActivity).findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_name();
	}
//	private void setcurrentaddress() {
//		final List<String> addresslist = new ArrayList<String>();
//		final List<String> familyidlist = new ArrayList<String>();
//		final AccountDaoDBImpl accountdao = new AccountDaoDBImpl(mMainActivity);
//		final Account account = new Account(mMainActivity);
//		AlertDialog.Builder builder = new AlertDialog.Builder(
//				mMainActivity);
//		/****************** database ***********************/
//		AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(
//				mMainActivity);
//		final List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
//
//		/****************** database ***********************/
//		addresslist.clear();
//		for (int i = 0; i < familyObjs.size(); i++) {
//			Loger.d("hyytest",
//					" long familyId[i]="
//							+ ((AllFamily) familyObjs.get(i))
//									.getFamily_id());
//			// familyId[i] =
//			// String.valueOf(((AllFamily)familyObjs.get(i)).getFamily_id());
//			familyidlist.add(String.valueOf(((AllFamily) familyObjs.get(i))
//					.getFamily_id()));
//			Loger.d("hyytest", "familyId[i]=" + familyidlist.get(i));
//			// strs3[i] =
//			// ((AllFamily)familyObjs.get(i)).getFamily_address();
//			addresslist.add(((AllFamily) familyObjs.get(i))
//					.getFamily_address());
//			if (addresslist.get(i).equals(
//					MainActivity.currentcity + MainActivity.currentvillage
//							+ mMainActivity.loadAddrdetailPrefrence())) {
//				currentindex = i;
//			}
//		}
//		if (MainActivity.currentvillage.equals("未设置"))
//			builder.setTitle("小区显示内容\r\n他很懒什么都没有留下");
//		else
//			builder.setTitle(mMainActivity.loadUsernamePrefrence()+"@" +MainActivity.currentvillage);
//		SimpleAdapter adapter =  new SimpleAdapter(mMainActivity, getData(address_show, addresslist.toArray(new String[addresslist.size()])),
//				R.layout.singlechooselist, new String[]{"address_text"}, new int[]{R.id.address_show});
//		builder.setSingleChoiceItems(adapter, currentindex, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int arg1) {
//				// TODO Auto-generated method stub
//				currentindex = arg1;
//				MainActivity.currentcity = ((AllFamily) familyObjs
//						.get(arg1)).getFamily_city();
//				MainActivity.currentvillage = ((AllFamily) familyObjs
//						.get(arg1)).getFamily_community();
//				App.sFamilyId = ((AllFamily) familyObjs
//						.get(arg1)).getFamily_id();
//				App.sFamilyCommunityId = ((AllFamily) familyObjs
//						.get(arg1)).getFamily_community_id();
//				mMainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
//				mMainActivity.saveSharePrefrence(
//						MainActivity.currentcity,
//						MainActivity.currentvillage,
//						((AllFamily) familyObjs.get(arg1)).getFamily_building_num()+((AllFamily) familyObjs.get(arg1)).getFamily_apt_num(),
//						String.valueOf(App.sFamilyId),
//						String.valueOf(App.sFamilyCommunityId));
//				/****************写到本地数据库 table_users********************/
//				
//				account.setUser_family_id(App.sFamilyId);
//				account.setUser_family_address(MainActivity.currentcity
//						+MainActivity.currentvillage+((AllFamily) familyObjs.get(arg1)).getFamily_building_num()+"-"+((AllFamily) familyObjs.get(arg1)).getFamily_apt_num());
//				accountdao.saveObject(account);
//				accountdao.releaseDatabaseRes();
//				Intent intent = new Intent();
//				intent.setAction("com.nfs.youlin.find.FindFragment");
//				intent.putExtra("family_id",App.sFamilyId );
//				mMainActivity.sendBroadcast(intent);
//				dialog.cancel();
//			}
//		});

//		builder.setSingleChoiceItems(
//				addresslist.toArray(new String[addresslist.size()]),
//				currentindex, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int arg1) {
//						// TODO Auto-generated method stub
//						currentindex = arg1;
//						MainActivity.currentcity = ((AllFamily) familyObjs
//								.get(arg1)).getFamily_city();
//						MainActivity.currentvillage = ((AllFamily) familyObjs
//								.get(arg1)).getFamily_community();
//						App.sFamilyId = ((AllFamily) familyObjs
//								.get(arg1)).getFamily_id();
//						App.sFamilyCommunityId = ((AllFamily) familyObjs
//								.get(arg1)).getFamily_community_id();
//						mMainActivity.setcurrentaddressTextView(MainActivity.currentvillage);
//						mMainActivity.saveSharePrefrence(
//								MainActivity.currentcity,
//								MainActivity.currentvillage,
//								((AllFamily) familyObjs.get(arg1)).getFamily_building_num()+((AllFamily) familyObjs.get(arg1)).getFamily_apt_num(),
//								String.valueOf(App.sFamilyId),
//								String.valueOf(App.sFamilyCommunityId));
//						/****************写到本地数据库 table_users********************/
//						
//						account.setUser_family_id(App.sFamilyId);
//						account.setUser_family_address(MainActivity.currentcity
//								+MainActivity.currentvillage+((AllFamily) familyObjs.get(arg1)).getFamily_building_num()+"-"+((AllFamily) familyObjs.get(arg1)).getFamily_apt_num());
//						accountdao.saveObject(account);
//						accountdao.releaseDatabaseRes();
//						Intent intent = new Intent();
//						intent.setAction("com.nfs.youlin.find.FindFragment");
//						intent.putExtra("family_id",App.sFamilyId );
//						mMainActivity.sendBroadcast(intent);
//						dialog.cancel();
//					}
//				});
//		builder.setPositiveButton("地址信息",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						Intent addressintent = new Intent(mMainActivity,
//								addressshow.class);
//						mMainActivity.startActivity(addressintent);
//						dialog.cancel();
//					}
//				});
//		builder.show();
//	}
	private void addImagefunc(){
		popupWindow = new PopupWindow(mMainActivity.getApplicationContext());
		//if (clickAddCount == 0) {
		//	clickAddCount = 1; 
			layoutInflater = mMainActivity.getLayoutInflater();
			addListViewButtom = layoutInflater.inflate(R.layout.popup_title_add, null);
			popupWindow.setContentView(addListViewButtom);
			popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setFocusable(true);
			
//			popupWindow.showAsDropDown(MainActivity.addImageView, -55, 40);com.mob.tools.utils.R.dipToPx(mMainActivity, 8)
			popupWindow.showAsDropDown(MainActivity.addImageView, com.mob.tools.utils.R.dipToPx(mMainActivity, 11), (mMainActivity.getActionBar().getHeight()-MainActivity.addImageView.getHeight())/2+2);
			ColorDrawable dw = new ColorDrawable(0x00000000);  
			popupWindow.setBackgroundDrawable(dw);
			addListViewButtom.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(popupWindow!=null&&popupWindow.isShowing()){
						popupWindow.dismiss();
						popupWindow=null;
					}
					return true;
				}
			});
			String[] addContexts = {
					"新建话题",
					"发起活动",
					"闲品会",
					"邀请",
			};
			PopupMenuAddAdapter popupMenuAddAdapter = new PopupMenuAddAdapter(
					addListViewButtom.getContext(),addContexts);
			addListView = (ListView) addListViewButtom.findViewById(R.id.popupmenu_add_listview);
			addListView.setAdapter(popupMenuAddAdapter);
			addListView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Loger.i("TEST","66666666666666666666666666");
							switch (position) {
							case 0: // 新建话题
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method
										// stub
										Message msg = new Message();
										msg.what = 1001;
										handler.sendMessage(msg);
									}
								}).start();
								mMainActivity.startActivity(new Intent(
										mMainActivity, NewTopic.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
								break;
							case 1: // 发起会话
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method
										// stub
										Message msg = new Message();
										msg.what = 1001;
										handler.sendMessage(msg);
									}
								}).start();
								mMainActivity.startActivity(new Intent(
										mMainActivity,
										SendActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
								break;
							case 2: // 闲品会
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method
										// stub
										Message msg = new Message();
										msg.what = 1001;
										handler.sendMessage(msg);
									}
								}).start();
								mMainActivity.startActivity(new Intent(
										mMainActivity, BarterActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
								break;
							case 3: // 分享
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method
										// stub
										Message msg = new Message();
										msg.what = 1001;
										handler.sendMessage(msg);
									}
								}).start();
//								setPopupWindow();
								mMainActivity.startActivity(new Intent(
										mMainActivity,Invite_friend_dealactivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
								break;
							default:
								break;

							}
							// Toast.makeText(MainTitleBar.sMainActivity,
							// "position->"+position,
							// Toast.LENGTH_LONG).show();
						}
					});
		//} else {
		//	clickAddCount = 0;
		//	popupWindow.dismiss();
		//}
	}
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1001) {
				popupWindow.dismiss();
			}
		};
	};
	private List<Map<String, Object>> getData(List<Map<String, Object>> list,String[] strs) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
		list.clear();
	        for (int i = 0; i < strs.length; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("address_text", strs[i]); 
	           // map.put("img", R.drawable.chat_location_normal);
	            list.add(map);  
	              
	        }  
	          
	        return list;  
	    } 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	    popupShareView =mMainActivity.getLayoutInflater().inflate(R.layout.popup_personal_invite, null);
		topLinearLayout = (LinearLayout) popupShareView.findViewById(R.id.ll_top_share);
		shareRrecordLayout = (RelativeLayout) popupShareView.findViewById(R.id.ll_popup_share_record);
		contactsImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_contact);
		tengxunImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_tengxun);
		weixinImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_weixin);
		contactsImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Toast.makeText(mMainActivity, "分享中...",Toast.LENGTH_SHORT).show();
				Uri smsToUri = Uri.parse("smsto:");
				Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
				sendIntent.putExtra( "sms_body", shareInformation);  
			    sendIntent.setType( "vnd.android-dir/mms-sms" ); 
			    mMainActivity.startActivity(sendIntent);
				popupShareWindow.dismiss();
			}
		});

		weixinImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isApkInstalled(mMainActivity,"com.tencent.mm")) {
					Toast.makeText(mMainActivity, "手机未发现有微信，请先安装!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mMainActivity, "分享中...",
							Toast.LENGTH_SHORT).show();
					initShareIntent("com.tencent.mm");
				}
				popupShareWindow.dismiss();
			}
		});
		tengxunImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isApkInstalled(mMainActivity, "com.tencent.mobileqq")) {
					Toast.makeText(mMainActivity, "手机未发现有QQ，请先安装!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mMainActivity, "分享中...",
							Toast.LENGTH_SHORT).show();
					initShareIntent("tencent.mobileqq");
				}
				popupShareWindow.dismiss();
			}
		});
		popupShareView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupShareWindow.dismiss();
			}
		});

		shareRrecordLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mMainActivity, "此部分功能尚在开发", Toast.LENGTH_LONG)
						.show();
			}
		});
		switch (v.getId()) {
		case R.id.main_bar_address:
//			setcurrentaddress();
			Intent intent = new Intent(mMainActivity,AddressShowForBar.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mMainActivity.startActivity(intent);
			break;
		case R.id.main_bar_search:
			mMainActivity.startActivity(new Intent(mMainActivity,TitleBarSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.main_bar_add:
//			Long.valueOf("null");
			String addrDetails=getAddrDetail();
			Loger.i("youlin","addrDetails-->"+addrDetails);
			if(addrDetails!=null){
				AllFamilyDaoDBImpl curfamilyDaoDBImpl = new AllFamilyDaoDBImpl(mMainActivity);
				AllFamily currentFamily = curfamilyDaoDBImpl.getCurrentAddrDetail(addrDetails);
				Loger.i("youlin","11111111111-->"+currentFamily.getEntity_type()+"   "+currentFamily.getPrimary_flag());
				if(currentFamily.getEntity_type()==1 && currentFamily.getPrimary_flag()==1){
					addImagefunc();
				}else{
					showAddDialog();
				}
			}else{
				showAddDialog();
			}
			
			break;
		case R.id.new_msg:
			mMainActivity.startActivity(new Intent(mMainActivity, NewPushRecordAbsActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
//		case R.id.main_bar_friend:
//			
//			setPopupWindow();
//			break;
		default:
			break;
		}
	}
	private Account getSenderInfo(){
		Account account = null;
		if(App.sUserLoginId > 0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(mMainActivity);
			account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		}
		return account;
	}
	private String getAddrDetail(){
		SharedPreferences sharedata = mMainActivity.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String addrDetail = null;
		String city = sharedata.getString("city", null);
		String village = sharedata.getString("village", null);
		String detail = sharedata.getString("detail", null);
		if(city!=null && village!=null && detail!=null){
			addrDetail = city+village+detail;
		}
		return addrDetail;
	}
	public void showAddDialog(){
		CustomDialog.Builder builder=new CustomDialog.Builder(mMainActivity);
		//builder.setTitle("提示");
		builder.setCancelable(true);
		builder.setMessage("您当前的地址信息不完整或正在审核中");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	private void setPopupWindow() {
		popupShareWindow = new PopupWindow(popupShareView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ColorDrawable cd = new ColorDrawable(0x90000000);
		popupShareWindow.setBackgroundDrawable(cd);
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f,
				Animation.RELATIVE_TO_PARENT, 0f);
		popupShareWindow.setFocusable(true);
		anim.setDuration(500);
		topLinearLayout.setAnimation(anim);
		popupShareWindow.showAtLocation(mMainActivity.getWindow()
				.getDecorView(), Gravity.BOTTOM, 0, 0);
		popupShareWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
	}
	@SuppressLint("DefaultLocale")
	private void initShareIntent(String type) {
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = mMainActivity.getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					share.putExtra(Intent.EXTRA_TEXT, shareInformation);
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found)
				return;
			mMainActivity.startActivity(share);
		}
	}

	private boolean isApkInstalled(Context context, String packageName) {
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}


