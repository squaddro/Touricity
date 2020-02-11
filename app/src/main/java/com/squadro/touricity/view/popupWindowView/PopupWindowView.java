package com.squadro.touricity.view.popupWindowView;

import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class PopupWindowView {

    private final int HEIGHT = 150;
    private final int WIDTH = 400;

    private LinearLayout linearLayout;

    private int numberOfButtons;
    private List<String> nameOfButtons;

    LinearLayout.LayoutParams layoutParams;

    private int totalHeight;

    public PopupWindowView(int numberOfButtons, List<String> nameOfButtons, LinearLayout linearLayout) {
        this.numberOfButtons = numberOfButtons;
        this.nameOfButtons = nameOfButtons;
        this.linearLayout = linearLayout;
        totalHeight = HEIGHT * numberOfButtons;
        layoutParams = new LinearLayout.LayoutParams(WIDTH,HEIGHT);
        layoutParams.setMargins(0,-15,0,-15);
        addButtonsToView();
    }

    private void addButtonsToView() {
        for(int i = 0;i<numberOfButtons;i++){
            Button button = new Button(linearLayout.getContext());
            button.setText(nameOfButtons.get(i));
            button.setLayoutParams(layoutParams);
            linearLayout.addView(button);
        }
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }
}
