package listnote;
import static spark.Spark.*;
import java.sql.*;

public class Main {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/listnote_auth";
    public static void main(String[] args) throws SQLException {
		DatabaseConfig db_config = new DatabaseConfig();
    	Connection mysqli;
    	try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mysqli = DriverManager.getConnection(DB_URL, "root", "");
		ResultSet result = mysqli.createStatement().executeQuery("SELECT * FROM users");
		while(result.next()) {
			System.out.println(result.getString("id"));
		}
    }
}