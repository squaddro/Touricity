package com.squadro.touricity.view.filter;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.squadro.touricity.R;

import lombok.Getter;

public class TransportationCheckBox {

    private CheckBox walkCheckBox = null;
    private CheckBox carCheckBox = null;
    private CheckBox busCheckBox = null;
    private CheckBox trainCheckBox = null;

    @Getter
    private int transportation = 0;

    public TransportationCheckBox(Activity activity){
        initializeWalkCheckBox(activity);
        initializeCarCheckBox(activity);
        initializeBusCheckBox(activity);
        initializeTrainCheckBox(activity);
    }

    private void initializeWalkCheckBox(Activity activity) {
        walkCheckBox = activity.findViewById(R.id.walkCheckBox);
        walkCheckBox.setOnCheckedChangeListener(getWalkOnCheckedChangeListener());
    }

    private void initializeCarCheckBox(Activity activity) {
        carCheckBox = activity.findViewById(R.id.carCheckBox);
        carCheckBox.setOnCheckedChangeListener(getCarOnCheckedChangeListener());
    }

    private void initializeBusCheckBox(Activity activity){
        busCheckBox = activity.findViewById(R.id.busCheckBox);
        busCheckBox.setOnCheckedChangeListener(getBusOnCheckedChangeListener());
    }

    private void initializeTrainCheckBox(Activity activity){
        trainCheckBox = activity.findViewById(R.id.trainCheckBox);
        busCheckBox.setOnCheckedChangeListener(getTrainOnCheckedChangeListener());
    }

    private CompoundButton.OnCheckedChangeListener getWalkOnCheckedChangeListener(){
        return (compoundButton, isChecked) -> {
            if(isChecked)
                transportation += 1;
            else
                transportation -= 1;
        };
    }

    private CompoundButton.OnCheckedChangeListener getCarOnCheckedChangeListener(){
        return (compoundButton, isChecked) -> {
            if(isChecked)
                transportation += 2;
            else
                transportation -= 2;
        };
    }

    private CompoundButton.OnCheckedChangeListener getBusOnCheckedChangeListener(){
        return (compoundButton, isChecked) -> {
            if(isChecked)
                transportation += 4;
            else
                transportation -= 4;
        };
    }

    private CompoundButton.OnCheckedChangeListener getTrainOnCheckedChangeListener(){
        return (compoundButton, isChecked) -> {
            if(isChecked)
                transportation += 8;
            else
                transportation -= 8;
        };
    }
}
