package com.nfs.youlin.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONObject;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.AlertDialog;
import com.easemob.chatuidemo.activity.AlertDialogforblack;
import com.easemob.chatuidemo.activity.AlertDialogforclear;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.adapter.ChatAllHistoryAdapter;
import com.easemob.exceptions.EaseMobException;
import com.nfs.youlin.R;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class neighbor_chat_fragment extends Fragment implements EMEventListener{
	private ListView newMsgInfoListView;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private ChatAllHistoryAdapter adapter;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private List<Object> neighborobjectlist;
	private List<String> conversationusernameList = new ArrayList<String>();
	private List<String> conversationusernickList = new ArrayList<String>();
	private List<String> conversationuserheadimgList = new ArrayList<String>();
	private int CHATDETAIL = 4001;
	private boolean bRequestSet = false;
	private String flag = "none";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_msginfo, container, false);
		this.newMsgInfoListView = (ListView) view.findViewById(R.id.my_listview);
		conversationList.clear();
		conversationusernameList.clear();
		conversationusernickList.clear();
		conversationuserheadimgList.clear();
//		conversationList.addAll(loadConversationsWithRecentChat());
		aNeighborDaoDBImpl = new NeighborDaoDBImpl(getActivity());
		final AccountDaoDBImpl account = new AccountDaoDBImpl(getActivity());
		//neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId);   //得到某个地址的全部邻居
		for(EMConversation conversation:loadConversationsWithRecentChat()){
			if(!conversation.getUserName().equals("xitongxinxi")){
				String userid = conversation.getUserName();
				neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyandUserObject(Long.parseLong(userid));
				if(neighborobjectlist.size()>0){
					conversationusernameList.add(userid);				
					conversationusernickList.add(((Neighbor)neighborobjectlist.get(0)).getUser_name());
					conversationuserheadimgList.add(((Neighbor)neighborobjectlist.get(0)).getUser_portrait());
					conversationList.add(conversation);
				}else{
					conversationusernameList.add(userid);				
					conversationusernickList.add("陌生人");
					conversationuserheadimgList.add(null);
					conversationList.add(conversation);
				}
			}
		}
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList,conversationusernickList,conversationuserheadimgList);
		// 设置adapter
		this.newMsgInfoListView.setAdapter(adapter);
//		this.newMsgInfoListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(neighbor_chat_fragment.this.getActivity(), AlertDialogforblack.class);
//				intent.putExtra("msg", "");
//				intent.putExtra("cancel", true);
//				intent.putExtra("position", position);
//				intent.putExtra("currentuser",String.valueOf(App.sUserLoginId));
//				intent.putExtra("removeid",conversationusernameList.get(position).toString());
//				neighbor_chat_fragment.this.startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
//				return true;
//			}
//		});
		this.newMsgInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				/******************通过username查数据库 得到用户昵称**************/
//				if (username.equals(DemoApplication.getInstance().getUserName()))
//					Toast.makeText(getActivity(), st2, 0).show();
//				else {
				    // 进入聊天页面
				    Intent intent = new Intent(getActivity(), ChatActivity.class);

				        // it is single chat
				    	if(username.equals("xitongxinxi")){
				    		intent.putExtra("userId", username);
	                        intent.putExtra("usernick", "系统信息");
				    	}else{
				    		neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyandUserObject(Long.parseLong(username));
				    		Loger.i("LYM","2323333--->"+Long.parseLong(username)+" "+neighborobjectlist.size());
	                        if(neighborobjectlist.size()>0){
	                        	intent.putExtra("userId", username);
					    		Loger.d("test4", "@@@user_id username="+username);
					    	//	neighbor = new Neighbor(getActivity());

		                        intent.putExtra("usernick", ((Neighbor)neighborobjectlist.get(0)).getUser_name());  // hyy 有数据库后 从写
		                        intent.putExtra("neighborurl", ((Neighbor)neighborobjectlist.get(0)).getUser_portrait());  // hyy 有数据库后 从写
		                        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
		                        intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
	        				}else{
	        					intent.putExtra("userId", username);
		                        intent.putExtra("usernick","陌生人");  // hyy 有数据库后 从写
		                        intent.putExtra("neighborurl", "null");  // hyy 有数据库后 从写
		                        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
		                        intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
	        				}
				    	}
				    startActivityForResult(intent, CHATDETAIL);
