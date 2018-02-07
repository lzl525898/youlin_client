package com.nfs.youlin.activity.personal;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.jpush.android.api.JPushInterface;

import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.personal.moreaddrdetail;
import com.nfs.youlin.adapter.AddressShowAdapter;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.UserAddressHelper;
import com.nfs.youlin.view.SwitchView;
import com.nfs.youlin.view.SwitchView.OnStateChangedListener;
import com.umeng.analytics.MobclickAgent;
import com.nfs.youlin.R;
public class addressshow extends Activity implements StatusChangeListener{
	private List<Map<String, Object>> list;
	private static final int GET_MOREADDRESS_DETAIL = 3;
	private static final int GET_ADDRESS_RETURN=4;
	//private static final int CHANGE_ADDRESS_REQUEST =5;
	public ListView addresslist;
	//public static final String[] familyId = new String[] {"","","",""};
	public final List<String> familyIdlist= new ArrayList<String>();
	public final List<String> familySubIdlist= new ArrayList<String>();
	public final List<Integer> familyEnType= new ArrayList<Integer>();
	public final List<Long> familyRecordIdList = new ArrayList<Long>();
	private int currentposition=-1 ;
	private StatusChangeutils statusutils;
	//UserAddressHelper dbHelper;
	 @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.setContentView(R.layout.addressshow);
	        AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(addressshow.this);
	        List<Object> allFamilyList =  allFamilyDaoDBImpl.findPointTypeObject(App.sUserLoginId);
	        statusutils = new StatusChangeutils();
			statusutils.statuschangelistener("ADDRVERIFY",this);
	       // dbHelper = new UserAddressHelper(this, "useraddress.db3", 1);
	        //ListView userlist = (ListView) findViewById(R.id.userimage);
	        addresslist = (ListView) findViewById(R.id.addrshow);
	        Button addressshowback = (Button)findViewById(R.id.backbutton);
	        Button addaddress = (Button)findViewById(R.id.addaddress);
//	        Drawable drawable1 = getResources().getDrawable(R.drawable.nav_fanhui_xin_tiao);
//	        drawable1.setBounds(0, 1, 28, 46);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//	        addressshowback.setCompoundDrawables(drawable1, null, null, null);//只放左边

	        addaddress.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
//					Intent intent = new Intent(addressshow.this,write_address.class);
//					intent.putExtra("setaddrmethod", "add");
					/******************database***********************/
					AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(addressshow.this);
					final List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
					/******************database***********************/
					if(NetworkService.networkBool){
						Intent intent = new Intent(addressshow.this,write_address.class);
						if(familyObjs.size()>0)
							intent.putExtra("setaddrmethod", "add");
						else
							intent.putExtra("setaddrmethod", "write");
							startActivityForResult(intent, GET_ADDRESS_RETURN);
					}else{
						Toast.makeText(addressshow.this,"请先开启网络", Toast.LENGTH_SHORT).show();
					}
				}
			});
	        addressshowback.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CommonTools.goBack();
				}
			});
			 List<String> addrlist= new ArrayList<String>();
			 List<String> checklist= new ArrayList<String>();
			 familyIdlist.clear();
			 familySubIdlist.clear();
			 familyEnType.clear();
			 familyRecordIdList.clear();
			 checklist.clear();
			 /******************database***********************/
				AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(addressshow.this);
				List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
				/******************database***********************/
			for(int i=0;i<familyObjs.size();i++){
				Loger.d("hyytest", " long familyId[i]="+((AllFamily)familyObjs.get(i)).getFamily_id());
				Loger.d("hyytest", " long familysubId[i]="+((AllFamily)familyObjs.get(i)).getNe_status());
				familyIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getFamily_id()));
				familySubIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getNe_status()));
				familyEnType.add(Integer.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));	
				familyRecordIdList.add(((AllFamily)familyObjs.get(i)).getFamily_address_id());
				Loger.i("TEST","familyRecordIdList["+i+"]==>"+((AllFamily)familyObjs.get(i)).getFamily_address_id());
				Loger.d("hyytest", "familyId[i]="+familyIdlist.get(i));
				addrlist.add(((AllFamily)familyObjs.get(i)).getFamily_address());
				checklist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));
				if( String.valueOf(((AllFamily)familyObjs.get(i)).getPrimary_flag()).equals("1") ){
					currentposition = i;
				}
			}
			
			list = getData(addrlist.toArray(new String[addrlist.size()]), checklist.toArray(new String[checklist.size()]),currentposition);
			AddressShowAdapter simpleadapter1 = new AddressShowAdapter(this, list,
					R.layout.addresslist_detail, new String[] { "name","check"},
					new int[] {R.id.address_detail});
