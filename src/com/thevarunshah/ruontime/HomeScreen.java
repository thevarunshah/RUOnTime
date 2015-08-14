package com.thevarunshah.ruontime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.thevarunshah.ruontime.backend.Database;

public class HomeScreen extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		Database.buildDatabase();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_screen);

		Button viewRoutes = (Button) findViewById(R.id.viewRoutes);
		viewRoutes.setOnClickListener(this);
		Button viewStops = (Button) findViewById(R.id.viewStops);
		viewStops.setOnClickListener(this);
		Button fastestRoute = (Button) findViewById(R.id.possibleRoute);
		fastestRoute.setOnClickListener(this);
		Button about = (Button) findViewById(R.id.about);
		about.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
			case R.id.viewRoutes:{
				Intent i = new Intent(HomeScreen.this, RoutesScreen.class);
				startActivity(i);
				break;
			}
			case R.id.viewStops:{
				Intent i = new Intent(HomeScreen.this, StopsScreen.class);
				startActivity(i);
				break;
			}
			case R.id.possibleRoute:{
				/*
				LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
				Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if(location != null){
			        double longitude = location.getLongitude();
			        double latitude = location.getLatitude();
					//Toast.makeText(getApplicationContext(), latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
			        ArrayList<Stop> activeStops = Database.findActiveStops();
			        ArrayList<StopDistance> distances = new ArrayList<StopDistance>();
					for(Stop s : activeStops){
						double xDist = s.getLat()-latitude;
						double yDist = s.getLon()-longitude;
						double distance = Math.sqrt(xDist*xDist+yDist*yDist);
						distances.add(new StopDistance(s, distance));
					}
					if(distances.size() != 0){
						Collections.sort(distances);
						Toast.makeText(getApplicationContext(), "closest stop: "
								+ activeStops.get(activeStops.indexOf(distances.get(0).getStop())), Toast.LENGTH_SHORT).show();
					}
				}
				*/
				Intent i = new Intent(HomeScreen.this, PossibleRoutesSelectionScreen.class);
				startActivity(i);
				break;
			}
			case R.id.about:{
				//Toast.makeText(getApplicationContext(), "App created by Varun Shah", Toast.LENGTH_LONG).show();
				Intent i = new Intent(HomeScreen.this, AboutScreen.class);
				startActivity(i);
				break;
			}
		}
	}

}
