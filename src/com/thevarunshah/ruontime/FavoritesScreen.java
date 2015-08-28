package com.thevarunshah.ruontime;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
		
		Database.readFavorites(getApplicationContext());
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd4b39")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		if(Build.VERSION.SDK_INT >= 18){
			getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		}
		setTitle(Html.fromHtml("<b>"+getTitle()+"</b>"));
		
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
				
				ArrayList<Route> activeRoutes = Database.findActiveRoutes();
				Route selectedRoute = aaRoutes.getItem(position);
				if(!activeRoutes.contains(selectedRoute)){
					Toast.makeText(getApplicationContext(), "Route is not active.", Toast.LENGTH_LONG).show();
					return;
				}
				
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
				
				ArrayList<Stop> activeStops = Database.findActiveStops();
				Stop selectedStop = aaStops.getItem(position);
				if(!activeStops.contains(selectedStop)){
					Toast.makeText(getApplicationContext(), "Stop is not active.", Toast.LENGTH_LONG).show();
					return;
				}
				
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
