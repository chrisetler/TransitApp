package app.transit.cetle.transitapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;

import static app.transit.cetle.transitapp.ExampleService.ACTION_START_FOREGROUND_SERVICE;

//manages keeping one service instance at a time for the service for tracking arrivals
public class ServiceSingleton {

    private static ServiceSingleton instance;
    private boolean isServiceRunning = false;


    static ServiceSingleton getInstance() {
        if (instance == null) instance = new ServiceSingleton();
        return instance;
    }

    public void start(TransitDataModel model, String endpoint, Context context) {

        if (isServiceRunning) stop(context);
        isServiceRunning = true;

        Intent serviceIntent = new Intent(context, ExampleService.class);
        serviceIntent.setAction(ACTION_START_FOREGROUND_SERVICE);

        Gson gson = new Gson();
        String modelString = gson.toJson(model);

        serviceIntent.putExtra("TransitDataModel", modelString);
        serviceIntent.putExtra("Endpoint", endpoint);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public void stop(Context context) {
        Intent serviceIntent = new Intent(context, ExampleService.class);
        context.stopService(serviceIntent);
        isServiceRunning = false;
    }


}
