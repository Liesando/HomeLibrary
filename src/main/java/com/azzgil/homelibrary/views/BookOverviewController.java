package com.azzgil.homelibrary.views;

import com.azzgil.homelibrary.model.Book;
import com.azzgil.homelibrary.utils.AlertUtil;
import com.azzgil.homelibrary.utils.FXMLUtils;
import com.azzgil.homelibrary.utils.HibernateUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.io.IOException;
import java.util.List;

/**
 * BookOverviewController
 *
 * Контроллер для секции обзора книг. Обрабатывает различные события
 * интерфейса
 *
 * @author Sergey Medelyan
 * @version 1.1 27 Feb 2018
 */
public class BookOverviewController {

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
        ImageView iv = new ImageView(new Image(getClass().getClassLoader()
                .getResourceAsStream("icons/add_book.png")));
        iv.setFitHeight(ADD_BOOK_ICON_HEIGHT);
        iv.setFitWidth(ADD_BOOK_ICON_HEIGHT);
        addBookBtn.setGraphic(iv);
    }

    /**
     * Загружает все книги и отображает их
     */
    private void loadBooks() {
        try {
            Session session = HibernateUtil.openSession();
            List books = session.createQuery("from Book").list();
            for (Object o : books) {
                Book b = (Book) o;
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

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
