package listnote;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointCollection {
	public List<PointWrapper> units = new ArrayList<PointWrapper>();
	protected Map<Integer, Integer> reverse_map = new HashMap<Integer, Integer>(), _built = new HashMap<Integer, Integer>();
	protected DatabaseConfig db_config;
	protected Database dbc;
	public class PointWrapper {
		public Point unit;
		public List<Integer> descendants;
		public Integer ancestor;
		public PointWrapper(Point unit, List<Integer> descendants, Integer ancestor) {
			this.unit = unit; this.descendants = descendants; this.ancestor = ancestor;
		}
	}
	public PointCollection(DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	public PointCollection(List<PointWrapper> units, Map<Integer, Integer> reverse_map, DatabaseConfig db_config) throws IllegalArgumentException, SQLException {
		this.units = units;
		this.reverse_map = reverse_map;
		this.db_config = db_config;
		this.dbc = db_config.connect(this.getClass().getSimpleName());
	}
	
	public Integer push(Point unit, Integer ancestor) {
		Integer ret;
		if((ret = this.reverse_map.get(unit.id)) != null) {
			return ret;
		}
		else {
			if(this.units.get(ancestor) != null) {
				this.units.add(new PointWrapper(unit, new ArrayList<Integer>(), null));
			}
			else {
				this.units.add(new PointWrapper(unit, new ArrayList<Integer>(), ancestor));
			}
			return this.count()-1;
		}
	}
	public Integer push(Point unit) {
		return this.push(unit, null);
	}
	public List<Integer> roots() {
		List<Integer> ret = new ArrayList<Integer>();
		for(Integer key : this._built.keySet()) {
			ret.add(key);
		}
		return ret;
	}
	public PointWrapper first() {
		return units.get(0);
	}
	public PointWrapper last() {
		return units.get(units.size()-1);
	}
	public int count() {
		return units.size();
	}
	public boolean is_empty() {
		return units.size()==0;
	}
	public void iterate(AssocPointCallable callable) {
		int key = 0;
		for(PointWrapper entry : units) {
			callable.method(entry.unit, key++);
		}
	}
	public void iterate_tree(Integer cursor, AssocPointCallable callable) {
		callable.method(units.get(cursor).unit, cursor);
		for(Integer descendant : units.get(cursor).descendants) {
			this.iterate_tree(descendant, callable);
		}
	}
	public List<PointWrapper> flatten_subtree(Integer cursor) throws IndexOutOfBoundsException {
		if(units.get(cursor) == null) throw new IndexOutOfBoundsException("Cursor "+cursor+" does not exist in PointsCollection::units");
		List<PointWrapper> ret = new ArrayList<PointWrapper>();
		ret.add(units.get(cursor));
		for(Integer descendant : units.get(cursor).descendants) {
			ret.addAll(this.flatten_subtree(descendant));
		}
		return ret;
	}
	public Integer build(Integer cursor, boolean _bare, boolean _subtree) {
		//returns root cursor
		if(_subtree) {
			return this._traverse_up(cursor, new ArrayList<Integer>(cursor), _bare, false);
		}
		else {
			this._traverse_down(cursor, _bare);
			return cursor;
		}
	}
	public void clear() {
		this.units.clear();
	}
	public Integer _traverse_up(Integer cursor, ArrayList<Integer> path_trace, boolean _bare, boolean _superpath) {
		PointWrapper current_unit = this.units.get(cursor);
		Point ancestor = units.get(current_unit.ancestor).unit;
		if(ancestor!=null) {
			if(!_bare && !ancestor._db_pulled) {
				ancestor.pull_information();
			}
			return this._traverse_up(current_unit.ancestor, path_trace, _bare, _superpath);
		}
		else {
			String sql = "SELECT p2.parent_id AS parent_id, p2.id AS id FROM points p LEFT JOIN points p2 ON p2.id=p.parent_id WHERE p.u_id"+current_unit.unit.id+";";
			try {
				ResultSet result = this.dbc.query(sql);
				try {
					result.next();
				}
				catch(SQLException e) {
					System.out.println("Point "+current_unit.unit.id+" does not exist.");
				}
				
				if(result.getString("parent_id")==null) {
					this._relevel_path_from_root(path_trace);
					if(this._built.get(cursor) != null && !_superpath) {
						this._traverse_down(cursor, _bare);
					}
					return cursor;
				}
				else {
					current_unit.ancestor = result.getInt("parent_id");
					Point parent = new Point(result.getInt("parent_id"), this.db_config);
					parent.pull_information();
					this.units.add(new PointWrapper(parent, new ArrayList<Integer>(cursor), null));
					path_trace.add(cursor);
					return this._traverse_up(result.getInt("parent_id"), path_trace, _bare, _superpath);
				}
			}
			catch(SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	protected void _relevel_path_from_root(ArrayList<Integer> path_trace) {
		for(int i=0; i<path_trace.size(); i++) {
			this.units.get(path_trace.get(i)).unit.level = path_trace.size()-1-i;
		}
	}
	protected void _traverse_down(Integer cursor, boolean _bare) {
		PointWrapper current_unit = this.units.get(cursor);
		String sql = "SELECT id FROM points WHERE parent_id = "+current_unit.unit.id+";";
		try {
			ResultSet result = this.dbc.query(sql);
			List<Integer> child_ids = new ArrayList<Integer>();
			for(Integer descendant : current_unit.descendants) {
				child_ids.add(this.units.get(descendant).unit.id);
			}
			try {
				while(result.next()) {
					int next_cursor, id = result.getInt("id");
					if(!child_ids.contains(id)) {
						Point new_point = new Point(id, this.db_config);
						if(!_bare) {
							new_point.pull_information();
						}
						new_point.level = current_unit.unit.level+1;
						next_cursor = this.units.size();
						this.units.add(new PointWrapper(new_point, new ArrayList<Integer>(), cursor));
					}
					else {
						next_cursor = this.reverse_map.get(id);
					}
					this._traverse_down(next_cursor, _bare);
				}
			}
			catch(SQLException e) {
				System.out.println("Point "+current_unit.unit.id+" doesn't exist in PointCollection::_traverse_down (somehow?!)");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<PointWrapper> getUnits() {
		return this.units; 
	}
	public Map<Integer, Integer> getReverse_map() {
		return this.reverse_map;
	}
}
