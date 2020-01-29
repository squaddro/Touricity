package com.squadro.touricity.view.search;

import android.app.Activity;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.squadro.touricity.R;

import lombok.Getter;

public class SearchBar {

    @Getter
    private String inputCity;
    private AutoCompleteTextView autoCompleteTextView = null;
    private ArrayAdapter<String> adapter = null;

    private static final String[] CITIES = {
      "Ankara", "Istanbul", "Izmir"
    };

    public SearchBar(Activity activity, Context context){
        initializeAutoCompleteTextView(activity, context);

    }

    private void initializeAutoCompleteTextView(Activity activity, Context context) {

        autoCompleteTextView = activity.findViewById(R.id.SearchBar);

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, CITIES);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(getOnItemClickListener());
    }

    private AdapterView.OnItemClickListener getOnItemClickListener(){
        return (adapterView, view, i, l) -> {
            inputCity = adapterView.getItemAtPosition(i).toString();
        };
    }

}
