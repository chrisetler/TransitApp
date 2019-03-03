package app.transit.cetle.transitapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import static app.transit.cetle.transitapp.App.CHANNEL_ID;
import static app.transit.cetle.transitapp.MainActivity.url;


public class ExampleService extends Service {

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private static final int NOTIF_ID = 1;

    private RequestQueue queue;
    private TransitDataModel model;
    private String endpoint;

    private boolean isDestroying = false;

    private Thread loop;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    //start the service calculations
    private void init() {
        new Thread(() -> {
            queue = Volley.newRequestQueue(ExampleService.this);
            checkForUpdate();
        }).start();


    }

    //check service for updates after a delay
    private void checkAfterDelay() {
        if (isDestroying) return;

        final Handler handler = new Handler();
        Integer minutesUntil = 1;
        try {
            minutesUntil = Integer.parseInt(model.minutesUntil);
        } catch (NumberFormatException ignored) {
        }


        int delayMillis = 60000;
        if (minutesUntil == 1) {
            delayMillis = 5000; //check every 5 seconds if less than 1 minute
        } else if (minutesUntil == 2) {
            delayMillis = 15000; //check every 15 seconds if 2 minutes
        } else if (minutesUntil == 3) {
            delayMillis = 30000; //check every 30 seconds if 3 minutes
        } else if (minutesUntil == 4) {
            delayMillis = 45000; //check every 45 seconds if 4 minutes
        }


        handler.postDelayed(this::checkForUpdate, 1000);
    }

    //check service for updates
    private void checkForUpdate() {
        if (isDestroying) return;


        //do work here
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + endpoint, response -> {

            Gson gson = new Gson();
            TransitDataModel[] list = gson.fromJson(response, TransitDataModel[].class);

            if (updateModel(list)) {
                //if the model was updated then update the notification
                updateNotification(model);
            }

            checkAfterDelay();

        }, error -> {
            checkAfterDelay();
        });

        queue.add(stringRequest);


    }

    //updates the model we are tracking by looking for one of the new ones that has the same name
    //returns true if the model was updated, false if not
    private boolean updateModel(TransitDataModel[] list) {
        boolean inList = false;

        for (TransitDataModel newModel : list) {
            if (newModel.name.equals(model.name)) {
                inList = true;
                //if the ETA changed update notification. If it is 0 keep updated so it keeps vibrating for attention
                if (newModel.minutesUntil.equals("0") || !newModel.minutesUntil.equals(model.minutesUntil)) {
                    model = newModel;
                    return true;
                }
                break;
            }
        }
        if (!inList) {
            //item is no longer in the list so remove it
            stopForegroundService();
        }
        return false;
    }


    //create the service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    return startForegroundService(intent, flags, startId);
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);


    }


    private int startForegroundService(Intent intent, int flags, int startId) {
        String modelString = intent.getStringExtra("TransitDataModel");
        Gson gson = new Gson();
        model = gson.fromJson(modelString, TransitDataModel.class);

        endpoint = intent.getStringExtra("Endpoint");


        builder = getNotificationBuilder();
        updateNotification(model);

        startForeground(NOTIF_ID, builder.build());


        init();
        return START_NOT_STICKY;

    }

    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        onDestroy();

        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }


    private NotificationCompat.Builder getNotificationBuilder() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        //set up the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle("title")
                .setSmallIcon(R.drawable.ic_subway_black_24dp)
                .setContentIntent(pendingIntent);


        // Add a cancel button intent in notification.
        Intent stopIntent = new Intent(this, ExampleService.class);
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, stopIntent, 0);
        //must include a drawable but it is not shown
        NotificationCompat.Action stopAction = new NotificationCompat.Action(android.R.drawable.ic_delete, "Cancel", pendingPlayIntent);
        builder.addAction(stopAction);

        return builder;

    }


    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification(TransitDataModel model) {
        if (isDestroying) return;


        builder.setContentTitle(model.name);
        builder.setContentText("ETA: " + model.minutesUntil + " min");

        Notification notification = builder.build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIF_ID, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroying = true;
        if (loop != null) loop.interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}