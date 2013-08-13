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
		
			<s:textfield key="name" label="name" />
		
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
  </body>
</html>