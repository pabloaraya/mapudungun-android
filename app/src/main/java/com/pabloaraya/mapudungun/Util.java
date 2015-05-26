package com.pabloaraya.mapudungun;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

/**
 * Created by pablo on 5/25/15.
 */
public class Util {

    public static int randomInt(int min, int max) {

        // Random object
        Random rand = new Random();

        // Set min and max
        int randomNum = rand.nextInt((max - min) + 1) + min;

        // Return number
        return randomNum;
    }

    // Method to remove the last letter, using to remove the value \n
    public static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

    public Object remove(int index, JSONArray array) {

        JSONArray output = new JSONArray();
        int len = array.length();
        for (int i = 0; i < len; i++)   {
            if (i != index) {
                try {
                    output.put(array.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return this;
                }
            }
        }
        return output;
    }

    public static String upperCaseFirstLetter(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
