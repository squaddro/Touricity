package com.squadro.touricity.message.types;

import java.util.ArrayList;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder(toBuilder = true)
public class Route {

    private String route_id;
    private ArrayList<Entry> entryList;

    public void addEntry(Entry entry){
        this.entryList.add(entry);
    }

    public void addEntry(Entry entry, int index){
        this.entryList.add(index, entry);
    }

    public void deleteEntry(Entry entry){
        this.entryList.remove(entry);
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
            else{ //TODO: might not work!
                entryList.remove(entry);
                entryList.add(newPos, entry);
            }
            return true;
        }
    }


}
