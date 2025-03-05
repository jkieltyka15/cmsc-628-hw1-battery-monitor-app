package com.example.batterymonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // battery level objects
    private StringBuilder batteryLevel;
    private TextView textView_batteryLevel_value;   // shows the percentage of battery remaining

    // battery service objects
    private TextView textView_batteryService_value; // shows the state of the battery monitor service
    private Button button_batteryService_start;     // starts battery monitor service
    private Button button_batteryService_stop;      // stops battery monitor service


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

        // initialize battery level objects
        textView_batteryLevel_value = (TextView)(findViewById(R.id.textView_batteryLevel_value));

        // initialize battery service objects
        textView_batteryService_value = (TextView)(findViewById(R.id.textView_batteryService_value));
        button_batteryService_start = (Button)(findViewById(R.id.button_batteryService_start));
        button_batteryService_stop = (Button)(findViewById(R.id.button_batteryService_stop));

        // setup button listeners
        button_batteryService_start.setOnClickListener(this);
        button_batteryService_stop.setOnClickListener(this);

        // setup default text for battery level
        int batteryLevelTextColor = (int)(getResources().getColor(R.color.black, getTheme()));
        textView_batteryLevel_value.setTextColor(batteryLevelTextColor);
        textView_batteryLevel_value.setText(R.string.textView_batteryLevel_unknown);

        // setup default text for battery service
        int batteryServiceTextColor = (int)(getResources().getColor(R.color.red, getTheme()));
        textView_batteryService_value.setTextColor(batteryServiceTextColor);
        textView_batteryService_value.setText(R.string.textView_batteryService_disabled);
    }


    /**
     * Called when Activity is partially hidden or the user navigates to a different activity,
     * but is not fully stopped
     */
    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * Called before Activity is about to be destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Called when a button is clicked
     *
     * @param view: View that was clicked
     */
    @Override
    public void onClick(View view) {

        // get ID of button clicked
        int buttonId = view.getId();

        // start battery service button clicked
        if (buttonId == R.id.button_batteryService_start) {
            int textColor = (int)(getResources().getColor(R.color.green, getTheme()));
            textView_batteryService_value.setTextColor(textColor);
            textView_batteryService_value.setText(R.string.textView_batteryService_enabled);
        }

        // stop battery service button clicked
        else if (buttonId == R.id.button_batteryService_stop) {
            int textColor = (int)(getResources().getColor(R.color.red, getTheme()));
            textView_batteryService_value.setTextColor(textColor);
            textView_batteryService_value.setText(R.string.textView_batteryService_disabled);
        }
    }
}