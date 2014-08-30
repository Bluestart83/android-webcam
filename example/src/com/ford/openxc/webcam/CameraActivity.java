package com.ford.openxc.webcam;

import android.app.Activity;
import android.os.Bundle;

/*
 * * 1 - Connect the Android-powered device via USB to your computer.
 * 2 - From your SDK platform-tools/ directory, enter adb tcpip 5555 at the command prompt.
 * 3 - Enter adb connect <device-ip-address>:5555 You should now be connected to the Android-powered device and can issue the usual adb commands like adb logcat.
 * 4 - To set your device to listen on USB, enter adb usb.

 */
public class CameraActivity extends Activity {
	private static String TAG = "CameraActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
