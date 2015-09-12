package listnote;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CurrentUser extends AnyUser {
	protected BCrypt bcrypt;
	protected boolean _is_logged_in = false;
	protected String password;
	
	protected DatabaseConfig db_config;
	protected Database dbc;
	
	public CurrentUser(BCrypt bcrypt, boolean _is_logged_in, String password) throws IllegalArgumentException, SQLException {
		this.bcrypt = bcrypt;
		this._is_logged_in = _is_logged_in;
		this.password = password;
		
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	
	public boolean authenticate_session(String user, String pass) {
		if(this._verify_user(user, pass)) {
			this.pull_information();
			this._is_logged_in = true;
			this._exists = true;
			return true;
		}
		else {
			this._is_logged_in = false;
			return false;
		}
	}
	
	public boolean authenticate_remember_me(String cookie) {
		String[] crumbs = cookie.split(":");
		if(this._verify_remember_me(crumbs[0], crumbs[1], crumbs[2])) {
			this.pull_information();
			this._is_logged_in = true;
			this._exists = true;
			return true;
		}
		else {
			this._is_logged_in = false;
			return false;
		}
	}
	
	protected boolean _verify_user(String user, String pass) {
		try {
			PreparedStatement stmt = this.dbc.prepare("SELECT password FROM users WHERE username=?");
			stmt.setString(1, user);
			ResultSet results = stmt.executeQuery();
			try {
				results.next();
				return BCrypt.checkpw(pass, results.getString("password"));
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	protected boolean _verify_remember_me(String user, String token, String mac) {
		try {
			PreparedStatement stmt = this.dbc.prepare("SELECT secret FROM users WHERE user=?");
			ResultSet result = stmt.executeQuery();
			try {
				result.next();
				String secret = result.getString("secret");
				try {
					Mac crypt = Mac.getInstance("HmacSHA256");
					SecretKeySpec secret_spec = new SecretKeySpec(secret.getBytes(),"HMacSHA256");
					crypt.init(secret_spec);
					byte[] digest = crypt.doFinal(token.getBytes());
					if(mac.getBytes() == digest) {
						this._exists = true;
						this._is_logged_in = true;
						return true;
					}
					else {
						this._is_logged_in = false;
						return false;
					}
				}
				catch(InvalidKeyException e) {
					System.out.println(e.getMessage());
				}
				catch(NoSuchAlgorithmException e) {
					System.out.println(e.getMessage());
				}
				finally {
					this._is_logged_in = false;
				}
			}
			catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			finally {
				this._is_logged_in = false;
			}
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		finally {
			this._is_logged_in = false;
		}
		return false;
	}
}