//			SimpleAdapter simpleadapter1 = new SimpleAdapter(this, list,
//					R.layout.addresslist_detail, new String[] { "name","check",  "head" },
//					new int[] {   R.id.address_detail,R.id.address_dcheck,R.id.address_head });
			addresslist.setAdapter(simpleadapter1);
			addresslist.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					// TODO Auto-generated method stub
					Loger.d("test4","address list on click position="+position);
					Intent intent = new Intent(addressshow.this,moreaddrdetail.class);
					intent.putExtra("addressitemindex", position);
					intent.putExtra("familyId", familyIdlist.get(position));
					intent.putExtra("familySubId", familySubIdlist.get(position));
					intent.putExtra("familyEnType", familyEnType.get(position));
					intent.putExtra("familyRecordId", familyRecordIdList.get(position));
					startActivityForResult(intent, GET_MOREADDRESS_DETAIL);
				}
			});
	 }
	  private List<Map<String, Object>> getData(String[] strs,String[] strs2,int current) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        for (int i = 0; i < strs.length&&strs[i].length()>3; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("name", strs[i]); 
	            map.put("check", strs2[i]);
	            if(i==current){
	            	map.put("current", "1");
	            }else{
	            	map.put("current", "0");
	            }
	            
	            list.add(map);  
	              
	        }  
	          
	        return list;  
	    } 
	  public String loadAddrdetailPrefrence() {
			SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
			return sharedata.getString("detail", "未设置");
		}
	  /**
		 * onActivityResult
		 */
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) { // 清空消息
				if (requestCode == GET_MOREADDRESS_DETAIL) {
					 List<String> addrlist= new ArrayList<String>();
					 List<String> checklist= new ArrayList<String>();
					 Loger.d("hyytest","22222222222222");
					 familyIdlist.clear();
					 familySubIdlist.clear();
					 familyEnType.clear();
					 familyRecordIdList.clear();
					 checklist.clear();
					 currentposition = -1;
					/******************database***********************/
					AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(addressshow.this);
					List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
					/******************database***********************/
					for(int i=0;i<familyObjs.size();i++){
						Loger.d("hyytest", " long familyId[i]="+((AllFamily)familyObjs.get(i)).getFamily_id());
						familyIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getFamily_id()));
						familySubIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getNe_status()));
						familyEnType.add(Integer.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));		
						familyRecordIdList.add(((AllFamily)familyObjs.get(i)).getFamily_address_id());
						Loger.d("hyytest", "familyId[i]="+familyIdlist.get(i));
						addrlist.add(((AllFamily)familyObjs.get(i)).getFamily_address());
						checklist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));
						if( String.valueOf(((AllFamily)familyObjs.get(i)).getPrimary_flag()).equals("1") ){
							currentposition = i;
						}
					}
					Loger.d("test5", "address show set currentposition="+currentposition);
					list = getData(addrlist.toArray(new String[addrlist.size()]), checklist.toArray(new String[checklist.size()]),currentposition);
					AddressShowAdapter simpleadapter1 = new AddressShowAdapter(this, list,
							R.layout.addresslist_detail, new String[] { "name","check"},
							new int[] {R.id.address_detail});
					addresslist.setAdapter(simpleadapter1);
				}
				if(requestCode == GET_ADDRESS_RETURN){
					 List<String> addrlist= new ArrayList<String>();
					 List<String> checklist= new ArrayList<String>();
					 Loger.d("hyytest","22222222222222");
					 familyIdlist.clear();
					 familySubIdlist.clear();
					 familyEnType.clear();
					 familyRecordIdList.clear();
					 checklist.clear();
					 currentposition = -1;
					/******************database***********************/
					AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(addressshow.this);
					List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
					/******************database***********************/
					for(int i=0;i<familyObjs.size();i++){
						Loger.d("hyytest", " long familyId[i]="+((AllFamily)familyObjs.get(i)).getFamily_id());
						familyIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getFamily_id()));
						familySubIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getNe_status()));
						familyEnType.add(Integer.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));		
						familyRecordIdList.add(((AllFamily)familyObjs.get(i)).getFamily_address_id());
						Loger.d("hyytest", "familyId[i]="+familyIdlist.get(i));
						addrlist.add(((AllFamily)familyObjs.get(i)).getFamily_address());
						checklist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));
						if( String.valueOf(((AllFamily)familyObjs.get(i)).getPrimary_flag()).equals("1") ){
							currentposition = i;
						}
					}
					list = getData(addrlist.toArray(new String[addrlist.size()]), checklist.toArray(new String[checklist.size()]),currentposition);
					AddressShowAdapter simpleadapter1 = new AddressShowAdapter(this, list,
							R.layout.addresslist_detail, new String[] { "name","check"},
							new int[] {R.id.address_detail});
					addresslist.setAdapter(simpleadapter1);				
				}
			}
			if (resultCode == RESULT_FIRST_USER){
				if (requestCode == GET_MOREADDRESS_DETAIL) {

					String familyId = data.getStringExtra("familyId");
					String familySubId = data.getStringExtra("familySubId");
					//int position = data.getIntExtra("position", 9);
					//Loger.d("hyytest", "resultCode == RESULT_FIRST_USER position="+position);
					Loger.d("hyytest", "resultCode == RESULT_FIRST_USER familyId="+familyId+"familySubId="+familySubId);
					Intent intentwrite = new Intent(addressshow.this,write_address.class);
					intentwrite.putExtra("setaddrmethod", "change");
					intentwrite.putExtra("addressitemindex", familyId);
					intentwrite.putExtra("addresssubitemindex", familySubId);
					startActivityForResult(intentwrite, GET_ADDRESS_RETURN);
				}
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
		
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy(); 
		}
		
		@Override
		public void setstatuschanged(int status) {
			// TODO Auto-generated method stub
			 List<String> addrlist= new ArrayList<String>();
			 List<String> checklist= new ArrayList<String>();
			 Loger.d("hyytest","22222222222222");
			 familyIdlist.clear();
			 familySubIdlist.clear();
			 familyEnType.clear();
			 familyRecordIdList.clear();
			 checklist.clear();
			 currentposition = -1;
			/******************database***********************/
			AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(addressshow.this);
			List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
			/******************database***********************/
			for(int i=0;i<familyObjs.size();i++){
				Loger.d("hyytest", " long familyId[i]="+((AllFamily)familyObjs.get(i)).getFamily_id());
				familyIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getFamily_id()));
				familySubIdlist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getNe_status()));
				familyEnType.add(Integer.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));		
				familyRecordIdList.add(((AllFamily)familyObjs.get(i)).getFamily_address_id());
				Loger.d("hyytest", "familyId[i]="+familyIdlist.get(i));
				addrlist.add(((AllFamily)familyObjs.get(i)).getFamily_address());
				checklist.add(String.valueOf(((AllFamily)familyObjs.get(i)).getEntity_type()));
				if( String.valueOf(((AllFamily)familyObjs.get(i)).getPrimary_flag()).equals("1") ){
					currentposition = i;
				}
			}
			Loger.d("test5", "address show set currentposition="+currentposition);
			list = getData(addrlist.toArray(new String[addrlist.size()]), checklist.toArray(new String[checklist.size()]),currentposition);
			AddressShowAdapter simpleadapter1 = new AddressShowAdapter(this, list,
					R.layout.addresslist_detail, new String[] { "name","check"},
					new int[] {R.id.address_detail});
			addresslist.setAdapter(simpleadapter1);
		}
		@Override
		public void setstatuschanged(int status, Bundle data) {
			// TODO Auto-generated method stub
			
		}
}
