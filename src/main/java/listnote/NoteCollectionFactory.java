package listnote;

import java.sql.*;

public class NoteCollectionFactory {
	public CurrentUser current_user;
	public DatabaseConfig db_config;
	public Database dbc;
	public NoteCollectionFactory(CurrentUser current_user, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.current_user = current_user;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public NoteCollection spawn_for_user() throws IllegalArgumentException, SQLException {
		NoteCollection collection = new NoteCollection(db_config);
		PreparedStatement stmt = this.dbc.prepare("SELECT id, title, date FROM notes WHERE user_id=?");
		stmt.setInt(1, current_user.user_id);
		ResultSet result = stmt.executeQuery();
		try {
			while(result.next()) {
				collection.push(new Note(result.getDate("date"), result.getInt("id"), current_user.user_id, result.getString("title"), db_config));
			}
			return collection;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
