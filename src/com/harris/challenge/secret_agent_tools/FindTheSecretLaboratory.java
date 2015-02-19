package com.harris.challenge.secret_agent_tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.harris.challenge.brata.R;

public class FindTheSecretLaboratory extends Activity{
	AlertDialog dialog;
	TextView decodedMessageView, labLoc;
	EditText lat1Edit, lon1Edit, lat2Edit, lon2Edit, lat3Edit, lon3Edit, rad1Edit, rad2Edit, rad3Edit, incrementEdit, errorEdit;
	Button submitButton;
	double lat1, lon1, lat2, lon2, lat3, lon3;
	int rad1, rad2, rad3;
	
	double increment = .000001;
	double error = 100;
	
	/**
	 * Function used for initializing activity variables, such as 
	 * widgets that need to be interactive. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the xml layout before initializing widgets
        

        setContentView(R.layout.activity_find_the_secret_laboratory);
        decodedMessageView = (TextView) findViewById(R.id.decodedMessageView);
        
        addLatLon();
        addRads();
        addSubmit();
        
        
    }
    
	/**
	 * If the activity gets onResume update the screen with the decoded MasterServer response data.  This 
	 * event will be triggered after MasterServerCommunicator.getInstructionUsingQR() or 
	 * MasterServerCommunicator.sendMessageUsingQR() has returned with a result.  The decoded string 
	 * MessageDecoder.decodedMessage is a public variable that should be displayed on all challenge 
	 * activities. See MessageDecoder.java for details about messages received from the MasterServer.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// Here update the correct TextView with MessageDecoder.decodedMessage any time the activity is resumed.
		decodedMessageView.setText("Decoded: " + MessageDecoder.decodedMessage);
	}
	
	public void addLatLon()
	{
		lat1Edit = (EditText) findViewById(R.id.lat1Edit);
		lat2Edit = (EditText) findViewById(R.id.lat2Edit);
		lat3Edit = (EditText) findViewById(R.id.lat3Edit);
		lon1Edit = (EditText) findViewById(R.id.lon1Edit);
		lon2Edit = (EditText) findViewById(R.id.lon2Edit);
		lon3Edit = (EditText) findViewById(R.id.lon3Edit);
	}
	
	public void addRads()
	{
		rad1Edit = (EditText) findViewById(R.id.rad1Edit);
		rad2Edit = (EditText) findViewById(R.id.rad2Edit);
		rad3Edit = (EditText) findViewById(R.id.rad3Edit);
	}
	
	public void addSubmit()
	{
		submitButton = (Button) findViewById(R.id.submitButton);
		labLoc = (TextView) findViewById(R.id.labLoc);
		incrementEdit = (EditText) findViewById(R.id.incrementEdit);
		errorEdit =  (EditText) findViewById(R.id.errorEdit);
		
		submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	try{
            		increment = Double.parseDouble(incrementEdit.getText().toString());
            		error = Double.parseDouble(errorEdit.getText().toString());
            	}
            	
            	catch(NumberFormatException e)
            	{
            		increment = .000001;
            		error = 100;
            	}
            	
	            try{
	            	lat1 = 
	            	lat2 = Double.parseDouble(lat2Edit.getText().toString());
	            	lat3 = Double.parseDouble(lat3Edit.getText().toString());
	            	lon1 = Double.parseDouble(lon1Edit.getText().toString());
	            	lon2 = Double.parseDouble(lon2Edit.getText().toString());
	            	lon3 = Double.parseDouble(lon3Edit.getText().toString());
	            	rad1 = Integer.parseInt(rad1Edit.getText().toString());
	            	rad2 = Integer.parseInt(rad2Edit.getText().toString());
	            	rad3 = Integer.parseInt(rad3Edit.getText().toString());
	            	
	            	labLoc.setText("Laboratory: " + findLab(lat1, lon1, lat2, lon2, lat3, lon3, rad1, rad2, rad3));
	            }
	            
	            catch(NumberFormatException e){
	            	
	            	Toast.makeText(getApplicationContext(), "Number Format Error", Toast.LENGTH_LONG).show();
	            }
              
            }
        });
		
	}
	
	public String findLab(double lat1, double lon1, double lat2, double lon2, double lat3, double lon3, int rad1, int rad2, int rad3)
	{
		String result = "Nowhere!!!";
		int[] rads = {rad1, rad2, rad3};
		double[] lats = {lat1, lat2, lat3};
		double[] lons = {lon1, lon2, lon3};
		
		int smallRad = 0;
		
		double smallLat = 0;
		double smallLon = 0;
		
		int cRad1, cRad2, cRad3;
		
		for(int r = 0; r <rads.length; r++)
		{
			if(rads[r]<smallRad)
			{
				smallRad = rads[r];
			}
		}
		
		for(int i = 0; i<rads.length; i++)
		{
			for(int j = 0; j<rads.length; j++)
			{
				cRad1 = rads[i];
				if(cRad1 == smallRad) 
				{
					smallLat = lat1;
					smallLon = lon1;
				}
				
				if(i!=j)
				{
					cRad2 = rads[j];
					if(cRad2 == smallRad) 
					{
						smallLat = lat2;
						smallLon = lon2;
					}
					
					for(int z = 0; z<rads.length; z++)
					{
						if(z!=i && z!=j)
						{
							cRad3 = rads[z];
							if(cRad2 == smallRad) 
							{
								smallLat = lat3;
								smallLon = lon3;
							}
							for(double la = smallRad-smallLat; la<=smallRad+smallLat; la++)
							{
								
								for(double lo = smallRad-smallLon; lo<=smallRad+smallLon; lo+=.000001)
								{
									if(Math.abs(Math.pow((la - lon1), 2.0) + Math.pow((lo - lat1), 2.0) - Math.pow(cRad1, 2.0))<= 100
											&& Math.abs(Math.pow((la - lon2), 2.0) + Math.pow((lo - lat2), 2.0) - Math.pow(cRad2, 2.0))<= 100
											&& Math.abs(Math.pow((la - lon3), 2.0) + Math.pow((lo - lat3), 2.0) - Math.pow(cRad3, 2.0))<= 100)
										return "The laboratory is at"
												+ "\n (  " + la + "  ,  (  " + lo + "  )";
								}
							}
							
						}
					}
				}
			}
			
		}
		
			
		return result;
			
	}
	
	
	
	
	
	
	
	
	
	
	
    
	/**
     * Display a dialog for confirmation before leaving this activity 
     * using the back button.  The back button with exit this activity
     * eliminating any progress made between challenge checkpoints.
     */
	@Override
	public void onBackPressed() {
		// Use the Builder class for convenient dialog construction
		// Create the AlertDialog object and return it
        dialog = new AlertDialog.Builder(this)
        	.setTitle("Close Activity")
        	.setMessage("Any recorded values will be lost!")
        	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                 finish();
              }
    		})
    		.setNegativeButton("No", null)
    		.create();
        dialog.show();
        
        // Temporarily disable the affirmative button as a safeguard
        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    	button.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
            	Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            	button.setEnabled(true);
            }   
        }, 3000);
	}
}