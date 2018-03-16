package com.azzgil.homelibrary.utils;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.spi.ServiceException;

import java.util.ArrayList;

/**
 * HibernateUtils
 *
 * Вспомогательный класс для конфигурации Hibernate, установки соединения с БД
 * и выполнения запросов к ней.
 *
 * @author Sergey Medelyan & Maria Laktionova
 * @version 1.0 23 Feb 2018
 */
public class HibernateUtils {
    /**
     * Список классов, которые подлежат ORM
     */
    private static ArrayList<Class> mappingClasses = new ArrayList<>();
    private static SessionFactory sessionFactory = null;


    private HibernateUtils() {
    }


    /**
     * Регистрирует классы, которые требуют ORM. Вызывать
     * <b><i>строго до</i></b> того, как произойдёт
     * вызов {@link #buildSessionFactory()}, иначе не
     * возымеет эффекта.
     *
     * @param c Класс
     */
    public static void registerMappingClass(Class c) {
        mappingClasses.add(c);
    }

    /**
     * Конфигурирует Hibernate для работы. Вызовите этот метод
     * При запуске программы.
     */
    public static void buildSessionFactory() throws HibernateException {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.configure();
            for (Class c :
                    mappingClasses) {
                configuration.addAnnotatedClass(c);
            }
            try {
                sessionFactory = configuration.buildSessionFactory();
            } catch (ServiceException e) {
                AlertUtil.showErrorAndWait(null,"Ошибка подключения",
                        "Не удалось подключиться к базе",
                        "Проверьте настройки hibernate.cfg.xml (" +
                                "возможно, неправильно указаны адрес сервера," +
                                " пара логин-пароль или поставщик СУБД)." +
                                "\nПрограмма завершит свою работу.");
                System.exit(1);
            }
        }
    }

    /**
     * Освобождает ресурсы, связанные с SessionFactory.
     * Вызывать при завершении приложения
     */
    public static void destroySessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    /**
     * Открывает новую сессию. Сессия должна быть закрыта после
     * завершения работы с ней.
     *
     * @return Новая сессия
     * @throws HibernateException
     */
    public static Session openSession() throws HibernateException {
        if (sessionFactory == null) {
            buildSessionFactory();
        }

        return sessionFactory.openSession();
    }
}