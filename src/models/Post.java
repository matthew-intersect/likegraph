package models;

public class Post
{
	private long id;
	private long time;
	private int likeCount;
	
	public Post(long id, long time, int likes)
	{
		this.id = id;
		this.time = time;
		this.likeCount = likes;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public int getLikeCount()
	{
		return likeCount;
	}

	public void setLikeCount(int likeCount)
	{
		this.likeCount = likeCount;
	}
	
	public String getListDisplay()
	{
		return String.valueOf(id);
	}
}
