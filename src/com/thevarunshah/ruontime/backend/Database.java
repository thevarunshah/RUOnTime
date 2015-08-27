package com.thevarunshah.ruontime.backend;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.util.Log;

public class Database {
	
	private static DocumentBuilderFactory factory = null;
	private static DocumentBuilder builder = null;
	private final static String nextBusBaseURL = "http://webservices.nextbus.com/service/publicXMLFeed?a=rutgers&command=";
	
	private final static String TAG = "Database";
	
	private static HashMap<String, Route> routes = new HashMap<String, Route>();
	private static HashMap<String, Stop> stops = new HashMap<String, Stop>();
	public static ArrayList<Route> favoriteRoutes = new ArrayList<Route>();
	public static ArrayList<Stop> favoriteStops = new ArrayList<Stop>();
	
	/*
	public static void main(String[] args) {
		
		buildDatabase();
		
		Scanner scanner = new Scanner(System.in);
		String input = "";
		while(true){
			System.out.println("\nchoose one of the following:");
			System.out.println("1) list active routes");
			System.out.println("2) list active stops");
			System.out.println("3) find fastest route");
			System.out.println("4) find closest bus stop");
			System.out.println("5) print database");
			System.out.println("6) exit");
			input = scanner.nextLine();
			if(input.equals("list active routes") || input.equals("1")){
				ArrayList<Route> activeRoutes = findActiveRoutes();
				printActive("route", activeRoutes, null);
				while(true){
					if(activeRoutes.size() == 0){
						System.out.println("there are no active routes as of now");
						break;
					}
					System.out.println("\nwhich route would you like the stops and times for? (type the id)");
					System.out.println("type 'back' to go back to the main menu");
					input = scanner.nextLine();
					if(input.equals("back"))
						break;
					Route tmp = new Route(input, "");
					Route r = activeRoutes.get(activeRoutes.indexOf(tmp));
					ArrayList<StopTimes> routeStopTimes = findStopsForRoute(r);
					printTimes("stoptimes", routeStopTimes, null);
				}
			}
			else if(input.equals("list active stops") || input.equals("2")){
				ArrayList<Stop> activeStops = findActiveStops();
				printActive("stop", null, activeStops);
				while(true){
					if(activeStops.size() == 0){
						System.out.println("there are no active stops as of now");
						break;
					}
					System.out.println("\nwhich stop would you like the routes and times for? (type the id)");
					System.out.println("type 'back' to go back to the main menu");
					input = scanner.nextLine();
					if(input.equals("back"))
						break;
					Stop tmp = new Stop(input, "", 0, 0);
					Stop s = activeStops.get(activeStops.indexOf(tmp));
					ArrayList<RouteTimes> stopRouteTimes = findRoutesforStop(s);
					printTimes("routetimes", null, stopRouteTimes);
				}
			}
			else if(input.equals("find fastest route") || input.equals("3")){
				ArrayList<Stop> activeStops = findActiveStops();
				if(activeStops.size() == 0){
					System.out.println("\nno active routes right now");
					continue;
				}
				System.out.println("list of active stops:");
				printActive("stop", null, activeStops);
				System.out.println("\nenter starting stop: (type id)");
				input = scanner.nextLine();
				Stop stop1 = new Stop(input, "", 0, 0);
				System.out.println("enter destination stop: (type id)");
				input = scanner.nextLine();
				Stop stop2 = new Stop(input, "", 0, 0);
				
				ArrayList<Route> possibleDirectRoutes = new ArrayList<Route>();
				ArrayList<Route> activeRoutes = findActiveRoutes();
				for(Route r : activeRoutes){
					ArrayList<Stop> sl = routes.get(r.id).stops;
					if(sl.contains(stop1) && sl.contains(stop2)){
						possibleDirectRoutes.add(r);
					}
				}
				
				if(possibleDirectRoutes.size() == 0){
					System.out.println("\nno possible direct routes right now");
					continue;
				}
				System.out.println();
				ArrayList<Integer> times = new ArrayList<Integer>();
				for(Route r : possibleDirectRoutes){
					ArrayList<StopTimes> stopTimes = findStopsForRoute(r);
					int stop1Time = -1;
					boolean stop1Found = false;
					int stop2Time = -1;
					boolean stop2Found = false;
					for(int i = 0; i < stopTimes.size(); i++){
						if(!stop1Found && stopTimes.get(i).id.equals(stop1.id)){
							stop1Found = true;
							stop1Time = stopTimes.get(i).times.get(0);
						}
						else if(!stop1Found){
							continue;
						}
						else if(stop1Found && !stop2Found && stopTimes.get(i).id.equals(stop2.id)){
							stop2Found = true;
							for(Integer tmp : stopTimes.get(i).times){
								if(tmp > stop1Time && tmp > stop2Time){
									stop2Time = tmp;
									break;
								}
							}
							break;
						}
						else if(stop1Found && !stop2Found){
							for(Integer tmp : stopTimes.get(i).times){
								if(tmp > stop1Time && tmp > stop2Time){
									stop2Time = tmp;
									break;
								}
							}
							if((i+1) == stopTimes.size()){
								i = -1;
							}
						}
					}
					int waitTime = stop1Time;
					int travelTime = stop2Time-stop1Time;
					if(waitTime == -1 || travelTime == -1){
						times.add(999);
						continue;
					}
					System.out.println(r.name + " will be arriving in " + waitTime + " minutes "
							+ "and it will reach your destination in " + travelTime + " minutes.");
					times.add(waitTime+travelTime);
				}
				int index = 0;
				for(int i = 0; i < times.size(); i++){
					if(times.get(i) < times.get(index)){
						index = i;
					}
				}
				System.out.println("the fastest route would be: " + possibleDirectRoutes.get(index).name);
			}
			else if(input.equals("find closest bus stop") || input.equals("4")){
				ArrayList<Stop> activeStops = findActiveStops();
				if(activeStops.size() == 0){
					System.out.println("there are currently no active stops near you");
					continue;
				}
				System.out.println("\nenter your latitude:");
				input = scanner.nextLine();
				Double latitude = Double.parseDouble(input);
				System.out.println("enter your longitude:");
				input = scanner.nextLine();
				Double longitude = Double.parseDouble(input);
				ArrayList<StopDistance> distances = new ArrayList<StopDistance>();
				for(Stop s : activeStops){
					double xDist = s.lat-latitude;
					double yDist = s.lon-longitude;
					double distance = Math.sqrt(xDist*xDist+yDist*yDist);
					distances.add(new StopDistance(s, distance));
				}
				Collections.sort(distances);
				System.out.println("the closest bus stop to you is: " + activeStops.get(activeStops.indexOf(distances.get(0).stop)));
			}
			else if(input.equals("print database") || input.equals("5")){
				printDatabase();
			}
			else if(input.equals("exit") || input.equals("6")){
				break;
			}
			else{
				System.out.println("invalid command - please try again.");
			}
			System.out.println();
		}
		
		scanner.close();
	}
	*/
	
