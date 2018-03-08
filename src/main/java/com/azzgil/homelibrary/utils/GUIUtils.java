package com.azzgil.homelibrary.utils;

import com.azzgil.homelibrary.model.Genre;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.Duration;


/**
 * GUIUtils
 *
 * Класс-утилита, отвечает за подгрузку различных ресурсов
 * для GUI-элементов и их настройку.
 *
 * @author Sergey Medelyan
 * @version 1.3 8 March 2018
 */
public class GUIUtils {

    // стандартные размеры иконок на кнопках
    public static final double STD_BUTTON_ICON_HEIGTH = 24.0;
    public static final double STD_BUTTON_ICON_WIDTH = 24.0;

    public static final double DEFAULT_TOOLTIP_DELAY = 30.0;

    // пути до определённых иконок
    public static final String ADD_ICON = "icons/add_book.png";
    public static final String EYE_FIND_ICON = "icons/watch-tool.png";
    public static final String DELETE_ICON = "icons/delete.png";
    public static final String EDIT_ICON = "icons/edit.png";

    // используется для замены содержимого элементов GUI, если они
    // пустые
    public static final String STD_NOT_SPECIFIED_REPLACER = "<не указано>";
    public static final String NO_COMMENT_REPLACER = "<нет комментария>";

    // фабрика, которая в качестве имени ячейки
    // вместо Genre.toString() будет выводить полное имя
    // жанра (Genre.getFullName())
    public static Callback<ListView<Genre>, ListCell<Genre>>
            STD_GENRE_CELL_FACTORY = new Callback<>() {
        @Override
        public ListCell<Genre> call(ListView<Genre> param) {
            return new ListCell<>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        setText(item.getFullName());
                    } else {
                        setText("без категории");
                    }
                }
            };
        }
    };

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
     * Добавляет кнопке всплывающую подсказку со временем
     * задержки до всплытия {@value DEFAULT_TOOLTIP_DELAY} мс
     *
     * @param btn         Кнопка
     * @param tooltipText Текст подсказки
     */
    public static void addTooltipToButton(Button btn, String tooltipText) {
        addTooltipToButton(btn, tooltipText, DEFAULT_TOOLTIP_DELAY);
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
