<%@ taglib prefix="s" uri="/struts-tags"%>
<span class="question">
	<span class="questionNumber"><s:property value="#q.expandedNumber"/></span>
	<s:property value="#q.name" escape="false"/>
	
</span>
<br clear="all">
<s:if test="hasRequirement">
	<div class="info"><s:property value="requirement"/></div>
</s:if>