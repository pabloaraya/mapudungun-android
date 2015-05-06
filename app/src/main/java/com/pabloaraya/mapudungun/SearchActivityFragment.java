package com.pabloaraya.mapudungun;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private EditText editTextTranslate;
    private ProgressBar progressBar;
    private CardView cardViewResult;
    private TextView textViewWordResult;
    private TextView textViewResult;

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

        editTextTranslate.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editTextTranslate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {

                    progressBar.setVisibility(View.VISIBLE);

                    JSONArray result = new JSONArray();

                    for (int i = 0; i < MainActivity.mapudungun.length(); i++) {

                        try {
                            JSONObject word = MainActivity.mapudungun.getJSONObject(i);

                            if (word.has(s.toString().toLowerCase().trim())) {
                                textViewWordResult.setText(s.toString().trim());
                                result = word.getJSONArray(s.toString().toLowerCase().trim());

                                String textResult = new String();
                                for(int w = 0; w < result.length(); w++){
                                    textResult = textResult.concat(result.getString(w)).trim().concat("\n");
                                }
                                textViewResult.setText(textResult);
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

        return v;
    }
}
