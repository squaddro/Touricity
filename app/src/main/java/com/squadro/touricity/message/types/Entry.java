package com.squadro.touricity.message.types;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Entry {

    private String entry_id;
    private float expense;
    private float duration;
    private String comment;


}
