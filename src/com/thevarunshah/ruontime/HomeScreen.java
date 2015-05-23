package com.thevarunshah.ruontime;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

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

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment())
			.commit();
		}

		Button viewRoutes = (Button) findViewById(R.id.viewRoutes);
		viewRoutes.setOnClickListener(this);
		Button viewStops = (Button) findViewById(R.id.viewStops);
		viewStops.setOnClickListener(this);
		Button fastestRoute = (Button) findViewById(R.id.fastestRoute);
		fastestRoute.setOnClickListener(this);
		Button settings = (Button) findViewById(R.id.settings);
		settings.setOnClickListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);
			return rootView;
		}
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
			case R.id.fastestRoute:{
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
				Intent i = new Intent(HomeScreen.this, FastestRouteSelectionScreen.class);
				startActivity(i);
				break;
			}
			case R.id.settings:{
				Toast.makeText(getApplicationContext(), "App created by Varun Shah", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

}
