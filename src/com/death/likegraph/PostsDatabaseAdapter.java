package com.death.likegraph;

import java.util.ArrayList;

import models.Checkin;
import models.Friend;
import models.Link;
import models.Photo;
import models.Post;
import models.Status;
import models.Video;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PostsDatabaseAdapter 
{
	static final String DATABASE_NAME = "likegraph.db";
	static final int DATABASE_VERSION = 1;
	
	static final String STATII_TABLE_CREATE = "create table statii" +
	        "( id long primary key, time long, status text); ";
	static final String LINKS_TABLE_CREATE = "create table links" +
            "( id long primary key, time long, message text, link text); ";
	static final String CHECKINS_TABLE_CREATE = "create table checkins" +
            "( id long primary key, time long, poster text, message text, location text); ";
	static final String PHOTOS_TABLE_CREATE = "create table photos" + 
			"( id long primary key, time long, poster text, message text, source text); ";
	static final String VIDEOS_TABLE_CREATE = "create table videos" + 
			"( id long primary key, time long, poster text, name text, description text, source text, picture text); ";
	static final String LIKE_TABLE_CREATE = "create table likes" + 
	        "( id integer primary key autoincrement, name text, post_id long); ";
	static final String FRIENDS_TABLE_CREATE = "create table friends" +
            "( id long primary key, name text, picture text);";
	public SQLiteDatabase db;
	private final Context context;
	private DatabaseHelper dbHelper;
	
	public PostsDatabaseAdapter(Context _context) 
	{
		context = _context;
		dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public PostsDatabaseAdapter open() throws SQLException 
	{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() 
	{
		db.close();
	}
	
	public  SQLiteDatabase getDatabaseInstance()
	{
		return db;
	}
	
	public void addStatus(long id, long time, String status)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("status", status);
	
		db.insert("statii", null, newValues);
	}
	
	public void addLink(long id, long time, String message, String link)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("message", message);
		newValues.put("link", link);
	
		db.insert("links", null, newValues);
	}
	
	public void addCheckin(long id, long time, String from, String message, String location)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("message", message);
		newValues.put("location", location);
		newValues.put("poster", from);
	
		db.insert("checkins", null, newValues);
	}
	
	public void addPhoto(long id, long time, String from, String message, String source)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("message", message);
		newValues.put("source", source);
		newValues.put("poster", from);
	
		db.insert("photos", null, newValues);
	}
	
	public void addVideo(long id, long time, String from, String name, String description, String source, String thumbnail)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("name", name);
		newValues.put("description", description);
		newValues.put("source", source);
		newValues.put("poster", from);
		newValues.put("picture", thumbnail);
	
		db.insert("videos", null, newValues);
	}
	
	public void addFriend(long id, String name, String link)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("name", name);
		newValues.put("picture", link);
		
		db.insert("friends", null, newValues);
	}
	
	public void addLike(String name, long status)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("name", name);
		newValues.put("post_id", status);
	
		db.insert("likes", null, newValues);
	}
	
	public void clearTables()
	{
		db.execSQL("DROP TABLE IF EXISTS statii");
		db.execSQL("DROP TABLE IF EXISTS likes");
		db.execSQL("DROP TABLE IF EXISTS links");
		db.execSQL("DROP TABLE IF EXISTS checkins");
		db.execSQL("DROP TABLE IF EXISTS photos");
		db.execSQL("DROP TABLE IF EXISTS videos");
		db.execSQL("DROP TABLE IF EXISTS friends");
		db.execSQL(PostsDatabaseAdapter.STATII_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.LIKE_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.LINKS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.CHECKINS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.PHOTOS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.VIDEOS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.FRIENDS_TABLE_CREATE);
		db.execSQL("delete from statii");
		db.execSQL("delete from likes");
		db.execSQL("delete from links");
		db.execSQL("delete from checkins");
		db.execSQL("delete from photos");
		db.execSQL("delete from videos");
		db.execSQL("delete from friends");
	}
	
	public ArrayList<Friend> getRankedLikes()
	{
		Cursor friendRanksResults = db.rawQuery("select friends.id,friends.name,friends.picture,count(likes.id) as count " +
				"from friends left outer join likes on friends.name=likes.name group by friends.name order by count desc limit 100;", null);
		
		ArrayList<Friend> friendRanks = new ArrayList<Friend>();
		while(friendRanksResults.moveToNext())
		{
			long id = friendRanksResults.getLong(friendRanksResults.getColumnIndex("id"));
			String name = friendRanksResults.getString(friendRanksResults.getColumnIndex("name"));
			String picture = friendRanksResults.getString(friendRanksResults.getColumnIndex("picture"));
			int count = friendRanksResults.getInt(friendRanksResults.getColumnIndex("count"));
			friendRanks.add(new Friend(id, name, picture, count));
		}
		
		return friendRanks;
	}
	
	public int getNumberOfStatii()
	{
		return db.query("statii", null, null, null, null, null, null).getCount();
	}
	
	public int getNumberOfLinks()
	{
		return db.query("links", null, null, null, null, null, null).getCount();
	}
	
	public int getNumberOfCheckins()
	{
		return db.query("checkins", null, null, null, null, null, null).getCount();
	}
	
	public int getNumberOfPhotos()
	{
		return db.query("photos", null, null, null, null, null, null).getCount();
	}
	
	public int getNumberOfVideos()
	{
		return db.query("videos", null, null, null, null, null, null).getCount();
	}
	
	public ArrayList<Post> getPosts(boolean statii, boolean links, boolean checkins, 
			boolean photos, boolean videos, boolean excludeZeroPhotos)
	{
		ArrayList<Post> posts = new ArrayList<Post>();
		if(statii)
			posts.addAll(getStatiiCounts());
		if(links)
			posts.addAll(getLinkCounts());
		if(checkins)
			posts.addAll(getCheckinCounts());
		if(photos)
			posts.addAll(getPhotoCounts(excludeZeroPhotos));
		if(videos)
			posts.addAll(getVideoCounts());
		return posts;
	}
	
	public ArrayList<Post> getStatiiCounts()
	{
		ArrayList<Post> statii = new ArrayList<Post>();
//		Cursor statiiCounts = db.query("statii,likes", new String[]{"statii.status", "count(likes.id)"},
//				"statii.id=?", new String[]{"likes.post_id"}, "statii.status", null, null);
		Cursor statusCounts = db.rawQuery("select statii.id, statii.time, statii.status, count(likes.id) as count from statii " +
				"left outer join likes on statii.id=likes.post_id group by statii.id order by statii.time desc;", null);
		while(statusCounts.moveToNext())
		{
			long id = statusCounts.getLong(statusCounts.getColumnIndex("id"));
			long time = statusCounts.getLong(statusCounts.getColumnIndex("time"));
			String status = statusCounts.getString(statusCounts.getColumnIndex("status"));
			int count = statusCounts.getInt(statusCounts.getColumnIndex("count"));
			statii.add(new Status(id, time, status, count));
		}
		statusCounts.close();
		return statii;
	}
	
	public ArrayList<Post> getLinkCounts()
	{
		ArrayList<Post> links = new ArrayList<Post>();
		Cursor linkCounts = db.rawQuery("select links.id, links.time, links.message, links.link, count(likes.id) as count from links " +
				"left outer join likes on links.id=likes.post_id group by links.id order by links.time desc;", null);
		while(linkCounts.moveToNext())
		{
			long id = linkCounts.getLong(linkCounts.getColumnIndex("id"));
			long time = linkCounts.getLong(linkCounts.getColumnIndex("time"));
			String message = linkCounts.getString(linkCounts.getColumnIndex("message"));
			String link = linkCounts.getString(linkCounts.getColumnIndex("link"));
			int count = linkCounts.getInt(linkCounts.getColumnIndex("count"));
			links.add(new Link(id, time, message, link, count));
		}
		linkCounts.close();
		return links;
	}
	
	public ArrayList<Post> getCheckinCounts()
	{
		ArrayList<Post> checkins = new ArrayList<Post>();
		Cursor checkinCounts = db.rawQuery("select checkins.id, checkins.time, checkins.message, checkins.location, count(likes.id) as count from checkins " +
				"left outer join likes on checkins.id=likes.post_id group by checkins.id order by checkins.time desc;", null);
		while(checkinCounts.moveToNext())
		{
			long id = checkinCounts.getLong(checkinCounts.getColumnIndex("id"));
			long time = checkinCounts.getLong(checkinCounts.getColumnIndex("time"));
			String message = checkinCounts.getString(checkinCounts.getColumnIndex("message"));
			String location = checkinCounts.getString(checkinCounts.getColumnIndex("location"));
			int count = checkinCounts.getInt(checkinCounts.getColumnIndex("count"));
			checkins.add(new Checkin(id, time, message, location, count));
		}
		checkinCounts.close();
		return checkins;
	}
	
	public ArrayList<Post> getPhotoCounts(boolean excludeZeroPhotos)
	{
		ArrayList<Post> photos = new ArrayList<Post>();
		Cursor photoCounts = db.rawQuery("select photos.id, photos.time, photos.message, photos.source, count(likes.id) as count from photos " +
				"left outer join likes on photos.id=likes.post_id group by photos.id order by photos.time desc;", null);
		while(photoCounts.moveToNext())
		{
			long id = photoCounts.getLong(photoCounts.getColumnIndex("id"));
			long time = photoCounts.getLong(photoCounts.getColumnIndex("time"));
			String message = photoCounts.getString(photoCounts.getColumnIndex("message"));
			String source = photoCounts.getString(photoCounts.getColumnIndex("source"));
			int count = photoCounts.getInt(photoCounts.getColumnIndex("count"));
			if((excludeZeroPhotos && count!=0) || !excludeZeroPhotos)
				photos.add(new Photo(id, time, message, source, count));
		}
		photoCounts.close();
		return photos;
	}
	
	public ArrayList<Post> getVideoCounts()
	{
		ArrayList<Post> videos = new ArrayList<Post>();
		Cursor videoCounts = db.rawQuery("select videos.id, videos.time, videos.name, videos.description, videos.source, videos.picture, count(likes.id) as count from videos " +
				"left outer join likes on videos.id=likes.post_id group by videos.id order by videos.time desc;", null);
		while(videoCounts.moveToNext())
		{
			long id = videoCounts.getLong(videoCounts.getColumnIndex("id"));
			long time = videoCounts.getLong(videoCounts.getColumnIndex("time"));
			String name = videoCounts.getString(videoCounts.getColumnIndex("name"));
			String description= videoCounts.getString(videoCounts.getColumnIndex("description"));
			String source = videoCounts.getString(videoCounts.getColumnIndex("source"));
			String picture = videoCounts.getString(videoCounts.getColumnIndex("picture"));
			int count = videoCounts.getInt(videoCounts.getColumnIndex("count"));
			videos.add(new Video(id, time, name, description, source, picture, count));
		}
		videoCounts.close();
		return videos;
	}

	public ArrayList<Friend> getAllFriends()
	{
		Cursor friendsCursor = db.query("friends", null, null, null, null, null, "name asc");
		
		ArrayList<Friend> friends = new ArrayList<Friend>();
		while(friendsCursor.moveToNext())
		{
			long id = friendsCursor.getLong(friendsCursor.getColumnIndex("id"));
			String name = friendsCursor.getString(friendsCursor.getColumnIndex("name"));
			String picture = friendsCursor.getString(friendsCursor.getColumnIndex("picture"));
			friends.add(new Friend(id, name, picture, 0));
		}
		
		return friends;
	}
	
	public ArrayList<Friend> searchFriends(String name)
	{
		Cursor friendsCursor = db.query("friends", null, "name LIKE?", new String[] { "%" + name + "%" }, null, null, "name asc");
		
		ArrayList<Friend> friends = new ArrayList<Friend>();
		while(friendsCursor.moveToNext())
		{
			long id = friendsCursor.getLong(friendsCursor.getColumnIndex("id"));
			String friendName = friendsCursor.getString(friendsCursor.getColumnIndex("name"));
			String picture = friendsCursor.getString(friendsCursor.getColumnIndex("picture"));
			friends.add(new Friend(id, friendName, picture, 0));
		}
		
		return friends;
	}

	public Friend getFriend(long friendId)
	{
		Cursor friend = db.query("friends", null, "id=?", new String[]{String.valueOf(friendId)}, null, null, null);
		friend.moveToFirst();
		long id = friend.getLong(friend.getColumnIndex("id"));
		String name = friend.getString(friend.getColumnIndex("name"));
		String picture = friend.getString(friend.getColumnIndex("picture"));
		return new Friend(id, name, picture, 0);
	}
	
	public ArrayList<Post> getLikedPostsByFriend(long friendId)
	{
		ArrayList<Post> posts = new ArrayList<Post>();
		posts.addAll(getLikedStatusesByFriend(friendId));
		posts.addAll(getLikedLinksByFriend(friendId));
		posts.addAll(getLikedCheckinsByFriend(friendId));
		posts.addAll(getLikedPhotosByFriend(friendId));
		posts.addAll(getLikedVideosByFriend(friendId));
		return posts;
	}

	public ArrayList<Status> getLikedStatusesByFriend(long friendId)
	{
		ArrayList<Status> statuses = new ArrayList<Status>();
		Cursor statusCursor = db.rawQuery("select statii.id, statii.time, statii.status from statii " +
				"left outer join likes on statii.id=likes.post_id inner join friends on likes.name=friends.name " +
				"where friends.id=?;", new String[] {String.valueOf(friendId)});
		while(statusCursor.moveToNext())
		{
			long id = statusCursor.getLong(statusCursor.getColumnIndex("id"));
			long time = statusCursor.getLong(statusCursor.getColumnIndex("time"));
			String status = statusCursor.getString(statusCursor.getColumnIndex("status"));
			statuses.add(new Status(id, time, status, 0));
		}
		return statuses;
	}
	
	public ArrayList<Link> getLikedLinksByFriend(long friendId)
	{
		ArrayList<Link> links = new ArrayList<Link>();
		Cursor linkCursor = db.rawQuery("select links.id, links.time, links.message, links.link from links " +
				"left outer join likes on links.id=likes.post_id inner join friends on likes.name=friends.name " +
				"where friends.id=?;", new String[] {String.valueOf(friendId)});
		while(linkCursor.moveToNext())
		{
			long id = linkCursor.getLong(linkCursor.getColumnIndex("id"));
			long time = linkCursor.getLong(linkCursor.getColumnIndex("time"));
			String message = linkCursor.getString(linkCursor.getColumnIndex("message"));
			String link = linkCursor.getString(linkCursor.getColumnIndex("link"));
			links.add(new Link(id, time, message, link, 0));
		}
		return links;
	}
	
	public ArrayList<Checkin> getLikedCheckinsByFriend(long friendId)
	{
		ArrayList<Checkin> checkins = new ArrayList<Checkin>();
		Cursor checkinCursor = db.rawQuery("select checkins.id, checkins.time, checkins.message, checkins.location from checkins " +
				"left outer join likes on checkins.id=likes.post_id inner join friends on likes.name=friends.name " +
				"where friends.id=?;", new String[] {String.valueOf(friendId)});
		while(checkinCursor.moveToNext())
		{
			long id = checkinCursor.getLong(checkinCursor.getColumnIndex("id"));
			long time = checkinCursor.getLong(checkinCursor.getColumnIndex("time"));
			String message = checkinCursor.getString(checkinCursor.getColumnIndex("message"));
			String location = checkinCursor.getString(checkinCursor.getColumnIndex("location"));
			checkins.add(new Checkin(id, time, message, location, 0));
		}
		return checkins;
	}
	
	public ArrayList<Photo> getLikedPhotosByFriend(long friendId)
	{
		ArrayList<Photo> photos = new ArrayList<Photo>();
		Cursor photoCursor = db.rawQuery("select photos.id, photos.time, photos.message, photos.source from photos " +
				"left outer join likes on photos.id=likes.post_id inner join friends on likes.name=friends.name " +
				"where friends.id=?;", new String[] {String.valueOf(friendId)});
		while(photoCursor.moveToNext())
		{
			long id = photoCursor.getLong(photoCursor.getColumnIndex("id"));
			long time = photoCursor.getLong(photoCursor.getColumnIndex("time"));
			String message = photoCursor.getString(photoCursor.getColumnIndex("message"));
			String source = photoCursor.getString(photoCursor.getColumnIndex("source"));
			photos.add(new Photo(id, time, message, source, 0));
		}
		return photos;
	}
	
	public ArrayList<Video> getLikedVideosByFriend(long friendId)
	{
		ArrayList<Video> videos = new ArrayList<Video>();
		Cursor videoCursor = db.rawQuery("select videos.id, videos.time, videos.name, videos.description, videos.source, " +
				"videos.picture from videos left outer join likes on videos.id=likes.post_id inner join friends on " +
				"likes.name=friends.name where friends.id=?;", new String[] {String.valueOf(friendId)});
		while(videoCursor.moveToNext())
		{
			long id = videoCursor.getLong(videoCursor.getColumnIndex("id"));
			long time = videoCursor.getLong(videoCursor.getColumnIndex("time"));
			String name = videoCursor.getString(videoCursor.getColumnIndex("name"));
			String description= videoCursor.getString(videoCursor.getColumnIndex("description"));
			String source = videoCursor.getString(videoCursor.getColumnIndex("source"));
			String picture = videoCursor.getString(videoCursor.getColumnIndex("picture"));
			videos.add(new Video(id, time, name, description, source, picture, 0));
		}
		return videos;
	}
	
	public int getTotalLikes()
	{
		Cursor count = db.query("likes", new String[] {"count(*) as count"}, null, null, null, null, null);
		count.moveToFirst();
		return count.getInt(count.getColumnIndex("count"));
	}
	
	public int getTotalUniqueLikes()
	{
		Cursor count = db.rawQuery("select count(*) as count from (select * from likes group by name);", null);
		count.moveToFirst();
		return count.getInt(count.getColumnIndex("count"));
	}
	
	public Post getTopPost()
	{
		Cursor count = db.query("likes", new String[] {"post_id, count(id) as count"}, null, null, "post_id", null, "count desc");
		count.moveToFirst();
		int likeCount = count.getInt(count.getColumnIndex("count"));
		long postId = count.getLong(count.getColumnIndex("post_id"));
		Post post = getPostById(postId);
		post.setLikeCount(likeCount);
		return post;
	}
	
	public int getUserLikes(String name)
	{
		Cursor count = db.query("likes", new String[] {"count(*) as count"}, "name=?", new String[] {name}, null, null, null);
		count.moveToFirst();
		return count.getInt(count.getColumnIndex("count"));
	}
	
	public Friend getTopFriend()
	{
		Cursor friendCursor = db.rawQuery("select friends.id,friends.name,friends.picture,count(likes.id) as count " +
				"from friends left outer join likes on friends.name=likes.name group by friends.name order by count desc limit 1;", null);
		friendCursor.moveToFirst();
		long id = friendCursor.getLong(friendCursor.getColumnIndex("id"));
		String name = friendCursor.getString(friendCursor.getColumnIndex("name"));
		String picture = friendCursor.getString(friendCursor.getColumnIndex("picture"));
		int count = friendCursor.getInt(friendCursor.getColumnIndex("count"));
		return new Friend(id, name, picture, count);
	}
	
	public Post getPostById(long id)
	{
		String postId = String.valueOf(id);
		Cursor table = db.rawQuery("select case when exists(select 1 from statii where id=?) then 'statii' when exists(" +
				"select 1 from links where id=?) then 'links' when exists(select 1 from checkins where id=?) then 'checkins' " +
				"when exists(select 1 from photos where id=?) then 'photos' when exists(select 1 from videos where id=?) then " +
				"'videos' end as 'none'", new String[] {postId, postId, postId, postId, postId});
		table.moveToFirst();
		String type = table.getString(0);
		if(type.equals("statii"))
		{
			Cursor status = db.query("statii", null, "id=?", new String[] {postId}, null, null, null);
			status.moveToFirst();
			long time = status.getLong(status.getColumnIndex("time"));
			String statusContent = status.getString(status.getColumnIndex("status"));
			return new Status(id, time, statusContent, 0);
		}
		else if(type.equals("links"))
		{
			Cursor link = db.query("links", null, "id=?", new String[] {postId}, null, null, null);
			link.moveToFirst();
			long time = link.getLong(link.getColumnIndex("time"));
			String message = link.getString(link.getColumnIndex("message"));
			String linkText = link.getString(link.getColumnIndex("link"));
			return new Link(id, time, message, linkText, 0);
		}
		else if(type.equals("checkins"))
		{
			Cursor checkin = db.query("checkins", null, "id=?", new String[] {postId}, null, null, null);
			checkin.moveToFirst();
			long time = checkin.getLong(checkin.getColumnIndex("time"));
			String message = checkin.getString(checkin.getColumnIndex("message"));
			String location = checkin.getString(checkin.getColumnIndex("location"));
			return new Checkin(id, time, message, location, 0);
		}
		else if(type.equals("photos"))
		{
			Cursor photo = db.query("photos", null, "id=?", new String[] {postId}, null, null, null);
			photo.moveToFirst();
			long time = photo.getLong(photo.getColumnIndex("time"));
			String message = photo.getString(photo.getColumnIndex("message"));
			String source = photo.getString(photo.getColumnIndex("source"));
			return new Photo(id, time, message, source, 0);
		}
		else if(type.equals("videos"))
		{
			Cursor video = db.query("videos", null, "id=?", new String[] {postId}, null, null, null);
			video.moveToFirst();
			long time = video.getLong(video.getColumnIndex("time"));
			String name = video.getString(video.getColumnIndex("name"));
			String description= video.getString(video.getColumnIndex("description"));
			String source = video.getString(video.getColumnIndex("source"));
			String picture = video.getString(video.getColumnIndex("picture"));
			return new Video(id, time, name, description, source, picture, 0);
		}
		return null;
	}
}