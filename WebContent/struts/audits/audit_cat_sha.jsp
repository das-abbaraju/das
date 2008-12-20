<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>

<h2>Category <s:property value="category.number"/> - <s:property value="category.category"/></h2>
<s:if test="permissions.admin || permissions.contractor">
	<span class="redMain">You must input at least your corporate statistics. To further assist your clients, please
	enter additional locations that you maintain logs for that may be needed by your clients.
	</span>
</s:if>
<s:if test="mode == 'View'">
	<s:iterator value="conAudit.oshas">
		<s:if test="matchesType(category.id, type) == true">
			<s:include value="audit_cat_osha2.jsp"/>
		</s:if>
	</s:iterator>
</s:if>
<s:if test="mode == 'Edit'">
	<s:if test="category.id in { 151, 157 }">		
		<s:if test="permissions.contractor">
			<span style="font-size: 12px;color:#003768;">Provide the following numbers (excluding subcontractors) using your OSHA/MSHA 300 Forms from the past 3 years:</span><br/>
		</s:if>
	</s:if>

	<s:if test="catDataID == 0">
		<s:include value="audit_cat_osha_edit2.jsp"/>
	</s:if>

	<s:iterator value="conAudit.oshas">
		<s:if test="matchesType(category.id, type) == true">
			<s:include value="audit_cat_osha_edit2.jsp"/>
		</s:if>
	</s:iterator>

	<s:if test="catDataID != 0">
		<s:form action="OshaSave" method="POST" enctype="multipart/form-data">
			<s:hidden name="auditID"></s:hidden>
			<s:hidden name="catDataID"></s:hidden>
			<s:submit name="button" value="Add New Location" cssStyle="padding: 6px;position: relative;left: 380px;"></s:submit>
		</s:form>
	</s:if>
</s:if>
