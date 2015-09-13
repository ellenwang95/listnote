package listnote;

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

import listnote.jchronic.DateParser;

public class ParseNotes {
	private static final DateFormatSymbols dfs = new DateFormatSymbols();
	public static final String[] MONTHS_SHORT = dfs.getShortMonths();
	public static final String[] MONTHS_FULL = dfs.getMonths();
	
	private static final String DAY_PATTERN = "(^0?[1-9]|[12][0-9]|3[01])"; //01 or 1 - 31
	private static final String YEAR_PATTERN = "((19|20)\\d\\d)"; 
	private static final String DATE_PATTERN = "((0?[1-9]|[12][0-9]|3[01])(/|-)(0?[1-9]|1[012])(/|-)((19|20)\\d\\d))"; //dd/mm/yyyy or dd-mm-yyyy
	private static final String DEFINITION_PATTERN = "(^[a-zA-Z0-9]*)(:)"; //alphanumeric:
	private static final String NUMBERED_LIST_PATTERN = "(\\d+)([)]|\\.)"; //1) 1. 
	
	protected DatabaseConfig db_config;
	protected Database dbc;
			
	public ParseNotes(DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	
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
	public Timestamp parseForDate(Point point) throws SQLException, ParseException {
		String body = getBody(point);
		
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
	
	public void parseNote(PointCollection note) {
		
		note.iterate_tree(0, (val, key) -> { //point, index
			try {
				//check for sequential point 
				int seqNum = parseForNumberedList(val);
				if(seqNum != -1) {
					//add as sequential point
					insertSequential(val.id, seqNum);
				}
				parsePoint(val);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	//returns list number
	public int parseForNumberedList(Point point) throws SQLException {
		int num = -1;
		String body = getBody(point);

	    String numberedList = parseByPattern(NUMBERED_LIST_PATTERN, body);
	    
	    if(numberedList != "") {
	    	num = Integer.parseInt(numberedList.substring(0, numberedList.length()-1));
	    }
	    
	    return num;
	    
	    
	}
	
	public void parsePoint(Point point) throws SQLException, ParseException {
		String body = getBody(point);
		int note_id = getNoteId(point);
	    
	    String term = parseByPattern(DEFINITION_PATTERN, body);	    
	   	    
	    if(term != "") {
	    	int def_begin = body.indexOf(term) + 1;
		    String definition = body.substring(def_begin + term.length(), body.length() - 1);
		    
		    System.out.println("Found definition: " + term + " : " + definition);

			int user_id = getUserId(point);
			insertDefinition(term, definition, note_id, user_id);	
	    }
	    
		Timestamp date = parseForDate(point);
		
		if(date != null) {
			System.out.println("Found date: " + date);
			insertDate(date, note_id);
		}

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
		
	private int getNoteId(Point point) throws SQLException {
		int note_id = -1;
		
		boolean independent = this.dbc.begin();
	    
	    if(independent) {
	    	ResultSet r = dbc.query(""
	    			+ "SELECT note FROM points WHERE id = " + point.id);
	    	note_id = r.getInt(0);
	    }
//	    this.dbc.commit();
	    
	    return note_id;
	}
	
	private int getUserId(Point point) throws SQLException {
		int user_id = -1;
		
		boolean independent = this.dbc.begin();
	    
	    if(independent) {
	    	ResultSet r = dbc.query(""
	    			+ "SELECT user_id FROM points WHERE id = " + point.id);
	    	user_id = r.getInt(0);
	    }
//	    this.dbc.commit();
	    
	    return user_id;
	}
	
	private void insertDate(Timestamp date, int note_id) throws ParseException, SQLException {
//	    Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
//	    Timestamp timestamp = new Timestamp(d.getTime());
	    
		boolean independent = this.dbc.begin();
		
		if(independent) {
			dbc.query(""
					+ "INSERT INTO dates (date, note_id) VALUES (" + date + "," + note_id + ")");	
		}
		
		this.dbc.commit();
		
		System.out.println("Added date: " + date + "note_id: " + note_id);
	    
	}
	
	private void insertDefinition(String term, String definition, int note_id, int user_id) throws SQLException {
		int term_id = -1;
		
		//insert into dictionary_terms 
		boolean independent = this.dbc.begin();
		
		if(independent) {
			dbc.query(""
					+ "INSERT INTO dictionary_terms (term) VALUES (" + term + ")");	
			
			ResultSet r_dt = dbc.query("SELECT id FROM dictionary_terms WHERE term = " + term);
			term_id = r_dt.getInt(0);
			
			dbc.query(""
					+ "INSERT INTO dictionary (def, note_id, user_id, term_id) "
					+ "VALUES (" + definition + "," + note_id + "," + user_id + "," + term_id + ")");
			
		}
		
		this.dbc.commit();
		
	}
	
	private void insertSequential(int point_id, int num) throws SQLException {
		
		boolean independent = this.dbc.begin();
		if(independent) {
			dbc.query(""
					+ "INSERT INTO sequential (point_id, num) VALUES (" + point_id + "," + num + ")");	
		}
		
		this.dbc.commit();
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

	
	public static void main(String[] args) {
		try {
			DatabaseConfig dbc = new DatabaseConfig();
			Point test = new Point(1, dbc);
			test.body = "This happened on 08/12/2006 when whoeever did whatever.";
			
			ParseNotes parse_notes = new ParseNotes(dbc);
			parse_notes.parsePoint(test);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
//		String datePoint = "This happened on 08/12/2006 when whoeever did whatever.";
//		parsePoint(datePoint);
//		
//		String defPoint = "SomeWord: means this and that";
//		parsePoint(defPoint);
 catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
