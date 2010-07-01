<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="center == null && requestURI.contains('assessment_edit')">
	<h1>Create New Assessment Center</h1>
</s:if>
<s:else>
	<h1><s:property value="center.name" /><span class="sub"><s:property value="subHeading" escape="false"/></span></h1>
</s:else>

<s:if test="permissions.admin">
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="AssessmentCenterEdit.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('assessment_edit')">class="current"</s:if>>Edit</a></li>
	<li><a href="UsersManage.action?accountId=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('users_manage')">class="current"</s:if>>Users</a></li>
	<li><a class="dropdown" href="#" onclick="return false;" 
		onmouseover="cssdropdown.dropit(this, event, 'manageSubMenu')">Management</a></li>
	<!-- Will we need this?
	<li><a href="ContractorList.action?filter.status=Active&filter.status=Demo<s:property value="operatorIds"/>">Contractors</a></li>
	 -->
</ul>
</div>
</s:if>

<div id="manageSubMenu" class="auditSubMenu">
<ul>
	<li><a href="ManageImportData.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_import_data')">class="current"</s:if>>Imported Data</a></li>
	<li><a href="ManageAssessmentTests.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_assessment_tests')">class="current"</s:if>>Assessment Tests</a></li>
	<li><a href="ManageUnmappedTests.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_unmapped_tests')">class="current"</s:if>>Test Mapping</a></li>
	<li><a href="ManageAssessmentResults.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_assessment_results')">class="current"</s:if>>Assessment Results</a></li>
	<li><a href="ManageMappedCompanies.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_mapped_companies')">class="current"</s:if>>Companies</a></li>
	<li><a href="ManageUnmappedCompanies.action?id=<s:property value="center.id"/>"
		<s:if test="requestURI.contains('manage_unmapped_companies')">class="current"</s:if>>Company Mapping</a></li>
</ul>
</div>

<s:include value="../../actionMessages.jsp" />