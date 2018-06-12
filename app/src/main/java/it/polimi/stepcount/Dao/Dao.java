package it.polimi.stepcount.Dao;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;

public abstract class Dao {
    private static final String TAG = Dao.class.getSimpleName();

    // This object must be always closed
    DatabaseHelper dbHelper;

    Dao(Context context) {

        dbHelper = DatabaseHelper.getInstance(context);
    }

    protected String getRightNow(){
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    protected String getLastMidnight(){
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(
                yesterday.get(Calendar.YEAR),
                yesterday.get(Calendar.MONTH),
                yesterday.get(Calendar.DAY_OF_MONTH),
                0,          // hours
                0);         // minutes
        return fromMillisToString(yesterday.getTimeInMillis());
    }

    String getSpecificDayMidnight(long dateMillis){
        Calendar thatDay = Calendar.getInstance();
        thatDay.setTimeInMillis(dateMillis);

        Calendar thatDayMidnight = Calendar.getInstance();
        thatDayMidnight.set(
                thatDay.get(Calendar.YEAR),
                thatDay.get(Calendar.MONTH),
                thatDay.get(Calendar.DAY_OF_MONTH),
                0,          // hours
                0           // minutes
        );

        return fromMillisToString(thatDayMidnight.getTimeInMillis());
    }

    String fromMillisToString(long dateMillis){
        return String.valueOf(dateMillis);
    }

    public void closeConnection(){
        Log.v(TAG, "Method closeConnection: start");

        if(dbHelper != null){
            try {
                dbHelper.close();
                Log.i(TAG, "DatabaseHelper released");
            } catch (Exception e){
                Log.e(TAG, "Exception: Cannot close DatabaseHelper");
                e.printStackTrace();
            }
        }
        else{
            Log.i(TAG, "DatabaseHelper null");
        }

        Log.v(TAG, "Method closeConnection: end");
    }

    public void printCreateQueries(){
//        Log.e(TAG,DatabaseContract.SmartphoneSessionTable.CREATE_TABLE);
//        Log.e(TAG,DatabaseContract.CallTable.CREATE_TABLE);
//        Log.e(TAG,DatabaseContract.MessageTable.CREATE_TABLE);
        Log.e(TAG, DatabaseContract.WalkingSessionTable.CREATE_TABLE);
    }
}
