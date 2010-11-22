<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>

<h2>Category <s:property value="#category.number"/> - <s:property value="#category.name"/></h2>
<s:if test="#category.helpText != null && #category.helpText.length() > 0">
	<div class="alert"><s:property value="#category.helpText" escape="false"/></div>
</s:if> 
<s:if test="permissions.admin || permissions.contractor">
	<span class="redMain">You must input at least your corporate statistics. To add additional 
		sites/locations, please click on 'Add New Location' button located at bottom of the page.
	</span>
</s:if>
<s:if test="mode == 'View'">
	<s:iterator value="conAudit.oshas">
		<s:if test="matchesType(#category.id, type) == true">
			<s:include value="audit_cat_osha2.jsp"/>
		</s:if>
	</s:iterator>
</s:if>
<s:if test="mode == 'Edit'">

	<s:if test="categoryData.id == 0">
		<s:include value="audit_cat_osha_edit2.jsp"/>
	</s:if>

	<s:iterator value="conAudit.oshas">
		<s:if test="matchesType(#category.id, type) == true">
			<s:include value="audit_cat_osha_edit2.jsp"/>
		</s:if>
	</s:iterator>

	<s:if test="categoryData.id != 0">
		<s:form action="OshaSave" method="POST" enctype="multipart/form-data" cssStyle="text-align: center;">
			<s:hidden name="auditID"/>
			<s:hidden name="catDataID" value="%{categoryData.id}"/>
			<s:submit name="button" cssClass="picsbutton positive" value="Add New Location"/>
		</s:form>
	</s:if>
</s:if>
