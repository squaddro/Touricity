package com.squadro.touricity.message.types.interfaces;

import com.squadro.touricity.message.types.Entry;

public interface IRoute {

    void addEntry(Entry entry);
    void addEntry(Entry entry, int index);
    void deleteEntry(Entry entry);
    void deleteEntry(int index);
    boolean deleteEntry(String entry_id);
    boolean changeEntryPosition(Entry entry, int newPos);
}
