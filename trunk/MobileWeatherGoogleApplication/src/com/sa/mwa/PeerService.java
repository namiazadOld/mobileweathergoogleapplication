package com.sa.mwa;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class PeerService extends Service{

	private final RemoteCallbackList<INotifyValueChanged> callBacks = new RemoteCallbackList<INotifyValueChanged>();
	TemperatureSensorListener temperatureSensorListener;
	QueryManager queryManager;
	
	public static final int TEMPERATURE_MESSAGE = 1;
	public static final int QUERY_MESSAGE = 2;
	public static final int CONNECTION_TO_CHAT_SERVER_ESTABLISHED = 3;
	public static final int CONNECTION_TO_CHAT_SERVER_FAILED = 4;

	//PeerService introduces its own interface using this method.
	@Override
	public IBinder onBind(Intent intent) {
		if (IPeerRemoteService.class.getName().equals(intent.getAction()))
			return binder;
		return null;
	}
	
	//handler the messages and broadcast them to all listeners
	private final Handler handler = new Handler(){
		
		@Override
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what)
			{
				case  TEMPERATURE_MESSAGE:
				{
					final int n = callBacks.beginBroadcast();
					for (int i = 0; i < n; i++)
					{
						try
						{
							callBacks.getBroadcastItem(i).temperatureChanged(binder.retrieveTemparature(), binder.retrieveHumidity());
						}
						catch (RemoteException re)
						{
							
						}
					}
					
					callBacks.finishBroadcast();
					sendMessageDelayed(obtainMessage(TEMPERATURE_MESSAGE), 1000);
				}break;
				case CONNECTION_TO_CHAT_SERVER_ESTABLISHED:
				{
					final int n = callBacks.beginBroadcast();
					for (int i = 0; i < n; i++)
					{
						try
						{
							callBacks.getBroadcastItem(i).connectionEstablished();
						}
						catch (RemoteException re)
						{
							
						}
					}
				}break;
				case CONNECTION_TO_CHAT_SERVER_FAILED:
				{
					final int n = callBacks.beginBroadcast();
					for (int i = 0; i < n; i++)
					{
						try
						{
							callBacks.getBroadcastItem(i).connectionFailed();
						}
						catch (RemoteException re)
						{
							
						}
					}
				}break;
			}
		};
		
	};
	
	@Override
	public void onCreate() {
		temperatureSensorListener = new TemperatureSensorListener();
		temperatureSensorListener.connect(this, SENSOR_SERVICE);
		temperatureSensorListener.registerListener(SensorManager.SENSOR_TEMPERATURE, SensorManager.SENSOR_DELAY_NORMAL);
		
		//starting the message sending process
		handler.sendEmptyMessage(TEMPERATURE_MESSAGE);
		
		queryManager = new QueryManager(handler);
		queryManager.connectToChatServer();		
	}

	@Override
	public void onDestroy() {
//		temperatureSensorListener.unregisterListener();     
//		handler.removeMessages(PeerService.TEMPERATURE_MESSAGE);
	}
	
	//implementation of the interface that this service exposes
	public final IPeerRemoteService.Stub binder = new IPeerRemoteService.Stub() {
		@Override
		public void registerCallBack(INotifyValueChanged ntc)
				throws RemoteException {
			if (ntc != null)
				callBacks.register(ntc);
		}

		@Override
		public void unregisterCallBack(INotifyValueChanged ntc)
				throws RemoteException {
			if (ntc != null)
				callBacks.unregister(ntc);
		}

		@Override
		public float retrieveHumidity() throws RemoteException {
			return -1;
		}

		@Override
		public float retrieveTemparature() throws RemoteException {

			float rawTemperature = temperatureSensorListener.getCurrentTemperature();
			return ((int)(rawTemperature * 10))/10;
		}
	};
	
	
}
