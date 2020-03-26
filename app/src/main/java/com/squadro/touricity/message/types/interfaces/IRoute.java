package com.squadro.touricity.message.types.interfaces;

import com.squadro.touricity.message.types.AbstractEntry;

public interface IRoute  extends IDataType {

    void addEntry(AbstractEntry abstractEntry);
    void addEntry(AbstractEntry abstractEntry, int index);
    void deleteEntry(AbstractEntry abstractEntry);
    void deleteEntry(int index);
    boolean deleteEntry(String entry_id);
    boolean changeEntryPosition(AbstractEntry abstractEntry,int oldPos, int newPos);
}
