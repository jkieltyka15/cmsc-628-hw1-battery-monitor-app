package com.example.batterymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Uses a local battery monitoring service and shows if the service is running.
 * Displays the battery level gotten from the battery monitoring service
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // handles UI changes safely
    private Handler handler;

    // battery level
    private StringBuilder batteryLevelText; // holds battery level value text
    private TextView textView_batteryLevel_value;   // shows the percentage of battery remaining
    private Button button_batteryService_start;     // starts battery monitor service
    private Button button_batteryService_stop;      // stops battery monitor service

    // battery service
    private boolean isBatteryServiceEnabled;    // flag for determining if battery service is enabled
    private TextView textView_batteryService_value; // shows the state of the battery service

    // broadcast receiver for getting battery level
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

        /**
         * Updates battery level value text upon receiving a battery level update
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

            // battery level changed
            if (BatteryService.ACTION_BATTERY_LEVEL.equals(intent.getAction())) {

                // get the battery level from broadcast
                int batteryLevel = intent.getIntExtra(BatteryService.EXTRA_BATTERY_LEVEL, -1);

                // update the battery level value text
                handler.post(new UpdateBatteryLevel(batteryLevel));
            }
        }
    };


    private class EnableBatteryServiceWork implements Runnable {

        /**
         * Sets battery service value TextView to display enabled in green
         */
        @Override
        public void run() {

            // update battery service state text
            int textColor = (int)(getResources().getColor(R.color.green, getTheme()));
            textView_batteryService_value.setTextColor(textColor);
            textView_batteryService_value.setText(R.string.textView_batteryService_enabled);

            // disable start button
            button_batteryService_start.setEnabled(false);
            button_batteryService_start.setClickable(false);

            // enable stop button
            button_batteryService_stop.setEnabled(true);
            button_batteryService_stop.setClickable(true);
        }
    }


    private class DisableBatteryServiceWork implements Runnable {

        /**
         * Sets battery service value TextView to display disabled in red
         */
        @Override
        public void run() {

            // update battery service state text
            int textColor = (int)(getResources().getColor(R.color.red, getTheme()));
            textView_batteryService_value.setTextColor(textColor);
            textView_batteryService_value.setText(R.string.textView_batteryService_disabled);

            // disable stop button
            button_batteryService_stop.setEnabled(false);
            button_batteryService_stop.setClickable(false);

            // enable start button
            button_batteryService_start.setEnabled(true);
            button_batteryService_start.setClickable(true);
        }
    }


    private class UpdateBatteryLevel implements Runnable {

        final static int BATTERY_LEVEL_LOW = 20; // battery level is low
        int batteryLevel;   // battery power level

        UpdateBatteryLevel(int batteryLevel) {
            this.batteryLevel = batteryLevel;
        }

        /**
         * Sets battery level value TextView
         */
        @Override
        public void run() {

            int textColor = (int) (getResources().getColor(R.color.green, getTheme()));

            // low battery
            if (BATTERY_LEVEL_LOW >= this.batteryLevel) {
                textColor = (int) (getResources().getColor(R.color.red, getTheme()));
            }

            textView_batteryLevel_value.setTextColor(textColor);

            batteryLevelText.setLength(0);
            batteryLevelText.append(this.batteryLevel).append('%');
            textView_batteryLevel_value.setText(batteryLevelText);
        }
    }


    /**
     * Called when Activity is created
     *
     * @param savedInstanceState: Most recent data in the save instance state or null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // perform standard setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize handler
        handler = new Handler();

        // initialize battery level variables
        batteryLevelText = new StringBuilder();
        textView_batteryLevel_value = (TextView)(findViewById(R.id.textView_batteryLevel_value));

        // initialize battery service variables
        isBatteryServiceEnabled = false;
        textView_batteryService_value = (TextView)(findViewById(R.id.textView_batteryService_value));
        button_batteryService_start = (Button) (findViewById(R.id.button_batteryService_start));
        button_batteryService_stop = (Button) (findViewById(R.id.button_batteryService_stop));

        // setup button listeners
        button_batteryService_start.setOnClickListener(this);
        button_batteryService_stop.setOnClickListener(this);

        // setup default text for battery level
        int batteryLevelTextColor = (int)(getResources().getColor(R.color.black, getTheme()));
        textView_batteryLevel_value.setTextColor(batteryLevelTextColor);
        textView_batteryLevel_value.setText(R.string.textView_batteryLevel_unknown);

        // setup default text for battery service
        handler.post(new DisableBatteryServiceWork());
    }


    /**
     * Called when Activity is brought back into focus
     */
    @Override
    protected void onResume() {

        super.onResume();

        // register broadcast receiver to get battery updates from battery service
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(BatteryService.ACTION_BATTERY_LEVEL);
        localBroadcastManager.registerReceiver(batteryReceiver, intentFilter);
    }


    /**
     * Called when Activity is partially hidden or the user navigates to a different activity,
     * but is not fully stopped
     */
    @Override
    protected void onPause() {

        super.onPause();

        // unregister battery receiver
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(batteryReceiver);
    }


    /**
     * Called when a button is clicked
     *
     * @param view: View that was clicked
     */
    @Override
    public void onClick(View view) {

        // null check
        if (null == view) {
            return;
        }

        // get ID of button clicked
        int buttonId = view.getId();

        // start battery service button clicked
        if (buttonId == R.id.button_batteryService_start) {

            // battery service is already enabled
            if (isBatteryServiceEnabled) {
                return;
            }

            // update battery service value text
            handler.post(new EnableBatteryServiceWork());

            // start battery service
            Intent serviceIntent = new Intent(this, BatteryService.class);
            startService(serviceIntent);
            isBatteryServiceEnabled = true;
        }

        // stop battery service button clicked
        else if (buttonId == R.id.button_batteryService_stop) {

            // battery service is already disabled
            if (!isBatteryServiceEnabled) {
                return;
            }

            // update battery service value text
            handler.post(new DisableBatteryServiceWork());

            // start battery service
            Intent serviceIntent = new Intent(this, BatteryService.class);
            stopService(serviceIntent);
            isBatteryServiceEnabled = false;
        }
    }
}