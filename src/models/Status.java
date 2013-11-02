package models;

public class Status extends Post
{
	private String status;

	public Status(long id, long time, int likes, String status)
	{
		super(id, time, likes);
		this.setStatus(status);
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

}
