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

/**
 * Actions that want to change the flow of action steps should implement this
 * interface.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public interface ActionFlowAware {
    /**
     * 
     * @param actionName
     * @return Name of the action to be called on next 'next'. If
     *         <code>null</code> is returned action flow won't be changed.
     */
    String nextActionFlowAction(String actionName);

    /**
     * 
     * @param actionName
     * @return Name of the action to be called on next 'next'. If
     *         <code>null</code> is returned action flow won't be changed.
     */
    String prevActionFlowAction(String actionName);
}
