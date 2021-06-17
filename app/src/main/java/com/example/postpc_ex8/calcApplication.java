package com.example.postpc_ex8;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import androidx.work.WorkManager;

import java.util.ArrayList;

public class calcApplication extends Application {

    private Context ctx = null;
    public static ArrayList<ItemToCalculate> itemToCalculateArrayList;

    @Override
    public void onCreate() {
        super.onCreate();
        if (ctx == null){ctx = this;}
        if (itemToCalculateArrayList == null){itemToCalculateArrayList = new ArrayList<ItemToCalculate>();}
    }
}
