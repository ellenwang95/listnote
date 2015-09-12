package listnote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseNotes {
	private static final String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)"; //dd/mm/yyyy
	private static final String DEFINITION_PATTERN = "(^[a-zA-Z0-9]*)(:)"; //alphanumeric:
	
	protected DatabaseConfig db_config;
	protected Database dbc;
			
	public ParseNotes(DatabaseConfig db_config, Point point) throws IllegalArgumentException, SQLException {
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	
	private static String parseByPattern(String staticPattern, String point_body) {
		Pattern pattern = Pattern.compile(staticPattern);
		
		Matcher matcher = pattern.matcher(point_body);
		
		if(matcher.find()) {
			return matcher.group();
		}
		
		return "";
	}
	
	public void parsePoint(Point point) throws SQLException, ParseException {
		String body = getBody(point);
		
	    String date = parseByPattern(DATE_PATTERN, body);
	    System.out.println("Found date: " + date);
	    
	    String term = parseByPattern(DEFINITION_PATTERN, body);	    
	    int def_begin = body.indexOf(term) + 1;
	    String definition = body.substring(def_begin, body.length() - 1);
	    
	    System.out.println("Found definition: " + term + " : " + definition);

	    
	    if(date != "" || definition != "") {
	    	int note_id = getNoteId(point);
	    	
	    	if(date != "") {
	    		insertDate(date, note_id);
	    	}
	    	
	    	if(definition != "") {
	    		int user_id = getUserId(point);
	    		insertDefinition(term, definition, note_id, user_id);
	    		
	    	}
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
	
	private void insertDate(String date, int note_id) throws ParseException, SQLException {
	    Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
	    Timestamp timestamp = new Timestamp(d.getTime());
	    
		boolean independent = this.dbc.begin();
		
		if(independent) {
			dbc.query(""
					+ "INSERT INTO dates (date, note_id) VALUES (" + timestamp + "," + note_id + ")");	
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

	
	public static void main(String[] args) {
		Point test = 
		
		String datePoint = "This happened on 08/12/2006 when whoeever did whatever.";
		parsePoint(datePoint);
		
		String defPoint = "SomeWord: means this and that";
		parsePoint(defPoint);
	}
}
