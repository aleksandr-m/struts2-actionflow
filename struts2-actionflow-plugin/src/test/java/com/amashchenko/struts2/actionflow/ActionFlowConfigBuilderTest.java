package com.amashchenko.struts2.actionflow;

import java.util.Map;

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
            if (!(e instanceof UnsupportedOperationException)) {
                Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
            }
        }

        try {
            map.remove("saveName");
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            if (!(e instanceof UnsupportedOperationException)) {
                Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
            }
        }

        try {
            map.get("saveName").put("key", "value");
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            if (!(e instanceof UnsupportedOperationException)) {
                Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
            }
        }

        try {
            map.get("saveName").put("key", "value");
            Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
        } catch (Exception e) {
            if (!(e instanceof UnsupportedOperationException)) {
                Assert.fail("The map must be unmodifiable. Should throw UnsupportedOperationException.");
            }
        }
    }
}
