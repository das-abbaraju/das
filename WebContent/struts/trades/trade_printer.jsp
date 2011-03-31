<%@ taglib prefix="s" uri="/struts-tags"%>
<s:property value="#ps.description" /> (<s:property value="#ps.id"/>)
<s:if test="#ps.parent != null">
<ul>
	IN
	<s:set var="ps" value="#ps.parent" />
	<s:include value="service_printer.jsp" />
</ul>
</s:if>
