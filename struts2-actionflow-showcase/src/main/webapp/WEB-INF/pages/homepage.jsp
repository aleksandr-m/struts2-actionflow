<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<div class="hero-unit">
  	<h2>Struts2 ActionFlow Plugin Showcase</h2>
  	
  	<p>A Struts2 plug-in for creating wizards (action flows).</p>
  
		<s:url var="m1Url" action="start" namespace="/"/>
		<s:url var="m2Url" action="start" namespace="/advanced"/>
	<s:a href="%{m1Url}" cssClass="btn btn-large">Start simple example</s:a>
	&nbsp;
	<s:a href="%{m2Url}" cssClass="btn btn-large">Start advanced example</s:a>
</div>
