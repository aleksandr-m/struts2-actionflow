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

import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: description -->
 * <p/>
 * An interceptor that controls flow actions.
 * <p/>
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/>
 * <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>nextActionName (optional) - Name of the 'next' action. If none is
 * specified the default name {@value #DEFAULT_NEXT_ACTION_NAME} will be used.</li>
 * <p/>
 * <li>prevActionName (optional) - Name of the 'previous' action. If none is
 * specified the default name {@value #DEFAULT_PREV_ACTION_NAME} will be used.</li>
 * <p/>
 * <li>forceFlowStepsOrder (optional) - To force the order of flow action
 * executions. The default is <code>true</code>.</li>
 * <p/>
 * <li>viewActionPostfix (optional) - String to append to generated view action
 * name. The default is {@value #DEFAULT_VIEW_ACTION_POSTFIX}.</li>
 * <p/>
 * <li>viewActionMethod (optional) - Action method to execute in generated view
 * actions. The default is {@value #DEFAULT_VIEW_ACTION_METHOD}.</li>
 * <p/>
 * <li>stepParameterName (optional) - Name of the form parameter holding
 * previous action value. The default is {@value #DEFAULT_STEP_PARAM_NAME}.</li>
 * <p/>
 * </ul>
 * <p/>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/>
 * <p/>
 * <u>Extending the interceptor:</u>
 * <p/>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * There are no known extensions points for this interceptor.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <u>Example code:</u>
 * 
 * <pre>
 * <!-- START SNIPPET: example-configuration -->
 * &lt;action name="saveName" method="saveName" class="com.example.FlowAction"&gt;
 *     &lt;param name="actionFlowStep"&gt;1&lt;/param&gt;
 *     
 *     &lt;result name="input"&gt;input_result.jsp&lt;/result&gt;
 *     &lt;result name="success"&gt;success_result.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example-configuration -->
 * </pre>
 * <p/>
 * You must use {@link #nextActionName} and {@link #prevActionName} in the form.
 * <p/>
 * 
 * <pre>
 * <!-- START SNIPPET: example-form -->
 * &lt;s:form action="next"&gt;
 *     &lt;s:hidden name="step" value="%{#session['actionFlowPreviousAction']}" /&gt;
 * 
 *     &lt;s:textfield name="name" label="Name" /&gt;
 *     &lt;s:submit value="previous" action="prev" /&gt;
 *     &lt;s:submit value="next" action="next" /&gt;
 * &lt;/s:form&gt;
 * <!-- END SNIPPET: example-form -->
 * </pre>
 * <p/>
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowInterceptor extends AbstractInterceptor {

    /** Serial version uid. */
    private static final long serialVersionUID = 6161518595680043244L;

    /** Logger. */
    public static final Logger LOG = LoggerFactory
            .getLogger(ActionFlowInterceptor.class);

    private static final String PREVIOUS_FLOW_ACTION = "actionFlowPreviousAction";

    private static final String DEFAULT_NEXT_ACTION_NAME = "next";
    private static final String DEFAULT_PREV_ACTION_NAME = "prev";

    protected static final String FIRST_FLOW_ACTION_NAME = "firstFlowAction";
    protected static final String GLOBAL_VIEW_RESULT = "actionFlowViewResult";

    private static final String DEFAULT_VIEW_ACTION_POSTFIX = "View";
    private static final String DEFAULT_VIEW_ACTION_METHOD = "execute";
    private static final String DEFAULT_STEP_PARAM_NAME = "step";

    protected static final String NEXT_ACTION_PARAM = "nextAction";
    protected static final String PREV_ACTION_PARAM = "prevAction";
    protected static final String VIEW_ACTION_PARAM = "viewAction";

    // interceptor parameters
    private String nextActionName = DEFAULT_NEXT_ACTION_NAME;
    private String prevActionName = DEFAULT_PREV_ACTION_NAME;
    private boolean forceFlowStepsOrder = true;
    private String viewActionPostfix = DEFAULT_VIEW_ACTION_POSTFIX;
    private String viewActionMethod = DEFAULT_VIEW_ACTION_METHOD;
    private String stepParameterName = DEFAULT_STEP_PARAM_NAME;

    /** Holds action flows. */
    private Map<String, Map<String, String>> flowMap;

    /** Action flow configuration parser. */
    @Inject
    private ActionFlowConfigParser flowConfigParser;

    /** {@inheritDoc} */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String actionName = invocation.getInvocationContext().getName();

        if (flowMap == null) {
            String packageName = invocation.getProxy().getConfig()
                    .getPackageName();
            flowMap = flowConfigParser.createFlowMap(packageName,
                    nextActionName, prevActionName, viewActionPostfix,
                    viewActionMethod);
        }

        boolean flowAction = false;
        boolean lastAction = false;

        if (flowMap.containsKey(actionName)) {
            flowAction = true;
            if (flowMap.get(actionName).get(NEXT_ACTION_PARAM) == null) {
                lastAction = true;
            }
        }

        // not a flow nor next nor previous action, just invoke
        if (!flowAction && !prevActionName.equals(actionName)
                && !nextActionName.equals(actionName)) {
            return invocation.invoke();
        }

        Map<String, Object> session = invocation.getInvocationContext()
                .getSession();

        String previousFlowAction = (String) session.get(PREVIOUS_FLOW_ACTION);

        if (previousFlowAction == null) {
            previousFlowAction = FIRST_FLOW_ACTION_NAME;
        }

        // handling of back/forward buttons
        Object[] stepParam = (Object[]) invocation.getInvocationContext()
                .getParameters().get(stepParameterName);
        if (stepParam != null && stepParam.length > 0) {
            String step = "" + stepParam[0];

            if (step.isEmpty()) {
                step = FIRST_FLOW_ACTION_NAME;
            }

            if (step != null && !step.equals(previousFlowAction)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The 'previousFlowAction' value from session is '"
                            + previousFlowAction
                            + "', but '"
                            + stepParameterName
                            + "' parameter value is '"
                            + step
                            + "' The '"
                            + stepParameterName
                            + "' parameter value will be used for 'previousFlowAction'.");
                }
                previousFlowAction = step;
            }
        }

        String nextAction = null;
        String prevAction = null;

        if (flowMap.containsKey(previousFlowAction)) {
            nextAction = flowMap.get(previousFlowAction).get(NEXT_ACTION_PARAM);
            prevAction = flowMap.get(previousFlowAction).get(PREV_ACTION_PARAM);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(actionName + "-> previousFlowAction: "
                    + previousFlowAction + ", nextAction: " + nextAction
                    + ", prevAction: " + prevAction);
        }

        // force order of flow actions
        if (forceFlowStepsOrder && flowAction && !actionName.equals(nextAction)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The forceFlowStepsOrder parameter is set to true. The '"
                        + actionName
                        + "' will not be executed because it is executed in the wrong order.");
            }

            invocation.getInvocationContext().getValueStack()
                    .set(VIEW_ACTION_PARAM, nextAction + viewActionPostfix);
            return GLOBAL_VIEW_RESULT;
        }

        if (nextActionName.equals(actionName)) {
            invocation.getInvocationContext().getValueStack()
                    .set(NEXT_ACTION_PARAM, nextAction);
        } else if (prevActionName.equals(actionName)) {
            if (FIRST_FLOW_ACTION_NAME.equals(previousFlowAction)) {
                invocation.getInvocationContext().getValueStack()
                        .set(PREV_ACTION_PARAM, nextAction + viewActionPostfix);
            } else {
                invocation
                        .getInvocationContext()
                        .getValueStack()
                        .set(PREV_ACTION_PARAM,
                                previousFlowAction + viewActionPostfix);
            }
            session.put(PREVIOUS_FLOW_ACTION, prevAction);
        }

        // execute global view result on not last flow action
        if (flowAction && nextAction.equals(actionName) && !lastAction) {
            final String nextAct = flowMap.get(actionName).get(
                    NEXT_ACTION_PARAM);
            invocation.addPreResultListener(new PreResultListener() {
                public void beforeResult(ActionInvocation invocation,
                        String resultCode) {
                    if (Action.SUCCESS.equals(resultCode)) {
                        invocation
                                .getInvocationContext()
                                .getValueStack()
                                .set(VIEW_ACTION_PARAM,
                                        nextAct + viewActionPostfix);
                        invocation.setResultCode(GLOBAL_VIEW_RESULT);
                    }
                }
            });
        }

        String result = invocation.invoke();

        if (GLOBAL_VIEW_RESULT.equals(result) && flowAction) {
            session.put(PREVIOUS_FLOW_ACTION, actionName);
        }

        // last flow action
        if (Action.SUCCESS.equals(result) && flowAction && lastAction) {
            session.put(PREVIOUS_FLOW_ACTION, null);
        }

        return result;
    }

    /**
     * @param nextActionName
     *            the nextActionName to set
     */
    public void setNextActionName(String nextActionName) {
        this.nextActionName = nextActionName;
    }

    /**
     * @param prevActionName
     *            the prevActionName to set
     */
    public void setPrevActionName(String prevActionName) {
        this.prevActionName = prevActionName;
    }

    /**
     * @param forceFlowStepsOrder
     *            the forceFlowStepsOrder to set
     */
    public void setForceFlowStepsOrder(String value) {
        this.forceFlowStepsOrder = Boolean.valueOf(value).booleanValue();
    }

    /**
     * @param viewActionPostfix
     *            the viewActionPostfix to set
     */
    public void setViewActionPostfix(String viewActionPostfix) {
        this.viewActionPostfix = viewActionPostfix;
    }

    /**
     * @param viewActionMethod
     *            the viewActionMethod to set
     */
    public void setViewActionMethod(String viewActionMethod) {
        this.viewActionMethod = viewActionMethod;
    }

    /**
     * @param stepParameterName
     *            the stepParameterName to set
     */
    public void setStepParameterName(String stepParameterName) {
        this.stepParameterName = stepParameterName;
    }
}
