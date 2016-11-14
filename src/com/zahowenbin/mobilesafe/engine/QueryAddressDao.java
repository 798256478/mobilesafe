package com.zahowenbin.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryAddressDao {
	protected static final String path = "data/data/com.zahowenbin.mobilesafe/files/address.db";
	private static String mAddress = "δ֪����";
	private static String area;
	private static String subPhone;
	public static String getAddress(String phone){
		mAddress = "δ֪����";
		String regularExpression = "^1[3-8]\\d{9}";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(phone.matches(regularExpression)){
			subPhone = phone.substring(0, 7);
			Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{subPhone}, null, null, null);
			if(cursor.moveToNext()){
				String outkey = cursor.getString(0);
				Cursor cursor2 = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
				if(cursor2.moveToNext()){
					mAddress = cursor2.getString(0);
				}
			}
		} else {
			int len = phone.length();
			switch (len) {
			case 0:
				mAddress = "";
				break;
			case 3:
				mAddress = "�����绰";
				break;
			case 4:
				mAddress = "ģ����";
				break;
			case 5:
				mAddress = "����绰";
				break;
			case 7:
				mAddress = "���ص绰";
				break;
			case 11:
				area = phone.substring(1, 3);
				Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
				if(cursor.moveToNext()){
					mAddress = cursor.getString(0);
				} else {
					mAddress = "δ֪����";
				}
				break;
			case 12:
				area = phone.substring(1, 4);
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
				if(cursor1.moveToNext()){
					mAddress = cursor1.getString(0);
				} else {
					mAddress = "δ֪����";
				}
				break;
			default:
				mAddress = "δ֪����";
				break;
			}
		}
		return mAddress;
	}
}