//				}
			}
		});
		// 注册上下文菜单
		registerForContextMenu(this.newMsgInfoListView);

		this.newMsgInfoListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				//hideSoftKeyboard();
				return false;
			}

		});
		return view;
	}
	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
		 * 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 
		 * 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					//if(conversation.getType() != EMConversationType.ChatRoom){
						sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
					//}
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}
	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}
	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
			chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();

			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					refresh();
				}
			});
			break;
		}

		case EventOfflineMessage: {
			Loger.d("hyytest", "mainactivity EventOfflineMessage");
			break;
		}

		case EventConversationListChanged: {
			Loger.d("hyytest", "mainactivity EventConversationListChanged");
		    break;
		}
		
		default:
			break;
		}
	}
	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationusernameList.clear();
		conversationusernickList.clear();
		conversationuserheadimgList.clear();
		for(EMConversation conversation:loadConversationsWithRecentChat()){
			if(!conversation.getUserName().equals("xitongxinxi")){
				String userid = conversation.getUserName();
				neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyandUserObject(Long.parseLong(userid));
				if(neighborobjectlist.size()>0){
					conversationusernameList.add(userid);				
					conversationusernickList.add(((Neighbor)neighborobjectlist.get(0)).getUser_name());
					conversationuserheadimgList.add(((Neighbor)neighborobjectlist.get(0)).getUser_portrait());
					conversationList.add(conversation);
				}else{
					conversationusernameList.add(userid);				
					conversationusernickList.add("陌生人");
					conversationuserheadimgList.add(null);
					conversationList.add(conversation);
				}
			}
		}
		if(conversationList.size()>0){
			try {
				adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList,conversationusernickList,conversationuserheadimgList);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		this.newMsgInfoListView.setAdapter(adapter);
//		if(adapter != null)
//		    adapter.notifyDataSetChanged();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == getActivity().RESULT_OK){
			if(requestCode == CHATDETAIL){
				refresh();
			}else if (requestCode == ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				Loger.d("test4", "REQUEST_CODE_ADD_TO_BLACKLIST position="+data.getIntExtra("position", -1));
				addUserToBlacklist(conversationList.get(data.getIntExtra("position", -1)).getUserName());
				gethttpdata(App.sUserLoginId, conversationList.get(data.getIntExtra("position", -1)).getUserName(), 1, IHttpRequestUtils.YOULIN);
			}
		}
//		else if(resultCode == AlertDialogforblack.REMOVE_BLACK_USER){
//			if (requestCode == ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST) { // 移出黑名单
//				
//				Loger.d("test4", "REMOVE_TO_BLACKLIST position="+data.getStringExtra("userid"));
//				gethttpdata(App.sUserLoginId, data.getStringExtra("userid"), 2, IHttpRequestUtils.YOULIN_SET_BLACKLIST);
//			}
//		}
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
					bRequestSet = false;
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
					bRequestSet = false;
					new ErrorServer(getActivity(), responseString);
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
		final ProgressDialog pd = new ProgressDialog(neighbor_chat_fragment.this.getActivity());
		pd.setMessage(getString(R.string.Is_moved_into_blacklist));
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().addUserToBlackList(username, false);
					neighbor_chat_fragment.this.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(neighbor_chat_fragment.this.getActivity(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					neighbor_chat_fragment.this.getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(neighbor_chat_fragment.this.getActivity(), R.string.Move_into_blacklist_failure, 0).show();
						}
					});
				}
			}
		}).start();
	}
}
