package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.HomeLibrary;
import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.model.Genre;
import com.azzgil.homelibrary.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.PersistenceException;
import java.util.*;

/**
 * GenresOverviewController
 *
 * Контроллер для секции обзора жанров. Обрабатывает события
 * интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.4 8 March 2018
 */
public class GenresOverviewController implements ICUDController {

    private static final String EMPTY_SELECTION_ERROR = "Пожалуйста, сначала выберите жанр из списка";

    // тексты всплывающих подсказок
    private static final String SHOW_BOOKS_BTN_TOOLTIP = "Показать книги этого жанра";
    private static final String DELETE_BTN_TOOLTIP = "Удалить выбранные жанры и их поджанры";
    private static final String EDIT_BTN_TOOLTIP = "Редактировать выбранный жанр";

    @FXML private Button addGenreBtn;
    @FXML private Button showBooksBtn;
    @FXML private Button deleteBtn;
    @FXML private Button editBtn;
    @FXML private MenuItem deleteMI;
    @FXML private MenuItem editMI;
    @FXML private MenuItem showBooksMI;
    @FXML private TreeView<Genre> treeView;
    private Genre[] genres;

    @FXML
    private void initialize() {
        GUIUtils.loadButtonIcon(addGenreBtn, GUIUtils.ADD_ICON);
        GUIUtils.loadButtonIcon(showBooksBtn, GUIUtils.DETAILS_ICON);
        GUIUtils.loadButtonIcon(deleteBtn, GUIUtils.DELETE_ICON);
        GUIUtils.loadButtonIcon(editBtn, GUIUtils.EDIT_ICON);

        GUIUtils.addTooltipToButton(showBooksBtn, SHOW_BOOKS_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(deleteBtn, DELETE_BTN_TOOLTIP);
        GUIUtils.addTooltipToButton(editBtn, EDIT_BTN_TOOLTIP);

        refreshGenres();
    }

    /** Обновляет список жанров */
    public void refreshGenres() {
        genres = HomeLibrary.getAllGenres();
        generateTree(genres);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void setReadFeatureAccess(boolean accessible) {
        showBooksBtn.setDisable(!accessible);
        showBooksMI.setDisable(!accessible);
    }

    /**
     * Генерирует дерево жанров по переданному массиву жанров.
     *
     * TODO: genres sort
     * @param genres Массив жанров
     */
    private void generateTree(Genre[] genres) {
        TreeItem<Genre>[] items = new TreeItem[genres.length + 1];

        // пустой жанр-корень
        items[genres.length] = new TreeItem<>(new Genre());
        for (int i = 0; i < genres.length; ++i) {
            items[i] = new TreeItem<>(genres[i]);
        }

        int indexOfParent;

        // next one is just to obtain access to indexOf() method :\
        List lgenres = Arrays.asList(genres);

        // здесь мы хотим создать между узлами дерева такую же
        // иерархию, в какой состоят жанры и их поджанры
        for(int i = 0; i < genres.length; ++i) {
            items[i].setExpanded(true);
            if(genres[i].getParentGenre() != null) {
                indexOfParent = lgenres.indexOf(genres[i].getParentGenre());
                items[indexOfParent].getChildren().add(items[i]);
            } else {
                items[genres.length].getChildren().add(items[i]);
            }
        }

//        на данный момент бесполезная строка, но использовалась при попытке
//        прикрутить сортировку
//        TreeItem<Genre> root = items[genres.length];

//        the next one doesn't make any effect (obviously)
//        Arrays.sort(items, Comparator.comparing(i -> i.getValue().getFullName()));
        treeView.setRoot(items[genres.length]);
        treeView.setShowRoot(false);
        items[genres.length].setExpanded(true);
    }

    /**
     * Отображает книги выбранного жанра и его поджанров.
     * Если выбрано несколько жанров, то выбирает первый из них.
     * TODO: сделать для нескольких жанров
     */
    @FXML
    private void showBooks() {
        Genre g = treeView.getSelectionModel().getSelectedItem() != null ?
                treeView.getSelectionModel().getSelectedItem().getValue():
                null;
        if(g == null) {
            AlertUtil.showEmptySelectionErrorAndWait(EMPTY_SELECTION_ERROR);
            return;
        }

        String books = DataUtils.reduceBooks(DataUtils.collectBooksOfGenre(g));
        AlertUtil.showInformationAndWait("Книги жанра", g.getFullName(),
                (books.equals("") ? books : "книг указанного жанра нет в библиотеке"));
    }

    @Override
    public void create() {
        HomeLibrary.showGenreEditWindow(false, null);
    }

    @Override
    public boolean validateUpdate() {
        return treeView.getSelectionModel().getSelectedItem() != null &&
            treeView.getSelectionModel().getSelectedItem().getValue() != null;
    }

    @Override
    public void update() {
        if (!validateUpdate()) {
            AlertUtil.showEmptySelectionErrorAndWait(EMPTY_SELECTION_ERROR);
            return;
        }
        HomeLibrary.showGenreEditWindow(true,
                treeView.getSelectionModel().getSelectedItem().getValue());
    }



    @Override
    public boolean validateDelete() {
        return validateUpdate();
    }

    @Override
    public void setUpdateFeatureAccess(boolean accessible) {
        editBtn.setDisable(!accessible);
        editMI.setDisable(!accessible);
    }

    @Override
    public void setDeleteFeatureAccess(boolean accessible) {
        deleteBtn.setDisable(!accessible);
        deleteMI.setDisable(!accessible);
    }

    @Override
    public void delete() {
        if(!validateDelete()) {
            AlertUtil.showEmptySelectionErrorAndWait(EMPTY_SELECTION_ERROR);
            return;
        }

        Session session = HibernateUtil.openSession();
        try {
            session.beginTransaction();
            for (TreeItem<Genre> t:
                    treeView.getSelectionModel().getSelectedItems()) {
                session.delete(t.getValue());
            }
            session.getTransaction().commit();

            HomeLibrary.refreshGenres();
            HomeLibrary.getLibraryOverviewController().refreshOverallInfo();
            refreshGenres();
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            if(e.getCause() instanceof ConstraintViolationException) {

                // отображаем список книг, которые вызывают
                // конфликт и препятствуют удалению жанра
                ArrayList<Book> books = new ArrayList<>();
                for (TreeItem<Genre> i:
                     treeView.getSelectionModel().getSelectedItems()) {
                    books.addAll(DataUtils.collectBooksOfGenre(i.getValue()));
                }

                String info = DataUtils.reduceBooks(books);

                AlertUtil.showWarningAndWait("Error",
                        "Невозможно удалить жанр",
                        "Существуют книги этого жанра или его поджанров. " +
                                "Чтобы удаление жанра стало возможным, " +
                                "сначала удалите эти книги либо измените их жанр\n\n" +
                                "Книги, могущие вызвать конфликт:\n" +
                                info);

            } else {
                // unknown exception, throw through
                throw e;
            }
        } finally {
            session.close();
        }
    }
}
