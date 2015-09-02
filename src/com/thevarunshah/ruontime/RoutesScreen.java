package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
		setContentView(R.layout.routes_screen);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd4b39")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		if(Build.VERSION.SDK_INT >= 18){
			getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		}
		setTitle(Html.fromHtml("<b>"+getTitle()+"</b>"));
		
		activeRoutes = new ArrayList<Route>();
		activeRoutes = Database.findActiveRoutes(getApplicationContext());
		if(activeRoutes.size() == 0){
			Toast.makeText(getApplicationContext(), "No routes are currently active.", Toast.LENGTH_LONG).show();
			finish();
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
		lw.setAdapter(adapter);
		lw.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3){

				Route selectedRoute = activeRoutes.get(position);
				Intent i = new Intent(RoutesScreen.this, RouteStopsScreen.class);
				Bundle extra = new Bundle();
				extra.putString("routeId", selectedRoute.getId());
				i.putExtra("bundle", extra);
				startActivity(i);
			}
		});
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
