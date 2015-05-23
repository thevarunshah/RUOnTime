package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.route_stops_screen);
		resuming = false;
		
		routeId = getIntent().getBundleExtra("bundle").getString("routeId");
		
		exListView = (ExpandableListView) findViewById(R.id.routeStopsExListView);
		
		List<StopTimes> listDataHeader = new ArrayList<StopTimes>();
		HashMap<StopTimes, List<Integer>> listDataChild = new HashMap<StopTimes, List<Integer>>();
		
		Route r = Database.routes.get(routeId);
		ArrayList<StopTimes> routeStopTimes = Database.findStopsForRoute(r);
		for(StopTimes st : routeStopTimes){
			listDataHeader.add(st);
			listDataChild.put(st, st.getTimes());
		}
		
		listAdapter = new RouteStopsExListAdapter(this, listDataHeader, listDataChild);
		exListView.setAdapter(listAdapter);
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				runOnUiThread(new Runnable(){
					public void run(){

						List<StopTimes> listDataHeader = new ArrayList<StopTimes>();
						HashMap<StopTimes, List<Integer>> listDataChild = new HashMap<StopTimes, List<Integer>>();
						
						Route r = Database.routes.get(routeId);
						ArrayList<StopTimes> routeStopTimes = Database.findStopsForRoute(r);
						if(routeStopTimes.size() != 0){
							for(StopTimes st : routeStopTimes){
								listDataHeader.add(st);
								listDataChild.put(st, st.getTimes());
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new RouteStopsExListAdapter(RouteStopsScreen.this, listDataHeader, listDataChild);
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
						
						Route r = Database.routes.get(routeId);
						ArrayList<StopTimes> routeStopTimes = Database.findStopsForRoute(r);
						if(routeStopTimes.size() != 0){
							for(StopTimes st : routeStopTimes){
								listDataHeader.add(st);
								listDataChild.put(st, st.getTimes());
							}
							
							int index = exListView.getFirstVisiblePosition();
							View v = exListView.getChildAt(0);
							int top = (v == null) ? 0 : v.getTop();
							
							ExpandableListAdapter listAdapter2 = new RouteStopsExListAdapter(RouteStopsScreen.this, listDataHeader, listDataChild);
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
}
