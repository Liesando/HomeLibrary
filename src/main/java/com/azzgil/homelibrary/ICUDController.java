package com.azzgil.homelibrary;


/**
 * CUD (Create, Update, Delete)
 *
 * Интерфейс, который реализуют все контроллеры секций
 * (см. {@link Section}). Каждый конкретный контроллер
 * реализует реакцию и поведение в соответствтии с
 * добавлением, редактированием или удалением своего
 * определённого вида объекта.
 *
 * Прим.: не все секции имеют такой контроллер. Например,
 * секция Библиотека не имеет контроллера в данный момент.
 *
 * @author Sergey Medelyan
 * @version 1.0 2 March 2018
 */
public interface ICUDController {
    void create();
    void update();
    void delete();
    boolean validateUpdate();
    boolean validateDelete();
}
