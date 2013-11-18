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
package com.amashchenko.struts2.actionflow.mock;

import java.util.Date;

import com.amashchenko.struts2.actionflow.ActionFlowScope;
import com.amashchenko.struts2.actionflow.ActionFlowStepCountAware;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Mock ActionFlow action.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
@ActionFlowScope
public class MockActionFlowAction extends ActionSupport implements
        ActionFlowStepCountAware {

    /** Serial version uid. */
    private static final long serialVersionUID = 6659699489397750306L;

    /** Action flow step count. */
    private Integer stepCount;

    /** {@inheritDoc} */
    @Override
    public void setActionFlowStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }

    /** Phone field. */
    @ActionFlowScope
    private String phone;

    /** Date. */
    private Date date;

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the stepCount
     */
    public Integer getStepCount() {
        return stepCount;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
