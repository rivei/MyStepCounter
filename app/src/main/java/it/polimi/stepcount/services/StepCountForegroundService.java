package it.polimi.stepcount.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import it.polimi.stepcount.R;
import it.polimi.stepcount.activities.StepSensorActivity;

public class StepCountForegroundService extends Service {
    //private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    private static final String TAG = StepCountForegroundService.class.getSimpleName();
    private static final String PACKAGE_NAME = "it.polimi.stepcount";

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_STEPS = PACKAGE_NAME + ".steps";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private final IBinder mBinder = new MyBinder();

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";

    private Handler mServiceHandler;
    private int mSteps;

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;
    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;


    public StepCountForegroundService() {
    }

/*
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
*/

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "My foreground service onCreate().");

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            resumeStepUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;

/*        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PLAY:
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);*/
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
    //                mNotificationManager.startServiceInForeground(new Intent(this,
//                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
               // startForegroundService(StepCountForegroundService.this, StepSensorActivity.class);
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

/*    *//* Used to build and start foreground service. *//*
    private void startForegroundService()
    {
        Log.d(TAG, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        Notification.Builder builder = new Notification.Builder(this);

        // Make notification show big text.
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_music_32);
        //builder.setLargeIcon(largeIconBitmap);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, StepCountForegroundService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        Notification.Action playAction = new Notification.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        builder.addAction(playAction);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, StepCountForegroundService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        Notification.Action prevAction = new Notification.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService()
    {
        Log.d(TAG, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }*/

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, StepCountForegroundService.class);

        CharSequence text = Utils.getStepCount(mSteps);//Utils.getLocationText(mSteps);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, StepSensorActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch, getString(R.string.launch_activity),
                        activityPendingIntent)
                .addAction(R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                        servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Utils.getStepTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

/*        Notification notification =
                new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.icon)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.ticker_text))
                        .build(); //a way avoid the deprecated function

        startForeground(ONGOING_NOTIFICATION_ID, notification);*/

        return builder.build();
    }


    private void onUpdateStep(int stepCount) {
        Log.i(TAG, "New location: " + stepCount);

        mSteps = stepCount;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_STEPS, stepCount);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    private void getLastStepCount(){
        //TODO: get last step count
        //mSteps;
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class MyBinder extends Binder {
        public StepCountForegroundService getService() {
            return StepCountForegroundService.this;
        }
    }

    /**
     * TODO: Removes step updates. Note that in this sample we merely log the; Should have same effect like open App
     * {@link SecurityException}.
     */
    public void resumeStepUpdates() {
        Log.i(TAG, "Removing step updates");
        try {
            //mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingStepUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {//TODO: what is this?
            Utils.setRequestingStepUpdates(this, true);
            Log.e(TAG, "Lost step permission. Could not remove updates. " + unlikely);
        }
    }
}
