package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.find.NewsDetail;
import com.nfs.youlin.activity.neighbor.AllNewshistoryList;
import com.nfs.youlin.adapter.communityserviceadapter.ViewHolder;
import com.nfs.youlin.dao.NewsDaoDBImpl;
import com.nfs.youlin.entity.NewsBlock;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SampleScrollListener;
import com.nfs.youlin.utils.StrToTime;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.gettime;
import com.nfs.youlin.view.NewsListFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private Context context;
	private LayoutInflater mInflater = null;
	private int res;
	private String[] fromstring; 
	private int[] Toelement;
	private int selectID;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	private OtherNewsadapter maddapter;
	private ListView actualListView;
	private PullToRefreshListView mPullRefreshListView;
	ImageLoader imageLoader;
	public NewsListAdapter(Context context, List<Map<String, Object>> list, 
    		int resource, String[] from, int[] to) {
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
		data = list;
		mInflater = (LayoutInflater) context  
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		res = resource;
		Loger.i("youlin","news send list.size()---->"+data.size());
		this.context = context;
		fromstring =from;
        Toelement = to;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		 if (convertView == null) {    
	            holder = new ViewHolder();    
	            convertView = mInflater.inflate(res, null); 
	            holder.firstnewsimg = (ImageButton)convertView.findViewById(Toelement[0]);
	            holder.firstnewstitle = (TextView) convertView.findViewById(Toelement[1]);
	            holder.othersnewslist = (ListView) convertView.findViewById(Toelement[2]);
	            holder.pushtime = (TextView) convertView.findViewById(R.id.newstime);
	            /*******************************************/
//	            try {
//					JSONArray response = new JSONArray(data.get(position).get("othernews").toString());
//					holder.othersnewslist.setTag(response);
//					int responseLen = response.length();
//					NewsDaoDBImpl newsdb = new NewsDaoDBImpl(context);
//					for (int i = 0; i < responseLen; i++) {
//							JSONObject jsonObject = new JSONObject(response.getString(i));
//							String news_title = jsonObject.getString("title");
//							String news_pic = jsonObject.getString("picture");
//							String news_url = jsonObject.getString("url");
//							int news_id = jsonObject.getInt("ID");
//							long news_send_time = jsonObject.getLong("send_time");
//							String news_others = jsonObject.getString("others");
//							NewsBlock news = new NewsBlock(context);
//							news.setfirstflag(0);
//							news.setnewsbelong(App.sFamilyId);
//							news.setnewsid(news_id);
//							news.setnewspic(news_pic);
//							news.setnewssendtime(news_send_time);
//							news.setnewstitle(news_title);
//							news.setnewsurl(news_url);
//							news.setnewsothers(news_others);
//							newsdb.saveObject(news);
//						
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	            /********************************************/
	            convertView.setTag(holder);    
	        } else {    
	            holder = (ViewHolder) convertView.getTag();    
	        } 
//		Picasso.with(context) 
//			.load(data.get(position).get("firsturl").toString()) 
//			.placeholder(R.drawable.plugin_camera_no_pictures) 
//			.error(R.drawable.plugin_camera_no_pictures)
//			.fit()
//			.tag(context)
//			.into(holder.firstnewsimg);
		imageLoader.displayImage(data.get(position).get("firsturl").toString(), holder.firstnewsimg, App.options_news_pic);
		holder.firstnewsimg.setTag(position);
		holder.pushtime.setText(TimeToStr.getTimeToStr(Long.parseLong(data.get(position).get("pushtime").toString())));
		
		holder.firstnewsimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int SelectId = Integer.parseInt(v.getTag().toString()) ;
				Intent newsdetailintent = new Intent(context,NewsDetail.class);
				newsdetailintent.putExtra("linkurl", data.get(SelectId).get("firstlinkurl").toString());
				newsdetailintent.putExtra("picurl", data.get(SelectId).get("firsturl").toString());
				newsdetailintent.putExtra("title", data.get(SelectId).get("firsttitle").toString());
				newsdetailintent.putExtra("newsid", data.get(SelectId).get("firstnewsid").toString());
				newsdetailintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(newsdetailintent);
			}
		});
		holder.firstnewstitle.setText(data.get(position).get("firsttitle").toString());
		
		List<Map<String, Object>> dataothers = new ArrayList<Map<String, Object>>();
		try {
			Loger.d("test5", "othernews JSONArray long="+ new JSONArray(data.get(position).get("othernews").toString()).length());
			getData(dataothers, new JSONArray(data.get(position).get("othernews").toString()));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Loger.d("test5", "dataothers.s="+dataothers.size());
		maddapter = new OtherNewsadapter(context,dataothers,R.layout.othernewsitem,new String[] {"url","title"},new int[]{R.id.newsotherimg,R.id.newsothertitle});
		holder.othersnewslist.setOnScrollListener(new SampleScrollListener(context));
		holder.othersnewslist.setAdapter(maddapter);
		holder.othersnewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Loger.d("test5", ((Map<String, Object>)parent.getAdapter().getItem(position)).get("url").toString());
				Loger.d("test5", ((Map<String, Object>)parent.getAdapter().getItem(position)).get("title").toString());
				Intent newsdetailintent = new Intent(context,NewsDetail.class);
				newsdetailintent.putExtra("linkurl", ((Map<String, Object>)parent.getAdapter().getItem(position)).get("linkurl").toString());
				newsdetailintent.putExtra("picurl", ((Map<String, Object>)parent.getAdapter().getItem(position)).get("url").toString());
				newsdetailintent.putExtra("title", ((Map<String, Object>)parent.getAdapter().getItem(position)).get("title").toString());
				newsdetailintent.putExtra("newsid", ((Map<String, Object>)parent.getAdapter().getItem(position)).get("newsid").toString());
				newsdetailintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(newsdetailintent);
			}
		});
		int height;
		try {
			View item = maddapter.getView(0, null, null);
			item.measure(0, 0);
			height = item.getMeasuredHeight();
			holder.othersnewslist.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height*dataothers.size()+holder.othersnewslist.getDividerHeight()*(dataothers.size()-1)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View layout = inflater.inflate(R.layout.othernewsitem, null);
//		int height = ((RelativeLayout)(layout.findViewById(R.id.newsotherlay))).getHeight();
//		Loger.d("test5", "newsotherimg.h="+height);
		
		/*********************refresh*********************/
//		FragmentManager manager  = ((FragmentActivity)context).getSupportFragmentManager();
//		FragmentTransaction tx = manager.beginTransaction();
//		tx.add(holder.othersnewslist.getId(),new NewsListFragment(data.get(position).get("othernews").toString()));
//		tx.commit();
		return convertView;
	}
    private List<Map<String, Object>> getData(List<Map<String, Object>> newslist,JSONArray httpresponse) {  
	        for (int i = 0; i < httpresponse.length(); i++) {  
	        	Map<String, Object> map = new HashMap<String, Object>();
	            try {
	            	JSONObject jsonObject = new JSONObject(httpresponse.getString(i));
					map.put("url", jsonObject.getString("new_small_pic"));
					map.put("title",jsonObject.getString("new_title"));
					map.put("linkurl",jsonObject.getString("new_url"));
					map.put("newsid",jsonObject.getString("new_id"));
					map.put("time",jsonObject.getString("new_add_time"));
					Loger.d("test5", "测试"+i+"resPath=="+jsonObject.getString("resPath"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            newslist.add(map);         
	        }    
	        return newslist;  
	    }
	class ViewHolder {    
	    public ImageButton firstnewsimg;
	    public TextView firstnewstitle;
	    public ListView othersnewslist;
	    public TextView pushtime;
//	    public FrameLayout othersnewslist;
	}
}
