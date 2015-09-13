package listnote.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import listnote.Point;
import listnote.PointCollection;
import listnote.jchronic.DateParser;

public class ParseNotesTest {
	
		private static final DateFormatSymbols dfs = new DateFormatSymbols();
		public static final String[] MONTHS_SHORT = dfs.getShortMonths();
		public static final String[] MONTHS_FULL = dfs.getMonths();
		
		private static final String DAY_PATTERN = "(^0?[1-9]|[12][0-9]|3[01])"; //01 or 1 - 31
		private static final String YEAR_PATTERN = "((19|20)\\d\\d)"; 
		private static final String DATE_PATTERN = "((0?[1-9]|[12][0-9]|3[01])(/|-)(0?[1-9]|1[012])(/|-)((19|20)\\d\\d))"; //dd/mm/yyyy or dd-mm-yyyy
		private static final String DEFINITION_PATTERN = "(^[a-zA-Z0-9]*)(:)"; //alphanumeric:
		private static final String NUMBERED_LIST_PATTERN = "(\\d+)([)]|\\.)"; //1) 1. 
		
		//returns matched string
		private static String parseByPattern(String staticPattern, String point_body) {
			Pattern pattern = Pattern.compile(staticPattern);
			Matcher matcher = pattern.matcher(point_body);
			
			if(matcher.find()) {
				return matcher.group();
			}
			
			return "";
		}
		
		//null if not found
		public Timestamp parseForDate(String body) throws SQLException, ParseException {
			
		    String date = parseByPattern(DATE_PATTERN, body);
		    if(date != "") { //found date! 
		    	return DateParser.parseToTimestamp(date);
		    } else {
		    	StringTokenizer tokenized_body = new StringTokenizer(body);
		    	//look for months 
		    	while (tokenized_body.hasMoreTokens()) {
		            String token = tokenized_body.nextToken();
		    		if(Arrays.asList(MONTHS_FULL).contains(token) || Arrays.asList(MONTHS_SHORT).contains(token)) {
		    			String token_day = tokenized_body.nextToken();
		    			if(parseByPattern(DAY_PATTERN, token_day) != "") { //found date!
		    				//check for year 
		    				String token_year = tokenized_body.nextToken();
		    				if(parseByPattern(YEAR_PATTERN, token_year) != "") { //also found year
			    				return DateParser.parseToTimestamp(token + " " + token_day + " " + token_year);
		    				} else {
			    				return DateParser.parseToTimestamp(token + " " + token_day);
		    				}
		    				
		    			}
		    		}
		        }
		    }
		    
		    return null;
		    
		}
		
		
//		public void parseNote(PointCollection note) {
//			
//			note.iterate_tree(0, (val, key) -> { //point, index
//				try {
//					//check for sequential point 
//					int seqNum = parseForNumberedList(val);
//					if(seqNum != -1) {
//						//add as sequential point
//						System.out.println("Inserted Sequential with id: " + val.id + "seqNum: " + seqNum);
////						insertSequential(val.id, seqNum);
//					}
//					parsePoint(val);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			});
//		}
		
		//returns list number
		public int parseForNumberedList(String body) throws SQLException {
			int num = -1;
		    String numberedList = parseByPattern(NUMBERED_LIST_PATTERN, body);
		    
		    if(numberedList != "") {
		    	num = Integer.parseInt(numberedList.substring(0, numberedList.length()-1));
		    }
		    
		    System.out.println("Numbered List: " + num);
		    
		    return num;
		    
		    
		}
		
		public void parsePoint(String body) throws SQLException, ParseException {
		
		    
		    String term = parseByPattern(DEFINITION_PATTERN, body);	    
		   	    
		    if(term != "") {
		    	int def_begin = body.indexOf(term) + 1;
			    String definition = body.substring(def_begin + term.length(), body.length() - 1);
			    
			    System.out.println("Insert definition: " + term + " : " + definition);

//				insertDefinition(term, definition, note_id, user_id);	
		    }
		    
			Timestamp date = parseForDate(body);
			
			if(date != null) {
				System.out.println("Insert date: " + date);
//				insertDate(date, note_id);
			}

		}

		
		//returns matching item
		public static boolean stringContainsItemFromList(String inputString, String[] items)
		{
		    for(int i =0; i < items.length; i++)
		    {
		        if(inputString.contains(items[i]))
		        {
		            return true;
		        }
		    }
		    return false;
		}

		
		public static void main(String[] args) throws SQLException, ParseException {
			ParseNotesTest pnt = new ParseNotesTest();	
//			String body = "This happened on May 23 when whoeever did whatever.";
//			String body = "1) I want to list this first on Dec 8 .";
			String body = "SomeWord: means this happened on June 3 2004 .";
			pnt.parseForNumberedList(body);	
			pnt.parsePoint(body);
			
//			String datePoint = "This happened on 08/12/2006 when whoeever did whatever.";
//			parsePoint(datePoint);
//			
//			String defPoint = "SomeWord: means this and that";
//			parsePoint(defPoint);
	
	}

}
