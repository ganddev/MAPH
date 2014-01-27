package de.htwberlin.liar;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

/**
 * The global {@link Application} class. Contais the {@link BluetoothDevice} device fpr global access.
 */
public class LiarApplication extends Application {

	/** The bluethooth device*/
	private BluetoothDevice mBtDevice;
	
	/**
	 * Call the super method.
	 */
	@Override
	public void onCreate() {
		super.onCreate();	
	}
	
	/**
	 * Gets the bluethooth device.
	 * 
	 * @return the bluethooth device
	 */
	public synchronized BluetoothDevice getBluetoothDevice() {
		return mBtDevice;
	}
}
