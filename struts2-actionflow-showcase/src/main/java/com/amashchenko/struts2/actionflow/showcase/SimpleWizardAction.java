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
package com.amashchenko.struts2.actionflow.showcase;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.amashchenko.struts2.actionflow.ActionFlowScope;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Simple wizard action.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
@ActionFlowScope
public class SimpleWizardAction extends ActionSupport implements SessionAware {

    /** Serial version uid. */
    private static final long serialVersionUID = 1023310153547766887L;

    /** HTTP session. */
    private Map<String, Object> session;

    /** {@inheritDoc} */
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    /** Name input. */
    @ActionFlowScope
    private String name;
    /** Phone input. */
    @ActionFlowScope
    private String phone;
    /** Email input. */
    @ActionFlowScope
    private String email;

    /**
     * Saves name.
     * 
     * @return action result.
     */
    public String saveName() {
        session.put("names", name);
        return SUCCESS;
    }

    /**
     * Saves phone.
     * 
     * @return action result.
     */
    public String savePhone() {
        session.put("phones", phone);
        return SUCCESS;
    }

    /**
     * Saves email.
     * 
     * @return action result.
     */
    public String saveEmail() {
        session.put("emails", email);
        return SUCCESS;
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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
