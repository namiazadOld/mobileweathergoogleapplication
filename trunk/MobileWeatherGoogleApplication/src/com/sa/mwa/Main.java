package com.sa.mwa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
	
	//public static String[] results;
	 public int counter=0;
	private GuiNotifyValueChanged guiListener;
	private LogNotifyValueChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	private ConnectionStatus connectionStatus;

	private TextView lbl_temperature, lbl_location, lbl_status, lbl_queryStatus;
	private Button btn_setting, btn_locationFinder, btn_go, btn_result;
	private EditText edt_location, edt_radius, edt_duration;
	private Login dlg_login;
	private Configuration dlg_configuration;
	
	private String currentLocation;
	private int queryProcessedCount = 0;
	
	private void establishServiceConnection() {
		// listeners for peer service
		guiListener = new GuiNotifyValueChanged(handler);
		logListener = new LogNotifyValueChanged(this);
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
		
		lbl_queryStatus = (TextView) findViewById(R.id.lbl_queryStatus);

		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(btn_setting_onClick);
		
		btn_result = (Button) findViewById(R.id.btn_result);
		btn_result.setOnClickListener(btn_result_onClick);

		btn_locationFinder = (Button) findViewById(R.id.btn_locationFinder);
		btn_locationFinder.setOnClickListener(btn_locationFinder_onClick);
		
		btn_go = (Button) findViewById(R.id.btn_go);
		btn_go.setOnClickListener(btn_go_onClick);

		edt_location = (EditText) findViewById(R.id.edt_location);
		edt_location.setEnabled(false);
		
		edt_radius = (EditText) findViewById(R.id.edt_radius);
		edt_duration = (EditText) findViewById(R.id.edt_duration);

		dlg_login = new Login(this);
		dlg_configuration = new Configuration(this);
	}

	private void initializeEnvironmentParameter() {
		connectionStatus = ConnectionStatus.Disconnected;
	}
	
	private Button.OnClickListener btn_go_onClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			//String locationText = edt_location.getText().toString();
			String locationText = currentLocation;
			if (locationText == null || locationText == "")
			{
				Toast.makeText(getBaseContext(), "Location is not selected",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			final int radius;
			try {
				String radiusText = edt_radius.getText().toString();
				radius = Integer.parseInt(radiusText);
			} catch (NumberFormatException nfe) {
				Toast.makeText(getBaseContext(), "Radius should be an integer",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			final int duration;
			try {
				String durationText = edt_duration.getText().toString();
				duration = Integer.parseInt(durationText);
			} catch (NumberFormatException nfe) {
				Toast.makeText(getBaseContext(), "Duration should be an integer",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			String[] parts = locationText.split("\\,");
			final double latitude = Double.parseDouble(parts[0]);
			final double longitude = Double.parseDouble(parts[1]);
			
				
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						peerServiceConnection.getRemoteService().findWeather(longitude, latitude, duration, radius);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
			
			thread.start();
		}
	};

	private Button.OnClickListener btn_setting_onClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					dlg_configuration.Prepare(peerServiceConnection);
					dlg_configuration.show();
				}
			});
		}
	};
	
	private Button.OnClickListener btn_result_onClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			Intent myIntent = new Intent(v.getContext(),Result.class);
//			Bundle bundle = new Bundle();
//			bundle.putStringArray("results", results);
//			myIntent.putExtras(bundle);
			startActivityForResult(myIntent, 0);
		
			
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
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
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
				if(ResultCache.contents!=null)
				ResultCache.contents .add(content);
				
//				results[counter]=content;
				
//				Bundle bundle = new Bundle();
//				Intent myIntent = new Intent(v.getContext(),Result.class);
//				bundle.putString("content",content);
//				myIntent.putExtra("content",content);
//				startActivityForResult(myIntent, 0);
//		    	
//		    	lbl_queryStatus.setText("content");
//		    	onActivityResult(0,0,intent);
//		        setResult(RESULT_OK, intent);
		        
			}
				break;
			case PeerService.QUERY_MESSAGE: {
				lbl_queryStatus.setText("    query received.");
			}
				break;
			case PeerService.QUERY_ANALYZED: {
				queryProcessedCount++;
				lbl_queryStatus.setText("    " + queryProcessedCount + " query processed." );
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
				SimpleLocation location = (SimpleLocation) msg.obj;
				lbl_location.setText((location.longitude + "," + location.latitude).toString());
				
				String x = LocationToLocationName(location);
				if (x == null) {
					lbl_location.setText((location.longitude + "," + location.latitude).toString());
				} else {
					lbl_location.setText(x);
				}
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
				currentLocation = pointer;
			}
		}
	}
	
	public String LocationToLocationName(SimpleLocation l) {

		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

		try {
			List<Address> addresses = geoCoder.getFromLocation(l.getLatitude(),
					l.getLongitude(), 1);

			String add = "";
			if (addresses.size() > 0) {
				for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
					add += addresses.get(0).getAddressLine(i) + "\n";
				return add;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
