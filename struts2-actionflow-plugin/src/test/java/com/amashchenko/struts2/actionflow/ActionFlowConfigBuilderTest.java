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
import org.junit.Assert;
import org.junit.Test;

import com.amashchenko.struts2.actionflow.entities.ActionFlowStepConfig;
import com.amashchenko.struts2.actionflow.mock.MockActionFlowAction;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Tests for ActionFlowConfigBuilder.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowConfigBuilderTest extends
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
     * Tests modifying of action flow configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
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
            map.remove("saveName");
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
     *             when something goes wrong.
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
}
