<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<h4>advanced example</h4>
<h4>Another phone page</h4>
<s:include value="steps.jsp"/>

<s:form action="nextAction">
	<s:hidden name="step" value="%{#session['actionFlowPreviousAction']}"/>

	<s:textfield key="phone" label="Phone" />

	<tr><td><br/></td></tr>

	<s:include value="submit-buttons.jsp"/>
</s:form>

<br/>
<div class="example-code">		
    <i>Form:</i>
<pre>
&lt;s:form action="nextAction"&gt;
    &lt;s:hidden name="step" value="%{#session['actionFlowPreviousAction']}" /&gt;
    
    &lt;s:textfield key="phone" label="Phone" /&gt;

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
&lt;action name="savePhone" method="savePhone" class="..."&gt;
    &lt;param name="actionFlowStep"&gt;2&lt;/param&gt;
		
    &lt;result name="input"&gt;/WEB-INF/advanced/phone.jsp&lt;/result&gt;
    &lt;result name="error"&gt;/WEB-INF/advanced/phone.jsp&lt;/result&gt;
    &lt;result type="redirectAction"&gt;finish&lt;/result&gt;
&lt;/action&gt;
		
&lt;!-- overriding view --&gt;
&lt;action name="savePhoneView" class="..."&gt;
    &lt;result&gt;/WEB-INF/advanced/anotherPhone.jsp&lt;/result&gt;
&lt;/action&gt;
</pre>
</div>
