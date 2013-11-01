package com.death.likegraph;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	
	public DatabaseHelper(Context context, String name,CursorFactory factory, int version) 
    {
	           super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
			db.execSQL(PostsDatabaseAdapter.STATII_TABLE_CREATE);
			db.execSQL(PostsDatabaseAdapter.LIKE_TABLE_CREATE);
			db.execSQL(PostsDatabaseAdapter.LINKS_TABLE_CREATE);
			db.execSQL(PostsDatabaseAdapter.CHECKINS_TABLE_CREATE);
			db.execSQL(PostsDatabaseAdapter.PHOTOS_TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion)
	{
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");

			_db.execSQL("DROP TABLE IF EXISTS statii");
			_db.execSQL("DROP TABLE IF EXISTS likes");
			_db.execSQL("DROP TABLE IF EXISTS links");
			_db.execSQL("DROP TABLE IF EXISTS checkins");
			_db.execSQL("DROP TABLE IF EXISTS photos");
			onCreate(_db);
	}

}