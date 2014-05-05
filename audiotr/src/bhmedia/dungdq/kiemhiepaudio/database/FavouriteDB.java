package bhmedia.dungdq.kiemhiepaudio.database;

import java.util.ArrayList;
import java.util.List;

import bhmedia.dungdq.kiemhiepaudio.model.FavouriteList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteDB extends SQLiteOpenHelper{
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "FavouriteList";
 
    // Contacts table name
    private static final String TABLE_FAVOURITES = "Favourites";
 
    // Contacts Table Columns names
    private static final String KEY_TRUYENID = "truyenid";
    private static final String KEY_TENTRUYEN = "tentruyen";
    private static final String KEY_TACGIA = "tacgia";
    private static final String KEY_SOTAP = "sotap";
    private static final String KEY_ANHDAIDIEN = "anhdaidien";
 
    public FavouriteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_FAVOURITES + "("
                + KEY_TRUYENID + " TEXT PRIMARY KEY," + KEY_TENTRUYEN + " TEXT,"
                + KEY_TACGIA + " TEXT," + KEY_SOTAP + " TEXT," + KEY_ANHDAIDIEN + " TEXT" + ")";
        db.execSQL(CREATE_FAVOURITES_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new TruyenAudio
    public void addFavourite(FavouriteList favourite) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_TRUYENID, favourite.getTruyenID()); // Truyen ID
        values.put(KEY_TENTRUYEN, favourite.getTenTruyen()); // Ten truyen
        values.put(KEY_TACGIA, favourite.getTacGia()); // Tac gia
        values.put(KEY_SOTAP, favourite.getSoTap()); // So tap
        values.put(KEY_ANHDAIDIEN, favourite.getAnhDaiDien()); // Anh dai dien
 
        // Inserting Row
        db.insert(TABLE_FAVOURITES, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single item
    public FavouriteList getFavouriteItem(String truyenid) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_FAVOURITES, new String[] { KEY_TRUYENID,
                KEY_TENTRUYEN, KEY_TACGIA, KEY_SOTAP, KEY_ANHDAIDIEN }, KEY_TRUYENID + "=?",
                new String[] { truyenid }, null, null, null, null);
        FavouriteList favourite = null;
        
        if (cursor != null) {
        	if (cursor.moveToFirst()) {
        		favourite = new FavouriteList(cursor.getString(0),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        	}
        }
            
        // return favourite list
        if (favourite != null) return favourite;
        else return null;
    }
     
    // Getting All Favourites
    public List<FavouriteList> getAllFavourites() {
        List<FavouriteList> favouriteList = new ArrayList<FavouriteList>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FavouriteList favourite = new FavouriteList();
                favourite.setTruyenID(cursor.getString(0));
                favourite.setTenTruyen(cursor.getString(1));
                favourite.setTacGia(cursor.getString(2));
                favourite.setSoTap(cursor.getString(3));
                favourite.setAnhDaiDien(cursor.getString(4));
                // Adding favourite item to list
                favouriteList.add(favourite);
            } while (cursor.moveToNext());
        }
 
        // return favourite list
        return favouriteList;
    }
 
    // Updating single favourite item
    public int updateFavourite(FavouriteList favourite) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_TENTRUYEN, favourite.getTenTruyen());
        values.put(KEY_TACGIA, favourite.getTacGia());
        values.put(KEY_SOTAP, favourite.getSoTap());
        values.put(KEY_ANHDAIDIEN, favourite.getAnhDaiDien());
 
        // updating row
        return db.update(TABLE_FAVOURITES, values, KEY_TRUYENID + " = ?",
                new String[] { String.valueOf(favourite.getTruyenID()) });
    }
 
    // Deleting single favourite item
    public void deleteFavourite(String truyenid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITES, KEY_TRUYENID + " = ?",
                new String[] { truyenid });
        db.close();
    }
 
    // Getting favourite Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FAVOURITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
 
        // return count
        return count;
    }
}
