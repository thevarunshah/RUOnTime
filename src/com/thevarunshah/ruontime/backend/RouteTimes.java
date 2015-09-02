package com.thevarunshah.ruontime.backend;

import java.util.ArrayList;

import android.content.Context;


public class RouteTimes implements Comparable<RouteTimes> {

	private String id;
	private String direction;
	private ArrayList<Integer> times = new ArrayList<Integer>();
	private Context context;
	
	public RouteTimes(String id, String direction, Context context){
		this.id = id;
		this.direction = direction;
		this.context = context;
	}

	public String getId() {
		return id;
	}
	
	public String getDirection(){
		return direction;
	}

	public ArrayList<Integer> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<Integer> times) {
		this.times = times;
	}
	
	@Override
	public String toString(){
		return Database.getRoutes(this.context).get(this.id).getName() + " (" + this.direction + ")";
	}
	
	@Override
	public int compareTo(RouteTimes rt) {
		return this.id.compareTo(rt.id);
	}
	
}
