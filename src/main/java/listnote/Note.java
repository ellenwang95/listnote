package listnote;

import java.sql.*;
import java.util.Date;

public class Note {
	public Date date;
	public int id, user_id;
	public String title;
	
	protected DatabaseConfig db_config;
	protected Database dbc;
	protected boolean _exists, _db_pulled, _db_checked;
	
	public Note(Date date, int id, int user_id, String title, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.date = date;
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public Note(int id, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.id = id;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public void _set_object_properties(String title, Date date, int user_id) {
		this.user_id = user_id;
		this.date = date;
		this.title = title;
	}
	public void pull_information() {
		try {
			ResultSet result = this.dbc.query("SELECT title, date, user_id FROM notes WHERE id="+this.id+";");
			try {
				result.next();
				this._set_object_properties(result.getString("title"), result.getDate("date"), result.getInt("user_id"));
				this._db_pulled = true;
			}
			catch(SQLException e) {
				this._exists = false;
				this._db_checked = true;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public PointCollection pull_children() throws IllegalArgumentException, SQLException {
		PointCollection ret = new PointCollection(this.db_config);
		try {
			ResultSet result = this.dbc.query("SELECT id, type, user_id, body FROM points WHERE parent IS NULL;"); // all roots
			try {
				while(result.next()) {
					int root_cursor = -1;
					switch(result.getInt("type")) {
					case 2:
						ResultSet def_result = this.dbc.query("SELECT dt.term AS term, d.id AS id, d.def AS def FROM dictionary d INNER JOIN dictionary_terms dt ON dt.id=d.term_id WHERE d.point_id="+result.getInt("id")+" LIMIT 1;");
						System.out.println("SELECT dt.term, d.id, d.def FROM dictionary d INNER JOIN dictionary_terms dt ON dt.id=d.term_id WHERE d.point_id="+result.getInt("id")+" LIMIT 1;");
						try {
							if(def_result.next()) {
								// System.out.println(def_result.getString("term"));
								root_cursor = ret.push(new Point(result.getInt("id"), result.getInt("type"), result.getInt("user_id"), 0, null, def_result.getString("term"), def_result.getString("def"), this.db_config));
							}
						}
						catch(SQLException e) {
							e.printStackTrace();
						}
						break;
					case 1:
					default:
						root_cursor = ret.push(new Point(result.getInt("id"), result.getInt("type"), result.getInt("user_id"), 0, null, result.getString("body"), this.db_config));
						break;
//					case 3:
//						root_cursor = ret.push(new )
//						break;
					}
					if(root_cursor!=-1) {
						ret.build(root_cursor, false, false);
					}
				}
				return ret;
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Date getDate() {
		return date;
	}
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
}
