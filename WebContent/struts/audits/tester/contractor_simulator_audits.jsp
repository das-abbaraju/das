<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="audits.keySet().size() == 0">
	<div class="alert">No audits are required for this configuration.</div>
</s:if>
<s:else>
<s:iterator value="audits.keySet()" var="audit">
	<p><a href="#"
		onclick="fillCategories(<s:property value="id" />); return false;"><s:property
		value="name" /></a>
		<s:iterator value="audits.get(#audit)">
			<br /><s:property />
		</s:iterator>
	</p>
</s:iterator>
<div class="info"><s:property value="audits.keySet().size()"/> audits will be included.</div>
</s:else>