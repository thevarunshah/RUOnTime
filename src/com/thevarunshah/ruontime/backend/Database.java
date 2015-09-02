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
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
	
	public static void buildDatabase(Context context){
		
		try{
			factory = DocumentBuilderFactory.newInstance();
		    builder = factory.newDocumentBuilder();
		}catch(Exception e){
			Log.i(TAG, "exception: " + e);			
		}
		
		if(readDatabase(context)){
			Log.i(TAG, "database read successful");
			return;
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
			return;
		}
		
		Log.i(TAG, "backing up");
		backupDatabase(context);
	}
	
	public static ArrayList<Route> findActiveRoutes(Context context){
		
		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase(context);
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
	
	public static ArrayList<Stop> findActiveStops(Context context){

		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase(context);
		}
		
		ArrayList<Stop> activeStops = new ArrayList<Stop>();
		
		ArrayList<Route> activeRoutes = findActiveRoutes(context);
		for(Route r : activeRoutes){
			for(Stop s : routes.get(r.getId()).getStops()){
				if(!activeStops.contains(s))
					activeStops.add(s);
			}
		}
		
		Collections.sort(activeStops);
		return activeStops;
	}
	
	public static ArrayList<StopTimes> findStopsForRoute(Route r, Context context){
		
		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase(context);
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

	public static ArrayList<RouteTimes> findRoutesforStop(Stop s, Context context){
		
		if(routes.size() == 0 || stops.size() == 0){
			buildDatabase(context);
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
		    		
		    		RouteTimes rt = new RouteTimes(routeElement.getAttribute("routeTag"), routeDirection, context);
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
	
	public static ArrayList<PossibleRoutesTimes> findPossibleRoutes(Stop startStop, Stop destinationStop, Context context){
		
		ArrayList<PossibleRoutesTimes> possibleRoutes = new ArrayList<PossibleRoutesTimes>();
		
		ArrayList<Route> possibleDirectRoutes = new ArrayList<Route>();
		ArrayList<Route> activeRoutes = findActiveRoutes(context);
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
			
			PossibleRoutesTimes currentRoute = new PossibleRoutesTimes(route.getId(), context);
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
	
	public static void backupDatabase(Context context){
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput("database_backup.ser", Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(Calendar.getInstance().getTime());
			oos.writeObject(routes);
			oos.writeObject(stops);
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
	
	public static boolean readDatabase(Context context){
		
		if(Database.routes.size() != 0 || Database.stops.size() != 0){
			return true;
		}
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = context.openFileInput("database_backup.ser");
			ois = new ObjectInputStream(fis);
			Date previous = (Date)ois.readObject();
			Date today = Calendar.getInstance().getTime();
			if(TimeUnit.DAYS.convert(today.getTime() - previous.getTime(), TimeUnit.MILLISECONDS) >= 7){
				return false;
			}
			Database.routes = (HashMap<String, Route>)ois.readObject();
			Database.stops = (HashMap<String, Stop>)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally{
			try{
				if(ois != null) ois.close();
				if(fis != null) fis.close();
			} catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
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
		
		if(Database.favoriteRoutes.size() != 0 || Database.favoriteStops.size() != 0){
			return;
		}
		
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

	public static HashMap<String, Route> getRoutes(Context context) {
		if(routes.size() == 0){
			buildDatabase(context);
		}
		return routes;
	}

	public static HashMap<String, Stop> getStops(Context context) {
		if(stops.size() == 0){
			buildDatabase(context);
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
}
