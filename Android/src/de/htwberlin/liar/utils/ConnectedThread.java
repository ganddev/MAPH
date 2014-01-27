package de.htwberlin.liar.utils;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ConnectedThread extends Thread {
	
	private static final String TAG = ConnectedThread.class.getName();
	
	private final InputStream mmInStream;

	private Handler handler;
	
	private int status;
	public ConnectedThread(BluetoothSocket socket, final Handler handler, int status) {
		InputStream tmpIn = null;
		
		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			if (socket != null) {
				
				tmpIn = socket.getInputStream();
			}
		} catch (IOException e) {
			Log.e(TAG, "Socket IOException"+e.getMessage());
		}

		mmInStream = tmpIn;
		this.handler = handler;
		this.status = status;
	}

	public void run() {
		byte[] buffer = new byte[256]; // buffer store for the stream
		int bytes; // bytes returned from read()

		//While true, for listening on incomming bytes.
		while (true) {
			try {
				// Read from the InputStream
				if (mmInStream != null) {
					bytes = mmInStream.read(buffer); 
					// Get number of bytes and message in "buffer"
					Log.d(TAG, "Received " + bytes + " bytes");
					//Send message to handler, which will handle the ui update.
					this.handler.obtainMessage(this.status, bytes, -1, buffer).sendToTarget(); 
				} else {
					Log.w(TAG, "Stream is null");
				}
			} catch (final IOException e) {
				break;
			}
		}
	}

}
