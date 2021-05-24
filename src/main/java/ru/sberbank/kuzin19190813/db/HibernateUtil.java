package ru.sberbank.kuzin19190813.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import ru.sberbank.kuzin19190813.util.PropertiesManager;
import ru.sberbank.kuzin19190813.winter_framework.util.AnnotationManager;

import javax.persistence.Entity;
import java.util.Properties;

public class HibernateUtil {
    private static final String entityPackageName = "ru.sberbank.kuzin19190813.model";
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                Properties settings = PropertiesManager.getProperties("database");
                settings.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
                //settings.setProperty(Environment.SHOW_SQL, "true");

                configuration
                        .addPackage(entityPackageName);

                configuration
                        .setProperties(settings);

                for (Class<?> annotatedClass : AnnotationManager.findClassesByAnnotation(entityPackageName, Entity.class)) {
                    configuration.addAnnotatedClass(annotatedClass);
                }

//                configuration
//                        .addAnnotatedClass(Card.class)
//                        .addAnnotatedClass(Account.class)
//                        .addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    private HibernateUtil() {
    }
}
