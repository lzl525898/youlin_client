package com.nfs.youlin.activity.titlebar.barter;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity.listViewListen;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopicRange;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.AddPicture;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.Bimp;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.FileUtils;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.UploadPicture;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;

public class BarterActivity extends Activity implements OnClickListener {
	public static TextView newTopicTv;
	private ActionBar actionBar;
	private LinearLayout newTopicLayout;
	private EditText newTopicTitleEt;
	private EditText newTopicContentEt;
	private ImageView newTopicSendToImg;
	private ImageView newTopicTitleImg;
	public Thread runQueryThread;
	private Account account;
	private AllFamilyDaoDBImpl curfamilyDaoDBImpl;
	private AllFamily currentFamily;
	private String communityDetail;
	private String addrDetails;
	public RequestParams sRequestParams;
	PopupWindow popupWindow;
	private LinearLayout newOldLayout;
	private TextView newOldTv;
	private ImageView newOldImg;
	private EditText priceEt;
	private ImageView priceImg;
	private LinearLayout priceLayout;
	private LinearLayout categoryLayout;
	private TextView categoryTv;
	private ImageView categoryImg;
	Bitmap bitmap;
	// Bitmap bitMap;
	Bitmap newbitmap;
	Bimp bimp;
	public static int nforumId = 0;
	int priceInt;
	String categoryStr = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_barter);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("以物换物");
		bimp = new Bimp();
		newTopicLayout = (LinearLayout) findViewById(R.id.new_topic_layout);
		newTopicTv = (TextView) findViewById(R.id.new_topic_tv);
		newTopicTv.setText("本小区");
		newTopicTitleEt = (EditText) findViewById(R.id.new_topic_title_et);
		newTopicContentEt = (EditText) findViewById(R.id.new_topic_content_et);
		newTopicSendToImg = (ImageView) findViewById(R.id.new_topic_send_to_img);
		newTopicTitleImg = (ImageView) findViewById(R.id.new_topic_title_img);
		newOldLayout = (LinearLayout) findViewById(R.id.new_old_layout);
		newOldTv = (TextView) findViewById(R.id.new_old_tv);
		newOldImg = (ImageView) findViewById(R.id.new_old_img);
		priceEt = (EditText) findViewById(R.id.price_et);
		priceImg = (ImageView) findViewById(R.id.price_img);
		priceLayout = (LinearLayout) findViewById(R.id.price_layout);
		categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
		categoryTv = (TextView) findViewById(R.id.category_tv);
		categoryImg = (ImageView) findViewById(R.id.category_img);
		new AddPicture(this);
		account = getSenderInfo();
		if (account == null) {
			Loger.i("TEST", "未找到loginId");
			return;
		}
		addrDetails = getAddrDetail();
		if (addrDetails == null) {
			Loger.i("TEST", "地址信息错误");
			return;
		}
		communityDetail = getCommunityDetail();
		curfamilyDaoDBImpl = new AllFamilyDaoDBImpl(this);
		currentFamily = curfamilyDaoDBImpl.getCurrentAddrDetail(addrDetails);
		newTopicLayout.setOnClickListener(this);
		newOldLayout.setOnClickListener(this);
		priceLayout.setOnClickListener(this);
		categoryLayout.setOnClickListener(this);
		newTopicTitleEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
			}
		});
		sendTvListen();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
		new BackgroundAlpha(1f, BarterActivity.this);
		AddPicture.adapter.notifyDataSetChanged();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitDialog();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AddPicture.TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == Activity.RESULT_OK) {
				// File file=new File(Environment.getExternalStorageDirectory(),
				// "circleImg.jpg");
				String str = Environment.getExternalStorageDirectory() + File.separator + "circleImg.jpg";
				try {
					bitmap = bimp.revitionImageSize(str);
					// Bimp.bitmapList.add(bitmap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				XuanzhuanBitmap xuanzhuanBitmap = new XuanzhuanBitmap();
				int degree = xuanzhuanBitmap.readPictureDegree(str);
				newbitmap = xuanzhuanBitmap.rotaingImageView(degree, bitmap);
				// Bimp.bitmapList.add(newbitmap);
				String fileName = String.valueOf(System.currentTimeMillis());
				// Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(newbitmap, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(newbitmap);
				takePhoto.setImagePath(Environment.getExternalStorageDirectory() + "/Photo_LJ/" + fileName + ".jpg");
				takePhoto
						.setThumbnailPath(Environment.getExternalStorageDirectory() + "/Photo_LJ/" + fileName + ".jpg");
				Bimp.tempSelectBitmap.add(takePhoto);
				AddPicture.adapter.notifyDataSetChanged();

			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.new_topic_layout:
			Intent intent = new Intent(this, BarterRangeActivity.class);
			intent.putExtra("village", communityDetail);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;
		case R.id.new_old_layout:
			newOldPopWindow();
			break;
		case R.id.price_layout:
			priceEt.setFocusable(true);
			priceEt.setFocusableInTouchMode(true);
			priceEt.requestFocus();
			priceEt.requestFocusFromTouch();
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			// inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.SHOW_FORCED);
			break;
		case R.id.category_layout:
			categoryPopWindow();
			break;
		default:
			break;
		}
	}

	private void sendFinish() {
		ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("发布中...");
		String sendToStr = newTopicTv.getText().toString().trim();
		String sendTitleStr = newTopicTitleEt.getText().toString().trim();
		String sendContentStr = newTopicContentEt.getText().toString().trim();
		String newOldStr = newOldTv.getText().toString().trim();
		String priceStr = priceEt.getText().toString().trim();
		Loger.i("youlin", "sendToStr--->" + sendToStr);
		if (!sendToStr.isEmpty()) {
			if (!sendTitleStr.isEmpty()) {
				if (sendContentStr.length() <= 1000) {
					if (Bimp.tempSelectBitmap.size() != 0) {
						if (!newOldStr.isEmpty()) {
							if (!categoryStr.equals("0")) {
								if (!priceStr.isEmpty()) {
									if (NetworkService.networkBool) {

										for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
											File file = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
											if (!file.exists()) {
												Toast.makeText(BarterActivity.this, "上传失败", 0).show();
												return;
											}
										}
										pd.show();
										sRequestParams = new RequestParams();
										sRequestParams.put("forum_id", BarterActivity.nforumId); // 0表示本小区、1、周边、2.同城
										sRequestParams.put("forum_name", sendToStr);
										sRequestParams.put("topic_category_type", 2); // 2
																						// 表示普通话题//
																						// 345
																						// 物业
										sRequestParams.put("sender_id", account.getLogin_account());
										sRequestParams.put("sender_name", account.getUser_name());
										sRequestParams.put("sender_lever", account.getUser_level());
										sRequestParams.put("sender_portrait", account.getUser_portrait());
										sRequestParams.put("sender_family_id", account.getUser_family_id());
										sRequestParams.put("sender_family_address", account.getUser_family_address());
										sRequestParams.put("sender_nc_role", 0);
										sRequestParams.put("display_name", communityDetail != null
												? account.getUser_name() + "@" + communityDetail : null);
										sRequestParams.put("object_data_id", 4); // 0.表示一般话题、1.表示活动
																					// 3.news
																					// //4.以物换物
										sRequestParams.put("circle_type", 1); // 1.表示一般话题
										sRequestParams.put("topic_title", sendTitleStr);
										sRequestParams.put("topic_content", newTopicContentEt.getText().toString());
										Loger.i("TEST", "发帖当前内容->" + newTopicContentEt.getText().toString());
										Loger.i("TEST", "发帖当前cityID->" + currentFamily.getFamily_city_id());
										Loger.i("TEST", "发帖当前commID->" + currentFamily.getFamily_community_id());
										sRequestParams.put("sender_city_id", currentFamily.getFamily_city_id());
										sRequestParams.put("sender_community_id",
												currentFamily.getFamily_community_id());
										sRequestParams.put("price", priceStr);
										sRequestParams.put("oldornew", priceInt);
										sRequestParams.put("goodstype", categoryStr);
										new UploadPicture(BarterActivity.this, sRequestParams, 5);
										new Timer().schedule(new TimerTask() {
											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												try {
													new ClearSelectImg();
													finish();
												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										}, 20000);

									} else {
										// if(pd!=null){
										// pd.cancel();
										// }
										Toast.makeText(BarterActivity.this, "请先开启网络", Toast.LENGTH_SHORT).show();
									}
								} else {
									priceImg.setVisibility(View.VISIBLE);
								}
							} else {
								categoryImg.setVisibility(View.VISIBLE);
							}
						} else {
							newOldImg.setVisibility(View.VISIBLE);
						}
					} else {
						Toast.makeText(BarterActivity.this, "图片不能为空", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(BarterActivity.this, "发送内容太长", Toast.LENGTH_SHORT).show();
				}
			} else {
				newTopicTitleImg.setVisibility(View.VISIBLE);
			}
		} else {
			newTopicSendToImg.setVisibility(View.VISIBLE);
		}
	}

	private void sendTvListen() {
		newTopicTv.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				newTopicSendToImg.setVisibility(View.GONE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		newTopicTitleEt.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				newTopicTitleImg.setVisibility(View.GONE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		newOldTv.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				newOldImg.setVisibility(View.GONE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		categoryTv.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				categoryImg.setVisibility(View.GONE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		priceEt.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				priceImg.setVisibility(View.GONE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_topic, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ExitDialog();
			break;
		case R.id.finish:
			sendFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Account getSenderInfo() {
		Account account = null;
		if (App.sUserLoginId > 0) {
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(BarterActivity.this);
			account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		}
		return account;
	}

	private String getCommunityDetail() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String commString = sharedata.getString("village", null);
		return commString;
	}

	private String getAddrDetail() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String addrDetail = null;
		String city = sharedata.getString("city", null);
		String village = sharedata.getString("village", null);
		String detail = sharedata.getString("detail", null);
		if (city != null && village != null && detail != null) {
			addrDetail = city + village + detail;
		}
		return addrDetail;
	}

	public void ExitDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(BarterActivity.this);
		// builder.setTitle("提示");
		builder.setMessage("确定要放弃此次编辑吗？");
		// builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new ClearSelectImg();
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}

	public void newOldPopWindow() {
		View contentView = getLayoutInflater().inflate(R.layout.activity_share_popwindow_newpush, null);
		ListView listView = (ListView) contentView.findViewById(R.id.share_pop_repair_lv);
		final String[] arr = new String[] { "全新", "九成新", "八成新", "七成新", "六成新", "五成新", "五成新以下" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(BarterActivity.this,
				R.layout.activity_share_pop_textview, arr);
		listView.setAdapter(adapter);
		popupWindow = new PopupWindow(BarterActivity.this);
		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setContentView(contentView);
		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				newOldTv.setText(arr[position]);
				priceInt = position;
				popupWindow.dismiss();
			}
		});
	}

	public void categoryPopWindow() {
		View contentView = getLayoutInflater().inflate(R.layout.activity_share_popwindow_newpush, null);
		ListView listView = (ListView) contentView.findViewById(R.id.share_pop_repair_lv);
		final String[] arr = new String[] { "手机", "数码", "家用电器", "代步工具", "母婴用品", "服装鞋帽", "家具家居", "电脑","其他" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(BarterActivity.this,
				R.layout.activity_share_pop_textview, arr);
		listView.setAdapter(adapter);
		final PopupWindow popupWindowCategory = new PopupWindow(BarterActivity.this);
		popupWindowCategory.setWidth(LayoutParams.WRAP_CONTENT);
		popupWindowCategory.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindowCategory.setBackgroundDrawable(new BitmapDrawable());
		popupWindowCategory.setFocusable(true);
		popupWindowCategory.setOutsideTouchable(true);
		popupWindowCategory.setContentView(contentView);
		popupWindowCategory.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				categoryTv.setText(arr[position]);
				categoryStr = arr[position];
				popupWindowCategory.dismiss();
			}
		});
	}
}