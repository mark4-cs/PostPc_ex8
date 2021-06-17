package com.example.postpc_ex8;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Collections;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}, application = calcApplication.class)
public class MainActivityTest {
    private ActivityController<MainActivity> activityController;
    private MainActivity activityUnderTest;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(MainActivity.class);
        activityUnderTest = activityController.get();
        activityController.create().start().resume();
    }

    @Test
    public void flowTest1(){
        // on startup list of items to calculate is empty
        assertTrue(activityUnderTest.itemToCalculateArrayList.isEmpty());
    }

    @Test
    public void flowTest2(){
        //check new number is ordered before calculated number
        ItemToCalculate calculated = new ItemToCalculate(5);
        calculated.workerLastNumber = 5;
        calculated.curProgress = 5;
        calculated.firstRoot = 1;
        calculated.secondRoot = 5;

        activityUnderTest.itemToCalculateArrayList.add(calculated);
        activityUnderTest.itemToCalculateArrayList.add(new ItemToCalculate(4));

        Collections.sort(activityUnderTest.itemToCalculateArrayList, new ItemToCalculateComparator());

        assertEquals(calculated, activityUnderTest.itemToCalculateArrayList.get(1));

        activityUnderTest.itemToCalculateArrayList.clear();
    }

    @Test
    public void flowTest3(){
        //check order between 3 numbers (calculated, in progress, and and in progress with smaller value)
        ItemToCalculate calculated = new ItemToCalculate(5);
        calculated.workerLastNumber = 5;
        calculated.curProgress = 5;
        calculated.firstRoot = 1;
        calculated.secondRoot = 5;

        ItemToCalculate small_inprog = new ItemToCalculate(10);
        ItemToCalculate big_inprog = new ItemToCalculate(30);

        activityUnderTest.itemToCalculateArrayList.add(calculated);
        activityUnderTest.itemToCalculateArrayList.add(small_inprog);
        activityUnderTest.itemToCalculateArrayList.add(big_inprog);
        Collections.sort(activityUnderTest.itemToCalculateArrayList, new ItemToCalculateComparator());

        shadowOf(getMainLooper()).idle();
        // expected to small_inprog be first, second big_inprog, last calculated

        assertEquals(activityUnderTest.itemToCalculateArrayList.get(0), small_inprog);
        assertEquals(activityUnderTest.itemToCalculateArrayList.get(1), big_inprog);
        assertEquals(activityUnderTest.itemToCalculateArrayList.get(2), calculated);


        activityUnderTest.itemToCalculateArrayList.clear();
    }
}
