package org.opencv.samples.bodydetect;

import java.util.Date;

import org.opencv.core.Point;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import geolocationutils.CoordinateConversion;

public class MyLocator implements LocationListener{

	public MyLocator(TextView gpsText){
		textViewGPS = gpsText;
		mcoordinateConverte = new CoordinateConversion();
	}
	
	
    //public static LocationListener locationListener = new LocationListener() {
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {
        	
        }
        public void onProviderDisabled(String provider) {
        	//Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_LONG).show();
        }
		@Override
		/*public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location provider.
			// makeUseOfNewLocation(location);
			Log.i("OpenCV", "LATITUDE="+location.getLatitude() + " LONGITUDE=" + location.getLongitude());
			mposition = location;
		}*/
		
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network/GPS location provider.
			//StringBuilder locationInfo = new StringBuilder("TIME="+location.getTime() +" LAT="+location.getLatitude() + 
				//	" LON=" + location.getLongitude());
			Date t = new Date();
			StringBuilder locationInfo = new StringBuilder("TIME="+t +" LAT="+location.getLatitude() + 
					" LON=" + location.getLongitude());
			
			
			if (location.hasAccuracy()) locationInfo.append(" ACC="+location.getAccuracy());
			if (location.hasSpeed()) locationInfo.append(" SP="+location.getSpeed());
			Log.i(FdActivity.LOCATION_TAG, "Locator, Latitude="+location.getLatitude() + " Longitude=" + location.getLongitude());
			FdActivity.logger.info("Android Locator API, Latitude=" + location.getLatitude() + " Longitude=" + location.getLongitude());
			// textViewGPS.setText(locationInfo);
		
			mposition = location;
			mpositionUTM = mcoordinateConverte.getNorthEastFromLatLong(mposition.getLatitude(), mposition.getLongitude());
		}
      
      
      public Point GetLatLongPoint() {
    	  return new Point(mposition.getLatitude(), mposition.getLongitude());
      }
      
  	public double getNorthing() {
  		return mpositionUTM[1];
  	}
  	
  	public double getEasting() {
  		return mpositionUTM[0];
  	}
      
      
      private static Location mposition;
      private TextView textViewGPS;
      private double[] mpositionUTM;
      private CoordinateConversion mcoordinateConverte;

}
