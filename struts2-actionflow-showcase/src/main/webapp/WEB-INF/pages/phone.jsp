<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Struts2 ActionFlow Showcase</title>
	</head>
	<body>
		<h4>simple example</h4>
		<s:form action="next">
			<s:hidden name="step" value="%{#session['actionFlowPreviousAction']}"/>
		
			<s:textfield key="phone" label="Phone" />
		
			<tr><td><br/></td></tr>
		
			<tr>
    		<td colspan="2">
    			<div>
						<s:submit value="previous" action="prev" theme="simple"/>
						<s:submit value="next" action="next" theme="simple"/>
					</div>
				</td>
			</tr>
		</s:form>
		
		<br/>
    <div class="example-code">		
        <i>Form:</i>
        <pre>
&lt;s:form action="next"&gt;
    &lt;s:hidden name="step" value="%{#session['actionFlowPreviousAction']}" /&gt;

    &lt;s:textfield key="phone" label="Phone" /&gt;

    &lt;s:submit value="previous" action="prev" /&gt;
    &lt;s:submit value="next" action="next" /&gt;
&lt;/s:form&gt;
        </pre>
        
        <i>Action configuration:</i>
        <pre>
&lt;action name="savePhone" method="savePhone" class="..."&gt;
    &lt;param name="actionFlowStep"&gt;2&lt;/param&gt;
			
    &lt;result name="input"&gt;/WEB-INF/pages/phone.jsp&lt;/result&gt;
    &lt;result name="error"&gt;/WEB-INF/pages/phone.jsp&lt;/result&gt;
    &lt;result type="redirectAction"&gt;finish&lt;/result&gt;
&lt;/action&gt;
        </pre>
    </div>
  </body>
</html>