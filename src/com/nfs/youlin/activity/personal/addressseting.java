package com.nfs.youlin.activity.personal;


import java.util.List;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;
public class addressseting extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.addressseting);
        // this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.address_title);
        Button back = (Button)findViewById(R.id.backbutton);
        Button addaddr = (Button)findViewById(R.id.addaddress);
        Button addaddrbig = (Button)findViewById(R.id.writeaddress);
//        Drawable drawable1 = getResources().getDrawable(R.drawable.nav_fanhui_xin_tiao);
//        drawable1.setBounds(0, 1, 28, 46);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//        back.setCompoundDrawables(drawable1, null, null, null);//只放左边
        back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonTools.goBack();
			}
		});
		
		
		
        addaddr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(addressseting.this);
					List<Object> familyObjs = allFamilyDaoDBImpl.findAllObject();
					Intent intent = new Intent(addressseting.this,write_address.class);
					Loger.d("test4", "familyObjs.size()="+familyObjs.size());
					if(familyObjs.size()>0)
						intent.putExtra("setaddrmethod", "add");
					else
						intent.putExtra("setaddrmethod", "write");
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(intent);
						finish();
				}else{
					Toast.makeText(addressseting.this,"请先开启网络", Toast.LENGTH_SHORT).show();
				}
			}
		});
        addaddrbig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					Intent intent = new Intent(addressseting.this,write_address.class);
					intent.putExtra("setaddrmethod", "write");
					startActivity(intent);
					finish();
				}else{
					Toast.makeText(addressseting.this,"请先开启网络", Toast.LENGTH_SHORT).show();
				}
			}
		});
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
