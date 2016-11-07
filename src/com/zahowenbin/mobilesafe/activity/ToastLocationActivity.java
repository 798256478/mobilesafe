package com.zahowenbin.mobilesafe.activity;

import com.zahowenbin.mobilesafe.R;
import com.zahowenbin.mobilesafe.utils.ConstantView;
import com.zahowenbin.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {
	private ImageView iv_drag;
	private int mStartY;
	private int mStartX;
	private int mMoveY;
	private int mMoveX;
	private WindowManager mWindowManager;
	private int mScreenHeight;
	private int mScreenwidth;
	private Button btn_toast_top;
	private Button btn_toast_bottom;
	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		btn_toast_top = (Button) findViewById(R.id.btn_toast_top);
		btn_toast_bottom = (Button) findViewById(R.id.btn_toast_bottom);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
		mScreenwidth = mWindowManager.getDefaultDisplay().getWidth();
		int toast_locationX = SpUtil.getInt(this, ConstantView.TOASTlOCATIONX, 0);
		int toast_locationY = SpUtil.getInt(this, ConstantView.TOASTlOCATIONY, 0);
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = toast_locationX;
		layoutParams.topMargin = toast_locationY;
		if((toast_locationY + iv_drag.getHeight()) > (mScreenHeight-22)/2){
			btn_toast_bottom.setVisibility(View.INVISIBLE);
			btn_toast_top.setVisibility(View.VISIBLE);
		} else {
			btn_toast_bottom.setVisibility(View.VISIBLE);
			btn_toast_top.setVisibility(View.INVISIBLE);
		}
		iv_drag.setLayoutParams(layoutParams);
		iv_drag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if(mHits[mHits.length-1] - mHits[0] < 500){
					iv_drag.layout((mScreenwidth/2)-iv_drag.getWidth()/2, ((mScreenHeight-22)/2)-iv_drag.getHeight()/2, (mScreenwidth/2)+iv_drag.getWidth()/2, ((mScreenHeight-22)/2)+iv_drag.getHeight()/2);
					SpUtil.putInt(getApplicationContext(), ConstantView.TOASTlOCATIONX, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantView.TOASTlOCATIONY, iv_drag.getTop());
				}
			}
		});
		iv_drag.setOnTouchListener(new OnTouchListener() {

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
					
					
					if((iv_drag.getTop() + desY + iv_drag.getHeight()) > (mScreenHeight-22)/2){
						btn_toast_bottom.setVisibility(View.INVISIBLE);
						btn_toast_top.setVisibility(View.VISIBLE);
					} else {
						btn_toast_bottom.setVisibility(View.VISIBLE);
						btn_toast_top.setVisibility(View.INVISIBLE);
					}
					
					if(iv_drag.getLeft() + desX < 0 || iv_drag.getTop() + desY < 0 || iv_drag.getRight() + desX > mScreenwidth || iv_drag.getBottom() + desY > mScreenHeight-22){
						return true;
					}
					iv_drag.layout(iv_drag.getLeft() + desX, iv_drag.getTop() + desY, iv_drag.getRight() + desX, iv_drag.getBottom() + desY);
					mStartX =(int) event.getRawX();
					mStartY =(int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					SpUtil.putInt(getApplicationContext(), ConstantView.TOASTlOCATIONX, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantView.TOASTlOCATIONY, iv_drag.getTop());
					break;
				}
				return false;
			}
		});
	}
}
