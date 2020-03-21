package com.squadro.touricity.message.types;

import com.squadro.touricity.message.types.interfaces.IStop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stop extends AbstractEntry implements IStop {

    private String stop_id;
    private String type = "stop";

    private Location location;

    public Stop(String entry_id, int expense, int duration, String comment,
                Location location, String stop_id){

        super(entry_id, expense, duration, comment);
        this.stop_id = stop_id;
        this.location = location;
    }

    @Override
    public String getType() {
        return "stop";
    }
}
