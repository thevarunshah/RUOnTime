package com.thevarunshah.ruontime;

import java.util.ArrayList;

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
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.Stop;

public class PossibleRoutesSelectionScreen extends Activity implements OnClickListener {

	Spinner startStop, destinationStop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.possible_routes_selection_screen);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd4b39")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		if(Build.VERSION.SDK_INT >= 18){
			getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		}
		setTitle(Html.fromHtml("<b>"+getTitle()+"</b>"));
		
		ArrayList<Stop> activeStops = Database.findActiveStops();
		if(activeStops.size() == 0){
			activeStops.add(new Stop("none", "no active stops", 0, 0));
		}
		
		startStop = (Spinner) findViewById(R.id.startStop);
		destinationStop = (Spinner) findViewById(R.id.destinationStop);
		
		ArrayAdapter<Stop> startStopAdapter = new ArrayAdapter<Stop>(this, R.layout.stops_spinner, activeStops);
		startStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		startStop.setAdapter(startStopAdapter);
		ArrayAdapter<Stop> destinationStopAdapter = new ArrayAdapter<Stop>(this, R.layout.stops_spinner, activeStops);
		destinationStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		destinationStop.setAdapter(destinationStopAdapter);
		destinationStop.setSelection(1);
		
		Button findPossibleRoutes = (Button) findViewById(R.id.findPossibleRoutes);
		findPossibleRoutes.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {

		switch(v.getId()){
			case R.id.findPossibleRoutes: {
				Stop start = (Stop) startStop.getSelectedItem();
				Stop destination = (Stop) destinationStop.getSelectedItem();
				//Toast.makeText(FastestRouteSelectionScreen.this, "calculating possible routes from " + startStop.getSelectedItem() + 
				//                " to " + destinationStop.getSelectedItem(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent(PossibleRoutesSelectionScreen.this, PossibleRoutesResultsScreen.class);
				Bundle extra = new Bundle();
				extra.putString("startStop", start.getName());
				extra.putString("destinationStop", destination.getName());
				i.putExtra("bundle", extra);
				startActivity(i);
				break;
			}
		}
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
