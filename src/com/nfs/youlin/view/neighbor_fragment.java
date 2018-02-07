package com.nfs.youlin.view;

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

import com.nfs.youlin.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView;

import android.widget.SimpleAdapter;

public class neighbor_fragment extends Fragment {
	private List<Map<String,Object>> list ;
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
    	View view = inflater.inflate(R.layout.neighbor_list3, container, false);
    	ListView list1 = (ListView)view.findViewById(R.id.list1);
    	ListView list2 = (ListView)view.findViewById(R.id.list2);
    	ListView list3 = (ListView)view.findViewById(R.id.list3);
    	final String[] strs1 = new String[] {"101","201","301","401"};
    	final String[] strs2 = new String[] {"102","202","302","402"};
    	final String[] strs3 = new String[] {"103","203"};
    	list = getData(strs1);
        SimpleAdapter simpleadapter = new SimpleAdapter(getActivity(),list,
        		R.layout.neighbor_address,
        		new String[] {"buttonview","text"},new int[]{R.id.nbhead1,R.id.nbname1});
        list1.setAdapter(simpleadapter);
    	list = getData(strs2);
        simpleadapter = new SimpleAdapter(getActivity(),list,
        		R.layout.neighbor_address,
        		new String[] {"buttonview","text"},new int[]{R.id.nbhead1,R.id.nbname1});
        list2.setAdapter(simpleadapter);
    	list = getData(strs3);
        simpleadapter = new SimpleAdapter(getActivity(),list,
        		R.layout.neighbor_address,
        		new String[] {"buttonview","text"},new int[]{R.id.nbhead1,R.id.nbname1});
        list3.setAdapter(simpleadapter);
        return view;  
    } 
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
     //   String[] str = new String[] {"buttonview","text"};

    }
	  private List<Map<String, Object>> getData(String[] strs) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        for (int i = 0; i < strs.length; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("text", strs[i]);    
	            map.put("buttonview", R.drawable.contacts);
	            list.add(map);  
	              
	        }  
	          
	        return list;  
	    } 

}
