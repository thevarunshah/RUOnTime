package com.thevarunshah.ruontime.backend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thevarunshah.ruontime.R;
import com.thevarunshah.ruontime.StopRoutesScreen;

public class StopRoutesExListAdapter extends BaseExpandableListAdapter {

	private Context _context;
    private List<RouteTimes> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<RouteTimes, List<Integer>> _listDataChild;
    
	private NotificationManager mNotifyMgr;
    private String stopName = "";
 
    public StopRoutesExListAdapter(Context context, List<RouteTimes> listDataHeader, HashMap<RouteTimes, List<Integer>> listChildData, String stopName) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        
        this.mNotifyMgr = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.stopName = stopName;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
 
        final Integer childText = (Integer) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.stop_routes_list_item, parent, false);
        }
 
        TextView txtListChild = (TextView) convertView.findViewById(R.id.stopRoutesListItem);
        
        final String timeText;
        Calendar timeNow = Calendar.getInstance();
        timeNow.add(Calendar.MINUTE, childText);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
        final String formattedTime = sdf.format(timeNow.getTime());
        if(childText.equals(0)){
        	timeText = "<b>&lt;1</b> minute at <b>" + formattedTime + "</b>";
        }
		else if(childText.equals(1))
			timeText = "<b>1</b> minute at <b>" + formattedTime + "</b>";
		else
			timeText = "<b>" + childText + "</b> minutes at <b>" + formattedTime + "</b>";
        
        txtListChild.setText(Html.fromHtml(timeText));
        
    	txtListChild.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				final int notificationID = Database.getNotificationID();
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_context).setSmallIcon(R.drawable.logo)
						.setContentTitle(stopName).setContentText(Database.getRoutes().get(_listDataHeader.get(groupPosition).getId()).getName() + " arriving at " + formattedTime);
				
				Intent resultIntent = new Intent(_context, StopRoutesScreen.class);
				Bundle extra = new Bundle();
				extra.putString("stopName", stopName);
				resultIntent.putExtra("bundle", extra);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
				stackBuilder.addParentStack(StopRoutesScreen.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(notificationID, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);
				
				mNotifyMgr.notify(notificationID, mBuilder.build());
				
				new Timer().schedule(new TimerTask() {
					
					@Override
					public void run() {
						
						mNotifyMgr.cancel(notificationID);
					}
				}, (childText.equals(0) ? 1 : childText)*60000);
				
				return true;
			}
		});
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    	
    	RouteTimes header = (RouteTimes) getGroup(groupPosition);
        String headerTitle = header.toString();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.stop_routes_list_group, parent, false);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.stopRoutesListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        
        String arrivingIn = "Arriving in ";
        Integer time1 = header.getTimes().get(0);
        Integer time2 = 0;
        Integer time3 = 0;
        if(header.getTimes().size() > 1){
        	time2 = header.getTimes().get(1);
        }
        if(header.getTimes().size() > 2){
        	time3 = header.getTimes().get(2);
        }
        if(header.getTimes().size() >= 3){
        	if(time1.equals(0)){
        		arrivingIn += "<1, ";
        	}
        	else{
        		arrivingIn += time1 + ", ";
        	}
        	if(time2.equals(0)){
        		arrivingIn += "<1, and ";
        	}
        	else{
        		arrivingIn += time2 + ", and ";
        	}
        	if(time3.equals(0)){
        		arrivingIn += "<1 minute";
        	}
        	else{
        		arrivingIn += time3 + " minutes";
        	}
        }
        else if(header.getTimes().size() >= 2){
        	if(time1.equals(0)){
        		arrivingIn += "<1 and ";
        	}
        	else{
        		arrivingIn += time1 + " and ";
        	}
        	if(time2.equals(0)){
        		arrivingIn += "<1 minute";
        	}
        	else{
        		arrivingIn += time2 + " minutes";
        	}
        }
        else if(header.getTimes().size() >= 1){
        	if(time1.equals(0)){
        		arrivingIn += "<1 minute";
        	}
        	else if(time1.equals(1)){
        		arrivingIn += "1 minute";
        	}
        	else{
        		arrivingIn += time1 + " minutes";
        	}
        }
        
        int arrivingInColor = Color.parseColor("#000099");
        if(time1.equals(0) || time1.equals(1)){
    		arrivingInColor = this._context.getResources().getColor(android.R.color.holo_red_dark);
        }
        else if(time1.compareTo(5) <= 0){
    		arrivingInColor = this._context.getResources().getColor(android.R.color.holo_orange_dark);
        }
        
        TextView lblListArriving = (TextView) convertView.findViewById(R.id.stopRoutesListArriving);
        lblListArriving.setText(arrivingIn);
        lblListArriving.setTextColor(arrivingInColor);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
