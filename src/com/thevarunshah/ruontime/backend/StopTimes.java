package com.thevarunshah.ruontime.backend;

import java.util.ArrayList;


public class StopTimes {

	private String id;
	private String name;
	private ArrayList<Integer> times = new ArrayList<Integer>();
	private ArrayList<Integer> timesInSeconds = new ArrayList<Integer>();
	private ArrayList<Integer> vehicleIDs = new ArrayList<Integer>();
	
	public StopTimes(String id, String name){
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Integer> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<Integer> times) {
		this.times = times;
	}
	
	public ArrayList<Integer> getTimesInSeconds() {
		return timesInSeconds;
	}

	public void setTimesInSeconds(ArrayList<Integer> timesInSeconds) {
		this.timesInSeconds = timesInSeconds;
	}

	public ArrayList<Integer> getVehicleIDs() {
		return vehicleIDs;
	}

	public void setVehicleIDs(ArrayList<Integer> vehicleIDs) {
		this.vehicleIDs = vehicleIDs;
	}
	
	public String toString(){
		
		return this.name;
	}
	
}
