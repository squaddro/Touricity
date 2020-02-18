package com.squadro.touricity.view.popupWindowView;

import java.util.List;

import lombok.Getter;

public class PopupWindowParameters {

    @Getter
    private int numberOfButtons;
    @Getter
    private List<String> buttonNames;

    public PopupWindowParameters(int numberOfButtons, List<String> buttonNames) {
        this.numberOfButtons = numberOfButtons;
        this.buttonNames = buttonNames;
    }
}
