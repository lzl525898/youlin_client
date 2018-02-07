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
package com.nfs.youlin.activity.titlebar.barter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import cn.jpush.android.api.JPushInterface;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.nfs.youlin.R;
import com.umeng.analytics.MobclickAgent;


public class BarterBaiduMapActivity extends Activity {
	private SDKReceiver mReceiver;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	boolean isFirstLoc = true;// 是否首次定位
	BitmapDescriptor mCurrentMarker;
	String jingDuStr;
	String weiDuStr;
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
			try {
				if (location == null) {
					return;
				}
				 MyLocationData locData = new MyLocationData.Builder()
				 .accuracy(location.getRadius())
				         // 此处设置开发者获取到的方向信息，顺时针0-360  126.612409,45.725806
				 .direction(0).latitude(Double.parseDouble(weiDuStr))
				 .longitude(Double.parseDouble(jingDuStr)).build();
				 mBaiduMap.setMyLocationData(locData);
				if (isFirstLoc) {
					isFirstLoc = false;
//     			LatLng ll = new LatLng(location.getLatitude(),
//     					location.getLongitude());
					LatLng ll =new LatLng(Double.parseDouble(weiDuStr),Double.parseDouble(jingDuStr));
					com.baidu.mapapi.map.MapStatus.Builder build = new MapStatus.Builder(mBaiduMap.getMapStatus());
					build.target(ll);
					build.zoom(17.0f);
					MapStatus ms = build.build();
					MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
					mBaiduMap.animateMapStatus(u);
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(ll));
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     		
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barter_detail_map);
//	    PoiNearbySearchOption option=new PoiNearbySearchOption().location(new LatLng(1, 1)).keyword("").radius(5000);
//		mPoiSearch.searchNearby(option);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("小区地址");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		Intent intent=getIntent();
		jingDuStr=intent.getStringExtra("jingdu");
		weiDuStr=intent.getStringExtra("weidu");
		mMapView = (MapView) findViewById(R.id.bmapView);
	       mBaiduMap = mMapView.getMap();
	        IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
			iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
			iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
			mReceiver = new SDKReceiver();
			registerReceiver(mReceiver, iFilter);
			initLocation();
			mSearch = GeoCoder.newInstance();
	}
	 private void initLocation(){
	    	// 开启定位图层
	    	mBaiduMap.setMyLocationEnabled(true);
	    	mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			
			mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
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
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
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

