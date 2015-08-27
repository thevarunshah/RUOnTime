package com.thevarunshah.ruontime.backend;

import java.util.ArrayList;

public class PossibleRoutesTimes implements Comparable<PossibleRoutesTimes>{
	
	private String id;
	private ArrayList<Integer> waitTimes;
	private ArrayList<Integer> travelTimes = new ArrayList<Integer>();
	
	public PossibleRoutesTimes(String id){
		this.id = id;
		this.waitTimes = new ArrayList<Integer>();
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
		
		Route route = Database.getRoutes().get(this.id);
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
