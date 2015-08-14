package com.thevarunshah.ruontime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.PossibleRoutesExListAdapter;
import com.thevarunshah.ruontime.backend.PossibleRoutesTimes;
import com.thevarunshah.ruontime.backend.Stop;

public class PossibleRoutesResultsScreen extends Activity {
	
	ExpandableListView exListView;
	ExpandableListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.possible_routes_results_screen);
		
		String startStopString = getIntent().getBundleExtra("bundle").getString("startStop");
		String destinationStopString = getIntent().getBundleExtra("bundle").getString("destinationStop");
		
		exListView = (ExpandableListView) findViewById(R.id.possibleRoutesExListView);
		
		List<PossibleRoutesTimes> listDataHeader = new ArrayList<PossibleRoutesTimes>();
		HashMap<PossibleRoutesTimes, List<Integer>> listDataChild = new HashMap<PossibleRoutesTimes, List<Integer>>();
		
		Stop startStop = Database.stops.get(startStopString);
		Stop destinationStop = Database.stops.get(destinationStopString);
		
		ArrayList<PossibleRoutesTimes> possibleRoutes = Database.findPossibleRoutes(startStop, destinationStop);
		Collections.sort(possibleRoutes);
		if(possibleRoutes.size() == 0){
			Toast.makeText(getApplicationContext(), "No direct routes possible.", Toast.LENGTH_LONG).show();
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
	}

}
