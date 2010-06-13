package com.sa.mwa;

import com.google.android.maps.GeoPoint;

public class GeopointDistance {
	
	public boolean IsResult(GeoPoint p1, GeoPoint p2, int radius){
		
		 double deltaLatitude , deltaLongitude, latitudeCircumference, resultX , resultY;

	
		  
		    deltaLatitude = p2.getLatitudeE6() - p1.getLatitudeE6();
		    deltaLongitude = p2.getLongitudeE6() - p2.getLatitudeE6();
		    latitudeCircumference = 40075160 * Math.cos(radians(p1.getLatitudeE6()));
		    resultX = (deltaLongitude * latitudeCircumference) / 360;
		    resultY = (deltaLatitude * 40008000) / 360;
		    double distance = Math.sqrt((resultX)*(resultX)+(resultY)*(resultY));
		    if(distance<= radius)
		return true;
		    else
		    	return false;
	}
	
	public double radians(double degree){
		double radians = (degree* Math.PI)/180;
		return radians;
	}

}
