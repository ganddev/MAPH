package de.htwberlin.liar;

import android.app.Application;
import android.bluetooth.BluetoothDevice;


public class LiarApplication extends Application {

	private BluetoothDevice mBtDevice;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}
	
	public synchronized BluetoothDevice getBluetoothDevice() {
		return mBtDevice;
	}
}
