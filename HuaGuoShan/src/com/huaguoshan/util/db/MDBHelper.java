package com.huaguoshan.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MDBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "huaguoshan_app1.db";
    private final static int DATABASE_VERSION = 1;
	
	public MDBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("数据库开始创建");
		// TODO Auto-generated method stub
		String sql="create table feeds_collect( id VARCHAR(32) primary key, crawl_source VARCHAR(32)," +
				" title VARCHAR(120), desc TEXT, desc_img VARCHAR(100), pub_time VARCHAR(10));";
		db.execSQL(sql);
		String sql2="create table feeds_cache( id VARCHAR(32) primary key, crawl_source VARCHAR(32)," +
				" title VARCHAR(120), desc TEXT, desc_img VARCHAR(100), pub_time VARCHAR(10));";
		db.execSQL(sql2);
		String sql3 = "create table feeds_read(id VARCHAR(32) primary key);";
		db.execSQL(sql3);
		String sql4 = "create table feeds_action(id VARCHAR(32) primary key, type int);";
		db.execSQL(sql4);
		System.out.println("数据库创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("数据库开始创建");

	}

}
