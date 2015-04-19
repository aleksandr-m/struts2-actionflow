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
package com.amashchenko.struts2.actionflow.entities;

import org.junit.Assert;
import org.junit.Test;

public class ActionFlowStepConfigTest {
    /**
     * Tests building action flow step configurations from the same builder.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testBuildingFromSameBuilder() throws Exception {
        ActionFlowStepConfig.Builder stepConfigBuilder = new ActionFlowStepConfig.Builder(
                0, "next", "prev");
        ActionFlowStepConfig stepConfig = stepConfigBuilder.build();

        Assert.assertNotNull(stepConfig);

        ActionFlowStepConfig stepConfig2 = stepConfigBuilder.build();

        Assert.assertNotNull(stepConfig2);

        Assert.assertNotSame(stepConfig, stepConfig2);
        Assert.assertEquals(stepConfig, stepConfig2);

        Assert.assertEquals(stepConfig.getIndex(), stepConfig2.getIndex());
        Assert.assertEquals(stepConfig.getNextAction(),
                stepConfig2.getNextAction());
        Assert.assertEquals(stepConfig.getPrevAction(),
                stepConfig2.getPrevAction());
    }

    /**
     * Tests cloning action flow step configurations.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testClone() throws Exception {
        ActionFlowStepConfig.Builder stepConfigBuilder = new ActionFlowStepConfig.Builder(
                0, "next", "prev");
        ActionFlowStepConfig stepConfig = stepConfigBuilder.build();

        Assert.assertNotNull(stepConfig);

        ActionFlowStepConfig.Builder stepConfigBuilder2 = new ActionFlowStepConfig.Builder(
                stepConfig);
        ActionFlowStepConfig stepConfig2 = stepConfigBuilder2.build();

        Assert.assertNotNull(stepConfig2);

        Assert.assertNotSame(stepConfig, stepConfig2);
        Assert.assertEquals(stepConfig, stepConfig2);
    }

    /**
     * Tests cloning and changing action flow step configurations.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testCloneAndSet() throws Exception {
        ActionFlowStepConfig.Builder stepConfigBuilder = new ActionFlowStepConfig.Builder(
                0, "next", "prev");
        ActionFlowStepConfig stepConfig = stepConfigBuilder.build();

        Assert.assertNotNull(stepConfig);

        ActionFlowStepConfig.Builder stepConfigBuilder2 = new ActionFlowStepConfig.Builder(
                stepConfig);

        ActionFlowStepConfig stepConfig2 = stepConfigBuilder2.index(0)
                .nextAction("next").prevAction("prev").build();

        Assert.assertNotNull(stepConfig2);

        Assert.assertNotSame(stepConfig, stepConfig2);
        Assert.assertEquals(stepConfig, stepConfig2);
    }

    /**
     * Tests toString method of action flow step configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testToString() throws Exception {
        ActionFlowStepConfig.Builder stepConfigBuilder = new ActionFlowStepConfig.Builder(
                0, "next", "prev");
        ActionFlowStepConfig stepConfig = stepConfigBuilder.build();

        Assert.assertEquals(
                "{ActionFlowStepConfig index:0, nextAction:next, prevAction:prev}",
                stepConfig.toString());
    }

    /**
     * Tests hashCode method of action flow step configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testHashCode() throws Exception {
        ActionFlowStepConfig.Builder stepConfigBuilder = new ActionFlowStepConfig.Builder(
                0, null, null);
        ActionFlowStepConfig stepConfig = stepConfigBuilder.build();
        ActionFlowStepConfig stepConfig2 = stepConfigBuilder.build();

        Assert.assertEquals(stepConfig.hashCode(), stepConfig2.hashCode());

        stepConfig = stepConfigBuilder.prevAction("prev").build();
        stepConfig2 = stepConfigBuilder.build();

        Assert.assertEquals(stepConfig.hashCode(), stepConfig2.hashCode());

        stepConfig = stepConfigBuilder.nextAction("next").prevAction(null)
                .build();
        stepConfig2 = stepConfigBuilder.build();

        Assert.assertEquals(stepConfig.hashCode(), stepConfig2.hashCode());
    }

    /**
     * Tests equals method of action flow step configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test
    public void testEquals() throws Exception {
        ActionFlowStepConfig.Builder stepConfigBuilder = new ActionFlowStepConfig.Builder(
                0, "next", "prev");
        ActionFlowStepConfig stepConfig = stepConfigBuilder.build();

        Assert.assertNotNull(stepConfig);

        Assert.assertEquals(stepConfig, stepConfig);
        Assert.assertNotEquals(stepConfig, null);

        ActionFlowStepConfig.Builder stepConfigBuilder2 = new ActionFlowStepConfig.Builder(
                stepConfig);
        ActionFlowStepConfig stepConfig2 = stepConfigBuilder2.index(1).build();
        Assert.assertNotEquals(stepConfig, stepConfig2);

        ActionFlowStepConfig.Builder stepConfigBuilder3 = new ActionFlowStepConfig.Builder(
                stepConfig);
        stepConfig2 = stepConfigBuilder3.nextAction("notNext").build();
        Assert.assertNotEquals(stepConfig, stepConfig2);

        ActionFlowStepConfig.Builder stepConfigBuilder4 = new ActionFlowStepConfig.Builder(
                stepConfig);
        stepConfig2 = stepConfigBuilder4.prevAction("notPrev").build();
        Assert.assertNotEquals(stepConfig, stepConfig2);

        ActionFlowStepConfig.Builder stepConfigBuilder5 = new ActionFlowStepConfig.Builder(
                stepConfig);
        stepConfig2 = stepConfigBuilder5.prevAction(null).build();
        Assert.assertNotEquals(stepConfig2, stepConfig);

        ActionFlowStepConfig.Builder stepConfigBuilder6 = new ActionFlowStepConfig.Builder(
                stepConfig);
        stepConfig2 = stepConfigBuilder6.nextAction(null).build();
        Assert.assertNotEquals(stepConfig2, stepConfig);
    }
}
