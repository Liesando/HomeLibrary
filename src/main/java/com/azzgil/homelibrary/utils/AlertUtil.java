package com.azzgil.homelibrary.utils;

import javafx.scene.control.Alert;
import javafx.stage.Window;

/**
 * AlertUtil
 *
 * Вспомогательный класс для отображения диалоговых окон с сообщениями пользователю.
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan
 */
public class AlertUtil
{
    private AlertUtil() {}


    /**
     * Отображает диалоговое предупреждающее окно и ожидает его закрытия
     *
     * @param owner       Родитель, владелец, он же - объект-контейнер (Stage)
     * @param title       Заголовок окна
     * @param headerText  Заголовок сообщения
     * @param contentText Сообщение
     */
    public static void showWarningAndWait(Window owner, String title, String headerText, String contentText)
    {
        showAlertAndWait(Alert.AlertType.WARNING, owner, title, headerText, contentText);
    }

    /**
     * Отображает диалоговое информационное окно и ожидает его закрытия
     *
     * @param owner       Родитель, владелец, он же - объект-контенейр (Stage)
     * @param title       Заголовок окна
     * @param headerText  Заговолок сообщения
     * @param contentText Сообщение
     */
    public static void showInformationAndWait(Window owner, String title, String headerText, String contentText)
    {
        showAlertAndWait(Alert.AlertType.INFORMATION, owner, title, headerText, contentText);
    }

    /**
     * Отображает диалоговое окно об ошибке и ожидает его закрытия
     *
     * @param owner       Родитель, владелец, он же - объект-контейнер (Stage)
     * @param title       Заголовок окна
     * @param headerText  Заголовок сообщения
     * @param contentText Сообщение
     */
    public static void showErrorAndWait(Window owner, String title, String headerText, String contentText)
    {
        showAlertAndWait(Alert.AlertType.ERROR, owner, title, headerText, contentText);
    }

    /**
     * Отображает диалоговое окно указаного типа и ожидает его закрытия
     *
     * @param alertType   Тип окна
     * @param owner       Родитель, владелец, он же - объект-контейнер (Stage)
     * @param title       Заголовок окна
     * @param headerText  Заголовок сообщения
     * @param contentText Сообщение
     */
    public static void showAlertAndWait(Alert.AlertType alertType, Window owner, String title,
                                        String headerText, String contentText)
    {
        Alert alert = new Alert(alertType);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    /**
     * Отображает диалоговое окно с сообщением о пока нереализованной функции,
     * к которой пользователь попытался получить доступ
     *
     * @param owner Родитель, владелец, он же - объект-контейнер (Stage)
     */
    public static void showNotRealizedWarningAndWait(Window owner)
    {
        showWarningAndWait(owner, "", "not realized yet", "");
    }

    /**
     * Отображает диалоговое окно об ошибке, вызванной нарушением
     * целостности файлов программы
     *
     * @param owner Родитель, владелец, он же - объект-контейнер (Stage)
     */
    public static void showDataCorruptionErrorAndWait(Window owner, String content)
    {
        showErrorAndWait(owner, "Core Error",
                "Файлы программы поверждены или отсутствуют",
                content);
    }

    /**
     * Отображает диалоговое окно с текстом "dev"
     * (используется только во время разработки)
     * TODO: удалить после завершения разработки
     */
    public static void showDevTimeMessage() {
        showInformationAndWait(null, "", "", "dev");
    }
}
