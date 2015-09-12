package listnote;
import static spark.Spark.*;

import java.io.File;
import java.io.StringWriter;
import java.sql.*;

import freemarker.template.*;

public class Main {
	protected static Configuration load_freemarker_configuration() {
    	Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
    	cfg.setDirectoryForTemplateLoading(new File("templates/"));
    	cfg.setDefaultEncoding("UTF-8");
    	cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
	}
    public static void main(String[] args) throws SQLException {
    	staticFileLocation("public/");
    	Configuration cfg = Main.load_freemarker_configuration();
    	CurrentUser current_user = new CurrentUser();
    	Renderer renderer = new Renderer(cfg);
    	get("/", (request, response) -> {
    		//index.ftl
    		Map<String,Object> root = new HashMap<String,Object>();
    		root.put("rendered_navigation", renderer.render_navigation(request.pathInfo(), current_user));
    	});
    }
}