<%@ taglib prefix="s" uri="/struts-tags"%>
<span class="question">
	<s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;
	<s:property value="question" escape="false"/>
	
</span>
<br clear="all">
<s:if test="hasRequirementB">
	<div class="info"><s:property value="requirement"/></div>
</s:if>