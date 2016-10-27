
import java.util.*;
import java.util.Map.Entry;

import com.mysql.jdbc.Statement;

public class CrawlerHelpers {
	// Goes through urlids sent and sorts which ones are the most common, 
	//then gets the url links from the database in order of importance
	public static ArrayList<String> getOrderedUrls(List<Integer> urlids) {
		// Sorts how many times an id appears then puts it in a hashtable
		// Key(number in array) Value(how many times it appears)
		Hashtable<Integer, Integer> ids = new Hashtable<Integer, Integer>();
		for (int i = 0; i < urlids.size(); i++) {
			int count = 0;
			if (ids.get(urlids.get(i)) != null) continue;
			for (int j = 0; j < urlids.size(); j++) {
				if (urlids.get(i) == urlids.get(j)) 
					count++;
			}
			ids.put(urlids.get(i), count);
		}
		
		// Loop through hashtable and make arraylist of most important urls
		List<Entry<Integer, Integer>> importantIds;
		
		List<Map.Entry<Integer, Integer>> result = new ArrayList(ids.entrySet());
	    Collections.sort(result, new Comparator<Map.Entry<Integer, Integer>>(){
	        public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
	            return o2.getValue() - o1.getValue();
	    }});
	    importantIds = result;
	    
	    System.out.println("results = " + importantIds);
	    System.out.println(importantIds.get(0).getKey());
		
		
	    /*
		// create big query to get all urls with urlids
		query = "select * from urls where urlid in (";
		for (int i = 0; i < urlids.size(); i++) {
			if (i != urlids.size() - 1) {
				query += "'" + urlids.get(i) + "', ";
			}else {
				query += "'" + urlids.get(i) + "');";
			}
		}
		
		Statement stat = (Statement)connection.createStatement();
		urlSet = stat.executeQuery(query);
		
		*/
		return null;
	}
	
	// Goes through urlids sent and sorts which ones are most common, then gets
	// the images links from those urlids
	public static ArrayList<String> getOrderedImages(List<Integer> urlids) {
		
		return null;
	}
	
	// sorts through urls and gets the ones most common then sends description in order of ranks
	public static ArrayList<String> getOrderedDescriptions(List<Integer> urlids) {
		
		return null;
	}
	
}
