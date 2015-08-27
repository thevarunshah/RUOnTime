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
	
	public String toString(){
		
		Route route = Database.getRoutes().get(this.id);
		if(route != null)
			return route.getName();
		else{
			return this.id;
		}
		
		/*
		String ret = "";
		ret += Database.routes.get(this.id).getName();
		int max = this.travelTimes.size();
		int counter = 0;
		if(max == 0){
			ret += " - no route possible";
			return ret;
		}
		else
			ret += " - arriving in ";
		for(Integer i : this.waitTimes){
			counter++;
			if(i.equals(0))
				ret += "<1 minute";
			else if(i.equals(1))
				ret += "1 minute";
			else
				ret += i + " minutes";
			if(counter != max)
				ret += ", ";
			else
				break;
		}
		counter = 0;
		ret += " and reaching destination after ";
		for(Integer i : this.travelTimes){
			counter++;
			if(i.equals(0))
				ret += "<1 minute";
			else if(i.equals(1))
				ret += "1 minute";
			else{
				int tmp = i-(this.waitTimes.get(this.travelTimes.indexOf(i)));
				ret += tmp + " minutes";
			}
			if(counter != max)
				ret += ", ";
		}
		
		return ret;
		*/
	}
	
	public int compareTo(PossibleRoutesTimes frt){
		return this.id.compareTo(frt.id);
	}
}
