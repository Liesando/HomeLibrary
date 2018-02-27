package com.azzgil.homelibrary.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BookOverviewController {
    private static final double ADD_BOOK_ICON_HEIGHT = 24.0;

    @FXML
    private Button addBookBtn;

    @FXML
    private void initialize()
    {
        ImageView iv = new ImageView(new Image(getClass().getClassLoader()
                .getResourceAsStream("icons/add_book.png")));
        iv.setFitHeight(ADD_BOOK_ICON_HEIGHT);
        iv.setFitWidth(ADD_BOOK_ICON_HEIGHT);
        addBookBtn.setGraphic(iv);
    }
}
