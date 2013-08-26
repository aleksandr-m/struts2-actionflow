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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.struts2.dispatcher.ServletActionRedirectResult;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Action flow configuration builder. Creates action flow configuration map.
 * 
 * @author Aleksandr Mashchenko
 * 
 */
public class ActionFlowConfigBuilder {

    /** Logger. */
    public static final Logger LOG = LoggerFactory
            .getLogger(ActionFlowConfigBuilder.class);

    /** Parameter indicating that this action belongs to action flow. */
    private static final String PARAM_ACTION_FLOW_STEP = "actionFlowStep";

    /** XWork configuration. */
    @Inject
    private Configuration configuration;

    /**
     * Creates action flow map for given package name.
     * 
     * @param packageName
     *            Name of the package.
     * @param nextActionName
     *            Name of the next action.
     * @param prevActionName
     *            Name of the previous action.
     * @param viewActionPostfix
     *            View action postfix.
     * @param viewActionMethod
     *            View action method.
     * @return Map of the action flow.
     */
    protected Map<String, Map<String, String>> createFlowMap(
            final String packageName, final String nextActionName,
            final String prevActionName, final String viewActionPostfix,
            final String viewActionMethod) {
        // holds all actions with PARAM_ACTION_FLOW_STEP parameter
        // using TreeMap for natural ordering of keys
        Map<String, ActionConfig> actionsStepMap = new TreeMap<String, ActionConfig>();

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
                if (actionsStepMap.containsKey(key)) {
                    throw new ConfigurationException(
                            "There is more than one action defined with the same value of '"
                                    + PARAM_ACTION_FLOW_STEP
                                    + "' parameter. Action '"
                                    + actionsStepMap.get(key).getName()
                                    + "' and '" + actionConfig.getName()
                                    + "' in '" + actionConfig.getPackageName()
                                    + "' package.", actionConfig);
                }

                actionsStepMap.put(key, actionConfig);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("found action flows: " + actionsStepMap);
        }

        if (actionsStepMap != null && !actionsStepMap.isEmpty()) {
            // view global result
            if (!packageConfig.getAllGlobalResults().containsKey(
                    ActionFlowInterceptor.GLOBAL_VIEW_RESULT)) {
                throw new ConfigurationException(
                        "There is no 'view' global result with name '"
                                + ActionFlowInterceptor.GLOBAL_VIEW_RESULT
                                + "' defined in '" + packageName + "' package.",
                        packageConfig);
            } else {
                ResultConfig rs = packageConfig.getAllGlobalResults().get(
                        ActionFlowInterceptor.GLOBAL_VIEW_RESULT);
                if (!ServletActionRedirectResult.class.getName().equals(
                        rs.getClassName())) {
                    throw new ConfigurationException(
                            "The '"
                                    + ActionFlowInterceptor.GLOBAL_VIEW_RESULT
                                    + "' global result type must be 'redirectAction' in package '"
                                    + packageName + "'.", rs);
                } else if (!("${" + ActionFlowInterceptor.VIEW_ACTION_PARAM + "}")
                        .equals(rs.getParams().get(
                                ServletActionRedirectResult.DEFAULT_PARAM))) {
                    throw new ConfigurationException("The '"
                            + ActionFlowInterceptor.GLOBAL_VIEW_RESULT
                            + "' global result '"
                            + ServletActionRedirectResult.DEFAULT_PARAM
                            + "' parameter must be '${"
                            + ActionFlowInterceptor.VIEW_ACTION_PARAM
                            + "}' in package '" + packageName + "'.", rs);
                }
            }
        }

        // holds action flows: {1:{nextAction:2,prevAction:0,index:1}}
        Map<String, Map<String, String>> actionFlows = new HashMap<String, Map<String, String>>();

        List<ActionConfig> viewActionConfigs = new ArrayList<ActionConfig>();

        List<String> keys = new ArrayList<String>(actionsStepMap.keySet());
        ListIterator<String> mapkeyitr = keys.listIterator();

