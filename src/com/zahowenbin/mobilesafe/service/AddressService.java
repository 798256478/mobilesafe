package com.zahowenbin.mobilesafe.service;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.engine.QueryAddressDao;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {
	
	private TelephonyManager mTelephonyManager;
	private PhoneStateListener mPhoneSteteListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mToastView;
	private int[] mDrawableIndexs;
	private String mAddress;
	private TextView tv_toast;
	private WindowManager mWindowManager;
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	protected int mStartX;
	protected int mStartY;
	protected int mMoveX;
	protected int mMoveY;
	private int mScreenHeight;
	private int mScreenwidth;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneSteteListener = new MyPhoneSteteListener();
		mTelephonyManager.listen(mPhoneSteteListener , PhoneStateListener.LISTEN_CALL_STATE);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
		mScreenwidth = mWindowManager.getDefaultDisplay().getWidth();
	}
	
	class MyPhoneSteteListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if(mWindowManager != null && mToastView != null){
					mWindowManager.removeView(mToastView);
				}
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				toastShow(incomingNumber);
				break;
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void toastShow(String incomingNumber) {
		
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;不允许获取触摸
		//指定吐司的位置
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.x = SpUtil.getInt(this, ConstantView.TOASTlOCATIONX, 0);
        params.y = SpUtil.getInt(this, ConstantView.TOASTlOCATIONY, 0);
        mToastView = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mToastView.findViewById(R.id.tv_toast);
        mDrawableIndexs = new int[]{
        		R.drawable.call_locate_white,
        		R.drawable.call_locate_blue,
        		R.drawable.call_locate_gray,
        		R.drawable.call_locate_green,
        		R.drawable.call_locate_orange
        };
        int toastViewIndex = SpUtil.getInt(this, ConstantView.TOAST_VIEW, 0);
        tv_toast.setBackgroundResource(mDrawableIndexs[toastViewIndex]);
        tv_toast.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mStartX =(int) event.getRawX();
					mStartY =(int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					mMoveX =(int) event.getRawX();
					mMoveY =(int) event.getRawY();
					
					int desX = mMoveX-mStartX;
					int desY = mMoveY-mStartY;
					
					params.x = params.x + desX;
					params.y = params.y + desY;
					
					if(params.x<0){
						params.x = 0;
					}
					if(params.y<0){
						params.y = 0;
					}
					if(params.x > mScreenwidth - tv_toast.getWidth()){
						params.x = mScreenwidth - tv_toast.getWidth();
					}
					if(params.y > mScreenHeight - 22 - tv_toast.getHeight()){
						params.y = mScreenHeight - 22 - tv_toast.getHeight();
					}
					
					mWindowManager.updateViewLayout(mToastView, params);
					mStartX =(int) event.getRawX();
					mStartY =(int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(), ConstantView.TOASTlOCATIONX, params.x);
					SpUtil.putInt(getApplicationContext(), ConstantView.TOASTlOCATIONY, params.y);
					break;
				}
				return true;
			}
		});
        mWindowManager.addView(mToastView, params);
        query(incomingNumber);
	}

	private void query(final String incomingNumber) {
		new Thread(){
			@Override
			public void run() {
				mAddress = QueryAddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);
				super.run();
			}
		}.start();
		
	}

	@Override
	public void onDestroy() {
		if(mTelephonyManager != null && mPhoneSteteListener != null){
			mTelephonyManager.listen(mPhoneSteteListener, PhoneStateListener.LISTEN_NONE);
		}
		super.onDestroy();
	}

}
