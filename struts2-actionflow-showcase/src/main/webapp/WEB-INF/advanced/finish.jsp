<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<div class="well">
	<h4>advanced example</h4>
	finish<br/><br/>
	
	Name: <s:property value="#session.names"/><br/>
	Phone: <s:property value="#session.phones"/><br/>
	Email: <s:property value="#session.emails"/><br/>
	
	<br/>
	<s:url var="m0Url" action="index" namespace="/"/>
	<s:url var="m1Url" action="start" namespace="/"/>
	<s:url var="m2Url" action="start" namespace="/advanced"/>
	<s:a href="%{m0Url}" cssClass="btn btn-large">Welcome</s:a>
	&nbsp;&nbsp;
	<s:a href="%{m1Url}" cssClass="btn btn-large">Start simple example</s:a>
	&nbsp;&nbsp;
	<s:a href="%{m2Url}" cssClass="btn btn-large">Start advanced example</s:a>
</div>
