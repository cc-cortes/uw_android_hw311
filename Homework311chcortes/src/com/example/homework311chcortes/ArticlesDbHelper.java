package com.example.homework311chcortes;


import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
//import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

public class ArticlesDbHelper extends SQLiteOpenHelper {
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "HW311.db";
    
    //Reference to the Context so that Assets can be reached
    private Context appContext;
    
    //Reference to the Database so getWriteableDatabase doesn't have to be done in every method
    SQLiteDatabase appDb;

	public ArticlesDbHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public ArticlesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		//Set the Context Reference
		appContext = context;
		
	    //Set a reference to the DB
	    appDb = this.getWritableDatabase();
	}

	/* Not supported in API 10
	public ArticlesDbHelper(Context context, String name,
			CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}
	*/

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//Set a reference to the DB
	    appDb = db;
		
		//Create the quotes table
		createArticlesTable(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	
	private void createArticlesTable(SQLiteDatabase db){
		
		//Create the string using the contract
		//CREATE TABLE Articles (_id INTEGER PRIMARY KEY, Content TEXT, Icon TEXT, Title Text, Date DATE)
		String createQuotesTableString = "CREATE TABLE " + ArticlesDbContract.Articles.TABLE_NAME + " (" +
				ArticlesDbContract.Articles._ID + " INTEGER PRIMARY KEY, " + 
				ArticlesDbContract.Articles.COLUMN_NAME_CONTENT + " TEXT" + ", " +
				ArticlesDbContract.Articles.COLUMN_NAME_ICON + " TEXT" + ", " +
				ArticlesDbContract.Articles.COLUMN_NAME_TITLE + " TEXT" + ", " +
				ArticlesDbContract.Articles.COLUMN_NAME_DATE + " DATE" +
				")";
		
		//Check if the table was already created
		
		//Execute the create command
		db.execSQL(createQuotesTableString);
	}
	
	//Add a new article to the database, returns the row ID
	//NEED TO PASS REFERENCE TO DB BACK!!! REMOVED IT AND OnCreate broke!
	public long addArticleToDatabase(String title, String content, String icon, Date date){
		
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(ArticlesDbContract.Articles.COLUMN_NAME_TITLE, title);
		values.put(ArticlesDbContract.Articles.COLUMN_NAME_CONTENT, content);
		values.put(ArticlesDbContract.Articles.COLUMN_NAME_ICON, icon);
		values.put(ArticlesDbContract.Articles.COLUMN_NAME_DATE, date.toString());
		
		long newRowId = appDb.insert(ArticlesDbContract.Articles.TABLE_NAME, null, values);
			
		return newRowId;
	}
	
	//Removes all articles in the Database
	public void clearArticlesInDatabase(){
		appDb.delete(ArticlesDbContract.Articles.TABLE_NAME, null, null);
	}
	
	public void testAddOneArticle(){
		//Add one article
		String title = "test title";
		String content = "test content";
		String icon = "test icon";
		Date date = new Date();
		addArticleToDatabase(title, content, icon, date);
		
		//Check it
		String getArticlesStatement = "SELECT * FROM " + ArticlesDbContract.Articles.TABLE_NAME;
		Cursor c = appDb.rawQuery(getArticlesStatement, null);
		int articleCount = c.getCount();
		
		c.moveToFirst();
		
		//Since * is used in the select statement, returns results indexed in the order the columns are created in the table
		String titleResult = c.getString(3);
		String dateResult = c.getString(4);
		
		//Delete all articles
		clearArticlesInDatabase();
		
		//Check it
		c = appDb.rawQuery(getArticlesStatement, null);
		articleCount = c.getCount();
		
		return;
	}
	
	public String getArticleTitleById(long id){
		
		String[] columns = {ArticlesDbContract.Articles.COLUMN_NAME_TITLE};
		String selection = ArticlesDbContract.Articles.COLUMN_KEY_ROWID + " = ?";
		String[] selectionArgs = {String.valueOf(id)};
		
		Cursor c = appDb.query(ArticlesDbContract.Articles.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
		
		if(c.getCount() == 0){
			return "";
		}
		
		c.moveToFirst();
		String title = c.getString(0);
		
		return title;
	}
	
	public String getArticleContentById(long id){
		
		String[] columns = {ArticlesDbContract.Articles.COLUMN_NAME_CONTENT};
		String selection = ArticlesDbContract.Articles.COLUMN_KEY_ROWID + " = ?";
		String[] selectionArgs = {String.valueOf(id)};
		
		Cursor c = appDb.query(ArticlesDbContract.Articles.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
		
		if(c.getCount() == 0){
			return "";
		}
		
		c.moveToFirst();
		String content = c.getString(0);
		
		return content;
	}

}
