package com.thevarunshah.ruontime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.Route;
import com.thevarunshah.ruontime.backend.Stop;

public class FavoritesScreen extends Activity{
	
	ListView lvRoutes;
	ListView lvStops;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites_screen);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		TextView tvRoutes = (TextView) findViewById(R.id.favorite_routes_tv);
		tvRoutes.setTypeface(null, Typeface.BOLD);
		
		TextView tvStops = (TextView) findViewById(R.id.favorite_stops_tv);
		tvStops.setTypeface(null, Typeface.BOLD);
		
		lvRoutes = (ListView) findViewById(R.id.favorite_routes_list);
		lvStops = (ListView) findViewById(R.id.favorite_stops_list);
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		final ArrayAdapter<Route> aaRoutes = new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1, Database.favoriteRoutes);
		lvRoutes.setAdapter(aaRoutes);
		lvRoutes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Route selectedRoute = aaRoutes.getItem(position-1);
				Intent i = new Intent(FavoritesScreen.this, RouteStopsScreen.class);
				Bundle extra = new Bundle();
				extra.putString("routeId", selectedRoute.getId());
				i.putExtra("bundle", extra);
				startActivity(i);
			}
		});
		
		final ArrayAdapter<Stop> aaStops = new ArrayAdapter<Stop>(this, android.R.layout.simple_list_item_1, Database.favoriteStops);
		lvStops.setAdapter(aaStops);
		lvStops.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Stop selectedStop = aaStops.getItem(position-1);
				Intent i = new Intent(FavoritesScreen.this, StopRoutesScreen.class);
				Bundle extra = new Bundle();
				extra.putString("stopName", selectedStop.getName());
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
