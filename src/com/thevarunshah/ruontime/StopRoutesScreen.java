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
import android.view.View;
import android.view.Window;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.stop_routes_screen);
		resuming = false;
		
		stopName = getIntent().getBundleExtra("bundle").getString("stopName");
		
		exListView = (ExpandableListView) findViewById(R.id.stopRoutesExListView);
		
		List<RouteTimes> listDataHeader = new ArrayList<RouteTimes>();
		HashMap<RouteTimes, List<Integer>> listDataChild = new HashMap<RouteTimes, List<Integer>>();
		
		Stop s = Database.stops.get(stopName);
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
}
