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

import org.junit.Assert;
import org.junit.Test;

import com.amashchenko.struts2.actionflow.test.TestConstants;

/**
 * Test for action flow interceptor.
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

        actionFlowInterceptor.clearSession(session);

        Assert.assertNull(session.get(TestConstants.PREVIOUS_FLOW_ACTION));
        Assert.assertNull(session.get(TestConstants.FLOW_SCOPE_KEY));
        Assert.assertNull(session
                .get(TestConstants.HIGHEST_CURRENT_ACTION_INDEX));
        Assert.assertNull(session.get(TestConstants.SKIP_ACTIONS));
    }
}
