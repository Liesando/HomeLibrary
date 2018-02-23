package com.azzgil.homelibrary.utils;

import com.azzgil.homelibrary.model.PublishingHouse;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;

/**
 * HibernateUtil
 *
 * Вспомогательный класс для конфигурации Hibernate, установки соединения с БД
 * и выполнения запросов к ней.
 *
 * @version 1.0 23 Feb 2018
 * @author Sergey Medelyan
 */
public class HibernateUtil
{
    private static SessionFactory sessionFactory = null;

    /** Список классов, которые подлежат ORM */
    private static ArrayList<Class> mappingClasses = new ArrayList<>();


    private HibernateUtil() {}


    /**
     * Регистрирует классы, которые требуют ORM. Вызывать
     * <b><i>строго до</i></b> того, как произойдёт
     * вызов {@link #buildSessionFactory()}
     * @param c Класс
     */
    public static void registerMappingClass(Class c)
    {
        mappingClasses.add(c);
    }

    /**
     * Конфигурирует Hibernate для работы. Вызовите этот метод
     * При запуске программы.
     */
    public static void buildSessionFactory() throws HibernateException
    {
        if(sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.configure();
            for (Class c:
                 mappingClasses) {
                configuration.addAnnotatedClass(c);
            }
            sessionFactory = configuration.buildSessionFactory();
        }
    }

    /**
     * Освобождает ресурсы, связанные с SessionFactory.
     * Вызывать при завершении приложения
     */
    public static void destroySessionFactory()
    {
        if(sessionFactory != null)
        {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    /**
     * Открывает новую сессию. Не забывайте закрывать сессию
     * после завершения работы с БД!
     *
     * @return Новая сессия
     * @throws HibernateException
     */
    public static Session openSession() throws HibernateException
    {
        if(sessionFactory == null)
        {
            buildSessionFactory();
        }

        return sessionFactory.openSession();
    }
}
