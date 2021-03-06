package com.example.postpc_ex8;

import android.content.Context;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.time.Instant;

public class MyRootCalculatorWorker extends Worker {

    public MyRootCalculatorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        long start = System.currentTimeMillis();

        long total = getInputData().getLong("target", -1);
        long startFrom = getInputData().getLong("startFrom", 2);
        long root1 = -1;
        long root2 = -1;
        int progress = 0;
        for (long i = startFrom; i <=  Math.sqrt(total); i++){
            if (i % 10000 == 0){
                progress = (int) ((double) i*100 / Math.sqrt(total));
                setProgressAsync(new Data.Builder().putInt("progress", progress).putLong("lastNumber", i).build());
            }
            if (total % i == 0){
                root1 = i;
                root2 = total / i;
                break;
            }
            if (System.currentTimeMillis() - start > 600000){
                return Result.failure(new Data.Builder().putInt("progress", progress).putLong("lastNumber", i).build());
            }
        }
        if (root1 == -1){root1 = total; root2 = 1;}
        return Result.success(new Data.Builder().putLong("root1", root1).putLong("root2", root2).build());
    }
}