	public static void buildDatabase(){
		
		try{
			factory = DocumentBuilderFactory.newInstance();
		    builder = factory.newDocumentBuilder();
		}catch(Exception e){
			Log.i(TAG, "exception: " + e);			
		}
		
		String config = HttpGet(nextBusBaseURL + "routeConfig&terse");
		try{
			InputSource is = new InputSource(new StringReader(config));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
		    
		    NodeList body = doc.getChildNodes();
		    NodeList routeList = body.item(0).getChildNodes();
		    for(int i = 0; i < routeList.getLength(); i++){
		    	
		    	Node route = routeList.item(i);
		    	if(route.getNodeName().equals("route") && route.getNodeType() == Node.ELEMENT_NODE){
		    		
		    		Element routeElement = (Element) route;
		    		String routeTag = routeElement.getAttribute("tag");
		    		if(routeTag.equals("penn") || routeTag.equals("pennexpr") || routeTag.equals("mdntpenn") || 
		    				routeTag.equals("kearney") || routeTag.equals("connect")){
		    			continue;
		    		}
		    		Route r = new Route(routeElement.getAttribute("tag"), routeElement.getAttribute("title"));
		    		
		    		ArrayList<Stop> tmpStops = new ArrayList<Stop>();
		    		NodeList stopList = routeElement.getChildNodes();
		    		for(int j = 0; j < stopList.getLength(); j++){
		    			
		    			Node stop = stopList.item(j);
		    			if(stop.getNodeName().equals("stop") && stop.getNodeType() == Node.ELEMENT_NODE){
		    				
		    				Element es = (Element) stop;
		    				String id = es.getAttribute("tag");
		    				Stop routeStop = new Stop(id, es.getAttribute("title"), 
		    						Double.parseDouble(es.getAttribute("lat")), Double.parseDouble(es.getAttribute("lon")));
		    				routeStop.getIdsToRoutesMap().get(id).add(r);
		    				tmpStops.add(routeStop);
		    				Stop s = new Stop(id, es.getAttribute("title"), 
		    						Double.parseDouble(es.getAttribute("lat")), Double.parseDouble(es.getAttribute("lon")));
		    				if(!stops.containsKey(s.getName())){
		    					s.getIdsToRoutesMap().get(id).add(r);
		    					stops.put(s.getName(), s);
		    				}
		    				else if(stops.get(s.getName()).getIdsToRoutesMap().containsKey(id)){
		    					if(!stops.get(s.getName()).getIdsToRoutesMap().get(id).contains(r))
		    						stops.get(s.getName()).getIdsToRoutesMap().get(id).add(r);
		    				}
		    				else{
		    					ArrayList<Route> tmp = new ArrayList<Route>();
		    					tmp.add(r);
		    					stops.get(s.getName()).getIdsToRoutesMap().put(id, tmp);
		    				}
		    			}
		    		}
		    		
		    		r.setStops(tmpStops);
			    	routes.put(r.getId(), r);
		    	}
		    }
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
	}
	
	public static ArrayList<Route> findActiveRoutes(){
		
		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase();
		}
		
		ArrayList<Route> activeRoutes = new ArrayList<Route>();
		
		String xml = HttpGet(nextBusBaseURL + "vehicleLocations&t=0");
		try{
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
			
			NodeList body = doc.getChildNodes();
		    NodeList activeVehicles = body.item(0).getChildNodes();
		    for(int i = 0; i < activeVehicles.getLength(); i++){
		    	
		    	Node vehicle = activeVehicles.item(i);
		    	if(vehicle.getNodeName().equals("vehicle") && vehicle.getNodeType() == Node.ELEMENT_NODE){
		    		
		    		Element ev = (Element) vehicle;
		    		if(!routes.containsKey(ev.getAttribute("routeTag"))){
		    			continue;
		    		}
		    		Route r = new Route(ev.getAttribute("routeTag"), routes.get(ev.getAttribute("routeTag")).getName());
		    		if(!activeRoutes.contains(r)){
		    			activeRoutes.add(r);
		    		}
		    	}
		    }
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
		
		Collections.sort(activeRoutes);
		return activeRoutes;
	}
	
	public static ArrayList<Stop> findActiveStops(){

		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase();
		}
		
		ArrayList<Stop> activeStops = new ArrayList<Stop>();
		
		ArrayList<Route> activeRoutes = findActiveRoutes();
		for(Route r : activeRoutes){
			for(Stop s : routes.get(r.getId()).getStops()){
				if(!activeStops.contains(s))
					activeStops.add(s);
			}
		}
		
		Collections.sort(activeStops);
		return activeStops;
	}
	
