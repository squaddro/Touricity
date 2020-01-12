package com.squadro.touricity.message.types;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder(toBuilder = true)
public class Entry {

    private String entry_id;
    private float expense;
    private float duration;
    private String comment;

}
