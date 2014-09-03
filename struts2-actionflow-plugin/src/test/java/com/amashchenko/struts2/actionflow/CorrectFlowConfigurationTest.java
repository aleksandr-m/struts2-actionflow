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
import org.apache.struts2.dispatcher.ServletDispatcherResult;
import org.junit.Assert;
import org.junit.Test;

import com.amashchenko.struts2.actionflow.entities.ActionFlowStepConfig;
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

        Map<String, ActionFlowStepConfig> map = flowConfigBuilder
                .createFlowMap("correctFlow", "next", "prev", "View", "execute");

        Assert.assertNotNull(map);

        // check firstFlowAction
        String action = ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME;

        Assert.assertTrue(map.containsKey(action));
        Assert.assertEquals("saveName-1", map.get(action).getNextAction());
        Assert.assertEquals(ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME, map
                .get(action).getPrevAction());
        Assert.assertEquals(0, map.get(action).getIndex());

        // check saveName
        action = "saveName-1";

        Assert.assertTrue(map.containsKey(action));
        Assert.assertEquals("savePhone-2", map.get(action).getNextAction());
        Assert.assertEquals(ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME, map
                .get(action).getPrevAction());
        Assert.assertEquals(1, map.get(action).getIndex());

        // check savePhone
        action = "savePhone-2";

        Assert.assertTrue(map.containsKey(action));
        Assert.assertEquals("saveEmail-3", map.get(action).getNextAction());
        Assert.assertEquals("saveName-1", map.get(action).getPrevAction());
        Assert.assertEquals(2, map.get(action).getIndex());

        // check saveEmail
        action = "saveEmail-3";

        Assert.assertTrue(map.containsKey(action));
        Assert.assertEquals(null, map.get(action).getNextAction());
        Assert.assertEquals("savePhone-2", map.get(action).getPrevAction());
        Assert.assertEquals(3, map.get(action).getIndex());
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
                        "/correctFlowOverride", "savePhone-2ViewOverride");

        Assert.assertNotNull(overriddenViewConf);
        Assert.assertEquals("phone", overriddenViewConf.getMethodName());
        Assert.assertEquals("anotherPhone",
                overriddenViewConf.getResults().get(Action.SUCCESS).getParams()
                        .get(ServletDispatcherResult.DEFAULT_PARAM));

        ActionConfig actionConfig = configuration.getRuntimeConfiguration()
                .getActionConfig("/correctFlowOverride",
                        "saveName-1ViewOverride");

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
