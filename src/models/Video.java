package models;

public class Video extends Post
{
	private String name;
	private String description;
	private String source;
	private String picture;

	public Video(long id, long time, String name, String description, String source, String thumbnail, int likes)
	{
		super(id, time, likes);
		this.name = name;
		this.description = description;
		this.source = source;
		this.setPicture(thumbnail);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

}
