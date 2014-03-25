package com.huaguoshan.dao;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.huaguoshan.entity.FeedItem;
import com.huaguoshan.util.db.MDBHelper;

public class FeedDao {
	private MDBHelper myDBOpenHelper;
	
	public FeedDao(Context context){  
		myDBOpenHelper = new MDBHelper(context);  
    }
	
	public boolean refreshFeedCache(LinkedList<FeedItem> feeds){
		try{
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			db.execSQL("delete from feeds_cache;");
			String sql = "insert into feeds_cache(id,crawl_source,title,desc,desc_img, pub_time) values(?,?,?,?,?,?)";
	        SQLiteStatement stat = db.compileStatement(sql);
	        db.beginTransaction();
	        for (FeedItem feed : feeds) {
	            stat.bindString(1, feed.getId());
	            stat.bindString(2, feed.getCrawl_source());
	            stat.bindString(3, feed.getTitle());
	            stat.bindString(4, feed.getDesc());
	            stat.bindString(5, feed.getDesc_img());
	            stat.bindString(6, Long.toString(feed.getPub_time()));
	            stat.executeInsert();
	        }
	        db.setTransactionSuccessful();
	        db.endTransaction();
	        db.close();
	        return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public LinkedList<FeedItem> getFeedsCache(){
		LinkedList<FeedItem> list = new LinkedList<FeedItem>();  
		FeedItem a = null;  
        //1.获取相应的（可读的）数据库  
        SQLiteDatabase readableDatabase = myDBOpenHelper.getReadableDatabase();  
        Cursor rawQuery = readableDatabase.rawQuery("Select * from feeds_cache",null);  
        while(rawQuery.moveToNext()){  
        	String id = rawQuery.getString(rawQuery.getColumnIndex("id"));
        	String crawl_source = rawQuery.getString(rawQuery.getColumnIndex("crawl_source"));
        	String title = rawQuery.getString(rawQuery.getColumnIndex("title"));
        	String desc = rawQuery.getString(rawQuery.getColumnIndex("desc"));
        	String desc_img = rawQuery.getString(rawQuery.getColumnIndex("desc_img"));
        	Long pub_time = Long.valueOf(rawQuery.getString(rawQuery.getColumnIndex("pub_time")));
        	a = new FeedItem(id, 0, "", crawl_source, title, desc, desc_img, pub_time);
        	if(isReaded(id)){
        		a.setRead(true);
        	}
            list.add(a);  
        }  
        rawQuery.close();  
        readableDatabase.close();  
        return list;  
	}
	
	public boolean addCollect(FeedItem feed){
		try{
			//1.获取相应的(可写的)数据库  
			SQLiteDatabase writableDatabase = myDBOpenHelper.getWritableDatabase();
			//2.执行语句  
			writableDatabase.execSQL("insert into feeds_collect(id,crawl_source,title,desc,desc_img, pub_time) values(?,?,?,?,?,?)", 
					new Object[]{feed.getId(),feed.getCrawl_source(),feed.getTitle(),feed.getDesc(),feed.getDesc_img(),Long.toString(feed.getPub_time())});  
			writableDatabase.close();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean isInCollect(String id){
		boolean bIn = false;
		//1.获取相应的（可读的）数据库  
        SQLiteDatabase readableDatabase = myDBOpenHelper.getReadableDatabase();  
        //2.执行更新语句  
        Cursor rawQuery = readableDatabase.rawQuery("select * from feeds_collect where id=?",  new String[]{id});
        //3.将查询到的数据赋值给Account对象
        if(rawQuery.moveToNext()){
        	bIn = true;
        }
        //3.关闭结果集与数据库  
        rawQuery.close();
        readableDatabase.close();
        return bIn;
	}
	
	public boolean deleteCollect(String id){
		try{
			SQLiteDatabase writableDatabase = myDBOpenHelper.getWritableDatabase();
			writableDatabase.execSQL("delete from feeds_collect where id = ?",new String[]{id});  
	        //3.关闭数据库 
	        writableDatabase.close(); 
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public LinkedList<FeedItem> getCollectFeeds(){
		LinkedList<FeedItem> list = new LinkedList<FeedItem>();  
		FeedItem a = null;  
        //1.获取相应的（可读的）数据库  
        SQLiteDatabase readableDatabase = myDBOpenHelper.getReadableDatabase();  
        Cursor rawQuery = readableDatabase.rawQuery("Select * from feeds_collect",null);  
        while(rawQuery.moveToNext()){  
        	String id = rawQuery.getString(rawQuery.getColumnIndex("id"));
        	String crawl_source = rawQuery.getString(rawQuery.getColumnIndex("crawl_source"));
        	String title = rawQuery.getString(rawQuery.getColumnIndex("title"));
        	String desc = rawQuery.getString(rawQuery.getColumnIndex("desc"));
        	String desc_img = rawQuery.getString(rawQuery.getColumnIndex("desc_img"));
        	Long pub_time = Long.valueOf(rawQuery.getString(rawQuery.getColumnIndex("pub_time")));
        	a = new FeedItem(id, 0, "", crawl_source, title, desc, desc_img, pub_time);
            list.addFirst(a);
        }  
        rawQuery.close();  
        readableDatabase.close();  
        return list;  
	}
	
	public boolean refreshFeedCollect(LinkedList<FeedItem> feeds){
		try{
			SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
			db.execSQL("delete from feeds_collect;");
			String sql = "insert into feeds_collect(id,crawl_source,title,desc,desc_img, pub_time) values(?,?,?,?,?,?)";
	        SQLiteStatement stat = db.compileStatement(sql);
	        db.beginTransaction();
	        for(int i = feeds.size() - 1; i >= 0; i--){
	        	stat.bindString(1, feeds.get(i).getId());
	            stat.bindString(2, feeds.get(i).getCrawl_source());
	            stat.bindString(3, feeds.get(i).getTitle());
	            stat.bindString(4, feeds.get(i).getDesc());
	            stat.bindString(5, feeds.get(i).getDesc_img());
	            stat.bindString(6, Long.toString(feeds.get(i).getPub_time()));
	            stat.executeInsert();
	        }
	        db.setTransactionSuccessful();
	        db.endTransaction();
	        db.close();
	        return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean addRead(String id){
		try{
			//1.获取相应的(可写的)数据库  
			SQLiteDatabase writableDatabase = myDBOpenHelper.getWritableDatabase();
			//2.执行语句  
			writableDatabase.execSQL("insert into feeds_read(id) values(?)", new String[]{id});  
			writableDatabase.close();
			return true;
		}catch(Exception e){
			return false;
		}
	} 
	
	public boolean isReaded(String id){
		boolean bReaded = false;
		//1.获取相应的（可读的）数据库  
        SQLiteDatabase readableDatabase = myDBOpenHelper.getReadableDatabase();  
        //2.执行更新语句  
        Cursor rawQuery = readableDatabase.rawQuery("select * from feeds_read where id=?",  new String[]{id});
        //3.将查询到的数据赋值给Account对象
        if(rawQuery.moveToNext()){
        	bReaded = true;
        }
        //3.关闭结果集与数据库  
        rawQuery.close();
        readableDatabase.close();
        return bReaded;
	}
	
	public boolean addFeedAction(String id, int type){
		try{
			//1.获取相应的(可写的)数据库  
			SQLiteDatabase writableDatabase = myDBOpenHelper.getWritableDatabase();
			//2.执行语句  
			writableDatabase.execSQL("insert into feeds_action(id,type) values(?,?)", new Object[]{id,type});  
			writableDatabase.close();
			return true;
		}catch(Exception e){
			return false;
		}
	} 
	
	public int getFeedActionType(String id){
		int type = -1;
		//1.获取相应的（可读的）数据库  
        SQLiteDatabase readableDatabase = myDBOpenHelper.getReadableDatabase();  
        //2.执行更新语句  
        Cursor rawQuery = readableDatabase.rawQuery("select * from feeds_action where id=?",  new String[]{id});  
        if(rawQuery.moveToNext()){
        	type = rawQuery.getInt(rawQuery.getColumnIndex("type"));  
        }
        //3.关闭结果集与数据库  
        rawQuery.close();  
        readableDatabase.close();  
        return type;  
	}

}
