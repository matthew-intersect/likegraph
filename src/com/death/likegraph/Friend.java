package com.death.likegraph;

public class Friend
{
	private String name;
	private int count;
	
	public Friend()
	{
	}
	
	public Friend(String name, int count)
	{
		this.count = count;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
