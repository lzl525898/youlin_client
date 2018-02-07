package com.nfs.youlin.activity.find;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpRequest;
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
import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.exceptions.EaseMobException;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.MainActionBarclicklisener;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.NewPushStoreCommentActivity;
import com.nfs.youlin.activity.WeatherListActivity;
import com.nfs.youlin.activity.neighbor.AllNewshistoryList;
import com.nfs.youlin.activity.neighbor.CommunityServiceActivity;
import com.nfs.youlin.activity.neighbor.PropertyActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.thirdlogin.OnLoginListener;
import com.nfs.youlin.activity.thirdlogin.UserInfo;
import com.nfs.youlin.adapter.ContentlistWithHead;
import com.nfs.youlin.adapter.Imagebuttonadapter;
import com.nfs.youlin.adapter.Imagebuttonadapterwithnew;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.DoorplateDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.NeighborDoor;
import com.nfs.youlin.entity.SearchHistory;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.StringToPinyinHelper;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.nfs.youlin.view.neighbor_chat_fragment;
import com.nfs.youlin.view.neighbor_list;
import com.nfs.youlin.entity.Person;
public class FindFragment extends Fragment {
	private View view;
	private ImageView unreadImg;
	private Drawable drawableNormal;
	private Drawable drawableUnread;
	public YLProgressDialog ylProgressDialog;
	private BroadcastReceiver sBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("com.nfs.youlin.find.newmsg") || arg1.getAction().equals("com.nfs.youlin.find.FindFragment")){
				if(getSysUnreadMsgCountTotal()>0){
					MainActivity.findBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableUnread, null, null);
					unreadImg.setVisibility(View.VISIBLE);
				}else{
					MainActivity.findBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNormal, null, null);
					unreadImg.setVisibility(View.GONE);
				}
			}
			
		}
		
	}; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			drawableNormal=getResources().getDrawable(R.drawable.tab_find_bg_selector);
			drawableUnread=getResources().getDrawable(R.drawable.tab_find_bg_selector_unread);
			if(getSysUnreadMsgCountTotal()>0){
				MainActivity.findBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableUnread, null, null);
			}else{
				try {
					MainActivity.findBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNormal, null, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ylProgressDialog = YLProgressDialog.createDialogwithcircle(getActivity(),"",1);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter=new IntentFilter();
		filter.addAction("com.nfs.youlin.find.newmsg");
		filter.addAction("com.nfs.youlin.find.FindFragment");
		getActivity().registerReceiver(sBroadcastReceiver,filter);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if(getSysUnreadMsgCountTotal()>0){
					unreadImg.setVisibility(View.VISIBLE);
				}else{
					MainActivity.findBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNormal, null, null);
					unreadImg.setVisibility(View.GONE);
				}
			}
		});
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(sBroadcastReceiver);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	public int getSysUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		int userUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance()
				.getAllConversations().values()) {
			if (!conversation.getUserName().equals("xitongxinxi"))
				userUnreadMsgCount += conversation.getUnreadMsgCount();
		}
		//return unreadMsgCountTotal - chatroomUnreadMsgCount;
		return userUnreadMsgCount;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view==null){
			view = inflater.inflate(R.layout.find_fragment, container, false);
		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生IllegalStateException。
        ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		unreadImg=(ImageView)view.findViewById(R.id.unread_img);
		RelativeLayout neighbor=(RelativeLayout)view.findViewById(R.id.neighbor_layout);
		RelativeLayout store=(RelativeLayout)view.findViewById(R.id.store_layout);
		RelativeLayout xinwen = (RelativeLayout)view.findViewById(R.id.xinwen);
		RelativeLayout tianqi = (RelativeLayout)view.findViewById(R.id.tianqi);
		RelativeLayout propertyNotice=(RelativeLayout)view.findViewById(R.id.wuyegonggao);
		RelativeLayout propertyPhone=(RelativeLayout)view.findViewById(R.id.wuyexinxi);
		RelativeLayout communityInfo=(RelativeLayout)view.findViewById(R.id.shequxinxi);
		neighbor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//ylProgressDialog.show();
				
				startActivity(new Intent(getActivity(),NeighborActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		store.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),coml_everyitem_list.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		xinwen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),AllNewshistoryList.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		tianqi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),WeatherListActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		propertyNotice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),PropertyGonggaoActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		propertyPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),PropertyActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		communityInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),CommunityServiceActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		return view;
	}
}
