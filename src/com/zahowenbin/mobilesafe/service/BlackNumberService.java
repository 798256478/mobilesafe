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
		
		//���ض���
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");//���ù�����
		intentFilter.setPriority(1000);//�������ȼ�
		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, intentFilter);//ע�����
		
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
				endCall(incomingNumber);//�Ҷϵ绰			
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
					//���ض��ţ�android4.4�汾ʧЧ��ʹ�ö������ݿ⣬ɾ����
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
			//ServiceManager����android�Կ���������,���Բ���ȥֱ�ӵ����䷽��,��Ҫ�������
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
