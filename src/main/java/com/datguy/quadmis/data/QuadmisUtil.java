package com.datguy.quadmis.data;

import java.util.Arrays;
import java.util.Random;

public class QuadmisUtil {

    // Source: https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array/21454317#21454317
    private static Random rand;

    public static <T> void shuffle(int[] arr) {
        if (rand == null) {
            rand = new Random();
        }

        for (int i = arr.length - 1; i > 0; i--) {
            swap(arr, i, rand.nextInt(i + 1));
        }
    }

    public static <T> void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int[] shuffled(int[] arr) {
        int[] copy = Arrays.copyOf(arr, arr.length);
        shuffle(copy);
        return copy;
    }
}
