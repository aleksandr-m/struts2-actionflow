/*
 * Copyright 2013-2015 Aleksandr Mashchenko.
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

/**
 * Tests for action flow scope.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowScopeTest extends
        StrutsJUnit4TestCase<MockActionFlowAction> {

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /**
     * Tests getting values from scope.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testGettingFromScopeInViewAction() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctFlow/savePhone-2View");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);
        Assert.assertTrue(MockActionFlowAction.class
                .isAnnotationPresent(ActionFlowScope.class));

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        Map<String, Object> scopeMap = new HashMap<String, Object>();

        // key for 'phone' field
        final String key = MockActionFlowAction.mockPropertyDescriptorPhone()
                .getReadMethod().toString();

        scopeMap.put(key, value);
        sessionMap.put(TestConstants.FLOW_SCOPE_KEY, scopeMap);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(value, action.getPhone());
    }

    /**
     * Tests getting values from scope.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testGettingFromScopeInFlowAction() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctFlow/saveEmail-3");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);
        Assert.assertTrue(MockActionFlowAction.class
                .isAnnotationPresent(ActionFlowScope.class));

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        Map<String, Object> scopeMap = new HashMap<String, Object>();

        // key for 'phone' field
        final String key = MockActionFlowAction.mockPropertyDescriptorPhone()
                .getReadMethod().toString();

        scopeMap.put(key, value);
        sessionMap.put(TestConstants.FLOW_SCOPE_KEY, scopeMap);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(value, action.getPhone());
    }

    /**
     * Tests setting values to scope.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
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
        Assert.assertTrue(MockActionFlowAction.class
                .isAnnotationPresent(ActionFlowScope.class));

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone(value);

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(TestConstants.PREVIOUS_FLOW_ACTION, "saveName-1");
        sessionMap.put(TestConstants.HIGHEST_CURRENT_ACTION_INDEX, 2);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertNotNull(sessionMap.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertTrue(sessionMap.get(TestConstants.FLOW_SCOPE_KEY) instanceof Map);
        Assert.assertTrue(((Map<String, Object>) sessionMap
                .get(TestConstants.FLOW_SCOPE_KEY)).containsValue(value));
    }

    /**
     * Tests setting and getting values to and from scope.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSettingAndGettingFromScope() throws Exception {
        executeAction("/correctActionExtends/correctActionExtends");
        initServletMockObjects();

        final String value = "phoneFromFlowScope";

        ActionProxy ap = getActionProxy("/correctActionExtends/savePhone-2");

        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getAction());
        Assert.assertTrue(ap.getAction() instanceof MockActionFlowAction);
        Assert.assertTrue(MockActionFlowAction.class
                .isAnnotationPresent(ActionFlowScope.class));

        MockActionFlowAction action = (MockActionFlowAction) ap.getAction();
        action.setPhone(value);

        Map<String, Object> sessionMap = new HashMap<String, Object>();
        sessionMap.put(TestConstants.PREVIOUS_FLOW_ACTION, "saveName-1");
        sessionMap.put(TestConstants.HIGHEST_CURRENT_ACTION_INDEX, 2);
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertNotNull(sessionMap.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertTrue(sessionMap.get(TestConstants.FLOW_SCOPE_KEY) instanceof Map);
        Assert.assertTrue(((Map<String, Object>) sessionMap
                .get(TestConstants.FLOW_SCOPE_KEY)).containsValue(value));

        ActionProxy ap2 = getActionProxy("/correctActionExtends/saveEmail-3View");
        ap2.getInvocation().getInvocationContext().setSession(sessionMap);

        Assert.assertNotNull(ap2);
        Assert.assertNotNull(ap2.getAction());
        Assert.assertTrue(ap2.getAction() instanceof MockActionFlowAction);

        MockActionFlowAction action2 = (MockActionFlowAction) ap2.getAction();

        Assert.assertNull(action2.getPhone());

        ap2.execute();

        Assert.assertEquals(value, action2.getPhone());
    }

    /**
     * Tests clearing scope on start.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
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
        sessionMap.put(TestConstants.FLOW_SCOPE_KEY,
                new HashMap<String, Object>());
        ap.getInvocation().getInvocationContext().setSession(sessionMap);

        ap.execute();

        Assert.assertEquals(null,
                sessionMap.get(TestConstants.PREVIOUS_FLOW_ACTION));
        Assert.assertNull(sessionMap.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertEquals(immutableValue, sessionMap.get(immutableValue));
    }

    /**
     * Tests clearing scope on last flow action.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
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
