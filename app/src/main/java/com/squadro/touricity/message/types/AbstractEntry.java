package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IEntry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractEntry implements IEntry {

    private String entry_id;
    private int expense;
    private int duration;
    private String comment;
    private int index;

    public AbstractEntry(String entry_id, int expense, int duration, String comment) {
        this.entry_id = entry_id;
        this.expense = expense;
        this.duration = duration;
        this.comment = comment;
    }

    public AbstractEntry(int expense, int duration, String comment) {
        this.entry_id = null;
        this.expense = expense;
        this.duration = duration;
        this.comment = comment;
    }

    public AbstractEntry(String entry_id, int expense, int duration, String comment, int index){
        this.entry_id = entry_id;
        this.expense = expense;
        this.duration = duration;
        this.comment = comment;
        this.index = index;
    }

    public abstract String getType();
}
