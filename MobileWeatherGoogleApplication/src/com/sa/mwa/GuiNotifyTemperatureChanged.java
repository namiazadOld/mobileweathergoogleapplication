package com.sa.mwa;

import android.os.Handler;
import android.os.RemoteException;

public class GuiNotifyTemperatureChanged extends INotifyValueChanged.Stub
{
	private Handler handler;
	public GuiNotifyTemperatureChanged(Handler handler)
	{
		this.handler = handler;
	}
	
	@Override
	public void temperatureChanged(double value, double humidity)
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

}
