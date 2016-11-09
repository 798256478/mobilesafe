package com.zahowenbin.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zahowenbin.mobilesafe.db.BlackNumberOpenHelper;
import com.zahowenbin.mobilesafe.db.domain.BlackNumberInfo;

public class BlackNumberDao {
	//blackNumber单例模式
	//1.私有化构造方法
	private BlackNumberOpenHelper blackNumberOpenHelper;
	private BlackNumberDao(Context context){
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	//2.声明当前类的对象
	private static BlackNumberDao blackNumberDao =null;
	//3.提供一个静态方法，如果当前类的对象为空创建一个新的示例
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao == null){
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	
	public void insert(String phone, String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("phone", phone);
		contentValues.put("mode", mode);
		db.insert("blacknumber", null, contentValues);
		db.close();
	}
	
	public void update(String phone, String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", mode);
		db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});
		db.close();
	}
	
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		db.delete("blacknumber", "phone = ?", new String[]{phone});
		db.close();
	}
	
	public List<BlackNumberInfo> findAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setPhone(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}
	
	public List<BlackNumberInfo> find(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery("select phone, mode from blacknumber order by _id desc limit ?, 20;", new String[]{index+""});
		while (cursor.moveToNext()) {
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setPhone(cursor.getString(0));
			blackNumberInfo.setMode(cursor.getString(1));
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}
	
	public int getCount(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
		int count = 0;
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;	
	}
	
	public int getModeByPhone(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);
		int mode = 0;
		while (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		return mode;
	}
}
