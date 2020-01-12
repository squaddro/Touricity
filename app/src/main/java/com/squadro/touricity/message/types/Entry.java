package com.squadro.touricity.message.types;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder(toBuilder = true)
public class Entry {

    private String entry_id;
    private double expense;
    private double duration;
    private String comment;

}
