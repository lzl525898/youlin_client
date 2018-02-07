package com.nfs.youlin.activity.titlebar.startactivity;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.VersionInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.easemob.chatuidemo.activity.BaiduMapActivity;
import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.neighbor.PropertyRepairAddActivity;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class SendActivityAddressChange extends Activity implements
OnGetGeoCoderResultListener{
	private EditText addressEt;
	private String addressStr;
	private SDKReceiver mReceiver;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LatLng currentPt;
	private LocationClient mLocClient;
	private static BDLocation lastLocation = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	boolean isFirstLoc = true;// 是否首次定位
	BitmapDescriptor mCurrentMarker;
	private PopupWindow popWindow;
	private View view;
	private List<String> currentpointaddresslist = new ArrayList<String>();
	private List<String> pointedaddresslist = new ArrayList<String>();
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
			} else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
			}
			else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
			}
		}
	}
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			 MyLocationData locData = new MyLocationData.Builder()
             .accuracy(location.getRadius())
                     // 此处设置开发者获取到的方向信息，顺时针0-360
             .direction(0).latitude(location.getLatitude())
             .longitude(location.getLongitude()).build();
			 mBaiduMap.setMyLocationData(locData);
     		if (isFirstLoc) {
     			isFirstLoc = false;
     			LatLng ll = new LatLng(location.getLatitude(),
     					location.getLongitude());
     			com.baidu.mapapi.map.MapStatus.Builder build = new MapStatus.Builder(mBaiduMap.getMapStatus());
    			build.target(ll);
    			build.zoom(17.0f);
    			MapStatus ms = build.build();
     			MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
     			mBaiduMap.animateMapStatus(u);
     			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
    			.location(ll));
     		}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_activity_address);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("活动地点");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		addressEt=(EditText)findViewById(R.id.send_address_et);
		addressEt.setText(SendActivityChange.sendAddressTv.getText());
		mMapView = (MapView) findViewById(R.id.bmapView);
	       mBaiduMap = mMapView.getMap();
			
	        IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
			iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
			iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
			mReceiver = new SDKReceiver();
			registerReceiver(mReceiver, iFilter);
			initListener();
			initLocation();	
			popWindow=new PopupWindow(this);
			view = getLayoutInflater().inflate(R.layout.activity_share_popwindow_repair, null);
			
	}
	 private void initLocation(){
	    	// 开启定位图层
	    	mBaiduMap.setMyLocationEnabled(true);
	    	mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			
			mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.title_icon_pin);
	        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
	                		LocationMode.NORMAL, true, mCurrentMarker));
	        
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 鎵撳紑gps
			// option.setCoorType("bd09ll"); //璁剧疆鍧愭爣绫诲瀷
			// Johnson change to use gcj02 coordination. chinese national standard
			// so need to conver to bd09 everytime when draw on baidu map
			option.setCoorType("bd09ll");
			option.setScanSpan(3000);
			option.setAddrType("all");
			mLocClient.setLocOption(option);
			mLocClient.start();
	    }
	    private void initListener() {
			// 初始化搜索模块，注册事件监听
			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);
			mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
				
				@Override
				public void onTouch(MotionEvent event) {
					
				}
			});
			
			mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
				public void onMapClick(LatLng point) {

					currentPt = point;
					Setpointloc(point);
					
				}

				public boolean onMapPoiClick(MapPoi poi) {
					return false;
				}
			});

	    }
		private void Setpointloc(LatLng point) {

			if (currentPt == null) {
			} else {
				mBaiduMap.clear();
				com.baidu.mapapi.map.MapStatus.Builder build = new MapStatus.Builder(mBaiduMap.getMapStatus());
				build.target(point);
//				build.zoom(17.0f);
				MapStatus ms = build.build();
				MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
				mBaiduMap.animateMapStatus(u);
				OverlayOptions ooA = new MarkerOptions().position(point).icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka))
						.zIndex(4).draggable(true);
				mBaiduMap.addOverlay(ooA);
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
				.location(point));
			}

		}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.new_topic_change, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.finish:
			addressStr=addressEt.getText().toString();
			SendActivityChange.sendAddressTv.setText(addressStr);
			finish();
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(SendActivityAddressChange.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
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
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub
		addressEt.setText(arg0.getAddress());
	}
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub
		addressEt.setText(arg0.getAddress());
		pointedaddresslist.clear();
		currentpointaddresslist.clear();
		if(arg0.getPoiList()!=null)
		for(int i=0;i<arg0.getPoiList().size();i++){
			Loger.d("hyytest", ""+arg0.getPoiList().get(i).address.toString()+"--"+arg0.getPoiList().get(i).name.toString());
			pointedaddresslist.add(arg0.getPoiList().get(i).name.toString());
			currentpointaddresslist.add(arg0.getPoiList().get(i).address.toString());
		}
		final ListView listView=(ListView)view.findViewById(R.id.share_pop_repair_lv);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,pointedaddresslist);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				listView.setItemChecked(arg2, true);
				addressEt.setText(pointedaddresslist.get(arg2)+"("+currentpointaddresslist.get(arg2)+")");
				popWindow.dismiss();
			}
		});
		popWindow.setWidth(LayoutParams.WRAP_CONTENT);
		popWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setContentView(view);
		//popWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, addressEt.getWidth()*2);
		try {
			popWindow.showAsDropDown(addressEt,-10,10);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				new BackgroundAlpha(1f, SendActivityAddressChange.this);
			}
		});
	}
}
		
