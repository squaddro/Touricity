package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IStop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Stop extends Entry implements IStop {

    private String location_id;
    private String stop_id;

    public Stop(String entry_id, float expense, float duration, String comment,
                String location_id, String stop_id) {
        super(entry_id, expense, duration, comment);
        this.location_id = location_id;
        this.stop_id = stop_id;
    }
}
