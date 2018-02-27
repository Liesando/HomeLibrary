package com.azzgil.homelibrary.utils;

import javafx.fxml.FXMLLoader;

/**
 * FXMLUtils
 *
 * Вспомогательный класс для работы с FXML-файлами
 *
 * @author Sergey Medelyan
 * @version 1.0 27 Feb 2018
 */
public class FXMLUtils {

    /**
     * Возвращает объект-загрузчик, настроенный на ресурс по указанному пути
     *
     * @param path Путь к файлу-ресурсу
     * @return Загрузчик
     */
    public static FXMLLoader configureLoaderFor(String path) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FXMLUtils.class.getClassLoader().getResource(path));
            return loader;
    }
}
