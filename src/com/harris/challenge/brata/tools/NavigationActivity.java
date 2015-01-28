/*------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *------------------------------------------------------------------------------
 */

package com.harris.challenge.brata.tools;

import com.harris.challenge.brata.R;
import com.harris.challenge.brata.framework.GPSService;
import com.harris.challenge.brata.framework.GPSService.GPSServiceListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Andrew
 * 
 */
public class NavigationActivity extends Activity implements OnClickListener, GPSServiceListener{

	TextView currentLocation, destination, distance, bearings;
	EditText destinationLongitude,destinationLatitude;
	Button navigate;
	ImageView arrow;
	public static double lat1=0;
	public static double lon1=0;
	public static double lat2=0;
	public static double lon2=0;
	public static double latB=0;
	
    private GPSService gpsService = null;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     * 
     * This will be called whenever this activity is created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This applies the layout specified in
        // res->layout->activity_request_clue.xml
        // to our Activity. We can now find views within that layout and
        // manipulate them. Don't try to call findViewById() before this!
        setContentView(R.layout.activity_navigation);
        
        currentLocation = (TextView) findViewById(R.id.currentLocation);
		destination = (TextView) findViewById(R.id.destination);
		distance = (TextView) findViewById(R.id.distance);
		bearings = (TextView)findViewById(R.id.bearingsTitle);
		destinationLongitude = (EditText) findViewById(R.id.destinationLongitude);
		destinationLatitude = (EditText) findViewById(R.id.destinationLatitude);
		navigate = (Button)findViewById(R.id.navigate);
		arrow = (ImageView) findViewById(R.id.arrow);

        // The GPS Service runs independently of the applications 
        // activities.  The bindService() function allows this 
        // activity to interact to the GPS service to retrieve location  
        // information. 
        Intent serviceIntent = new Intent(this, GPSService.class);
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * This activity returns to the foreground.  The activity becomes active.
     */
    @Override
    protected void onResume() {
        // Rebind activity to gps service
        Intent mServiceIntent = new Intent(this, GPSService.class);
        bindService(mServiceIntent, this, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    /**
     * The activity is finished running
     */
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public void onClick(View view) {
    }

    /**
     * Called when the connection with the GPS service is established
     * 
     * @param ComponentName
     * @param service binder
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        // Because we have bound to an explicit
        // service that is running in our own process, we can
        // cast its IBinder to a concrete class and directly access it.
        Toast.makeText(getBaseContext(), "GPS service connected", Toast.LENGTH_SHORT).show();
        GPSService.LocalBinder gpsBinder = (GPSService.LocalBinder) binder;
        GPSService gpsService = gpsBinder.getService();
        gpsService.addGPSListener(NavigationActivity.this);
    }

    /**
     * Called when the connection with the GPS service disconnects
     * 
     * @param classname
     */
    @Override
    public void onServiceDisconnected(ComponentName className) {
        if(gpsService != null) 
        {
            gpsService.removeGPSListener(NavigationActivity.this);
        }
    }

    /**
     * Takes action on each new location update
     * 
     * @param location 
     */
    @Override
    public void onLocationChanged(Location location) {
    	double latitude = location.getLatitude();
	    double longitude = location.getLongitude();
		lat1 = latitude;
		lon1 = longitude;

        // Use Toast to display a messages briefly. It is a great tool
        // for debugging.  It is useful for determining the details of
        // an event such as a button press as well as when it occurred.  
        // However Android's Log tool is a better alternative for
        // debugging multiple recurring events and application crashes. 
        // Set this variable to true to view the GPS toast message.
        boolean GPS_show_updates = false;

        // The default GPS update time is 3 seconds.  You can set this to a 
        // different time by changing GPSUpdateDelay in GPSService.java.  
        // You should disable this toast message if that time is less than 2 secs.
        if(GPS_show_updates)
        {
            Toast.makeText(getBaseContext(), 
                "Latitude: "+latitude+
                "; Longitude: "+longitude,
                Toast.LENGTH_SHORT).show();
        }

        //TODO:  Use the location coordinates to do something useful
        
      //Displaying current location
      	currentLocation.setText("Current Location:\n (" + latitude +", " + longitude + ")");

      //Calculating and displaying distance 
      	haversine(lat1, lon1, lat2, lon2);
      		
      //Calculating and displaying bearings
      	double longDiff = lon2-lon1;
      	double y = Math.sin(longDiff)*Math.cos(lat2);
      	double x = Math.cos(lat1)*Math.sin(lat2) -  Math.sin(lat1) 
      				   * Math.cos(lat2)*Math.cos(longDiff);
      	double bear = Math.toDegrees((Math.atan2(y, x))+360)%360;
      	bearings.setText(""+(int)bear+(char)0x00B0);
      	//TODO change API arrow.setRotation((float)Math.abs(360-bear));

        new Handler().post(new Runnable() {
            public void run() {
            }
        });
    }
	public void onButtonClick(View view)
	{
		lon2 = Double.parseDouble(destinationLongitude.getText().toString());
		lat2 = Double.parseDouble(destinationLatitude.getText().toString());
		destination.setText("Destination: (" + lat2 + " ,"+lon2+")");
		destination.setVisibility(1);
		distance.setVisibility(1);
		bearings.setVisibility(1);
		arrow.setVisibility(1);
	}
	
       public void haversine(double la1, double lo1, double la2, double lo2)
       {
        final int R = 20925524; // ft 6371 meters = Radius of the earth
        Double latA = la1;
        Double lonA = lo1;
        Double latB = la2;
        Double lonB = lo2;
        Double latDistance = toRad(latB-latA);
        Double lonDistance = toRad(lonB-lonA);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
                   Math.cos(toRad(latA)) * Math.cos(toRad(latB)) * 
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c;
        distance.setText("You are "+ Math.round(d*100)/100 + " feet from your destination.");
 
    }
     
    public static double toRad(double value) {
        return value * Math.PI / 180;
    }
}
