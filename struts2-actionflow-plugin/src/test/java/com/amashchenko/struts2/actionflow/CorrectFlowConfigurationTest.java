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
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * Tests for correct action flow configurations.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class CorrectFlowConfigurationTest extends StrutsJUnit4TestCase<Object> {

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /**
     * Tests creating action flow configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testCreateFlowMap() throws Exception {
        ActionFlowConfigBuilder flowConfigBuilder = new ActionFlowConfigBuilder();
        injectStrutsDependencies(flowConfigBuilder);

        Assert.assertNotNull(flowConfigBuilder);

        Map<String, Map<String, String>> map = flowConfigBuilder.createFlowMap(
                "correctFlow", "next", "prev", "View", "execute");

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

    /**
     * Tests creating action flow override configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testCorrectFlowOverride() throws Exception {
        ActionProxy proxy = getActionProxy("/correctFlowOverride/correctFlowOverride");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();

        ActionConfig overriddenViewConf = configuration
                .getRuntimeConfiguration().getActionConfig(
                        "/correctFlowOverride", "savePhoneViewOverride");

        Assert.assertNotNull(overriddenViewConf);
        Assert.assertEquals("phone", overriddenViewConf.getMethodName());
        Assert.assertEquals("anotherPhone",
                overriddenViewConf.getResults().get(Action.SUCCESS).getParams()
                        .get(ServletDispatcherResult.DEFAULT_PARAM));

        ActionConfig actionConfig = configuration
                .getRuntimeConfiguration()
                .getActionConfig("/correctFlowOverride", "saveNameViewOverride");

        Assert.assertNotNull(actionConfig);
        Assert.assertEquals("executeOverride", actionConfig.getMethodName());
        Assert.assertEquals(
                "name",
                actionConfig.getResults().get(Action.SUCCESS).getParams()
                        .get(ServletDispatcherResult.DEFAULT_PARAM));
    }

    /**
     * Tests creating empty action flow configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testNoFlow() throws Exception {
        ActionProxy proxy = getActionProxy("/noFlow/noFlow");
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }
}
