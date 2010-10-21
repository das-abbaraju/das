<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<ul id="toolbar" class="vert-toolbar"> 
	<li class="head">TOOLBAR</li>
	<pics:permission perm="AuditEdit">
		<li><a class="edit1" href="ConAuditMaintain.action?auditID=<s:property value="auditID" />"
				<s:if test="requestURI.contains('audit_maintain.jsp')">class="current"</s:if>>System Edit</a></li>
	</pics:permission>
	<s:if test="canVerify">
		<pics:permission perm="AuditVerification">
			<li><a class="verify" href="VerifyView.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('verif')">class="current"</s:if>>Verify</a></li>
		</pics:permission>
	</s:if>
	<s:if test="canPreview">
		<li><a class="preview" href="Audit.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
		Questions</a></li>
	</s:if>
	<s:if test="permissions.admin">
		<li>
			<a class="addremove" href="AddRemoveCategories.action?auditID=<s:property value="auditID"/>">
				Add/Remove Categories
			</a>
		</li>
	</s:if>
	<s:if test="canViewRequirements">
		<li><a class="print" href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true" 
			<s:if test="onlyReq && mode != 'Edit'">class="current"</s:if>>Print Requirements</a></li>
		<s:if test="permissions.auditor">
			<li><a class="edit2" href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true&mode=Edit"
			 <s:if test="onlyReq && mode == 'Edit'">class="current"</s:if>>Edit Requirements</a></li>
		</s:if>
		<s:if test="permissions.admin">
			<li><a class="uploadreq" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
		</s:if>
		<s:elseif test="permissions.onlyAuditor">
			<li><a class="uploadreq" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
		</s:elseif>
		<s:if test="permissions.operatorCorporate">
			<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Review Requirements</a></li>
		</s:if>
	</s:if>
	<s:if test="canSchedule">
		<li><a class="calendar" href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>"
				<s:if test="requestURI.contains('schedule_audit')">class="current"</s:if>>Schedule Audit</a></li>
	</s:if>
</ul>

<div <s:if test="categories.keySet().size == 1"> style="display: none;"</s:if>>
	<ul id="aCatlist" class="vert-toolbar catUL">
		<li class="head">CATEGORIES <span class="hidden-button">Show N/A</span></li>			
		<s:iterator value="categoryNodes" id="catNode">
			<li id="category_<s:property value="#catNode.category.id"/>" class="catlist">
				<a class="hist-category" href="#categoryID=<s:property value="#catNode.category.id"/>"><s:property value="#catNode.category.name" />
				<s:if test="conAudit.auditType.pqf">
					<span class="cat-percent">
						<s:if test="categories.get(#catNode.category).percentCompleted == 100"><img src="images/okCheck.gif"/></s:if>
						<s:else><s:property value="categories.get(#catNode.category).percentCompleted" />%</s:else>
					</span>
				</s:if>
				<s:elseif test="conAudit.auditType.workflow.hasSubmittedStep">
					<span class="cat-percent">
						<s:if test="categories.get(#catNode.category).percentVerified == 100"><img src="images/okCheck.gif"/></s:if>
						<s:else><s:property value="categories.get(#catNode.category).percentVerified" />%</s:else>
					</span>
				</s:elseif>
				<s:elseif test="conAudit.auditType.id == 17">
					<span class="cat-percent">
						<s:if test="categories.get(#catNode.category).printableScore == 100"><img src="images/okCheck.gif"/></s:if>
						<s:else><s:property value="categories.get(#catNode.category).printableScore" />%</s:else>
					</span>
				</s:elseif>
				</a>
				<s:set name="subcatNode" value="%{#catNode}"/>
				<s:if test="#catNode.subCategories.size() > 0">
					<div class="subcat">
						<s:include value="con_audit_sidebar_subcat.jsp"/>
					</div>
				</s:if>
			</li>
		</s:iterator>
	</ul>
	<ul id="nacatlist" class="vert-toolbar catUL">
		<li class="head">N/A CATEGORIES <span class="hidden-button">Back</span></li>
		<s:iterator value="notApplicableCategoryNodes" id="catNode" status="rowStatus">
			<li id="category_<s:property value="#catNode.category.id"/>" class="catlist">
				<a class="hist-category" href="#categoryID=<s:property value="#catNode.category.id" />"><s:property value="#catNode.category.name" /></a>
				<s:set name="subcatNode" value="%{#catNode}"/>
				<s:if test="#catNode.subCategories.size() > 0">
					<div class="subcat">
						<s:include value="con_audit_sidebar_subcat.jsp"/>
					</div>
				</s:if>
			</li>
		</s:iterator>
	</ul>
</div>