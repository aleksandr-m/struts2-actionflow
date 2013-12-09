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
package com.amashchenko.struts2.actionflow.entities;

import java.util.TreeMap;

/**
 * Holds sorted indexed map of steps names and currently active step index. Step
 * index starts from <code>1</code> for more convenient display in view.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowStepsData {
    /** Naturally sorted step names. */
    private final TreeMap<Integer, String> steps;

    /** Currently active step index. */
    private Integer stepIndex;

    /**
     * 
     * @param steps
     *            map of steps.
     */
    public ActionFlowStepsData(final TreeMap<Integer, String> steps) {
        this.steps = steps;
    }

    /**
     * @return the stepIndex
     */
    public Integer getStepIndex() {
        return stepIndex;
    }

    /**
     * @param stepIndex
     *            the stepIndex to set
     */
    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    /**
     * @return the steps
     */
    public TreeMap<Integer, String> getSteps() {
        return steps;
    }
}
