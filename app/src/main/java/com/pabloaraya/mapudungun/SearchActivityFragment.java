package com.pabloaraya.mapudungun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    // View elements
    protected EditText editTextTranslate;
    protected ProgressBar progressBar;
    protected CardView cardViewResult;
    //protected LinearLayout layoutSuggestions;
    protected ListView listViewSuggestions;

    // TextView elements
    protected TextView textViewOrientation;
    protected TextView textViewWordResult;
    protected TextView textViewResult;
    protected TextView textViewFromWord;
    protected TextView textViewToWord;
    protected TextView textViewWhatYouSay;

    // ImageView elements
    protected ImageView imageViewRefresh;
    protected ImageView imageViewShare;
    protected ImageView imageViewFavorite;
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
    private final static String WORD_SEARCHED = "WORD_SEARCHED";
    private final static String WORD_RESULTED = "WORD_RESULTED";
    private final static String WORD_TO_SHARE = "WORD_TO_SHARE";
    private final static String SUGGESTIONS_WORDS = "SUGGESTIONS_WORDS";
    private final static String SPECIAL_CHART = "äëïöüáéíóú";

    // Tracker statics values
    private final static String TRACKER_BUTTON = "Button";
    private final static String TRACKER_CLICK = "Click";
    private final static String TRACKER_CHANGE_ORIENTATION = "ChangeOrientationButton";
    private final static String TRACKER_REFRESH_ACTION = "RefreshActionButton";
    private final static String TRACKER_SHARE_ACTION = "ShareActionButton";
    private final static String TRACKER_FAVORITE_ACTION = "FavoriteActionButton";

    // Analitycs instance
    private Tracker tracker;

    // Aux vars
    private String wordSearched;
    private String wordResulted;

    // Empty constructor
    public SearchActivityFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Action bar buttons
        //setHasOptionsMenu(true);

        // Tracker analytics instance
        tracker = ((Mapudungun)getActivity().getApplication()).getTracker(Mapudungun.TrackerName.APP_TRACKER);

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
        //layoutSuggestions   = (LinearLayout)v.findViewById(R.id.layoutSuggestions);
        listViewSuggestions = (ListView)v.findViewById(R.id.listViewSuggestionsWord);

        // References to text view
        textViewOrientation = (TextView)v.findViewById(R.id.textViewOrientation);
        textViewWordResult  = (TextView)v.findViewById(R.id.textViewWordResult);
        textViewResult      = (TextView)v.findViewById(R.id.textViewResult);
        textViewFromWord    = (TextView)v.findViewById(R.id.textViewFromWord);
        textViewToWord      = (TextView)v.findViewById(R.id.textViewToWord);
        textViewWhatYouSay  = (TextView)v.findViewById(R.id.textViewWhatYouSay);

        // References to image view
        imageViewRefresh    = (ImageView)v.findViewById(R.id.imageViewRefresh);
        imageViewShare      = (ImageView)v.findViewById(R.id.imageViewShare);
        imageViewFavorite   = (ImageView)v.findViewById(R.id.imageViewFavorite);
        imageViewChange     = (ImageView)v.findViewById(R.id.imageViewChange);

        // Set typefaces
        textViewOrientation.setTypeface(robotoRegular);
        textViewWordResult.setTypeface(robotoLight);
        textViewResult.setTypeface(robotoLight);
        textViewWhatYouSay.setTypeface(robotoLight);
        editTextTranslate.setTypeface(robotoLight);

        // Remove suggestions
        editTextTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if(sharedPref.contains(DEFAULT_TRANSLATE_ORIENTATION)){
            if(sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH).equals(TRANSLATE_FROM_SPANISH)){
                textViewFromWord.setText(R.string.spanish);
                textViewToWord.setText(R.string.mapudungun);
                textViewOrientation.setText(R.string.spanish_to_mapudungun);

                editTextTranslate.setHint(R.string.word_in_spanish);
                imageViewFavorite.setVisibility(View.INVISIBLE);
                imageViewRefresh.setVisibility(View.INVISIBLE);
            }else{
                textViewFromWord.setText(R.string.mapudungun);
                textViewToWord.setText(R.string.spanish);
                textViewOrientation.setText(R.string.mapudungun_to_spanish);

                editTextTranslate.setHint(R.string.word_in_mapudungun);
                imageViewFavorite.setVisibility(View.VISIBLE);
                imageViewRefresh.setVisibility(View.VISIBLE);
            }
        }else{
            textViewFromWord.setText(R.string.spanish);
            textViewToWord.setText(R.string.mapudungun);
            textViewOrientation.setText(R.string.spanish_to_mapudungun);

            editTextTranslate.setHint(R.string.word_in_spanish);
            imageViewFavorite.setVisibility(View.INVISIBLE);
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


            }

            @Override
            public void afterTextChanged(final Editable s) {

                // Verify the length
                if (s.toString().trim().length() > 0) {

                    new LoadTranslateResult().execute();

                }else{
                    // Hide result card
                    cardViewResult.setVisibility(View.INVISIBLE);

                    // Hice suggestions card
                    //layoutSuggestions.setVisibility(View.INVISIBLE);
                }

            }
        });

        // Listener to change translate
        imageViewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Rotate animation
                Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation_180);
                rotation.setRepeatCount(Animation.ABSOLUTE);
                v.startAnimation(rotation);

                // Alpha animation
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
                alphaAnimation.setDuration(500);
                alphaAnimation.setFillAfter(true);
                alphaAnimation.setRepeatMode(Animation.REVERSE);
                textViewFromWord.startAnimation(alphaAnimation);
                textViewToWord.startAnimation(alphaAnimation);

                if(sharedPref.contains(DEFAULT_TRANSLATE_ORIENTATION)){
                    if(sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH).equals(TRANSLATE_FROM_SPANISH)){
                        textViewFromWord.setText(R.string.mapudungun);
                        textViewToWord.setText(R.string.spanish);
                        textViewOrientation.setText(R.string.mapudungun_to_spanish);

                        editTextTranslate.setHint(R.string.word_in_mapudungun);

                        imageViewFavorite.setVisibility(View.VISIBLE);
                        imageViewRefresh.setVisibility(View.VISIBLE);
                        editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_MAPUDUNGUN);
                    }else{
                        textViewFromWord.setText(R.string.spanish);
                        textViewToWord.setText(R.string.mapudungun);
                        textViewOrientation.setText(R.string.spanish_to_mapudungun);

                        editTextTranslate.setHint(R.string.word_in_spanish);

                        imageViewFavorite.setVisibility(View.INVISIBLE);
                        imageViewRefresh.setVisibility(View.INVISIBLE);
                        editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH);
                    }
                }else{
                    textViewFromWord.setText(R.string.mapudungun);
                    textViewToWord.setText(R.string.spanish);
                    textViewOrientation.setText(R.string.mapudungun_to_spanish);

                    editTextTranslate.setHint(R.string.word_in_mapudungun);

                    imageViewFavorite.setVisibility(View.VISIBLE);
                    imageViewRefresh.setVisibility(View.VISIBLE);
                    editorSharedPref.putString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_MAPUDUNGUN);
                }

                // Save changed
                editorSharedPref.commit();


                //layoutSuggestions.setVisibility(View.INVISIBLE);

                // Verify...
                if(editTextTranslate.getText().length() > 0) {
                    new LoadTranslateResult().execute();
                }else{
                    cardViewResult.setVisibility(View.INVISIBLE);
                }

                // Event tracker
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(TRACKER_BUTTON)
                        .setAction(TRACKER_CLICK)
                        .setLabel(TRACKER_CHANGE_ORIENTATION)
                        .build());
            }
        });

        // Listener for refresh the result card
        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Rotate animation
                Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation_360);
                rotation.setRepeatCount(Animation.ABSOLUTE);
                v.startAnimation(rotation);

                // Refresh the daily word
                refreshDailyWord(true);

                // Event tracker
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(TRACKER_BUTTON)
                        .setAction(TRACKER_CLICK)
                        .setLabel(TRACKER_REFRESH_ACTION)
                        .build());
            }
        });

        // Listener to share translated word
        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                // Translate from Spanish
                if (sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH)
                        .equals(TRANSLATE_FROM_SPANISH)) {

                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "¿Sabías que " + wordResulted + " significa " + wordSearched + " en mapudungun? https://goo.gl/p7w3JQ");

                }else{

                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "¿Sabías que " + wordSearched + " significa " + wordResulted + " en mapudungun? https://goo.gl/p7w3JQ");

                }
                sendIntent.setType("text/plain");

                // Event tracker
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(TRACKER_BUTTON)
                        .setAction(TRACKER_CLICK)
                        .setLabel(TRACKER_SHARE_ACTION)
                        .build());

                startActivity(sendIntent);
            }
        });

        // Listener button favorite
        imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewFavorite.setImageResource(R.drawable.star);
            }
        });

        // Show the daily word
        refreshDailyWord(false);

        // Event tracker
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(TRACKER_BUTTON)
                .setAction(TRACKER_CLICK)
                .setLabel(TRACKER_FAVORITE_ACTION)
                .build());

        return v;
    }

    // Show the daily word
    public void refreshDailyWord(boolean changeEditText){
        try {

            // Random index number
            int indexRandom = Util.randomInt(0, MainActivity.mapudungun.length() - 1);

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
            textViewResult.setText(Util.removeLastChar(textResult));

            // Save result
            wordSearched = word.keys().next();
            wordResulted = words.getString(0);

            if(changeEditText){
                editTextTranslate.setText(wordCap);
                editTextTranslate.setSelection(editTextTranslate.getText().length());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadTranslateResult extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected void onPreExecute(){

            // Show loading progress
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected JSONObject doInBackground(JSONObject... params) {

            // Array of results
            JSONObject result = new JSONObject();

            // Suggestions words
            JSONArray suggestionsWord = new JSONArray();

            String wordSearched = editTextTranslate.getText().toString().toLowerCase().trim();

            try {

                // Go over list of words
                for (int i = 0; i < MainActivity.mapudungun.length(); i++) {

                    // Create a JSONObject for each word
                    JSONObject word = MainActivity.mapudungun.getJSONObject(i);

                    if (sharedPref.contains(DEFAULT_TRANSLATE_ORIENTATION)) {

                        // Translate from spanish
                        if (sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH)
                                .equals(TRANSLATE_FROM_SPANISH)) {

                            //
                            JSONArray words = word.getJSONArray(word.keys().next());

                            for (int y = 0; y < words.length(); y++) {

                                if (words.getString(y).matches("(?i).*" + wordSearched + ".*")) {

                                    // Add word to suggestions word
                                    suggestionsWord.put(word);
                                }
                            }

                        // Translate from mapudungun
                        } else {

                            if (word.keys().next().matches("(?i).*" + wordSearched + ".*")) {

                                // Add suggestion words
                                suggestionsWord.put(word);
                            }
                        }
                    }
                }

                if(suggestionsWord.length() > 0){

                    for(int i = 0; i < suggestionsWord.length(); i++){

                        String keySearchedWord = suggestionsWord.getJSONObject(i).keys().next();

                        // Array of words
                        JSONArray words = suggestionsWord.getJSONObject(i).getJSONArray(keySearchedWord);

                        // Translate from Spanish
                        if (sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH)
                                .equals(TRANSLATE_FROM_SPANISH)) {

                            for (int w = 0; w < words.length(); w++) {

                                if(words.getString(w).equalsIgnoreCase(wordSearched)){

                                    // Word searched
                                    result.put(WORD_SEARCHED, Util.upperCaseFirstLetter(words.getString(w)));

                                    // Word resulted
                                    result.put(WORD_RESULTED, keySearchedWord);

                                    // Word to share
                                    result.put(WORD_TO_SHARE, keySearchedWord);

                                    // Remove element from suggestion word
                                    new Util().remove(i, suggestionsWord);

                                    break;
                                }
                            }

                        // Translate from Mapudungun
                        } else {

                            if(keySearchedWord.equalsIgnoreCase(wordSearched)){

                                // Word searched
                                result.put(WORD_SEARCHED, Util.upperCaseFirstLetter(keySearchedWord));

                                // Final string to show in the result
                                String textResult = new String();

                                // Concat all words
                                for (int w = 0; w < words.length(); w++) {
                                    textResult = textResult.concat(words.getString(w)).trim().concat("\n");
                                }

                                // Word resulted
                                result.put(WORD_RESULTED, Util.removeLastChar(textResult));

                                // Word to share
                                result.put(WORD_TO_SHARE, words.getString(0));

                                // Remove element from suggestion word
                                new Util().remove(i, suggestionsWord);

                                break;
                            }
                        }
                    }

                    if(!result.has(WORD_SEARCHED)){

                        // Translate from Spanish
                        if (sharedPref.getString(DEFAULT_TRANSLATE_ORIENTATION, TRANSLATE_FROM_SPANISH)
                                .equals(TRANSLATE_FROM_SPANISH)) {

                            result.put(WORD_SEARCHED, suggestionsWord.getJSONObject(0).getJSONArray(suggestionsWord.getJSONObject(0).keys().next()).getString(0));

                            result.put(WORD_RESULTED, suggestionsWord.getJSONObject(0).keys().next());

                            result.put(WORD_TO_SHARE, suggestionsWord.getJSONObject(0).keys().next());

                        }else{

                            result.put(WORD_SEARCHED, suggestionsWord.getJSONObject(0).keys().next());

                            // Final string to show in the result
                            String textResult = new String();

                            // Concat all words
                            for (int w = 0; w < suggestionsWord.getJSONObject(0).getJSONArray(result.getString(WORD_SEARCHED)).length(); w++) {
                                textResult = textResult.concat(suggestionsWord.getJSONObject(0).getJSONArray(result.getString(WORD_SEARCHED)).getString(w)).trim().concat("\n");
                            }

                            result.put(WORD_RESULTED, textResult);

                            result.put(WORD_TO_SHARE, suggestionsWord.getJSONObject(0).getJSONArray(result.getString(WORD_SEARCHED)).getString(0));
                        }
                    }

                    new Util().remove(0, suggestionsWord);

                    // Add suggestions words to result
                    result.put(SUGGESTIONS_WORDS, suggestionsWord);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
        @Override
        protected void onPostExecute(JSONObject result) {

            // Verify if exists result
            if (result.has(WORD_SEARCHED) && result.has(WORD_RESULTED)) {

                // Event tracker
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Translate")
                        .setLabel("TranslateEvent")
                        .build());

                try {

                    // Update UI
                    textViewWordResult.setText(result.getString(WORD_SEARCHED));
                    textViewResult.setText(result.getString(WORD_RESULTED));

                    // Save the current result word;
                    wordSearched = result.getString(WORD_SEARCHED);
                    wordResulted = result.getString(WORD_TO_SHARE);

                    if(result.has(SUGGESTIONS_WORDS) && result.getJSONArray(SUGGESTIONS_WORDS).length() > 0) {

                        /*ArrayList<String> words = new ArrayList<>();

                        for(int i = 0; i < result.getJSONArray(SUGGESTIONS_WORDS).length(); i++){
                            words.add(result.getJSONArray(SUGGESTIONS_WORDS).getString(i));
                        }

                        ArrayAdapter<String> wordAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, words);
                        listViewSuggestions.setAdapter(wordAdapter);

                        if(result.getJSONArray(SUGGESTIONS_WORDS).length() == 1){

                            Log.e("WORD", result.getJSONArray(SUGGESTIONS_WORDS).getString(0));
                        }else {

                            Log.e("COUNT", String.valueOf(result.getJSONArray(SUGGESTIONS_WORDS).length()));
                        }*/

                        //layoutSuggestions.setVisibility(View.VISIBLE);
                    }else{
                        //layoutSuggestions.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Show result card
                cardViewResult.setVisibility(View.VISIBLE);

            // No results
            } else {

                // Hide result card
                cardViewResult.setVisibility(View.INVISIBLE);

                // Hide suggestions card
                //layoutSuggestions.setVisibility(View.INVISIBLE);
            }

            // Hide progressbar
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorites){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}