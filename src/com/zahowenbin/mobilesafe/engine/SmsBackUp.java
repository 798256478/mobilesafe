package com.zahowenbin.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;
import com.zahowenbin.mobilesafe.utils.ToastUtil;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {
	private static int index = 0;
	public static void backUp(Context context, String path, CallBack callBack){
		Cursor cursor = null;
		FileOutputStream fos = null;
		try {
			cursor  = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
			File file = new File(path);
			fos = new FileOutputStream(file);
			int count = cursor.getCount();
			ToastUtil.show(context, count+"");
			if(count>0){
				if(callBack != null){
					callBack.setMax(count);
				}
				XmlSerializer xmlSerializer = Xml.newSerializer();
				xmlSerializer.setOutput(fos, "utf-8");
				xmlSerializer.startDocument("utf-8", true);
				xmlSerializer.startTag(null, "smss");
				while (cursor .moveToNext()) {
					
					xmlSerializer.startTag(null, "sms");
					
					xmlSerializer.startTag(null, "address");
					xmlSerializer.text(cursor .getString(0));
					xmlSerializer.endTag(null, "address");
					
					xmlSerializer.startTag(null, "date");
					xmlSerializer.text(cursor .getString(1));
					xmlSerializer.endTag(null, "date");
					
					xmlSerializer.startTag(null, "type");
					xmlSerializer.text(cursor .getString(2));
					xmlSerializer.endTag(null, "type");
					
					xmlSerializer.startTag(null, "body");
					xmlSerializer.text(cursor .getString(3));
					xmlSerializer.endTag(null, "body");
					
					xmlSerializer.endTag(null, "sms");
					
					index++;
					if(callBack != null){
						callBack.setProgress(index);
					}
					Thread.sleep(100);
				}
				xmlSerializer.endTag(null, "smss");
				xmlSerializer.endDocument();
			} else {
				ToastUtil.show(context, "没有短信，无需备份");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(fos != null && cursor != null){
				cursor.close();
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
	 * 回调（观察者模式）
	 */
	public interface CallBack{
		public void setMax(int max);
		public void setProgress(int index);
	}
}

