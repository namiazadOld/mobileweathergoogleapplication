package com.sa.mwa;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.os.RemoteException;
import android.text.method.DateTimeKeyListener;

public class LogNotifyValueChanged extends INotifyValueChanged.Stub {

	public static final String FILENAME = "TemperatureHistory";

	private Context context;

	public LogNotifyValueChanged(Context context) {
		this.context = context;
	}

	@Override
	public void connectionEstablished() throws RemoteException {

	}

	@Override
	public void connectionFailed() throws RemoteException {

	}

	@Override
	public void connectionProcessing() throws RemoteException {

	}

	@Override
	public void disconnected() throws RemoteException {

	}

	@Override
	public void queryResultReceived() throws RemoteException {

	}

	@Override
	public void queryReceived(String destination) throws RemoteException {

	}

	@Override
	public void exceptionOccured(String message) throws RemoteException {

	}

	@Override
	public void gpsLocationChanged(double longitude, double latitude)
			throws RemoteException {

	}

	@Override
	public void temperatureChanged(double value, double humidity,
			double longitude, double latitude) throws RemoteException {

		synchronized (this) {
			try {

				Calendar calendar = new GregorianCalendar();
				FileOutputStream fos = context.openFileOutput(FILENAME,
						Context.MODE_APPEND);
				try {
					fos.write((longitude + "-" + latitude + "-"
							+ calendar.get(Calendar.HOUR_OF_DAY) + "-" + value + "\n")
							.toString().getBytes());
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
