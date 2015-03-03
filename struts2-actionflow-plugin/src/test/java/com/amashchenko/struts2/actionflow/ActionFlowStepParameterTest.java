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

import javax.servlet.http.HttpSession;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import com.amashchenko.struts2.actionflow.test.TestConstants;

/**
 * Tests for forcing order of flow actions.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowStepParameterTest extends
        StrutsJUnit4TestCase<ActionFlowInterceptor> {

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /** Initializes servlet mock objects but preserves session. */
    private void initServletMockObjectsPreserveSession() {
        servletContext = new MockServletContext(resourceLoader);
        response = new MockHttpServletResponse();

        // preserve session
        HttpSession session = null;
        if (request != null && request.getSession() != null) {
            session = request.getSession();
        }
        request = new MockHttpServletRequest();
        request.setSession(session);

        pageContext = new MockPageContext(servletContext, request, response);
    }

    // next, step from saveName-1 to savePhone-2
    @Test
    public void testChangingStepParam() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        String previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName-1", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName-1", nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhone-2View", viewActionParam);

        initServletMockObjectsPreserveSession();
        // set wrong order step parameter
        request.setParameter("step", "savePhone-2");
        executeAction("/correctFlow/next");

        previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName-1", previousAction);

        prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhone-2View", viewActionParam);
    }

    // next, saveEmail-3
    @Test
    public void testWrongOrderAction() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        String previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName-1", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName-1", nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhone-2View", viewActionParam);

        initServletMockObjectsPreserveSession();
        // execute action in wrong order
        executeAction("/correctFlow/saveEmail-3");

        previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName-1", previousAction);

        nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName-1", nextActionParam);

        prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhone-2View", viewActionParam);
    }

    // next, prev, back, next
    @Test
    public void testPrevWithBack() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        String previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("saveName-1", previousAction);

        String nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName-1", nextActionParam);

        String prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals(null, prevActionParam);

        String viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhone-2View", viewActionParam);

        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/prev");

        previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals(ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME,
                previousAction);

        nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("saveName-1", nextActionParam);

        prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals("saveName-1View", prevActionParam);

        viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("savePhone-2View", viewActionParam);

        initServletMockObjectsPreserveSession();
        // simulate browser back button
        request.setParameter("step", "saveName-1");
        executeAction("/correctFlow/next");

        previousAction = (String) findValueAfterExecute(TestConstants.SESSION_PREVIOUS_FLOW_ACTION);
        Assert.assertEquals("savePhone-2", previousAction);

        nextActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.NEXT_ACTION_PARAM);
        Assert.assertEquals("savePhone-2", nextActionParam);

        prevActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.PREV_ACTION_PARAM);
        Assert.assertEquals("saveName-1View", prevActionParam);

        viewActionParam = (String) findValueAfterExecute(ActionFlowInterceptor.VIEW_ACTION_PARAM);
        Assert.assertEquals("saveEmail-3View", viewActionParam);
    }

    @Test
    public void testHighestCurrentActionIndex() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        Integer highestCurrentIndex = (Integer) findValueAfterExecute(TestConstants.SESSION_HIGHEST_CURRENT_ACTION_INDEX);
        Assert.assertEquals(new Integer(1), highestCurrentIndex);

        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        highestCurrentIndex = (Integer) findValueAfterExecute(TestConstants.SESSION_HIGHEST_CURRENT_ACTION_INDEX);
        Assert.assertEquals(new Integer(2), highestCurrentIndex);
    }

    @Test
    public void testHighestCurrentActionIndexInput() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjectsPreserveSession();
        // for input result
        request.setParameter("date", "wrong-date-format");
        executeAction("/correctFlow/next");
        Integer highestCurrentIndex = (Integer) findValueAfterExecute(TestConstants.SESSION_HIGHEST_CURRENT_ACTION_INDEX);
        Assert.assertEquals(null, highestCurrentIndex);
    }

    @Test
    public void testHighestCurrentActionIndexAndPrev() throws Exception {
        executeAction("/correctFlow/correctFlow");
        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        Integer highestCurrentIndex = (Integer) findValueAfterExecute(TestConstants.SESSION_HIGHEST_CURRENT_ACTION_INDEX);
        Assert.assertEquals(new Integer(1), highestCurrentIndex);

        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/prev");
        highestCurrentIndex = (Integer) findValueAfterExecute(TestConstants.SESSION_HIGHEST_CURRENT_ACTION_INDEX);
        Assert.assertEquals(new Integer(1), highestCurrentIndex);

        initServletMockObjectsPreserveSession();
        executeAction("/correctFlow/next");
        highestCurrentIndex = (Integer) findValueAfterExecute(TestConstants.SESSION_HIGHEST_CURRENT_ACTION_INDEX);
        Assert.assertEquals(new Integer(1), highestCurrentIndex);
    }
}
