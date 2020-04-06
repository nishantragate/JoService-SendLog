package com.boltcorp.jobservicedemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.boltcorp.jobservicedemo.JobService.TestService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button mStartJobButton = findViewById(R.id.startJob);
        mStartJobButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TestService.scheduleTestJob(getApplicationContext());
                Log.d(TAG, "onClick: request to start test service");
            }
        });
    }
}
