/*
 * Copyright 2013-2014 Aleksandr Mashchenko.
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
import com.amashchenko.struts2.actionflow.test.TestConstants;
import com.opensymphony.xwork2.ActionProxy;

public class ActionFlowScopeTest extends
        StrutsJUnit4TestCase<MockActionFlowAction> {

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    @Test
    public void testGettingFromScope() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctFlow/savePhone-2View");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        Map<String, Object> scopeMap = new HashMap<String, Object>();
        scopeMap.put(action.getClass().getName() + ".phone", value);
        sessionMap.put(TestConstants.FLOW_SCOPE_KEY, scopeMap);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(value, action.getPhone());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSettingToScope() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctFlow/savePhone-2");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone(value);

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(TestConstants.PREVIOUS_FLOW_ACTION, "saveName-1");
        sessionMap.put(TestConstants.HIGHEST_CURRENT_ACTION_INDEX, 2);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertNotNull(sessionMap.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertEquals(value, ((Map<String, Object>) sessionMap
                .get(TestConstants.FLOW_SCOPE_KEY)).get(action.getClass()
                .getName() + ".phone"));
    }

    @Test
    public void testClearFlowScopeStart() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        executeAction("/correctFlow/next");
        initServletMockObjects();

        final String immutableValue = "immutableValue";

        ActionProxy ap = getActionProxy("/correctFlow/correctFlow");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone("someValue");

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(immutableValue, immutableValue);
        sessionMap.put(TestConstants.PREVIOUS_FLOW_ACTION, "savePhone-2");
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(null,
                sessionMap.get(TestConstants.PREVIOUS_FLOW_ACTION));
        Assert.assertNull(sessionMap.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertEquals(immutableValue, sessionMap.get(immutableValue));
    }

    @Test
    public void testClearFlowScopeLast() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String immutableValue = "immutableValue";

        ActionProxy ap = getActionProxy("/correctFlow/saveEmail-3");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone("someValue");

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(immutableValue, immutableValue);
        sessionMap.put(TestConstants.PREVIOUS_FLOW_ACTION, "savePhone-2");
        sessionMap.put(TestConstants.HIGHEST_CURRENT_ACTION_INDEX, 3);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(null,
                sessionMap.get(TestConstants.PREVIOUS_FLOW_ACTION));
        Assert.assertNull(sessionMap.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertEquals(immutableValue, sessionMap.get(immutableValue));
    }
}
