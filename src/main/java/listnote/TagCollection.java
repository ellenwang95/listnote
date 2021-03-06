package listnote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TagCollection {
	public List<Tag> units = new ArrayList<Tag>();
	protected DatabaseConfig db_config;
	protected Database dbc;
	
	protected interface AssocTagCallable {
		public void method (Tag entry, Integer key);
	}
	
	public TagCollection(DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public void push(Tag unit) {
		this.units.add(unit);
	}
	public Tag first() {
		return units.get(0);
	}
	public Tag last() {
		return units.get(units.size()-1);
	}
	public int count() {
		return units.size();
	}
	public boolean is_empty() {
		return units.size()==0;
	}
	public void iterate(AssocTagCallable callable) {
		int key = 0;
		for(Tag entry : units) {
			callable.method(entry, key++);
		}
	}
	public List<Tag> getUnits() {
		return units;
	} 
}
