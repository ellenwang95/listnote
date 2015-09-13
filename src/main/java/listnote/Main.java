package listnote;
import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import freemarker.template.*;
import spark.Request;
import spark.Response;

public class Main {
	static final DatabaseConfig DB_CONFIG = new DatabaseConfig();
	protected static Configuration load_freemarker_configuration() throws IOException {
    	Configuration cfg = new Configuration();
    	cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir")+"/src/main/resources/templates/"));
//    	System.out.println(System.getProperty("user.dir")+"/src/main/resources/templates/");
    	cfg.setDefaultEncoding("UTF-8");
    	cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    	return cfg;
	}
	
	protected interface HTTPHandler {
		public String method(Request request, Response response);
	}
	
    public static void main(String[] args) throws SQLException, IOException {
    	staticFileLocation("/public");
    	Configuration cfg = Main.load_freemarker_configuration();
    	CurrentUser current_user = new CurrentUser(DB_CONFIG);
    	NoteCollectionFactory ncfactory = new NoteCollectionFactory(current_user, DB_CONFIG);
    	Renderer renderer = new Renderer(cfg);
    	
    	HTTPHandler authenticated_view = (request, response) -> {
    		Writer sw = new StringWriter();
    		Map<String,Object> root = new HashMap<String,Object>();
    		String note_id = request.queryParams("note_id");
    		if(note_id != "" && note_id != null) {
        		try {
        			Note note = new Note(Integer.parseInt(request.queryParams("note_id")), DB_CONFIG);
        			root.put("rendered_body", renderer.render_note(note));
        		}
        		catch(Exception e) {
        			e.printStackTrace();
        		}
    		}
    		else {
    			try {
    				root.put("rendered_body", renderer.render_list_view(ncfactory));
	    		}
	    		catch(Exception e) {
	    			e.printStackTrace();
	    		}
    		}
    		try {
				root.put("rendered_navigation", renderer.render_app_nav(request.pathInfo(), current_user, ncfactory));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		try {
        		cfg.getTemplate("index.ftl").process(root, sw); //why so many exceptions ;_;
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		return sw.toString();
    	};
    	
    	HTTPHandler unauthenticated_view = (request, response) -> {
    		Writer sw = new StringWriter();
    		Map<String,Object> root = new HashMap<String,Object>();
    		try {
				cfg.getTemplate("landing.ftl").process(root, sw);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return sw.toString();
    	};
    	
    	post("/", (request, response) -> {
    		String username, password, cookie_str;
//    		System.out.println(request.)
    		if((username=request.queryParams("u")) != "" && request.queryParams("u")!=null && (password = request.queryParams("p")) != "" && request.queryParams("p") != null) {
    			current_user.setUser(username).setPassword(password);
    			if((cookie_str = current_user.authenticate_from_scratch()) != null) {
    				System.out.println(cookie_str);
    				response.cookie("rememberme", cookie_str);
    				return authenticated_view.method(request, response);
    			}
    			else {
    				return unauthenticated_view.method(request, response);
    			}
    		}
    		return "";
    	});
    	get("/", (request, response) -> {
    		String cookie = request.cookie("rememberme");
    		if(current_user.authenticate_remember_me(cookie)) {
    			return authenticated_view.method(request, response);
    		}
    		else {
				return unauthenticated_view.method(request, response);
    		}
    	});
    	get("/login/", (request, response) -> {
    		String cookie = request.cookie("rememberme");
    		if(current_user.authenticate_remember_me(cookie)) {
    			response.redirect("/");
    			return "";
    		}
    		else {
				return renderer.render_login();
    		}
    	});
    	get("/ajax/", (request, response) -> {
    		String cookie = request.cookie("rememberme");
    		if(current_user.authenticate_remember_me(cookie)) {
    		}
			return "";
    	});
    }
}