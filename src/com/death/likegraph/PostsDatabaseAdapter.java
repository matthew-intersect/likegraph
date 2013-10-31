package com.death.likegraph;

import java.util.ArrayList;

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
		db.execSQL(PostsDatabaseAdapter.STATII_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.LIKE_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.LINKS_TABLE_CREATE);
		db.execSQL(PostsDatabaseAdapter.CHECKINS_TABLE_CREATE);
		db.execSQL("delete from statii");
		db.execSQL("delete from likes");
		db.execSQL("delete from links");
		db.execSQL("delete from checkins");
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
			
}