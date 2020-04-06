package com.boltcorp.jobservicedemo.JobService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestService extends JobService {

    private static final String TAG = "TestService";
    private static final int TEST_JOB_ID = 10;
    private static final long REFRESH_INTERVAL =  960 * 1000 ; // 16 minutes
    static boolean isJobScheduled = false;

    @TargetApi(28)
    private static JobInfo.Builder getJobBuilder(Context context) {
        Log.d(TAG, "Job Builder for Session Revoke in Target API 28+");
        @SuppressLint("JobSchedulerService") JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(TEST_JOB_ID, new ComponentName(context, TestService.class))
                .setMinimumLatency(REFRESH_INTERVAL)
                .setOverrideDeadline(REFRESH_INTERVAL);
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .build();
        try {
            Method requiredNetworkMethod = JobInfo.Builder.class.getMethod("setRequiredNetwork", TestService.class);
            jobInfoBuilder = (JobInfo.Builder) requiredNetworkMethod.invoke(jobInfoBuilder, request);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            jobInfoBuilder = null;
        }
        return jobInfoBuilder;
    }

    public static void scheduleTestJob(Context context) {
        if (!isJobScheduled) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            Log.d(TAG,"Scheduling Test Job with refresh interval : "+REFRESH_INTERVAL);
            JobInfo.Builder builder = null;
            int jobScheduleResult = JobScheduler.RESULT_FAILURE;

            if (Build.VERSION.SDK_INT >= 28) {
                builder = getJobBuilder(context);
                if(builder != null){
                    assert jobScheduler != null;
                    jobScheduleResult = jobScheduler.schedule(builder.build());
                }
            }
            if(builder == null) {
                @SuppressLint("JobSchedulerService") JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(TEST_JOB_ID, new ComponentName(context, TestService.class))
                        .setMinimumLatency(REFRESH_INTERVAL)
                        .setOverrideDeadline(REFRESH_INTERVAL);
                assert jobScheduler != null;
                jobScheduleResult = jobScheduler.schedule(jobInfoBuilder.build());
            }

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
        Log.d(TAG, "Test Job Cancelling.");
        isJobScheduled = false;
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert jobScheduler != null;
        jobScheduler.cancel(TEST_JOB_ID);
    }
}
