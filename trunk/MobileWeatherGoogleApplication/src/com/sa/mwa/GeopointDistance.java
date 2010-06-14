package com.sa.mwa;

import com.google.android.maps.GeoPoint;

public class GeopointDistance {

	public boolean IsResult(double latetude1, double longitude1,
			double latetude2, double longitude2, int radius) {

		double deltaLatitude, deltaLongitude, latitudeCircumference, resultX, resultY;

		deltaLatitude = latetude2 - latetude1;
		deltaLongitude = longitude2 - longitude1;
		latitudeCircumference = 40075160 * Math.cos(radians(latetude1));
		resultX = (deltaLongitude * latitudeCircumference) / 360;
		resultY = (deltaLatitude * 40008000) / 360;
		double distance = Math.sqrt((resultX) * (resultX) + (resultY)
				* (resultY));
		if (distance <= radius)
			return true;
		else
			return false;
	}

	public double radians(double degree) {
		double radians = (degree * Math.PI) / 180;
		return radians;
	}

}
