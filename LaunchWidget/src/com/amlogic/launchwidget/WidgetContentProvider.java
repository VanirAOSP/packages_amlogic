
package com.amlogic.launchwidget;

import com.amlogic.launchwidget.WidgetBaseColumns;

import android.content.ClipDescription;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.ContentProvider.PipeDataWriter;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.LiveFolders;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class WidgetContentProvider extends ContentProvider implements PipeDataWriter<Cursor> {
    // Used for debugging and logging
    private static final String TAG = "NotePadProvider";
    /**
     * The database that the provider uses as its underlying data store
     */ 
    private static final String DATABASE_NAME = "tv_widget.db";

    /**   
     * The database version  
     */
    private static final int DATABASE_VERSION = 2;
   
    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sNotesProjectionMap;
 
    /**
     * A projection map used to select columns from the database
     */  
    private static HashMap<String, String> sLiveFolderProjectionMap;
  
    /**
     * Standard projection for the interesting columns of a normal note.
     */
    private static final String[] READ_NOTE_PROJECTION = new String[] {
            WidgetBaseColumns.Columns._ID,               // Projection position 0, the note's id
            WidgetBaseColumns.Columns.COLUMN_CLASS_NAME,  // Projection position 1, the note's content
            WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, // Projection position 2, the note's title
    };
    private static final int READ_NOTE_NOTE_INDEX = 1;
    private static final int READ_NOTE_TITLE_INDEX = 2;

    private static final int WIDGET = 1;

    private static final int WIDGET_ID = 2;

    private static final int LIVE_FOLDER_WIDGET = 3;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;


    static {

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(WidgetBaseColumns.AUTHORITY, "launchwidgets", WIDGET);

        sUriMatcher.addURI(WidgetBaseColumns.AUTHORITY, "launchwidgets/#", WIDGET_ID);
        sUriMatcher.addURI(WidgetBaseColumns.AUTHORITY, "live_folders/launchwidgets", LIVE_FOLDER_WIDGET);


        sNotesProjectionMap = new HashMap<String, String>();

        sNotesProjectionMap.put(WidgetBaseColumns.Columns._ID, WidgetBaseColumns.Columns._ID);

        sNotesProjectionMap.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT);

        sNotesProjectionMap.put(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, WidgetBaseColumns.Columns.COLUMN_CLASS_NAME);

        sNotesProjectionMap.put(WidgetBaseColumns.Columns.COLUMN_APP_NAME,
                WidgetBaseColumns.Columns.COLUMN_APP_NAME);

        sNotesProjectionMap.put(
                WidgetBaseColumns.Columns.COLUMN_MODIFY,
                WidgetBaseColumns.Columns.COLUMN_MODIFY);

        sNotesProjectionMap.put(WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON, WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON);
        
        sLiveFolderProjectionMap = new HashMap<String, String>();

        sLiveFolderProjectionMap.put(LiveFolders._ID, WidgetBaseColumns.Columns._ID + " AS " + LiveFolders._ID);

        sLiveFolderProjectionMap.put(LiveFolders.NAME, WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT + " AS " +
            LiveFolders.NAME);
    }
   static class DatabaseHelper extends SQLiteOpenHelper {

       DatabaseHelper(Context context) {

           // calls the super constructor, requesting the default cursor factory.
           super(context, DATABASE_NAME, null, DATABASE_VERSION);
       }

       @Override
       public void onCreate(SQLiteDatabase db) {
           db.execSQL("CREATE TABLE " + WidgetBaseColumns.Columns.TABLE_NAME + " ("
                   + WidgetBaseColumns.Columns._ID + " INTEGER PRIMARY KEY,"
                   + WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT + " TEXT,"
                   + WidgetBaseColumns.Columns.COLUMN_CLASS_NAME + " TEXT,"
                   + WidgetBaseColumns.Columns.COLUMN_APP_NAME + " TEXT,"
                   + WidgetBaseColumns.Columns.COLUMN_MODIFY + " INTEGER,"
                   + WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON + " BLOB"
                   + ");");
       }

       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                   + newVersion + ", which will destroy all old data");

           db.execSQL("DROP TABLE IF EXISTS tb_widget");

           onCreate(db);
       }
   }

   @Override
   public boolean onCreate() {

       mOpenHelper = new DatabaseHelper(getContext());

       return true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
           String sortOrder) {

       SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
       qb.setTables(WidgetBaseColumns.Columns.TABLE_NAME);

       switch (sUriMatcher.match(uri)) {
           case WIDGET:
               qb.setProjectionMap(sNotesProjectionMap);
               break;

           case WIDGET_ID:
               qb.setProjectionMap(sNotesProjectionMap);
               qb.appendWhere(
                   WidgetBaseColumns.Columns._ID +    
                   "=" +
                   uri.getPathSegments().get(WidgetBaseColumns.Columns.NOTE_ID_PATH_POSITION));
               break;

           case LIVE_FOLDER_WIDGET:
               qb.setProjectionMap(sLiveFolderProjectionMap);
               break;

           default:
               throw new IllegalArgumentException("Unknown URI " + uri);
       }


       String orderBy;
       if (TextUtils.isEmpty(sortOrder)) {
           orderBy = WidgetBaseColumns.Columns.DEFAULT_SORT_ORDER;
       } else {
           orderBy = sortOrder;
       }

       SQLiteDatabase db = mOpenHelper.getReadableDatabase();

       Cursor c = qb.query(
           db,             
           projection,    
           selection,      
           selectionArgs,  
           null,          
           null,         
           orderBy         
       );

       c.setNotificationUri(getContext().getContentResolver(), uri);
       return c;
   }

   @Override
   public String getType(Uri uri) {

       switch (sUriMatcher.match(uri)) {

           case WIDGET:
           case LIVE_FOLDER_WIDGET:
               return WidgetBaseColumns.Columns.CONTENT_TYPE;
           case WIDGET_ID:
               return WidgetBaseColumns.Columns.CONTENT_ITEM_TYPE;

           default:
               throw new IllegalArgumentException("Unknown URI " + uri);
       }
    }
    static ClipDescription NOTE_STREAM_TYPES = new ClipDescription(null,
            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN });

    @Override
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        /**
         *  Chooses the data stream type based on the incoming URI pattern.
         */
        switch (sUriMatcher.match(uri)) {

            case WIDGET:
            case LIVE_FOLDER_WIDGET:
                return null;

            case WIDGET_ID:
                return NOTE_STREAM_TYPES.filterMimeTypes(mimeTypeFilter);

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
    }


    @Override
    public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts)
            throws FileNotFoundException {

        String[] mimeTypes = getStreamTypes(uri, mimeTypeFilter);

        if (mimeTypes != null) {

            Cursor c = query(
                    uri,                    
                    READ_NOTE_PROJECTION,   
                                          
                    null,                  
                    null,                  
                    null                   
                                         
            );


            if (c == null || !c.moveToFirst()) {

                if (c != null) {
                    c.close();
                }

                throw new FileNotFoundException("Unable to query " + uri);
            }

            return new AssetFileDescriptor(
                    openPipeHelper(uri, mimeTypes[0], opts, c, this), 0,
                    AssetFileDescriptor.UNKNOWN_LENGTH);
        }

        return super.openTypedAssetFile(uri, mimeTypeFilter, opts);
    }
    @Override
    public void writeDataToPipe(ParcelFileDescriptor output, Uri uri, String mimeType,
            Bundle opts, Cursor c) {
        // We currently only support conversion-to-text from a single widget entry,
        // so no need for cursor data type checking here.
        FileOutputStream fout = new FileOutputStream(output.getFileDescriptor());
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(fout, "UTF-8"));
            pw.println(c.getString(READ_NOTE_TITLE_INDEX));
            pw.println("");
            pw.println(c.getString(READ_NOTE_NOTE_INDEX));
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "Ooops", e);
        } finally {
            c.close();
            if (pw != null) {
                pw.flush();
            }
            try {
                fout.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        if (sUriMatcher.match(uri) != WIDGET) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            values = new ContentValues();
        }
        Long now = Long.valueOf(System.currentTimeMillis());

        if (values.containsKey(WidgetBaseColumns.Columns.COLUMN_APP_NAME) == false) {
            values.put(WidgetBaseColumns.Columns.COLUMN_APP_NAME, "");
        }
        if (values.containsKey(WidgetBaseColumns.Columns.COLUMN_MODIFY) == false) {
            values.put(WidgetBaseColumns.Columns.COLUMN_MODIFY, now); 
        }
        if (values.containsKey(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT) == false) {
            Resources r = Resources.getSystem();
            values.put(WidgetBaseColumns.Columns.COLUMN_ACTION_INTENT, r.getString(android.R.string.untitled));
        }
        if (values.containsKey(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME) == false) {
            values.put(WidgetBaseColumns.Columns.COLUMN_CLASS_NAME, "");
        }
        if (values.containsKey(WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON) == false) {
            values.put(WidgetBaseColumns.Columns.COLUMN_PREVIEW_ICON, "");
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(
            WidgetBaseColumns.Columns.TABLE_NAME,        
            WidgetBaseColumns.Columns.COLUMN_CLASS_NAME,   
            values                          
        );
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(WidgetBaseColumns.Columns.CONTENT_ID_URI_BASE, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;
        int count;
        switch (sUriMatcher.match(uri)) {

            case WIDGET:
                count = db.delete(
                    WidgetBaseColumns.Columns.TABLE_NAME,  // The database table name
                    where,                     // The incoming where clause column names
                    whereArgs                  // The incoming where clause values
                );
                break;

            case WIDGET_ID:
                finalWhere =
                        WidgetBaseColumns.Columns._ID +                            
                        " = " +                                         
                        uri.getPathSegments().                          
                            get(WidgetBaseColumns.Columns.NOTE_ID_PATH_POSITION)
                ;

                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(
                    WidgetBaseColumns.Columns.TABLE_NAME,   
                    finalWhere,              
                    whereArgs                 
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {

        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        switch (sUriMatcher.match(uri)) {

            case WIDGET:
                count = db.update(
                    WidgetBaseColumns.Columns.TABLE_NAME,  
                    values,                   
                    where,                   
                    whereArgs               
                );
                break;
            case WIDGET_ID:
                String noteId = uri.getPathSegments().get(WidgetBaseColumns.Columns.NOTE_ID_PATH_POSITION);
                finalWhere =
                        WidgetBaseColumns.Columns._ID +                              // The ID column name
                        " = " +                                          // test for equality
                        uri.getPathSegments().                           // the incoming note ID
                            get(WidgetBaseColumns.Columns.NOTE_ID_PATH_POSITION)
                ;
                if (where !=null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.update(
                    WidgetBaseColumns.Columns.TABLE_NAME,  
                    values,                    
                    finalWhere,              
                                             
                    whereArgs                
                                             
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    DatabaseHelper getOpenHelperForTest() {
        return mOpenHelper;
    }
}
