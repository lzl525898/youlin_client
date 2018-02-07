package com.nfs.youlin.activity.personal;

import java.util.Calendar;

import u.aly.da;
import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StrToTime;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.Toast;
public class PersonalInfoBirthdayActivity extends Activity {
	private int year;
	private int month;
	private int day;
	private AccountDaoDBImpl accountDaoDBImpl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info_birthday);
		accountDaoDBImpl=new AccountDaoDBImpl(this);
		DatePicker datePicker=(DatePicker)findViewById(R.id.dataPicker);
		Button buttonOk=(Button)findViewById(R.id.dataBnOk);
		Button buttonCancel=(Button)findViewById(R.id.dataBnCancel);
		Calendar c=Calendar.getInstance();
		year=c.get(Calendar.YEAR);
		month=c.get(Calendar.MONTH);
		day=c.get(Calendar.DAY_OF_MONTH);
		datePicker.init(year, month, day, new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month,
					int day) {
				// TODO Auto-generated method stub
				PersonalInfoBirthdayActivity.this.year=year;
				PersonalInfoBirthdayActivity.this.month=month;
				PersonalInfoBirthdayActivity.this.day=day;
			}
		});
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					showDate(PersonalInfoBirthdayActivity.this.year,
						PersonalInfoBirthdayActivity.this.month,
						PersonalInfoBirthdayActivity.this.day);
				}
				finish();
			}
		});
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void showDate(int year,int month,int day){
		//PersonalInformationActivity.brithdayTv.setText(year+"年"+(month+1)+"月"+day+"日");
		StrToTime time=new StrToTime();
		Account account=accountDaoDBImpl.findAccountByPhone(App.sUserPhone);
		Loger.i("youlin", "333333333333333333336666666666666666666---->"+time.getTime(year+"年"+(month+1)+"月"+day+"日")+" "+System.currentTimeMillis());
		if(time.getTime(year+"年"+(month+1)+"月"+day+"日")>System.currentTimeMillis()){
			Toast.makeText(PersonalInfoBirthdayActivity.this, "生日日期不合理", Toast.LENGTH_SHORT).show();
		}else{
			account.setUser_birthday(Long.valueOf(time.getTime(year+"年"+(month+1)+"月"+day+"日")));
			accountDaoDBImpl.modifyObject(account);
			accountDaoDBImpl.releaseDatabaseRes();
		}
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
}
