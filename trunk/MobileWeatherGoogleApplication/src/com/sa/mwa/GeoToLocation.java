package com.sa.mwa;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;

public class GeoToLocation {
	
//	public String GeoPointToLocation(GeoPoint p){
//		 
//		 
//		 Geocoder geoCoder = new Geocoder(
//	             getBaseContext(), Locale.getDefault());
//	 
//		 try {
//	         List<Address> addresses = geoCoder.getFromLocation(
//	             p.getLatitudeE6()  / 1E6, 
//	             p.getLongitudeE6() / 1E6, 1);
//
//	         String add = "";
//	         if (addresses.size() > 0) 
//	         {
//	             for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
//	                  i++)
//	                add += addresses.get(0).getAddressLine(i) + "\n";
//	             return add;
//	         }
//		 }
//	     catch (IOException e) {                
//	         e.printStackTrace();
//	     }   
//	     return null;
//	 }

}
