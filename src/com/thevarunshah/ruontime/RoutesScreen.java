package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.Route;

public class RoutesScreen extends Activity {

	ArrayList<Route> activeRoutes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.routes_screen);
		
		activeRoutes = new ArrayList<Route>();
		activeRoutes = Database.findActiveRoutes();
		if(activeRoutes.size() == 0){
			activeRoutes.add(new Route("none", "no active routes"));
		}
		
		ListView lw = (ListView) findViewById(R.id.routesListView);
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for(Route r : activeRoutes){
	        HashMap<String,String> datum = new HashMap<String, String>();
	        datum.put("RouteName", r.getName());
	        datum.put("RoutePath", r.getPath());
	        data.add(datum);
	    }
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, 
				new String[] {"RouteName", "RoutePath"}, new int[] {android.R.id.text1, android.R.id.text2});
		//ArrayAdapter<Route> aa = new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1, activeRoutes);
		lw.setAdapter(adapter);
		lw.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3){

				Route selectedRoute = activeRoutes.get(position);
				//Toast.makeText(getApplicationContext(), "Route: " + selectedRoute.getId(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent(RoutesScreen.this, RouteStopsScreen.class);
				Bundle extra = new Bundle();
				extra.putString("routeId", selectedRoute.getId());
				i.putExtra("bundle", extra);
				startActivity(i);
			}
		});
	}
}
