<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"
	pageEncoding="UTF-8"%>
<ul id="toolbar" class="vert-toolbar">
	<li class="head">TOOLBAR</li>
	<pics:permission perm="AuditEdit">
		<li><a class="edit1"
			href="ConAuditMaintain.action?auditID=<s:property value="auditID" />">System
		Edit</a></li>
	</pics:permission>
	<li><a href="Audit.action?auditID=<s:property value="auditID"/>&button=Recalculate">% Recalculate</a></li>
	<s:if test="canVerifyPqf">
		<li><a class="verify"
			href="VerifyView.action?id=<s:property value="id" />">Verify</a></li>
	</s:if>
	<s:if test="canPreview">
		<li><a class="preview" href="#mode=ViewQ">Preview Questions</a></li>
	</s:if>
	<li><a class="file" href="#viewBlanks=false&mode=View" id="viewBlanks">View Answered</a></li>
	<s:if test="permissions.admin && categories.keySet().size > 1">
		<li><a class="addremove"
			href="AddRemoveCategories.action?auditID=<s:property value="auditID"/>">
		Add/Remove Categories </a></li>
	</s:if>
	<s:if test="canViewRequirements">
		<li><a class="print"
			href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true">Print
		Requirements</a></li>
		<s:if test="permissions.auditor">
			<li><a class="edit2"
				href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true&mode=Edit">Edit
			Requirements</a></li>
		</s:if>
		<s:if test="permissions.admin">
			<li><a class="uploadreq"
				href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload
			Requirements</a></li>
		</s:if>
		<s:elseif test="permissions.onlyAuditor">
			<li><a class="uploadreq"
				href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload
			Requirements</a></li>
		</s:elseif>
		<s:elseif test="permissions.contractor">
			<li><a class="uploadreq"
				href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload
			Requirements</a></li>
		</s:elseif>
		<s:if test="permissions.operatorCorporate">
			<li><a class="file"
				href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Review
			Requirements</a></li>
		</s:if>
	</s:if>
	<s:if test="canSchedule">
		<li><a class="calendar"
			href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>">Schedule
		Audit</a></li>
	</s:if>
</ul>

<div
	<s:if test="categories.keySet().size == 1"> style="display: none;"</s:if>>
<ul id="aCatlist" class="vert-toolbar catUL">
	<li class="head">CATEGORIES <span class="hidden-button">Show
	N/A</span></li>
	<s:iterator value="categoryNodes" id="catNode">
		<li id="category_<s:property value="#catNode.category.id"/>" class="catlist">
			<a class="hist-category" href="#categoryID=<s:property value="#catNode.category.id"/>">
			<s:property value="#catNode.category.name" /> 
			<span class="cat-percent">
				<s:if test="#catNode.percentComplete < 100">
					<s:property value="#catNode.percentComplete" />%
				</s:if>
				<s:else>
					<s:if test="#catNode.percentVerified < 100">
						<img src="images/icon_text_alert.png"/>
					</s:if>
					<s:else>						
						<img src="images/okCheck.gif" />
					</s:else>					
				</s:else> 
			</span></a> 
			<s:set name="subcatNode" value="%{#catNode}" /> 
			<s:if test="#catNode.subCategories.size() > 0">
				<div class="subcat"><s:include value="con_audit_sidebar_subcat.jsp" /></div>
			</s:if>
		</li>
	</s:iterator>
</ul>
<ul id="nacatlist" class="vert-toolbar catUL">
	<li class="head">N/A CATEGORIES <span class="hidden-button">Back</span></li>
	<s:iterator value="notApplicableCategoryNodes" id="catNode"
		status="rowStatus">
		<li id="category_<s:property value="#catNode.category.id"/>"
			class="catlist"><a class="hist-category"
			href="#categoryID=<s:property value="#catNode.category.id" />"><s:property
			value="#catNode.category.name" /></a> <s:set name="subcatNode"
			value="%{#catNode}" /> <s:if test="#catNode.subCategories.size() > 0">
			<div class="subcat"><s:include
				value="con_audit_sidebar_subcat.jsp" /></div>
		</s:if></li>
	</s:iterator>
</ul>
</div>