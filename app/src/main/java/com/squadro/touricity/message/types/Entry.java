package com.squadro.touricity.message.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Entry {

    private String entry_id;
    private float expense;
    private float duration;
    private String comment;

    public Entry(String entry_id, float expense, float duration, String comment) {
        this.entry_id = entry_id;
        this.expense = expense;
        this.duration = duration;
        this.comment = comment;
    }
}
