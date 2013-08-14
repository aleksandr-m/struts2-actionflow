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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.ConfigurationException;

/**
 * Tests wrong action flow configurations.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
@RunWith(Parameterized.class)
public class WrongConfigurationTest extends StrutsJUnit4TestCase<Object> {

    /** URI of the action. */
    private String actionUri;

    /** {@inheritDoc} */
    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml, struts-test.xml";
    }

    /** Parameters to use. */
    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { { "/noInputResult/noInputResult" },
                { "/sameFlowSteps/sameFlowSteps" },
                { "/wrongNextAction/wrongNextAction" },
                { "/wrongTypeNextAction/wrongTypeNextAction" },
                { "/wrongNextParam/wrongNextParam" },
                { "/noPrevAction/noPrevAction" },
                { "/wrongPrevParam/wrongPrevParam" },
                { "/prevNoSuccess/prevNoSuccess" },
                { "/noViewResult/noViewResult" },
                { "/wrongViewResultType/wrongViewResultType" },
                { "/wrongViewResultParam/wrongViewResultParam" }, };
        return Arrays.asList(data);
    }

    /**
     * Parameterized constructor.
     * 
     * @param actionUri
     *            URI of the action.
     */
    public WrongConfigurationTest(final String actionUri) {
        this.actionUri = actionUri;
    }

    /**
     * Tests wrong action flow configuration.
     * 
     * @throws Exception
     *             when something goes wrong.
     */
    @Test(expected = ConfigurationException.class)
    public void testWrongConfiguration() throws Exception {
        ActionProxy proxy = getActionProxy(actionUri);
        Assert.assertNotNull(proxy);
        proxy.getInvocation().getInvocationContext()
                .setSession(new HashMap<String, Object>());
        proxy.execute();
    }
}
