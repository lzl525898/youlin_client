package com.easemob.chatuidemo.db;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chatuidemo.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class NeighborDaoDBSearch {
    private SQLiteOpenHelper dbOpenHelper;
    private Context context;
    private SQLiteDatabase db;
    private final String SELECT_BELONG_FAMILY_ID_NEIGHBOR_OBJ = "select * from " + 
            "table_neighbor" + " where user_id = ? group by user_id order by _id"; 
    private final String SELECT_SELF_ACCOUNT_OBJ = "select user_portrait from " + 
            "table_users" + " where user_id = ?";
    public NeighborDaoDBSearch(Context context){
        this.context = context;
        this.dbOpenHelper = new SQLiteOpenHelper(context, "neighbors.db", null, 1) {
            
            @Override
            public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCreate(SQLiteDatabase arg0) {
                // TODO Auto-generated method stub
                
            }
        };
    }
    public List<String> findSelfImg(long user_id){
        List<String> neighborsList = new ArrayList<String>();
        String toString_User = String.valueOf(user_id);
        if(this.db == null){
            this.db = this.dbOpenHelper.getReadableDatabase();
        }   
        this.db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = this.db.rawQuery(SELECT_SELF_ACCOUNT_OBJ, new String[]{toString_User});
            while(cursor.moveToNext()){
                neighborsList.add(cursor.getString(cursor.getColumnIndex("user_portrait")));
            }
            this.db.setTransactionSuccessful();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loger.i("TEST", "NeighborDaoDBImpl->findAppointObject"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
            db.endTransaction();
        }
        return neighborsList;
    }
    /*找到指定用户的某个邻居  根据邻居的user_id****************/
    public List<String> findPointFamilyandUserObject(long user_id){
        List<String> neighborsList = new ArrayList<String>();
        String toString_User = String.valueOf(user_id);
        if(this.db == null){
            this.db = this.dbOpenHelper.getReadableDatabase();
        }   
        this.db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = this.db.rawQuery(SELECT_BELONG_FAMILY_ID_NEIGHBOR_OBJ, new String[]{toString_User});
            while(cursor.moveToNext()){
                neighborsList.add(cursor.getString(cursor.getColumnIndex("user_name")));
            }
            this.db.setTransactionSuccessful();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loger.i("TEST", "NeighborDaoDBImpl->findAppointObject"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
            db.endTransaction();
        }
        return neighborsList;
    }
    public List<String> findPointFamilyandUserimg(long user_id){
        List<String> neighborsList = new ArrayList<String>();
        String toString_User = String.valueOf(user_id);
        if(this.db == null){
            this.db = this.dbOpenHelper.getReadableDatabase();
        }   
        this.db.beginTransaction();
        Cursor cursor = null;
        try {
            cursor = this.db.rawQuery(SELECT_BELONG_FAMILY_ID_NEIGHBOR_OBJ, new String[]{toString_User});
            while(cursor.moveToNext()){
                neighborsList.add(cursor.getString(cursor.getColumnIndex("user_portrait")));
            }
            this.db.setTransactionSuccessful();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Loger.i("TEST", "NeighborDaoDBImpl->findAppointObject"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
            db.endTransaction();
        }
        return neighborsList;
    }
}
