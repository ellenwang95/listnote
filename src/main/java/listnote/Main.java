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
    	cfg.setDefaultEncoding("UTF-8");
    	cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    	return cfg;
	}
	
	protected interface HTTPHandler {
		public String method(Request request, Response response);
	}
	
    public static void main(String[] args) throws SQLException, IOException {
    	staticFileLocation(System.getProperty("user.dir")+"/src/main/resources/public/");
    	Configuration cfg = Main.load_freemarker_configuration();
    	CurrentUser current_user = new CurrentUser(DB_CONFIG);
    	NoteCollectionFactory ncfactory = new NoteCollectionFactory(current_user, DB_CONFIG);
    	Renderer renderer = new Renderer(cfg);
    	
    	HTTPHandler authenticated_view = (request, response) -> {
    		Writer sw = new StringWriter();
    		Map<String,Object> root = new HashMap<String,Object>();
    		if(request.queryParams("node_id") != "") {
    			Note note = new Note(Integer.parseInt(request.queryParams("note_id")), DB_CONFIG);
        		try {
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
    		root.put("rendered_navigation", renderer.render_app_nav(request.pathInfo(), current_user));
    		try {
        		cfg.getTemplate("index.ftl").process(root, sw); //why so many exceptions ;_;
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    		return sw.toString();
    	};
    	
    	HTTPHandler unauthenticated_index = (request, response) -> {
    		Writer sw = new StringWriter();
    		Map<String,Object> root = new HashMap<String,Object>();
    		return "";
    	};
    	
    	post("/", (request, response) -> {
    		String username, password, cookie_str;
    		if((username=request.queryParams("username")) != "" && (password = request.queryParams("password")) != "") {
    			current_user.setUser(username).setPassword(password);
    			if((cookie_str = current_user.authenticate_from_scratch()) != null) {
    				response.cookie("rememberme", cookie_str, 3600, true);
    				return authenticated_view.method(request, response);
    			}
    			else {
    				
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
    			
    		}
			return "";
    	});
    	get("/ajax/", (request, response) -> {
    		String cookie = request.cookie("rememberme");
    		if(current_user.authenticate_remember_me(cookie)) {
    		}
			return "";
    	});
    }
}