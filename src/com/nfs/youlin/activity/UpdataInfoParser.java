package com.nfs.youlin.activity;

import android.util.Log;
import android.util.Xml;

import com.nfs.youlin.activity.UpdataInfo;
import com.nfs.youlin.utils.Loger;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

public class UpdataInfoParser {
	public  UpdataInfo getUpdataInfo(InputStream is) throws Exception{
		XmlPullParser parser = Xml.newPullParser();  
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					info.setVersion(parser.nextText());
					Loger.i("TEST", "1111111111111------>"+info.getVersion());
				} else if ("url".equals(parser.getName())) {
					info.setUrl(parser.nextText());
				} else if ("force".equals(parser.getName())){
					info.setForce(parser.nextText());
				} else if ("description".equals(parser.getName())) {
					info.setDescription(parser.nextText());
				} else if ("url_server".equals(parser.getName())) {
					info.setUrl_server(parser.nextText());
				} 
				break;
			}
			type = parser.next();
		}
		return info;
	}
}
