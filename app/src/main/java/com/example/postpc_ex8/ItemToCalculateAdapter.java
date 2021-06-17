package com.example.postpc_ex8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemToCalculateAdapter extends RecyclerView.Adapter<ItemToCalculateAdapter.ViewHolder> {

    private List<ItemToCalculate> itemToCalculateList;
    private Context ctx;

    public ItemToCalculateAdapter(List<ItemToCalculate> itemToCalculates){this.itemToCalculateList = itemToCalculates;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        View numberView = inflater.inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(numberView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemToCalculate itemToCalculate = itemToCalculateList.get(position);
        TextView textView = holder.textView;
        ProgressBar progressBar = holder.progressBar;
        TextView progressBarTXT = holder.progressBarTxt;
        int prog = itemToCalculate.curProgress;
        progressBar.setProgress(prog);
        progressBarTXT.setText(prog + "%");

        if (itemToCalculate.workerLastNumber != itemToCalculate.input){
            textView.setText("Calculating roots for " + Long.toString(itemToCalculate.input) + "...");
        }
        else{
            if (itemToCalculate.firstRoot != itemToCalculate.input && itemToCalculate.secondRoot != itemToCalculate.input){
                textView.setText("Roots for "+ Long.toString(itemToCalculate.input) + ": " + Long.toString(itemToCalculate.firstRoot) +"x" + Long.toString(itemToCalculate.secondRoot));}
            else{
                textView.setText("Roots for "+ Long.toString(itemToCalculate.input) + ": " +"number is prime");
            }
        }

        holder.del_row_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) ctx).removeItemFromRecycleView(itemToCalculate);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemToCalculateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ProgressBar progressBar;
        public ImageButton del_row_button;
        public TextView progressBarTxt;

        public ViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.numberItemRowInfo);
            progressBar = itemView.findViewById(R.id.progressBar);
            del_row_button = itemView.findViewById(R.id.del_row_button);
            progressBarTxt = itemView.findViewById(R.id.ProgressBarTXT);
        }
    }
}
