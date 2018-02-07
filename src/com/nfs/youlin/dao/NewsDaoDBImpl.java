package com.nfs.youlin.dao;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.NewsBlock;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewsDaoDBImpl implements INeighborsDao{
	private NeighborsDBOpenHelper dbOpenHelper;
	private Context context;
	private SQLiteDatabase db;
	private final String INSERT_SQL = "insert into " + NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+"("
			  +"news_first,news_title,news_pic,news_url,news_belongs,news_id,news_send_time,news_push_time,news_others," 
			  +"table_version) values(?,?,?,?,?,?,?,?,?,?)";
//	private final String DELETE_NEWS_BYID = "delete from "+NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+" where news_belongs = ?";
	private final String SELECT_NEWS_BYID = "select * from "+NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+" where news_belongs = ? and news_first = ? group by news_id order by news_id";
	private final String SELECT_NEWS_LAST = "select max(news_id) from "+NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+" where news_belongs = ? and news_first = ?";
	private final String SELECT_NEWS_LAST_PUSHTIME = "select max(news_push_time) from "+NeighborsDBOpenHelper.TABLE_NEWS_RECEIVE+" where news_belongs = ? and news_first = ?";
	public NewsDaoDBImpl(Context context){
		this.context = context;
		this.dbOpenHelper = new NeighborsDBOpenHelper(this.context, null);
	}
	public long findNewsPushtimeMax(long news_belongs, int first_flag) {
		long maxfirstpushtime = 0;
		String toString_Belongs = String.valueOf(news_belongs);
		String toString_Flag = String.valueOf(first_flag);
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_NEWS_LAST_PUSHTIME, new String[]{toString_Belongs,toString_Flag});
			while(cursor.moveToNext()){
				Loger.i("test5", "---------"+cursor.getLong(cursor.getColumnIndex("max(news_push_time)")));
				maxfirstpushtime = cursor.getLong(cursor.getColumnIndex("max(news_push_time)"));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NewsDaoDBImpl->findAppointObject"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return maxfirstpushtime;
	}
	public int findNewsIdMax(long news_belongs, int first_flag) {
		int maxfirstid = 0;
		String toString_Belongs = String.valueOf(news_belongs);
		String toString_Flag = String.valueOf(first_flag);
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_NEWS_LAST, new String[]{toString_Belongs,toString_Flag});
			while(cursor.moveToNext()){
				Loger.i("test5", "---------"+cursor.getInt(cursor.getColumnIndex("max(news_id)")));
				maxfirstid = cursor.getInt(cursor.getColumnIndex("max(news_id)"));
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NewsDaoDBImpl->findAppointObject"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return maxfirstid;
	}
	public List<Object> findNewsObjectbelongto(long news_belongs, int first_flag) {
		// TODO Auto-generated method stub
		List<Object> newsList = new ArrayList<Object>();
		String toString_Belongs = String.valueOf(news_belongs);
		String toString_Flag = String.valueOf(first_flag);
		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		Cursor cursor = null;
		try {
			cursor = this.db.rawQuery(SELECT_NEWS_BYID, new String[]{toString_Belongs,toString_Flag});
			while(cursor.moveToNext()){
				NewsBlock news = new NewsBlock(context);
				news.setfirstflag(cursor.getInt(cursor.getColumnIndex("news_first")));
				news.setnewstitle(cursor.getString(cursor.getColumnIndex("news_title")));
				news.setnewspic(cursor.getString(cursor.getColumnIndex("news_pic")));
				news.setnewsurl(cursor.getString(cursor.getColumnIndex("news_url")));
				news.setnewsbelong(cursor.getLong(cursor.getColumnIndex("news_belongs")));
				news.setnewsid(cursor.getInt(cursor.getColumnIndex("news_id")));
				news.setnewssendtime(cursor.getLong(cursor.getColumnIndex("news_send_time")));
				news.setnewspushtime(cursor.getLong(cursor.getColumnIndex("news_push_time")));
				news.setnewsothers(cursor.getString(cursor.getColumnIndex("news_others")));
				newsList.add(news);
			}
			this.db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NewsDaoDBImpl->findAppointObject"+e.getMessage());
			e.printStackTrace();
		} finally{
			if(cursor != null){
				cursor.close();
				cursor = null;
			}
			db.endTransaction();
		}
		return newsList;
	}
	@Override
	public List<Object> findAllObject() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Object> findPointTypeObject(int type) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void saveObject(Object obj) {
		// TODO Auto-generated method stub
		//NewsBlock news = new NewsBlock(context);

		if(this.db == null){
			this.db = this.dbOpenHelper.getReadableDatabase();
		}	
		this.db.beginTransaction();
		try {
			this.db.execSQL(INSERT_SQL, new Object[]{((NewsBlock)obj).getfirstflag(),
					((NewsBlock)obj).getnewstitle(),
					((NewsBlock)obj).getnewspic(),
					((NewsBlock)obj).getnewsurl(),
					((NewsBlock)obj).getnewsbelong(),
					((NewsBlock)obj).getnewsid(),
					((NewsBlock)obj).getnewssendtime(),
					((NewsBlock)obj).getnewspushtime(),
					((NewsBlock)obj).getnewsothers(),
													 NeighborsDBOpenHelper.DB_VERSION});
			this.db.setTransactionSuccessful();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "NewsDaoDBImpl saveObject:"+e.getMessage());
			e.printStackTrace();
		} finally{
			db.endTransaction();
		}
	}
	@Override
	public void deleteObject(int news_id) {
		// TODO Auto-generated method stub

	}
	@Override
	public void modifyObject(Object obj) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void releaseDatabaseRes() {
		// TODO Auto-generated method stub
		
	}
}
