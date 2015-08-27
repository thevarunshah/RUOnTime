package com.thevarunshah.ruontime.backend;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.Html;


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
			case "a": this.path = Html.fromHtml("Busch &#8646; College Ave").toString(); break;
			case "b": this.path = Html.fromHtml("Busch &#8646; Livingston").toString(); break;
			case "c": this.path = "Commuter Loop (Busch)"; break;
			case "ee": this.path = Html.fromHtml("College Ave &#8646; Cook/Douglass").toString(); break;
			case "f": this.path = Html.fromHtml("College Ave &#8646; Cook/Douglass (Express)").toString(); break;
			case "h": this.path = Html.fromHtml("College Ave &#8646; Busch").toString(); break;
			case "lx": this.path = Html.fromHtml("Livingston &#8646; College Ave").toString(); break;
			case "rexb": this.path = Html.fromHtml("Cook/Douglass &#8646; Busch").toString(); break;
			case "rexl": this.path = Html.fromHtml("Cook/Douglass &#8646; Livingston").toString(); break;
			case "s": this.path = Html.fromHtml("College Ave &#8594; Busch &#8594; Livingston &#8594; Cook/Douglass").toString(); break;
			case "w1": this.path = Html.fromHtml("New Brunswick &#8646; College Ave").toString(); break;
			case "w2": this.path = Html.fromHtml("College Ave &#8646; New Brunswick").toString(); break;
			case "wknd1": this.path = Html.fromHtml("College Ave &#8594; Busch &#8594; Livingston &#8594; Cook/Douglass").toString(); break;
			case "wknd2": this.path = Html.fromHtml("College Ave &#8594; Cook/Douglass &#8594; Livingston &#8594; Busch").toString(); break;
			case "rbhs": this.path = Html.fromHtml("RBHS &#8646; RWJ").toString(); break;
			default: this.path = "Unknown Path"; break;
		}
	}
	
	public void setStops(ArrayList<Stop> stops) {
		this.stops = stops;
	}
	
	public int compareTo(Route r) {
		return this.name.compareTo(r.name);
	}

	public String toString(){
		return this.name;
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
