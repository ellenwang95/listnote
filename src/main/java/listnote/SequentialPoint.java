package listnote;

import java.sql.*;

public class SequentialPoint extends Point {

	public int num;
	
	public SequentialPoint(int id, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		super(id, db_config);
		// TODO Auto-generated constructor stub
	}
	
}
