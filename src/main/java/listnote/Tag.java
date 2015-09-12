package listnote;

public class Tag {
	public int id, point_id, instances;
	public String tag;
	public Tag(int id, String tag, int point_id, int instances) {
		this.id = id;
		this.tag = tag;
		this.instances = instances;
		this.point_id = point_id;
	}
	public int getId() {
		return id;
	}
	public int getInstances() {
		return instances;
	}
	public String getTag() {
		return tag;
	}
}
