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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.inject.Inject;

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

        Map<String, Map<String, String>> map = flowConfigBuilder.createFlowMap(
                "correctFlow", "next", "prev", "View", "execute");

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

        try {
            map.get("saveName").put("key", "value");
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The map must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }

        try {
            map.get("saveName").put("key", "value");
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            Assert.assertTrue(
                    "The map must be unmodifiable. Should throw UnsupportedOperationException.",
                    e instanceof UnsupportedOperationException);
        }
    }

    /**
     * Tests that action flow index is integer.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testIndexIsInteger() throws Exception {
        injectStrutsDependencies(this);
        Assert.assertNotNull(flowConfigBuilder);

        Map<String, Map<String, String>> map = flowConfigBuilder.createFlowMap(
                "correctFlow", "next", "prev", "View", "execute");

        Assert.assertNotNull(map);

        for (Entry<String, Map<String, String>> entry : map.entrySet()) {
            String indexStr = entry.getValue().get(
                    ActionFlowInterceptor.ACTION_FLOW_INDEX);
            Assert.assertNotNull(Integer.parseInt(indexStr));
        }
    }
}
