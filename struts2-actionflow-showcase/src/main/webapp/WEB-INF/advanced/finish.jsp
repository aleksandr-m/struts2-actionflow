<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Struts2 ActionFlow Showcase</title>
	</head>
	<body>
		<h4>advanced example</h4>
		finish<br/><br/>
		
		<s:property value="#session.names"/><br/>
		<s:property value="#session.phones"/><br/>
		<s:property value="#session.emails"/><br/>
		
		<br/>
		<s:url var="m1Url" action="start" namespace="/"/>
		<s:url var="m2Url" action="start" namespace="/advanced"/>
		<s:a href="%{m1Url}">start simple example</s:a>
		&nbsp;&nbsp;
		<s:a href="%{m2Url}">start advanced example</s:a>
  </body>
</html>