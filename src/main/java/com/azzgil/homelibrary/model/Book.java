package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Iterator;


/**
 * Book
 *
 * Книга в библиотеке. Помимо очевидных свойств и полей также имеет
 * информацию о жанрах, к которым она отнесена, и истории займов,
 * а именно кто и когда взял книгу (см. {@link Borrowing}).
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan
 */
@Entity
@Table(name = "Books")
public class Book {
    private int id;
    private String name;
    private int year;
    private String picAuthor;
    private String translator;
    private String author;
    private String commentary;
    private PublishingHouse publishingHouse;

    /** Список жанров книги */
    private Collection<Genre> genres;

    /** История займов */
    private Collection<Borrowing> bookBorrowings;


    public Book() {}


    // TODO: Sequence-generator
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_book")
    public int getId() {
        return id;
    }

    @Column(name = "book_name")
    public String getName() {
        return name;
    }

    @Column(name = "pub_year")
    public int getYear() {
        return year;
    }

    @ManyToOne
    @JoinColumn(name = "id_pub_house")
    public PublishingHouse getPublishingHouse() {
        return publishingHouse;
    }

    @Column(name = "author")
    public String getAuthor() {
        return author;
    }

    @Column(name = "commentary")
    public String getCommentary() {
        return commentary;
    }

    @Column(name = "pic_author")
    public String getPicAuthor() {
        return picAuthor;
    }

    @Column(name = "translator")
    public String getTranslator() {
        return translator;
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "BooksGenres",
            joinColumns = { @JoinColumn(name = "id_book") },
            inverseJoinColumns = { @JoinColumn(name = "id_genre") }
    )
    public Collection<Genre> getGenres() {
        return genres;
    }

    @OneToMany
    @JoinColumn(name = "id_book")
    public Collection<Borrowing> getBookBorrowings() {
        return bookBorrowings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public void setPicAuthor(String picAuthor) {
        this.picAuthor = picAuthor;
    }

    public void setPublishingHouse(PublishingHouse publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenres(Collection<Genre> genres) {
        this.genres = genres;
    }

    public void setBookBorrowings(Collection<Borrowing> bookBorrowings) {
        this.bookBorrowings = bookBorrowings;
    }

    @Override
    @Transient
    public String toString() {
        return String.format("%s, %d", getName(), getYear());
    }

    @Transient
    public String getGenresAsString() {
        StringBuilder sb = new StringBuilder();
        if (getGenres() == null || getGenres().size() == 0) {
            return "[жанр не указан]";
        }

        for (Genre g: getGenres()) {
            sb.append(g.getFullName());
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}