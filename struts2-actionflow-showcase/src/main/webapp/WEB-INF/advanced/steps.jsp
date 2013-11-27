<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="navbar">
	<div class="navbar-inner">
		<ul class="nav">
			<li class="divider-vertical"></li>
			
			<s:iterator value="stepsData.steps">
				<s:if test="stepsData.stepIndex > key">
					<s:set var="status" value="'passed'"/>
				</s:if>
				<s:elseif test="stepsData.stepIndex == key">
					<s:set var="status" value="'active'"/>
				</s:elseif>
				<s:else>
					<s:set var="status" value="'simple'"/>
				</s:else>
				
				<li class="<s:property value="#status"/>"><s:property value="key"/><br/><s:property value="value"/></li>
				<li class="divider-vertical"></li>
			</s:iterator>
			
		</ul>
	</div>
</div>
