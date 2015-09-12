package listnote;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.*;

public class Renderer {
	Configuration cfg;
	public Renderer(Configuration cfg) {
		this.cfg = cfg;
	}
	public String render_navigation(String path, CurrentUser user) {
		StringWriter sw = new StringWriter();
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("path", path);
		root.put("user", user);
		try {
			cfg.getTemplate("nav.ftl").process(root, sw);
			return sw.toString();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
