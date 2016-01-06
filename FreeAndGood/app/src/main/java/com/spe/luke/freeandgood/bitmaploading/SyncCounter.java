package com.spe.luke.freeandgood.bitmaploading;

/**
 * Created by Luke on 7/13/2015.
 */
//static class to count number of concurrent asynctasks
public final class SyncCounter
{

    private static int i = 0;

    public static synchronized void inc() {
        i++;
    }

    public static synchronized void dec() {
        i--;
    }

    public static synchronized int current() {
        return i;
    }
}