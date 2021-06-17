package com.example.postpc_ex8;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    Context ctx = null;
    ArrayList<ItemToCalculate> itemToCalculateArrayList;
    ItemToCalculateAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ctx == null){ctx = this;}
        if (itemToCalculateArrayList == null){itemToCalculateArrayList = calcApplication.itemToCalculateArrayList;}
        if (adapter == null) {adapter = new ItemToCalculateAdapter(itemToCalculateArrayList);}
        RecyclerView rvItems = findViewById(R.id.rvNumbersToCalc);

        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(ctx));

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setTitle("Insert number");
                final EditText input = new EditText(ctx);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        long number = Long.parseLong(input.getText().toString());
                        ItemToCalculate itemToCalculate = new ItemToCalculate(number);
                        itemToCalculateArrayList.add(itemToCalculate);
                        Collections.sort(itemToCalculateArrayList, new ItemToCalculateComparator());
                        adapter.notifyDataSetChanged();
                        initWork(itemToCalculate, 2);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alert.show();
            }
        });
    }

    public void removeItemFromRecycleView(ItemToCalculate itemToCalculate){
        itemToCalculateArrayList.remove(itemToCalculate);
        adapter.notifyDataSetChanged();
    }

    private void initWork(ItemToCalculate itemToCalculate, long currentProgress){
        long number = itemToCalculate.input;
        WorkManager wm = WorkManager.getInstance(ctx);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyRootCalculatorWorker.class)
                .setInputData(new Data.Builder().putLong("target", number).putLong("startFrom", currentProgress).build())
                .build();
        wm.enqueue(oneTimeWorkRequest);
        LiveData<WorkInfo> wild = wm.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId());
        wild.observeForever(new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                long current = workInfo.getProgress().getLong("current", 0);
                long total = workInfo.getProgress().getLong("total", -1);
                if (total != -1){
                    itemToCalculate.curProgress = (int) (((double)current / total) * 100);
                    itemToCalculate.workerLastNumber = current;
                    Collections.sort(itemToCalculateArrayList, new ItemToCalculateComparator());
                    adapter.notifyDataSetChanged();
                }
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED){
                    itemToCalculate.curProgress = 100;
                    itemToCalculate.workerLastNumber = itemToCalculate.input;
                    long root1 = workInfo.getOutputData().getLong("root1", 0);
                    long root2 = workInfo.getOutputData().getLong("root2", -1);
                    itemToCalculate.firstRoot = root1;
                    itemToCalculate.secondRoot = root2;
                    Collections.sort(itemToCalculateArrayList, new ItemToCalculateComparator());
                    adapter.notifyDataSetChanged();
                }

                if (workInfo.getState() == WorkInfo.State.FAILED){
                    long currentProgress = workInfo.getOutputData().getLong("current", 2);
                    itemToCalculate.workerLastNumber = currentProgress;
                    initWork(itemToCalculate, currentProgress);
                }

            }
        });
    }

}