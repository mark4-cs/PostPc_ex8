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
    ItemToCalculateAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ctx == null){ctx = this;}
        if (adapter == null) {adapter = new ItemToCalculateAdapter(calcApplication.data.getValue());}
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
                        long number;
                        try {
                            number = Long.parseLong(input.getText().toString());
                        }catch (Exception NumberFormatException){
                            Toast.makeText(ctx, "Number is too big!!!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ItemToCalculate itemToCalculate = new ItemToCalculate(number);
                        ((calcApplication)getApplication()).initWork(itemToCalculate, 2);
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

        calcApplication.data.observeForever(new Observer<ArrayList<ItemToCalculate>>() {
            @Override
            public void onChanged(ArrayList<ItemToCalculate> itemToCalculates) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void removeItemFromRecycleView(ItemToCalculate itemToCalculate){
        calcApplication.itemToCalculateArrayList.remove(itemToCalculate);
        calcApplication.saveTosp();
        adapter.notifyDataSetChanged();
    }
}