package com.squadro.touricity.view.map.DirectionsAPI;

import com.squadro.touricity.message.types.Stop;

import java.util.Comparator;

public class StopComparator implements Comparator<Stop> {

    @Override
    public int compare(Stop stop, Stop t1) {

        return stop.getIndex() - t1.getIndex();
    }
}
