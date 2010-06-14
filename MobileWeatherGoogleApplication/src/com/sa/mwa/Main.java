package com.sa.mwa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private GuiNotifyTemperatureChanged guiListener;
	private LogNotifyTemperatureChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	private ConnectionStatus connectionStatus;

	private TextView lbl_temperature, lbl_location, lbl_status;
	private Button btn_setting, btn_locationFinder;
	private EditText edt_location;
	private Login dlg_login;
	private Configuration dlg_configuration;

	private void establishServiceConnection() {
		// listeners for peer service
		guiListener = new GuiNotifyTemperatureChanged(handler);
		logListener = new LogNotifyTemperatureChanged();
		List<INotifyValueChanged> listeners = new ArrayList<INotifyValueChanged>();
		listeners.add(guiListener);
		listeners.add(logListener);

		// establishing connection to peer service
		peerServiceConnection = new PeerServiceConnector(listeners);

		// binding connection to service
		bindService(new Intent(IPeerRemoteService.class.getName()),
				peerServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void uiElementInitializing() {
		lbl_temperature = (TextView) findViewById(R.id.lbl_temperature);
		lbl_location = (TextView) findViewById(R.id.lbl_location);

		lbl_status = (TextView) findViewById(R.id.lbl_status);
		lbl_status.setOnClickListener(lbl_status_onClick);

		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(btn_setting_onClick);

		btn_locationFinder = (Button) findViewById(R.id.btn_locationFinder);
		btn_locationFinder.setOnClickListener(btn_locationFinder_onClick);

		edt_location = (EditText) findViewById(R.id.edt_location);
		edt_location.setEnabled(false);

		dlg_login = new Login(this);
		dlg_configuration = new Configuration(this);
	}

	private void initializeEnvironmentParameter() {
		connectionStatus = ConnectionStatus.Disconnected;
	}

	private Button.OnClickListener btn_setting_onClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				peerServiceConnection.getRemoteService().findWeather("DELFT");
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					dlg_configuration.Prepare(peerServiceConnection);
					dlg_configuration.show();
				}
			});
		}
	};

	private Button.OnClickListener btn_locationFinder_onClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent myIntent = new Intent(v.getContext(),
					MobileWeatherGoogleApplication.class);
			startActivityForResult(myIntent, 0);
		}
	};

	private TextView.OnClickListener lbl_status_onClick = new TextView.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (connectionStatus) {
			case Disconnected: {
				handler.post(new Runnable() {

					@Override
					public void run() {

						dlg_login.Prepare(peerServiceConnection);
						dlg_login.show();
					}
				});
			}
				break;
			case Connected: {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							peerServiceConnection.getRemoteService()
									.disconnect();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});

				thread.start();

			}
				break;
			default:
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// preparing connection to peer service
		establishServiceConnection();

		// ui element initialization
		uiElementInitializing();

		// initializing environment parameters
		initializeEnvironmentParameter();

		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		MyLocationListener mlocListener = new MyLocationListener(mlocManager);
		mlocListener.Register();
//		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
//				mlocListener);

		// LocationManager l =(LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);
		// List<String> li = l.getProviders(true);
		// // String li = l.getProviders(true).get(0);
		// for (Iterator<String> iterator = li.iterator(); iterator.hasNext();)
		// {
		// String string = iterator.next();
		// Log.d("gps", string);
		// }
		// if (l.getLastKnownLocation("gps")==null)
		// Log.d("gps", "null");
		// else {
		// String location = (l.getLastKnownLocation("gps").getLatitude() + ","
		// + l.getLastKnownLocation("gps").getLongitude()).toString();
		//        
		//        
		// lbl_location.setText(location);
		// }
	}

	@Override
	protected void onDestroy() {

		if (peerServiceConnection != null)
			unbindService(peerServiceConnection);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PeerService.TEMPERATURE_MESSAGE: {
				Temperature temperature = (Temperature) msg.obj;
				lbl_temperature.setText(Double.valueOf(temperature.getValue())
						.toString());
			}
				break;
			case PeerService.QUERY_RESULT: {
				String content = (String) msg.obj;
				lbl_location.setText(content);
			}
				break;
			case PeerService.QUERY_MESSAGE: {
				String content = (String) msg.obj;
				lbl_location.setText(content);
			}
				break;
			case PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED: {
				connectionStatus = ConnectionStatus.Connected;
				lbl_status.setText("Connected.");
			}
				break;
			case PeerService.CONNECTION_TO_CHAT_SERVER_FAILED: {
				connectionStatus = ConnectionStatus.Disconnected;
				lbl_status.setText("Connection Failed.");
			}
				break;
			case PeerService.CONNECTION_TO_CHAT_SERVER_PROCESSING: {
				connectionStatus = ConnectionStatus.Connecting;
				lbl_status.setText("Connecting...");
			}
				break;
			case PeerService.CONNECTION_TO_CHAT_SERVER_DISCONNECTED: {
				connectionStatus = ConnectionStatus.Disconnected;
				lbl_status.setText("Not Connected!");
			}
				break;
			case PeerService.EXCEPTION_OCCURED: {
				Toast.makeText(getBaseContext(), (String) msg.obj,
						Toast.LENGTH_LONG).show();
			}
				break;
			case PeerService.GPS_LOCATION_CHANGED: {
				Toast.makeText(getBaseContext(), (String) msg.obj,
						Toast.LENGTH_LONG).show();
			}
				break;
			default:
				super.handleMessage(msg);
			}
		};
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String pointer = data.getStringExtra("pointer");
				edt_location.setText(pointer);
			}
		}
	}

	public class MyLocationListener implements LocationListener {
		private Location _lastKnownLocation = null;
		private LocationManager _locationManager = null;

		public MyLocationListener(LocationManager locationManager)
		{
			this._locationManager = locationManager;
		}
		
		@Override
		public void onLocationChanged(Location loc) {
//			String location = (loc.getLatitude() + "," + loc.getLongitude())
//					.toString();
//			handler.sendMessage(handler.obtainMessage(
//					PeerService.GPS_LOCATION_CHANGED, location));
			
			if (loc != null)
			{
				synchronized (this) {
					_lastKnownLocation = loc;
				}
			}
		}

		public Location getLastKnownValue() {
			synchronized (this) {
				return _lastKnownLocation;
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// lbl_location.setText("Gps Disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// lbl_location.setText("Gps Enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}/* End of Class MyLocationListener */
		
		public void Register() {
			_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			
		}
	}
}
