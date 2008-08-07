<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="actionMessages.size > 0">
	<div id="info">
	<s:iterator value="actionMessages">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
</s:if>

<s:if test="actionErrors.size > 0">
	<div id="error">
	<s:iterator value="actionErrors">
		<s:property escape="false" /><br />
	</s:iterator>
	</div>
</s:if>
