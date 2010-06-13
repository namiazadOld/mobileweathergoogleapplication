package com.sa.mwa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jivesoftware.smack.Chat;

import com.google.android.maps.GeoPoint;





import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private GuiNotifyTemperatureChanged guiListener;
	private LogNotifyTemperatureChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	
public	TextView lbl_temperature, lbl_location, lbl_status;
//	Button btn_change;
public	Button  changeConfigurationButton, backButton, changeButton, goButton, locationFinderButton ;
public	TableLayout configurationTable, changeTable,rateTable ;
public	TextView rateText, durationText, minText, maxText, avgText, showRateText, secText, refreshText, searchLocationText;
public	GridView resultGrid;
public	ImageView Image;
public	Drawable sun, snow, cloud;
	int i = 0;
	Chat chat;
	
	private void establishServiceConnection()
	{
		//listeners for peer service
		guiListener = new GuiNotifyTemperatureChanged(handler);
		logListener = new LogNotifyTemperatureChanged();
		List<INotifyValueChanged> listeners = new ArrayList<INotifyValueChanged>();
		listeners.add(guiListener);
		listeners.add(logListener);
		
		//establishing connection to peer service
		peerServiceConnection = new PeerServiceConnector(listeners);
		
		//binding connection to service
		bindService(new Intent(IPeerRemoteService.class.getName()), peerServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void uiElementInitializing()
	{
		lbl_temperature = (TextView) findViewById(R.id.lbl_temperature);
		lbl_location = (TextView) findViewById(R.id.lbl_location);
		lbl_status = (TextView)findViewById(R.id.lbl_status);
		
		
//		btn_change = (Button) findViewById(R.id.btn_change);
//		btn_change.setOnClickListener(btn_change_onClick);
		
		
		changeConfigurationButton = (Button)this.findViewById(R.id.changeConfigurationButton);
        changeConfigurationButton.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ changeConfiguration(); }}); 
        
        backButton = (Button)this.findViewById(R.id.backButton);
        backButton.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ cancelation(); }});
        
        changeButton = (Button)this.findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ change(); }});
        
        goButton = (Button)this.findViewById(R.id.goButton);
        goButton.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ go(); }});
        
        locationFinderButton = (Button)this.findViewById(R.id.locationFinderButton);
        locationFinderButton.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ locationFinder(v); }});
        
        rateText = (TextView)this.findViewById(R.id.rateText);
        durationText = (TextView)this.findViewById(R.id.durationText);
//        minText = (TextView)this.findViewById(R.id.minText);
//        maxText = (TextView)this.findViewById(R.id.maxText);
//        avgText = (TextView)this.findViewById(R.id.avgText);
        showRateText = (TextView)this.findViewById(R.id.showRateText);
        secText = (TextView)this.findViewById(R.id.secText);
        refreshText = (TextView)this.findViewById(R.id.refreshText);
       searchLocationText = (TextView)this.findViewById(R.id.searchLocationText);
        
        
        
        
        configurationTable = (TableLayout)this.findViewById(R.id.configurationTable);
        changeTable = (TableLayout)this.findViewById(R.id.changeTable);
        rateTable = (TableLayout)this.findViewById(R.id.rateTable);
        
        
        changeButton.setVisibility(4);
    	backButton.setVisibility(4);
    	refreshText.setVisibility(4);
    	secText.setVisibility(4);
    	rateText.setVisibility(4);
    	
    	//if)()
		
	}
	

	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		
		//preparing connection to peer service
		establishServiceConnection();
		
		//ui element initialization
		uiElementInitializing();
		
//		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		LocationListener mlocListener = new MyLocationListener();
//		//mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
//		mlocManager.getLastKnownLocation("GPS");
		
//		 LocationManager l =(LocationManager) getSystemService(Context.LOCATION_SERVICE);
//	        List<String> li = l.getProviders(true);
//	        String li = l.getProviders(true).get(0);
//	        for (Iterator<String> iterator = li.iterator(); iterator.hasNext();) {
//	                String string =  iterator.next();
//	                Log.d("gps", string);
////	        }
//	        if (l.getLastKnownLocation(li)==null)
//	            Log.d("gps", "null");   
//	        else {
//	        	String location = (l.getLastKnownLocation(li).getLatitude() + "," + l.getLastKnownLocation(li).getLongitude()).toString();
//	        
//	        
//	        lbl_location.setText(location);
//	        }
	    }
		

	
	
	@Override
	protected void onDestroy() {
		
//		if (peerServiceConnection != null)
//			unbindService(peerServiceConnection);
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what)
			{
				case PeerService.TEMPERATURE_MESSAGE:     
				{
					Temperature temperature = (Temperature) msg.obj;
					lbl_temperature.setText(Double.valueOf(temperature.getValue()).toString());
				}
				break;
				case PeerService.QUERY_MESSAGE:
				{
					String content = (String) msg.obj;
					lbl_location.setText(content);
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED:
				{
					lbl_status.setText("Connected");
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_FAILED:
				{
					lbl_status.setText("Connection Failed");
				}
				break;
				default:
					super.handleMessage(msg);     
			}   
		};
	};
	
	public void changeConfiguration(){

    	changeButton.setVisibility(0);
    	backButton.setVisibility(0);
    	changeConfigurationButton.setVisibility(4);
    	refreshText.setVisibility(0);
    	secText.setVisibility(0);
    	rateText.setVisibility(0);
    	
    	rateText.setText(showRateText.getText());
    	
        
        
    	
    }
  public void  change(){
    	    
    	changeButton.setVisibility(4);
    	backButton.setVisibility(4);
    	refreshText.setVisibility(4);
    	secText.setVisibility(4);
    	rateText.setVisibility(4);
    	changeConfigurationButton.setVisibility(0);
        showRateText.setText(rateText.getText());
    
    }
   
    
  public void cancelation(){
    	  
    	changeButton.setVisibility(4);
    	backButton.setVisibility(4);
    	refreshText.setVisibility(4);
    	secText.setVisibility(4);
    	rateText.setVisibility(4);
    	changeConfigurationButton.setVisibility(0);
    	rateText.setText(showRateText.getText());
   	
	}
    
  public void  go(){
	  goButton.setVisibility(4);
	  locationFinderButton.setVisibility(0);
	  searchLocationText.setText(null);
	  
 	
	  
	  
    	   
    }
  public void  locationFinder(View v){
	 
	 
	  Intent myIntent = new Intent(v.getContext(), MobileWeatherGoogleApplication.class);
	  startActivityForResult(myIntent,0); 

	  
    }

  protected void onActivityResult(int requestCode, int resultCode,
          Intent data) {
      if (requestCode == 0) {
          if (resultCode == RESULT_OK) {
              // A contact was picked.  Here we will just display it
              // to the user.
           //   startActivity(data);
        	String x=  data.getStringExtra("pointer");
        	 searchLocationText.setText(x);
        	  goButton.setVisibility(0);
         	  locationFinderButton.setVisibility(4);
        	  
          }
      }
  }
  
  
  public class MyLocationListener implements LocationListener

  {
	  
	  

  @Override

  public void onLocationChanged(Location loc)

  {

String location = (loc.getLatitude() + "," + loc.getLongitude()).toString();
  
  lbl_location.setText(location);


  }


  @Override

  public void onProviderDisabled(String provider)

  {


	  lbl_location.setText("Gps Disabled");

 

  }


  @Override

  public void onProviderEnabled(String provider)

  {



	  lbl_location.setText("Gps Enabled");

  

  }


  @Override

  public void onStatusChanged(String provider, int status, Bundle extras)

  {


  }

  }/* End of Class MyLocationListener */

 

}
