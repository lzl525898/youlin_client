package com.nfs.youlin.view;

import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.adapter.OtherNewsadapter;
import com.nfs.youlin.utils.Loger;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NewsListFragment extends Fragment{
		ListView newslist;
		private OtherNewsadapter maddapter;
		private List<Map<String, Object>> dataothers = new ArrayList<Map<String, Object>>();
		public NewsListFragment(String json) {
			// TODO Auto-generated constructor stub
			try {
				Loger.d("test5", "othernews JSONArray long="+ new JSONArray(json).length());
				getData(dataothers, new JSONArray(json));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	// TODO Auto-generated method stub
	    	View view = inflater.inflate(R.layout.newsotherslist, container, false);
	    	newslist = (ListView)view.findViewById(R.id.newsforothers);
	    	maddapter = new OtherNewsadapter(getActivity(),dataothers,R.layout.othernewsitem,new String[] {"url","title"},new int[]{R.id.newsotherimg,R.id.newsothertitle});
	    	newslist.setAdapter(maddapter);
	    	return view;
	    }
	    private List<Map<String, Object>> getData(List<Map<String, Object>> newslist,JSONArray httpresponse) {  
	    	newslist.clear();
		        for (int i = 0; i < httpresponse.length(); i++) {  
		        	Map<String, Object> map = new HashMap<String, Object>();
		            try {
		            	JSONObject jsonObject = new JSONObject(httpresponse.getString(i));
						map.put("url", jsonObject.getString("resPath"));
						Loger.d("test5", "测试"+i+"resPath=="+jsonObject.getString("resPath"));
						map.put("title","测试"+i);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            newslist.add(map);         
		        }    
		        return newslist;  
		    }
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    // TODO Auto-generated method stub
	    super.onCreate(savedInstanceState);
	    }
}
