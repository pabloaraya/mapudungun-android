package com.pabloaraya.mapudungun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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

    // TextView elements
    protected TextView textViewOrientation;
    protected TextView textViewWordResult;
    protected TextView textViewResult;
    protected TextView textViewFromWord;
    protected TextView textViewToWord;

    // ImageView elements
    protected ImageView imageViewRefresh;
    protected ImageView imageViewShare;
    //protected ImageView imageViewFavorite;
    protected ImageView imageViewChange;

    // Typeface elements
    private Typeface robotoLight;
    private Typeface robotoRegular;

    // SharedPreferences element
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorSharedPref;

    // Statics values
    private final static String DEFAULT_TRANSLATE_ORIENTATION = "TRANSLATE_FROM";
    private final static String TRANSLATE_FROM_SPANISH = "SPANISH";
    private final static String TRANSLATE_FROM_MAPUDUNGUN = "MAPUDUNGUN";

    // Aux vars
    private String wordSearched;
    private String wordResulted;

    // Empty constructor
    public SearchActivityFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Load shared preferences
        sharedPref = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editorSharedPref = sharedPref.edit();

        // Inflate layout
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        // Instance typefaces from assets
        robotoLight     = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        robotoRegular   = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

        // References to view elements
        editTextTranslate   = (EditText)v.findViewById(R.id.editTextTranslate);
        progressBar         = (ProgressBar)v.findViewById(R.id.progressBar);
        cardViewResult      = (CardView)v.findViewById(R.id.cardViewResult);

        // References to text view
        textViewOrientation = (TextView)v.findViewById(R.id.textViewOrientation);
        textViewWordResult  = (TextView)v.findViewById(R.id.textViewWordResult);
        textViewResult      = (TextView)v.findViewById(R.id.textViewResult);
        textViewFromWord    = (TextView)v.findViewById(R.id.textViewFromWord);
        textViewToWord      = (TextView)v.findViewById(R.id.textViewToWord);

        // References to image view
        imageViewRefresh    = (ImageView)v.findViewById(R.id.imageViewRefresh);
        imageViewShare      = (ImageView)v.findViewById(R.id.imageViewShare);
        //imageViewFavorite   = (ImageView)v.findViewById(R.id.imageViewFavorite);
        imageViewChange     = (ImageView)v.findViewById(R.id.imageViewChange);

        // Set typefaces
        textViewOrientation.setTypeface(robotoRegular);
        textViewWordResult.setTypeface(robotoLight);
        textViewResult.setTypeface(robotoLight);
        editTextTranslate.setTypeface(robotoLight);

        // Remove suggestions
        editTextTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if(sharedPref.contains(DEFAULT_TRANSLATE_ORIENTATION)){
            if(sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH).equals(TRANSLATE_FROM_SPANISH)){
                textViewFromWord.setText("Español");
                textViewToWord.setText("Mapudungun");
                textViewOrientation.setText("Español → Mapudungun");

                editTextTranslate.setHint("Palabra en español");
                //imageViewFavorite.setVisibility(View.INVISIBLE);
                imageViewRefresh.setVisibility(View.INVISIBLE);
            }else{
                textViewFromWord.setText("Mapudungun");
                textViewToWord.setText("Español");
                textViewOrientation.setText("Mapudungun → Español");

                editTextTranslate.setHint("Palabra en mapudungun");
                //imageViewFavorite.setVisibility(View.VISIBLE);
                imageViewRefresh.setVisibility(View.VISIBLE);
            }
        }else{
            textViewFromWord.setText("Español");
            textViewToWord.setText("Mapudungun");
            textViewOrientation.setText("Español → Mapudungun");

            editTextTranslate.setHint("Palabra en español");
            //imageViewFavorite.setVisibility(View.INVISIBLE);
            imageViewRefresh.setVisibility(View.INVISIBLE);

            editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH);
            editorSharedPref.commit();
        }

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

                    searchWord(s);
                }else{
                    // Hide result card
                    cardViewResult.setVisibility(View.INVISIBLE);
                }
                // Hide the progress bar
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // Listener to change translate
        imageViewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPref.contains(DEFAULT_TRANSLATE_ORIENTATION)){
                    if(sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH).equals(TRANSLATE_FROM_SPANISH)){
                        textViewFromWord.setText("Mapudungun");
                        textViewToWord.setText("Español");
                        textViewOrientation.setText("Mapudungun → Español");

                        editTextTranslate.setHint("Palabra en mapudungun");

                        //imageViewFavorite.setVisibility(View.VISIBLE);
                        imageViewRefresh.setVisibility(View.VISIBLE);
                        editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_MAPUDUNGUN);
                    }else{
                        textViewFromWord.setText("Español");
                        textViewToWord.setText("Mapudungun");
                        textViewOrientation.setText("Español → Mapudungun");

                        editTextTranslate.setHint("Palabra en español");

                        //imageViewFavorite.setVisibility(View.INVISIBLE);
                        imageViewRefresh.setVisibility(View.INVISIBLE);
                        editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH);
                    }
                }else{
                    textViewFromWord.setText("Mapudungun");
                    textViewToWord.setText("Español");
                    textViewOrientation.setText("Mapudungun → Español");

                    editTextTranslate.setHint("Palabra en mapudungun");

                    //imageViewFavorite.setVisibility(View.VISIBLE);
                    imageViewRefresh.setVisibility(View.VISIBLE);
                    editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_MAPUDUNGUN);
                }
                editorSharedPref.commit();
                searchWord(editTextTranslate.getText());
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

        // Listener to share translated word
        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "¿Sabías que " + wordSearched + " significa " + wordResulted + " en mapudungun?");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        // Listener button favorite
        /*imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewFavorite.setImageResource(R.drawable.star);
            }
        });*/

        // Show the daily word
        refreshDailyWord(false);

        return v;
    }

    public void searchWord(Editable s){

        // Array of results
        JSONArray result = new JSONArray();

        // Go over list of words
        for (int i = 0; i < MainActivity.mapudungun.length(); i++) {

            try {

                // Create a JSONObject for each word
                JSONObject word = MainActivity.mapudungun.getJSONObject(i);

                if(sharedPref.contains(DEFAULT_TRANSLATE_ORIENTATION)){

                    if(sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH).equals(TRANSLATE_FROM_SPANISH)){

                        //
                        JSONArray words = word.getJSONArray(word.keys().next());

                        for(int y = 0; y < words.length(); y++){

                            if(words.getString(y).toLowerCase().trim().matches("(?i).*" + s.toString().toLowerCase().trim()+".*")){

                                result = words;

                                // Upper case the first letter
                                String wordCap = words.getString(y).substring(0, 1).toUpperCase() + words.getString(y).substring(1);

                                // Show the word searched
                                textViewWordResult.setText(wordCap);

                                // Show the result of words
                                textViewResult.setText(word.keys().next());

                                wordSearched = words.getString(y);
                                wordResulted = word.keys().next();

                                break;
                            }
                        }
                    }else {

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

                            wordSearched = result.getString(0);
                            wordResulted = word.keys().next();
                        }
                    }
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

    // Show the daily word
    public void refreshDailyWord(boolean changeEditText){
        try {

            // Random index number
            int indexRandom = randomInt(0, MainActivity.mapudungun.length()-1);

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
