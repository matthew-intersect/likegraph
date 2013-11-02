package com.death.likegraph;

import java.util.ArrayList;

import models.Link;
import models.Post;
import models.Status;

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
			"( id long primary key, time long, poster text, name text, description text, source text); ";
	static final String LIKE_TABLE_CREATE = "create table likes" + 
	                             "( id integer primary key autoincrement, name text, post_id long); ";
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
	
	public void addVideo(long id, long time, String from, String name, String description, String source)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("name", name);
		newValues.put("description", description);
		newValues.put("source", source);
		newValues.put("poster", from);
	
		db.insert("videos", null, newValues);
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
		db.execSQL(PostsDatabaseAdapter.STATII_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.LIKE_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.LINKS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.CHECKINS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.PHOTOS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.VIDEOS_TABLE_CREATE);
		db.execSQL("delete from statii");
		db.execSQL("delete from likes");
		db.execSQL("delete from links");
		db.execSQL("delete from checkins");
		db.execSQL("delete from photos");
		db.execSQL("delete from videos");
	}
	
	public ArrayList<Friend> getRankedLikes()
	{
		Cursor results = db.query("likes", new String[]{"name", "count(*) as count"}, null, null, "name", null, "count desc", "20");
		
		ArrayList<Friend> ranks = new ArrayList<Friend>();
		while(results.moveToNext())
		{
			String name = results.getString(results.getColumnIndex("name"));
			int count = results.getInt(results.getColumnIndex("count"));
			ranks.add(new Friend(name, count));
		}
		
		return ranks;
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
			boolean photos, boolean videos)
	{
		ArrayList<Post> posts = new ArrayList<Post>();
		if(statii)
			posts.addAll(getStatiiCounts());
		if(links)
			posts.addAll(getLinkCounts());
		return posts;
	}
	
	public ArrayList<Post> getStatiiCounts()
	{
		ArrayList<Post> statii = new ArrayList<Post>();
//		Cursor statiiCounts = db.query("statii,likes", new String[]{"statii.status", "count(likes.id)"},
//				"statii.id=?", new String[]{"likes.post_id"}, "statii.status", null, null);
		Cursor statusCounts = db.rawQuery("select statii.id, statii.time, statii.status, count(likes.id) as count from statii,likes " +
				"where statii.id=likes.post_id group by statii.id order by statii.time desc;", null);
		while(statusCounts.moveToNext())
		{
			long id = statusCounts.getLong(statusCounts.getColumnIndex("id"));
			long time = statusCounts.getLong(statusCounts.getColumnIndex("time"));
			String status = statusCounts.getString(statusCounts.getColumnIndex("status"));
			int count = statusCounts.getInt(statusCounts.getColumnIndex("count"));
			statii.add(new Status(id, time, status, count));
//			counts.add(statiiCounts.getInt(statiiCounts.getColumnIndex("count")));
		}
		statusCounts.close();
		return statii;
	}
	
	public ArrayList<Post> getLinkCounts()
	{
		ArrayList<Post> links = new ArrayList<Post>();
		Cursor linkCounts = db.rawQuery("select links.id, links.time, links.message, links.link, count(likes.id) as count from links,likes " +
				"where links.id=likes.post_id group by links.id order by links.time desc;", null);
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
}