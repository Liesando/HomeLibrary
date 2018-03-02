package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.ICUDController;
import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * BookOverviewController
 *
 * Контроллер для секции обзора книг. Обрабатывает различные события
 * интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.3 2 March 2018
 */
public class BookOverviewController implements ICUDController {

    /** Высота иконки добавления книги (иконка подгружается динамически) */
    private static final double ADD_BOOK_ICON_HEIGHT = 24.0;

    private Stage primaryStage;
    @FXML private Button addBookBtn;
    @FXML private VBox contentVBox;

    @FXML
    private void initialize()
    {
        loadIcons();
        loadBooks();
    }

    /**
     * Загружает все требуемые интерфейсу иконки
     */
    private void loadIcons() {
        GUIUtils.loadButtonIcon(addBookBtn, "icons/add_book.png");
    }

    /**
     * Загружает все книги и отображает их
     */
    private void loadBooks() {
        try {
            Book[] books = DataUtils.fetchAllBooks();
            for (Book b : books) {
                FXMLLoader loader = FXMLUtils.configureLoaderFor("views/BookPanel.fxml");
                contentVBox.getChildren().add(loader.load());
                BookPanelController controller = loader.getController();
                controller.setBook(b);
            }
        } catch (IOException e) {
            AlertUtil.showDataCorruptionErrorAndWait(primaryStage, "views/BookPanel.fxml");
            Platform.exit();
        }
    }

    @Override
    public void create() {

    }

    @Override
    public boolean validateUpdate() {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean validateDelete() {
        return false;
    }

    @Override
    public void delete() {

    }
}
