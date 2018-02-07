package com.easemob.chatuidemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.ContentValues;
import android.content.Context;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbadapter {
    private final Context context;
    private UserAddressHelper DBHelper; 
    private SQLiteDatabase db;
    
    public dbadapter(Context ctx) {
        this.context = ctx; 
        DBHelper = new UserAddressHelper(context,"useraddress.db3", 1); 
    }
    public void initAdapter(){
        this.open();
        this.close();
    }
    
    //---打开数据库--- 
    public dbadapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }
    
    //---关闭数据库---
    public void close() {  
        DBHelper.close(); 
    }
    public  List<Map<String, Object>> getaddresslist(){
        Cursor cursor = db.rawQuery("select * from dict", null);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();;
        String city;
        String village;
        String rooft;
        String number;
        while (cursor.moveToNext()) {
            city = cursor.getString(cursor.getColumnIndex("city"));
            village = cursor.getString(cursor.getColumnIndex("village"));
            rooft = cursor.getString(cursor.getColumnIndex("rooft"));
            number = cursor.getString(cursor.getColumnIndex("number"));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("city", city); 
            map.put("village", village);
            map.put("rooft", rooft);
            map.put("number", number);
            list.add(map); 
        }
        return list;
    }
    public void setaddresslist( List<Map<String, Object>> list){
        db.delete("dict", null, null);
        ContentValues values = new ContentValues();  
        String city;
        String village;
        String rooft;
        String number;
        for(int i = 0;i<list.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map = list.get(i);
            city = (String) map.get("city");
            village = (String) map.get("village");
            rooft = (String) map.get("rooft");
            number = (String) map.get("number");
            values.put("city", city);
            values.put("village", village);
            values.put("rooft", rooft);
            values.put("number", number);
            db.insert("dict", null, values);
        }
       
    }
}
