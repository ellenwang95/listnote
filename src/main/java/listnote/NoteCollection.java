package listnote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import listnote.TagCollection.AssocTagCallable;

public class NoteCollection {
	public List<Note> units = new ArrayList<Note>();
	protected DatabaseConfig db_config;
	protected Database dbc;
	
	protected interface AssocNoteCallable {
		public void method(Note unit, Integer key);
	}
	
	public NoteCollection(DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public void push(Note unit) {
		this.units.add(unit);
	}
	public Note first() {
		return units.get(0);
	}
	public Note last() {
		return units.get(units.size()-1);
	}
	public int count() {
		return units.size();
	}
	public boolean is_empty() {
		return units.size()==0;
	}
	public void iterate(AssocNoteCallable callable) {
		int key = 0;
		for(Note entry : units) {
			callable.method(entry, key++);
		}
	}
	public List<Note> getUnits() {
		return units;
	}
}