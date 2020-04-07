package com.boltcorp.jobservicedemo.JobService;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class TestService extends JobService {

    private static final String TAG = "TestService";
    private static final int TEST_JOB_ID = 10;
    private static final long REFRESH_INTERVAL =  960 * 1000 ; // 16 minutes
    public static boolean isJobScheduled = false;

    public static void scheduleTestJob(Context context) {
        if (!isJobScheduled) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            Log.d(TAG,"Scheduling Test Job with refresh interval : "+REFRESH_INTERVAL);
            int jobScheduleResult;

            @SuppressLint("JobSchedulerService") JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(TEST_JOB_ID, new ComponentName(context, TestService.class))
                    .setMinimumLatency(REFRESH_INTERVAL)
                    .setOverrideDeadline(REFRESH_INTERVAL);
            assert jobScheduler != null;
            jobScheduleResult = jobScheduler.schedule(jobInfoBuilder.build());

            isJobScheduled = jobScheduleResult == JobScheduler.RESULT_SUCCESS;
            if (isJobScheduled) {
                Log.d(TAG, "Test Job Scheduled.");
            } else {
                Log.d(TAG, "Test Job Schedule failed.");
            }
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        String tid = "t"+Thread.currentThread().getId();
        String pid = "p"+android.os.Process.myPid();
        Log.d(TAG, "onStartJob: Test Service Started. pid= "+pid+", tid= "+tid);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        String tid = "t"+Thread.currentThread().getId();
        String pid = "p"+android.os.Process.myPid();
        Log.d(TAG, "onStopJob: Test service stopped. pid= "+pid+"tid= "+tid);
        return false;
    }

    public static void cancelJob(Context context) {
        Log.d(TAG, "Test Job Cancelled.");
        isJobScheduled = false;
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.cancel(TEST_JOB_ID);
    }
}
