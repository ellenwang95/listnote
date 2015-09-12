package test;
import static spark.Spark.*;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class Main {
	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		get("/*", (request, response) -> {
			System.out.println(request.getClass().getSimpleName());
			return "SOMETHING!";
		});
//		Mock m = new Mock();
//		Mock n = new Mock(m);
//		get("/*", (request, response) -> {
//			Map<String,Object> root = new HashMap<String,Object>();
//			Configuration cfg = new Configuration();
//	    	cfg.setDirectoryForTemplateLoading(new File("/Users/derek-lam/Documents/workspace/listnote/src/main/resources/templates/"));
//	    	cfg.setDefaultEncoding("UTF-8");
//	    	cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
//			StringWriter sw = new StringWriter();
//			root.put("n", n);
//			cfg.getTemplate("mock.ftl").process(root, sw);
//			System.out.println(sw.toString());
//			return "SOMETHING!";
//		});
	}
}
