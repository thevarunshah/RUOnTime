package com.thevarunshah.ruontime.backend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.thevarunshah.ruontime.R;

public class PossibleRoutesExListAdapter extends BaseExpandableListAdapter {
	
	private Context _context;
    private List<PossibleRoutesTimes> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<PossibleRoutesTimes, List<Integer>> _listDataChild;
 
    public PossibleRoutesExListAdapter(Context context, List<PossibleRoutesTimes> listDataHeader, 
    		HashMap<PossibleRoutesTimes, List<Integer>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
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
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
 
        final Integer childText = (Integer) getChild(groupPosition, childPosition);
        PossibleRoutesTimes frt = (PossibleRoutesTimes) getGroup(groupPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.possible_routes_list_item, parent, false);
        }
 
        TextView txtListChild = (TextView) convertView.findViewById(R.id.possibleRoutesListItem);
        
        String timeText = "Arrives in ";
        Calendar timeNow = Calendar.getInstance();
        Calendar timeNow2 = Calendar.getInstance();
        timeNow.add(Calendar.MINUTE, childText);
        timeNow2.add(Calendar.MINUTE, frt.getTravelTimes().get(frt.getWaitTimes().indexOf(childText)));
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
        String startTime = sdf.format(timeNow.getTime());
        String reachTime = sdf.format(timeNow2.getTime());
        if(childText.equals(0))
        	timeText += "<b>&lt;1</b> minute at <b>" + startTime + "</b><br/>&nbsp;&nbsp;&nbsp;&nbsp; reaches at <b>" + reachTime + "</b>";
		else if(childText.equals(1))
			timeText += "<b>1</b> minute at <b>" + startTime + "</b><br/>&nbsp;&nbsp;&nbsp;&nbsp; reaches at <b>" + reachTime + "</b>";
		else
			timeText += "<b>" + childText + "</b> minutes at <b>" + startTime + "</b><br/>&nbsp;&nbsp;&nbsp;&nbsp; reaches at <b>" + reachTime + "</b>";
        
        txtListChild.setText(Html.fromHtml(timeText));
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
    	
    	PossibleRoutesTimes header = (PossibleRoutesTimes) getGroup(groupPosition);
        String headerTitle = header.toString();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.possible_routes_list_group, parent, false);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.possibleRoutesListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        if(this._listDataChild.get(this._listDataHeader.get(groupPosition)).size() == 0){
        	lblListHeader.setText(headerTitle + " - no trips possible right now");
        }
 
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
