<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp" pageEncoding="UTF-8"%>
<ul id="toolbar" class="vert-toolbar"> 
	<li class="head">TOOLBAR</li>
	<pics:permission perm="AuditEdit">
		<li><a class="edit" href="ConAuditMaintain.action?auditID=<s:property value="auditID" />"
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
	<li>
		<a class="edit" href="AddRemoveCategories.action?auditID=<s:property value="auditID"/>">
			Add/Remove Categories
		</a>
	</li>
	<s:if test="canViewRequirements">
		<li><a class="print" href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true" 
			<s:if test="onlyReq && mode != 'Edit'">class="current"</s:if>>Print Requirements</a></li>
		<s:if test="permissions.auditor">
			<li><a class="edit" href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true&mode=Edit"
			 <s:if test="onlyReq && mode == 'Edit'">class="current"</s:if>>Edit Requirements</a></li>
		</s:if>
		<s:if test="permissions.admin">
			<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
		</s:if>
		<s:elseif test="permissions.onlyAuditor">
			<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
		</s:elseif>
		<s:if test="permissions.operatorCorporate">
			<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Review Requirements</a></li>
		</s:if>
	</s:if>
	<s:if test="canSchedule">
		<li><a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>"
				<s:if test="requestURI.contains('schedule_audit')">class="current"</s:if>>Schedule Audit</a></li>
	</s:if>
</ul>

<div <s:if test="categories.keySet().size == 1"> style="display: none;"</s:if>>
	<ul id="aCatlist" class="vert-toolbar catUL">
		<li class="head">CATEGORIES <span class="hidden-button">Show N/A</span></li>			
		<s:iterator value="categories">
			<s:if test="key.parent == NULL && value.applies">
				<li id="category_<s:property value="key.id"/>" class="catlist">
					<a class="hist-category" href="#categoryID=<s:property value="key.id" />"><s:property value="key.name" />
					<s:if test="conAudit.auditType.pqf">
						<span class="cat-percent">
							<s:if test="value.percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
							<s:else><s:property value="value.percentCompleted" />%</s:else>
						</span>
					</s:if>
					<s:elseif test="conAudit.auditType.workflow.hasSubmittedStep">
						<span class="cat-percent">
							<s:if test="value.percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
							<s:else><s:property value="value.percentVerified" />%</s:else>
						</span>
					</s:elseif>
					<s:elseif test="conAudit.auditType.id == 17">
						<span class="cat-percent">
							<s:if test="value.printableScore == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
							<s:else><s:property value="value.printableScore" />%</s:else>
						</span>
					</s:elseif>
					</a>
				</li>
				<s:if test="key.subCategories.size() > 1">
					<li style="display: none;" id="catSubCat_<s:property value="key.id"/>" class="currSub">
						<ul>
							<s:iterator value="key.subCategories">
								<li id="<s:property value="fullNumber.replace('.', '_')"/>_" class="subCatli clickable"><s:property value="name" /></li>
							</s:iterator>
						</ul>
					</li>
				</s:if>
			</s:if>
		</s:iterator>
	</ul>
	<ul id="nacatlist" class="vert-toolbar catUL">
		<li class="head">N/A CATEGORIES <span class="hidden-button">Back</span></li>
		<s:iterator value="categories" status="rowStatus">
			<s:if test="key.parent == NULL && !value.applies && permissions.picsEmployee">
				<li id="category_<s:property value="key.id"/>" class="catlist">
					<a class="hist-category" href="#categoryID=<s:property value="key.id" />"><s:property value="key.name" /></a>
					<div class="clear"></div>
				</li>
			</s:if>
		</s:iterator>
	</ul>
</div>