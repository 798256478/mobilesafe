package com.zahowenbin.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.zahowenbin.mobilesafe.db.AppLockOpenHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppLockDao {
	private AppLockOpenHelper appLockOpenHelper;
	Context context = null;
	private static AppLockDao appLockDao = null;
	private ContentResolver contentResolver;
	
	private AppLockDao(Context context){
		this.context = context;
		contentResolver = context.getContentResolver();
		appLockOpenHelper = new AppLockOpenHelper(context);
	}

	public static AppLockDao instance(Context context){
		if(appLockDao == null){
			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}
	
	public void insert(String packageName){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("packageName", packageName);
		db.insert("applock", null, contentValues);
		db.close();
		contentResolver.notifyChange(Uri.parse("content://applock/change"), null);
	}
	
	public void update(){
		
	}
	
	public void delete(String packageName){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		db.delete("applock", "packageName = ?", new String[]{packageName});
		db.close();
		contentResolver.notifyChange(Uri.parse("content://applock/change"), null);
	}
	
	public List<String> findAll(){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packageName"}, null, null, null, null, null);
		List<String> packNameList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			packNameList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return packNameList;
	}
}
