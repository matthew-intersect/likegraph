package models;

public class Friend
{
	private long id;
	private String name;
	private String picture;
	private int count;
	
	public Friend(long id, String name, String picture, int count)
	{
		this.id = id;
		this.name = name;
		this.picture = picture;
		this.count = count;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPicture()
	{
		return picture;
	}

	public void setPicture(String picture)
	{
		this.picture = picture;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}
}
