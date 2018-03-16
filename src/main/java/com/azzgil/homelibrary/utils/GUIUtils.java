package com.azzgil.homelibrary.utils;

import com.azzgil.homelibrary.model.Genre;
import javafx.scene.control.*;
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
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.3 11 March 2018
 */
public class GUIUtils {

    // стандартные размеры
    public static final double STD_BUTTON_ICON_HEIGHT = 24.0;
    public static final double STD_BUTTON_ICON_WIDTH = 24.0;
    public static final double DEFAULT_TOOLTIP_DELAY = 30.0;

    // пути до определённых иконок
    public static final String ADD_ICON = "icons/add_book.png";
    public static final String DETAILS_ICON = "icons/watch-tool.png";
    public static final String DELETE_ICON = "icons/delete.png";
    public static final String EDIT_ICON = "icons/edit.png";
    public static final String REFRESH_ICON = "icons/crutch.png";
    public static final String HISTORY_ICON = "icons/history.png";
    public static final String GIVE_ICON = "icons/give.png";
    public static final String RETURN_ICON = "icons/return.png";
    public static final String FILTER_ICON = "icons/filter.png";

    // используется для замены содержимого элементов GUI, если они
    // пустые
    public static final String STD_NOT_SPECIFIED_REPLACER = "(не указано)";
    public static final String NO_COMMENT_REPLACER = "(нет комментария)";
    public static final String DO_NOT_HAVE_ONE = "(не имеется)";

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
    public static void loadButtonIcon(Labeled btn, String iconPath) {
        loadButtonIcon(btn, iconPath, STD_BUTTON_ICON_HEIGHT, STD_BUTTON_ICON_WIDTH);
    }

    /**
     * Загружает и отображает иконку внутри указанной кнопки с указанными
     * размерами (иконки)
     */
    public static void loadButtonIcon(Labeled btn, String iconPath, double height, double width) {
        ImageView iv = new ImageView(new Image(GUIUtils.class.getClassLoader()
                .getResourceAsStream(iconPath)));
        iv.setFitHeight(height);
        iv.setFitWidth(width);
        btn.setGraphic(iv);
    }

    /**
     * Добавляет кнопке всплывающую подсказку со временем
     * задержки до всплытия {@value DEFAULT_TOOLTIP_DELAY} мс
     * и бесконечным временем показа.
     *
     * @param btn         Кнопка
     * @param tooltipText Текст подсказки
     */
    public static void addTooltipToButton(Button btn, String tooltipText) {
        addTooltipToButton(btn, tooltipText, DEFAULT_TOOLTIP_DELAY);
    }

    /**
     * Добавляет кнопке всплывающую подсказку с бесконечным временем
     * показа.
     *
     * @param btn         Кнопка
     * @param tooltipText Текст подсказки
     * @param showDelayMs Задержка до показа подсказки в миллисекундах
     */
    public static void addTooltipToButton(Button btn, String tooltipText, double showDelayMs) {
        btn.setTooltip(createTooltip(tooltipText, showDelayMs, Duration.INDEFINITE));
    }

    /**
     * Создаёт всплывающую подсказку с указанным текстом, временем задержки
     * {@value DEFAULT_TOOLTIP_DELAY} мс и бесконечным временем показа
     *
     * @param tooltipText Текст подсказки
     * @return Всплывающая подсказка
     */
    public static Tooltip createTooltip(String tooltipText) {
        return createTooltip(tooltipText, DEFAULT_TOOLTIP_DELAY, Duration.INDEFINITE);
    }

    /**
     * Создаёт всплывающую подсказку с указанными текстом, временем задержки
     * и бесконечным временем показа.
     * Размер текста всех подсказок - 10 пунктов.
     *
     * @param tooltipText Текст подсказки
     * @param showDelayMs Время задержки до появления в миллисекундах
     * @return Всплывающая подсказка
     */
    public static Tooltip createTooltip(String tooltipText, double showDelayMs, Duration duration) {
        Tooltip t = new Tooltip(tooltipText);
        t.setShowDelay(Duration.millis(showDelayMs));
        t.setShowDuration(duration);
        t.setStyle("-fx-font-size: 10pt;");
        return t;
    }
}
