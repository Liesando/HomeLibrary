package com.azzgil.homelibrary.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

/**
 * BorrowingId
 *
 * Служебный класс. Представляет составной ключ для займа ({@link Borrowing}).
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan & Maria Laktionova
 */
@Embeddable
public class BorrowingId implements Serializable {
    private int idBook;
    private int idFriend;
    private Date borrowingDate;


    public BorrowingId() {}


    @Column(name = "borrow_date")
    public Date getBorrowingDate() {
        return borrowingDate;
    }

    @Column(name = "id_book")
    public int getIdBook() {
        return idBook;
    }

    @Column(name = "id_friend")
    public int getIdFriend() {
        return idFriend;
    }

    public void setBorrowingDate(Date borrowingDate) {
        this.borrowingDate = borrowingDate;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BorrowingId)) {
            return false;
        }
        BorrowingId that = (BorrowingId) obj;
        return Objects.equals(getIdBook(), that.getIdBook()) &&
                Objects.equals(getIdFriend(), that.getIdFriend()) &&
                Objects.equals(getBorrowingDate(), that.getBorrowingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdBook(), getIdFriend(), getBorrowingDate());
    }
}