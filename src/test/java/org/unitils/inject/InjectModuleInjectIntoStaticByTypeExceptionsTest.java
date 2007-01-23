package org.unitils.inject;

import org.unitils.UnitilsJUnit3;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.util.PropertyAccess;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoStaticByTypeExceptionsTest extends UnitilsJUnit3 {

    private static final Log logger = LogFactory.getLog(InjectModuleInjectIntoStaticByTypeExceptionsTest.class);

    private TestInjectIntoStaticByType_NoPropertyOfType testInjectIntoStaticByType_noPropertyOfType = new TestInjectIntoStaticByType_NoPropertyOfType();
    private TestInjectIntoStaticByType_MoreThanOneFieldOfType testInjectIntoStaticByType_moreThanOneFieldOfType = new TestInjectIntoStaticByType_MoreThanOneFieldOfType();
    private TestInjectIntoStaticByType_MoreThanOneSetterOfType testInjectIntoStaticByType_moreThanOneSetterOfType = new TestInjectIntoStaticByType_MoreThanOneSetterOfType();
    private TestInjectIntoStaticByType_MoreThanOneFieldOfSuperType testInjectIntoStaticByType_moreThanOneFieldOfSuperType = new TestInjectIntoStaticByType_MoreThanOneFieldOfSuperType();
    private TestInjectIntoStaticByType_MoreThanOneSetterOfSuperType testInjectIntoStaticByType_moreThanOneSetterOfSuperType = new TestInjectIntoStaticByType_MoreThanOneSetterOfSuperType();

    private InjectModule injectModule = new InjectModule();

    protected void setUp() throws Exception {
        super.setUp();

        Configuration conf = Unitils.getInstance().getConfiguration();
        injectModule.init(conf);
    }

    public void testInjectIntoStaticByType_noPropertyOfType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_noPropertyOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoStaticByType_moreThanOneFieldOfType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneFieldOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoStaticByType_moreThanOneSetterOfType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneSetterOfType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoStaticByType_moreThanOneFieldOfSuperType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneFieldOfSuperType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public void testInjectIntoStaticByType_moreThanOneSetterOfSuperType() {
        try {
            injectModule.injectObjects(testInjectIntoStaticByType_moreThanOneSetterOfSuperType);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected flow
            logger.debug(this, e);
        }
    }

    public class TestInjectIntoStaticByType_NoPropertyOfType {

        @InjectIntoStaticByType(target = InjectOn_NoPropertyOfType.class)
        private ToInject toInject;
    }

    public class TestInjectIntoStaticByType_MoreThanOneFieldOfType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneFieldOfType.class)
        private ToInject toInject;
    }

    public class TestInjectIntoStaticByType_MoreThanOneSetterOfType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneSetterOfType.class, propertyAccess = PropertyAccess.SETTER)
        private ToInject toInject;
    }

    public class TestInjectIntoStaticByType_MoreThanOneFieldOfSuperType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneFieldOfSuperType.class)
        private ToInject toInject;
    }

    public class TestInjectIntoStaticByType_MoreThanOneSetterOfSuperType {

        @InjectIntoStaticByType(target = InjectOn_MoreThanOneSetterOfSuperType.class, propertyAccess = PropertyAccess.SETTER)
        private ToInject toInject;
    }

    public class ToInjectSuper {
    }

    /**
     * Object to inject
     */
    public class ToInject extends ToInjectSuper {
    }

    /**
     * Object to inject into
     */
    public class InjectOn {
    }

    public static class InjectOn_NoPropertyOfType {
    }

    public static class InjectOn_MoreThanOneFieldOfType {

        private static ToInject toInject1;

        private static ToInject toInject2;

    }

    public static class InjectOn_MoreThanOneSetterOfType {

        public static void setToInject1(ToInject toInject1) {
        }

        public static void setToInject2(ToInject toInject2) {
        }

    }

    public static class InjectOn_MoreThanOneFieldOfSuperType {

        private static ToInjectSuper toInject1;

        private static ToInjectSuper toInject2;

    }

    public static class InjectOn_MoreThanOneSetterOfSuperType {

        public static void setToInject1(ToInjectSuper toInject1) {
        }

        public static void setToInject2(ToInjectSuper toInject2) {
        }

    }
}