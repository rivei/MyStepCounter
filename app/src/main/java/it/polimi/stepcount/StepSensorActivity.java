package it.polimi.stepcount;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepSensorActivity extends AppCompatActivity {

    public static final String TAG = "StepSensorActivity";

    final StepSensorActivity self = this;
    // Actions from REGISTER cards
    public static final int ACTION_REGISTER_DETECT_NOBATCHING = 10;
    public static final int ACTION_REGISTER_DETECT_BATCHING_5s = 11;
    public static final int ACTION_REGISTER_DETECT_BATCHING_10s = 12;
    public static final int ACTION_REGISTER_COUNT_NOBATCHING = 21;
    public static final int ACTION_REGISTER_COUNT_BATCHING_5s = 22;
    public static final int ACTION_REGISTER_COUNT_BATCHING_10s = 23;
    // Action from COUNTING card
    public static final int ACTION_UNREGISTER = 1;
    // Actions from description cards
    private static final int ACTION_BATCHING_DESCRIPTION_DISMISS = 2;
    private static final int ACTION_EXPLANATION_DISMISS = 3;

    // State of application, used to register for sensors when app is restored
    public static final int STATE_OTHER = 0;
    public static final int STATE_COUNTER = 1;
    public static final int STATE_DETECTOR = 2;

    // Bundle tags used to store data when restoring application state
    private static final String BUNDLE_STATE = "state";
    private static final String BUNDLE_LATENCY = "latency";
    private static final String BUNDLE_STEPS = "steps";

    // max batch latency is specified in microseconds
    private static final int BATCH_LATENCY = 5000000; //5s of delay

    TextView textView, mStepText ;
    Button start,stop,reset,save;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Seconds, Minutes, MilliSeconds, walking_duration;
    Handler handler;

    /*
    For illustration we keep track of the last few events and show their delay from when the
    event occurred until it was received by the event listener.
    These variables keep track of the list of timestamps and the number of events.
     */
    // Number of events to keep in queue and display on card
    private static final int EVENT_QUEUE_LENGTH = 10;
    // List of timestamps when sensor events occurred
    private float[] mEventDelays = new float[EVENT_QUEUE_LENGTH];

    // number of events in event list
    private int mEventLength = 0;
    // pointer to next entry in sensor event list
    private int mEventData = 0;

    // Steps counted in current session
    private int mSteps = 0;
    // Value of the step counter sensor when the listener was registered.
    // (Total steps are calculated from this value.)
    private int mCounterSteps = 0;
    // Steps counted by the step counter previously. Used to keep counter consistent across rotation
    // changes
    private int mPreviousCounterSteps = 0;
    // State of the app (STATE_OTHER, STATE_COUNTER or STATE_DETECTOR)
    private int mState = STATE_OTHER;
    // When a listener is registered, the batch sensor delay in microseconds
    private int mMaxDelay = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_sensor);
        textView = (TextView)findViewById(R.id.textView);
        mStepText = (TextView)findViewById(R.id.stepsText);
        start = (Button)findViewById(R.id.button_start);
        stop = (Button)findViewById(R.id.button_stop);
        reset = (Button)findViewById(R.id.button_reset);
        save = (Button) findViewById(R.id.button_save);
        handler = new Handler() ;
        stop.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
        save.setEnabled(false);
        mStepText.setText(String.format("Total step: 0 "));

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                reset.setEnabled(false);
                save.setEnabled(false);
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);

                registerEventListener(BATCH_LATENCY, Sensor.TYPE_STEP_COUNTER);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unregisterListeners();
                mStepText.setText(String.format("Total step: %d ", mSteps));

                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                reset.setEnabled(true);
                save.setEnabled(true);
                stop.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCounter();
                mStepText.setText(String.format("Total step: 0 "));
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;
                textView.setText("00:00:00");
                reset.setEnabled(false);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                walking_duration = Minutes*60+Seconds;
           /*     schedule.setScore(walking_duration);
                writeDatabase();
                Toast.makeText(WalkingActivity.this, "Walking Speed Successfully saved", Toast.LENGTH_SHORT).show();
*/
            }
        });
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000)/10;

            textView.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds)+":"
                    + String.format("%02d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };

    /**
     * Returns true if this device is supported. It needs to be running Android KitKat (4.4) or
     * higher and has a step counter and step detector sensor.
     * This check is useful when an app provides an alternative implementation or different
     * functionality if the step sensors are not available or this code runs on a platform version
     * below Android KitKat. If this functionality is required, then the minSDK parameter should
     * be specified appropriately in the AndroidManifest.
     *
     * @return True iff the device can run this sample
     */
    private boolean isKitkatWithStepSensor() {
        // BEGIN_INCLUDE(iskitkatsensor)
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        //PackageManager packageManager = getActivity().getPackageManager();
        PackageManager packageManager = self.getPackageManager();
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
                //&& packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        // END_INCLUDE(iskitkatsensor)
    }



    /**
     * Register a {@link android.hardware.SensorEventListener} for the sensor and max batch delay.
     * The maximum batch delay specifies the maximum duration in microseconds for which subsequent
     * sensor events can be temporarily stored by the sensor before they are delivered to the
     * registered SensorEventListener. A larger delay allows the system to handle sensor events more
     * efficiently, allowing the system to switch to a lower power state while the sensor is
     * capturing events. Once the max delay is reached, all stored events are delivered to the
     * registered listener. Note that this value only specifies the maximum delay, the listener may
     * receive events quicker. A delay of 0 disables batch mode and registers the listener in
     * continuous mode.
     * The optimium batch delay depends on the application. For example, a delay of 5 seconds or
     * higher may be appropriate for an  application that does not update the UI in real time.
     *
     * @param maxdelay
     * @param sensorType
     */
    private void registerEventListener(int maxdelay, int sensorType) {
        // BEGIN_INCLUDE(register)

        // Keep track of state so that the correct sensor type and batch delay can be set up when
        // the app is restored (for example on screen rotation).
        mMaxDelay = maxdelay;
        if (sensorType == Sensor.TYPE_STEP_COUNTER) {
            mState = STATE_COUNTER;
            /*
            Reset the initial step counter value, the first event received by the event listener is
            stored in mCounterSteps and used to calculate the total number of steps taken.
             */
            mCounterSteps = 0;
            Log.i(TAG, "Event listener for step counter sensor registered with a max delay of "
                    + mMaxDelay);
        } else {
            mState = STATE_DETECTOR;
            Log.i(TAG, "Event listener for step detector sensor registered with a max delay of "
                    + mMaxDelay);
        }

        //TODO: Get the default sensor for the sensor type from the SenorManager
        SensorManager sensorManager =
                (SensorManager) self.getSystemService(Activity.SENSOR_SERVICE);
        // sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        // Register the listener for this sensor in batch mode.
        // If the max delay is 0, events will be delivered in continuous mode without batching.
        final boolean batchMode = sensorManager.registerListener(
                mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, maxdelay);

        if (!batchMode) {
            // Batch mode could not be enabled, show a warning message and switch to continuous mode
//            getCardStream().getCard(CARD_NOBATCHSUPPORT)
//                    .setDescription(getString(R.string.warning_nobatching));
//            getCardStream().showCard(CARD_NOBATCHSUPPORT);
            Toast.makeText(self, "Could not register sensor listener in batch mode, " +
                    "falling back to continuous mode.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Could not register sensor listener in batch mode, " +
                    "falling back to continuous mode.");
        }

        if (maxdelay > 0 && batchMode) {
            // Batch mode was enabled successfully, show a description card
//            getCardStream().showCard(CARD_BATCHING_DESCRIPTION);
            Toast.makeText(self, "Batch mode succeess.", Toast.LENGTH_SHORT).show();
        }

        // Show the explanation card
//        getCardStream().showCard(CARD_EXPLANATION);

        // END_INCLUDE(register)

    }

    /**
     * Unregisters the sensor listener if it is registered.
     */
    private void unregisterListeners() {
        // BEGIN_INCLUDE(unregister)
        SensorManager sensorManager =
                (SensorManager) self.getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.unregisterListener(mListener);
        Log.i(TAG, "Sensor listener unregistered.");

        // END_INCLUDE(unregister)
    }

    /**
     * Resets the step counter by clearing all counting variables and lists.
     */
    private void resetCounter() {
        // BEGIN_INCLUDE(reset)
        mSteps = 0;
        mCounterSteps = 0;
        mEventLength = 0;
        mEventDelays = new float[EVENT_QUEUE_LENGTH];
        mPreviousCounterSteps = 0;
        // END_INCLUDE(reset)
    }


    /**
     * Listener that handles step sensor events for step detector and step counter sensors.
     */
    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // BEGIN_INCLUDE(sensorevent)
            // store the delay of this event
            recordDelay(event);
            final String delayString = getDelayString();

/*            if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                // A step detector event is received for each step.
                // This means we need to count steps ourselves

                mSteps += event.values.length;

                //Update the card with the latest step count
//                getCardStream().getCard(CARD_COUNTING)
//                        .setTitle(getString(R.string.counting_title, mSteps))
//                        .setDescription(getString(R.string.counting_description,
//                                getString(R.string.sensor_detector), mMaxDelay, delayString));
                Log.i(TAG,
                        "New step detected by STEP_DETECTOR sensor. Total step count: " + mSteps);

            } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {*/

                /*
                A step counter event contains the total number of steps since the listener
                was first registered. We need to keep track of this initial value to calculate the
                number of steps taken, as the first value a listener receives is undefined.
                 */
                if (mCounterSteps < 1) {
                    // initial value
                    mCounterSteps = (int) event.values[0];
                }

                // Calculate steps taken based on first counter value received.
                mSteps = (int) event.values[0] - mCounterSteps;

                // Add the number of steps previously taken, otherwise the counter would start at 0.
                // This is needed to keep the counter consistent across rotation changes.
                mSteps = mSteps + mPreviousCounterSteps;

                //TODO: Update the card with the latest step count
//                getCardStream().getCard(CARD_COUNTING)
//                        .setTitle(getString(R.string.counting_title, mSteps))
//                        .setDescription(getString(R.string.counting_description,
//                                getString(R.string.sensor_counter), mMaxDelay, delayString));
                //mStepText.setText(String.format("Total step: %d ", mSteps));
                //Toast.makeText(self, String.format("Total step: %d ", mSteps), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "New step detected by STEP_COUNTER sensor. Total step count: " + mSteps);
                Log.e(TAG, "Counter step: " + mCounterSteps);
            // END_INCLUDE(sensorevent)
 //           }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * Records the delay for the event.
     *
     * @param event
     */
    private void recordDelay(SensorEvent event) {
        // Calculate the delay from when event was recorded until it was received here in ms
        // Event timestamp is recorded in us accuracy, but ms accuracy is sufficient here
        mEventDelays[mEventData] = System.currentTimeMillis() - (event.timestamp / 1000000L);

        // Increment length counter
        mEventLength = Math.min(EVENT_QUEUE_LENGTH, mEventLength + 1);
        // Move pointer to the next (oldest) location
        mEventData = (mEventData + 1) % EVENT_QUEUE_LENGTH;
    }

    private final StringBuffer mDelayStringBuffer = new StringBuffer();

    /**
     * Returns a string describing the sensor delays recorded in
     * {@link #recordDelay(android.hardware.SensorEvent)}.
     *
     * @return
     */
    private String getDelayString() {
        // Empty the StringBuffer
        mDelayStringBuffer.setLength(0);

        // Loop over all recorded delays and append them to the buffer as a decimal
        for (int i = 0; i < mEventLength; i++) {
            if (i > 0) {
                mDelayStringBuffer.append(", ");
            }
            final int index = (mEventData + i) % EVENT_QUEUE_LENGTH;
            final float delay = mEventDelays[index] / 1000f; // convert delay from ms into s
            mDelayStringBuffer.append(String.format("%1.1f", delay));
        }

        return mDelayStringBuffer.toString();
    }

    /**
     * Records the state of the application into the {@link android.os.Bundle}.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // BEGIN_INCLUDE(saveinstance)
        super.onSaveInstanceState(outState);
        // Store all variables required to restore the state of the application
        outState.putInt(BUNDLE_LATENCY, mMaxDelay);
        outState.putInt(BUNDLE_STATE, mState);
        outState.putInt(BUNDLE_STEPS, mSteps);
        // END_INCLUDE(saveinstance)
    }

    @Override
    public void onPause() {
        super.onPause();
        // BEGIN_INCLUDE(onpause)
        // Unregister the listener when the application is paused
        unregisterListeners();
        // END_INCLUDE(onpause)
    }

    @Override
    public void onResume() {
        super.onResume();

/*        CardStreamFragment stream = getCardStream();
        if (stream.getVisibleCardCount() < 1) {
            // No cards are visible, started for the first time
            // Prepare all cards and show the intro card.
            initialiseCards();
            showIntroCard();
            // Show the registration card if the hardware is supported, show an error otherwise
            if (isKitkatWithStepSensor()) {
                showRegisterCard();
            } else {
                showErrorCard();
            }
        }*/
    }


}
