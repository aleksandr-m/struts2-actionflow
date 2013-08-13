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
		
			<s:textfield key="phone" label="phone" />
		
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
  </body>
</html>