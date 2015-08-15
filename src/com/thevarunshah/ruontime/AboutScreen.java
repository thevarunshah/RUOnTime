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
	    tv.setText(Html.fromHtml("<a href=http://rudots.rutgers.edu/campusbuses.shtml>Click here to find more bus information</a>"));
	    tv.setMovementMethod(LinkMovementMethod.getInstance());
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
