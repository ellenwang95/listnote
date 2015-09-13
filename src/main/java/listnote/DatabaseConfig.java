package listnote;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {
	static final String LDB = "listnote";
	static final String LAUTHDB = "listnote_auth";
	static Map<String,String[]> db_to_creds = new HashMap<String,String[]>();
	static {
		Map<String,String[]> temp_map = new HashMap<String,String[]>();
		temp_map.put("listnote", new String[]{"localhost:3307","listnote","listnoteisthebest","listnote"});
		temp_map.put("listnote_auth", new String[]{"localhost:3307","listnote_auth","listnoteisthebest","listnote_auth"});
		db_to_creds = Collections.unmodifiableMap(temp_map);
	}
	
	static Map<String,String> class_to_db = new HashMap<String,String>();
	static {
		Map<String,String> temp_map = new HashMap<String,String>();
		temp_map.put("ParseNotes", LDB);
		temp_map.put("Renderer", LDB);
		temp_map.put("Point", LDB);
		temp_map.put("SequentialPoint", LDB);
		temp_map.put("TagCollection", LDB);
		temp_map.put("PointCollection", LDB);
		temp_map.put("NoteCollectionFactory", LDB);
		temp_map.put("NoteCollection", LDB);
		temp_map.put("Note", LDB);
		
		//-----
		
		temp_map.put("CurrentUser", LAUTHDB);
		class_to_db = Collections.unmodifiableMap(temp_map);
	}
	protected Map<String,Database> connection_cache = new HashMap<String,Database>();
	public DatabaseConfig() {}
	public Database connect(String classname) throws IllegalArgumentException, SQLException {
		String db;
		if((db = class_to_db.get(classname)) != null) {
			Database dbc;
			if((dbc = connection_cache.get(db)) != null) {
				return dbc;
			}
			else {
				String[] creds = db_to_creds.get(db);
				connection_cache.put(db, new Database(creds[0], creds[1], creds[2], creds[3]));
				return connection_cache.get(db);
			}
		}
		else {
			throw new IllegalArgumentException("Database not mapped for class "+classname);
		}
	}
}
