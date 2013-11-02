package models;

public class Checkin extends Post
{
	private String message;
	private String location;

	public Checkin(long id, long time, String message, String location, int likes)
	{
		super(id, time, likes);
		this.message = message;
		this.location = location;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}
	
}
