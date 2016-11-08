package com.zahowenbin.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zahowenbin.mobilesafe.db.BlackNumberOpenHelper;
import com.zahowenbin.mobilesafe.db.domain.BlackNumberInfo;

public class BlackNumberDao {
	//blackNumber����ģʽ
	//1.˽�л����췽��
	private BlackNumberOpenHelper blackNumberOpenHelper;
	private BlackNumberDao(Context context){
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
	
	//2.������ǰ��Ķ���
	private static BlackNumberDao blackNumberDao =null;
	//3.�ṩһ����̬�����������ǰ��Ķ���Ϊ�մ���һ���µ�ʾ��
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
}