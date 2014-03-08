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
package com.amashchenko.struts2.actionflow.entities;

/**
 * Action flow step configuration.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowStepConfig {
    private int index;
    private String nextAction;
    private String prevAction;

    protected ActionFlowStepConfig(ActionFlowStepConfig orig) {
        this.index = orig.index;
        this.nextAction = orig.nextAction;
        this.prevAction = orig.prevAction;
    }

    protected ActionFlowStepConfig(int index, String nextAction,
            String prevAction) {
        this.index = index;
        this.nextAction = nextAction;
        this.prevAction = prevAction;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the getNextAction
     */
    public String getNextAction() {
        return nextAction;
    }

    /**
     * @return the getPrevAction
     */
    public String getPrevAction() {
        return prevAction;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionFlowStepConfig)) {
            return false;
        }

        final ActionFlowStepConfig stepConfig = (ActionFlowStepConfig) o;

        if (index != stepConfig.index) {
            return false;
        }
        if ((nextAction != null) ? (!nextAction.equals(stepConfig.nextAction))
                : (stepConfig.nextAction != null)) {
            return false;
        }
        if ((prevAction != null) ? (!prevAction.equals(stepConfig.prevAction))
                : (stepConfig.prevAction != null)) {
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result
                + ((nextAction == null) ? 0 : nextAction.hashCode());
        result = prime * result
                + ((prevAction == null) ? 0 : prevAction.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ActionFlowStepConfig ");
        sb.append("index:").append(index).append(", ");
        sb.append("nextAction:").append(nextAction).append(", ");
        sb.append("prevAction:").append(prevAction);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Action flow step configuration builder.
     * 
     */
    public static class Builder {
        private ActionFlowStepConfig target;

        public Builder(ActionFlowStepConfig toClone) {
            target = new ActionFlowStepConfig(toClone);
        }

        public Builder(int index, String nextAction, String prevAction) {
            target = new ActionFlowStepConfig(index, nextAction, prevAction);
        }

        public Builder index(int index) {
            target.index = index;
            return this;
        }

        public Builder nextAction(String nextAction) {
            target.nextAction = nextAction;
            return this;
        }

        public Builder prevAction(String prevAction) {
            target.prevAction = prevAction;
            return this;
        }

        public ActionFlowStepConfig build() {
            ActionFlowStepConfig result = target;
            target = new ActionFlowStepConfig(target);
            return result;
        }
    }
}
