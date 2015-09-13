package listnote;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CurrentUser extends AnyUser {
	protected boolean _is_logged_in = false;
	protected String password;
	
	protected DatabaseConfig db_config;
	protected Database dbc;
	
	public CurrentUser(String username, String password, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.password = password;
		this.username = username;
		
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public CurrentUser(DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.db_config = db_config;
		System.out.println(this.getClass().getSimpleName());
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public CurrentUser setUser(String user) {
		this.username = user;
		return this;
	}
	public CurrentUser setPassword(String user) {
		this.username = user;
		return this;
	}
	
//	public boolean authenticate_session(String user, String pass) {
//		if(this._verify_user(user, pass)) {
//			this.pull_information();
//			this._is_logged_in = true;
//			this._exists = true;
//			return true;
//		}
//		else {
//			this._is_logged_in = false;
//			return false;
//		}
//	}
//	
	
	public String authenticate_from_scratch() {
		try {
			boolean independent_transaction = this.dbc.begin();
			PreparedStatement stmt = this.dbc.prepare("SELECT id, password FROM users WHERE username=?");
			stmt.setString(1, this.username);
			ResultSet result = stmt.executeQuery();
			try {
				result.next();
				if(BCrypt.checkpw(this.password, result.getString("password"))) {
					SessionIdentifierGenerator r = new SessionIdentifierGenerator();
					String token = r.nextSessionId();
					String secret = r.nextSessionId();
					PreparedStatement cookie_upd_stmt = this.dbc.prepare("INSERT INTO users (token, secret, start) VALUES (?, ?, NOW()) WHERE id=?;");
					cookie_upd_stmt.setString(1, token);
					cookie_upd_stmt.setString(2, secret);
					cookie_upd_stmt.setInt(3, result.getInt("id"));
					cookie_upd_stmt.executeUpdate();
					
					Mac crypt;
					SecretKeySpec secret_spec = new SecretKeySpec(secret.getBytes(),"HMacSHA256");
					try {
						crypt = Mac.getInstance("HmacSHA256");
						crypt.init(secret_spec);
						byte[] digest = crypt.doFinal(token.getBytes());
						return this.username + ":" + token + ":" + (new String(digest));
					} catch (NoSuchAlgorithmException | InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
				if(independent_transaction) this.dbc.rollback();
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public boolean authenticate_remember_me(String cookie) {
		if(cookie!="") {
			String[] crumbs = cookie.split("%3A");
			if(this._verify_remember_me(crumbs[0], crumbs[1], crumbs[2])) {
				this.username = crumbs[0];
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
		else {
			return false;
		}
	}
	
//	protected boolean _verify_user(String user, String pass) {
//		try {
//			PreparedStatement stmt = this.dbc.prepare("SELECT password FROM users WHERE username=?");
//			stmt.setString(1, user);
//			ResultSet results = stmt.executeQuery();
//			try {
//				results.next();
//				return BCrypt.checkpw(pass, results.getString("password"));
//			}
//			catch(SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		catch(SQLException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
	protected boolean _verify_remember_me(String user, String token, String mac) {
		try {
			PreparedStatement stmt = this.dbc.prepare("SELECT secret FROM users WHERE username=?");
			stmt.setString(1, user);
			ResultSet result = stmt.executeQuery();
			try {
				if(result.next()) {
					String secret = result.getString("secret");
					if(secret!=null) {
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
							e.printStackTrace();
						}
						catch(NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
						finally {
							this._is_logged_in = false;
						}	
					}
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				this._is_logged_in = false;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			this._is_logged_in = false;
		}
		return false;
	}
}
