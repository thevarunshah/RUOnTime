package com.thevarunshah.ruontime.backend;

import java.io.Serializable;
import java.util.ArrayList;


public class Route implements Comparable<Route>, Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String path;
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	
	public Route(String id, String name){
		this.id = id;
		this.name = name;
		setPath(id);
	}
	
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

	public ArrayList<Stop> getStops() {
		return this.stops;
	}

	private void setPath(String id){
		
		switch(id){
			case "a": this.path = "Busch <--> College Ave"; break;
			case "b": this.path = "Busch <--> Livingston"; break;
			case "c": this.path = "Commuter Loop (Busch)"; break;
			case "ee": this.path = "College Ave <--> Cook/Douglass"; break;
			case "f": this.path = "College Ave <--> Cook/Douglass (Express)"; break;
			case "h": this.path = "College Ave <--> Busch"; break;
			case "lx": this.path = "Livingston <--> College Ave"; break;
			case "rexb": this.path = "Cook/Douglass <--> Busch"; break;
			case "rexl": this.path = "Cook/Douglass <--> Livingston"; break;
			case "s": this.path = "College Ave --> Busch --> Livingston --> Cook/Douglass"; break;
			case "w1": this.path = "New Brunswick <--> College Ave"; break;
			case "w2": this.path = "College Ave <--> New Brunswick"; break;
			case "wknd1": this.path = "College Ave --> Busch --> Livingston --> Cook/Douglass"; break;
			case "wknd2": this.path = "College Ave --> Cook/Douglass --> Livingston --> Busch"; break;
			case "rbhs": this.path = "RBHS <--> RWJ"; break;
			default: this.path = "unknown"; break;
		}
	}
	
	public void setStops(ArrayList<Stop> stops) {
		this.stops = stops;
	}
	
	public int compareTo(Route r) {
		return this.name.compareTo(r.name);
	}

	public String toString(){
		return this.name + " (" + this.path + ")";
	}
	
	public boolean equals(Object o){
		
		if(o == null || !(o instanceof Route))
			return false;
		
		Route r = (Route)o;
		if(r.id.equals(this.id))
			return true;
		else
			return false;
	}
}
