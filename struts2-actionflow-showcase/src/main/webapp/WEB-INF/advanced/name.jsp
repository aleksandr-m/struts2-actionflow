<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Struts2 ActionFlow Showcase</title>
	</head>
	<body>
		<h4>advanced example</h4>
		<s:form action="nextAction">
		
			<s:textfield key="name" label="Name" />
		
			<tr><td><br/></td></tr>
		
			<tr>
    		<td colspan="2">
    			<div>
						<s:submit value="previous" action="prevAction" theme="simple"/>
						<s:submit value="next" action="nextAction" theme="simple"/>
					</div>
				</td>
			</tr>
		</s:form>
		
		<br/>
    <div class="example-code">		
        <i>Form:</i>
        <pre>
&lt;s:form action="nextAction">
    &lt;s:textfield key="name" label="Name" />

    &lt;s:submit value="previous" action="prevAction" />
    &lt;s:submit value="next" action="nextAction" />
&lt;/s:form>
        </pre>
        
        <i>Interceptor configuration:</i>
        <pre>
&lt;interceptor-ref name="actionFlow">
    &lt;param name="nextActionName">nextAction&lt;/param>
    &lt;param name="prevActionName">prevAction&lt;/param>
&lt;/interceptor-ref> 
        </pre>
        
        <i>Action configuration:</i>
        <pre>
&lt;action name="saveName" method="saveName" class="...">
    &lt;param name="actionFlowStep">1&lt;/param>
			
    &lt;result name="input">/WEB-INF/advanced/name.jsp&lt;/result>
    &lt;result name="error">/WEB-INF/advanced/name.jsp&lt;/result>
    &lt;result type="redirectAction">finish&lt;/result>
&lt;/action>
        </pre>
    </div>
  </body>
</html>