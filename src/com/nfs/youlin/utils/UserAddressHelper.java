package com.nfs.youlin.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserAddressHelper extends SQLiteOpenHelper{
	final String CREATE_TABLE_SQL =
			"create table dict(_id integer primary"+
					"key autoincrement,name,city,village,rooft,number)";
	public UserAddressHelper(Context context,String name ,int version)
	{
		super(context , name , null, version);
		}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_SQL);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion ,int newversion)
	{
		System.out.println("-------upgrade called--------"+oldversion+"---->"+newversion);
	}
}
