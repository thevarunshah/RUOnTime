package com.thevarunshah.ruontime.backend;

import java.util.ArrayList;

import android.content.Context;

public class PossibleRoutesTimes implements Comparable<PossibleRoutesTimes>{
	
	private String id;
	private ArrayList<Integer> waitTimes;
	private ArrayList<Integer> travelTimes = new ArrayList<Integer>();
	private Context context;
	
	public PossibleRoutesTimes(String id, Context context){
		this.id = id;
		this.waitTimes = new ArrayList<Integer>();
		this.context = context;
	}
	
	public String getId() {
		return id;
	}

	public ArrayList<Integer> getWaitTimes() {
		return waitTimes;
	}

	public void setWaitTimes(ArrayList<Integer> waitTimes) {
		this.waitTimes = waitTimes;
	}

	public ArrayList<Integer> getTravelTimes() {
		return travelTimes;
	}

	public void setTravelTimes(ArrayList<Integer> travelTimes) {
		this.travelTimes = travelTimes;
	}
	
	@Override
	public String toString(){
		
		Route route = Database.getRoutes(this.context).get(this.id);
		if(route != null)
			return route.getName();
		else{
			return this.id;
		}
	}
	
	@Override
	public int compareTo(PossibleRoutesTimes frt){
		return this.id.compareTo(frt.id);
	}
}
