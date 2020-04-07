package com.boltcorp.jobservicedemo.Logger;

import android.content.Context;

import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.StatusPrinter;

public class BoltLog {
    private static final int MAX_LOG_IDX = 1;
    private static final int MIN_LOG_IDX = 1;
    private static final String MAX_LOG_SIZE = "2MB";

    static final String passRegEx = "{\\b(" +
            ")[ :\"']+)\"[^\"]+\", $1\"---\"}";

    static final String logPattern = "%d [%thread] %-5level %logger{36} - %replace(%msg)" + passRegEx + "%n";
    static final String logcatPattern = "[%thread] %replace(%msg)" + passRegEx + "%n";
    static boolean init=false;

    /**
     * Configure Logback
     */
    public static void configLogback(Context ctx) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();
        // setup FileAppender
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern(logPattern);
        encoder1.start();

        //RollingPolicy
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(lc);
        fileAppender.setFile(ctx.getFilesDir().getAbsolutePath() + "/" + SendLogs.JAVA_LOG_FILE_NAME);
        fileAppender.setEncoder(encoder1);
        fileAppender.setName(SendLogs.JAVA_LOG_FILE);
        fileAppender.setAppend(true);
        FixedWindowRollingPolicy rollPolicy = new FixedWindowRollingPolicy();
        rollPolicy.setMaxIndex(MAX_LOG_IDX);
        rollPolicy.setMinIndex(MIN_LOG_IDX);
        rollPolicy.setFileNamePattern(ctx.getFilesDir().getAbsolutePath() + "/eventlogs.%i.txt");
        rollPolicy.setContext(lc);
        rollPolicy.setParent(fileAppender);
        rollPolicy.start();
        SizeBasedTriggeringPolicy<ILoggingEvent> triggerPolicy = new SizeBasedTriggeringPolicy<>();
        triggerPolicy.setMaxFileSize(FileSize.valueOf(MAX_LOG_SIZE));
        triggerPolicy.start();
        fileAppender.setTriggeringPolicy(triggerPolicy);
        fileAppender.setRollingPolicy(rollPolicy);
        fileAppender.start();


        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern(logcatPattern);
        encoder2.start();

        // We want to write important events to separate file since the debug logs file gets filled up fast
        // and only stores information for the last couple of days
        PatternLayoutEncoder encoder3 = new PatternLayoutEncoder();
        encoder3.setContext(lc);
        encoder3.setPattern(logPattern);
        encoder3.start();

        //RollingPolicy 2
        RollingFileAppender<ILoggingEvent> fileAppender2 = new RollingFileAppender<>();
        fileAppender2.setContext(lc);
        fileAppender2.setFile(ctx.getFilesDir().getAbsolutePath() + "/" + SendLogs.ERROR_EVENTS_FILE_NAME);
        fileAppender2.setEncoder(encoder3);
        fileAppender2.setName(SendLogs.ERROR_EVENTS_FILE);
        fileAppender2.setAppend(true);
        FixedWindowRollingPolicy rollPolicy2 = new FixedWindowRollingPolicy();
        rollPolicy2.setMaxIndex(MAX_LOG_IDX);
        rollPolicy2.setMinIndex(MIN_LOG_IDX);
        rollPolicy2.setFileNamePattern(ctx.getFilesDir().getAbsolutePath() + "/importantEvents.%i.txt");
        rollPolicy2.setContext(lc);
        rollPolicy2.setParent(fileAppender2);
        rollPolicy2.start();
        SizeBasedTriggeringPolicy<ILoggingEvent> triggerPolicy2 = new SizeBasedTriggeringPolicy<>();
        triggerPolicy2.setMaxFileSize(FileSize.valueOf(MAX_LOG_SIZE));
        triggerPolicy2.start();
        fileAppender2.setTriggeringPolicy(triggerPolicy2);
        fileAppender2.setRollingPolicy(rollPolicy2);

        //We want to log only error level messages in this file
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setContext(lc);
        levelFilter.setLevel(Level.ERROR);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        fileAppender2.addFilter(levelFilter);
        levelFilter.start();

        fileAppender2.start();

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setAdditive(false);
        root.addAppender(fileAppender);
        root.addAppender(fileAppender2);
        StatusPrinter.print(lc);
    }

    // for BoltLog testing
    public static String getLastLogMessage() {
        try {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            OutputStreamAppender outputStreamAppender = (OutputStreamAppender) lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).getAppender("TestAppender");
            return (((ByteArrayOutputStream) outputStreamAppender.getOutputStream()).toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
