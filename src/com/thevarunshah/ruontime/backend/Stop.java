package com.thevarunshah.ruontime.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class Stop implements Comparable<Stop>, Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private double lat;
	private double lon;
	private String campus;
	private HashMap<String, ArrayList<Route>> idsToRoutesMap = new HashMap<String, ArrayList<Route>>();
	
	public Stop(String id, String name, double lat, double lon){
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.idsToRoutesMap.put(id, new ArrayList<Route>());
		setCampus(id);
	}

	public String getName() {
		return name;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getCampus() {
		return campus;
	}

	public HashMap<String, ArrayList<Route>> getIdsToRoutesMap() {
		return idsToRoutesMap;
	}
	
	private void setCampus(String id){
		
		if(id.equals("liberty") || id.equals("patersonn") || id.equals("patersons") || id.equals("pubsafn") || id.equals("rockoff") || 
				id.equals("rutgerss") || id.equals("rutgerss_a") || id.equals("scott") || id.equals("stuactcntr") || id.equals("stuactcntrn") || 
				id.equals("stuactcntrn_2") || id.equals("stuactcntrs") || id.equals("traine") || id.equals("traine_a") || id.equals("trainn") || 
				id.equals("trainn_a") || id.equals("zimmerli") || id.equals("zimmerli_2")){
			this.campus = "College Ave";
		}
		else if(id.equals("allison") || id.equals("allison_a") || id.equals("buel") || id.equals("buells") || id.equals("busch") || 
				id.equals("busch_a") || id.equals("buschse") || id.equals("davidson") || id.equals("hilln") || id.equals("hillw") || 
				id.equals("libofsci") || id.equals("libofsciw") || id.equals("lot48a") || id.equals("science") || id.equals("stadium_a") || 
				id.equals("werblinback") || id.equals("werblinm")){
			this.campus = "Busch";
		}
		else if(id.equals("biel") || id.equals("cabaret") || id.equals("college") || id.equals("college_a") || id.equals("foodsci") || 
				id.equals("gibbons") || id.equals("henders") || id.equals("katzenbach") || id.equals("lipman") || id.equals("newstree") || 
				id.equals("pubsafs") || id.equals("redoak") || id.equals("redoak_a") || id.equals("rockhall")){
			this.campus = "Cook/Douglass";
		}
		else if(id.equals("beck") || id.equals("livingston") || id.equals("livingston_a") || id.equals("quads")){
			this.campus = "Livingston";
		}
		else{
			this.campus = "Other";
		}
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	@Override
	public int compareTo(Stop s) {
		return this.name.compareTo(s.name);
	}
	
	@Override
	public boolean equals(Object o){
		
		if(o == null || !(o instanceof Stop))
			return false;
		
		Stop s = (Stop)o;
		if(s.name.equals(this.name))
			return true;
		else
			return false;
	}
}
