package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IEntry;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Entry implements IEntry {

    private String entry_id;
    private double expense;
    private double duration;
    private String comment;

    public Entry(String entry_id, double expense, double duration, String comment) {
        this.entry_id = entry_id;
        this.expense = expense;
        this.duration = duration;
        this.comment = comment;
    }
}
