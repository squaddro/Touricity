package com.squadro.touricity.message.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Vertex {

    private float lat;
    private float lon;
}
