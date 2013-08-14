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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.struts2.dispatcher.ServletActionRedirectResult;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
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
 * executions. The default is true.</li>
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
 *     &lt;s:textfield name="name" label="Name"/&gt;
 *     &lt;s:submit value="previous" action="prev"/&gt;
 *     &lt;s:submit value="next" action="next"/&gt;
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
    private static final long serialVersionUID = 7715021688586768830L;

    /** Logger. */
    public static final Logger LOG = LoggerFactory
            .getLogger(ActionFlowInterceptor.class);

    private static final String PARAM_ACTION_FLOW_STEP = "actionFlowStep";

    private static final String PREV_FLOW_ACTION = "com.amashchenko.struts2.actionflow.ActionFlowInterceptor.prevFlowAction";

    private static final String DEFAULT_NEXT_ACTION_NAME = "next";
    private static final String DEFAULT_PREV_ACTION_NAME = "prev";

    private static final String FIRST_FLOW_ACTION_NAME = "firstFlowAction";
    private static final String GLOBAL_VIEW_RESULT = "actionFlowViewResult";
    // TODO allow to override
    private static final String DEFAULT_VIEW_ACTION_POSTFIX = "View";
    // TODO allow to override method name (execute)
    private static final String DEFAULT_VIEW_ACTION_METHOD = "execute";

    protected static final String NEXT_ACTION_PARAM = "nextAction";
    protected static final String PREV_ACTION_PARAM = "prevAction";
    protected static final String VIEW_ACTION_PARAM = "viewAction";

    // interceptor parameters
    private String nextActionName = DEFAULT_NEXT_ACTION_NAME;
    private String prevActionName = DEFAULT_PREV_ACTION_NAME;
    private boolean forceFlowStepsOrder = true;

    /** Holds action flows. */
    private Map<String, Map<String, String>> flowMap;

    /** XWork configuration. */
    @Inject
    private Configuration configuration;

    /** {@inheritDoc} */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String actionName = invocation.getInvocationContext().getName();

        Map<String, Object> session = invocation.getInvocationContext()
                .getSession();

        String prevFlowAction = (String) session.get(PREV_FLOW_ACTION);

        if (prevFlowAction == null) {
            prevFlowAction = FIRST_FLOW_ACTION_NAME;
        }

        String nextAction = null;
        String prevAction = null;

        if (flowMap == null) {
            String packageName = invocation.getProxy().getConfig()
                    .getPackageName();
            flowMap = createFlowMap(packageName);
        }

        if (flowMap.containsKey(prevFlowAction)) {
            nextAction = flowMap.get(prevFlowAction).get(NEXT_ACTION_PARAM);
            prevAction = flowMap.get(prevFlowAction).get(PREV_ACTION_PARAM);
        }

        boolean flowAction = false;
        boolean lastAction = false;

        if (flowMap.containsKey(actionName)) {
            flowAction = true;
            if (flowMap.get(actionName).get(NEXT_ACTION_PARAM) == null) {
                lastAction = true;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(actionName + "-> prevFlowAction: " + prevFlowAction
                    + ", nextAction: " + nextAction + ", prevAction: "
                    + prevAction);
        }

        // force order of flow actions
        if (forceFlowStepsOrder && flowAction && !actionName.equals(nextAction)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The forceFlowStepsOrder parameter is set to true. The '"
                        + actionName
                        + "' will not be executed because it is executed in the wrong order.");
            }
            // TODO maybe not NONE
            return Action.NONE;
        }

        if (nextActionName.equals(actionName)) {
            invocation.getInvocationContext().getValueStack()
                    .set(NEXT_ACTION_PARAM, nextAction);
        } else if (prevActionName.equals(actionName)) {
            if (FIRST_FLOW_ACTION_NAME.equals(prevFlowAction)) {
                invocation
                        .getInvocationContext()
                        .getValueStack()
                        .set(PREV_ACTION_PARAM,
                                nextAction + DEFAULT_VIEW_ACTION_POSTFIX);
            } else {
                invocation
                        .getInvocationContext()
                        .getValueStack()
                        .set(PREV_ACTION_PARAM,
                                prevFlowAction + DEFAULT_VIEW_ACTION_POSTFIX);
            }
            session.put(PREV_FLOW_ACTION, prevAction);
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
                                        nextAct + DEFAULT_VIEW_ACTION_POSTFIX);
                        invocation.setResultCode(GLOBAL_VIEW_RESULT);
                    }
                }
            });
        }

        String result = invocation.invoke();

        if (GLOBAL_VIEW_RESULT.equals(result) && flowAction) {
            session.put(PREV_FLOW_ACTION, actionName);
        }

        // last flow action
        if (Action.SUCCESS.equals(result) && flowAction && lastAction) {
            session.put(PREV_FLOW_ACTION, null);
        }

        return result;
    }

    /**
     * Creates action flow map for given package name.
     * 
     * @param packageName
     *            Name of the package.
     * @return Map of the action flow.
     */
    protected Map<String, Map<String, String>> createFlowMap(
            final String packageName) {
        // holds all actions with PARAM_ACTION_FLOW_STEP parameter
        // using TreeMap for natural ordering of keys
        Map<String, ActionConfig> actionsMap = new TreeMap<String, ActionConfig>();

        Map<String, Map<String, ActionConfig>> runtimeActionConfigs = configuration
                .getRuntimeConfiguration().getActionConfigs();

        PackageConfig packageConfig = configuration
                .getPackageConfig(packageName);

        // Map<String, ActionConfig> actionConfigs = packageConfig
        // .getAllActionConfigs();
        Map<String, ActionConfig> actionConfigs = runtimeActionConfigs
                .get(packageConfig.getNamespace());
        for (Entry<String, ActionConfig> entrActConf : actionConfigs.entrySet()) {
            ActionConfig actionConfig = entrActConf.getValue();
            if (actionConfig.getParams().containsKey(PARAM_ACTION_FLOW_STEP)) {
                String key = actionConfig.getParams().get(
                        PARAM_ACTION_FLOW_STEP);

                // action flow steps check
                if (actionsMap.containsKey(key)) {
                    throw new ConfigurationException(
                            "There is more than one action defined with the same value of '"
                                    + PARAM_ACTION_FLOW_STEP
                                    + "' parameter. Action '"
                                    + actionsMap.get(key).getName() + "' and '"
                                    + actionConfig.getName() + "' in '"
                                    + actionConfig.getPackageName()
                                    + "' package.", actionConfig);
                }

                actionsMap.put(key, actionConfig);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("found action flows: " + actionsMap);
        }

        // special actions and results check
        if (actionsMap != null && !actionsMap.isEmpty()) {
            // next action
            if (!actionConfigs.containsKey(nextActionName)) {
                throw new ConfigurationException(
                        "There is no 'next' action with name '"
                                + nextActionName + "' defined in '"
                                + packageName + "' package.", packageConfig);
            } else {
                Map<String, ResultConfig> rs = actionConfigs
                        .get(nextActionName).getResults();
                if (rs == null
                        || rs.isEmpty()
                        || !ActionChainResult.class.getName().equals(
                                rs.get(Action.SUCCESS).getClassName())) {
                    throw new ConfigurationException("The '" + nextActionName
                            + "' action must define '" + Action.SUCCESS
                            + "' result of 'chain' type in package '"
                            + packageName + "'.",
                            actionConfigs.get(nextActionName));
                } else if (!("${" + NEXT_ACTION_PARAM + "}").equals(rs
                        .get(Action.SUCCESS).getParams()
                        .get(ActionChainResult.DEFAULT_PARAM))) {
                    throw new ConfigurationException("The '" + nextActionName
                            + "' action '" + Action.SUCCESS + "' result '"
                            + ActionChainResult.DEFAULT_PARAM
                            + "' parameter must be '${" + NEXT_ACTION_PARAM
                            + "}' in package '" + packageName + "'.",
                            rs.get(Action.SUCCESS));
                }
            }
            // previous action
            if (!actionConfigs.containsKey(prevActionName)) {
                throw new ConfigurationException(
                        "There is no 'previous' action with name '"
                                + prevActionName + "' defined in '"
                                + packageName + "' package.", packageConfig);
            } else {
                Map<String, ResultConfig> rs = actionConfigs
                        .get(prevActionName).getResults();
                if (rs == null
                        || rs.isEmpty()
                        || !rs.containsKey(Action.SUCCESS)
                        || !ServletActionRedirectResult.class.getName().equals(
                                rs.get(Action.SUCCESS).getClassName())) {
                    throw new ConfigurationException("The '" + prevActionName
                            + "' action must define '" + Action.SUCCESS
                            + "' result of 'redirectAction' type in package '"
                            + packageName + "'.",
                            actionConfigs.get(prevActionName));
                } else if (!("${" + PREV_ACTION_PARAM + "}").equals(rs
                        .get(Action.SUCCESS).getParams()
                        .get(ServletActionRedirectResult.DEFAULT_PARAM))) {
                    throw new ConfigurationException("The '" + prevActionName
                            + "' action '" + Action.SUCCESS + "' result '"
                            + ServletActionRedirectResult.DEFAULT_PARAM
                            + "' parameter must be '${" + PREV_ACTION_PARAM
                            + "}' in package '" + packageName + "'.",
                            rs.get(Action.SUCCESS));
                }
            }
            // view global result
            if (!packageConfig.getAllGlobalResults().containsKey(
                    GLOBAL_VIEW_RESULT)) {
                throw new ConfigurationException(
                        "There is no 'view' global result with name '"
                                + GLOBAL_VIEW_RESULT + "' defined in '"
                                + packageName + "' package.", packageConfig);
            } else {
                ResultConfig rs = packageConfig.getAllGlobalResults().get(
                        GLOBAL_VIEW_RESULT);
                if (!ServletActionRedirectResult.class.getName().equals(
                        rs.getClassName())) {
                    throw new ConfigurationException(
                            "The '"
                                    + GLOBAL_VIEW_RESULT
                                    + "' global result type must be 'redirectAction' in package '"
                                    + packageName + "'.", rs);
                } else if (!("${" + VIEW_ACTION_PARAM + "}").equals(rs
                        .getParams().get(
                                ServletActionRedirectResult.DEFAULT_PARAM))) {
                    throw new ConfigurationException("The '"
                            + GLOBAL_VIEW_RESULT + "' global result '"
                            + ServletActionRedirectResult.DEFAULT_PARAM
                            + "' parameter must be '${" + VIEW_ACTION_PARAM
                            + "}' in package '" + packageName + "'.", rs);
                }
            }
        }

        // holds action flows: {1:{nextAction:2,prevAction:0}}
        Map<String, Map<String, String>> actionFlows = new HashMap<String, Map<String, String>>();

        List<ActionConfig> viewActionConfigs = new ArrayList<ActionConfig>();

        List<String> keys = new ArrayList<String>(actionsMap.keySet());
        ListIterator<String> mapkeyitr = keys.listIterator();

        String prevKey = null;
        while (mapkeyitr.hasNext()) {
            String key = mapkeyitr.next();

            ActionConfig actionConfig = actionsMap.get(key);

            // create view action
            if (actionConfigs.containsKey(actionConfig.getName()
                    + DEFAULT_VIEW_ACTION_POSTFIX)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The '" + actionConfig.getName()
                            + DEFAULT_VIEW_ACTION_POSTFIX
                            + "' action is overrided in '" + packageName
                            + "' package.");
                }
            } else {
                ResultConfig inputResultConfig = actionConfig.getResults().get(
                        Action.INPUT);
                if (inputResultConfig == null) {
                    throw new ConfigurationException("There is no '"
                            + Action.INPUT + "' result defined for action '"
                            + actionConfig.getName() + "' in '" + packageName
                            + "' package.", actionConfig);
                }

                ResultConfig resultConfig = new ResultConfig.Builder(
                        Action.SUCCESS, inputResultConfig.getClassName())
                        .addParams(inputResultConfig.getParams()).build();
                // build action configuration
                ActionConfig act = new ActionConfig.Builder(packageName,
                        DEFAULT_VIEW_ACTION_METHOD, actionConfig.getClassName())
                        .name(actionConfig.getName()
                                + DEFAULT_VIEW_ACTION_POSTFIX)
                        .addResultConfig(resultConfig).build();
                viewActionConfigs.add(act);
            }

            String nextKey = null;
            if (mapkeyitr.hasNext()) {
                // peek
                nextKey = mapkeyitr.next();
                mapkeyitr.previous();
            }

            String nextActionVal = null;
            String prevActionVal = null;
            if (nextKey == null) {
                nextActionVal = null;
            } else {
                ActionConfig nextActionConfig = actionsMap.get(nextKey);
                nextActionVal = nextActionConfig.getName();
            }

            if (prevKey == null) {
                prevActionVal = FIRST_FLOW_ACTION_NAME;

                // first action
                Map<String, String> v = new HashMap<String, String>();
                v.put(NEXT_ACTION_PARAM, actionConfig.getName());
                v.put(PREV_ACTION_PARAM, FIRST_FLOW_ACTION_NAME);
                actionFlows.put(FIRST_FLOW_ACTION_NAME, v);
            } else {
                prevActionVal = actionsMap.get(prevKey).getName();
            }

            Map<String, String> v = new HashMap<String, String>();
            v.put(NEXT_ACTION_PARAM, nextActionVal);
            v.put(PREV_ACTION_PARAM, prevActionVal);

            actionFlows.put(actionConfig.getName(), v);

            prevKey = key;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("created action flow mapping: " + actionFlows);
        }

        if (viewActionConfigs != null && !viewActionConfigs.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("view action configurations: " + viewActionConfigs);
            }

            // build new package configuration with view actions
            PackageConfig.Builder pcb = new PackageConfig.Builder(packageConfig);

            for (ActionConfig ac : viewActionConfigs) {
                pcb.addActionConfig(ac.getName(), ac);
            }

            PackageConfig pconf = pcb.build();

            configuration.removePackageConfig(packageName);
            configuration.addPackageConfig(packageName, pconf);
            configuration.rebuildRuntimeConfiguration();
        }

        return actionFlows;
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
}
