package com.squadro.touricity.requests;

import com.squadro.touricity.message.types.Location;

public interface ILocationRequest {
    void onResponseLocationInfo(Location location);
}
