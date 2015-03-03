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
package com.amashchenko.struts2.actionflow;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.amashchenko.struts2.actionflow.entities.ActionFlowStepConfig;
import com.amashchenko.struts2.actionflow.entities.ActionFlowStepsData;
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
 * specified the default name <code>next</code> will be used.</li>
 * <p/>
 * <li>prevActionName (optional) - Name of the 'previous' action. If none is
 * specified the default name <code>prev</code> will be used.</li>
 * <p/>
 * <li>forceFlowStepsOrder (optional) - To force the order of flow action
 * executions. The default is <code>true</code>.</li>
 * <p/>
 * <li>viewActionPostfix (optional) - String to append to generated view action
 * name. The default is <code>View</code>.</li>
 * <p/>
 * <li>viewActionMethod (optional) - Action method to execute in generated view
 * actions. The default is <code>execute</code>.</li>
 * <p/>
 * <li>stepParameterName (optional) - Name of the form parameter holding
 * previous action value. The default is <code>step</code>.</li>
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
    private static final long serialVersionUID = -8931708101962468929L;

    /** Logger. */
    public static final Logger LOG = LoggerFactory
            .getLogger(ActionFlowInterceptor.class);

    /** Key for holding in session the name of the previous flow action. */
    private static final String PREVIOUS_FLOW_ACTION = "actionFlowPreviousAction";
    /** Key for holding in session current highest action index. */
    private static final String HIGHEST_CURRENT_ACTION_INDEX = "actionFlowHighestCurrentActionIndex";
    /** Key for holding in session map of skip actions. */
    private static final String SKIP_ACTIONS = "actionFlowSkipActionsMap";

    /** Default next action name. */
    private static final String DEFAULT_NEXT_ACTION_NAME = "next";
    /** Default previous action name. */
    private static final String DEFAULT_PREV_ACTION_NAME = "prev";

    /** Name of the first flow action. */
    protected static final String FIRST_FLOW_ACTION_NAME = "firstFlowAction";
    protected static final String GLOBAL_VIEW_RESULT = "actionFlowViewResult";

    /** Default postfix for view actions. */
    private static final String DEFAULT_VIEW_ACTION_POSTFIX = "View";
    private static final String DEFAULT_VIEW_ACTION_METHOD = "execute";
    private static final String DEFAULT_STEP_PARAM_NAME = "step";

    protected static final String NEXT_ACTION_PARAM = "nextAction";
    protected static final String PREV_ACTION_PARAM = "prevAction";
    protected static final String VIEW_ACTION_PARAM = "viewAction";

    private static final String FLOW_SCOPE_KEY = "actionFlowScope";
    private Map<String, List<PropertyDescriptor>> flowScopeFields;

    /** Previous not special nor flow action. */
    private String prevSimpleAction;
    /** Action before first next. */
    private String startAction;

    // interceptor parameters
    private String nextActionName = DEFAULT_NEXT_ACTION_NAME;
    private String prevActionName = DEFAULT_PREV_ACTION_NAME;
    private boolean forceFlowStepsOrder = true;
    private String viewActionPostfix = DEFAULT_VIEW_ACTION_POSTFIX;
    private String viewActionMethod = DEFAULT_VIEW_ACTION_METHOD;
    private String stepParameterName = DEFAULT_STEP_PARAM_NAME;

    /** Holds action flow. */
    private Map<String, ActionFlowStepConfig> flowMap;

    /** Holds action flow steps data. */
    private ActionFlowStepsData flowStepsData;

    /** Action flow configuration builder. */
    @Inject
    private ActionFlowConfigBuilder flowConfigBuilder;

    /** Current action name retrieved from invocation context. */
    private String actionName;
    /** Index of the current action from the flowMap. */
    private int indexCurrent;

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        actionName = invocation.getInvocationContext().getName();

        // initialize action flow configuration
        if (flowMap == null) {
            initFlowConfiguration(invocation.getProxy().getConfig()
                    .getPackageName());
        }

        indexCurrent = -1;
        Integer stepCount = 1;

        final boolean flowAction;
        final boolean lastFlowAction;
        if (flowMap.containsKey(actionName)) {
            flowAction = true;

            indexCurrent = flowMap.get(actionName).getIndex();

            // this is needed when input result is returned
            stepCount = indexCurrent;

            lastFlowAction = flowMap.get(actionName).getNextAction() == null;
        } else {
            flowAction = false;
            lastFlowAction = false;
        }

        boolean flowViewAction = false;
        // action name w/o view postfix
        String plainActionName = null;
        if (actionName.endsWith(viewActionPostfix)) {
            plainActionName = actionName.substring(0,
                    actionName.indexOf(viewActionPostfix));
            if (flowMap.containsKey(plainActionName)) {
                flowViewAction = true;

                stepCount = flowMap.get(plainActionName).getIndex();
            }
        }

        Map<String, Object> session = invocation.getInvocationContext()
                .getSession();

        // start
        if (startAction != null && startAction.equals(actionName)) {
            clearSession(session);
        }

        // action flow steps aware
        if (invocation.getAction() instanceof ActionFlowStepsAware) {
            flowStepsData.setStepIndex(stepCount);

            ((ActionFlowStepsAware) invocation.getAction())
                    .setActionFlowSteps(flowStepsData);
        }

        // scope
        if (flowViewAction) {
            handleFlowScope(invocation.getAction(), session, true);
        }

        // not a flow nor next nor previous action, just invoke
        if (!flowAction && !prevActionName.equals(actionName)
                && !nextActionName.equals(actionName)) {
            prevSimpleAction = actionName;
            return invocation.invoke();
        }

        String previousFlowAction = (String) session.get(PREVIOUS_FLOW_ACTION);

        if (previousFlowAction == null) {
            previousFlowAction = FIRST_FLOW_ACTION_NAME;
        }

        // handling of back/forward buttons
        Object[] stepParam = (Object[]) invocation.getInvocationContext()
                .getParameters().get(stepParameterName);
        boolean overriddenWithStep = false;
        if (stepParam != null && stepParam.length > 0) {
            String step = "" + stepParam[0];

            if (step.isEmpty()) {
                step = FIRST_FLOW_ACTION_NAME;
            }

            if (step != null && !step.equals(previousFlowAction)
                    && flowMap.containsKey(step)) {
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
                overriddenWithStep = true;
            }
        }

        String nextAction = null;
        String prevAction = null;

        if (flowMap.containsKey(previousFlowAction)) {
            nextAction = flowMap.get(previousFlowAction).getNextAction();
            prevAction = flowMap.get(previousFlowAction).getPrevAction();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(actionName + "-> previousFlowAction: "
                    + previousFlowAction + ", nextAction: " + nextAction
                    + ", prevAction: " + prevAction);
        }

        final Integer highestCurrentIndex;
        if (session.containsKey(HIGHEST_CURRENT_ACTION_INDEX)
                && session.get(HIGHEST_CURRENT_ACTION_INDEX) != null) {
            highestCurrentIndex = (Integer) session
                    .get(HIGHEST_CURRENT_ACTION_INDEX);
        } else {
            highestCurrentIndex = 0;
        }

        // force order of flow actions
        if (forceFlowStepsOrder && flowAction
                && (highestCurrentIndex.intValue() + 1) < indexCurrent) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The forceFlowStepsOrder parameter is set to true. The '"
                        + actionName
                        + "' action will not be executed because it is called in the wrong order.");
            }

            if (overriddenWithStep) {
                invocation
                        .getInvocationContext()
                        .getValueStack()
                        .set(VIEW_ACTION_PARAM,
                                previousFlowAction + viewActionPostfix);
            } else {
                invocation.getInvocationContext().getValueStack()
                        .set(VIEW_ACTION_PARAM, nextAction + viewActionPostfix);
            }
            return GLOBAL_VIEW_RESULT;
        }

        Map<String, String> skipMap = null;
        if (session.containsKey(SKIP_ACTIONS)
                && session.get(SKIP_ACTIONS) instanceof Map) {
            skipMap = (Map<String, String>) session.get(SKIP_ACTIONS);
        }

        if (nextActionName.equals(actionName)) {
            // set start action
            if (startAction == null) {
                startAction = prevSimpleAction;
            }

            invocation.getInvocationContext().getValueStack()
                    .set(NEXT_ACTION_PARAM, nextAction);
        } else if (prevActionName.equals(actionName)) {
            String prevView = null;
            if (FIRST_FLOW_ACTION_NAME.equals(previousFlowAction)) {
                prevView = nextAction;
            } else {
                if (skipMap != null && skipMap.containsKey(nextAction)) {
                    prevView = skipMap.get(nextAction);

                    // override prevAction
                    prevAction = flowMap.get(prevView).getPrevAction();
                } else {
                    prevView = previousFlowAction;
                }
            }

            invocation.getInvocationContext().getValueStack()
                    .set(PREV_ACTION_PARAM, prevView + viewActionPostfix);

            session.put(PREVIOUS_FLOW_ACTION, prevAction);
        }

        // add pre-result listener on correct flow action
        if (flowAction && nextAction.equals(actionName)) {
            invocation.addPreResultListener(new PreResultListener() {
                public void beforeResult(ActionInvocation invocation,
                        String resultCode) {
                    if (Action.SUCCESS.equals(resultCode)) {
                        // action flow aware
                        String nextFromAction = null;
                        if (invocation.getAction() instanceof ActionFlowAware) {
                            nextFromAction = ((ActionFlowAware) invocation
                                    .getAction())
                                    .nextActionFlowAction(actionName);

                            // if null just ignore otherwise check if returned
                            // action is a flow action
                            if (nextFromAction != null
                                    && !flowMap.containsKey(nextFromAction)) {
                                nextFromAction = null;
                            }
                        }

                        Map<String, String> skipMap = null;
                        if (invocation.getInvocationContext().getSession()
                                .containsKey(SKIP_ACTIONS)
                                && invocation.getInvocationContext()
                                        .getSession().get(SKIP_ACTIONS) instanceof Map) {
                            skipMap = (Map<String, String>) invocation
                                    .getInvocationContext().getSession()
                                    .get(SKIP_ACTIONS);
                        } else {
                            skipMap = new HashMap<String, String>();
                        }
                        if (nextFromAction != null) {
                            skipMap.put(nextFromAction, actionName);

                            // override actionName
                            actionName = flowMap.get(nextFromAction)
                                    .getPrevAction();
                            // override indexCurrent
                            indexCurrent = flowMap.get(actionName).getIndex();
                        } else if (!lastFlowAction) {
                            // get next action if it isn't the last flow action
                            // and not overridden from ActionFlowAware
                            nextFromAction = flowMap.get(actionName)
                                    .getNextAction();

                            skipMap.remove(nextFromAction);
                        }

                        invocation.getInvocationContext().getSession()
                                .put(SKIP_ACTIONS, skipMap);

                        // execute global view result on not last flow action or
                        // if next action is overridden from ActionFlowAware
                        if (!lastFlowAction || nextFromAction != null) {
                            invocation
                                    .getInvocationContext()
                                    .getValueStack()
                                    .set(VIEW_ACTION_PARAM,
                                            nextFromAction + viewActionPostfix);
                            invocation.setResultCode(GLOBAL_VIEW_RESULT);
                        }
                    }
                }
            });
        }

        final String result = invocation.invoke();

        // scope
        if (flowAction) {
            handleFlowScope(invocation.getAction(), session, false);
        }

        if (GLOBAL_VIEW_RESULT.equals(result) && flowAction) {
            session.put(PREVIOUS_FLOW_ACTION, actionName);

            // set highest current action index on a view result
            if (indexCurrent > highestCurrentIndex) {
                session.put(HIGHEST_CURRENT_ACTION_INDEX, indexCurrent);
            }
        }

        // last flow action
        if (Action.SUCCESS.equals(result) && flowAction && lastFlowAction) {
            clearSession(session);
        }

        return result;
    }

    private void initFlowConfiguration(final String packageName) {
        flowMap = flowConfigBuilder.createFlowMap(packageName, nextActionName,
                prevActionName, viewActionPostfix, viewActionMethod);

        flowScopeFields = flowConfigBuilder.createFlowScopeFields(packageName);

        // create action flow steps data
        if (flowMap != null) {
            TreeMap<Integer, String> m = new TreeMap<Integer, String>();
            for (ActionFlowStepConfig cfg : flowMap.values()) {
                if (cfg.getIndex() < flowMap.size() - 1) {
                    m.put(cfg.getIndex() + 1, cfg.getNextAction());
                }
            }
            flowStepsData = new ActionFlowStepsData(m);
        }
    }

    void clearSession(final Map<String, Object> session) {
        session.put(PREVIOUS_FLOW_ACTION, null);
        session.put(FLOW_SCOPE_KEY, null);

        session.put(HIGHEST_CURRENT_ACTION_INDEX, null);

        session.put(SKIP_ACTIONS, null);
    }

    /**
     * Handles action flow scope fields.
     * 
     * @param action
     *            action object.
     * @param session
     *            session map.
     * @param fromFlowScope
     *            whether to store value into the session or retrieve it. On
     *            <code>true</code> sets value from session into the action
     *            field, on <code>false</code> puts value from action field to
     *            session.
     */
    @SuppressWarnings("unchecked")
    void handleFlowScope(final Object action,
            final Map<String, Object> session, final boolean fromFlowScope) {
        if (action != null && flowScopeFields != null && session != null) {
            final String actionClassName = action.getClass().getName();

            Map<String, Object> scopeMap = null;
            if (session.containsKey(FLOW_SCOPE_KEY)
                    && session.get(FLOW_SCOPE_KEY) instanceof Map) {
                scopeMap = (Map<String, Object>) session.get(FLOW_SCOPE_KEY);
            }
            if (scopeMap == null) {
                scopeMap = new HashMap<String, Object>();
            }

            if (flowScopeFields.containsKey(actionClassName)
                    && flowScopeFields.get(actionClassName) != null) {
                for (PropertyDescriptor pd : flowScopeFields
                        .get(actionClassName)) {
                    try {
                        final Method getter = pd.getReadMethod();
                        if (getter != null) {
                            final Object val = getter.invoke(action);
                            final String scopeFieldKey = getter.toString();

                            if (fromFlowScope) {
                                if (val == null
                                        && scopeMap.containsKey(scopeFieldKey)) {
                                    final Method setter = pd.getWriteMethod();
                                    if (setter != null) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("Setting the value: '"
                                                    + scopeMap
                                                            .get(scopeFieldKey)
                                                    + "' for key: '"
                                                    + scopeFieldKey
                                                    + "' from the action flow scope into the action.");
                                        }

                                        setter.invoke(action,
                                                scopeMap.get(scopeFieldKey));
                                    }
                                }
                            } else {
                                if (val != null) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Storing the value: '"
                                                + val
                                                + "' for key: '"
                                                + scopeFieldKey
                                                + "' from the action into the action flow scope.");
                                    }

                                    scopeMap.put(scopeFieldKey, val);
                                    session.put(FLOW_SCOPE_KEY, scopeMap);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOG.warn("In handleFlowScope", e);
                    }
                }
            }
        }
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
     * @param value
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
