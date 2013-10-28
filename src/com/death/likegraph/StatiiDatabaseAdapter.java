package com.death.likegraph;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class StatiiDatabaseAdapter 
{
	static final String DATABASE_NAME = "likegraph.db";
	static final int DATABASE_VERSION = 1;
	
	static final String STATII_TABLE_CREATE = "create table statii" +
	                             "( id integer primary key, time long, status text); ";
	static final String LIKE_TABLE_CREATE = "create table likes" + 
	                             "( id integer primary key, name text, status_id, " +
	                             "foreign key(status_id) references statii(id)); ";
	public  SQLiteDatabase db;
	private final Context context;
	private DatabaseHelper dbHelper;
	
	public StatiiDatabaseAdapter(Context _context) 
	{
		context = _context;
		dbHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public StatiiDatabaseAdapter open() throws SQLException 
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
	
	public void addStatus(int id, long time, String status)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("time", time);
		newValues.put("status", status);
	
		db.insert("statii", null, newValues);
	}
	
	public void addLike(int id , String name, int status)
	{
		ContentValues newValues = new ContentValues();
		newValues.put("id", id);
		newValues.put("name", name);
		newValues.put("status", status);
	
		db.insert("likes", null, newValues);
	}
	
	public void clearTables()
	{
		db.execSQL("delete from statii");
		db.execSQL("delete from likes");
	}
			
}