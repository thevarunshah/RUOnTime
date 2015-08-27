package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.Route;
import com.thevarunshah.ruontime.backend.RouteStopsExListAdapter;
import com.thevarunshah.ruontime.backend.StopTimes;

public class RouteStopsScreen extends Activity {

	Timer timer;
	String routeId;
	ExpandableListView exListView;
	ExpandableListAdapter listAdapter;
	boolean resuming;
	boolean favorite;
	boolean favoriteChanged = false;
	Route r = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_stops_screen);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd4b39")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		if(Build.VERSION.SDK_INT >= 18){
			getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		}
		setTitle(Html.fromHtml("<b>"+getTitle()+"</b>"));
		
		resuming = false;
		
		routeId = getIntent().getBundleExtra("bundle").getString("routeId");
		
		TextView tvMessages = (TextView) findViewById(R.id.route_messages);
		String message = Database.getMessages(routeId);
		if(message == null){
			((ViewGroup)tvMessages.getParent()).removeView(tvMessages);
			View border = (View) findViewById(R.id.route_messages_border);
			((ViewGroup)border.getParent()).removeView(border);
		}
		else{
			tvMessages.setText(message);
		}
		
		exListView = (ExpandableListView) findViewById(R.id.routeStopsExListView);
		
		List<StopTimes> listDataHeader = new ArrayList<StopTimes>();
		HashMap<StopTimes, List<Integer>> listDataChild = new HashMap<StopTimes, List<Integer>>();
		
		r = Database.getRoutes().get(routeId);
		setTitle(r.getName() + " Route");
		
		ArrayList<StopTimes> routeStopTimes = Database.findStopsForRoute(r);
		if(routeStopTimes.size() == 0){
			Toast.makeText(getApplicationContext(), "This stop is currently not active.", Toast.LENGTH_LONG).show();
			finish();
		}
		for(StopTimes st : routeStopTimes){
			listDataHeader.add(st);
			listDataChild.put(st, st.getTimes());
		}
		
		listAdapter = new RouteStopsExListAdapter(this, listDataHeader, listDataChild, r);
		exListView.setAdapter(listAdapter);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				runOnUiThread(new Runnable(){
					public void run(){

						List<StopTimes> listDataHeader = new ArrayList<StopTimes>();
						HashMap<StopTimes, List<Integer>> listDataChild = new HashMap<StopTimes, List<Integer>>();
						
						Route r = Database.getRoutes().get(routeId);
						ArrayList<StopTimes> routeStopTimes = Database.findStopsForRoute(r);
						if(routeStopTimes.size() != 0){
							for(StopTimes st : routeStopTimes){
								listDataHeader.add(st);
								listDataChild.put(st, st.getTimes());
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new RouteStopsExListAdapter(RouteStopsScreen.this, listDataHeader, listDataChild, r);
							boolean[] tmp = new boolean[listAdapter.getGroupCount()];
							for(int i = 0; i < tmp.length; i++){
								tmp[i] = exListView.isGroupExpanded(i);
							}
							listAdapter = listAdapter2;
							exListView.setAdapter(listAdapter);
							if(tmp.length == listAdapter.getGroupCount()){
								for(int i = 0; i < tmp.length; i++){
									if(tmp[i]){
										exListView.expandGroup(i);
									}
								}
								exListView.setSelectionFromTop(index, top);
							}
						}
					}
				});
			}
			
		}, 30000, 30000);
		
	}

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        resuming = true;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(!resuming)
    		return;
    	
    	timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				runOnUiThread(new Runnable(){
					public void run(){

						List<StopTimes> listDataHeader = new ArrayList<StopTimes>();
						HashMap<StopTimes, List<Integer>> listDataChild = new HashMap<StopTimes, List<Integer>>();
						
						Route r = Database.getRoutes().get(routeId);
						ArrayList<StopTimes> routeStopTimes = Database.findStopsForRoute(r);
						if(routeStopTimes.size() != 0){
							for(StopTimes st : routeStopTimes){
								listDataHeader.add(st);
								listDataChild.put(st, st.getTimes());
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new RouteStopsExListAdapter(RouteStopsScreen.this, listDataHeader, listDataChild, r);
							boolean[] tmp = new boolean[listAdapter.getGroupCount()];
							for(int i = 0; i < tmp.length; i++){
								tmp[i] = exListView.isGroupExpanded(i);
							}
							listAdapter = listAdapter2;
							exListView.setAdapter(listAdapter);
							if(tmp.length == listAdapter.getGroupCount()){
								for(int i = 0; i < tmp.length; i++){
									if(tmp[i]){
										exListView.expandGroup(i);
									}
								}
								exListView.setSelectionFromTop(index, top);
							}
						}
					}
				});
			}
			
		}, 0, 30000);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.route_stops_screen, menu);
        
		if(Database.favoriteRoutes.contains(r)){
			menu.findItem(R.id.RSfavorite).setIcon(R.drawable.ic_star_white_24dp);
			favorite = true;
		}
		else{
			menu.findItem(R.id.RSfavorite).setIcon(R.drawable.ic_star_border_white_24dp);
			favorite = false;
		}
        
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.RSfavorite:
        		favoriteChanged = true;
        		if(!favorite){
        			item.setIcon(R.drawable.ic_star_white_24dp);
        			Database.favoriteRoutes.add(r);
        			Toast.makeText(getApplicationContext(), "Route added to favorites!", Toast.LENGTH_SHORT).show();
        		}
        		else{
        			item.setIcon(R.drawable.ic_star_border_white_24dp);
        			Database.favoriteRoutes.remove(r);
        			Toast.makeText(getApplicationContext(), "Route removed from favorites.", Toast.LENGTH_SHORT).show();
        		}
        		favorite = !favorite;
        		return true;
	        case android.R.id.home:
	        	if(favoriteChanged){
	        		Database.backupFavorites(getApplicationContext());
	        	}
	            this.finish();
	            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onBackPressed() {
		
		if(favoriteChanged){
			Database.backupFavorites(getApplicationContext());
		}		
		super.onBackPressed();
	}
}
