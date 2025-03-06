package com.example.batterymonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Service for getting battery information and then broadcasting the battery level
 */
public class BatteryService extends Service {

    // actions for local broadcast
    public static final String ACTION_BATTERY_LEVEL = "com.example.ACTION_BATTERY_LEVEL";
    public static final String EXTRA_BATTERY_LEVEL  = "com.example.EXTRA_BATTERY_LEVEL";

    // broadcast receiver for battery level
    private final BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {


        /**
         * Retrieves battery level and then broadcasts it
         *
         * @param context: Context in which the receiver is running
         * @param intent: Intent being received
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            // null check
            if (null == context || null == intent) {
                return;
            }

            // status of battery changed
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

                // get battery information
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                // only broadcast valid battery levels
                if (0 < scale  && 0 <= level) {

                    // calculate battery percentage
                    int batteryLevel = Math.round(((float) level / (float) scale) * 100f);

                    // create intent for battery level
                    Intent localIntent = new Intent(ACTION_BATTERY_LEVEL);
                    localIntent.putExtra(EXTRA_BATTERY_LEVEL, batteryLevel);

                    // send local broadcast with battery level
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
                }
            }
        }
    };


    /**
     * Registers battery broadcast receiver
     */
    @Override
    public void onCreate() {

        super.onCreate();

        // register receiver for battery changed broadcasts
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastReceiver, filter);
    }


    /**
     * Unregisters battery receiver and performs cleanup
     */
    @Override
    public void onDestroy() {

        // unregister battery receiver service
        unregisterReceiver(batteryBroadcastReceiver);

        super.onDestroy();
    }


    /**
     * Always return null on bind since this is not a bound service
     *
     * @param intent: Intent to bind service
     *
     * @return Always null
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
