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

    /** Suffix to append. */
    private String suffix;
    /** Expected wrong order action name. */
    private String expectedWrongOrderAction;
    /** Expected wrong order view action parameter value. */
    private String expectedWrongOrderViewParam;

    /** Namespace. */
    private String namespace;
    /** Starting action. */
    private String startingAction;
    /** Next action. */
    private String nextAction;
    /** Previous action. */
    private String prevAction;

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

    /**
     * Parameters to use.
     * 
     * @return Parameters as collection.
     */
    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { "", "saveName", "savePhoneView" },
                { "Override", null, null }, };
        return Arrays.asList(data);
    }

    /**
     * Parameterized constructor.
     * 
     * @param suffix
     *            suffix.
     * @param expectedWrongOrderAction
     *            expected wrong order action name.
     * @param expectedWrongOrderViewParam
     *            expected wrong order view action parameter value.
     */
    public ActionFlowInterceptorTest(final String suffix,
            final String expectedWrongOrderAction,
            final String expectedWrongOrderViewParam) {
        this.suffix = suffix;
        this.expectedWrongOrderAction = expectedWrongOrderAction;
        this.expectedWrongOrderViewParam = expectedWrongOrderViewParam;

        this.namespace = "/correctFlow" + suffix;
        this.startingAction = namespace + "/correctFlow" + suffix;
        this.nextAction = namespace + "/next" + suffix;
        this.prevAction = namespace + "/prev" + suffix;
    }

    /**
     * Tests next action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testNext() throws Exception {
        executeAction(startingAction);
        initServletMockObjects();
        executeAction(nextAction);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName", nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhoneView" + suffix, viewActionParam);
    }

    /**
     * Tests first previous action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testFirstPrev() throws Exception {
        executeAction(startingAction);
        initServletMockObjects();
        executeAction(prevAction);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("firstFlowAction", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals(null, nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals("saveNameView" + suffix, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals(null, viewActionParam);
    }

    /**
     * Tests previous action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testPrev() throws Exception {
        executeAction(startingAction);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "savePhone");
        executeAction(prevAction);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals(null, nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals("savePhoneView" + suffix, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals(null, viewActionParam);
    }

    /**
     * Tests last next action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testLastNext() throws Exception {
        executeAction(startingAction);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "savePhone");
        executeAction(nextAction);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals(null, previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveEmail", nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals(null, viewActionParam);
    }

    /**
     * Tests step parameter mismatch.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testStepParameterMismatch() throws Exception {
        executeAction(startingAction);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "savePhone");
        request.setParameter("step" + suffix, "");
        executeAction(nextAction);
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName", nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhoneView" + suffix, viewActionParam);
    }

    /**
     * Tests wrong flow action order.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testWrongFlowOrder() throws Exception {
        executeAction(startingAction);
        initServletMockObjects();
        request.getSession().setAttribute(PREVIOUS_FLOW_ACTION, "saveName");
        executeAction(namespace + "/saveEmail");
        String previousAction = (String) findValueAfterExecute(SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals(expectedWrongOrderAction, previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals(null, nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals(expectedWrongOrderViewParam, viewActionParam);
    }
}
