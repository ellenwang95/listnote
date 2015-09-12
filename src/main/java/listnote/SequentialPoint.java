package listnote;

import java.sql.*;

public class SequentialPoint extends Point {

	private int num;
	
	public SequentialPoint(int id, DatabaseConfig db_config, int num) throws IllegalArgumentException, SQLException {
		super(id, db_config);
		this.num = num;
	}

	
}
