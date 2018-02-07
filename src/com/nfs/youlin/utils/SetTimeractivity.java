package com.nfs.youlin.utils;

import java.util.Calendar;

import cn.jpush.android.api.JPushInterface;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.nfs.youlin.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetTimeractivity extends Activity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener{
	private Button timeyes;
	private Button timecancel;
	private LinearLayout daylay;
	private LinearLayout timelay;
	private TextView yearview;
	private TextView mouthview;
	private TextView dayview;
	private TextView hourview;
	private TextView minuteview;
	private TextView groupview;
	private TextView timetitle;
	private Intent intent;
	private int year,month,day,hour,minute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/************************/
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settime);
		Intent timeIntent=getIntent();
		String timeIntentStr=timeIntent.getStringExtra("time");
		intent = new Intent();
		//year = 0;month =0; day = 0;hour=0;minute=0;
		timeyes = (Button)findViewById(R.id.timeok);
		timecancel = (Button)findViewById(R.id.timecancel);
		daylay = (LinearLayout)findViewById(R.id.daylayout);
		timelay = (LinearLayout)findViewById(R.id.timelayout);
		Calendar c=Calendar.getInstance();
		if(timeIntentStr.isEmpty()||timeIntentStr==null||timeIntentStr.length()==0||timeIntentStr.equals("null")){
			year=c.get(Calendar.YEAR);
			month=c.get(Calendar.MONTH)+1;
			day=c.get(Calendar.DAY_OF_MONTH);
			hour=c.get(Calendar.HOUR_OF_DAY);
			minute=c.get(Calendar.MINUTE);
		}else{
			String[] yearSp=timeIntentStr.split("[年]");
			String[] monthSp=yearSp[1].split("[月]");
			String[] daySp=monthSp[1].split("[日]");
			String[] hourSp=daySp[1].split("[时]");
			String[] minSp=hourSp[1].split("[分]");
			year=Integer.parseInt(yearSp[0]);
			month=Integer.parseInt(monthSp[0]);
			day=Integer.parseInt(daySp[0]);
			hour=Integer.parseInt(hourSp[0]);
			minute=Integer.parseInt(minSp[0]);
		}
		yearview = (TextView)findViewById(R.id.year);
		yearview.setText(String.valueOf(year)+"年");
		mouthview = (TextView)findViewById(R.id.mouth);
		mouthview.setText(String.valueOf(month)+"月");
		dayview = (TextView)findViewById(R.id.day);
		dayview.setText(String.valueOf(day)+"日");
		hourview = (TextView)findViewById(R.id.hour);
		hourview.setText(String.valueOf(hour)+"时");
		minuteview = (TextView)findViewById(R.id.minute);
		minuteview.setText(String.valueOf(minute)+"分");
		groupview = (TextView)findViewById(R.id.timegroup);
		timetitle = (TextView)findViewById(R.id.settimetitle);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//		p.height = (int) (d.getHeight() * 0.32);
		 //高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.9); //宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		Intent intent1 = this.getIntent();
		String parent = intent1.getStringExtra("parent");
		if(parent.equals("start")){
			timetitle.setText("开始时间");
		}else{
			timetitle.setText("结束时间");
		}
		daylay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        Calendar now = Calendar.getInstance();
		        DatePickerDialog dpd = DatePickerDialog.newInstance(
		        		SetTimeractivity.this,
		                now.get(Calendar.YEAR),
		                now.get(Calendar.MONTH),
		                now.get(Calendar.DAY_OF_MONTH)
		        );
		        dpd.setThemeDark(true);
		        dpd.vibrate(true);
		        dpd.dismissOnPause(true);
		        dpd.showYearPickerFirst(false);
		        dpd.show(getFragmentManager(), "Datepickerdialog");
			}
		});
		timelay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		        Calendar now = Calendar.getInstance();
		        TimePickerDialog tpd = TimePickerDialog.newInstance(
		        		SetTimeractivity.this,
		                now.get(Calendar.HOUR_OF_DAY),
		                now.get(Calendar.MINUTE),
		                true
		        );
		        tpd.setThemeDark(true);
		        tpd.vibrate(true);
		        tpd.dismissOnPause(true);
		        tpd.show(getFragmentManager(), "Timepickerdialog");
			}
		});
		timeyes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				intent.putExtra("year", year);
				intent.putExtra("month", month);
				intent.putExtra("day", day);
				intent.putExtra("hour", hour);
				intent.putExtra("minute", minute);
				SetTimeractivity.this.setResult(RESULT_OK, intent);
				SetTimeractivity.this.finish();
			}
		});

		timecancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SetTimeractivity.this.finish();
			}
		});
	}

	@Override
	public void onDateSet(DatePickerDialog arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		yearview.setText(String.valueOf(arg1)+"年");
		mouthview.setText(String.valueOf(arg2)+"月");
		dayview.setText(String.valueOf(arg3)+"日");
		year = arg1;month = arg2;day = arg3;
	}

	@Override
	public void onTimeSet(RadialPickerLayout arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		hourview.setText(String.valueOf(arg1)+"时");
		minuteview.setText(String.valueOf(arg2)+"分");
		hour = arg1;minute = arg2;
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
