package listnote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class MatchPoints {
	
	private Point point;
	protected DatabaseConfig db_config;
	protected Database dbc;
	
	private static final String COMMON_WORDS[] = {"the", "be" , "to", "of", "and", "a", "in", "that", "have", "I"};
	
	public void MatchPoints(DatabaseConfig db_config, Point point) throws IllegalArgumentException, SQLException {
		this.point = point;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
		
	//returns map<num of matches, list<point id>>
	public SortedMap<Integer, List<Integer>> getMatchings() throws SQLException {
		
		SortedMap<Integer, List<Integer>> matchings = new TreeMap<Integer, List<Integer>>();
		
		String body = getBody(this.point);
		
		List<String> this_tokens = getTokens(body);
    	
		boolean independent = this.dbc.begin();
	    
	    if(independent) {
	    	ResultSet r = dbc.query(""
	    			+ "SELECT body, id FROM points");
	    	
	    	while(r.next()) {
	    		int point_id = r.getInt("id");
	    		List<String> compared_tokens = getTokens(r.getString("body"));
	    		int numMatching = numberOfMatchingTokens(this_tokens, compared_tokens);
	    		
	    		if(numMatching > 0) {
	    			List<Integer> point_id_list = new ArrayList<Integer>();
		    		if(matchings.containsKey(numMatching)) {
		    			point_id_list = matchings.get(numMatching);
		    		}
	    			point_id_list.add(point_id);
		    		matchings.put(numMatching, point_id_list);
	    		}
	    	}
	    }
	    
	    return matchings;
		
	}

	
	private int numberOfMatchingTokens(List<String> a, List<String> b) {
		int count = 0;
		HashSet<String> tokens = new HashSet<String>(); //doesn't allow duplicates
		for(int i = 0; i < a.size(); i++) {
			tokens.add(a.get(i));
		}
		//check for duplicates 
		for(int i = 0; i < b.size(); i++) {
			if(!tokens.add(b.get(i))) {
				count++;
			}
		}
		return count;

	}
	
	private List<String> getTokens(String body) {
		List<String> tokens = new ArrayList<String>();
		
		StringTokenizer tokenized_body = new StringTokenizer(body);
		
    	while (tokenized_body.hasMoreTokens()) {
            String token = tokenized_body.nextToken();
            if(!Arrays.asList(COMMON_WORDS).contains(token) && token.matches("[a-zA-Z0-9]+")) { //only want alphanumeric
            	tokens.add(token);
            }
    	}	
    	
    	return tokens;
	}
	
	private String getBody(Point point) throws SQLException {
		String body = "";
		
		boolean independent = this.dbc.begin();
	    
	    if(independent) {
	    	ResultSet r = dbc.query(""
	    			+ "SELECT body FROM points WHERE id = " + point.id);
	    	body = r.getString(0);
	    }
//	    this.dbc.commit();
	    
	    return body;
	}
	
	public static void main(String[] args) {
		List<String> test1 = new ArrayList<String>();
		test1.add("hi");
		test1.add("hello");
		List<String> test2 = new ArrayList<String>();
		test2.add("hello");
		
//		System.out.println(numberOfMatchingTokens(test1, test2));
	}
	

	
	
}
