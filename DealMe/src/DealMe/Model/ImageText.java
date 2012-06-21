package DealMe.Model;

public class ImageText {

	private int _id;
	private String _description;
	
	public ImageText(int id, String description)
	{
		_id = id;
		_description = description;
	}
	
	
	public void set_id(int _id) {
		this._id = _id;
	}
	
	public int get_id() {
		return _id;
	}
	
	public void set_description(String _description) {
		this._description = _description;
	}
	
	public String get_description() {
		return _description;
	}
	
	
}
