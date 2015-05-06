package com.pabloaraya.mapudungun;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    // View elements
    protected EditText editTextTranslate;
    protected ProgressBar progressBar;
    protected CardView cardViewResult;
    protected TextView textViewWordResult;
    protected TextView textViewResult;
    protected ImageView imageViewRefresh;

    // Empty constructor
    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate layout
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        // References to view elements
        editTextTranslate   = (EditText)v.findViewById(R.id.editTextTranslate);
        progressBar         = (ProgressBar)v.findViewById(R.id.progressBar);
        cardViewResult      = (CardView)v.findViewById(R.id.cardViewResult);
        textViewWordResult  = (TextView)v.findViewById(R.id.textViewWordResult);
        textViewResult      = (TextView)v.findViewById(R.id.textViewResult);
        imageViewRefresh    = (ImageView)v.findViewById(R.id.imageViewRefresh);

        // Remove suggestions
        editTextTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // Listener to changes text to translate
        editTextTranslate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Show loading progress
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Verify the length
                if (s.toString().trim().length() > 0) {

                    // Array of results
                    JSONArray result = new JSONArray();

                    // Go over list of words
                    for (int i = 0; i < MainActivity.mapudungun.length(); i++) {

                        try {

                            // Create a JSONObject for each word
                            JSONObject word = MainActivity.mapudungun.getJSONObject(i);

                            // Verify if exits the word in the "key"
                            if (word.has(s.toString().toLowerCase().trim())) {

                                // Array of words
                                result = word.getJSONArray(s.toString().toLowerCase().trim());

                                // Final string to show in the result
                                String textResult = new String();

                                // Concat all words
                                for (int w = 0; w < result.length(); w++) {
                                    textResult = textResult.concat(result.getString(w)).trim().concat("\n");
                                }

                                // Upper case the first letter
                                String wordCap = s.toString().trim().substring(0, 1).toUpperCase() + s.toString().trim().substring(1);

                                // Show the word searched
                                textViewWordResult.setText(wordCap);

                                // Show the result of words
                                textViewResult.setText(removeLastChar(textResult));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Verify if exists result
                    if (result.length() > 0) {

                        // Show result card
                        cardViewResult.setVisibility(View.VISIBLE);
                    } else {

                        // Hide result card
                        cardViewResult.setVisibility(View.INVISIBLE);
                    }
                }
                // Hide the progress bar
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // Listener for refresh the result card
        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Refresh the daily word
                refreshDailyWord(true);
            }
        });

        // Show the daily word
        refreshDailyWord(false);

        return v;
    }

    // Show the daily word
    public void refreshDailyWord(boolean changeEditText){
        try {

            // Random index number
            int indexRandom = randomInt(0, MainActivity.mapudungun.length());

            // Word object
            JSONObject word = MainActivity.mapudungun.getJSONObject(indexRandom);

            // Translated words
            JSONArray words = word.getJSONArray(word.keys().next());

            // Final text result
            String textResult = new String();

            // Concat results
            for(int i = 0; i < words.length(); i++){
                textResult = textResult.concat(words.getString(i).concat("\n"));
            }

            // Upper case the first letter
            String wordCap = word.keys().next().substring(0,1).toUpperCase() + word.keys().next().substring(1);

            textViewWordResult.setText(wordCap);
            textViewResult.setText(removeLastChar(textResult));

            if(changeEditText){
                editTextTranslate.setText(wordCap);
                editTextTranslate.setSelection(editTextTranslate.getText().length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static int randomInt(int min, int max) {

        // Random object
        Random rand = new Random();

        // Set min and max
        int randomNum = rand.nextInt((max - min) + 1) + min;

        // Return number
        return randomNum;
    }

    // Method to remove the last letter, using to remove the value \n
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

}
