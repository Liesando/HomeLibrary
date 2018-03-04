package com.azzgil.homelibrary.utils;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

/**
 * AlertUtil
 *
 * Вспомогательный класс для отображения диалоговых окон с сообщениями пользователю.
 *
 * @version 1.2 4 March 2018
 * @author Sergey Medelyan
 */
public class AlertUtil
{
    private AlertUtil() {}


    /**
     * Отображает диалоговое предупреждающее окно и ожидает его закрытия
     *
     * @param title       Заголовок окна
     * @param headerText  Заголовок сообщения
     * @param contentText Сообщение
     */
    public static void showWarningAndWait(String title, String headerText, String contentText)
    {
        showAlertAndWait(Alert.AlertType.WARNING, null, title, headerText, contentText);
    }

    /**
     * Отображает диалоговое информационное окно и ожидает его закрытия
     *
     * @param title       Заголовок окна
     * @param headerText  Заговолок сообщения
     * @param contentText Сообщение
     */
    public static void showInformationAndWait(String title, String headerText, String contentText)
    {
        showAlertAndWait(Alert.AlertType.INFORMATION, null, title, headerText, contentText);
    }

    /**
     * Отображает диалоговое окно об ошибке и ожидает его закрытия
     *
     * @param title       Заголовок окна
     * @param headerText  Заголовок сообщения
     * @param contentText Сообщение
     */
    public static void showErrorAndWait(String title, String headerText, String contentText)
    {
        showAlertAndWait(Alert.AlertType.ERROR, null, title, headerText, contentText);
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
        TextArea ta = new TextArea(contentText);
        ta.setEditable(false);
        ta.setWrapText(true);
        alert.getDialogPane().setExpandableContent(ta);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }

    /**
     * Отображает диалоговое окно с сообщением о пока нереализованной функции,
     * к которой пользователь попытался получить доступ
     */
    public static void showNotRealizedWarningAndWait()
    {
        showWarningAndWait("", "not realized yet", "");
    }

    /**
     * Отображает диалоговое окно об ошибке, вызванной нарушением
     * целостности файлов программы
     */
    public static void showDataCorruptionErrorAndWait(String content)
    {
        showErrorAndWait("Core Error",
                "Файлы программы поверждены или отсутствуют",
                content);
    }

    /**
     * Отображает диалоговое окно с сообщением об ошибке, которая возникает
     * при попытке обработки команды редактирования и отсутствии редактируемого
     * объекта.
     *
     * @param content Текст сообщения
     */
    public static void showEmptySelectionErrorAndWait(String content) {
        showErrorAndWait("Error", "Не выбран объект", content);
    }

    /**
     * Отображает диалоговое окно с текстом "dev"
     * (используется только во время разработки)
     * TODO: удалить после завершения разработки
     */
    public static void showDevTimeMessage() {
        showInformationAndWait("", "", "dev");
    }
}
