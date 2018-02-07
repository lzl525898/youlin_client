package com.nfs.youlin.activity.find;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Coml_District_activity extends Activity implements OnClickListener{
	private ActionBar actionBar;
	private ImageView commsearch;
	private EditText commedit;
	private RelativeLayout commbarlay;
	private List<ImageView> viewmap = new ArrayList<ImageView>();
	
	private String searchitem = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coml_district_lay);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("商圈");
		viewmap.add((ImageView) findViewById(R.id.meishiview));
		viewmap.add((ImageView) findViewById(R.id.yuleview));
		viewmap.add((ImageView) findViewById(R.id.gouwuview));
		viewmap.add((ImageView) findViewById(R.id.jianshenview));
		viewmap.add((ImageView) findViewById(R.id.yinhangview));
		viewmap.add((ImageView) findViewById(R.id.qitaview));

		for(int i =0;i<viewmap.size();i++){
			viewmap.get(i).setOnClickListener(this);
			viewmap.get(i).setTag(i);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.newcommbar, menu);
		LinearLayout SearchGroup = (LinearLayout) menu.findItem(R.id.commactionbar).getActionView();
		commsearch = (ImageView)SearchGroup.findViewById(R.id.commsearch);
		commedit = (EditText)SearchGroup.findViewById(R.id.commedit);
		commbarlay = (RelativeLayout)SearchGroup.findViewById(R.id.commbarlay);
		
		commsearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(commsearch.getTag().equals("0")){
					commedit.setVisibility(View.VISIBLE);
					commsearch.setTag("1");
					commbarlay.setBackgroundResource(R.drawable.comm_search_normal);
					commsearch.setImageResource(R.drawable.sousuohuang);
				}else{
//					commsearch.setTag("0");
//					commbarlay.setBackgroundColor(0xffffba02);
					if(searchitem.length()<1){
						Toast.makeText(Coml_District_activity.this, "请输入正确的搜索关键字", Toast.LENGTH_SHORT).show();
					}else{
						
					}
				}
			}
		});
		commedit.addTextChangedListener(new TextWatcher() {
			int l=0;////////记录字符串被删除字符之前，字符串的长度
	  		int location=0;//记录光标的位置
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				 l=s.length();
	     		 location=commedit.getSelectionStart();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				searchitem = s.toString();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		commedit.setVisibility(View.INVISIBLE);
		commsearch.setTag("0");
		commbarlay.setBackgroundColor(0xffffba02);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.meishiview){
			Intent intent = new Intent(this,coml_everyitem_list.class);
			intent.putExtra("tag", v.getTag().toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
	}
}
