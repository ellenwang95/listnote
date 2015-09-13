package listnote;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.*;

public class Renderer {
	Configuration cfg;
	public Renderer(Configuration cfg) {
		this.cfg = cfg;
	}
	public String render_login() throws TemplateException, IOException {
		StringWriter sw = new StringWriter();
		Map<String,Object> root = new HashMap<String,Object>();
		cfg.getTemplate("login.ftl").process(root, sw);
		return sw.toString();
	}
	public String render_app_nav(String path, CurrentUser user, NoteCollectionFactory factory) throws IllegalArgumentException, SQLException {
		StringWriter sw = new StringWriter();
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("path", path);
		root.put("user", user);
		root.put("note_collection", factory.spawn_for_user());
		try {
			this.cfg.getTemplate("app_nav.ftl").process(root, sw);
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
	public String render_list_view(NoteCollectionFactory factory) throws IllegalArgumentException, SQLException, TemplateException, IOException {
		StringWriter sw = new StringWriter();
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("note_collection", factory.spawn_for_user());
		this.cfg.getTemplate("list_view.ftl").process(root, sw);
		return sw.toString();
	}
	public String render_note(Note note) throws IllegalArgumentException, SQLException {
		StringWriter sw = new StringWriter();
		Map<String,Object> root = new HashMap<String,Object>();
		
		PointCollection collection = note.pull_children();
		List<Integer> cursors = new ArrayList<Integer>();
//		List<String> point_cluster = new ArrayList<String>();
		Template[] list_templates = new Template[2];
		try {
			list_templates[0] = this.cfg.getTemplate("bullet_list.ftl");
			list_templates[1] = this.cfg.getTemplate("def_list.ftl");
			list_templates[2] = this.cfg.getTemplate("bio_list.ftl");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		int prev_type = -1;
		StringBuilder ret = new StringBuilder();
		// collection.iterate((unit, key)->{
		// 	System.out.println(unit.body);
		// });
		List<Integer> roots = collection.roots();
		for(Integer root_cursor : roots) {
			collection.iterate_tree(root_cursor, (unit, key)->{
				System.out.println(unit.id);
//				if(prev_type != unit.type) {
//					root.clear();
//					root.put("cursors", cursors);
//					root.put("point_collection", collection);
//					sw.getBuffer().setLength(0);
//					try {
//						list_templates[unit.type-1].process(root, sw);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					ret.append(sw.toString());
//					
//					cursors.clear();
//				}
//				else {
//					cursors.add(key);
//				}
			});
		}
		System.out.println(ret.toString());
		return ret.toString();
	}
}
