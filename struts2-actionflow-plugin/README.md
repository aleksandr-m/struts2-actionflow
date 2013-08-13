# Struts2 ActionFlow Plug-in

A Struts2 plug-in for creating action flows.

## Installation

Copy struts2-actionflow-plugin-x.x.x.jar into your classpath (WEB-INF/lib). No other files need to be copied or created.

## Example Usage

The most simple setup to use this plug-in would be:

1. Install it by copying jar into /WEB-INF/lib directory.
2. Make your action package extend actionflow-default package.
3. Add <param name="actionFlowStep">1</param> parameters to actions you want to include in action flows. (NOTE: the input result must exist!)
4. Use next and prev actions in JSP to move between flow steps.

### Action Mappings

    <package name="actionflow-showcase" namespace="/" extends="actionflow-default">
    
        <action name="saveName" method="saveName" class="com.example.FlowAction">
            <param name="actionFlowStep">1</param>
 
            <result name="input">/WEB-INF/pages/name.jsp</result>
            <result name="error">/WEB-INF/pages/name.jsp</result>
            <result>/WEB-INF/pages/name-success.jsp</result>
        </action>
        <action name="savePhone" method="savePhone" class="com.example.FlowAction">
            <param name="actionFlowStep">2</param>
 
            <result name="input">/WEB-INF/pages/phone.jsp</result>
            <result name="error">/WEB-INF/pages/phone.jsp</result>
            <result>/WEB-INF/pages/phone-success.jsp</result>
        </action>
    
    </package>

### Form

    <s:form action="next">
       <s:textfield key="name" label="Name" />
       <s:submit value="previous" action="prev" />
       <s:submit value="next" action="next" />
    </s:form>

## License

    Copyright 2013 Aleksandr Mashchenko.
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.