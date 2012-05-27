/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package bonsai.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class BonsaiDbUtil {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_FAMILY = "family";
    public static final String KEY_AGE = "age";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_PHOTO = "photourl";
    public static final String KEY_LAST_PODE = "last_pode";
    public static final String KEY_LAST_WATER = "last_water";
    public static final String KEY_LAST_TRASPLANT = "last_trasplant";
    public static final String KEY_LOCALIZATION = "localization";
    public static final String KEY_SITUATION = "situation";
    
    
    private static final String TAG = "BonsaiDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table bonsais(_id integer primary key autoincrement, "
        + "name string not null, family string not null, age integer not null, height integer not null, "
        + "photourl string not null, last_pode integer not null, last_water integer not null, "
        + "last_trasplant integer not null, localization string not null, situation string not null);";
    
    private static final String FAMILY_DATABASE_CREATE =
            "create table familys(_id integer primary key autoincrement, "
            + "family string not null, pode_frecuency integer not null, water_frecuency integer not null, "
            + "transplant_frecuency integer not null, situation string not null);";


    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "bonsais";
    private static final int DATABASE_VERSION = 6;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        										// En la creacion de la clase
            db.execSQL(DATABASE_CREATE);		// crea la base de datos

			// En la creacion de la clase
            db.execSQL(FAMILY_DATABASE_CREATE);		// crea la base de datos
            familydbseed(db);

        }
      

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "		// Al actualizar
                    + newVersion + ", which will destroy all old data");			// avisa
            db.execSQL("DROP TABLE IF EXISTS bonsais");								// borra la tabla bonsais
            onCreate(db);															// y crea otra
        }
        
        public void familydbseed(SQLiteDatabase db) {
        	db.execSQL("Insert Into familys (family, pode_frecuency, water_frecuency, transplant_frecuency, situation) " +
        		"Values ('Serissa Phoetida', '" + 120*24 + "', '" + 3*24 + "', '" + 630*24 + "', 'Interior') ");
        	db.execSQL("Insert Into familys (family, pode_frecuency, water_frecuency, transplant_frecuency, situation) " +
        		"Values ('Ficus Retusa', '" + 90*24 + "', '" + 4*24 + "', '" + 630*24 + "', 'Interior') ");
        	db.execSQL("Insert Into familys (family, pode_frecuency, water_frecuency, transplant_frecuency, situation) " +
				"Values ('Olea Europaea', '" + 150*24 + "', '" + 6*24 + "', '" + 1030*24 + "', 'Exterior') ");
        	db.execSQL("Insert Into familys (family, pode_frecuency, water_frecuency, transplant_frecuency, situation) " +
				"Values ('Carmona Mircophilla', '" + 60*24 + "', '" + 3*24 + "', '" + 630*24 + "', 'Interior') ");
        	db.execSQL("Insert Into familys (family, pode_frecuency, water_frecuency, transplant_frecuency, situation) " +
				"Values ('Picea Glauca Conica', '" + 150*24 + "', '" + 6*24 + "', '" + 1030*24 + "', 'Exterior') ");
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public BonsaiDbUtil(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public BonsaiDbUtil open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the database.
     */
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createBonsai(String name, String family, long age, int height, 
    		String photo, long last_pode, long last_water, long last_trasplant, String localization, String situation) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_FAMILY, family);
        initialValues.put(KEY_AGE, age);
        initialValues.put(KEY_HEIGHT, height);
        initialValues.put(KEY_PHOTO, photo);
        initialValues.put(KEY_LAST_PODE, last_pode);
        initialValues.put(KEY_LAST_WATER, last_water);
        initialValues.put(KEY_LAST_TRASPLANT, last_trasplant);
        initialValues.put(KEY_LOCALIZATION, localization);
        initialValues.put(KEY_SITUATION, situation);


        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteBonsai(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllBonsais() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_NAME, KEY_FAMILY, KEY_AGE, KEY_HEIGHT, KEY_PHOTO,
                KEY_LAST_PODE, KEY_LAST_WATER, KEY_LAST_TRASPLANT, KEY_LOCALIZATION, KEY_SITUATION}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchBonsai(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_FAMILY, KEY_AGE, KEY_HEIGHT, KEY_PHOTO,
                    KEY_LAST_PODE, KEY_LAST_WATER, KEY_LAST_TRASPLANT, KEY_LOCALIZATION, KEY_SITUATION}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateBonsai(long rowId, String name, String family, long age, int height, 
    		String photo, String localization, String situation) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_FAMILY, family);
        args.put(KEY_AGE, age);
        args.put(KEY_HEIGHT, height);
        args.put(KEY_PHOTO, photo);
        args.put(KEY_LOCALIZATION, localization);
        args.put(KEY_SITUATION, situation);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean waterBonsai(long rowId, long last_water) {
        ContentValues args = new ContentValues();
        args.put(KEY_LAST_WATER, last_water);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean podeBonsai(long rowId, long last_pode) {
        ContentValues args = new ContentValues();
        args.put(KEY_LAST_PODE, last_pode);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean transplantBonsai(long rowId, long last_trasplant) {
        ContentValues args = new ContentValues();
        args.put(KEY_LAST_TRASPLANT, last_trasplant);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
}