	public static ArrayList<StopTimes> findStopsForRoute(Route r){
		
		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase();
		}
		
		ArrayList<String> stopsOrder = new ArrayList<String>();
		String routeStops = "predictionsForMultiStops";
		for(Stop s : routes.get(r.getId()).getStops()){
			String currStop = s.getIdsToRoutesMap().keySet().iterator().next();
			routeStops += "&stops=" + r.getId() + "%7C" + currStop;
			stopsOrder.add(currStop);
		}
		
		ArrayList<StopTimes> routeStopTimes = new ArrayList<StopTimes>();
		String xml = HttpGet(nextBusBaseURL + routeStops);
		try{
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
			
			NodeList body = doc.getChildNodes();
		    NodeList predictions = body.item(0).getChildNodes();
		    for(int i = 0; i < predictions.getLength(); i++){
		    	
		    	Node stop = predictions.item(i);
		    	if(stop.getNodeName().equals("predictions") && stop.getNodeType() == Node.ELEMENT_NODE){
		    		
		    		Element stopElement = (Element) stop;
		    		
		    		NodeList direction = stopElement.getChildNodes();
		    		if(direction.item(1) == null)
		    			continue;
		    		NodeList times = direction.item(1).getChildNodes();
		    		ArrayList<Integer> stopTimes = new ArrayList<Integer>();
		    		for(int j = 0; j < times.getLength(); j++){
		    			
		    			Node time = times.item(j);
		    			if(time.getNodeName().equals("prediction") && time.getNodeType() == Node.ELEMENT_NODE){
		    				
		    				Element timeElement = (Element) time;
		    				stopTimes.add(Integer.parseInt(timeElement.getAttribute("minutes")));
		    			}
		    		}
		    		
		    		StopTimes st = new StopTimes(stopElement.getAttribute("stopTag"), stopElement.getAttribute("stopTitle"));
		    		st.setTimes(stopTimes);
		    		routeStopTimes.add(st);
		    	}
		    }
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
		
		ArrayList<StopTimes> routeStopTimesFinal = new ArrayList<StopTimes>();
		for(String stopId : stopsOrder){
			for(StopTimes st : routeStopTimes){
				if(st.getId().equals(stopId)){
					routeStopTimesFinal.add(st);
					break;
				}
			}
		}
		
		return routeStopTimesFinal;
	}

