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
}
