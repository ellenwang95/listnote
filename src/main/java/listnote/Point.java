package listnote;

import java.sql.*;
import java.sql.SQLException;

public class Point {
	public Integer id, author, level, parent_id, type;
	public String body;
	protected TagCollection tags;
	protected Database dbc;
	protected DatabaseConfig db_config;
	protected boolean _db_pulled, _db_checked, _exists;
	
	public Point(int id, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.id = id;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
		this.tags = new TagCollection(db_config);
	}
	public Point(int id, int author, int level, int parent_id, String body, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.id = id;
		this.author = author;
		this.level = level;
		this.parent_id = parent_id;
		this.body = body;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
		this.tags = new TagCollection(db_config);
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
				this._set_object_properties(result.getInt("user_id"), 0, result.getInt("parent"), result.getString("body"));
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
	public void pull_tags() {
		try {
			ResultSet result = this.dbc.query("SELECT COUNT(*) AS c, t.tag AS tag, pt.point_id AS point_id FROM tags t INNER JOIN point_tags pt ON pt.tag_id = t.id WHERE pt.point_id="+this.id+" GROUP BY pt point_id, tag;");
			try {
				while(result.next()) {
					this.tags.push(new Tag(result.getInt("point_id"), result.getString("tag"), result.getInt("note_id"), result.getInt("c")));
				}
				result.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public Integer getId() {
		return id;
	}
	public Integer getAuthor() {
		return author;
	}
	public Integer getLevel() {
		return level;
	}
	public Integer getParent_id() {
		return parent_id;
	}
	public Integer getType() {
		return type;
	}
	public String getBody() {
		return body;
	}
	public TagCollection getTags() {
		return tags;
	}
}
