package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Comparator;

/**
 * Book
 *
 * Книга в библиотеке. Помимо очевидных свойств и полей также имеет
 * информацию о жанрах, к которым она отнесена, и истории займов,
 * а именно: кто и когда взял книгу (см. {@link Borrowing}).
 *
 * @version 1.2 11 March 2018
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


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_gen")
    @SequenceGenerator(name = "book_gen", sequenceName = "BOOK_SEQ", initialValue = 50)
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "BooksGenres",
            joinColumns = { @JoinColumn(name = "id_book") },
            inverseJoinColumns = { @JoinColumn(name = "id_genre") }
    )
    public Collection<Genre> getGenres() {
        return genres;
    }

    @OneToMany(cascade = CascadeType.REMOVE)
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
    public String toString() {
        return String.format("\"%1$s\", %2$d", getName(), getYear());
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

    /**
     * Проверяет наличие книги. Если книга одолжена другом, то для её возвращения
     * нужно лишь указать дату возвращения большую, чем дату займа. В ином случае
     * считается, что книги нет в наличии.
     *
     * @return true, если книга в наличии, false - иначе
     */
    @Transient
    public boolean isPresent() {
        Borrowing last = getLastBorrowing();
        if(last == null) {
            return false;
        }

        return last.getReturnDate().after(last.getId().getBorrowingDate());
    }

    /**
     * Возвращает последний займ этой книги (null, если займов не было)
     * @return Последний займ или null, если займов не было
     */
    @Transient
    private Borrowing getLastBorrowing() {
        return bookBorrowings.stream().max(Comparator.comparing(b -> b.getId()
                    .getBorrowingDate())).orElse(null);
    }
}