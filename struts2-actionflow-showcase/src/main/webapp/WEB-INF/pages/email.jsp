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
		
			<s:textfield key="email" label="Email" />
		
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
&lt;s:form action="next">
    &lt;s:textfield key="email" label="Email" />

    &lt;s:submit value="previous" action="prev" />
    &lt;s:submit value="next" action="next" />
&lt;/s:form>
        </pre>
        
        <i>Action configuration:</i>
        <pre>
&lt;action name="saveEmail" method="saveEmail" class="...">
    &lt;param name="actionFlowStep">3&lt;/param>
			
    &lt;result name="input">/WEB-INF/pages/email.jsp&lt;/result>
    &lt;result name="error">/WEB-INF/pages/email.jsp&lt;/result>
    &lt;result type="redirectAction">finish&lt;/result>
&lt;/action>
        </pre>
    </div>
  </body>
</html>