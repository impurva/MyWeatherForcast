package com.example.myweatherforcast;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class DynamicFragment extends Fragment {
	
	JSONObject json;

	Bitmap b=null;
	String city,state,country,URL,text,temp,unit,link,picture,name,caption,description,feed;
	JSONArray forecast;
	 Dialog dialog;
	 Bundle savedInstanceState;
	 private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	 
	  @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		 Session.getActiveSession()
         .onActivityResult(getActivity(), requestCode, resultCode, data);
	}

	private void publishFeedDialog() {
	        Bundle params = new Bundle();
	        params.putString("caption", caption);
	        params.putString("name", name);
	        params.putString("description",description);
	        params.putString("link",link);
	        params.putString("picture", picture);
	        
	        WebDialog feedDialog = (
	                new WebDialog.FeedDialogBuilder(getActivity(),
	                        Session.getActiveSession(),
	                        params))
	                        .setOnCompleteListener(new OnCompleteListener() {

	                            //  @Override
	                            public void onComplete(Bundle values,
	                                    FacebookException error) {
	                                dialog.dismiss();

	                                if (error == null) {
	                                    // When the story is posted, echo the success
	                                    // and the post Id.
	                                    final String postId = values.getString("post_id");
	                                    if (postId != null) {
	                                        Toast.makeText(getActivity(),
	                                                "Posted Weather Information to the Wall",
	                                                Toast.LENGTH_SHORT).show();
	                                    } else {
	                                        // User clicked the Cancel button
	                                        Toast.makeText(getActivity().getApplicationContext(), 
	                                                "Publish cancelled", 
	                                                Toast.LENGTH_SHORT).show();
	                                    }
	                                } else if (error instanceof FacebookOperationCanceledException) {
	                                    // User clicked the "x" button
	                                    Toast.makeText(getActivity().getApplicationContext(), 
	                                            "Publish cancelled", 
	                                            Toast.LENGTH_SHORT).show();
	                                } else {
	                                    // Generic, ex: network error
	                                    Toast.makeText(getActivity().getApplicationContext(), 
	                                            "Error posting story", 
	                                            Toast.LENGTH_SHORT).show();
	                                }
	                            }

	                        })
	                        .build();
	        feedDialog.show();
	    }
	 private void logintoFacebook()
		{
			Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
			Session session = Session.getActiveSession();
			if (session == null) {
				if (savedInstanceState != null) {
					session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
				}
				if (session == null) {
					session = new Session(getActivity());
				}
				Session.setActiveSession(session);
				if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
					session.openForRead(new Session.OpenRequest(getActivity()).setCallback(statusCallback));
				}
			}
			if (!session.isOpened() && !session.isClosed()) {
				session.openForRead(new Session.OpenRequest(getActivity()).setCallback(statusCallback));
			} else {
				Session.openActiveSession(getActivity(), true, statusCallback);
			}
		}
		
		private class SessionStatusCallback implements Session.StatusCallback {
			@Override
			public void call(Session session, SessionState state, Exception exception) 
			{
				if (session.isOpened()) 
				{
					//FeedDialogtoFB();
					//System.out.println("hvjk");
					
				}
			}
		}

	
	private class load_image extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... url) {
	        try {
	            URL aURL = new URL(url[0]);
	            URLConnection conn = aURL.openConnection();
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            BufferedInputStream bis = new BufferedInputStream(is);
	            b = BitmapFactory.decodeStream(bis);
	            bis.close();
	            is.close();
	        } catch (IOException e) {
	            Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
	        }
			return ""; 
	        
		}
		
	}

	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	//	super.onCreate(savedStateInstance);
		View view = inflater.inflate(R.layout.dynlayout,container, false);
		TextView tv;
		
		try {
			
			city=json.getJSONObject("location").getString("city");
			state=json.getJSONObject("location").getString("region");
			country=json.getJSONObject("location").getString("country");
			URL=json.getString("img");
			text=json.getJSONObject("condition").getString("text");
			temp=json.getJSONObject("condition").getString("temp");
			unit=json.getJSONObject("units").getString("temperature");
			temp+=Html.fromHtml("&deg;"+unit);
			forecast=json.getJSONArray("forecast");
			for(int i=0;i<5;i++){
				String temp=forecast.getJSONObject(i).getString("high")+Html.fromHtml("&deg;")+unit.toUpperCase();
				forecast.getJSONObject(i).put("high", temp);
				temp=forecast.getJSONObject(i).getString("low")+Html.fromHtml("&deg;")+unit.toUpperCase();
				forecast.getJSONObject(i).put("low", temp);
			}
			link=json.getString("link");
			picture=json.getString("img");
			name=city+","+state+","+country;
			caption="Weather Forecast for "+city;
			description="Temperature is "+temp;
			feed=json.getString("feed");

	//		picture,name,caption,description,feed
		
		tv=(TextView)view.findViewById(R.id.citytextView);
	    tv.setText("");
	    tv.setText(city);
	    
	    tv=(TextView)view.findViewById(R.id.location);
	    tv.setText("");
	    tv.setText(state+","+country);
	    
	 
	    load_image l=new load_image();
		try {
			l.execute(new String[] {URL}).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    ImageView iv=(ImageView)view.findViewById(R.id.imageView);
	    iv.getLayoutParams().height=150;
        iv.getLayoutParams().width=150;
        iv.setImageBitmap(b);
        
	    tv=(TextView)view.findViewById(R.id.text);
	    tv.setText("");
	    tv.setText(text);
	    
	   
	    tv=(TextView)view.findViewById(R.id.temp);
	    tv.setText("");
	    tv.setText(temp);
	    
	  
	    TableLayout tl = (TableLayout) view.findViewById(R.id.forecasttable);
	    
	    
	    TableRow row = (TableRow) view.findViewById(R.id.tableRow1);
    	TextView day = (TextView)row.findViewById(R.id.day1);
    	day.setText(forecast.getJSONObject(0).getString("day"));
    	TextView weather = (TextView)row.findViewById(R.id.weather1);
    	weather.setText(forecast.getJSONObject(0).getString("text"));
    	TextView high = (TextView)row.findViewById(R.id.high1);
    	high.setText(forecast.getJSONObject(0).getString("high"));
    	TextView low = (TextView)row.findViewById(R.id.low1);
    	low.setText(forecast.getJSONObject(0).getString("low"));
    	
    	row = (TableRow) view.findViewById(R.id.tableRow2);
    	day = (TextView)row.findViewById(R.id.day2);
     	day.setText(forecast.getJSONObject(1).getString("day"));
     	weather = (TextView)row.findViewById(R.id.weather2);
     	weather.setText(forecast.getJSONObject(1).getString("text"));
     	high = (TextView)row.findViewById(R.id.high2);
     	high.setText(forecast.getJSONObject(1).getString("high"));
     	low = (TextView)row.findViewById(R.id.low2);
     	low.setText(forecast.getJSONObject(1).getString("low"));
     	
     	row = (TableRow) view.findViewById(R.id.tableRow3);
    	day = (TextView)row.findViewById(R.id.day3);
     	day.setText(forecast.getJSONObject(2).getString("day"));
     	weather = (TextView)row.findViewById(R.id.weather3);
     	weather.setText(forecast.getJSONObject(2).getString("text"));
     	high = (TextView)row.findViewById(R.id.high3);
     	high.setText(forecast.getJSONObject(2).getString("high"));
     	low = (TextView)row.findViewById(R.id.low3);
     	low.setText(forecast.getJSONObject(2).getString("low"));
     	
     	row = (TableRow) view.findViewById(R.id.tableRow4);
    	day = (TextView)row.findViewById(R.id.day4);
     	day.setText(forecast.getJSONObject(3).getString("day"));
     	weather = (TextView)row.findViewById(R.id.weather4);
     	weather.setText(forecast.getJSONObject(3).getString("text"));
     	high = (TextView)row.findViewById(R.id.high4);
     	high.setText(forecast.getJSONObject(3).getString("high"));
     	low = (TextView)row.findViewById(R.id.low4);
     	low.setText(forecast.getJSONObject(3).getString("low"));
     	
     	row = (TableRow) view.findViewById(R.id.tableRow5);
    	day = (TextView)row.findViewById(R.id.day5);
     	day.setText(forecast.getJSONObject(4).getString("day"));
     	weather = (TextView)row.findViewById(R.id.weather5);
     	weather.setText(forecast.getJSONObject(4).getString("text"));
     	high = (TextView)row.findViewById(R.id.high5);
     	high.setText(forecast.getJSONObject(4).getString("high"));
     	low = (TextView)row.findViewById(R.id.low5);
     	low.setText(forecast.getJSONObject(4).getString("low"));
     	
     	tv=(TextView)view.findViewById(R.id.textcurr);
 	    tv.setText("Show Current Weather");
 	    tv.setOnClickListener(new View.OnClickListener() {
			
 	   	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				    dialog = new Dialog(getActivity());
	                dialog.setContentView(R.layout.dialogbox);
	                dialog.setTitle("Post to Facebook");
	                dialog.findViewById(R.id.postcurr).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
				//			logintoFacebook();
							Session.openActiveSession(getActivity(), true,new Session.StatusCallback() {
								
								@Override
								public void call(Session session, SessionState state, Exception exception) {
									// TODO Auto-generated method stub
								System.out.println("here");	
								  if (state.isOpened() || session.isOpened()) {
	                                    
	                                    publishFeedDialog(); 
	                                    
	                                }
								}
							});
							
						}
					});
	                dialog.findViewById(R.id.postcurrCancel).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
	                dialog.show();
				
			}
		}); 
 	    		   	
 	    
 	   tv=(TextView)view.findViewById(R.id.textfore);
	    tv.setText("Show Weather Forecast");
	    tv.setOnClickListener(new View.OnClickListener() {
			
	 	   	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				    dialog = new Dialog(getActivity());
	                dialog.setContentView(R.layout.dialogbox);
	                Button b= (Button)dialog.findViewById(R.id.postcurr);
	                b.setText("Post Weather Forecast");
	                dialog.setTitle("Post to Facebook");
	                dialog.findViewById(R.id.postcurr).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
				//			logintoFacebook();
							Session.openActiveSession(getActivity(), true,new Session.StatusCallback() {
								
								@Override
								public void call(Session session, SessionState state, Exception exception) {
									// TODO Auto-generated method stub
								System.out.println("here");	
								  if (state.isOpened() || session.isOpened()) {
	                                    
									   caption="Weather Forecast for "+city;
									   picture="http://www-scf.usc.edu/~csci571/2013Fall/hw8/weather.jpg";
									   description="";
									   for(int i=0;i<5;i++){
										 
										try {
											description = description+ forecast.getJSONObject(i).getString("day")+ ":"+ forecast.getJSONObject(i).getString("text")+ ","
													+ forecast.getJSONObject(i).getString("high")+ Html.fromHtml("&deg;")+ "/"+ forecast.getJSONObject(i).getString("low")
													+ Html.fromHtml("&deg;")+ unit+"\n";
															} catch (JSONException e) {
																// TODO
																// Auto-generated
																// catch block
																e.printStackTrace();
															}
										 }
	                                    publishFeedDialog(); 
	                                    
	                                }
								}
							});
							
						}
					});
	                dialog.findViewById(R.id.postcurrCancel).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
	                dialog.show();
				
			}
		}); 
 	    
     	
     /*	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     	builder.setTitle("Post to facebook");
    	builder.setNegativeButton("Sample Music", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//Uri myUri = Uri.parse(url);
			
				
			}
		});
	
    	builder.setPositiveButton("Facebook", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//Session session = Session.getActiveSession();
			
				//loginToFacebook();
			}
		});
		builder.show();*/
  
    	
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	public void setJSON(String sjson){
		
		try {
			
			JSONObject mainObject = new JSONObject(sjson);
			this.json=mainObject.getJSONObject("weather");
			
	
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	
}
