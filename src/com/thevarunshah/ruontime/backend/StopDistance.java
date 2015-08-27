package com.thevarunshah.ruontime.backend;


public class StopDistance implements Comparable<StopDistance>{

	private Stop stop;
	private Double distance;
	
	public StopDistance(Stop stop, Double distance){
		
		this.stop = stop;
		this.distance = distance;
	}

	public Stop getStop() {
		return stop;
	}

	public Double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(StopDistance sd) {
		return Double.compare(this.distance, sd.distance);
	}
	
}
