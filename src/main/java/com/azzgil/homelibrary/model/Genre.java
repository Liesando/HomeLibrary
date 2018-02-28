package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Genre
 *
 * Жанр книги в библиотеке. Помимо непосредственной информации о себе
 * содержит информацию о книгах, относящихся к этому жанру (см. {@link Book})
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan
 */
@Entity
@Table(name = "Genres")
public class Genre {
    private int id;
    private String name;
    private Integer parentId;

    /**
     * Книги данного жанра (но это не означает, что они
     * <i>только</i> этого жанра, наоборот, они могут принадлежать
     * и другому жанру).
     */
    private Collection<Book> books;
    private Genre parentGenre;
    private Collection<Genre> childGenres;


    public Genre() {}


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_genre")
    public int getId() {
        return id;
    }

    @Column(name = "genre_name")
    public String getName() {
        return name;
    }

    @JoinColumn
    @Column(name = "id_parent_genre")
    public int getParentId() {
        return parentId;
    }

    @ManyToMany(mappedBy = "genres")
    public Collection<Book> getBooks() {
        return books;
    }

    @ManyToOne
    @JoinColumn(name = "id_parent_genre", insertable = false, updatable = false)
    public Genre getParentGenre() {
        return parentGenre;
    }

    @OneToMany(mappedBy = "parentGenre")
    public Collection<Genre> getChildGenres() {
        return childGenres;
    }

    public void setChildGenres(Collection<Genre> childGenres) {
        this.childGenres = childGenres;
    }

    public void setParentGenre(Genre parentGenre) {
        this.parentGenre = parentGenre;
    }

    public void setBooks(Collection<Book> books) {
        this.books = books;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    @Transient
    public String toString() {
        return getName();
    }

    @Transient
    public String getFullName() {
        if(getParentGenre() == null) {
            return getName();
        }
        return getParentGenre().getFullName().concat(" / " + getName());
    }
}
