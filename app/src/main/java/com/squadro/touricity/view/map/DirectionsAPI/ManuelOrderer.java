package com.squadro.touricity.view.map.DirectionsAPI;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.squadro.touricity.maths.MapMaths;
import com.squadro.touricity.maths.Permutation;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.message.types.Stop;
import com.squadro.touricity.message.types.interfaces.IEntry;

import java.util.ArrayList;
import java.util.List;

public class ManuelOrderer {

    public static List<Stop> minStopList = new ArrayList<>();
    public static List<Integer> minOrder = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Integer> getOptimizedOrder(Route route){
        List<Stop> stopList = new ArrayList<>();

        int stopSize = 0;

        for (IEntry e:route.getAbstractEntryList()) {
            if(e instanceof Stop){
                stopSize++;
                stopList.add((Stop) e);
            }
        }
        Permutation p = new Permutation();
        int[] order = p.getZeroToN(stopSize);
        List<List<Integer>> allOrders = p.permute(order);

        for (List<Integer> o:allOrders) {
            List<Stop> temp = stopListOrder(stopList, o);
            if((MapMaths.distanceOfRoute(temp) < MapMaths.distanceOfRoute(minStopList)) || minStopList.size()==0){
                minStopList.clear();
                for (Stop s: temp) {
                    minStopList.add(s);
                }
                minOrder.clear();
                for (Integer i:o) {
                    minOrder.add(i);
                }
            }
        }
        return minOrder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Stop> stopListOrder(List<Stop> stopList, List<Integer> order) {
        List<Stop> temp = new ArrayList<>(stopList.size());

        for(int i = 0; i<stopList.size(); i++){
            stopList.get(i).setIndex(order.get(i));
        }

        StopComparator comp = new StopComparator();
        stopList.sort(comp);
        return stopList;
    }
}
