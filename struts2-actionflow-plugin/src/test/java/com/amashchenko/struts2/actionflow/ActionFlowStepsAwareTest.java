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

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.Test;

import com.amashchenko.struts2.actionflow.mock.MockActionFlowAction;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;

public class ActionFlowStepsAwareTest extends
        StrutsJUnit4TestCase<MockActionFlowAction> {

    /** Key for previous flow action. */
    private static final String PREVIOUS_FLOW_ACTION = "actionFlowPreviousAction";

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    @Test
    public void testNotActionFlowAction() throws Exception {
        final Integer stepCount = 1;

        ActionProxy ap = getActionProxy("/correctFlow/correctFlow");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);
        Assert.assertTrue(ap.getAction() instanceof ActionFlowStepsAware);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        ap.execute();

        Assert.assertNotNull(action.getStepsData());
        Assert.assertEquals(new Integer(1), action.getStepsData().getSteps()
                .firstKey());

        Assert.assertEquals(stepCount, action.getStepsData().getStepIndex());
    }

    @Test
    public void testActionFlowAction() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final Integer stepCount = 2;

        ActionProxy ap = getActionProxy("/correctFlow/savePhoneView");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);
        Assert.assertTrue(ap.getAction() instanceof ActionFlowStepsAware);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        ap.execute();

        Assert.assertNotNull(action.getStepsData());

        Assert.assertEquals(stepCount, action.getStepsData().getStepIndex());
    }

    @Test
    public void testInputResult() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final Integer stepCount = 2;

        // for the input result
        request.setParameter("date", "errorrr");

        ActionProxy ap = getActionProxy("/correctFlow/savePhone");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);
        Assert.assertTrue(ap.getAction() instanceof ActionFlowStepsAware);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(PREVIOUS_FLOW_ACTION, "saveName");
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        String resultCode = ap.execute();

        Assert.assertEquals(Action.INPUT, resultCode);

        Assert.assertNotNull(action.getStepsData());

        Assert.assertEquals(stepCount, action.getStepsData().getStepIndex());
    }
}
