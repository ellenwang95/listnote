package listnote;
import java.sql.*;
import java.util.Arrays;

public class Database {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://%s/%s";
	static private final String[] isolation_levels = {
		"READ REPEATABLE",
		"READ UNCOMMITTED",
		"READ COMMITTED",
		"SERIALIZABLE"
	};
	Connection mysqli = null;
	
	public Database(String host, String user, String pass, String database) throws SQLException {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mysqli = DriverManager.getConnection(String.format(DB_URL, host, database), user, pass);
	}
	public boolean begin() throws SQLException {
		return this.begin("READ REPEATABLE");
	}
	public boolean begin(String isolation_level) throws SQLException {
		ResultSet result = this.query("SELECT @@autocommit AS autocommit");
		result.next();
		if(result.getInt("autocommit") != 0) {
			return false;
		}
		result.close();
		String sql = "SET TRANSACTION ISOLATION LEVEL ";
		if(Arrays.asList(Database.isolation_levels).contains(isolation_level)) {
			sql += isolation_level;
		}
		else {
			sql += "READ REPEATABLE";
		}
		this.query(sql);
		mysqli.setAutoCommit(false);
		return true;
	}
	public boolean commit() throws SQLException {
		ResultSet result = this.query("SELECT @@autocommit AS autocommit;");
		result.next();
		if(result.getInt("autocommit") != 0) {
			return false;
		}
		else {
			mysqli.commit();
			mysqli.setAutoCommit(true);
			return true;
		}
	}
	public boolean rollback() throws SQLException {
		ResultSet result = this.query("SELECT @@autocommit AS autocommit;");
		result.next();
		if(result.getInt("autocommit") != 0) {
			return false;
		}
		else {
			mysqli.rollback();
			mysqli.setAutoCommit(true);
			return true;
		}
	}
	protected Statement stmt() throws SQLException {
		return mysqli.createStatement();
	}
	protected ResultSet query(String sql) throws SQLException {
		return this.stmt().executeQuery(sql);
	}
}