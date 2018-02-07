package com.nfs.youlin.view;

import com.nfs.youlin.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class addandreduceedit extends LinearLayout {

	private EditText mEditText;
	private ImageButton bAdd;
	private ImageButton bReduce;

	// 这里的构造一定是两个参数。
	public addandreduceedit(final Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();

		LayoutInflater.from(getContext()).inflate(R.layout.myedittext, this);
		init_widget();
		addListener();

	}

	public void init_widget() {

		mEditText = (EditText) findViewById(R.id.et01);
		bAdd = (ImageButton) findViewById(R.id.bt02);
		bReduce = (ImageButton) findViewById(R.id.bt01);
		mEditText.setText("1");
		mEditText.addTextChangedListener(new TextWatcher() {
			int l=0;////////记录字符串被删除字符之前，字符串的长度
	 		int location=0;//记录光标的位置
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				l=arg0.length();
    		    location=mEditText.getSelectionStart();
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(arg0.toString().length()==0 && l>0){
					mEditText.removeTextChangedListener(this);
					mEditText.setText("0");
					mEditText.setSelection(mEditText.getText().toString().length());
					mEditText.addTextChangedListener(this);
				}else if(Integer.parseInt(arg0.toString())>=60000){
					mEditText.removeTextChangedListener(this);
					mEditText.setText("60000");
					mEditText.setSelection(mEditText.getText().toString().length());
					Toast.makeText(getContext(), "报名人数已到上限", Toast.LENGTH_SHORT).show();
					mEditText.addTextChangedListener(this);
				}
			}
		});
	}

	public void addListener() {
		bAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				int num = Integer.valueOf(mEditText.getText().toString());
				num++;
				mEditText.setText(Integer.toString(num));
			}
		});

		bReduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int num = Integer.valueOf(mEditText.getText().toString());
				num--;
				if(num<0){
					num = 0;
				}
				mEditText.setText(Integer.toString(num));
			}
		});
	}
}
