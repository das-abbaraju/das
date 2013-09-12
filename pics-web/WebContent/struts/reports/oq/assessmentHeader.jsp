<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="center.id == 0 && requestURI.contains('assessment_edit')">
	<h1>
		<s:text name="AssessmentCenterEdit.CreateNewAssessmentCenter" />
	</h1>
</s:if>
<s:else>
	<h1>
		<s:property value="center.name" />
		<span class="sub">
			<s:property value="subHeading" escape="false"/>
		</span>
	</h1>
</s:else>

<s:if test="permissions.admin">
	<div id="internalnavcontainer">
		<ul id="navlist">
			<li>
				
				<a href="AssessmentCenterEdit.action?center=<s:property value="center.id"/>"
					class="<s:if test="requestURI.contains('assessment_edit')">current</s:if>">
					<s:text name="button.Edit" />
				</a>
			</li>
			<li>
				<a href="UsersManage.action?account=<s:property value="center.id"/>"
					class="<s:if test="requestURI.contains('users_manage')">current</s:if>">
					<s:text name="global.Users" />
				</a>
			</li>
			<li>
				<a class="dropdown"
					href="javascript:;"
					onmouseover="cssdropdown.dropit(this, event, 'manageSubMenu')">
					<s:text name="menu.Management" />
				</a>
			</li>
		</ul>
	</div>
</s:if>
	
<div id="manageSubMenu" class="auditSubMenu">
	<ul>
		<li>
			<a href="ManageImportData.action?id=<s:property value="center.id"/>"
				class="<s:if test="requestURI.contains('manage_import_data')">current</s:if>">
				Imported Data
			</a>
		</li>
		<li>
			<a href="ManageAssessmentTests.action?id=<s:property value="center.id"/>"
				class="<s:if test="requestURI.contains('manage_assessment_tests')">current</s:if>">
				Assessment Tests
			</a>
		</li>
		<li>
			<a href="ManageUnmappedTests.action?id=<s:property value="center.id"/>"
				class="<s:if test="requestURI.contains('manage_unmapped_tests')">current</s:if>">
				Test Mapping
			</a>
		</li>
		<li>
			<a href="ManageAssessmentResults.action?id=<s:property value="center.id"/>"
				class="<s:if test="requestURI.contains('manage_assessment_results')">current</s:if>">
				Assessment Results
			</a>
		</li>
		<li>
			<a href="ManageMappedCompanies.action?id=<s:property value="center.id"/>"
				class="<s:if test="requestURI.contains('manage_mapped_companies')">current</s:if>">
				Companies
			</a>
		</li>
		<li>
			<a href="ManageUnmappedCompanies.action?id=<s:property value="center.id"/>"
				class="<s:if test="requestURI.contains('manage_unmapped_companies')">current</s:if>">
				Company Mapping
			</a>
		</li>
	</ul>
</div>

<s:include value="../../actionMessages.jsp" />