	public static ArrayList<RouteTimes> findRoutesforStop(Stop s){
		
		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase();
		}
		
		String stopRoutes = "predictionsForMultiStops";
		for(String id : stops.get(s.getName()).getIdsToRoutesMap().keySet()){
			for(Route r : stops.get(s.getName()).getIdsToRoutesMap().get(id)){
				stopRoutes += "&stops=" + r.getId() + "%7C" + id;
			}
		}
		
		ArrayList<RouteTimes> stopRouteTimes = new ArrayList<RouteTimes>();
		String xml = HttpGet(nextBusBaseURL + stopRoutes);
		try{
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
			
			NodeList body = doc.getChildNodes();
		    NodeList predictions = body.item(0).getChildNodes();
		    for(int i = 0; i < predictions.getLength(); i++){
		    	
		    	Node route = predictions.item(i);
		    	if(route.getNodeName().equals("predictions") && route.getNodeType() == Node.ELEMENT_NODE){
		    		
		    		Element routeElement = (Element) route;
		    		
		    		NodeList direction = routeElement.getChildNodes();
		    		if(direction.item(1) == null)
		    			continue;
		    		
		    		Node dir = direction.item(1);
		    		Element dirElement = (Element) dir;
		    		String routeDirection = dirElement.getAttribute("title");
		    		
		    		NodeList times = direction.item(1).getChildNodes();
		    		ArrayList<Integer> routeTimes = new ArrayList<Integer>();
		    		for(int j = 0; j < times.getLength(); j++){
		    			
		    			Node time = times.item(j);
		    			if(time.getNodeName().equals("prediction") && time.getNodeType() == Node.ELEMENT_NODE){
		    				
		    				Element timeElement = (Element) time;
		    				routeTimes.add(Integer.parseInt(timeElement.getAttribute("minutes")));
		    			}
		    		}
		    		
		    		RouteTimes rt = new RouteTimes(routeElement.getAttribute("routeTag"), routeDirection);
		    		rt.setTimes(routeTimes);
		    		stopRouteTimes.add(rt);
		    	}
		    }
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
		
		return stopRouteTimes;
	}
	
