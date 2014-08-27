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

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.junit.Assert;
import org.junit.Test;

import com.amashchenko.struts2.actionflow.entities.ActionFlowStepConfig;
import com.amashchenko.struts2.actionflow.mock.MockActionFlowAction;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Tests for ActionFlowConfigBuilder with configuration.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowConfigBuilderConfTest extends
        StrutsJUnit4TestCase<ActionFlowConfigBuilder> {

    /** Action flow configuration builder. */
    @Inject
    private ActionFlowConfigBuilder flowConfigBuilder;

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /**
     * Tests injecting of action flow configuration builder.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testInjectingConfigBuilder() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);
    }

    /**
     * Tests modifying of action flow configuration.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testModifyingFlowConfig() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);

        Map<String, ActionFlowStepConfig> map = flowConfigBuilder
                .createFlowMap("correctFlow", "next", "prev", "View", "execute");

        Assert.assertNotNull(map);

        try {
            map.put("key", null);
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The map must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }

        try {
            map.remove("saveName-1");
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The map must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }
    }

    /**
     * Tests modifying of action flow scope fields configuration.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testModifyingFlowScopeConfig() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);

        Map<String, List<PropertyDescriptor>> map = flowConfigBuilder
                .createFlowScopeFields("correctFlow");

        Assert.assertNotNull(map);

        try {
            map.put("key", null);
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The map must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }

        try {
            map.remove(MockActionFlowAction.class.getName());
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The map must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }

        List<PropertyDescriptor> list = map.get(MockActionFlowAction.class
                .getName());

        Assert.assertNotNull(list);

        try {
            list.add(new PropertyDescriptor("phone", MockActionFlowAction.class));
            Assert.fail("The list must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The list must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }

        try {
            list.remove(0);
            Assert.fail("The list must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The list must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }
    }

    /**
     * Tests creating of 'view' global result.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testCreatingViewGlobalResult() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);
        Assert.assertNotNull(configuration);

        final String packageName = "correctNoViewResult";
        Map<String, ActionFlowStepConfig> map = flowConfigBuilder
                .createFlowMap(packageName, "next", "prev", "View", "execute");

        Assert.assertNotNull(map);

        final PackageConfig packageConfig = configuration
                .getPackageConfig(packageName);

        Assert.assertNotNull(packageConfig);

        final Map<String, ResultConfig> globalResults = packageConfig
                .getGlobalResultConfigs();

        Assert.assertNotNull(globalResults);
        Assert.assertFalse(globalResults.isEmpty());

        Assert.assertTrue(globalResults
                .containsKey(ActionFlowInterceptor.GLOBAL_VIEW_RESULT));

        final ResultConfig viewGlobalResult = globalResults
                .get(ActionFlowInterceptor.GLOBAL_VIEW_RESULT);

        Assert.assertNotNull(viewGlobalResult);

        // name
        Assert.assertEquals(ActionFlowInterceptor.GLOBAL_VIEW_RESULT,
                viewGlobalResult.getName());
        // type
        Assert.assertEquals(ServletActionRedirectResult.class.getName(),
                viewGlobalResult.getClassName());

        // parameters
        Assert.assertNotNull(viewGlobalResult.getParams());
        Assert.assertFalse(viewGlobalResult.getParams().isEmpty());

        Assert.assertTrue(viewGlobalResult.getParams().containsKey(
                ServletActionRedirectResult.DEFAULT_PARAM));

        Assert.assertEquals(
                "${" + ActionFlowInterceptor.VIEW_ACTION_PARAM + "}",
                viewGlobalResult.getParams().get(
                        ServletActionRedirectResult.DEFAULT_PARAM));
    }

    /**
     * Tests creating of 'previous' action.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testCreatingPreviousAction() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);
        Assert.assertNotNull(configuration);

        final String packageName = "correctNoViewResult";
        final String prevActionName = "prevActionName";
        Map<String, ActionFlowStepConfig> map = flowConfigBuilder
                .createFlowMap(packageName, "next", prevActionName, "View",
                        "execute");

        Assert.assertNotNull(map);

        final PackageConfig packageConfig = configuration
                .getPackageConfig(packageName);

        Assert.assertNotNull(packageConfig);

        final Map<String, ActionConfig> actionConfs = packageConfig
                .getActionConfigs();

        Assert.assertNotNull(actionConfs);
        Assert.assertFalse(actionConfs.isEmpty());

        Assert.assertTrue(actionConfs.containsKey(prevActionName));

        final ActionConfig actionConf = actionConfs.get(prevActionName);

        Assert.assertNotNull(actionConf);

        // name
        Assert.assertEquals(prevActionName, actionConf.getName());
        // method
        Assert.assertEquals(null, actionConf.getMethodName());
        // class
        Assert.assertEquals("", actionConf.getClassName());

        // result
        Assert.assertNotNull(actionConf.getResults());
        Assert.assertFalse(actionConf.getResults().isEmpty());
        Assert.assertEquals(1, actionConf.getResults().size());
        Assert.assertTrue(actionConf.getResults().containsKey(Action.SUCCESS));

        final ResultConfig actionConfResult = actionConf.getResults().get(
                Action.SUCCESS);

        Assert.assertNotNull(actionConfResult);

        // result name
        Assert.assertEquals(Action.SUCCESS, actionConfResult.getName());
        // result type
        Assert.assertEquals(ServletActionRedirectResult.class.getName(),
                actionConfResult.getClassName());

        // parameters
        Assert.assertNotNull(actionConfResult.getParams());
        Assert.assertFalse(actionConfResult.getParams().isEmpty());

        Assert.assertTrue(actionConfResult.getParams().containsKey(
                ServletActionRedirectResult.DEFAULT_PARAM));

        Assert.assertEquals(
                "${" + ActionFlowInterceptor.PREV_ACTION_PARAM + "}",
                actionConfResult.getParams().get(
                        ServletActionRedirectResult.DEFAULT_PARAM));
    }

    /**
     * Tests creating of 'next' action.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testCreatingNextAction() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);
        Assert.assertNotNull(configuration);

        final String packageName = "correctNoViewResult";
        final String nextActionName = "nextActionName";
        Map<String, ActionFlowStepConfig> map = flowConfigBuilder
                .createFlowMap(packageName, nextActionName, "prev", "View",
                        "execute");

        Assert.assertNotNull(map);

        final PackageConfig packageConfig = configuration
                .getPackageConfig(packageName);

        Assert.assertNotNull(packageConfig);

        final Map<String, ActionConfig> actionConfs = packageConfig
                .getActionConfigs();

        Assert.assertNotNull(actionConfs);
        Assert.assertFalse(actionConfs.isEmpty());

        Assert.assertTrue(actionConfs.containsKey(nextActionName));

        final ActionConfig actionConf = actionConfs.get(nextActionName);

        Assert.assertNotNull(actionConf);

        // name
        Assert.assertEquals(nextActionName, actionConf.getName());
        // method
        Assert.assertEquals(null, actionConf.getMethodName());
        // class
        Assert.assertEquals("", actionConf.getClassName());

        // result
        Assert.assertNotNull(actionConf.getResults());
        Assert.assertFalse(actionConf.getResults().isEmpty());
        Assert.assertEquals(1, actionConf.getResults().size());
        Assert.assertTrue(actionConf.getResults().containsKey(Action.SUCCESS));

        final ResultConfig actionConfResult = actionConf.getResults().get(
                Action.SUCCESS);

        Assert.assertNotNull(actionConfResult);

        // result name
        Assert.assertEquals(Action.SUCCESS, actionConfResult.getName());
        // result type
        Assert.assertEquals(ActionChainResult.class.getName(),
                actionConfResult.getClassName());

        // parameters
        Assert.assertNotNull(actionConfResult.getParams());
        Assert.assertFalse(actionConfResult.getParams().isEmpty());

        Assert.assertTrue(actionConfResult.getParams().containsKey(
                ActionChainResult.DEFAULT_PARAM));

        Assert.assertEquals(
                "${" + ActionFlowInterceptor.NEXT_ACTION_PARAM + "}",
                actionConfResult.getParams().get(
                        ActionChainResult.DEFAULT_PARAM));
    }
}
