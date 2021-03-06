package com.thevarunshah.ruontime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.Window;
import android.widget.Button;

import com.thevarunshah.ruontime.backend.Database;

public class HomeScreen extends Activity implements OnClickListener {
	
	private AlphaAnimation clickEffect = new AlphaAnimation(1F, 0.5F);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		if(Database.getRoutes(getApplicationContext()).size() == 0 || Database.getStops(getApplicationContext()).size() == 0){
			Database.buildDatabase(getApplicationContext());
		}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_screen);

		Button viewRoutes = (Button) findViewById(R.id.viewRoutes);
		viewRoutes.setOnClickListener(this);
		Button viewStops = (Button) findViewById(R.id.viewStops);
		viewStops.setOnClickListener(this);
		Button favorites = (Button) findViewById(R.id.favorites);
		favorites.setOnClickListener(this);
		Button fastestRoute = (Button) findViewById(R.id.possibleRoute);
		fastestRoute.setOnClickListener(this);
		Button about = (Button) findViewById(R.id.about);
		about.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		v.startAnimation(clickEffect);
		
		switch(v.getId()){
			case R.id.viewRoutes:{
				Intent i = new Intent(HomeScreen.this, RoutesScreen.class);
				startActivity(i);
				break;
			}
			case R.id.viewStops:{
				Intent i = new Intent(HomeScreen.this, StopsScreen.class);
				startActivity(i);
				break;
			}
			case R.id.favorites:{
				Intent i = new Intent(HomeScreen.this, FavoritesScreen.class);
				startActivity(i);
				break;
			}
			case R.id.possibleRoute:{
				Intent i = new Intent(HomeScreen.this, PossibleRoutesSelectionScreen.class);
				startActivity(i);
				break;
			}
			case R.id.about:{
				Intent i = new Intent(HomeScreen.this, AboutScreen.class);
				startActivity(i);
				break;
			}
		}
	}

}
