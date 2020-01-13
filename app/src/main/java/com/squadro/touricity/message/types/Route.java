package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IRoute;

import java.util.ArrayList;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder(toBuilder = true)
public class Route implements IRoute {

    private String route_id;
    private ArrayList<AbstractEntry> abstractEntryList;

    public Route() {
        route_id = null;
        this.abstractEntryList = new ArrayList<>();
    }

    public Route(ArrayList<AbstractEntry> abstractEntryList) {
        route_id = null;
        this.abstractEntryList = abstractEntryList;
    }

    public void addEntry(AbstractEntry abstractEntry){
        this.abstractEntryList.add(abstractEntry);
    }

    public void addEntry(AbstractEntry abstractEntry, int index){
        if(index<0)
            abstractEntryList.add(0, abstractEntry);

        else if(index >= abstractEntryList.size()){
            abstractEntryList.add(abstractEntry);
        }

        else{
            this.abstractEntryList.add(index, abstractEntry);
        }
    }

    public void deleteEntry(AbstractEntry abstractEntry){
        this.abstractEntryList.remove(abstractEntry);
    }

    public void deleteEntry(int index){
        this.abstractEntryList.remove(index);
    }

    /**
     * not efficient
     *
     * @param entry_id
     * @return true when success, false when entry_id is not found.
     */
    public boolean deleteEntry(String entry_id){

        Iterator<AbstractEntry> iter = abstractEntryList.iterator();

        while(iter.hasNext()){
            AbstractEntry temp = iter.next();
            if(temp.getEntry_id().equals(entry_id)){
                abstractEntryList.remove(temp);
                return true;
            }
        }
        return false;
    }

    /**
     * @param abstractEntry is the abstractEntry that will be replaced.
     * @param newPos is the new position of given abstractEntry.
     * @return true when success, false when abstractEntry is not found in list (list is unchanged)
     */

    public boolean changeEntryPosition(AbstractEntry abstractEntry, int newPos){

        if(!abstractEntryList.contains(abstractEntry)){
            return false;
        }
        else{
            if(newPos < 0){
                abstractEntryList.remove(abstractEntry);
                abstractEntryList.add(0, abstractEntry);
            }
            else if(newPos >= abstractEntryList.size()){
                abstractEntryList.remove(abstractEntry);
                abstractEntryList.add(abstractEntry);
            }
            else{
                abstractEntryList.remove(abstractEntry);
                abstractEntryList.add(newPos, abstractEntry);
            }
            return true;
        }
    }


}
