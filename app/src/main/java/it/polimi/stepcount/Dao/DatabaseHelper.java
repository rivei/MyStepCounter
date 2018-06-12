package it.polimi.stepcount.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static volatile DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        //Double check locking pattern
        if (instance == null) { //Check for the first time

            synchronized (DatabaseHelper.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (instance == null) instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        /*SQLiteDatabase mDatabase = context.openOrCreateDatabase(
                DatabaseContract.DATABASE_NAME, MODE_PRIVATE, null);*/
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Metodo OnCreate!");

/*
        db.execSQL(DatabaseContract.GaitSessionTable.CREATE_TABLE);
        //db.execSQL(DatabaseContract.InsolesTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.InsolesRawHeaderTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.InsolesRawDataTable.CREATE_TABLE);
*/
        db.execSQL(DatabaseContract.WalkingSessionTable.CREATE_TABLE);
        Log.v(TAG, "Fine Metodo OnCreate!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
        /*db.execSQL(DatabaseContract.CallTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.MessageTable.DELETE_TABLE);
        onCreate(db);*/
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
