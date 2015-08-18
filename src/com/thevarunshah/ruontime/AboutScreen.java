package com.thevarunshah.ruontime;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutScreen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_screen);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		TextView tv = (TextView) findViewById(R.id.rudots_link);
	    tv.setText(Html.fromHtml("To view bus route maps and more information about busses, vsit <a href=http://rudots.rutgers.edu/campusbuses.shtml>RU DOTS</a>"));
	    tv.setMovementMethod(LinkMovementMethod.getInstance());
	    
	    TextView email = (TextView) findViewById(R.id.email);
	    email.setText(Html.fromHtml("Bugs/Comments? <a href=\"mailto:varun.shah@rutgers.edu\">Email me!</a>"));
	    email.setMovementMethod(LinkMovementMethod.getInstance());
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