        String prevKey = null;
        // starting from 1 because of the FIRST_FLOW_ACTION_NAME
        int index = 1;
        while (mapkeyitr.hasNext()) {
            String key = mapkeyitr.next();

            ActionConfig actionConfig = actionsStepMap.get(key);

            // create view action
            if (actionConfigs.containsKey(actionConfig.getName()
                    + viewActionPostfix)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The '" + actionConfig.getName()
                            + viewActionPostfix + "' action is overridden in '"
                            + packageName + "' package.");
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
                        actionConfig.getName() + viewActionPostfix,
                        actionConfig.getClassName())
                        .methodName(viewActionMethod)
                        .addInterceptors(actionConfig.getInterceptors())
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
                ActionConfig nextActionConfig = actionsStepMap.get(nextKey);
                nextActionVal = nextActionConfig.getName();
            }

            if (prevKey == null) {
                prevActionVal = ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME;

                // first action
                Map<String, String> v = new HashMap<String, String>();
                v.put(ActionFlowInterceptor.NEXT_ACTION_PARAM,
                        actionConfig.getName());
                v.put(ActionFlowInterceptor.PREV_ACTION_PARAM,
                        ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME);
                v.put(ActionFlowInterceptor.ACTION_FLOW_INDEX, "0");
                actionFlows.put(ActionFlowInterceptor.FIRST_FLOW_ACTION_NAME,
                        Collections.unmodifiableMap(v));
            } else {
                prevActionVal = actionsStepMap.get(prevKey).getName();
            }

            Map<String, String> v = new HashMap<String, String>();
            v.put(ActionFlowInterceptor.NEXT_ACTION_PARAM, nextActionVal);
            v.put(ActionFlowInterceptor.PREV_ACTION_PARAM, prevActionVal);
            v.put(ActionFlowInterceptor.ACTION_FLOW_INDEX, "" + index);

            actionFlows.put(actionConfig.getName(),
                    Collections.unmodifiableMap(v));

            prevKey = key;
            index++;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("created action flow mapping: " + actionFlows);
        }

        if (actionFlows != null && !actionFlows.isEmpty()) {
            // build new package configuration with special actions
            PackageConfig.Builder pcb = new PackageConfig.Builder(packageConfig);

            // add view actions
            if (viewActionConfigs != null && !viewActionConfigs.isEmpty()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("view action configurations: "
                            + viewActionConfigs);
                }

                for (ActionConfig ac : viewActionConfigs) {
                    pcb.addActionConfig(ac.getName(), ac);
                }
            }

            // TODO add interceptors ?
            // previous action
            if (actionConfigs.containsKey(prevActionName)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The '" + prevActionName
                            + "' action is overridden in '" + packageName
                            + "' package.");
                }

                // previous action check
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
                } else if (!("${" + ActionFlowInterceptor.PREV_ACTION_PARAM + "}")
                        .equals(rs.get(Action.SUCCESS).getParams()
                                .get(ServletActionRedirectResult.DEFAULT_PARAM))) {
                    throw new ConfigurationException("The '" + prevActionName
                            + "' action '" + Action.SUCCESS + "' result '"
                            + ServletActionRedirectResult.DEFAULT_PARAM
                            + "' parameter must be '${"
                            + ActionFlowInterceptor.PREV_ACTION_PARAM
                            + "}' in package '" + packageName + "'.",
                            rs.get(Action.SUCCESS));
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("There is no 'previous' action with name '"
                            + prevActionName + "' found in '" + packageName
                            + "' package. Creating one.");
                }

                // add previous action
                ResultConfig prevResultConfig = new ResultConfig.Builder(
                        Action.SUCCESS,
                        ServletActionRedirectResult.class.getName()).addParam(
                        ServletActionRedirectResult.DEFAULT_PARAM,
                        "${" + ActionFlowInterceptor.PREV_ACTION_PARAM + "}")
                        .build();
                // build previous action configuration
                ActionConfig prevAct = new ActionConfig.Builder(packageName,
                        prevActionName, "").addResultConfig(prevResultConfig)
                        .build();
                pcb.addActionConfig(prevAct.getName(), prevAct);
            }

            // next action
            if (actionConfigs.containsKey(nextActionName)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The '" + nextActionName
                            + "' action is overridden in '" + packageName
                            + "' package.");
                }

                // next action check
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
                } else if (!("${" + ActionFlowInterceptor.NEXT_ACTION_PARAM + "}")
                        .equals(rs.get(Action.SUCCESS).getParams()
                                .get(ActionChainResult.DEFAULT_PARAM))) {
                    throw new ConfigurationException("The '" + nextActionName
                            + "' action '" + Action.SUCCESS + "' result '"
                            + ActionChainResult.DEFAULT_PARAM
                            + "' parameter must be '${"
                            + ActionFlowInterceptor.NEXT_ACTION_PARAM
                            + "}' in package '" + packageName + "'.",
                            rs.get(Action.SUCCESS));
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("There is no 'next' action with name '"
                            + nextActionName + "' found in '" + packageName
                            + "' package. Creating one.");
                }

                // add next action
                ResultConfig nextResultConfig = new ResultConfig.Builder(
                        Action.SUCCESS, ActionChainResult.class.getName())
                        .addParam(
                                ActionChainResult.DEFAULT_PARAM,
                                "${" + ActionFlowInterceptor.NEXT_ACTION_PARAM
                                        + "}").build();
                // build next action configuration
                ActionConfig nextAct = new ActionConfig.Builder(packageName,
                        nextActionName, "").addResultConfig(nextResultConfig)
                        .build();
                pcb.addActionConfig(nextAct.getName(), nextAct);
            }

            // build flow package
            PackageConfig pconf = pcb.build();

            configuration.removePackageConfig(packageName);
            configuration.addPackageConfig(packageName, pconf);
            configuration.rebuildRuntimeConfiguration();
        }
        return Collections.unmodifiableMap(actionFlows);
    }
}
