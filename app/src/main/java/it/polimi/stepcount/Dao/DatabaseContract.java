package it.polimi.stepcount.Dao;

import android.provider.BaseColumns;

public class DatabaseContract {
    static final int DATABASE_VERSION = 2; //TODO
    static final String DATABASE_NAME = "MOVECARE_DB";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTO_INCREMENT = " AUTOINCREMENT";
    private static final String NULL = " NULL";
    private static final String NOT_NULL = " NOT NULL";
    private static final String DEFAULT = " DEFAULT";
    private static final String COMMA = ", ";
    private static final String LEFT_PAR = " ( ";
    private static final String RIGHT_PAR = " )";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {
    }

    //Start time; End time; Calories (kcal); Distance (m); Low latitude (deg);
    // Low longitude (deg);High latitude (deg);High longitude (deg);Average speed (m/s);
    // Max speed (m/s);Min speed (m/s);Step count; Inactive duration (ms);

    static abstract class WalkingSessionTable implements BaseColumns {
        static final String TABLE_NAME = "WALKING_SESSION";
        static final String START_TIME = "StartTime";
        static final String END_TIME = "EndTime";
        static final String STEP_COUNT = "StepCount";
        static final String DISTANCE = "Distance";
        static final String AVG_SPEED = "AverageSpeed";
        static final String DURATION = "Duration";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + LEFT_PAR +
                _ID          + INT_TYPE  + PRIMARY_KEY + AUTO_INCREMENT  + COMMA    +
                START_TIME  +TEXT_TYPE  + COMMA +
                END_TIME    + TEXT_TYPE + COMMA +
                STEP_COUNT  + INT_TYPE  + COMMA +
                DISTANCE    + INT_TYPE  + COMMA +
                AVG_SPEED   + REAL_TYPE + COMMA +
                DURATION    + INT_TYPE  +  RIGHT_PAR;
    }

}
