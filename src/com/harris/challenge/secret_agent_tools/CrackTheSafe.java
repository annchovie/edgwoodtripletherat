package com.harris.challenge.secret_agent_tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.harris.challenge.brata.BrataLauncherActivity;
import com.harris.challenge.brata.R;


public class CrackTheSafe extends Activity{
    AlertDialog dialog;
    Button buttonStartChallenge, submitButton;
    TextView decodedMessageView, answerView;
    EditText a1Edit, a2Edit, a3Edit, a4Edit, a5Edit;
    
    /**
     * Function used for initializing activity variables, such as 
     * widgets that need to be interactive. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the xml layout before initializing widgets
        setContentView(R.layout.activity_crack_the_safe);
        
        addStart();
        addEdits();
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
    
    /**
     * This function should test all possible 3 permutations of the 5 measured angles on myHash() 
     * The correct answer will be a set of 3 digits in a specific order that gets myHash() to 
     * returns a string matching the 4 character clue. 
     * 
     * @param clue: the clue to check against
     * @param a-e: the measured angles to check
     * @return The computed answer as a formatted string
     */
    public String computeCtsAnswer(String clue, int a, int b, int c, int d, int e)
    {
        String result = "No Solution Found";
        // Check result from myHash(a, b, c) until it returns a string matching the 4 character clue 
        
        int[] angs = {a,b,c,d,e};
        
        for(int i = 0; i<angs.length; i++)
        {
        	for(int j = -5; j<6; j++)
        	{
        		for(int z = 0; z<angs.length; z++)
        		{
        			if(z!=i)
        			{
        				int first = angs[i]+j;
        				for(int q = -5; q<6; q++)
        				{
        					int second = angs[z]+q;
        					for(int f = 0; f<angs.length; f++)
        					{
        						if(f!=i&&f!=z)
        						{
        							for(int t = -5; t<6; t++ )
        							{
        								int third = angs[f]-t;
        								if(myHash(first, second, third).equals(MessageDecoder.decodedMessage)) return ""+first+", "+second+", "+third;
        							}
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
     * Return a hash key given 3 integers.  Used to create a simple hash key 
     * string given 3 integers. The integers are angles in the range 0..90. 
     * No need for modification.
     */
    public static String myHash( int a, int b, int c ) {        
        String XLATE = "BCDGHJKLMNPQRSTVWZbcdghjkmnpqrstvwz";
        int h = (a*127 + b)*127 + c;
        char[] k = new char[4];
        for ( int i = 0; i < 4; i++ ) {
            k[i] = XLATE.charAt(h % XLATE.length());
            h = h / XLATE.length();
        }
        return new String( k );
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
    
    public void addStart()
    {
         decodedMessageView = (TextView) findViewById(R.id.decodedMessageView);
    	 buttonStartChallenge = (Button)findViewById(R.id.buttonCtsStartChallenge);
         buttonStartChallenge.setOnClickListener(new Button.OnClickListener() {
             @Override
             public void onClick(View v) {
                 /**
                  * Either of these functions can be used to send and/or get messages from the MasterServer.
                  * 
                  * MasterServerCommunicator.getInstructionUsingQR(ActivityName.this);
                  * MasterServerCommunicator.sendMessageUsingQR(ActivityName.this, message);
                  * 
                  * In this case the MasterServer will update your progress and send back a clue that must
                  * be decoded. 
                  */
                 MasterServerCommunicator.getInstructionUsingQR(CrackTheSafe.this);
             }
         });
    }
    
    public void addEdits()
    {
    	a1Edit = (EditText) findViewById(R.id.a1Edit);
    	a2Edit = (EditText) findViewById(R.id.a2Edit);
    	a3Edit = (EditText) findViewById(R.id.a3Edit);
    	a4Edit = (EditText) findViewById(R.id.a4Edit);
    	a5Edit = (EditText) findViewById(R.id.a5Edit);
    }
    
    public void addSubmit()
    {
    	answerView = (TextView) findViewById(R.id.answerView);
    	submitButton = (Button) findViewById(R.id.submitButton);
    	submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	int a,b,c,d,e;
            	
            	try { 
            		a = Integer.parseInt(a1Edit.getText().toString());
	            	b = Integer.parseInt(a2Edit.getText().toString());
	            	c = Integer.parseInt(a3Edit.getText().toString());
	            	d = Integer.parseInt(a4Edit.getText().toString());
	            	e = Integer.parseInt(a5Edit.getText().toString());
	            	
	            	answerView.setText("Combo: " + computeCtsAnswer(MessageDecoder.decodedMessage, a, b, c, d, e));
            	}
            	catch(NumberFormatException error ){
            		 Toast.makeText(getApplicationContext(), "Error with Parse Int", Toast.LENGTH_LONG).show();
            	}
            	
                
            }
        });
    }
  
}