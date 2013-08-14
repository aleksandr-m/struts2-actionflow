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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for ActionFlowInterceptor.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowInterceptorTest extends
        StrutsJUnit4TestCase<ActionFlowInterceptor> {

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /** Key for previous flow action. */
    private static final String PREV_FLOW_ACTION = "com.amashchenko.struts2.actionflow.ActionFlowInterceptor.prevFlowAction";
    /** Expression for getting previous flow action from session. */
    private static final String SESSION_PREV_FLOW_ACTION = "#session['"
            + PREV_FLOW_ACTION + "']";

    /**
     * Initialize method.
     * 
     */
    @BeforeClass
    public static void init() {
        Logger.getLogger("").setLevel(Level.FINE);
    }

    /**
     * Tests next action.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testNext() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();
        executeAction("/correctFlow/next");
        String previousAction = (String) findValueAfterExecute(SESSION_PREV_FLOW_ACTION);
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
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();
        executeAction("/correctFlow/prev");
        String previousAction = (String) findValueAfterExecute(SESSION_PREV_FLOW_ACTION);
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
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();
        request.getSession().setAttribute(PREV_FLOW_ACTION, "savePhone");
        executeAction("/correctFlow/prev");
        String previousAction = (String) findValueAfterExecute(SESSION_PREV_FLOW_ACTION);
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
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();
        request.getSession().setAttribute(PREV_FLOW_ACTION, "savePhone");
        executeAction("/correctFlow/next");
        String previousAction = (String) findValueAfterExecute(SESSION_PREV_FLOW_ACTION);
        Assert.assertEquals(null, previousAction);
    }

    /**
     * Tests wrong flow action order.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testWrongFlowOrder() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();
        request.getSession().setAttribute(PREV_FLOW_ACTION, "saveName");
        executeAction("/correctFlow/saveEmail");
        String previousAction = (String) findValueAfterExecute(SESSION_PREV_FLOW_ACTION);
        Assert.assertEquals("saveName", previousAction);
    }

    /**
     * Tests wrong flow action force order set to false.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testWrongFlowOrderForceFalse() throws Exception {
        executeAction("/correctFlowOverride/correctFlowOverride");
        initServletMockObjects();
        request.getSession().setAttribute(PREV_FLOW_ACTION, "saveName");
        executeAction("/correctFlowOverride/saveEmail");
        String previousAction = (String) findValueAfterExecute(SESSION_PREV_FLOW_ACTION);
        Assert.assertEquals(null, previousAction);
    }
}
