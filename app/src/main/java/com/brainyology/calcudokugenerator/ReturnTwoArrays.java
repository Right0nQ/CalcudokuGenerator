package com.brainyology.calcudokugenerator;

/**
 * Created by testtube24 on 12/31/17.
 */

class ReturnTwoArrays {
    int[] arrayOne;
    int[] arrayTwo;
    int arrayOneLength;
    int arrayTwoLength;
    public ReturnTwoArrays (int[] arrayOne, int[] arrayTwo, int arrayOneLength, int arrayTwoLength) {
        this.arrayOne = arrayOne;
        this.arrayTwo = arrayTwo;
        this.arrayOneLength = arrayOneLength;
        this.arrayTwoLength = arrayTwoLength;
    }

    public int[] getArrayOne () {
        return arrayOne;
    }

    public int lengthArrayOne () {
        return arrayOneLength;
    }

    public int[] getArrayTwo () {
        return arrayTwo;
    }

    public int lengthArrayTwo () {
        return arrayTwoLength;
    }
}