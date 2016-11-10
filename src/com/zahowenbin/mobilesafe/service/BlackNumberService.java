package com.zahowenbin.mobilesafe.service;

import java.lang.reflect.Method;
import java.util.Objects;

import com.android.internal.telephony.ITelephony;
import com.zahowenbin.mobilesafe.db.dao.BlackNumberDao;
import com.zahowenbin.mobilesafe.utils.ToastUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Switch;

public class BlackNumberService extends Service {
	
	private InnerSmsReceiver mInnerSmsReceiver;
	private BlackNumberDao mBlackNumberDao;
	private mContentOberver mContentOberver;
	private TelephonyManager telephonyManager;
	private MyPhoneListener myPhoneListener;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
		
		//拦截短信
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");//设置过滤器
		intentFilter.setPriority(1000);//设置优先级
		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, intentFilter);//注册服务
		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myPhoneListener = new MyPhoneListener();
		telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				endCall(incomingNumber);//挂断电话			
				break;
			}
		}
	}
	
	class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				String originatingAddress = smsMessage.getOriginatingAddress();
				
				int mode = mBlackNumberDao.getModeByPhone(originatingAddress);
				if(mode == 1 || mode == 3){
					//拦截短信（android4.4版本失效，使用短信数据库，删除）
					abortBroadcast();
				}
			}
		}
		
	} 
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void endCall(String incomingNumber) {
		int mode = mBlackNumberDao.getModeByPhone(incomingNumber);
		if(mode == 2 || mode == 3){
//			ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
			//ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,需要反射调用
			try {
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				Method method = clazz.getMethod("getService", String.class);
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				iTelephony.endCall();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mContentOberver = new mContentOberver(new Handler(), incomingNumber);
			getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls/"), true, mContentOberver);
		}
	}
	
	class mContentOberver extends ContentObserver{
		private String incomingNumber;
		
		public mContentOberver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			getContentResolver().delete(Uri.parse("content://call_log/calls/"), "number = ?", new String[]{incomingNumber});
		}
		
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mInnerSmsReceiver != null){
			unregisterReceiver(mInnerSmsReceiver);
		}
		if(mContentOberver != null){
			getContentResolver().unregisterContentObserver(mContentOberver);
		}
		
		if(myPhoneListener != null){
			telephonyManager.listen(myPhoneListener, PhoneStateListener.LISTEN_NONE);
		}
		
	}
}
