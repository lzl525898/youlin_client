package com.nfs.youlin.activity.find;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.activity.AlertDialogforblack;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.exceptions.EaseMobException;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.adapter.ContentlistWithHead;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.Person;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StringToPinyinHelper;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.neighbor_chat_fragment;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.jpush.android.api.JPushInterface;

public class NewsSharerangeList extends FragmentActivity implements OnRefreshListener<ListView>{
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	private FragmentTransaction tx;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private List<Object> neighborobjectlist;
	private List<Map<String,Object>> neighborlist  = new ArrayList<Map<String,Object>>();
	private ContentlistWithHead mAdapter;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	public static final int CHATTYPE_SHARE_NEWS = 4;
	private String sharenewslink;
	private String sharenewstitle;
	private String sharenewspic;
	private String sharenewsid;
	private boolean cRequestSet = false;
	private String flag = "none";
	private List<String> blacklist;
	private List<Person> persons = null;  
    private List<Person> newPersons = null; 
    private Pattern p = Pattern.compile("[a-zA-Z]");
	private Matcher m = null;
    private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",  
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",  
            "V", "W", "X", "Y", "Z" }; 
    private PullToRefreshListFragment neighborListView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newssharerangelay);
//		WindowManager m = getWindowManager();
//		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
//		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//		p.height = (int) (d.getHeight() * 0.8);
//		 //高度设置为屏幕的1.0
//		p.width = (int) (d.getWidth() * 0.9); //宽度设置为屏幕的0.8
//		p.x = 0;
//		p.y = 0;
//		getWindow().setAttributes(p);
		Intent intent = getIntent();
		sharenewstitle = intent.getStringExtra("newstitle");
		sharenewspic = intent.getStringExtra("newspic");
		sharenewslink = intent.getStringExtra("newslink");
		sharenewsid = intent.getStringExtra("newsid");
		aNeighborDaoDBImpl = new NeighborDaoDBImpl(this);
		neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId,App.sUserLoginId);	
		final AccountDaoDBImpl account = new AccountDaoDBImpl(this);
		/*********************refresh*********************/
		FragmentManager manager  = this.getSupportFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		neighborListView = new PullToRefreshListFragment();
		tx.replace(R.id.contentnewsshare, neighborListView ,"neighborfragment");
		tx.commit();
		blacklist = EMContactManager.getInstance().getBlackListUsernames();
		new Thread(new Runnable() {
			public void run() {
				try {
					while (NewsSharerangeList.this.getSupportFragmentManager().findFragmentByTag(
							"neighborfragment") == null) {
					}
					NewsSharerangeList.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub

							final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) NewsSharerangeList.this.getSupportFragmentManager()
										.findFragmentByTag("neighborfragment");
							try {
								mPullRefreshListView = finalfrag9.getPullToRefreshListView();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
							mPullRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);
							mPullRefreshListView.setOnRefreshListener(NewsSharerangeList.this);
							actualListView = mPullRefreshListView.getRefreshableView();
							neighborlist.clear();
							getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
							mAdapter = new ContentlistWithHead(
									NewsSharerangeList.this,
									neighborlist,
									R.layout.neighbor_detail,
									new String[] { "buttonview", "name","profession", "detail" },
									new int[] { R.id.neighbor_head,
											R.id.nerghbor_name,
											R.id.nerghbor_profession,
											R.id.nerghbor_detail});
							actualListView.setAdapter(mAdapter);
							actualListView.setDivider(getResources().getDrawable(R.drawable.fengexian));
							actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//							actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//								@Override
//								public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
//										long id) {
//									// TODO Auto-generated method stub
//									Intent intent = new Intent(NewsSharerangeList.this, AlertDialogforblack.class);
//									intent.putExtra("msg", "");
//									intent.putExtra("cancel", true);
//									intent.putExtra("position", position-1);
//									intent.putExtra("currentuser",String.valueOf(App.sUserLoginId));
//									NewsSharerangeList.this.startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
//									return true;
//								}
//							});
							actualListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(
										AdapterView<?> arg0, View arg1,
										int position, long id) {
									// TODO Auto-generated method stub
									Loger.d("test3","actualListView position"+position);
									gethttpdata(App.sUserLoginId, Long.parseLong(neighborlist.get(position-1).get("userid").toString()), 
											Integer.parseInt(sharenewsid), App.sFamilyCommunityId, IHttpRequestUtils.YOULIN);
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// TODO Auto-generated method stub
												Long currenttime = System.currentTimeMillis();
												while (!cRequestSet) {
													if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME+5000)) {
														cRequestSet = true;
													}
												}
											return null;
										}
										protected void onPostExecute(Void result) {
											super.onPostExecute(result);
											if(cRequestSet && flag.equals("ok")){
												finish();
											}
										};
									}.execute();
								}
							});
							finalfrag9.setListShown(true);
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private List<Map<String, Object>> getDatawithrank(List<Map<String, Object>> neighborlist,List<Object> objectneighbors,int getcount) {  
    	
    	persons = new ArrayList<Person>();
        for (int i = 0; i < getcount; i++) {  
            String personName = ((Neighbor)(objectneighbors.get(i))).getUser_name();
            Person person = new Person(personName,i);
            persons.add(person);    
        }  
        sortList(sortIndex(persons), neighborlist, objectneighbors);
        return neighborlist;  
    }
	
	public String[] sortIndex(List<Person> persons) {
		TreeSet<String> set = new TreeSet<String>();  
        for (Person person : persons) {  
        	String tmp = StringToPinyinHelper.getPinYinHeadChar(person.getName()).substring(0, 1);
        	m = p.matcher(tmp); 
        	if(m.matches()){
        		set.add(tmp);  
        	}else{
        		set.add("#");  
        	}
        }  
        String[] names = new String[persons.size() + set.size()];  
        int i = 0;  
        for (String string : set) {
            names[i] = string;  
            i++;  
        } 
		String[] pinYinNames = new String[persons.size()];
		for (int j = 0; j < persons.size(); j++) {
			persons.get(j).setPinYinName(StringToPinyinHelper.getPingYin(persons.get(j).getName().toString()));
			pinYinNames[j] = StringToPinyinHelper.getPingYin(persons.get(j).getName().toString());
		}
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);  
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}
	
	private List<Map<String, Object>> sortList(String[] allNames,List<Map<String, Object>> neighborlist,List<Object> objectneighbors) {
//		Loger.i("TEST", "allNames size==>"+allNames.length);
		
		newPersons = new ArrayList<Person>();
		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < allNames.length; i++) {
			if (allNames[i].length() != 1) {
				for (int j = 0; j < persons.size(); j++) {
					if (allNames[i].equals(persons.get(j).getPinYinName())) {
						if (!indexList.contains(persons.get(j).getPersonindex())) {
							indexList.add(persons.get(j).getPersonindex());
							Person p = new Person(persons.get(j).getName(), persons.get(j).getPinYinName(),
									persons.get(j).getPersonindex());
							newPersons.add(p);
						}
					}
				}
			}
			else{
				newPersons.add(new Person(allNames[i]));  
			}
		}
		if(NeighborActivity.indexSelector!=null){
		NeighborActivity.indexSelector.clear();
        for (int j = 0; j < indexStr.length; j++) {
            for (int i = 0; i < newPersons.size(); i++) {  
                try {
					if (newPersons.get(i).getName().equals(indexStr[j])) {  
						NeighborActivity.indexSelector.put(indexStr[j], i);  
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }
        } 
		}
        neighborlist.clear();
        for (int i = 0; i < newPersons.size(); i++) {
        	int index = newPersons.get(i).getPersonindex();
        	if(-1 == index){
        		neighborlist.add(setNeighborIndexWithMap(newPersons.get(i).getName()));
        	}else{
        		Neighbor neighbor = (Neighbor)(objectneighbors.get(index));
        		neighborlist.add(setNeighborsWithMap(neighbor));
        	}
		}

        return neighborlist;
    }
	
	private Map<String, Object> setNeighborsWithMap(Neighbor neighbor){
    	Map<String, Object> map = new HashMap<String, Object>();  
        map.put("name", neighbor.getUser_name());  
        map.put("detail",neighbor.getBuilding_num()+"-"+neighbor.getAptnum());
        map.put("userid",neighbor.getUser_id());
        map.put("buttonview", neighbor.getUser_portrait());
        map.put("describe", neighbor.getBriefdesc());
        map.put("status", neighbor.getAddrstatus());
        map.put("profession", neighbor.getProfession());
        map.put("blackflag", 0);
        for(int j=0;j<blacklist.size();j++){
        	if(neighbor.getUser_id() == Long.parseLong(blacklist.get(j))){
        		map.put("blackflag", 1);
        		break;
        	}
        }
        return map;
    }
	
	private Map<String, Object> setNeighborIndexWithMap(String indexString){
    	Map<String, Object> map = new HashMap<String, Object>();  
    	map.put("name", indexString);  
    	return map;
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(PinyinActivity.sPinyinStatusBoolean == true){
			if(PinyinActivity.selectIndex==100){
				neighborListView.setSelection(neighborlist.size()-1);  
			}else{
				neighborListView.setSelection(PinyinActivity.selectIndex);
			}
			PinyinActivity.sPinyinStatusBoolean = false;
		}else{
			Loger.i("TEST", "没有刷新数ListView");
		}
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}
	
    private List<Map<String, Object>> getData(List<Map<String, Object>> neighborlist,List<Object> objectneighbors,int getcount) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
  		
	        for (int i = 0; i < getcount; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("name", ((Neighbor)(objectneighbors.get(i))).getUser_name());  
	            map.put("detail",((Neighbor)(objectneighbors.get(i))).getBuilding_num()+"-"+((Neighbor)(objectneighbors.get(i))).getAptnum());
	            map.put("userid",((Neighbor)(objectneighbors.get(i))).getUser_id());
	            map.put("buttonview", ((Neighbor)(objectneighbors.get(i))).getUser_portrait());
	            map.put("describe", ((Neighbor)(objectneighbors.get(i))).getBriefdesc());
	            map.put("status", ((Neighbor)(objectneighbors.get(i))).getAddrstatus());
	            Loger.i("test3", "Neighbor=>"+((Neighbor)(objectneighbors.get(i))).getProfession());
	            map.put("profession", ((Neighbor)(objectneighbors.get(i))).getProfession());
	            map.put("blackflag", 0);
	            for(int j=0;j<blacklist.size();j++){
	            	if(((Neighbor)(objectneighbors.get(i))).getUser_id() == Long.parseLong(blacklist.get(j))){
	            		map.put("blackflag", 1);
	            		break;
	            	}
	            }
	            neighborlist.add(map);         
	        }    
	        return neighborlist;  
	    }
	private void gethttpdata(long sender_id,long recipy_id ,int news_id,long community_id,String http_addr){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
					params.put("sender_id", sender_id);
					params.put("recipy_id", recipy_id);
					params.put("news_id", news_id);
					params.put("community_id", community_id);
					params.put("tag", "newshare");
					params.put("apitype", IHttpRequestUtils.APITYPE[5]);
					Loger.d("test3", "news_id="+news_id+"community_id="+community_id+"sender_id="+sender_id+"recipy_id="+recipy_id);
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				Loger.i("test3", "share news youlin ->" + jsonContext);
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set currentaddress flag->" + flag);
					if(flag.equals("ok")){
						Toast.makeText(NewsSharerangeList.this, "分享成功！", Toast.LENGTH_SHORT).show();
						cRequestSet = true;
					}else if(flag.equals("black")){
						Toast.makeText(NewsSharerangeList.this, "该用户已将您加入黑名单！", Toast.LENGTH_SHORT).show();
						cRequestSet = false;
					}else if(flag.equals("push_error")){
						Toast.makeText(NewsSharerangeList.this, "分享失败！", Toast.LENGTH_SHORT).show();
						cRequestSet = false;
					}else{
						cRequestSet = false;
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					cRequestSet = false;
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					Loger.i("test3",responseString + "\r\n" + throwable.toString()+"\r\n-----\r\n" + statusCode);
					cRequestSet = false;
					new ErrorServer(NewsSharerangeList.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if (requestCode == ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				Loger.d("test4", "REQUEST_CODE_ADD_TO_BLACKLIST position="+neighborlist.get(data.getIntExtra("position", -1)).get("userid").toString()+"---"+data.getIntExtra("position", -1));
				addUserToBlacklist(neighborlist.get(data.getIntExtra("position", -1)).get("userid").toString());
				gethttpdata(App.sUserLoginId,neighborlist.get(data.getIntExtra("position", -1)).get("userid").toString(), 1, IHttpRequestUtils.YOULIN);
			}
		}
		else if(resultCode == AlertDialogforblack.REMOVE_BLACK_USER){
			if (requestCode == ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST) { // 移出黑名单
				
				Loger.d("test4", "REMOVE_TO_BLACKLIST position="+data.getStringExtra("userid"));
				neighborlist.clear();
				blacklist = EMContactManager.getInstance().getBlackListUsernames();
				getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	private void gethttpdata(long user_id,String black_uesr_id,int action_id,String http_addr){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
			params.put("user_id", user_id);
			params.put("black_user_id", black_uesr_id);
			params.put("action_id", action_id);
			params.put("tag", "blacklist");
			params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		flag ="";
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				String flag = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set black flag->" + flag);
					
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
			
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					Loger.i("test3",
							responseString + "\r\n" + throwable.toString()
							+"\r\n-----\r\n" + statusCode);
					new ErrorServer(NewsSharerangeList.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	/**
	 * 加入到黑名单
	 * 
	 * @param username
	 */
	private void addUserToBlacklist(final String username) {
		final ProgressDialog pd = new ProgressDialog(NewsSharerangeList.this);
		pd.setMessage(getString(R.string.Is_moved_into_blacklist));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().addUserToBlackList(username, false);
					NewsSharerangeList.this.runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(NewsSharerangeList.this, R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
							neighborlist.clear();
							blacklist = EMContactManager.getInstance().getBlackListUsernames();
							Loger.d("test4", "REQUEST_CODE_ADD_TO_BLACKLIST blacklist long="+blacklist.size());
							getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
							mAdapter.notifyDataSetChanged();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					NewsSharerangeList.this.runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(NewsSharerangeList.this, R.string.Move_into_blacklist_failure, 0).show();
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
}
