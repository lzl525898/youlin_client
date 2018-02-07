package com.nfs.youlin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;  
import java.util.HashMap;  
  
import android.app.ListActivity;  
import android.os.Bundle;  
import android.view.View;  
import android.widget.ListView;  
import android.widget.SimpleAdapter;  
import android.widget.Toast; 
import android.content.Intent;
import com.nfs.youlin.R;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class neighbor_list extends ListFragment {
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
    	View view = inflater.inflate(R.layout.fragnebt_list, container, false);
//    	ImageView buttonview = (ImageView)view.findViewById(R.id.btimg);
//    	TextView buttontext = (TextView)view.findViewById(R.id.bttext);
//    	buttonview.setImageResource(R.drawable.head2);
//    	buttontext.setText("何永毅");
        return view;  
    } 
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
     //   String[] str = new String[] {"buttonview","text"};
        String[] strs = new String[] {"何永毅","刘彦明","梁泽雷"};
        String[] strdetail = new String[] {"9栋 4单元 302","9栋 4单元 302","9栋 4单元 302"};
        
        SimpleAdapter simpleadapter = new SimpleAdapter(getActivity(),getData(strs,strdetail),
        		R.layout.neighbor_detail,
        		new String[] {"buttonview","name","detail"},new int[]{R.id.neighbor_head,R.id.nerghbor_name,R.id.nerghbor_detail});
        setListAdapter(simpleadapter);
    }
    private List<Map<String, Object>> getData(String[] strs,String[] strsdetail) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        for (int i = 0; i < strs.length; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("name", strs[i]);  
	            map.put("detail",strsdetail[i]);
	            map.put("buttonview", R.drawable.contacts);
	            list.add(map);  
	              
	        }  
	          
	        return list;  
	    } 
    @Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(getActivity(), com.easemob.chatuidemo.activity.ChatActivity.class);
        intent.putExtra("userId", "hyy01");
        intent.putExtra("chatType", CHATTYPE_SINGLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}

