package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IRoute;

import java.util.ArrayList;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
public class Route implements IRoute {

    private String route_id;
    private ArrayList<Entry> entryList;

    public Route() {
        //TODO : uuid will be obtained
        this.entryList = new ArrayList<>();
    }

    public Route(String route_id) {
        this.route_id = route_id;
        this.entryList = new ArrayList<>();
    }

    public Route(ArrayList<Entry> entryList) {
        //TODO : uuid will be obtained
        this.entryList = entryList;
    }

    public void addEntry(Entry entry){
        this.entryList.add(entry);
    }

    public void addEntry(Entry entry, int index){
        if(index<0)
            entryList.add(0, entry);

        else if(index >= entryList.size()){
            entryList.add(entry);
        }

        else{
            this.entryList.add(index, entry);
        }
    }

    public void deleteEntry(Entry entry){
        this.entryList.remove(entry);
    }

    public void deleteEntry(int index){
        this.entryList.remove(index);
    }

    /**
     * not efficient
     *
     * @param entry_id
     * @return true when success, false when entry_id is not found.
     */
    public boolean deleteEntry(String entry_id){

        Iterator<Entry> iter = entryList.iterator();

        while(iter.hasNext()){
            Entry temp = iter.next();
            if(temp.getEntry_id().equals(entry_id)){
                entryList.remove(temp);
                return true;
            }
        }
        return false;
    }

    /**
     * @param entry is the entry that will be replaced.
     * @param newPos is the new position of given entry.
     * @return true when success, false when entry is not found in list (list is unchanged)
     */

    public boolean changeEntryPosition(Entry entry, int newPos){

        if(!entryList.contains(entry)){
            return false;
        }
        else{
            if(newPos < 0){
                entryList.remove(entry);
                entryList.add(0, entry);
            }
            else if(newPos >= entryList.size()){
                entryList.remove(entry);
                entryList.add(entry);
            }
            else{
                entryList.remove(entry);
                entryList.add(newPos, entry);
            }
            return true;
        }
    }


}
