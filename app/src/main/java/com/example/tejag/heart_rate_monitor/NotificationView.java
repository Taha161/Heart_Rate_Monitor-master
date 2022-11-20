package com.example.tejag.heart_rate_monitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationView extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        textView = (TextView) findViewById(R.id.textView);

        textView.setText("Alert..!!! Low Soil Moisture! Plant needs Water.! Please Supply Water to Plant");
    }
}
