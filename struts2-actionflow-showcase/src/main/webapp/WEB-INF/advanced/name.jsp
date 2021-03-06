<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<h4>advanced example</h4>
<s:include value="steps.jsp"/>

<s:form action="nextAction">
	<s:hidden name="step" value="%{#session['actionFlowPreviousAction']}"/>

	<s:textfield key="name" label="Name" />
	
	<tr><td></td><td colspan="2" class="hint">Type 'skip' as a name to jump over next step. <a href="#ActionFlowAware">(configuration)</a></td></tr>

	<tr><td><br/></td></tr>

	<s:include value="submit-buttons.jsp"/>
</s:form>

<br/>
<div class="example-code">		
    <i>Form:</i>
<pre>
&lt;s:form action="nextAction"&gt;
    &lt;s:hidden name="step" value="%{#session['actionFlowPreviousAction']}" /&gt;

    &lt;s:textfield key="name" label="Name" /&gt;

    &lt;s:submit value="previous" action="prevAction" /&gt;
    &lt;s:submit value="next" action="nextAction" /&gt;
&lt;/s:form&gt;
</pre>
        
    <i>Interceptor configuration:</i>
<pre>
&lt;interceptor-ref name="actionFlow"&gt;
    &lt;param name="nextActionName"&gt;nextAction&lt;/param&gt;
    &lt;param name="prevActionName"&gt;prevAction&lt;/param&gt;
&lt;/interceptor-ref&gt;
</pre>
        
    <i>Action configuration:</i>
<pre>
&lt;action name="saveName" method="saveName" class="..."&gt;
    &lt;param name="actionFlowStep"&gt;1&lt;/param&gt;
			
    &lt;result name="input"&gt;/WEB-INF/advanced/name.jsp&lt;/result&gt;
    &lt;result name="error"&gt;/WEB-INF/advanced/name.jsp&lt;/result&gt;
    &lt;result type="redirectAction"&gt;finish&lt;/result&gt;
&lt;/action&gt;
</pre>

    <i>ActionFlowAware:</i><a id="ActionFlowAware"></a>
<pre>
public class AdvancedWizardAction extends ActionSupport implements ActionFlowAware {
    @Override
    public String nextActionFlowAction(String currentActionName) {
        String action = null;
        if ("saveName".equals(currentActionName) &amp;&amp; "skip".equalsIgnoreCase(getName())) {
            action = "saveEmail";
        }
        return action;
    }
}
</pre>
</div>
