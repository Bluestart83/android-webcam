package com.ford.openxc.webcam;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class NativeWebcam implements Webcam {

    private static String TAG = "NativeWebcam";
    private static final int DEFAULT_IMAGE_WIDTH = 640;
    private static final int DEFAULT_IMAGE_HEIGHT = 480;

    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;


    private native int startCamera(String deviceName, int width, int height);
    private native void processCamera();
    private native boolean cameraAttached();
    private native void stopCamera();
    private native void loadNextFrame(Bitmap bitmap);

    static {
        System.loadLibrary("webcam");
    }

    public NativeWebcam(String deviceName, int width, int height) {
        mWidth = width;
        mHeight = height;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        connect(deviceName, mWidth, mHeight);
    }

    public NativeWebcam(String deviceName) {
        this(deviceName, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    }

    private void connect(String deviceName, int width, int height) {
        boolean deviceReady = true;

        rootDeviceFile(deviceName);
        File deviceFile = new File(deviceName);
        if(deviceFile.exists()) {
            if(!deviceFile.canRead()) {
                Log.w(TAG, "Insufficient permissions on " + deviceName +
                        " -- does the app have the CAMERA permission?");
                deviceReady = false;
            }
        } else {
            Log.e(TAG, deviceName + " does not exist");
            deviceReady = false;
        }

        if(deviceReady) {
            Log.i(TAG, "Preparing camera with device name " + deviceName);
            startCamera(deviceName, width, height);
        }
    }

    private static void rootDeviceFile(String deviceName) {
    	//String command = String.format("su -c \"chmod 666 %s\"", deviceName);
    	String command = String.format("chmod 666 %s", deviceName);
    	int ret = runCommand(command, true);
    	if (ret !=0) {
    		Log.e(TAG, "rootDeviceFile: error");
    	}
    	else {
    		Log.i(TAG, "rootDeviceFile: success");
    	}
    }
    
    private static int runCommand(String command, boolean bAsRoot) {
    	Log.d(TAG, "runCommand: " + command);
	    int ret = -10;
	    try{
	    	Process proc;
	    	if(bAsRoot) {
	    		//"su -c \"chmod 660 /dev/video3\" => does not detect chmod as cmd => fix
	    		proc = Runtime.getRuntime().exec(new String[]{"su","-c",command});
	    	}
	    	else {
	    		proc = Runtime.getRuntime().exec(command);
	    	}
	    	proc.waitFor();// wait finished
	    	ret = proc.exitValue();  
	    	
	    	String str = "";
	    	String line = null;
	    	BufferedReader stdErr= new BufferedReader(new InputStreamReader(proc.getErrorStream()));
	        while((line = stdErr.readLine())!=null){
	        	str += line;
	        }
	        if(str.length() != 0) {
	        	Log.e(TAG, str);
	        }
	        
	        str = "";
	        line = null;
	        BufferedReader stdOut= new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        while((line = stdOut.readLine())!=null){
	        	str += line;
	        }
	        if(str.length() != 0) {
	        	Log.d(TAG, str);
	        }
	    }
	    catch(IOException e){
	    	Log.w(TAG, "Error rooting: "+e.getMessage());
	    }
	    catch (InterruptedException e) {
	    	Log.w(TAG, "Error rooting: "+e.getMessage());
		}
	    return ret;
    }
    
    public Bitmap getFrame() {
        loadNextFrame(mBitmap);
        return mBitmap;
    }

    public void stop() {
        stopCamera();
    }

    public boolean isAttached() {
        return cameraAttached();
    }
}
