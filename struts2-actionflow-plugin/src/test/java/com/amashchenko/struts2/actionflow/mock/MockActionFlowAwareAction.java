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
package com.amashchenko.struts2.actionflow.mock;

import com.amashchenko.struts2.actionflow.ActionFlowAware;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Mock ActionFlowAware action.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class MockActionFlowAwareAction extends ActionSupport implements
        ActionFlowAware {

    /** Serial version uid. */
    private static final long serialVersionUID = 6183268804607231373L;

    /** Wrong action name constant. */
    public static final String WRONG_ACTION_NAME = "wrongActionName";

    /** Name field. */
    private String name;

    /** {@inheritDoc} */
    @Override
    public String nextActionFlowAction(String actionName) {
        String nextAction = null;
        if ("saveName-1".equals(actionName)) {
            nextAction = "saveEmail-3";
        }
        if (WRONG_ACTION_NAME.equals(name)) {
            nextAction = WRONG_ACTION_NAME;
        }
        if ("null".equals(name)) {
            nextAction = null;
        }
        return nextAction;
    }

    /** {@inheritDoc} */
    @Override
    public String prevActionFlowAction(String actionName) {
        String prevAction = null;
        if ("savePhone-2".equals(actionName)) {
            prevAction = "saveName-1";
        }
        return prevAction;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
