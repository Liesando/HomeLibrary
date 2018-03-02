package com.azzgil.homelibrary.utils;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUIUtils {

    public static final double STD_BUTTON_ICON_HEIGTH = 24.0;
    public static final double STD_BUTTON_ICON_WIDTH = 24.0;

    private GUIUtils() {
    }

    public static void loadButtonIcon(Button btn, String iconPath) {
        loadButtonIcon(btn, iconPath, STD_BUTTON_ICON_HEIGTH, STD_BUTTON_ICON_WIDTH);
    }

    /**
     * Загружает и отображает иконку для указанной кнопки с указанными
     * размерами (иконки)
     */
    public static void loadButtonIcon(Button btn, String iconPath, double heigth, double width) {
        ImageView iv = new ImageView(new Image(GUIUtils.class.getClassLoader()
                .getResourceAsStream(iconPath)));
        iv.setFitHeight(heigth);
        iv.setFitWidth(width);
        btn.setGraphic(iv);
    }
}
