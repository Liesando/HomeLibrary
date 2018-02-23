package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * PublishingHouse
 *
 * Представляет издательство книги и его свойства.
 * Дополнительно содержит информацию о книгах, изданных
 * данным издательством.
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan
 */
@Entity
@Table(name = "Publishing_Houses")
public class PublishingHouse {

    private int id;
    private String name;

    /** Книги, изданные данным издательством */
    private Collection<Book> books;


    public PublishingHouse() {}


    @Id
    @Column(name = "id_pub_house")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    @Column(name = "pub_house_name")
    public String getName() {
        return name;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publishingHouse")
    public Collection<Book> getBooks() {
        return books;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBooks(Collection<Book> books) {
        this.books = books;
    }

    @Override
    @Transient
    public String toString() {
        return getName();
    }
}
