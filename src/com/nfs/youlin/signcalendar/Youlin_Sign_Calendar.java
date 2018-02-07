package com.nfs.youlin.signcalendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Youlin_Sign_Calendar extends Activity {
	private SignCalendar signCalendar;
    private TextView signtitle;
    private Intent intent;
    private String signdata;
    private String[] currentday = new String[3];
    private String todayJiFen;
//    Map<String , List<String>> signdatamap = new HashMap<String , List<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.youlincalender);
        signtitle = (TextView)findViewById(R.id.signtitle);
        intent = getIntent();
        signdata = intent.getStringExtra("signjsondata");
//        signdata = "[{\"timestamp\":1455169369,\"month\":2,\"day\":11,\"year\":2016}"+","+"{\"timestamp\":1455169369,\"month\":2,\"day\":10,\"year\":2016}]";
        Loger.d("test4", "signdata="+signdata);
        WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//		p.height = (int) (d.getHeight() * 0.5);
		p.height = LayoutParams.WRAP_CONTENT;
		 //高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.84); //宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		
        signCalendar = (SignCalendar) findViewById(R.id.my_sign_calendar);
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");       
		String date = sDateFormat.format(new java.util.Date());
		currentday = date.split("-");
		ArrayList<MonthSignData> monthDatas = new ArrayList<MonthSignData>();
		if(signdata.equals("none")){
			Toast.makeText(this, "还没有签到记录!", Toast.LENGTH_SHORT).show();
			return;
		}else{ 
			Loger.d("test4", "currentday"+date);
			JSONArray signdatajson;
			try {
				signdatajson = new JSONArray(signdata);
				MonthSignData monthData1 = new MonthSignData();
		        ArrayList<Date> signDates1 = new ArrayList<Date>();
				MonthSignData monthData2 = new MonthSignData();
				ArrayList<Date> signDates2 = new ArrayList<Date>();
				MonthSignData monthData3 = new MonthSignData();
				ArrayList<Date> signDates3 = new ArrayList<Date>();
				for(int i = 1;i<signdatajson.length();i++){
					String year = ((JSONObject)signdatajson.get(i)).getString("year");
					String month = ((JSONObject)signdatajson.get(i)).getString("month");
					String day = ((JSONObject)signdatajson.get(i)).getString("day");
					Loger.d("test4", "year="+year+"month="+month+"day="+day);
					if(monthData1.getMonth() == -1 ||( monthData1.getMonth() == (Integer.parseInt(month)-1) &&monthData1.getYear() == Integer.parseInt(year))){
						monthData1.setYear(Integer.parseInt(year));
				        monthData1.setMonth(Integer.parseInt(month)-1);
				        Date date11= new Date(Integer.parseInt(year)-1900,Integer.parseInt(month)-1, Integer.parseInt(day));
				        signDates1.add(date11);
				        Loger.d("test4","cccccccccccccc");
					}else if(monthData2.getMonth() == -1 ||( monthData2.getMonth() == (Integer.parseInt(month)-1) &&
							monthData2.getYear() == Integer.parseInt(year))){
						monthData2.setYear(Integer.parseInt(year));
						monthData2.setMonth(Integer.parseInt(month)-1);
				        Date date11= new Date(Integer.parseInt(year)-1900,Integer.parseInt(month)-1, Integer.parseInt(day));
				        signDates2.add(date11);
				        Loger.d("test4","dddddddddddddddddd");
					}else if(monthData3.getMonth() == -1 ||( monthData3.getMonth() == (Integer.parseInt(month)-1) &&
							monthData3.getYear() == Integer.parseInt(year))){
						monthData3.setYear(Integer.parseInt(year));
						monthData3.setMonth(Integer.parseInt(month)-1);
				        Date date11= new Date(Integer.parseInt(year)-1900,Integer.parseInt(month)-1, Integer.parseInt(day));
				        signDates3.add(date11);
				        Loger.d("test4","eeeeeeeeeeee");
					}	
				}
				if(monthData3.getMonth() != -1){
					monthData3.setSignDates(signDates3);
					monthDatas.add(monthData3);
					Loger.d("test4","hhhhhhhhhhhhhh");
				}
				if(monthData2.getMonth() != -1){
					monthData2.setSignDates(signDates2);
					monthDatas.add(monthData2);
					Loger.d("test4","gggggggggggggg");
				}
				if(monthData1.getMonth() != -1){
					monthData1.setSignDates(signDates1);
					monthDatas.add(monthData1);
					Loger.d("test4","ffffffffffffffff");
				}
				
				todayJiFen=((JSONObject)signdatajson.get(0)).getString("credit");
				signtitle.setText(Html.fromHtml("<font color='black'>今天已签过，已领</font>"+"<font color= '#ffba02'>"+" "+todayJiFen+" "+"</font>"+"<font color='black'>积分</font>"));
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
        

//        ArrayList<MonthSignData> monthDatas = new ArrayList<MonthSignData>();
//        MonthSignData monthData1 = new MonthSignData();
//        monthData1.setYear(2015);
//        monthData1.setMonth(8);
//        ArrayList<Date> signDates1 = new ArrayList<Date>();
//        Date date11= new Date(2015-1900, 7, 1);
//        Date date12= new Date(2015-1900, 7, 19);
//        Date date13= new Date(2015-1900, 7, 15);
//        Date date14= new Date(2015-1900, 7, 16);
//        signDates1.add(date11);
//        signDates1.add(date12);
//        signDates1.add(date13);
//        signDates1.add(date14);
//        monthData1.setSignDates(signDates1);
//        monthDatas.add(monthData1);
//


//        MonthSignData monthData3 = new MonthSignData();
//        monthData3.setYear(2016);
//        monthData3.setMonth(2-1);
//        ArrayList<Date> signDates3 = new ArrayList<Date>();
//        Date date31= new Date(2016-1900, 2-1, 1);
//        Date date32= new Date(2016-1900, 2-1, 11);
//        Date date33= new Date(2016-1900, 2-1, 15);
//        signDates3.add(date31);
//        signDates3.add(date32);
//        signDates3.add(date33);
//        monthData3.setSignDates(signDates3);
//        monthDatas.add(monthData3);
//        
//      MonthSignData monthData2 = new MonthSignData();
//      monthData2.setYear(2016);
//      monthData2.setMonth(3);
//      ArrayList<Date> signDates2 = new ArrayList<Date>();
//      Date date21= new Date(2016-1900, 3, 1);
//      Date date22= new Date(2016-1900, 3, 17);
//      Date date23= new Date(2016-1900, 3, 15);
//      Date date24= new Date(2016-1900, 3, 16);
//      signDates2.add(date21);
//      signDates2.add(date22);
//      signDates2.add(date23);
//      signDates2.add(date24);
//      monthData2.setSignDates(signDates2);
//      monthDatas.add(monthData2);
      
        Date today = new Date(Integer.parseInt(currentday[0])-1900, Integer.parseInt(currentday[1])-1, Integer.parseInt(currentday[2]));
        signCalendar.setToday(today);
        signCalendar.setSignDatas(monthDatas);
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
