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

    private EditText editTextTranslate;
    private ProgressBar progressBar;
    private CardView cardViewResult;
    private TextView textViewWordResult;
    private TextView textViewResult;
    private ImageView imageViewRefresh;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        editTextTranslate   = (EditText)v.findViewById(R.id.editTextTranslate);
        progressBar         = (ProgressBar)v.findViewById(R.id.progressBar);
        cardViewResult      = (CardView)v.findViewById(R.id.cardViewResult);
        textViewWordResult  = (TextView)v.findViewById(R.id.textViewWordResult);
        textViewResult      = (TextView)v.findViewById(R.id.textViewResult);
        imageViewRefresh    = (ImageView)v.findViewById(R.id.imageViewRefresh);

        editTextTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editTextTranslate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {

                    JSONArray result = new JSONArray();

                    for (int i = 0; i < MainActivity.mapudungun.length(); i++) {

                        try {
                            JSONObject word = MainActivity.mapudungun.getJSONObject(i);

                            if (word.has(s.toString().toLowerCase().trim())) {

                                result = word.getJSONArray(s.toString().toLowerCase().trim());

                                String textResult = new String();
                                for (int w = 0; w < result.length(); w++) {
                                    textResult = textResult.concat(result.getString(w)).trim().concat("\n");
                                }

                                String wordCap = s.toString().trim().substring(0, 1).toUpperCase() + s.toString().trim().substring(1);
                                textViewWordResult.setText(wordCap);
                                textViewResult.setText(removeLastChar(textResult));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (result.length() > 0) {
                        cardViewResult.setVisibility(View.VISIBLE);
                    } else {
                        cardViewResult.setVisibility(View.INVISIBLE);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshDailyWord(true);
            }
        });

        refreshDailyWord(false);

        return v;
    }

    public void refreshDailyWord(boolean changeEditText){
        try {
            int indexRandom = randomInt(0, MainActivity.mapudungun.length());
            JSONObject word = MainActivity.mapudungun.getJSONObject(indexRandom);
            JSONArray words = word.getJSONArray(word.keys().next());

            String textResult = new String();
            for(int i = 0; i < words.length(); i++){
                textResult = textResult.concat(words.getString(i).concat("\n"));
            }

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

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

}
