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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amashchenko.struts2.actionflow.ActionFlowScope;
import com.amashchenko.struts2.actionflow.ActionFlowStepsAware;
import com.amashchenko.struts2.actionflow.entities.ActionFlowStepsData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Mock ActionFlow action.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
@ActionFlowScope
public class MockActionFlowAction extends ActionSupport implements
        ActionFlowStepsAware {

    /** Serial version uid. */
    private static final long serialVersionUID = 6659699489397750306L;

    private ActionFlowStepsData stepsData;

    @Override
    public void setActionFlowSteps(ActionFlowStepsData stepsData) {
        this.stepsData = stepsData;
    }

    /** Phone field. */
    @ActionFlowScope
    private String phone;

    /** Date. */
    private Date date;

    /**
     * Creates map action flow scope fields map for this mock action class.
     * 
     * @return Map of the action flow scope fields, where key is the name of the
     *         action class (as returned by {@link Class#getName()}) and value
     *         is list of {@link PropertyDescriptor}.
     */
    public static Map<String, List<PropertyDescriptor>> mockFlowScopeFields() {
        Map<String, List<PropertyDescriptor>> map = new HashMap<String, List<PropertyDescriptor>>();
        List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();

        descriptors.add(mockPropertyDescriptorPhone());

        map.put(MockActionFlowAction.class.getName(), descriptors);
        return map;
    }

    public static PropertyDescriptor mockPropertyDescriptorPhone() {
        try {
            return new PropertyDescriptor("phone", MockActionFlowAction.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

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

    /**
     * @return the stepsData
     */
    public ActionFlowStepsData getStepsData() {
        return stepsData;
    }
}
