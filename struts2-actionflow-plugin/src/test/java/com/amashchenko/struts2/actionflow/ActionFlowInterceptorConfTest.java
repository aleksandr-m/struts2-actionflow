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
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Test for creating action flow map.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowInterceptorConfTest extends StrutsJUnit4TestCase<Object> {

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    @Test(expected = ConfigurationException.class)
    public void testNoInputResult() throws Exception {
        ActionProxy proxy = getActionProxy("/noInputResult/noInputResult");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testSameFlowSteps() throws Exception {
        ActionProxy proxy = getActionProxy("/sameFlowSteps/sameFlowSteps");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongNextAction() throws Exception {
        ActionProxy proxy = getActionProxy("/wrongNextAction/wrongNextAction");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongTypeNextAction() throws Exception {
        ActionProxy proxy = getActionProxy("/wrongTypeNextAction/wrongTypeNextAction");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongNextParam() throws Exception {
        ActionProxy proxy = getActionProxy("/wrongNextParam/wrongNextParam");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testNoPrevAction() throws Exception {
        ActionProxy proxy = getActionProxy("/noPrevAction/noPrevAction");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongPrevParam() throws Exception {
        ActionProxy proxy = getActionProxy("/wrongPrevParam/wrongPrevParam");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testPrevNoSuccess() throws Exception {
        ActionProxy proxy = getActionProxy("/prevNoSuccess/prevNoSuccess");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testNoViewResult() throws Exception {
        ActionProxy proxy = getActionProxy("/noViewResult/noViewResult");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongViewResultType() throws Exception {
        ActionProxy proxy = getActionProxy("/wrongViewResultType/wrongViewResultType");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongViewResultParam() throws Exception {
        ActionProxy proxy = getActionProxy("/wrongViewResultParam/wrongViewResultParam");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }

    @Test
    public void testCreateFlowMap() throws Exception {
        ActionFlowInterceptor interceptor = new ActionFlowInterceptor();
        injectStrutsDependencies(interceptor);

        Assert.assertNotNull(interceptor);

        Map<String, Map<String, String>> map = interceptor
                .createFlowMap("correctFlow");

        Assert.assertNotNull(map);

        // check saveName
        String action = "saveName";

        Assert.assertTrue(map.containsKey(action));
        Assert.assertTrue(map.get(action).containsKey(
                ActionFlowInterceptor.NEXT_ACTION_PARAM));
        Assert.assertTrue(map.get(action).containsKey(
                ActionFlowInterceptor.PREV_ACTION_PARAM));
        Assert.assertEquals("savePhone",
                map.get(action).get(ActionFlowInterceptor.NEXT_ACTION_PARAM));
        Assert.assertEquals("firstFlowAction",
                map.get(action).get(ActionFlowInterceptor.PREV_ACTION_PARAM));

        // check savePhone
        action = "savePhone";

        Assert.assertTrue(map.containsKey(action));
        Assert.assertTrue(map.get(action).containsKey(
                ActionFlowInterceptor.NEXT_ACTION_PARAM));
        Assert.assertTrue(map.get(action).containsKey(
                ActionFlowInterceptor.PREV_ACTION_PARAM));
        Assert.assertEquals("saveEmail",
                map.get(action).get(ActionFlowInterceptor.NEXT_ACTION_PARAM));
        Assert.assertEquals("saveName",
                map.get(action).get(ActionFlowInterceptor.PREV_ACTION_PARAM));

        // check saveEmail
        action = "saveEmail";

        Assert.assertTrue(map.containsKey(action));
        Assert.assertTrue(map.get(action).containsKey(
                ActionFlowInterceptor.NEXT_ACTION_PARAM));
        Assert.assertTrue(map.get(action).containsKey(
                ActionFlowInterceptor.PREV_ACTION_PARAM));
        Assert.assertEquals(null,
                map.get(action).get(ActionFlowInterceptor.NEXT_ACTION_PARAM));
        Assert.assertEquals("savePhone",
                map.get(action).get(ActionFlowInterceptor.PREV_ACTION_PARAM));
    }

    @Test
    public void testCorrectFlowOverride() throws Exception {
        ActionProxy proxy = getActionProxy("/correctFlowOverride/correctFlowOverride");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();

        ActionConfig actionConfig = configuration.getRuntimeConfiguration()
                .getActionConfig("/correctFlowOverride", "savePhoneView");

        Assert.assertNotNull(actionConfig);
        Assert.assertEquals("phone", actionConfig.getMethodName());
        Assert.assertEquals(
                "anotherPhone",
                actionConfig.getResults().get(Action.SUCCESS).getParams()
                        .get(ServletDispatcherResult.DEFAULT_PARAM));
    }

    @Test
    public void testNoFlow() throws Exception {
        ActionProxy proxy = getActionProxy("/noFlow/noFlow");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }
}
