package org.unitils.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.UnitilsModule;
import org.unitils.db.DatabaseModule;
import org.unitils.hibernate.annotation.AfterCreateHibernateSession;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateTest;
import org.unitils.util.AnnotationUtils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 * todo javadoc
 */
public class HibernateModule implements UnitilsModule {

    private static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    private static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    private Configuration hibernateConfiguration;

    private SessionFactory hibernateSessionFactory;

    private ThreadLocal<Session> currentHibernateSessionHolder = new ThreadLocal<Session>();


    private String configurationClassName;

    private List<String> configFiles;

    public void init(org.apache.commons.configuration.Configuration configuration) {
        //noinspection unchecked
        configFiles = configuration.getList(PROPKEY_HIBERNATE_CONFIGFILES);
        configurationClassName = configuration.getString(PROPKEY_HIBERNATE_CONFIGURATION_CLASS);
    }


    /**
     * Checks whether the given test instance is a hibernate test, i.e. is annotated with the {@link HibernateTest} annotation.
     *
     * @param testObject the test instance, not null
     * @return true if the test class is a hibernate test false otherwise
     */
    protected boolean isHibernateTest(Object testObject) {

        return testObject.getClass().getAnnotation(HibernateTest.class) != null;
    }


    protected void configureHibernate(Object testObject) {
        if (hibernateConfiguration == null) {
            hibernateConfiguration = createHibernateConfiguration(testObject);
            createHibernateSessionFactory();
        }
    }


    private Configuration createHibernateConfiguration(Object test) {
        Configuration hbnConfiguration = createHibernateConfiguration();
        callHibernateConfigurationMethods(test, hbnConfiguration);
        return hbnConfiguration;
    }


    protected Configuration createHibernateConfiguration() {

        Configuration hbnConfiguration;
        try {
            hbnConfiguration = (Configuration) Class.forName(configurationClassName).newInstance();
        } catch (Exception e) {
            throw new UnitilsException("Invalid configuration class " + configurationClassName, e);
        }
        for (String configFile : configFiles) {
            hbnConfiguration.configure(configFile);
        }
        return hbnConfiguration;
    }

    private void callHibernateConfigurationMethods(Object test, Configuration configuration) {
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(test.getClass(), HibernateConfiguration.class);
        for (Method method : methods) {
            try {
                method.invoke(test, configuration);
            } catch (Exception e) {
                throw new UnitilsException("Error while calling method annotated with @" +
                        HibernateConfiguration.class.getSimpleName() + ". Ensure that this method has following signature: " +
                        "void myMethod(Configuration hibernateConfiguration)", e);
            }
        }
    }

    protected void createHibernateSession(Object testObject) {
        Session currentHibernateSession = currentHibernateSessionHolder.get();
        if (currentHibernateSession != null && (currentHibernateSession.isConnected() || currentHibernateSession.isOpen())) {
            currentHibernateSession.close();
        }
        currentHibernateSessionHolder.set(hibernateSessionFactory.openSession(getConnection()));
        callAfterCreateHibernateSessionMethods(testObject);
    }

    private void callAfterCreateHibernateSessionMethods(Object testObject) {
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), AfterCreateHibernateSession.class);
        for (Method method : methods) {
            try {
                method.invoke(testObject, currentHibernateSessionHolder.get());
            } catch (Exception e) {
                throw new UnitilsException("Error while calling method annotated with @" +
                        AfterCreateHibernateSession.class.getSimpleName() + ". Ensure that this method has following signature: " +
                        "void myMethod(org.hibernate.Session session)", e);
            }
        }
    }

    protected Connection getConnection() {
        DatabaseModule dbModule = Unitils.getModulesRepository().getFirstModule(DatabaseModule.class);
        return dbModule.getCurrentConnection();
    }

    private void createHibernateSessionFactory() {
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
    }

    public Configuration getHibernateConfiguration() {
        return hibernateConfiguration;
    }

    public Session getCurrentSession() {
        return currentHibernateSessionHolder.get();
    }

    public TestListener createTestListener() {
        return new HibernateTestListener();
    }

    private class HibernateTestListener extends TestListener {

        @Override
        public void beforeTestClass(Object testObject) {
            if (isHibernateTest(testObject.getClass())) {
                configureHibernate(testObject);
            }
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {

            if (isHibernateTest(testObject)) {
                createHibernateSession(testObject);
            }
        }

    }
}
