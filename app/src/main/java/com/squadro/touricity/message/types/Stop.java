package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IStop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stop extends AbstractEntry implements IStop {

    private String location_id;
    private String stop_id;
    private String type = "stop";

    public Stop(String entry_id, int expense, int duration, String comment,
                String location_id, String stop_id) {
        super(entry_id, expense, duration, comment);
        this.location_id = location_id;
        this.stop_id = stop_id;
    }

    @Override
    public String getType() {
        return "stop";
    }
}
