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

import org.junit.Assert;
import org.junit.Test;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Tests for action flow configuration builder.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowConfigBuilderTest {

    /** Action flow configuration builder instance. */
    private ActionFlowConfigBuilder actionFlowConfigBuilder = new ActionFlowConfigBuilder();

    /**
     * Tests findField method.
     * 
     * @throws Exception
     *             When something goes wrong.
     */
    @Test
    public void testFindField() throws Exception {
        Assert.assertNotNull(actionFlowConfigBuilder);

        // existing field
        Assert.assertNotNull(actionFlowConfigBuilder.findField(LevelOne.class,
                "name"));
        Assert.assertNotNull(actionFlowConfigBuilder.findField(LevelTwo.class,
                "name"));
        Assert.assertNotNull(actionFlowConfigBuilder.findField(Simple.class,
                "name"));

        // NOT existing field
        Assert.assertNull(actionFlowConfigBuilder.findField(LevelTwo.class,
                "notExistingField"));
        Assert.assertNull(actionFlowConfigBuilder.findField(Simple.class,
                "notExistingField"));

        // primitive and null
        Assert.assertNull(actionFlowConfigBuilder.findField(int.class,
                "notExistingField"));
        Assert.assertNull(actionFlowConfigBuilder.findField(Simple.class, null));
        Assert.assertNull(actionFlowConfigBuilder.findField(null, "name"));
        Assert.assertNull(actionFlowConfigBuilder.findField(null, null));
    }

    class LevelOne extends ActionSupport {
        private static final long serialVersionUID = 1L;
        String name;
    }

    private class LevelTwo extends LevelOne {
        private static final long serialVersionUID = 1L;
    }

    class Simple {
        String name;
    }
}
