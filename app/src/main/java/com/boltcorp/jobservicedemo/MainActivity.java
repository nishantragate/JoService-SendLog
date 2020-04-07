package com.boltcorp.jobservicedemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.boltcorp.jobservicedemo.JobService.TestService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!TestService.isJobScheduled){
            TestService.scheduleTestJob(getApplicationContext());
        }

        final Button mStopJobButton = findViewById(R.id.stopJob);
        mStopJobButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick: request to stop test service");
                TestService.cancelJob(getApplicationContext());
            }
        });

        final Button mSendLogsButton = findViewById(R.id.sendLogs);
        final TextView mTextView = findViewById(R.id.textView);
        mSendLogsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick: request to send logs");
            }
        });
    }
}
