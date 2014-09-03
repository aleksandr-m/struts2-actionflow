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
package com.amashchenko.struts2.actionflow.test;

/**
 * Utility class for test constants.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class TestConstants {
    /** Key for previous flow action. */
    public static final String PREVIOUS_FLOW_ACTION = "actionFlowPreviousAction";
    /** Expression for getting previous flow action from session. */
    public static final String SESSION_PREVIOUS_FLOW_ACTION = "#session['"
            + PREVIOUS_FLOW_ACTION + "']";

    public static final String FLOW_SCOPE_KEY = "actionFlowScope";
    public static final String FLOW_SCOPE_FIELD_NAME = "flowScopeFields";

    /** Key for holding in session current highest action index. */
    public static final String HIGHEST_CURRENT_ACTION_INDEX = "actionFlowHighestCurrentActionIndex";

    /** Expression for getting current highest action index from session. */
    public static final String SESSION_HIGHEST_CURRENT_ACTION_INDEX = "#session['"
            + HIGHEST_CURRENT_ACTION_INDEX + "']";

    public static final String SKIP_ACTIONS = "actionFlowSkipActionsMap";
}
