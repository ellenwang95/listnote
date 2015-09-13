package listnote;

import java.sql.*;

abstract public class AnyUser {
	public String username, avatar;
	public int user_id;
	protected DatabaseConfig db_config;
	protected Database dbc;
	protected boolean _db_pulled = false, 
			_exists = false;
	
	protected void _set_object_properties(Integer id, String avatar) {
		this.user_id = id;
		this.avatar = avatar;
	}
	
	public void pull_information() {
		try {
			ResultSet result = this.dbc.query("SELECT id, avatar FROM users WHERE id="+this.username);
			try {
				result.next();
				this._set_object_properties(result.getInt("id"), result.getString("avatar"));
				this._db_pulled = true;
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
