package listnote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteCollectionFactory {
	public CurrentUser current_user;
	public DatabaseConfig db_config;
	public Database dbc;
	public NoteCollectionFactory(CurrentUser current_user, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.current_user = current_user;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public List<Note> spawn_for_user() throws IllegalArgumentException, SQLException {
		List<Note> ret = new ArrayList<Note>();
		// System.out.println(current_user.user_id);
		PreparedStatement stmt = this.dbc.prepare("SELECT id, title, date FROM notes WHERE user_id=?");
		stmt.setInt(1, current_user.user_id);
		ResultSet result = stmt.executeQuery();
		try {
			while(result.next()) {
				ret.add(new Note(result.getDate("date"), result.getInt("id"), current_user.user_id, result.getString("title"), db_config));
			}
			return ret;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
