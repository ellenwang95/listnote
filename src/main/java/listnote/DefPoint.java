package listnote;

import java.sql.SQLException;

public class DefPoint extends Point {

	public String term;
	public DefPoint(int id, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		super(id, db_config);
		// TODO Auto-generated constructor stub
	}
	public DefPoint(int id, int type, int author, int level, Integer parent, String term, String def, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		super(id, type, author, level, parent, def, db_config);
		this.term = term;
	}
	public String getTerm() {
		return term;
	}
}
