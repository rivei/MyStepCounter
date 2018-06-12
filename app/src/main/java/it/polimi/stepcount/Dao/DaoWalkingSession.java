package it.polimi.stepcount.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.polimi.stepcount.models.WalkingSession;

public class DaoWalkingSession extends Dao {
    private static final String TAG = DaoWalkingSession.class.getSimpleName();

    static final int COL_START_TIME = 1;
    static final int COL_END_TIME   = 2;
    static final int COL_STEP_COUNT = 3;
    static final int COL_DISTANCE   = 4;
    static final int COL_AVG_SPEED  = 5;
    static final int COL_DURATION   = 6;

    public DaoWalkingSession(Context context) {
        super(context);
    }

    public void storeWalkingSession(WalkingSession walkingSession) throws Exception{
        
        if(walkingSession == null) {
            Log.d(TAG, "storeWalkingSession: null object");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WalkingSessionTable.START_TIME, walkingSession.getmStartTime());
        values.put(DatabaseContract.WalkingSessionTable.END_TIME, walkingSession.getmEndTime());
        values.put(DatabaseContract.WalkingSessionTable.STEP_COUNT, walkingSession.getmStepCount());
        values.put(DatabaseContract.WalkingSessionTable.DISTANCE, walkingSession.getmDistance());
        values.put(DatabaseContract.WalkingSessionTable.AVG_SPEED, walkingSession.getmAverageSpeed());
        values.put(DatabaseContract.WalkingSessionTable.DURATION, walkingSession.getmDuration());

        long newRowId = db.insert(
                DatabaseContract.WalkingSessionTable.TABLE_NAME,
                null,
                values);

        if(newRowId == -1){
            throw new Exception("DBInsertException");
        }

        Log.i(TAG, "Row inserted");
    }

    public List<WalkingSession> retrieveAllSessions(){
        List<WalkingSession> walkingSessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (
                Cursor cursor = db.query(
                        DatabaseContract.WalkingSessionTable.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        ){
            if (cursor == null){
                Log.e(TAG, "");
                return null;
            }

            while (cursor.moveToNext()){
                WalkingSession cur = new WalkingSession(
                        cursor.getLong(COL_START_TIME ),
                        cursor.getLong(COL_END_TIME   ),
                        cursor.getLong(COL_STEP_COUNT ),
                        cursor.getLong(COL_DISTANCE   ),
                        cursor.getLong(COL_AVG_SPEED  ),
                        cursor.getLong(COL_DURATION   ));

                walkingSessions.add(cur);
            }
        }
        Log.i(TAG, "Retrieve data end");
        return walkingSessions;
    }
}
