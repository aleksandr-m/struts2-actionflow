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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.amashchenko.struts2.actionflow.mock.MockActionFlowAction;
import com.amashchenko.struts2.actionflow.test.TestConstants;

/**
 * Tests for action flow interceptor.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowInterceptorTest {

    /** Action flow interceptor instance. */
    private ActionFlowInterceptor actionFlowInterceptor = new ActionFlowInterceptor();

    /**
     * Tests clear session method.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testClearSession() throws Exception {
        Assert.assertNotNull(actionFlowInterceptor);

        Map<String, Object> session = new HashMap<String, Object>();
        session.put(TestConstants.PREVIOUS_FLOW_ACTION, "PREVIOUS_FLOW_ACTION");
        session.put(TestConstants.FLOW_SCOPE_KEY, "FLOW_SCOPE_KEY");
        session.put(TestConstants.HIGHEST_CURRENT_ACTION_INDEX,
                "HIGHEST_CURRENT_ACTION_INDEX");
        session.put(TestConstants.SKIP_ACTIONS, "SKIP_ACTIONS");

        session.put(TestConstants.OVERRIDE_ACTION_NAME, "OVERRIDE_ACTION_NAME");

        // execute clearSession method
        actionFlowInterceptor.clearSession(session);

        Assert.assertNull(session.get(TestConstants.PREVIOUS_FLOW_ACTION));
        Assert.assertNull(session.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertNull(session
                .get(TestConstants.HIGHEST_CURRENT_ACTION_INDEX));
        Assert.assertNull(session.get(TestConstants.SKIP_ACTIONS));
        Assert.assertNull(session.get(TestConstants.OVERRIDE_ACTION_NAME));
    }

    /**
     * Tests handleFlowScope method with nulls.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testHandleFlowScopeNulls() throws Exception {
        Assert.assertNotNull(actionFlowInterceptor);

        final MockActionFlowAction action = new MockActionFlowAction();
        final Map<String, Object> session = new HashMap<String, Object>();
        final boolean fromFlowScope = true;

        // set flowScopeFields in ActionFlowInterceptor
        Field field = ActionFlowInterceptor.class
                .getDeclaredField(TestConstants.FLOW_SCOPE_FIELD_NAME);
        field.setAccessible(true);
        Map<String, List<PropertyDescriptor>> map = new HashMap<String, List<PropertyDescriptor>>();

        // flowScopeFields is null
        actionFlowInterceptor.handleFlowScope(action, session, fromFlowScope);

        field.set(actionFlowInterceptor, map);

        // action is null
        actionFlowInterceptor.handleFlowScope(null, session, fromFlowScope);
        // session is null
        actionFlowInterceptor.handleFlowScope(action, null, fromFlowScope);
    }

    /**
     * Tests handleFlowScope method setting values to scope.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHandleFlowScopeSetToScope() throws Exception {
        Assert.assertNotNull(actionFlowInterceptor);

        final MockActionFlowAction action = new MockActionFlowAction();
        final Map<String, Object> session = new HashMap<String, Object>();

        // set flowScopeFields in ActionFlowInterceptor
        injectFlowScopeFields();

        // phone in action
        final String phoneActionValue = "phoneActionValue";
        final String phoneScopeKey = MockActionFlowAction
                .mockPropertyDescriptorPhone().getReadMethod().toString();
        action.setPhone(phoneActionValue);

        // execute handleFlowScope method
        actionFlowInterceptor.handleFlowScope(action, session, false);

        Assert.assertTrue(session.containsKey(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertTrue(session.get(TestConstants.FLOW_SCOPE_KEY) instanceof Map);

        Map<String, Object> resultScopeMap = (Map<String, Object>) session
                .get(TestConstants.FLOW_SCOPE_KEY);

        Assert.assertNotNull(resultScopeMap);

        Assert.assertTrue(resultScopeMap.containsKey(phoneScopeKey));
        Assert.assertEquals(phoneActionValue, resultScopeMap.get(phoneScopeKey));

        //
        // same for wrong FLOW_SCOPE_KEY type
        session.put(TestConstants.FLOW_SCOPE_KEY, "someStringNotMap");

        // execute handleFlowScope method
        actionFlowInterceptor.handleFlowScope(action, session, false);

        Assert.assertTrue(session.containsKey(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertTrue(session.get(TestConstants.FLOW_SCOPE_KEY) instanceof Map);

        resultScopeMap = (Map<String, Object>) session
                .get(TestConstants.FLOW_SCOPE_KEY);

        Assert.assertNotNull(resultScopeMap);

        Assert.assertTrue(resultScopeMap.containsKey(phoneScopeKey));
        Assert.assertEquals(phoneActionValue, resultScopeMap.get(phoneScopeKey));
    }

    /**
     * Tests handleFlowScope method setting null values to scope.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testHandleFlowScopeSetToScopeNull() throws Exception {
        Assert.assertNotNull(actionFlowInterceptor);

        final MockActionFlowAction action = new MockActionFlowAction();
        final Map<String, Object> session = new HashMap<String, Object>();

        // set flowScopeFields in ActionFlowInterceptor
        injectFlowScopeFields();

        // phone in action is null
        action.setPhone(null);

        // execute handleFlowScope method
        actionFlowInterceptor.handleFlowScope(action, session, false);

        Assert.assertFalse(session.containsKey(TestConstants.FLOW_SCOPE_KEY));
    }

    /**
     * Tests handleFlowScope method setting values to action.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHandleFlowScopeSetToAction() throws Exception {
        Assert.assertNotNull(actionFlowInterceptor);

        final MockActionFlowAction action = new MockActionFlowAction();
        final Map<String, Object> session = new HashMap<String, Object>();

        // set flowScopeFields in ActionFlowInterceptor
        injectFlowScopeFields();

        // phone in action is null
        action.setPhone(null);

        final String phoneScopeValue = "phoneScopeValue";
        final String phoneScopeKey = MockActionFlowAction
                .mockPropertyDescriptorPhone().getReadMethod().toString();

        Map<String, Object> scopeMap = new HashMap<String, Object>();
        scopeMap.put(phoneScopeKey, phoneScopeValue);
        session.put(TestConstants.FLOW_SCOPE_KEY, scopeMap);

        // execute handleFlowScope method
        actionFlowInterceptor.handleFlowScope(action, session, true);

        // scope map from session must hold same values
        final Map<String, Object> resultScopeMap = (Map<String, Object>) session
                .get(TestConstants.FLOW_SCOPE_KEY);
        Assert.assertNotNull(resultScopeMap);
        Assert.assertTrue(resultScopeMap.containsKey(phoneScopeKey));
        Assert.assertEquals(phoneScopeValue, resultScopeMap.get(phoneScopeKey));

        // action
        Assert.assertNotNull(action);
        Assert.assertEquals(phoneScopeValue, action.getPhone());
    }

    /**
     * Tests handleFlowScope method setting values to action when action already
     * holds a value.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testHandleFlowScopeSetToActionWithValue() throws Exception {
        Assert.assertNotNull(actionFlowInterceptor);

        final MockActionFlowAction action = new MockActionFlowAction();
        final Map<String, Object> session = new HashMap<String, Object>();

        // set flowScopeFields in ActionFlowInterceptor
        injectFlowScopeFields();

        // phone in action
        final String phoneActionValue = "phoneActionValue";
        action.setPhone(phoneActionValue);

        final String phoneScopeValue = "phoneScopeValue";
        final String phoneScopeKey = MockActionFlowAction
                .mockPropertyDescriptorPhone().getReadMethod().toString();

        Map<String, Object> scopeMap = new HashMap<String, Object>();
        scopeMap.put(phoneScopeKey, phoneScopeValue);
        session.put(TestConstants.FLOW_SCOPE_KEY, scopeMap);

        // execute handleFlowScope method
        actionFlowInterceptor.handleFlowScope(action, session, true);

        // scope map from session must hold same values
        final Map<String, Object> resultScopeMap = (Map<String, Object>) session
                .get(TestConstants.FLOW_SCOPE_KEY);
        Assert.assertNotNull(resultScopeMap);
        Assert.assertTrue(resultScopeMap.containsKey(phoneScopeKey));
        Assert.assertEquals(phoneScopeValue, resultScopeMap.get(phoneScopeKey));

        // action
        Assert.assertNotNull(action);
        // phone value must be same as before
        Assert.assertEquals(phoneActionValue, action.getPhone());
    }

    /**
     * Sets value to private flowScopeFields field in ActionFlowInterceptor.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    private void injectFlowScopeFields() throws Exception {
        Field field = ActionFlowInterceptor.class
                .getDeclaredField(TestConstants.FLOW_SCOPE_FIELD_NAME);
        field.setAccessible(true);
        field.set(actionFlowInterceptor,
                MockActionFlowAction.mockFlowScopeFields());
    }
}
