<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<h4>simple example</h4>
<s:include value="steps.jsp"/>

<s:form action="next">
	<s:hidden name="step" value="%{#session['actionFlowPreviousAction']}"/>

	<s:textfield key="email" label="Email" />

	<tr><td><br/></td></tr>

	<s:include value="submit-buttons.jsp"/>
</s:form>

<br/>
<div class="example-code">		
    <i>Form:</i>
<pre>
&lt;s:form action="next"&gt;
    &lt;s:hidden name="step" value="%{#session['actionFlowPreviousAction']}" /&gt;
    
    &lt;s:textfield key="email" label="Email" /&gt;

    &lt;s:submit value="previous" action="prev" /&gt;
    &lt;s:submit value="next" action="next" /&gt;
&lt;/s:form&gt;
</pre>
        
    <i>Action configuration:</i>
<pre>
&lt;action name="saveEmail" method="saveEmail" class="..."&gt;
    &lt;param name="actionFlowStep"&gt;3&lt;/param&gt;
			
    &lt;result name="input"&gt;/WEB-INF/pages/email.jsp&lt;/result&gt;
    &lt;result name="error"&gt;/WEB-INF/pages/email.jsp&lt;/result&gt;
    &lt;result type="redirectAction"&gt;finish&lt;/result&gt;
&lt;/action&gt;
</pre>
</div>
