package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.VBox;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.io.IOException;
import java.util.Arrays;

/**
 * BookOverviewController
 *
 * Контроллер для секции обзора книг. Обрабатывает различные события
 * интерфейса
 *
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.7 16 March 2018
 */
public class BookOverviewController implements ICUDController {

    @FXML private Button addBookBtn;
    @FXML private SplitMenuButton filterSMBtn;
    @FXML private MenuItem clearFilterMI; // мб иконку добавить ей?..
    @FXML private VBox contentVBox;

    private String filteringQuery;

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
        GUIUtils.loadButtonIcon(filterSMBtn, GUIUtils.FILTER_ICON);
    }

    /**
     * Обновляет список книг и перерисовывает их
     */
    public void refreshBooks() {
        contentVBox.getChildren().clear();
        try {

            // если у нас есть фильтр, то используем его
            Book[] books = filteringQuery != null ?
                    applyFilter() :
                    HomeLibrary.getAllBooks();

            // но если он ничего не дал, переключаемся на стандарт
            if(books.length == 0) {
                filteringQuery = null;
                books = HomeLibrary.getAllBooks();
                AlertUtil.showInformationAndWait("Результаты поиска", "Нет результатов",
                        "Нет результатов, удовлетворяющих критериям применённого фильтра.\n" +
                                "Фильтр будет сброшен.");
            }

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

        Session session = HibernateUtils.openSession();

        session.beginTransaction();
        session.delete(observableBook);
        session.getTransaction().commit();

        HomeLibrary.refreshBooks();
        HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
        refreshBooks();

        session.close();
        observableBook = null;
    }

    @FXML
    private void onFilterBtn() {
        filteringQuery = HomeLibrary.showFilterBooksWindow();
        refreshBooks();
    }

    @FXML
    private void onClearFilterBtn() {
        filteringQuery = null;
        refreshBooks();
    }

    /**
     * Возвращает книги, прошедшую фильтрацию. За составление
     * запроса-фильтра отвечает {@link FilterBooksWindowController}
     * @return Массив книг, прошедших фильтрацию
     */
    private Book[] applyFilter() {
        Session session = HibernateUtils.openSession();
        Book[] books  = Arrays.stream(session.createQuery(filteringQuery).stream().toArray())
                .toArray(Book[]::new);
        Arrays.stream(books).forEach(b -> Hibernate.initialize(b.getBookBorrowings()));
        session.close();
        return books;
    }
}
