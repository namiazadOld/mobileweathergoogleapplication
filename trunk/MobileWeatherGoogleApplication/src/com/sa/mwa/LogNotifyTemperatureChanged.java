package com.sa.mwa;

import android.os.RemoteException;

public class LogNotifyTemperatureChanged  extends INotifyValueChanged.Stub{

	@Override
	public void temperatureChanged(double value, double humidity) throws RemoteException {
	}

	@Override
	public void connectionEstablished() throws RemoteException {
		
	}

	@Override
	public void connectionFailed() throws RemoteException {
		
	}
}
