package com.thevarunshah.ruontime.backend;

import java.util.ArrayList;


public class RouteTimes implements Comparable<RouteTimes> {

	private String id;
	private String direction;
	private ArrayList<Integer> times = new ArrayList<Integer>();
	
	public RouteTimes(String id, String direction){
		this.id = id;
		this.direction = direction;
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
	
	public String toString(){
		return Database.getRoutes().get(this.id).getName() + " (" + this.direction + ")";
	}
	
	public int compareTo(RouteTimes rt) {
		return this.id.compareTo(rt.id);
	}
	
}
