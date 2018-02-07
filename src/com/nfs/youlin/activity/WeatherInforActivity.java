package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ChooseWeaherTB;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.XingZuoBean;
import com.nfs.youlin.view.YLProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherInforActivity extends Activity {
	public TextView tv_position;
	public TextView tv_wendu;
	public TextView tv_weather;
	public TextView tv_time;
	public TextView tv_bai_wendu;
	public TextView tv_wan_wendu;
	public TextView tv1_weather;
	public TextView tv1_feng;
	public TextView tv1_low;
	public TextView tv1_high;
	public TextView tv2_weather;
	public TextView tv2_feng;
	public TextView tv2_low;
	public TextView tv2_high;
	public TextView tv3_day;
	public TextView tv3_weather;
	public TextView tv3_feng;
	public TextView tv3_low;
	public TextView tv3_high;
	public TextView tv4_day;
	public TextView tv4_weather;
	public TextView tv4_feng;
	public TextView tv4_low;
	public TextView tv4_high;
	public TextView tv5_day;
	public TextView tv5_weather;
	public TextView tv5_feng;
	public TextView tv5_low;
	public TextView tv5_high;
	public TextView tv_updown;
	public TextView tv_shidu;
	public TextView tv_ziwaixian;
	public TextView tv_chuanyi;
	public TextView tv_kongtiao;
	public TextView tv_xiche;
	public TextView tv_yundong;
	public TextView zh, jk, cy, love, sp, color, count, jk_infor, work_infor, love_infor, cy_infor;
	public ImageView img_bai;
	public ImageView img_wan;
	public ImageView img_updown;
	public LinearLayout ll_yincang;
	public LinearLayout ll_updown;
	public XingZuoBean[] xzlist;
	private String Flag = "none";
	YLProgressDialog yLProgressDialog;
	HorizontalScrollView hs;
	boolean flg = false;
	int index = 0;
	int width;
	// private RelativeLayout nonewsview;
	// private TextView nonemsgtext;
	Animation scale;
	ImageView im1, im2, im3, im4, im5, im6, im7, im8, im9, im10, im11, im12;
	List<ImageView> list;
	TextView tv;
	ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_infor);
		imageLoader = ImageLoader.getInstance();
		Init();
		ll_updown.setOnClickListener(new MyOnClick());
		tv = (TextView) findViewById(R.id.tv);
		WindowManager wm = this.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		LayoutParams para;
		for (int i = 0; i < 12; i++) {
			para = list.get(i).getLayoutParams();
			para.height = (width - 30) / 5;
			para.width = (width - 30) / 5;
			if (i == 2) {
				scale.setDuration(100);
				im3.startAnimation(scale);
			}
			list.get(i).setLayoutParams(para);
		}
		Data();

	}

	public void Data() {
		// nonemsgtext =(TextView)
		// findViewById(R.id.friend_circle_noinfo_tvnews);
		// nonewsview = (RelativeLayout) findViewById(R.id.nonewsview);
		// NewsLists.clear();
		if (NetworkService.networkBool) {
			RequestParams reference = new RequestParams();
			reference.put("community_id", App.sFamilyCommunityId);
			reference.put("tag", "getweaorzodinfo");
			reference.put("apitype", IHttpRequestUtils.APITYPE[1]);
			reference.put("weaorzod_id", getIntent().getIntExtra("id", 0));
			// reference.put("key_word",searchmessage);
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
					super.onSuccess(statusCode, headers, response);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					// TODO Auto-generated method stub
					try {
						JSONArray zodiac_detail = response.getJSONArray("zodiac_detail");
						JSONObject weaher_detail = response.getJSONObject("weaher_detail");
						Flag = response.getString("flag");
						if ("ok".equals(Flag)) {
							getTopicDetailsInfos(zodiac_detail);
							getweatherInfo(weaher_detail);
							yLProgressDialog.dismiss();
							// nonemsgtext.setVisibility(View.VISIBLE);
							// new Timer().schedule(new TimerTask() {
							// @Override
							// public void run() {
							// // TODO Auto-generated method stub
							// AllNewshistoryList.this.runOnUiThread(new
							// Runnable() {
							// public void run() {
							// nonemsgtext.setVisibility(View.GONE);
							// }
							// });
							// }
							// }, 1000);
						} else if ("empty".equals(Flag)) {
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					new ErrorServer(WeatherInforActivity.this, responseString.toString());
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
	}

	private void getweatherInfo(JSONObject response) {
		try {
			String position = response.getJSONObject("pm25").getString("cityName");
			JSONObject realtime = response.getJSONObject("realtime");
			JSONObject weather = realtime.getJSONObject("weather");
			String nowwendu = weather.getString("temperature");
			String nowweather = weather.getString("info");
			String updatatime = realtime.getString("time");
			String wind = realtime.getJSONObject("wind").getString("direct")
					+ realtime.getJSONObject("wind").getString("power");
			JSONArray weartherarray = response.getJSONArray("weather");
			JSONObject info = weartherarray.getJSONObject(0).getJSONObject("info");
			String bai_weather = info.getJSONArray("day").getString(1);
			String bai_wendu = info.getJSONArray("day").getString(2);
			String night_weather = info.getJSONArray("night").getString(1);
			String night_wendu = info.getJSONArray("night").getString(2);
			String weather1 = weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(1);
			String wind1 = weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(3)
					+ weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(4);
			String low1 = weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("night").getString(2);
			String hight1 = weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(2);
			String weather2 = weartherarray.getJSONObject(2).getJSONObject("info").getJSONArray("day").getString(1);
			String wind2 = weartherarray.getJSONObject(2).getJSONObject("info").getJSONArray("day").getString(3)
					+ weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(4);
			String low2 = weartherarray.getJSONObject(2).getJSONObject("info").getJSONArray("night").getString(2);
			String hight2 = weartherarray.getJSONObject(2).getJSONObject("info").getJSONArray("day").getString(2);
			String day3 = "周" + weartherarray.getJSONObject(3).getString("week");
			String weather3 = weartherarray.getJSONObject(3).getJSONObject("info").getJSONArray("day").getString(1);
			String wind3 = weartherarray.getJSONObject(3).getJSONObject("info").getJSONArray("day").getString(3)
					+ weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(4);
			String low3 = weartherarray.getJSONObject(3).getJSONObject("info").getJSONArray("night").getString(2);
			String hight3 = weartherarray.getJSONObject(3).getJSONObject("info").getJSONArray("day").getString(2);
			String day4 = "周" + weartherarray.getJSONObject(4).getString("week");
			String weather4 = weartherarray.getJSONObject(4).getJSONObject("info").getJSONArray("day").getString(1);
			String wind4 = weartherarray.getJSONObject(4).getJSONObject("info").getJSONArray("day").getString(3)
					+ weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(4);
			String low4 = weartherarray.getJSONObject(4).getJSONObject("info").getJSONArray("night").getString(2);
			String hight4 = weartherarray.getJSONObject(4).getJSONObject("info").getJSONArray("day").getString(2);
			String day5 = "周" + weartherarray.getJSONObject(5).getString("week");
			String weather5 = weartherarray.getJSONObject(5).getJSONObject("info").getJSONArray("day").getString(1);
			String wind5 = weartherarray.getJSONObject(5).getJSONObject("info").getJSONArray("day").getString(3)
					+ weartherarray.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(4);
			String low5 = weartherarray.getJSONObject(5).getJSONObject("info").getJSONArray("night").getString(2);
			String hight5 = weartherarray.getJSONObject(5).getJSONObject("info").getJSONArray("day").getString(2);
			JSONObject life = response.getJSONObject("life");
			String shidu = life.getJSONObject("info").getJSONArray("wuran").getString(0);
			String ziwaixian = life.getJSONObject("info").getJSONArray("ziwaixian").getString(0);
			String chuanyi = life.getJSONObject("info").getJSONArray("chuanyi").getString(0);
			String kongtiao = life.getJSONObject("info").getJSONArray("kongtiao").getString(0);
			String xiche = life.getJSONObject("info").getJSONArray("xiche").getString(0);
			String yundong = life.getJSONObject("info").getJSONArray("yundong").getString(0);
			tv_position.setText(position);
			tv_wendu.setText(nowwendu+"°");
			tv_weather.setText(nowweather + "  " + wind);
			tv_time.setText("更新时间：" + updatatime);
			img_bai.setImageResource(new ChooseWeaherTB().choosetb(bai_weather));
			img_wan.setImageResource(new ChooseWeaherTB().choosetb(night_weather));
			tv_bai_wendu.setText(bai_wendu + "°");
			tv_wan_wendu.setText(night_wendu + "°");
			tv1_weather.setText(weather1);
			tv1_feng.setText(wind1);
			tv1_low.setText(low1 + "°");
			tv1_high.setText(hight1 + "°");
			tv2_weather.setText(weather2);
			tv2_feng.setText(wind2);
			tv2_low.setText(low2 + "°");
			tv2_high.setText(hight2 + "°");
			tv3_day.setText(day3);
			tv3_weather.setText(weather3);
			tv3_feng.setText(wind3);
			tv3_low.setText(low3 + "°");
			tv3_high.setText(hight3 + "°");
			tv4_day.setText(day4);
			tv4_weather.setText(weather4);
			tv4_feng.setText(wind4);
			tv4_low.setText(low4 + "°");
			tv4_high.setText(hight4 + "°");
			tv5_day.setText(day5);
			tv5_weather.setText(weather5);
			tv5_feng.setText(wind5);
			tv5_low.setText(low5 + "°");
			tv5_high.setText(hight5 + "°");
			tv_shidu.setText(shidu);
			tv_ziwaixian.setText(ziwaixian);
			tv_chuanyi.setText(chuanyi);
			tv_kongtiao.setText(kongtiao);
			tv_xiche.setText(xiche);
			tv_yundong.setText(yundong);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getTopicDetailsInfos(JSONArray response) {
		int responseLen = response.length();
		if (responseLen > 0) {

			/************************** new add **************************/
			for (int i = 0; i < responseLen; i++) {
				try {
					JSONObject jsonObject = new JSONObject(response.getString(i));
					XingZuoBean news = new XingZuoBean();
					news.setZhcount(jsonObject.getString("all"));
					news.setJkcount(jsonObject.getString("health"));
					news.setCycount(jsonObject.getString("money"));
					news.setLove(jsonObject.getString("love"));
					news.setSpxz(jsonObject.getString("QFriend"));
					news.setLuckycolor(jsonObject.getString("color"));
					news.setLuckycount(jsonObject.getString("number"));
					news.setJkinfor(jsonObject.getJSONObject("week").getString("health"));
					news.setWorkinfor(jsonObject.getJSONObject("week").getString("work"));
					news.setLoveinfor(jsonObject.getJSONObject("week").getString("love"));
					news.setCyinfor(jsonObject.getJSONObject("week").getString("money"));
					if (jsonObject.getString("name").equals("水瓶座")) {
						// xzlist.add(0,news);
						xzlist[0] = news;
					} else if (jsonObject.getString("name").equals("双鱼座")) {
						// xzlist.add(1,news);
						xzlist[1] = news;
					} else if (jsonObject.getString("name").equals("白羊座")) {
						// xzlist.add(2,news);
						xzlist[2] = news;
					} else if (jsonObject.getString("name").equals("金牛座")) {
						// xzlist.add(3,news);
						xzlist[3] = news;
					} else if (jsonObject.getString("name").equals("双子座")) {
						// xzlist.add(4,news);
						xzlist[4] = news;
					} else if (jsonObject.getString("name").equals("巨蟹座")) {
						// xzlist.add(5,news);
						xzlist[5] = news;
					} else if (jsonObject.getString("name").equals("狮子座")) {
						// xzlist.add(6,news);
						xzlist[6] = news;
					} else if (jsonObject.getString("name").equals("处女座")) {
						// xzlist.add(7,news);
						xzlist[7] = news;
					} else if (jsonObject.getString("name").equals("天秤座")) {
						// xzlist.add(8,news);
						xzlist[8] = news;
					} else if (jsonObject.getString("name").equals("天蝎座")) {
						// xzlist.add(9,news);
						xzlist[9] = news;
					} else if (jsonObject.getString("name").equals("射手座")) {
						// xzlist.add(10,news);
						xzlist[10] = news;
					} else if (jsonObject.getString("name").equals("摩羯座")) {
						// xzlist.add(11,news);
						xzlist[11] = news;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			// XingZuoBean bean=xzlist[2];
			XZData(xzlist[2]);
		}

	}

	public void XZData(XingZuoBean bean) {
		zh.setText(bean.getZhcount());
		jk.setText(bean.getJkcount());
		cy.setText(bean.getCycount());
		love.setText(bean.getLove());
		sp.setText(bean.getSpxz());
		color.setText(bean.getLuckycolor());
		count.setText(bean.getLuckycount());
		jk_infor.setText(bean.getJkinfor());
		work_infor.setText(bean.getWorkinfor());
		love_infor.setText(bean.getLoveinfor());
		cy_infor.setText(bean.getCyinfor());
	}

	public void Init() {
		xzlist = new XingZuoBean[12];
		hs = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		tv_position = (TextView) findViewById(R.id.position);
		tv_wendu = (TextView) findViewById(R.id.tv_wendu);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_weather = (TextView) findViewById(R.id.tv_weather);
		tv_bai_wendu = (TextView) findViewById(R.id.tv_bai_wendu);
		tv_wan_wendu = (TextView) findViewById(R.id.tv_wan_wendu);
		tv1_weather = (TextView) findViewById(R.id.tv1_weather);
		tv1_feng = (TextView) findViewById(R.id.tv1_feng);
		tv1_low = (TextView) findViewById(R.id.tv1_low);
		tv1_high = (TextView) findViewById(R.id.tv1_high);
		tv2_weather = (TextView) findViewById(R.id.tv2_weather);
		tv2_feng = (TextView) findViewById(R.id.tv2_feng);
		tv2_low = (TextView) findViewById(R.id.tv2_low);
		tv2_high = (TextView) findViewById(R.id.tv2_high);
		tv3_day = (TextView) findViewById(R.id.tv3_day);
		tv3_weather = (TextView) findViewById(R.id.tv3_weather);
		tv3_feng = (TextView) findViewById(R.id.tv3_feng);
		tv3_low = (TextView) findViewById(R.id.tv3_low);
		tv3_high = (TextView) findViewById(R.id.tv3_high);
		tv4_day = (TextView) findViewById(R.id.tv4_day);
		tv4_weather = (TextView) findViewById(R.id.tv4_weather);
		tv4_feng = (TextView) findViewById(R.id.tv4_feng);
		tv4_low = (TextView) findViewById(R.id.tv4_low);
		tv4_high = (TextView) findViewById(R.id.tv4_high);
		tv5_day = (TextView) findViewById(R.id.tv5_day);
		tv5_weather = (TextView) findViewById(R.id.tv5_weather);
		tv5_feng = (TextView) findViewById(R.id.tv5_feng);
		tv5_low = (TextView) findViewById(R.id.tv5_low);
		tv5_high = (TextView) findViewById(R.id.tv5_high);
		tv_updown = (TextView) findViewById(R.id.tv_updown);
		tv_shidu = (TextView) findViewById(R.id.tv_shidu);
		tv_ziwaixian = (TextView) findViewById(R.id.tv_ziwaixian);
		tv_chuanyi = (TextView) findViewById(R.id.tv_chuanyi);
		tv_kongtiao = (TextView) findViewById(R.id.tv_kongtiao);
		tv_xiche = (TextView) findViewById(R.id.tv_xiche);
		tv_yundong = (TextView) findViewById(R.id.tv_yundong);
		zh = (TextView) findViewById(R.id.tv_zonghe);
		jk = (TextView) findViewById(R.id.tv_jiankang);
		cy = (TextView) findViewById(R.id.tv_caiyun);
		love = (TextView) findViewById(R.id.tv_love);
		sp = (TextView) findViewById(R.id.tv_supei);
		color = (TextView) findViewById(R.id.tv_xingyunse);
		count = (TextView) findViewById(R.id.tv_xingyunshuzi);
		jk_infor = (TextView) findViewById(R.id.bz_jkinfo);
		work_infor = (TextView) findViewById(R.id.bz_workinfo);
		love_infor = (TextView) findViewById(R.id.bz_loveinfo);
		cy_infor = (TextView) findViewById(R.id.bz_cyinfo);
		img_bai = (ImageView) findViewById(R.id.img_bai_tb);
		img_wan = (ImageView) findViewById(R.id.img_wan_tb);
		img_updown = (ImageView) findViewById(R.id.img_updown);
		ll_yincang = (LinearLayout) findViewById(R.id.ll_yincang);
		ll_updown = (LinearLayout) findViewById(R.id.ll_updown);
		im1 = (ImageView) findViewById(R.id.imageView1);
		im2 = (ImageView) findViewById(R.id.imageView2);
		im3 = (ImageView) findViewById(R.id.imageView3);
		im4 = (ImageView) findViewById(R.id.imageView4);
		im5 = (ImageView) findViewById(R.id.imageView5);
		im6 = (ImageView) findViewById(R.id.imageView6);
		im7 = (ImageView) findViewById(R.id.imageView7);
		im8 = (ImageView) findViewById(R.id.imageView8);
		im9 = (ImageView) findViewById(R.id.imageView9);
		im10 = (ImageView) findViewById(R.id.imageView10);
		im11 = (ImageView) findViewById(R.id.imageView11);
		im12 = (ImageView) findViewById(R.id.imageView12);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "01.png", im1, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "02.png", im2, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "03.png", im3, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "04.png", im4, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "05.png", im5, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "06.png", im6, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "07.png", im7, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "08.png", im8, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "09.png", im9, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "10.png", im10, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "11.png", im11, App.options_weather);
		imageLoader.displayImage(IHttpRequestUtils.XINGZUOURL + "12.png", im12, App.options_weather);

		list = new ArrayList<ImageView>();
		yLProgressDialog = YLProgressDialog.createDialogwithcircle(WeatherInforActivity.this, "", 1);
		yLProgressDialog.setCanceledOnTouchOutside(false);
		yLProgressDialog.show();
		data();
		hs.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					for (int i = 0; i < list.size(); i++) {
						list.get(i).clearAnimation();
					}
					break;

				default:
					break;
				}
				return false;
			}
		});
		scale = AnimationUtils.loadAnimation(this, R.anim.changebig);
		scale.setFillAfter(true);
		for (int i = 0; i < 12; i++) {
			list.get(i).setOnClickListener(new MyImageOnclick());
		}
	}

	public class MyImageOnclick implements OnClickListener {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// arg0.startAnimation(scale);
			if(NetworkService.networkBool){
			switch (arg0.getId()) {
			case R.id.imageView1:
				StartaSc(0);
				// XZData(xzlist.get(0));
				XZData(xzlist[0]);
				break;
			case R.id.imageView2:
				StartaSc(1);
				// XZData(xzlist.get(1));
				XZData(xzlist[1]);
				break;
			case R.id.imageView3:
				StartaSc(2);
				// XZData(xzlist.get(2));
				XZData(xzlist[2]);
				break;
			case R.id.imageView4:
				StartaSc(3);
				XZData(xzlist[3]);
				// XZData(xzlist.get(3));
				break;
			case R.id.imageView5:
				StartaSc(4);
				XZData(xzlist[4]);
				// XZData(xzlist.get(4));
				break;
			case R.id.imageView6:
				StartaSc(5);
				XZData(xzlist[5]);
				// XZData(xzlist.get(5));
				break;
			case R.id.imageView7:
				StartaSc(6);
				XZData(xzlist[6]);
				// XZData(xzlist.get(6));
				break;
			case R.id.imageView8:
				StartaSc(7);
				XZData(xzlist[7]);
				// XZData(xzlist.get(7));
				break;
			case R.id.imageView9:
				StartaSc(8);
				XZData(xzlist[8]);
				// XZData(xzlist.get(8));
				break;
			case R.id.imageView10:
				StartaSc(9);
				XZData(xzlist[9]);
				// XZData(xzlist.get(9));
				break;
			case R.id.imageView11:
				StartaSc(10);
				XZData(xzlist[10]);
				// XZData(xzlist.get(10));
				break;
			case R.id.imageView12:
				StartaSc(11);
				XZData(xzlist[11]);
				// XZData(xzlist.get(11));
				break;
			default:
				break;
			}}
		}

	}

	public void StartaSc(int index) {
		for (int i = 0; i < 12; i++) {
			if (i == index) {
				scale.setDuration(200); // 设置动画持续时间
				list.get(i).startAnimation(scale);
				;
			} else {
				list.get(i).clearAnimation();
			}
		}
	}

	public void data() {
		list.add(im1);
		list.add(im2);
		list.add(im3);
		list.add(im4);
		list.add(im5);
		list.add(im6);
		list.add(im7);
		list.add(im8);
		list.add(im9);
		list.add(im10);
		list.add(im11);
		list.add(im12);

	}

	class MyOnClick implements OnClickListener{
		
		@Override
		public void onClick(View arg0) {
		  switch (arg0.getId()) {
		case R.id.ll_updown:
			if(flg){
				
				ll_yincang.setVisibility(View.GONE);
				img_updown.setImageResource(R.drawable.jiantou2);
				tv_updown.setText("查看未来几天");
				flg=false;
			}else {
				
				ll_yincang.setVisibility(View.VISIBLE);
				img_updown.setImageResource(R.drawable.jiantou1);
				tv_updown.setText("收起");
				flg=true;
			}
			break;

		default:
			break;
		}
			
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
