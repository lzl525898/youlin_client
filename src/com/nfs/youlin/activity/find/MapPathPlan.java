package com.nfs.youlin.activity.find;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.poi.BaiduMapPoiSearch;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.baidu.mapapi.utils.route.RouteParaOption.EBusStrategyType;
import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class MapPathPlan{
	Context context;
	LocationClient mLocationClient;
    MyLocationListener mMyLocationListener;
    
	// 起始坐标
    double mLat1;
    double mLon1;
    // 终点坐标
    double mLat2;
    double mLon2;
    public MapPathPlan(Context context,double mLat2,double mLon2) {
		// TODO Auto-generated constructor stub
    	this.context=context;
    	this.mLat2=mLat2;
    	this.mLon2=mLon2;
    	startRoutePlanTransit();
	}
	/**
     * 启动百度地图公交路线规划
     */
    public void startRoutePlanTransit() {
    	mLocationClient = new LocationClient((Activity)context);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
    	initLocation();
    	mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        mLocationClient.requestLocation();
    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)context);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp((Activity)context);
            }
        });

        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span=1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(false);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mLocationClient.setLocOption(option);
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
        	mLat1=location.getLatitude();
        	mLon1=location.getLongitude();
        	LatLng ptStart = new LatLng(mLat1, mLon1);
            LatLng ptEnd = new LatLng(mLat2, mLon2);
            Loger.i("LYM", "222222222222222---->"+mLat1+" "+mLon1);
            // 构建 route搜索参数
            RouteParaOption para = new RouteParaOption()
                .startPoint(ptStart)
//              .startName("天安门")
                .endPoint(ptEnd)
//              .endName("百度大厦")
                .busStrategyType(EBusStrategyType.bus_recommend_way);

//            RouteParaOption para = new RouteParaOption()
//                    .startName("天安门").endName("百度大厦").busStrategyType(EBusStrategyType.bus_recommend_way);

//            RouteParaOption para = new RouteParaOption()
//            		  .startPoint(ptStart).endPoint(ptEnd).busStrategyType(EBusStrategyType.bus_recommend_way);

            try {
                BaiduMapRoutePlan.openBaiduMapTransitRoute(para, (Activity)context);
            } catch (Exception e) {
                e.printStackTrace();
                showDialog();
            }
            mLocationClient.stop();
        }
    }
}
