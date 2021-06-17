package com.example.postpc_ex8;

import java.io.Serializable;
import java.util.Comparator;

public class ItemToCalculate implements Serializable {
    public long input;
    public int curProgress;
    public long workerLastNumber;
    public long firstRoot;
    public long secondRoot;

    public ItemToCalculate(long input){this.input = input; curProgress = 0; workerLastNumber = 0; firstRoot = -1; secondRoot = -1;}
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