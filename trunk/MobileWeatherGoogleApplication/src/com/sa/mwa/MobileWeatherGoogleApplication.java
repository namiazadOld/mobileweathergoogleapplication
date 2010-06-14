package com.sa.mwa;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import com.sa.mwa.maplocation.R;
//import com.sa.mwa.maplocation.MapLocation.MapOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MobileWeatherGoogleApplication extends MapActivity {
    MapView mapView; 
    Button okButton;
    MapController mc;
    
   public GeoPoint p = new GeoPoint(0,0) ;
    
   static final int GEOPOINT_SELECTED = 0;
 
    class MapOverlay extends com.google.android.maps.Overlay
    {
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {   
//            //---when user lifts his finger---
//            if (event.getAction() == 1) {                
//                GeoPoint p = mapView.getProjection().fromPixels(
//                    (int) event.getX(),
//                    (int) event.getY());
//                    Toast.makeText(getBaseContext(), 
//                        p.getLatitudeE6() / 1E6 + "," + 
//          p.getLongitudeE6() /1E6 , 
//                        Toast.LENGTH_SHORT).show();
//                   
//            }                            
//            return false;
//        }        
        	
        	 //---when user lifts his finger---
            if (event.getAction() == 1) {                
                p = mapView.getProjection().fromPixels(
                    (int) event.getX(),
                    (int) event.getY());
                String add = "";
 
                Geocoder geoCoder = new Geocoder(
                    getBaseContext(), Locale.getDefault());
              //  List<Address> addresses = null;
//                try {
//                	add = (geoCoder.getFromLocation(
//                        (p.getLatitudeE6() /1E6 ), 
//                        (p.getLongitudeE6()/1E6), 1)).get(0).toString();   
//                   
//                }
//                catch (IOException e) {                
//                    e.printStackTrace();
//                }  
              
//                if (addresses.size() > 0) 
//                {
//                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
//                         i++)
//                       add += addresses.get(0).getAddressLine(i) + "\n";
//                }
                Toast.makeText(getBaseContext(),p.getLatitudeE6() / 1E6 + "," + p.getLongitudeE6() /1E6 , Toast.LENGTH_SHORT).show();
                return true;
                
                
            }
            else                
                return false;
        }   
        
        @Override
        public boolean draw(Canvas canvas, MapView mapView, 
        boolean shadow, long when) 
        {
        	
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);

            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(
                getResources(), R.drawable.pin);            
            canvas.drawBitmap(bmp, screenPts.x-14, screenPts.y-22, null);         
            return true;
        }
        
    }
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.location);
        mapView = (MapView) findViewById(R.id.mapview);
        okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new Button.OnClickListener() { public void onClick (View v){ SetLocation(); }});
       
		mapView.setBuiltInZoomControls(true);
 
//        //...
        mc = mapView.getController();
// 
//        mc.animateTo(p);
//        mc.setZoom(10); 
        
// 
//        //---Add a location marker---
        mc.animateTo(p);
        mc.setZoom(3);
        MapOverlay mapOverlay = new MapOverlay();
       
        List<Overlay> listOfOverlays =null;
     //   
        listOfOverlays= mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);  
      //
      //  mapOverlay.draw(null , mapView, false);
        
        mapView.invalidate();
        
        
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    public void SetLocation(){
    	
    	String x= (p.getLatitudeE6() / 1E6 + "," + p.getLongitudeE6()).toString();
    	Intent intent = new Intent();
    	//intent.EXTRA_CC = "GeoPoint://p;
    	intent.putExtra("pointer",x);
    //	intent.setData(data)
    	onActivityResult(0,0,intent);
        setResult(RESULT_OK, intent);
        finish();
       
        
    	}
    
    public GeoPoint getLocation() {
		return p;
	}
}
