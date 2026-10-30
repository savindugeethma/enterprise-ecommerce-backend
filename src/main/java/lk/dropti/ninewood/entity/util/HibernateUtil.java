package lk.dropti.ninewood.entity.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    static {
        try{

            sessionFactory = new Configuration().configure().buildSessionFactory();
            System.out.println("✅ Hibernate SessionFactory CREATED");
        }catch (HibernateException e){
            throw new ExceptionInInitializerError("SessionFactory creation failed: "+e.getMessage());
        }
    }
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }
    public static void shutdown(){
        sessionFactory.close();
    }
}

//package lk.dropti.ninewood.util;
//
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.service.ServiceRegistry;
//
//public class HibernateUtil {
//
//    private static SessionFactory sessionFactory;
//
//    static {
//        try {
//            Configuration configuration = new Configuration();
//            configuration.configure("hibernate.cfg.xml");
//
//            ServiceRegistry serviceRegistry =
//                    new StandardServiceRegistryBuilder()
//                            .applySettings(configuration.getProperties())
//                            .build();
//
//            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//
//            System.out.println("✅ Hibernate SessionFactory CREATED SUCCESSFULLY");
//
//        } catch (Exception e) {
//            System.err.println("❌ Hibernate SessionFactory CREATION FAILED");
//            e.printStackTrace();
//
//            // VERY IMPORTANT — fail fast
//            throw new ExceptionInInitializerError(e);
//        }
//    }
//
//    public static SessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
//
//    public static void shutdown() {
//        if (sessionFactory != null) {
//            sessionFactory.close();
//            System.out.println("Hibernate SessionFactory CLOSED");
//        }
//    }
//}
