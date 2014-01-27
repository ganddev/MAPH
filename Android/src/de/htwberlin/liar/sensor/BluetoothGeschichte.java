package de.htwberlin.liar.sensor;

import android.bluetooth.BluetoothAdapter;

public class BluetoothGeschichte {
	
	public static final int GALVANIC_ADAPTER = 1;
	public static final int EEG_ADAPTER = 2;

	private static BluetoothAdapter btAdapter;
	
	public BluetoothGeschichte(int flag){
		switch(flag){
			case GALVANIC_ADAPTER:  
				btAdapter = BluetoothAdapter.getDefaultAdapter();
				break;
			case EEG_ADAPTER:
				//tgDevice
				break;
			default: throw new IllegalArgumentException("No known flag found!!!");	
		}
	}
	
	
	
}
