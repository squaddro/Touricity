package com.squadro.touricity.view.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter {

    private String city_name = "";
    private double score = 0.0;
    private int expense = 0;
    private int duration = 0;
    private int path_type = 0;

}
