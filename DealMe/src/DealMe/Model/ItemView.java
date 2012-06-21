package DealMe.Model;

public class ItemView 
{
	private String _imageLink;
	private String _description;
	private String _link;
	private String _title;
	
	public ItemView(String imageLink, String description, String link, String title)
	{
		_imageLink = imageLink;
		_description = description;
		_link = link;
		_title = title;
	}
	
	public ItemView()
	{
		
	}
	
	public ItemView(String object)
	{
		String [] arr = object.split("\\|");
		_imageLink = arr[0];
		_description = arr[1];
		_link = arr[2];
		_title = arr[3];
	}
	
	public void set_image(String _image) {
		this._imageLink = _image;
	}
	public String get_image() {
		return _imageLink;
	}
	public void set_description(String _description) {
		this._description = _description;
	}
	public String get_description() {
		return _description;
	}
	public void set_link(String _link) {
		this._link = _link;
	}
	public String get_link() {
		return _link;
	}
	
	public void set_title(String title) {
		this._title = title;
	}

	public String get_title() {
		return _title;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		return _imageLink+"|"+_description+"|"+_link+"|"+_title;
	}


	
}
