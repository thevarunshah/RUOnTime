package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.PossibleRoutesExListAdapter;
import com.thevarunshah.ruontime.backend.PossibleRoutesTimes;
import com.thevarunshah.ruontime.backend.Stop;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class PossibleRoutesResultsScreen extends Activity {
	
	ExpandableListView exListView;
	ExpandableListAdapter listAdapter;
	
	String startStopString = "";
	String destinationStopString = "";
	
	Timer timer;
	boolean resuming;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.possible_routes_results_screen);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd4b39")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		if(Build.VERSION.SDK_INT >= 18){
			getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		}
		
		startStopString = getIntent().getBundleExtra("bundle").getString("startStop");
		destinationStopString = getIntent().getBundleExtra("bundle").getString("destinationStop");
		setTitle(Html.fromHtml("<b>" + startStopString + " to " + destinationStopString + "</b>"));
		
		resuming = false;
		
		exListView = (ExpandableListView) findViewById(R.id.possibleRoutesExListView);
		
		List<PossibleRoutesTimes> listDataHeader = new ArrayList<PossibleRoutesTimes>();
		HashMap<PossibleRoutesTimes, List<Integer>> listDataChild = new HashMap<PossibleRoutesTimes, List<Integer>>();
		
		Stop startStop = Database.getStops().get(startStopString);
		Stop destinationStop = Database.getStops().get(destinationStopString);
		
		ArrayList<PossibleRoutesTimes> possibleRoutes = Database.findPossibleRoutes(startStop, destinationStop);
		Collections.sort(possibleRoutes);
		if(possibleRoutes.size() == 0){
			Toast.makeText(getApplicationContext(), "No direct routes possible from " + startStopString + " to " + destinationStopString + ".", Toast.LENGTH_LONG).show();
			finish();
		}
		for(PossibleRoutesTimes frt : possibleRoutes){
			listDataHeader.add(frt);
			List<Integer> truncatedWaitTimes = frt.getWaitTimes().subList(0, frt.getTravelTimes().size());
			listDataChild.put(frt, truncatedWaitTimes);
		}
		
		listAdapter = new PossibleRoutesExListAdapter(this, listDataHeader, listDataChild);
		exListView.setAdapter(listAdapter);
		
		if(listAdapter.getGroupCount() <= 3){
			for(int i = 0; i < listAdapter.getGroupCount(); i++){
				exListView.expandGroup(i);
			}
		}
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){

						List<PossibleRoutesTimes> listDataHeader = new ArrayList<PossibleRoutesTimes>();
						HashMap<PossibleRoutesTimes, List<Integer>> listDataChild = new HashMap<PossibleRoutesTimes, List<Integer>>();
						
						Stop startStop = Database.getStops().get(startStopString);
						Stop destinationStop = Database.getStops().get(destinationStopString);
						
						ArrayList<PossibleRoutesTimes> possibleRoutes = Database.findPossibleRoutes(startStop, destinationStop);
						Collections.sort(possibleRoutes);
						if(possibleRoutes.size() != 0){
							
							for(PossibleRoutesTimes frt : possibleRoutes){
								listDataHeader.add(frt);
								List<Integer> truncatedWaitTimes = frt.getWaitTimes().subList(0, frt.getTravelTimes().size());
								listDataChild.put(frt, truncatedWaitTimes);
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new PossibleRoutesExListAdapter(PossibleRoutesResultsScreen.this, listDataHeader, listDataChild);
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
			@Override
			public void run(){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){

						List<PossibleRoutesTimes> listDataHeader = new ArrayList<PossibleRoutesTimes>();
						HashMap<PossibleRoutesTimes, List<Integer>> listDataChild = new HashMap<PossibleRoutesTimes, List<Integer>>();
						
						Stop startStop = Database.getStops().get(startStopString);
						Stop destinationStop = Database.getStops().get(destinationStopString);
						
						ArrayList<PossibleRoutesTimes> possibleRoutes = Database.findPossibleRoutes(startStop, destinationStop);
						Collections.sort(possibleRoutes);
						if(possibleRoutes.size() != 0){
							
							for(PossibleRoutesTimes frt : possibleRoutes){
								listDataHeader.add(frt);
								List<Integer> truncatedWaitTimes = frt.getWaitTimes().subList(0, frt.getTravelTimes().size());
								listDataChild.put(frt, truncatedWaitTimes);
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new PossibleRoutesExListAdapter(PossibleRoutesResultsScreen.this, listDataHeader, listDataChild);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
