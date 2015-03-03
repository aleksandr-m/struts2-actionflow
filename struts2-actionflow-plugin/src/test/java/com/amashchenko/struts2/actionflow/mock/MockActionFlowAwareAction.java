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
    private static final long serialVersionUID = 3108909520018157706L;

    /** Wrong action name constant. */
    public static final String WRONG_ACTION_NAME = "wrongActionName";
    /** Null constant. */
    public static final String NULL = "null";
    /** Skip multiple actions constant. */
    public static final String SKIP_MULTIPLE_ACTIONS = "multiple";

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
        if (NULL.equals(name)) {
            nextAction = null;
        }
        if (SKIP_MULTIPLE_ACTIONS.equals(name)) {
            nextAction = "saveAddress-4";
        }
        return nextAction;
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
