package com.example.myweatherforcast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.Session;

import android.os.Bundle;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	 public final static String EXTRA_MESSAGE = "com.example.myweatherforcast.MESSAGE";	
	 private String weather="";
	 private boolean flag=false;
	 
	 
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);
	     Session.getActiveSession()
	         .onActivityResult(this, requestCode, resultCode, data);
	 }
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	/*	 try {
		        PackageInfo info = getPackageManager().getPackageInfo(
		                "com.example.myweatherforcast", 
		                PackageManager.GET_SIGNATURES);
		        for (Signature signature : info.signatures) {
		            MessageDigest md = MessageDigest.getInstance("SHA");
		            md.update(signature.toByteArray());
		            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		            }
		    } catch (NameNotFoundException e) {

		    } catch (NoSuchAlgorithmException e) {

		    }*/
		
		 Session session = new Session(this);
			if (!session.isClosed()) 
			{
				session.closeAndClearTokenInformation();
			}
//	System.out.println("here!!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void display(){
	//	DynamicFragment frag=(DynamicFragment)getFragmentManager().findFragmentById(R.id.fragment1);
		if(weather.equals("{}")){
			
			FrameLayout fr=(FrameLayout) this.findViewById(R.id.dynframe);
			LayoutParams lparams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					TextView tv=new TextView(this);
					tv.setLayoutParams(lparams);
					tv.setId(100);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
					tv.setTextColor(Color.WHITE);
					tv.setText("Weather information cannot be found!");
					fr.addView(tv);
					getFragmentManager().beginTransaction().replace(R.id.dynframe, new Fragment()).commit();
					flag=true;
						
					return;
		    
		}
		DynamicFragment frag= new DynamicFragment();
	   
	    
	    frag.setJSON(weather);
	    frag.setArguments(getIntent().getExtras());
	    if(flag==false){
	    	getFragmentManager().beginTransaction().add(R.id.dynframe, frag).commit();
	    	flag=true;
		}
	    else
	    {
	    	FrameLayout fr=(FrameLayout) this.findViewById(R.id.dynframe);
	    	if(fr.findViewById(100)!=null){
	    		//TextView tv=(TextView) fr.findViewById(100);
	    		fr.removeAllViews();
	    //		tv.setText("");
	    	}
	    	getFragmentManager().beginTransaction().replace(R.id.dynframe, frag).commit();
	    }
		
	}
	public void setJsonString(String weather){

		this.weather=weather;
		display();
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, MainActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String location = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, location);
		String unit;
		String type="City";
		
		
		if (location.length() == 0) {
	        Toast.makeText(this, "Please enter location!",Toast.LENGTH_LONG).show();
	        return;
	      }                
		if(location.matches("\\w+\\s*\\w*,\\s*\\w+,*\\s*\\w*")==true)
			type="City";
		else if(location.matches("\\d+")==true)
			type="Zip_Code";
		else
		{  Toast.makeText(this, "Invalid location:must include state or country separated by comma.Example:Los Angeles,CA",Toast.LENGTH_LONG).show();
		   return;
		}

		if(type.equals("Zip_Code")){
			
			if(!(location.matches("\\d{5}")))
			  {	
				 Toast.makeText(this, "Invalid Zip code:must be five digits.Example:90089",Toast.LENGTH_LONG).show();
				return;
			  }
		}
		
		RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.radioButton1);
		RadioButton celsiusButton = (RadioButton) findViewById(R.id.radioButton2);
	     
		if(fahrenheitButton.isChecked()==true)
			unit="f";
		else	
			unit="c";
	
		//send req.to servlet
	    //http://localhost:8080/Assign8/MyServlet
		String url;
		try {
			url = "http://cs-server.usc.edu:35139/examples/servlet/MyServlet?location="+URLEncoder.encode(location, "UTF-8")+"&type="+type+"&unit="+unit;
			servletConnectTask task = new servletConnectTask(this);
			task.execute(new String[] { url});
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	    startActivity(intent); 

	}
	
	

}
