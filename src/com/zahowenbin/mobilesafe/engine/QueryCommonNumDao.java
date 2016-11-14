package com.zahowenbin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class QueryCommonNumDao {
	protected static final String path = "data/data/com.zahowenbin.mobilesafe/files/commonnum.db";
	public static List<Group> getGroup(){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
		List<Group> groupList = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			QueryCommonNumDao.Group group = new QueryCommonNumDao().new Group();
			group.name = cursor.getString(0);
			group.idx = cursor.getString(1);
			group.childList = getChild(group.idx);
			groupList.add(group);
		}
		cursor.close();
		db.close();
		return groupList;
	}
	
	public static List<Child> getChild(String idx){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from table" + idx, null);
		List<Child> childList = new ArrayList<Child>();
		while (cursor.moveToNext()) {
			QueryCommonNumDao.Child child = new QueryCommonNumDao().new Child();
			child._id = cursor.getString(0);
			child.number = cursor.getString(1);
			child.name = cursor.getString(2);
			childList.add(child);
		}
		cursor.close();
		db.close();
		return childList;
	}
	
	public class Group{
		public String name;
		public String idx;
		public List<Child> childList;
	}

	public class Child{
		public String _id;
		public String number;
		public String name;
	}
}




