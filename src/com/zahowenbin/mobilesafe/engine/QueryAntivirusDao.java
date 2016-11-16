package com.zahowenbin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class QueryAntivirusDao {
	protected static final String path = "data/data/com.zahowenbin.mobilesafe/files/antivirus.db";

	public static List<String> getAntivirus(){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
		List<String> antivirusList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			antivirusList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return antivirusList;
	}
}
