package com.example.postpc_ex8;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collections;

public class calcApplication extends Application {

    private static Context ctx = null;
    public static ArrayList<ItemToCalculate> itemToCalculateArrayList;
    public static MutableLiveData<ArrayList<ItemToCalculate>> data;


    @Override
    public void onCreate() {
        super.onCreate();
        if (ctx == null){ctx = this;}
        if (itemToCalculateArrayList == null){itemToCalculateArrayList = new ArrayList<ItemToCalculate>();}
        if (data == null){data = new MutableLiveData<ArrayList<ItemToCalculate>>();}
        loadFromSP();
        data.setValue(itemToCalculateArrayList);
        continueWorkAfterStartup();
    }

    public void initWork(ItemToCalculate itemToCalculate, long currentProgress){
        itemToCalculateArrayList.add(itemToCalculate);
        Collections.sort(itemToCalculateArrayList, new ItemToCalculateComparator());
        long number = itemToCalculate.input;
        WorkManager wm = WorkManager.getInstance(ctx);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyRootCalculatorWorker.class)
                .addTag("calculation")
                .setInputData(new Data.Builder().putLong("target", number).putLong("startFrom", currentProgress).build())
                .build();
        itemToCalculate.workerID = oneTimeWorkRequest.getId();
        wm.enqueue(oneTimeWorkRequest);
        observeWorker(itemToCalculate);
    }

    public void continueWorkAfterStartup(){
        for (ItemToCalculate itemToCalculate: itemToCalculateArrayList){
            if (itemToCalculate.curProgress == 100){
                continue;
            }
            long number = itemToCalculate.input;
            WorkManager wm = WorkManager.getInstance(ctx);
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyRootCalculatorWorker.class)
                    .addTag("calculation")
                    .setInputData(new Data.Builder().putLong("target", number).putLong("startFrom", itemToCalculate.workerLastNumber).build())
                    .build();
            itemToCalculate.workerID = oneTimeWorkRequest.getId();
            wm.enqueue(oneTimeWorkRequest);
            observeWorker(itemToCalculate);
        }
    }

    public void observeWorker(ItemToCalculate itemToCalculate){
        WorkManager wm = WorkManager.getInstance(ctx);
        wm.getWorkInfoByIdLiveData(itemToCalculate.workerID).observeForever(new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if (workInfo != null){
                    Data progress = workInfo.getProgress();
                    int prog = progress.getInt("progress", 0);
                    long last = progress.getLong("lastNumber", 2);
                    itemToCalculate.curProgress = prog;
                    itemToCalculate.workerLastNumber = last;
                    Collections.sort(itemToCalculateArrayList, new ItemToCalculateComparator());

                }
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    itemToCalculate.curProgress = 100;
                    itemToCalculate.workerLastNumber = itemToCalculate.input;
                    long root1 = workInfo.getOutputData().getLong("root1", 0);
                    long root2 = workInfo.getOutputData().getLong("root2", -1);
                    itemToCalculate.firstRoot = root1;
                    itemToCalculate.secondRoot = root2;
                    Collections.sort(itemToCalculateArrayList, new ItemToCalculateComparator());
                }

                if (workInfo.getState() == WorkInfo.State.FAILED){
                    Data progress = workInfo.getProgress();
                    int currentProgress = progress.getInt("progress", 0);
                    long last = progress.getLong("lastNumber", 2);
                    itemToCalculate.curProgress = currentProgress;
                    itemToCalculate.workerLastNumber = last;
                    initWork(itemToCalculate, currentProgress);
                }
                saveTosp();
                data.setValue(itemToCalculateArrayList);
            }
        });
    }

    public static Context getInstance(){return ctx;}

    public MutableLiveData<ArrayList<ItemToCalculate>> getData(){return data;}

    public static void saveTosp(){
        String s = "";
        for (ItemToCalculate i : itemToCalculateArrayList){
            s = s.concat(i.itemTotextRepr());
            s = s.concat("%");
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        editor.putString("data", s);
        editor.apply();
    }

    public void loadFromSP(){
        String s = PreferenceManager.getDefaultSharedPreferences(ctx).getString("data", "");
        String[] objects = s.split("%");
        for (String i : objects){
            if (!i.isEmpty()){
                ItemToCalculate j = new ItemToCalculate(0);
                j.editFromString(i);
                itemToCalculateArrayList.add(j);
            }
        }


    }
}
