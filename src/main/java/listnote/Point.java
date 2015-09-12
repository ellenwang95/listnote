package listnote;

import java.sql.*;
import java.sql.SQLException;

public class Point {
	public Integer id, author, level, parent_id, type;
	public String body;
	protected Database dbc;
	protected DatabaseConfig db_config;
	protected boolean _db_pulled, _db_checked, _exists;
	
	public Point(int id, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.id = id;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public Point(int id, int author, int level, int parent_id, String body, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.id = id;
		this.author = author;
		this.level = level;
		this.parent_id = parent_id;
		this.body = body;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	protected void _set_object_properties(int author, int level, int parent_id, String body) {
		this.author = author;
		this.level = level;
		this.parent_id = parent_id;
		this.body = body;
	}
	public void pull_information() {
		try {
			ResultSet result = this.dbc.query("SELECT type, user_id, body, parent, note FROM points WHERE id="+this.id+" LIMIT 1");
			try {
				result.next();
			}
			catch(SQLException e) {
				this._exists = false;
				this._db_checked = true;
			}
			this._set_object_properties(result.getInt("user_id"), 0, result.getInt("parent"), result.getString("body"));
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
