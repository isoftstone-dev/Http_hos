package com.example.httplibrary.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class LogHandler implements LogInterface {
    static final HiLogLabel label=new HiLogLabel(HiLog.DEBUG,0x00101,"网络请求");

    boolean mLoggingEnabled = true;
    int mLoggingLevel = VERBOSE;

    @Override
    public boolean isLoggingEnabled() {
        return mLoggingEnabled;
    }

    @Override
    public void setLoggingEnabled(boolean loggingEnabled) {
        this.mLoggingEnabled = loggingEnabled;
    }

    @Override
    public int getLoggingLevel() {
        return mLoggingLevel;
    }

    @Override
    public void setLoggingLevel(int loggingLevel) {
        this.mLoggingLevel = loggingLevel;
    }

    @Override
    public boolean shouldLog(int logLevel) {
        return logLevel >= mLoggingLevel;
    }

    public void log(int logLevel, String tag, String msg) {
        logWithThrowable(logLevel, tag, msg, null);
    }

    public void logWithThrowable(int logLevel, String tag, String msg, Throwable t) {
        if (isLoggingEnabled() && shouldLog(logLevel)) {
            switch (logLevel) {
                case VERBOSE:
                    HiLog.fatal(label, msg, t);
                    break;
                case WARN:
                    HiLog.warn(label, msg, t);
                    break;
                case ERROR:
                    HiLog.error(label, msg, t);
                    break;
                case DEBUG:
                    HiLog.debug(label, msg, t);
                    break;
                case WTF:
                    checkedWtf(tag, msg, t);
                    break;
                case INFO:
                    HiLog.info(label, msg, t);
                    break;
            }
        }
    }

    private void checkedWtf(String tag, String msg, Throwable t) {
        HiLog.error(label, msg, t);
    }

    @Override
    public void v(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    @Override
    public void v(String tag, String msg, Throwable t) {
        logWithThrowable(VERBOSE, tag, msg, t);
    }

    @Override
    public void d(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    @Override
    public void d(String tag, String msg, Throwable t) {
        logWithThrowable(DEBUG, tag, msg, t);
    }

    @Override
    public void i(String tag, String msg) {
        log(INFO, tag, msg);
    }

    @Override
    public void i(String tag, String msg, Throwable t) {
        logWithThrowable(INFO, tag, msg, t);
    }

    @Override
    public void w(String tag, String msg) {
        log(WARN, tag, msg);
    }

    @Override
    public void w(String tag, String msg, Throwable t) {
        logWithThrowable(WARN, tag, msg, t);
    }

    @Override
    public void e(String tag, String msg) {
        log(ERROR, tag, msg);
    }

    @Override
    public void e(String tag, String msg, Throwable t) {
        logWithThrowable(ERROR, tag, msg, t);
    }

    @Override
    public void wtf(String tag, String msg) {
        log(WTF, tag, msg);
    }

    @Override
    public void wtf(String tag, String msg, Throwable t) {
        logWithThrowable(WTF, tag, msg, t);
    }
}
