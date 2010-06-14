package com.sa.mwa;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;

public class GPSLocationListener implements LocationListener {
	private Location _lastKnownLocation = null;
	private LocationManager _locationManager = null;
	private Handler handler;
	private PeerServiceConnector connector;

	public GPSLocationListener(LocationManager locationManager, Handler handler, PeerServiceConnector connector) {
		this._locationManager = locationManager;
		this.handler = handler;
		this.connector = connector;
	}
	
	public GPSLocationListener(LocationManager locationManager) {
		this._locationManager = locationManager;
	}

	@Override
	public void onLocationChanged(Location loc) {
		// String location = (loc.getLatitude() + "," + loc.getLongitude())
		// .toString();
		// handler.sendMessage(handler.obtainMessage(
		// PeerService.GPS_LOCATION_CHANGED, location));

		if (loc != null) {
			synchronized (this) {
				_lastKnownLocation = loc;
			}
		}
		
//		if (connector != null && connector.getRemoteService() != null)
//			try {
//				connector.getRemoteService().cacheLocation(loc.getLongitude(), loc.getLatitude());
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
		if (handler != null)
			handler.sendMessage(handler.obtainMessage(PeerService.GPS_LOCATION_CHANGED, new SimpleLocation(loc.getLongitude(), loc.getLatitude())));
		
		
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

	public void register() {
		_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this);
		_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				0, 0, this);

	}
	
	public void stopListening()
	{
		if (_locationManager != null)
			_locationManager.removeUpdates(this);
	}
}