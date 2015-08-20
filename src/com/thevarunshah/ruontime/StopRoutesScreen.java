package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.RouteTimes;
import com.thevarunshah.ruontime.backend.Stop;
import com.thevarunshah.ruontime.backend.StopRoutesExListAdapter;

public class StopRoutesScreen extends Activity {

	Timer timer;
	String stopName;
	ExpandableListView exListView;
	ExpandableListAdapter listAdapter;
	boolean resuming;
	boolean favorite;
	Stop s = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_routes_screen);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		resuming = false;
		
		stopName = getIntent().getBundleExtra("bundle").getString("stopName");
		
		exListView = (ExpandableListView) findViewById(R.id.stopRoutesExListView);
		
		List<RouteTimes> listDataHeader = new ArrayList<RouteTimes>();
		HashMap<RouteTimes, List<Integer>> listDataChild = new HashMap<RouteTimes, List<Integer>>();
		
		s = Database.stops.get(stopName);
		setTitle(s.getName() + " Stop");
		
		ArrayList<RouteTimes> stopRouteTimes = Database.findRoutesforStop(s);
		Collections.sort(stopRouteTimes);
		for(RouteTimes rt : stopRouteTimes){
			listDataHeader.add(rt);
			listDataChild.put(rt, rt.getTimes());
		}
		
		listAdapter = new StopRoutesExListAdapter(this, listDataHeader, listDataChild);
		exListView.setAdapter(listAdapter);
		
		if(listAdapter.getGroupCount() <= 3){
			for(int i = 0; i < listAdapter.getGroupCount(); i++){
				exListView.expandGroup(i);
			}
		}
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				runOnUiThread(new Runnable(){
					public void run(){

						List<RouteTimes> listDataHeader = new ArrayList<RouteTimes>();
						HashMap<RouteTimes, List<Integer>> listDataChild = new HashMap<RouteTimes, List<Integer>>();
						
						Stop s = Database.stops.get(stopName);
						ArrayList<RouteTimes> stopRouteTimes = Database.findRoutesforStop(s);
						if(stopRouteTimes.size() != 0){
							Collections.sort(stopRouteTimes);
							for(RouteTimes rt : stopRouteTimes){
								listDataHeader.add(rt);
								listDataChild.put(rt, rt.getTimes());
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new StopRoutesExListAdapter(StopRoutesScreen.this, listDataHeader, listDataChild);
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

						List<RouteTimes> listDataHeader = new ArrayList<RouteTimes>();
						HashMap<RouteTimes, List<Integer>> listDataChild = new HashMap<RouteTimes, List<Integer>>();
						
						Stop s = Database.stops.get(stopName);
						ArrayList<RouteTimes> stopRouteTimes = Database.findRoutesforStop(s);
						if(stopRouteTimes.size() != 0){
							Collections.sort(stopRouteTimes);
							for(RouteTimes rt : stopRouteTimes){
								listDataHeader.add(rt);
								listDataChild.put(rt, rt.getTimes());
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new StopRoutesExListAdapter(StopRoutesScreen.this, listDataHeader, listDataChild);
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
        inflater.inflate(R.menu.stop_routes_screen, menu);
        
		if(Database.favoriteStops.contains(s)){
			menu.findItem(R.id.SRfavorite).setIcon(R.drawable.ic_star_white_24dp);
			favorite = true;
		}
		else{
			menu.findItem(R.id.SRfavorite).setIcon(R.drawable.ic_star_border_white_24dp);
			favorite = false;
		}
        
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.SRfavorite:
	    		if(!favorite){
	    			item.setIcon(R.drawable.ic_star_white_24dp);
	    			Database.favoriteStops.add(s);
	    		}
	    		else{
	    			item.setIcon(R.drawable.ic_star_border_white_24dp);
	    			Database.favoriteStops.remove(s);
	    		}
	    		favorite = !favorite;
	    		return true;
	        case android.R.id.home:
	            this.finish();
	            return true;
	        }
        return super.onOptionsItemSelected(item);
    }
}
