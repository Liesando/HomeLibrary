package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.hibernate.Session;

import java.io.IOException;

/**
 * BookOverviewController
 *
 * Контроллер для секции обзора книг. Обрабатывает различные события
 * интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.6 8 March 2018
 */
public class BookOverviewController implements ICUDController {

    @FXML private Button addBookBtn;
    @FXML private VBox contentVBox;

    /**
     * Книга, по панельке которой щёлкнули (точнее, по кнопке
     * редактирования или удаления на панельке)
     */
    private Book observableBook;

    @FXML
    private void initialize()
    {
        loadIcons();
        refreshBooks();
    }

    /**
     * Загружает все требуемые интерфейсу иконки
     */
    private void loadIcons() {
        GUIUtils.loadButtonIcon(addBookBtn, GUIUtils.ADD_ICON);
    }

    /**
     * Обновляет список книг и перерисовывает их
     */
    public void refreshBooks() {
        contentVBox.getChildren().clear();
        try {
            Book[] books = HomeLibrary.getAllBooks();
            double totalHeight = 0.0;
            for (Book b : books) {
                FXMLLoader loader = FXMLUtils.configureLoaderFor("views/BookPanel.fxml");
                contentVBox.getChildren().add(loader.load());
                BookPanelController controller = loader.getController();
                controller.setParentController(this);
                controller.setBook(b);
                totalHeight += controller.getCurrentHeight();
            }

            setVBoxHeight(totalHeight);
        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait("views/BookPanel.fxml");
            Platform.exit();
        }
    }

    /**
     * Корректирует размер VBox'а для правильного отображения
     * полосы прокрутки
     *
     * @param correction Коррекция в пикселах
     */
    public void correctVBoxHeight(double correction) {
        setVBoxHeight(contentVBox.getPrefHeight() + correction);
    }

    /**
     * Задаёт предпочтительную и максимальную высоты для VBox'а
     *
     * @param height Новая высота
     */
    private void setVBoxHeight(double height) {
        contentVBox.setMaxHeight(height);
        contentVBox.setPrefHeight(height);
    }

    @Override
    public void create() {
        HomeLibrary.showBookEditWindow(false, null);
    }

    @Override
    public boolean validateUpdate() {
        return false;
    }

    @Override
    public void update() {
        AlertUtil.showNotRealizedWarningAndWait();
    }

    @Override
    public boolean validateDelete() {
        return false;
    }

    @Override
    public void setUpdateFeatureAccess(boolean accessible) {

    }

    @Override
    public void setReadFeatureAccess(boolean accessible) {

    }

    @Override
    public void setDeleteFeatureAccess(boolean accessible) {

    }

    public void setObservableBook(Book observableBook) {
        this.observableBook = observableBook;
    }

    @Override
    public void delete() {
        if(observableBook == null) {
            AlertUtil.showEmptySelectionErrorAndWait("Не поддерживается в данной секции. " +
                    "Используйте кнопки рядом с описанием книги.");
            return;
        }

        Session session = HibernateUtil.openSession();

        session.beginTransaction();
        session.delete(observableBook);
        session.getTransaction().commit();

        HomeLibrary.refreshBooks();
        HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
        refreshBooks();

        session.close();
        observableBook = null;
    }
}
