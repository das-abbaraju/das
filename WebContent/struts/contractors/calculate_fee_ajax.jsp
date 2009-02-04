<%@ taglib prefix="s" uri="/struts-tags"%>
$<s:if test="fee != null">
	<s:property value="fee.amount"/>
</s:if>
<s:else>
	0
</s:else>
<br/>