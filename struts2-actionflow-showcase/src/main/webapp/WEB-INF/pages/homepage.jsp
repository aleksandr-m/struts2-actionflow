<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Struts2 ActionFlow Showcase</title>
		<link rel="stylesheet" type="text/css" href="<s:url value="/css/bootstrap.min.css"/>"/>
		<link rel="stylesheet" type="text/css" href="<s:url value="/css/style.css"/>"/>
	</head>
	<body>
		<a href="https://github.com/aleksandr-m/struts2-actionflow" target="_blank"><img style="position: absolute; top: 0; right: 0; border: 0;" src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png" alt="Fork me on GitHub"></a>
	
	
		<div class="hero-unit">
    	<h2>Struts2 ActionFlow Showcase</h2>
    
  		<s:url var="m1Url" action="start" namespace="/"/>
  		<s:url var="m2Url" action="start" namespace="/advanced"/>
			<s:a href="%{m1Url}" cssClass="btn btn-large">Start simple example</s:a>
			&nbsp;
			<s:a href="%{m2Url}" cssClass="btn btn-large">Start advanced example</s:a>
		</div>
	

	</body>
</html>
