package helpers;

import java.util.Comparator;

import models.Post;

public class PostLikesComparator implements Comparator<Post>
{
	@Override
	public int compare(Post postOne, Post postTwo)
	{
		return ((Long) postTwo.getTime()).compareTo(postOne.getTime());
	}
}
