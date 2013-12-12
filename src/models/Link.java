package models;

public class Link extends Post
{
	private String message;
	private String link;

	public Link(long id, long time, String message, String link, int likes)
	{
		super(id, time, likes);
		this.message = message;
		this.link = link;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}
	
	@Override
	public String getListDisplay()
	{
		return message;
	}

}
