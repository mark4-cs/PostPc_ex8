package com.example.postpc_ex8;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

public class ItemToCalculate implements Serializable {
    public long input;
    public int curProgress;
    public long workerLastNumber;
    public long firstRoot;
    public long secondRoot;
    public UUID workerID;

    public ItemToCalculate(long input){this.input = input; curProgress = 0; workerLastNumber = 0; firstRoot = -1; secondRoot = -1;}

    public String itemTotextRepr(){
        return this.input + "#" + this.curProgress + "#" + this.workerLastNumber + "#" + this.firstRoot + "#" + this.secondRoot + "#" + this.workerID.toString();
    }

    public void editFromString(String input){
        String[] inputs = input.split("#");
        this.input = Long.parseLong(inputs[0]);
        this.curProgress = Integer.parseInt(inputs[1]);
        this.workerLastNumber = Long.parseLong(inputs[2]);
        this.firstRoot = Long.parseLong(inputs[3]);
        this.secondRoot = Long.parseLong(inputs[4]);
        this.workerID = UUID.fromString(inputs[5]);
    }
}

class ItemToCalculateComparator implements Comparator<ItemToCalculate>{

    @Override
    public int compare(ItemToCalculate t1, ItemToCalculate t2) {
        if (t1.workerLastNumber < t1.input && t2.workerLastNumber == t2.input){
            return -1;
        }
        if (t1.workerLastNumber == t1.input && t2.workerLastNumber < t2.input){
            return 1;
        }
        if (t1.input < t2.input){
            return -1;
        }
        return 1;

    }
}