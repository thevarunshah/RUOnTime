package com.thevarunshah.ruontime;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.thevarunshah.ruontime.backend.Database;
import com.thevarunshah.ruontime.backend.Stop;

public class StopsScreen extends Activity {
	
	ArrayList<Stop> activeStops;
	ArrayAdapter<Stop> aa;
	EditText et;
	ListView lw;
	LinearLayout mainLayout;
	private AlphaAnimation clickEffect = new AlphaAnimation(1F, 0.5F);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stops_screen);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dd4b39")));
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		if(Build.VERSION.SDK_INT >= 18){
			getActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		}
		setTitle(Html.fromHtml("<b>"+getTitle()+"</b>"));
		
		activeStops = Database.findActiveStops();
		if(activeStops.size() == 0){
			Toast.makeText(getApplicationContext(), "No stops are currently active.", Toast.LENGTH_LONG).show();
			finish();
		}
		
		lw = (ListView) findViewById(R.id.stopsListView);
		et = (EditText) findViewById(R.id.filterText);
		mainLayout = (LinearLayout) findViewById(R.id.stopsScreenLinearLayout);
		aa = new ArrayAdapter<Stop>(this, android.R.layout.simple_list_item_1, activeStops);
		lw.setAdapter(aa);
		et.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        StopsScreen.this.aa.getFilter().filter(cs);
		    }

		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		    @Override
		    public void afterTextChanged(Editable arg0) {}
		});
		lw.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3){
				
				Stop selectedStop = aa.getItem(position);
				//Toast.makeText(getApplicationContext(), "Stop: " + selectedStop.getId(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent(StopsScreen.this, StopRoutesScreen.class);
				Bundle extra = new Bundle();
				extra.putString("stopName", selectedStop.getName());
				i.putExtra("bundle", extra);
				startActivity(i);
			}
		});
		lw.setFocusableInTouchMode(true);
		lw.requestFocus();
		et.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            final int DRAWABLE_RIGHT = 2;
	            
	            v.setAnimation(clickEffect);
	            if(event.getAction() == MotionEvent.ACTION_UP) {
	                if(event.getX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
	                    et.getText().clear();
	                }
	            }
	            return false;
	        }
	    });
		lw.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }
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
