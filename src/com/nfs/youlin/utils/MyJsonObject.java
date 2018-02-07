package com.nfs.youlin.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyJsonObject {
	JSONObject jsonObject;
	public void setJsonObject(String jsonStr) throws JSONException{
			jsonObject=new JSONObject(jsonStr);
	}
	public String getPortrait() throws JSONException{
		return jsonObject.getString("senderAvatar");
	}
	public String getName() throws JSONException{
		return jsonObject.getString("displayName");
	}
	public String getContent() throws JSONException{
		return jsonObject.getString("content");
	}
	public String getReplyImg(){
		String replyImg=null;
		try {
			if (!jsonObject.getString("mediaFiles").isEmpty()) {
				JSONArray mediaArray=new JSONArray(jsonObject.getString("mediaFiles"));
				for (int j = 0; j < mediaArray.length(); j++) {
					JSONObject mediaObject=new JSONObject(mediaArray.getString(j));
					try {
						replyImg = mediaObject.getString("resPath");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						replyImg = "null";
						e.printStackTrace();
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			replyImg="null";
			e.printStackTrace();
		}
		return replyImg;
	}
	public String getTime() throws JSONException{
		return TimeToStr.getTimeToStr(Long.parseLong(jsonObject.getString("sendTime")));
	}
}

