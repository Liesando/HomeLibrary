package com.azzgil.homelibrary.model;

import javax.persistence.*;
import java.util.Collection;

/**
 * Genre
 *
 * Жанр книги в библиотеке. Помимо непосредственной информации о себе
 * содержит информацию о книгах, относящихся к этому жанру (см. {@link Book})
 *
 * @version 1.2 2 March 2018
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

    public Genre(int id, String name, Integer parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_seq_gen")
    @SequenceGenerator(name = "genre_seq_gen",
            sequenceName = "genre_seq", initialValue = 1,
            allocationSize = 20)
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
    public Integer getParentId() {
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

    /**
     * Возвращает полное имя жанра в формате:
     * < и т. д. > / <имя предка предка> / <имя предка> / <имя жанра>
     * Если name == null, то в качестве имени жанра ( <имя жанра> )
     * берётся пустая строка
     *
     * @return Полное имя жанра
     */
    @Transient
    public String getFullName() {
        String niceName = getName() == null ? "" : getName();
        if(getParentGenre() == null) {
            return niceName;
        }
        return getParentGenre().getFullName().concat(" / " + niceName);
    }
}
