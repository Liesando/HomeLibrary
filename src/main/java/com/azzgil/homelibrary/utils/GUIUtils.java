package com.azzgil.homelibrary.utils;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * GUIUtils
 *
 * Класс-утилита, отвечает за подгрузку различных ресурсов
 * для GUI-элементов и их настройку.
 *
 * @author Sergey Medelyan
 * @version 1.1 4 March 2018
 */
public class GUIUtils {

    // стандартные размеры иконок на кнопках
    public static final double STD_BUTTON_ICON_HEIGTH = 24.0;
    public static final double STD_BUTTON_ICON_WIDTH = 24.0;

    // пути до определённых иконок
    public static final String ADD_ICON = "icons/add_book.png";
    public static final String EYE_ICON = "icons/eye.png";
    public static final String EYE_FIND_ICON = "icons/watch-tool.png";

    private GUIUtils() {
    }

    /**
     * Загружает указанную иконку и отображает её внутри кнопки btn
     */
    public static void loadButtonIcon(Button btn, String iconPath) {
        loadButtonIcon(btn, iconPath, STD_BUTTON_ICON_HEIGTH, STD_BUTTON_ICON_WIDTH);
    }

    /**
     * Загружает и отображает иконку внутри указанной кнопки с указанными
     * размерами (иконки)
     */
    public static void loadButtonIcon(Button btn, String iconPath, double heigth, double width) {
        ImageView iv = new ImageView(new Image(GUIUtils.class.getClassLoader()
                .getResourceAsStream(iconPath)));
        iv.setFitHeight(heigth);
        iv.setFitWidth(width);
        btn.setGraphic(iv);
    }

    /**
     * Добавляет кнопке всплывающую подсказку.
     *
     * @param btn         Кнопка
     * @param tooltipText Текст подсказки
     * @param showDelayMs Задержка до показа подсказки в миллисекундах
     */
    public static void addTooltipToButton(Button btn, String tooltipText, double showDelayMs) {
        Tooltip t = new Tooltip(tooltipText);
        t.setShowDelay(Duration.millis(showDelayMs));
        btn.setTooltip(t);
    }
}
