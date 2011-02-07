<%@ taglib prefix="s" uri="/struts-tags"%>
<h3><s:property value="auditType.name" /></h3>
<ul class="categories">
	<s:iterator value="categories">
		<s:include value="contractor_simulator_category_include.jsp" />
	</s:iterator>
</ul>
