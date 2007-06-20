/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.spring;

import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.SpringApplicationContext;

import java.util.Properties;

/**
 * Test for the application context injection in the {@link SpringModule}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModuleInjectApplicationContextTest extends TestCase {

    /* Tested object */
    private SpringModule springModule;


    /**
     * Initializes the test and test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        springModule = new SpringModule();
        springModule.init(configuration);
    }


    /**
     * Tests assigning an application context to a field and a setter.
     */
    public void testInjectApplicationContext() {
        SpringTest springTest = new SpringTest();
        springModule.injectApplicationContext(springTest);

        assertNotNull(springTest.field);
        assertNotNull(springTest.setter);
    }


    /**
     * Tests assigning an application context that is created by multiple field and setter annotations.
     * The same context should have been assigned..
     */
    public void testInjectApplicationContext_mixing() {
        SpringTestMixing springTestMixing = new SpringTestMixing();
        springModule.injectApplicationContext(springTestMixing);

        assertNotNull(springTestMixing.field1);
        assertSame(springTestMixing.field1, springTestMixing.field2);
        assertSame(springTestMixing.field1, springTestMixing.setter);
    }


    /**
     * Tests assigning an application context but no context was created.
     */
    public void testInjectApplicationContext_noContextCreated() {
        SpringTestNoContextCreated springTestNoContextCreated = new SpringTestNoContextCreated();
        try {
            springModule.injectApplicationContext(springTestNoContextCreated);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
        }
        assertNull(springTestNoContextCreated.field);
    }


    /**
     * Tests assigning an application context that was created in the super class.
     */
    public void testInjectApplicationContext_contextCreatedInSuperClass() {
        SpringTestContextCreatedInSuperClass springTestContextCreatedInSuperClass = new SpringTestContextCreatedInSuperClass();
        springModule.injectApplicationContext(springTestContextCreatedInSuperClass);

        assertNotNull(springTestContextCreatedInSuperClass.field);
    }


    /**
     * Test SpringTest class.
     */
    @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
    private class SpringTest {

        @SpringApplicationContext
        private ApplicationContext field = null;

        private ApplicationContext setter;


        @SpringApplicationContext
        public void setField(ApplicationContext setter) {
            this.setter = setter;
        }
    }


    /**
     * Test SpringTest class.
     */
    private class SpringTestMixing {

        @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
        private ApplicationContext field1 = null;

        @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
        private ApplicationContext field2 = null;

        private ApplicationContext setter;


        @SpringApplicationContext({"classpath:org/unitils/spring/services-config.xml"})
        public void setField(ApplicationContext setter) {
            this.setter = setter;
        }
    }


    /**
     * Test SpringTest class that does not create a context (no locations) but asks for an assignment (should
     * cause an exception).
     */
    private class SpringTestNoContextCreated {

        @SpringApplicationContext
        protected ApplicationContext field = null;
    }


    /**
     * Test SpringTest class that does not create a context (no locations) but asks for an assignment (should
     * cause an exception).
     */
    private class SpringTestContextCreatedInSuperClass extends SpringTest {

        @SpringApplicationContext
        protected ApplicationContext field = null;
    }


}
