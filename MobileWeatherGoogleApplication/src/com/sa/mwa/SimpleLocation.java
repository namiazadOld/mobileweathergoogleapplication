package com.sa.mwa;

public class SimpleLocation {
	
	double longitude, latitude;
	
	public SimpleLocation(double longitude, double latitude)
	{
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
}
