/*
 * Copyright 2013 Aleksandr Mashchenko.
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
package com.amashchenko.struts2.actionflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for ActionFlowInterceptor.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
@RunWith(Parameterized.class)
public class ActionFlowInterceptorTest extends
        StrutsJUnit4TestCase<ActionFlowInterceptor> {

    /** Package namespace. */
    private String packageNamespace;
    /** Next action name. */
    private String nextActionName;
    /** Previous action name. */
    private String prevActionName;
    private String expectedWrongOrderAction;

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /** Key for previous flow action. */
    private static final String PREVIOUS_FLOW_ACTION = "actionFlowPreviousAction";
    /** Expression for getting previous flow action from session. */
    private static final String SESSION_PREVIOUS_FLOW_ACTION = "#session['"
            + PREVIOUS_FLOW_ACTION + "']";

    /** Initialize method. */
    @BeforeClass
    public static void init() {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.FINE);
        if (logger.getHandlers().length > 0) {
            logger.removeHandler(logger.getHandlers()[0]);
        }
    }

    /** Parameters to use. */
    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
                { "/correctFlow", "/next", "/prev", "saveName" },
                { "/correctFlowOverride", "/nextOverride", "/prevOverride",
                        null }, };
        return Arrays.asList(data);
    }

    /**
     * Parameterized constructor.
     * 
     * @param packageNamespace
     *            package namespace.
     * @param nextActionName
     *            next action name.
     * @param prevActionName
     *            previous action name.
     */
    public ActionFlowInterceptorTest(final String packageNamespace,
            final String nextActionName, final String prevActionName,
            final String expectedWrongOrderAction) {
        this.packageNamespace = packageNamespace;
        this.nextActionName = nextActionName;
        this.prevActionName = prevActionName;
        this.expectedWrongOrderAction = expectedWrongOrderAction;
    }

    /**
     * Tests next action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testNext() throws Exception {
        executeAction(packageNamespace + packageNamespace);
        initServletMockObjects();
        executeAction(packageNamespace + nextActionName);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);
    }

    /**
     * Tests first previous action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testFirstPrev() throws Exception {
        executeAction(packageNamespace + packageNamespace);
        initServletMockObjects();
        executeAction(packageNamespace + prevActionName);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("firstFlowAction", previousAction);
    }

    /**
     * Tests previous action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testPrev() throws Exception {
        executeAction(packageNamespace + packageNamespace);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "savePhone");
        executeAction(packageNamespace + prevActionName);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);
    }

    /**
     * Tests last next action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testLastNext() throws Exception {
        executeAction(packageNamespace + packageNamespace);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "savePhone");
        executeAction(packageNamespace + nextActionName);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals(null, previousAction);
    }

    /**
     * Tests step parameter mismatch.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testStepParameterMismatch() throws Exception {
        executeAction(packageNamespace + packageNamespace);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "savePhone");
        request.setParameter("step", "");
        executeAction(packageNamespace + nextActionName);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);
    }

    /**
     * Tests wrong flow action order.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testWrongFlowOrder() throws Exception {
        executeAction(packageNamespace + packageNamespace);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "saveName");
        executeAction(packageNamespace + "/saveEmail");
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals(expectedWrongOrderAction, previousAction);
    }
}
