<%@ taglib prefix="s" uri="/struts-tags"%>

<p><label>Facility Count:</label> <s:property value="operators.size" /></p>
<ul style="list-style-type: none;">
	<s:iterator value="activeOperators">
	<li><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="flag.flagColor.smallIcon" escape="false" /></a>
		<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a>
	</li>
	</s:iterator>
	<li>...<a href="con_selectFacilities.jsp?id=<s:property value="id" />">add to more Facilities</a></li>
</ul>
