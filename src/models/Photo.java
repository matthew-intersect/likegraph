package models;

public class Photo extends Post
{
	private String message;
	private String source;

	public Photo(long id, long time, String message, String source, int likes)
	{
		super(id, time, likes);
		this.message = message;
		this.source = source;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}
}
