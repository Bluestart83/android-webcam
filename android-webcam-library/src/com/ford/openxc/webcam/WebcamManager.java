package com.ford.openxc.webcam;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WebcamManager extends Service {

	private static final String TAG = "WebcamManager";

	public static final String PARAM_CAMERA = "camera";

	private IBinder mBinder = new WebcamBinder();
	private Webcam mWebcam;

	public class WebcamBinder extends Binder {
		public WebcamManager getService() {
			return WebcamManager.this;
		}
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		Log.i(TAG, "Service starting");
		//initCamera("/dev/video3");
	}

	/*
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.e(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}*/

	public void initCamera(String camera) {
		Log.d(TAG, "initCamera="+camera);
		if(mWebcam != null) {
			mWebcam.stop();
		}
		mWebcam = new NativeWebcam(camera);//"/dev/video3"
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service being destroyed");
		mWebcam.stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service binding in response to " + intent);
		return mBinder;
	}

	public Bitmap getFrame() {
		if(!mWebcam.isAttached()) {
			stopSelf();
		}
		return mWebcam.getFrame();
	}
}
