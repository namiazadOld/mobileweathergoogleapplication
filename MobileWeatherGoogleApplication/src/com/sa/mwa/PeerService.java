package com.sa.mwa;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class PeerService extends Service {

	private final RemoteCallbackList<INotifyValueChanged> callBacks = new RemoteCallbackList<INotifyValueChanged>();
	private TemperatureSensorListener temperatureSensorListener;
	private GPSLocationListener locationListener;
	private QueryManager queryManager;

	public static final int TEMPERATURE_MESSAGE = 1;
	public static final int QUERY_MESSAGE = 2;
	public static final int CONNECTION_TO_CHAT_SERVER_ESTABLISHED = 3;
	public static final int CONNECTION_TO_CHAT_SERVER_FAILED = 4;
	public static final int CONNECTION_TO_CHAT_SERVER_PROCESSING = 5;
	public static final int CONNECTION_TO_CHAT_SERVER_DISCONNECTED = 6;
	public static final int QUERY_RESULT = 7;
	public static final int EXCEPTION_OCCURED = 8;
	public static final int SETTING_INVOKED = 9;
	public static final int GPS_LOCATION_CHANGED = 10;
	public static final int QUERY_ANALYZED = 11;

	private SimpleLocation location;

	// PeerService introduces its own interface using this method.
	@Override
	public IBinder onBind(Intent intent) {
		if (IPeerRemoteService.class.getName().equals(intent.getAction()))
			return binder;
		return null;
	}

	// handler the messages and broadcast them to all listeners
	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case TEMPERATURE_MESSAGE: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						if (location != null)
							callBacks.getBroadcastItem(i).temperatureChanged(
									binder.retrieveTemparature(),
									binder.retrieveHumidity(),
									location.getLongitude(),
									location.getLatitude());
					} catch (RemoteException re) {

					}
				}

				callBacks.finishBroadcast();
				sendMessageDelayed(obtainMessage(TEMPERATURE_MESSAGE),
						1000 * EnvironmentVariables
								.getRefreshRate(getBaseContext()));
			}
				break;

			case GPS_LOCATION_CHANGED: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						location = new SimpleLocation(binder.retrieveLongitude(), binder.retrieveLatitude());
						callBacks.getBroadcastItem(i).gpsLocationChanged(
								location.getLongitude(),
								location.getLatitude());
					} catch (RemoteException re) {

					}
				}

				callBacks.finishBroadcast();
				sendMessageDelayed(obtainMessage(GPS_LOCATION_CHANGED), 1000*EnvironmentVariables.getRefreshRate(getBaseContext()));
			}
				break;

			case CONNECTION_TO_CHAT_SERVER_ESTABLISHED: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).connectionEstablished();
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case CONNECTION_TO_CHAT_SERVER_FAILED: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).connectionFailed();
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case CONNECTION_TO_CHAT_SERVER_PROCESSING: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).connectionProcessing();
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case CONNECTION_TO_CHAT_SERVER_DISCONNECTED: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).disconnected();
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case QUERY_MESSAGE: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).queryReceived(
								(String) msg.obj);
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case QUERY_RESULT: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).queryResultReceived();
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case QUERY_ANALYZED: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).queryAnalyzed();
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case EXCEPTION_OCCURED: {
				final int n = callBacks.beginBroadcast();
				for (int i = 0; i < n; i++) {
					try {
						callBacks.getBroadcastItem(i).exceptionOccured(
								(String) msg.obj);
					} catch (RemoteException re) {

					}
				}
			}
				break;
			case SETTING_INVOKED: {
				EnvironmentVariables.settings(getBaseContext(),
						(String) msg.obj, msg.arg1);
			}
				break;
			}
		};

	};

	@Override
	public void onCreate() {
		temperatureSensorListener = new TemperatureSensorListener();
		temperatureSensorListener.connect(this, SENSOR_SERVICE);
		temperatureSensorListener.registerListener(
				SensorManager.SENSOR_TEMPERATURE,
				SensorManager.SENSOR_DELAY_NORMAL);

		// starting the message sending process
		handler.sendEmptyMessage(TEMPERATURE_MESSAGE);
		queryManager = new QueryManager(handler);

		EnvironmentVariables.initalize(getBaseContext());

		// This code should be here, but gps listener does not work on service.
		LocationManager mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationListener = new GPSLocationListener(mlocManager);
		locationListener.register();
		handler.sendEmptyMessage(GPS_LOCATION_CHANGED);
	}

	@Override
	public void onDestroy() {
		temperatureSensorListener.unregisterListener();
		handler.removeMessages(PeerService.TEMPERATURE_MESSAGE);

		locationListener.stopListening();
		super.onDestroy();
	}

	// implementation of the interface that this service exposes
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

			float rawTemperature = temperatureSensorListener
					.getCurrentTemperature();
			return ((int) (rawTemperature * 10)) / 10;
		}

		@Override
		public void establishConnection(String username, String password)
				throws RemoteException {
			queryManager.connectToChatServer(username, password);
		}

		@Override
		public void disconnect() throws RemoteException {
			queryManager.disconnectFromChatServer();

		}

		@Override
		public void findWeather(double longitude, double latitude, int duration, int radius) throws RemoteException {
			try {
				queryManager.findWeather(longitude, latitude, duration, radius);
			} catch (CustomException e) {
				handler.sendMessage(handler.obtainMessage(
						PeerService.EXCEPTION_OCCURED, e.getDefaultMessage()));
			}
		}

		@Override
		public void settings(String deviceName, int refreshRate)
				throws RemoteException {
			handler.sendMessage(handler.obtainMessage(
					PeerService.SETTING_INVOKED, refreshRate, 1, deviceName));
		}

		@Override
		public double retrieveLatitude() throws RemoteException {
			if (locationListener.getLastKnownValue() == null)
				return 0;
			return locationListener.getLastKnownValue().getLatitude();
		}

		@Override
		public double retrieveLongitude() throws RemoteException {
			if (locationListener.getLastKnownValue() == null)
				return 0;
			return locationListener.getLastKnownValue().getLongitude();
		}
	};

}
