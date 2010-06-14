package com.sa.mwa;

import android.os.Handler;
import android.os.RemoteException;

public class GuiNotifyValueChanged extends INotifyValueChanged.Stub
{
	private Handler handler;
	public GuiNotifyValueChanged(Handler handler)
	{
		this.handler = handler;
	}
	
	@Override
	public void temperatureChanged(double value, double humidity, double longitude, double latitude)
			throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.TEMPERATURE_MESSAGE, new Temperature(value, humidity)));
	}

	@Override
	public void connectionEstablished() throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED));
	}

	@Override
	public void connectionFailed() throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_FAILED));
	}

	@Override
	public void connectionProcessing() throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_PROCESSING));
		
	}

	@Override
	public void disconnected() throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_DISCONNECTED));
	}

	@Override
	public void queryResultReceived() throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.QUERY_RESULT));
	}

	@Override
	public void queryReceived(String destination) throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.QUERY_MESSAGE, destination));
	}

	@Override
	public void exceptionOccured(String message) throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.EXCEPTION_OCCURED, message));
	}

	@Override
	public void gpsLocationChanged(double longitude, double latitude)
			throws RemoteException {
		handler.sendMessage(handler.obtainMessage(PeerService.GPS_LOCATION_CHANGED, new SimpleLocation(longitude, latitude)));		
	}

}
