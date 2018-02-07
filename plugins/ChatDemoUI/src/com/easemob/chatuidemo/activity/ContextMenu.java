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
package com.easemob.chatuidemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;
import com.umeng.analytics.MobclickAgent;

public class ContextMenu extends BaseActivity {

	private int position;
	private String filePath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int txtValue = EMMessage.Type.TXT.ordinal();
		int type = getIntent().getIntExtra("type", -1);
		filePath=getIntent().getStringExtra("file_path");
		if (type == EMMessage.Type.TXT.ordinal()) {
		    setContentView(R.layout.context_menu_for_text);
		} else if (type == EMMessage.Type.LOCATION.ordinal()) {
		    setContentView(R.layout.context_menu_for_location);
		} else if (type == EMMessage.Type.IMAGE.ordinal()) {
		    setContentView(R.layout.context_menu_for_image);
		} else if (type == EMMessage.Type.VOICE.ordinal()) {
		    setContentView(R.layout.context_menu_for_voice);
		} else if (type == EMMessage.Type.VIDEO.ordinal()) {
			setContentView(R.layout.context_menu_for_video);
		} else if(type == EMMessage.Type.FILE.ordinal()){
		    setContentView(R.layout.context_menu_for_file);
		}
		    
		/*    
		switch (getIntent().getIntExtra("type", -1)) {
		case txtValue:
			setContentView(R.layout.context_menu_for_text);
			break;
		case EMMessage.Type.LOCATION.ordinal():
			setContentView(R.layout.context_menu_for_location);
			break;
		case EMMessage.Type.IMAGE.ordinal():
			setContentView(R.layout.context_menu_for_image);
			break;
		case EMMessage.Type.VOICE.ordinal():
			setContentView(R.layout.context_menu_for_voice);
			break;
			//need to support netdisk and send netsdk?
		case Message.TYPE_NETDISK:
		    setContentView(R.layout.context_menu_for_netdisk);
		    break;
		case Message.TYPE_SENT_NETDISK:
		    setContentView(R.layout.context_menu_for_sent_netdisk);
		    break;
		default:
			break;
		}
		*/
		position = getIntent().getIntExtra("position", -1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void copy(View view){
		setResult(ChatActivity.RESULT_CODE_COPY, new Intent().putExtra("position", position));
		finish();
	}
	public void delete(View view){
		setResult(ChatActivity.RESULT_CODE_DELETE, new Intent().putExtra("position", position));
		finish();
	}
	public void forward(View view){
		setResult(ChatActivity.RESULT_CODE_FORWARD, new Intent().putExtra("position", position));
		finish();
	}
	
	public void open(View v){
	    setResult(ChatActivity.RESULT_CODE_OPEN, new Intent().putExtra("position", position));
        finish();
	}
	public void download(View v){
	    setResult(ChatActivity.RESULT_CODE_DWONLOAD, new Intent().putExtra("position", position));
        finish();
	}
	public void toCloud(View v){
	    setResult(ChatActivity.RESULT_CODE_TO_CLOUD, new Intent().putExtra("position", position));
        finish();
	}
	public void file(View v){
        setResult(ChatActivity.RESULT_CODE_FILE, new Intent().putExtra("position", position));
        //Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        //File file=new File(filePath);
        new AlertDialog.Builder(ContextMenu.this).setMessage(filePath).setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                finish();
            }
        }).create().show();
        //Toast.makeText(ContextMenu.this, filePath, 1).show();
        //intent.setDataAndType(Uri.fromFile(file.getParentFile()), "file/*");
        //startActivity(intent);
        //finish();
    }
	@Override
	protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
	    // TODO Auto-generated method stub
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}
