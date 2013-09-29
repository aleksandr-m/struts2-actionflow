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
import com.opensymphony.xwork2.ActionProxy;

public class ActionFlowScopeTest extends
        StrutsJUnit4TestCase<MockActionFlowAction> {

    /** Key for previous flow action. */
    private static final String PREVIOUS_FLOW_ACTION = "actionFlowPreviousAction";

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    @Test
    public void testGettingFormScope() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctFlow/savePhoneView");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(ActionFlowInterceptor.FLOW_SCOPE_PREFIX
                + action.getClass().getSimpleName() + ".phone", value);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(value, action.getPhone());
    }

    @Test
    public void testSettingToScope() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctFlow/savePhone");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone(value);

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(PREVIOUS_FLOW_ACTION, "saveName");
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(
                value,
                sessionMap.get(ActionFlowInterceptor.FLOW_SCOPE_PREFIX
                        + action.getClass().getSimpleName() + ".phone"));
    }

    @Test
    public void testClearFlowScope() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String immutableValue = "immutableValue";

        ActionProxy ap = getActionProxy("/correctFlow/saveEmail");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone("someValue");

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(immutableValue, immutableValue);
        sessionMap.put(PREVIOUS_FLOW_ACTION, "savePhone");
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(
                null,
                sessionMap.get(ActionFlowInterceptor.FLOW_SCOPE_PREFIX
                        + action.getClass().getSimpleName() + ".phone"));
        Assert.assertEquals(immutableValue, sessionMap.get(immutableValue));
    }
}
