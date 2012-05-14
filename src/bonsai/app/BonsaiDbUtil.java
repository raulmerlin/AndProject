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
    public static final String KEY_FAMILY_ID = "family_id";
    public static final String KEY_AGE = "age";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_LAST_PODE = "last_pode";
    public static final String KEY_LAST_WATER = "last_water";
    public static final String KEY_LAST_TRASPLANT = "last_trasplant";
    public static final String KEY_SITUATION = "situation";
    
    
    private static final String TAG = "BonsaiDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table bonsais (_id integer primary key autoincrement, "
        + "name string not null, family_id integer not null, " +
        "age integer not null, height integer not null, " +
        "photo string not null, last_pode timestamp not null, " +
        "last_water timestamp not null, last_trasplant timestamp not null, " +
        "situation integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "bonsais";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        										// En la creacion de la clase
            db.execSQL(DATABASE_CREATE);		// crea la base de datos
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "		// Al actualizar
                    + newVersion + ", which will destroy all old data");			// avisa
            db.execSQL("DROP TABLE IF EXISTS bonsais");								// borra la tabla tasks
            onCreate(db);															// y crea otra
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
    public long createBonsai(String name, int family_id, int age, int height, 
    		String photo, long last_pode, long last_water, long last_trasplant, int situation) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_FAMILY_ID, family_id);
        initialValues.put(KEY_AGE, age);
        initialValues.put(KEY_HEIGHT, height);
        initialValues.put(KEY_PHOTO, photo);
        initialValues.put(KEY_LAST_PODE, last_pode);
        initialValues.put(KEY_LAST_WATER, last_water);
        initialValues.put(KEY_LAST_TRASPLANT, last_trasplant);
        initialValues.put(KEY_SITUATION, situation);


        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllBonsais() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_FAMILY_ID}, null, null, null, null, null);
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
                    KEY_NAME, KEY_FAMILY_ID}, KEY_ROWID + "=" + rowId, null,
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
    public boolean updateBonsai(long rowId, String name, int family_id, int age, int height, 
    		String photo, long last_pode, long last_water, long last_trasplant, int situation) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_FAMILY_ID, family_id);
        args.put(KEY_AGE, age);
        args.put(KEY_HEIGHT, height);
        args.put(KEY_PHOTO, photo);
        args.put(KEY_LAST_PODE, last_pode);
        args.put(KEY_LAST_WATER, last_water);
        args.put(KEY_LAST_TRASPLANT, last_trasplant);
        args.put(KEY_SITUATION, situation);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