	public static ArrayList<StopTimes> findStopsForPossibleRoutes(Route r, Stop start, Stop end){
		
		ArrayList<String> stopsOrder = new ArrayList<String>();
		String startID = "";
		String endID = "";
		String routeStops = "predictionsForMultiStops";
		for(Stop s : routes.get(r.getId()).getStops()){
			if(s.equals(start)){
				String currStop = s.getIdsToRoutesMap().keySet().iterator().next();
				routeStops += "&stops=" + r.getId() + "%7C" + currStop;
				startID = currStop;
			}
			else if(s.equals(end)){
				String currStop = s.getIdsToRoutesMap().keySet().iterator().next();
				routeStops += "&stops=" + r.getId() + "%7C" + currStop;
				endID = currStop;
			}
		}
		stopsOrder.add(startID);
		stopsOrder.add(endID);
		
		ArrayList<StopTimes> routeStopTimes = new ArrayList<StopTimes>();
		String xml = HttpGet(nextBusBaseURL + routeStops);
		try{
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
			
			NodeList body = doc.getChildNodes();
		    NodeList predictions = body.item(0).getChildNodes();
		    for(int i = 0; i < predictions.getLength(); i++){
		    	
		    	Node stop = predictions.item(i);
		    	if(stop.getNodeName().equals("predictions") && stop.getNodeType() == Node.ELEMENT_NODE){
		    		
		    		Element stopElement = (Element) stop;
		    		
		    		NodeList direction = stopElement.getChildNodes();
		    		if(direction.item(1) == null)
		    			continue;
		    		NodeList times = direction.item(1).getChildNodes();
		    		ArrayList<Integer> stopTimes = new ArrayList<Integer>();
		    		ArrayList<Integer> stopTimesInSeconds = new ArrayList<Integer>();
		    		ArrayList<Integer> vehicleIDs = new ArrayList<Integer>();
		    		for(int j = 0; j < times.getLength(); j++){
		    			
		    			Node time = times.item(j);
		    			if(time.getNodeName().equals("prediction") && time.getNodeType() == Node.ELEMENT_NODE){
		    				
		    				Element timeElement = (Element) time;
		    				stopTimes.add(Integer.parseInt(timeElement.getAttribute("minutes")));
		    				stopTimesInSeconds.add(Integer.parseInt(timeElement.getAttribute("seconds")));
		    				vehicleIDs.add(Integer.parseInt(timeElement.getAttribute("vehicle")));
		    			}
		    		}
		    		
		    		StopTimes st = new StopTimes(stopElement.getAttribute("stopTag"), stopElement.getAttribute("stopTitle"));
		    		st.setTimes(stopTimes);
		    		st.setTimesInSeconds(stopTimesInSeconds);
		    		st.setVehicleIDs(vehicleIDs);
		    		routeStopTimes.add(st);
		    	}
		    }
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
		
		ArrayList<StopTimes> routeStopTimesFinal = new ArrayList<StopTimes>();
		for(String stopId : stopsOrder){
			for(StopTimes st : routeStopTimes){
				if(st.getId().equals(stopId)){
					routeStopTimesFinal.add(st);
					break;
				}
			}
		}
		
		return routeStopTimesFinal;
	}
	
	public static ArrayList<PossibleRoutesTimes> findPossibleRoutes(Stop startStop, Stop destinationStop){
		
		ArrayList<PossibleRoutesTimes> possibleRoutes = new ArrayList<PossibleRoutesTimes>();
		
		ArrayList<Route> possibleDirectRoutes = new ArrayList<Route>();
		ArrayList<Route> activeRoutes = findActiveRoutes();
		for(Route route : activeRoutes){
			ArrayList<Stop> stopList = routes.get(route.getId()).getStops();
			if(stopList.contains(startStop) && stopList.contains(destinationStop)){
				possibleDirectRoutes.add(route);
			}
		}
		
		if(possibleDirectRoutes.size() == 0){
			return possibleRoutes;
		}
		
		for(Route route : possibleDirectRoutes){
			
			PossibleRoutesTimes currentRoute = new PossibleRoutesTimes(route.getId());
			ArrayList<StopTimes> stopTimes = findStopsForPossibleRoutes(route, startStop, destinationStop);
			
			if(stopTimes.size() != 2){
				continue;
			}
			
			int startTime = stopTimes.get(0).getTimesInSeconds().get(0);
			currentRoute.setWaitTimes(stopTimes.get(0).getTimes());
			int vehicleID = stopTimes.get(0).getVehicleIDs().get(0);
			
			ArrayList<Integer> destinationTimes = stopTimes.get(1).getTimesInSeconds();
			for(int i = 0; i < destinationTimes.size(); i++){
				int time = destinationTimes.get(i);
				int vehicleID2 = stopTimes.get(1).getVehicleIDs().get(i);
				if(time > startTime && vehicleID == vehicleID2){
					for(int j = i; j < stopTimes.get(1).getTimes().size(); j++){
						currentRoute.getTravelTimes().add(stopTimes.get(1).getTimes().get(j));
					}
					break;
				}
			}
			
			if(currentRoute.getTravelTimes().size() == 0 && stopTimes.get(0).getTimes().size() > 1){
				startTime = stopTimes.get(0).getTimesInSeconds().get(1);
				currentRoute.setWaitTimes(stopTimes.get(0).getTimes());
				vehicleID = stopTimes.get(0).getVehicleIDs().get(1);
				
				destinationTimes = stopTimes.get(1).getTimesInSeconds();
				for(int i = 0; i < destinationTimes.size(); i++){
					int time = destinationTimes.get(i);
					int vehicleID2 = stopTimes.get(1).getVehicleIDs().get(i);
					if(time > startTime && vehicleID == vehicleID2){
						for(int j = i; j < stopTimes.get(1).getTimes().size(); j++){
							currentRoute.getTravelTimes().add(stopTimes.get(1).getTimes().get(j));
						}
						break;
					}
				}
			}
			
			possibleRoutes.add(currentRoute);
		}
		
		/*
		Log.d("TEST", "From " + startStop + " to " + destinationStop);
		for(FastestRouteTimes frt : possibleRoutes){
			Log.d("TEST", "Route: " +  frt.getId());
			for(int i = 0; i < frt.getTravelTimes().size(); i++){
				Log.d("TEST", "\tArriving in: " + frt.getWaitTimes().get(i) + "; reaching after: " + frt.getTravelTimes().get(i));
			}
		}
		*/
		
		return possibleRoutes;
	}
	
	public static String getMessages(String r){
		
		String impMessage = null;
		String xml = HttpGet(nextBusBaseURL + "messages&r=" + r);
		try{
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
			
			NodeList body = doc.getChildNodes();
		    NodeList routes = body.item(0).getChildNodes();
		    for(int i = 0; i < routes.getLength(); i++){
		    	
		    	Node route = routes.item(i);
		    	if(route.getNodeName().equals("route") && route.getNodeType() == Node.ELEMENT_NODE){
		    		
		    		Element er = (Element) route;
		    		String routeTag = er.getAttribute("tag");
		    		if(!routeTag.equals(r)){
		    			continue;
		    		}
		    		
		    		NodeList messages = er.getChildNodes();
		    		NodeList msgProperties = messages.item(1).getChildNodes();
		    		for(int j = 0; j < msgProperties.getLength(); j++){
		    			
		    			Node property = msgProperties.item(j);
		    			if(property.getNodeName().equals("text") && property.getNodeType() == Node.ELEMENT_NODE){
		    				
		    				Element ep = (Element) property;
		    				impMessage = ep.getTextContent();
		    			}
		    		}
		    	}
		    }
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
		
		return impMessage;
	}
	
	@SuppressWarnings("unused")
	private static ArrayList<Integer> findStopTimes(Route r, Stop s){
		
		ArrayList<Integer> stopTimes = new ArrayList<Integer>();
		String xml = HttpGet(nextBusBaseURL + "predictions&r=" + r.getId() 
				+ "&s=" + s.getIdsToRoutesMap().keySet().iterator().next()); //might not work?
		try{
		    InputSource is = new InputSource(new StringReader(xml));
		    Document doc = builder.parse(is);
		    doc.getDocumentElement().normalize();
			
			NodeList body = doc.getChildNodes();
		    NodeList prediction = body.item(0).getChildNodes();
	    	Node stop = prediction.item(0);
	    	if(stop.getNodeName().equals("predictions") && stop.getNodeType() == Node.ELEMENT_NODE){
	    		
	    		Element stopElement = (Element) stop;
	    		NodeList direction = stopElement.getChildNodes();
	    		if(direction.item(1) == null)
	    			return stopTimes;
	    		NodeList times = direction.item(1).getChildNodes();
	    		for(int j = 0; j < times.getLength(); j++){
	    			
	    			Node time = times.item(j);
	    			if(time.getNodeName().equals("prediction") && time.getNodeType() == Node.ELEMENT_NODE){
	    				
	    				Element timeElement = (Element) time;
	    				stopTimes.add(Integer.parseInt(timeElement.getAttribute("seconds")));
	    			}
	    		}
	    	}
		    
		}catch (Exception e){
			System.out.println("exception: " + e);
		}
		
		return stopTimes;
	}
	
	public static void backupFavorites(Context context){
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput("favorites_backup.ser", Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(favoriteRoutes);
			oos.writeObject(favoriteStops);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				oos.close();
				fos.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void readFavorites(Context context){
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = context.openFileInput("favorites_backup.ser");
			ois = new ObjectInputStream(fis);
			Database.favoriteRoutes = (ArrayList<Route>)ois.readObject();
			Database.favoriteStops = (ArrayList<Stop>)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(ois != null) ois.close();
				if(fis != null) fis.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private static String HttpGet(String url){
		
		try{

			HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
			InputStream is = new BufferedInputStream(urlConnection.getInputStream());		 
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			
			String line = "";
			String response = "";
			while ((line = rd.readLine()) != null){
				response += line;
			}
			
			return response;
			
		} catch(Exception e){
			
			System.out.println("exception: " + e);
		}
		
		return null;
	}

	public static HashMap<String, Route> getRoutes() {
		if(routes.size() == 0){
			buildDatabase();
		}
		return routes;
	}

	public static HashMap<String, Stop> getStops() {
		if(stops.size() == 0){
			buildDatabase();
		}
		return stops;
	}

	public static int getNotificationID() {
		
		Calendar c = Calendar.getInstance();
		long now = c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long passed = now - c.getTimeInMillis();
		int notificationID = (int)passed / 1000;
		
		return notificationID;
	}
	
	/*
	static void printDatabase(){
		
		System.out.println("----------list of routes----------");
		Map<String, Route> sortedRoutes = new TreeMap<String, Route>(routes);
	    for(Route r : sortedRoutes.values()){
	    	System.out.println(r);
	    	for(Stop s : r.getStops()){
	    		System.out.println("\t" + s);
	    	}
	    	System.out.println();
	    }

		System.out.println("----------list of stops----------");
		Map<String, Stop> sortedStops = new TreeMap<String, Stop>(stops);
	    for(Stop s : sortedStops.values()){
	    	System.out.println(s);
	    	for(Route r : s.routes){
	    		System.out.println("\t" + r);
	    	}
	    	System.out.println();
	    }
	}
	
	static void printActive(String type, ArrayList<Route> activeRoutes, ArrayList<Stop> activeStops){
		
		if(type.equals("route")){
			for(Route r : activeRoutes){
				System.out.println(r);
			}
		}
		else if(type.equals("stop")){
			for(Stop s : activeStops){
				System.out.println(s);
			}
		}
		else
			return;
	}
	
	static void printTimes(String type, ArrayList<StopTimes> routeStopTimes, ArrayList<RouteTimes> stopRouteTimes){
		
		if(type.equals("stoptimes")){
			for(StopTimes st : routeStopTimes){
				System.out.println(st);
			}
		}
		else if(type.equals("routetimes")){
			for(RouteTimes rt : stopRouteTimes){
				System.out.println(rt);
			}
		}
		else
			return;
		
	}
	*/
}
