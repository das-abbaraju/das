<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>

<a href="AuditCat.action?auditID=<s:property value="auditID"/>&catDataID=<s:property value="catDataMap.get(#category).id"/>"><s:property value="name"/></a>
<s:if test="subCategories.size() > 0">
	<ol>
		<s:iterator value="subCategories" id="category">
			<li><s:include value="con_audit_cat.jsp"/></li>
		</s:iterator>
	</ol>
</s:if>
