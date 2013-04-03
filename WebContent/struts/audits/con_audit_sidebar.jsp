<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<ul id="toolbar" class="vert-toolbar clearfix">
	<li class="head">
		<s:text name="Audit.header.Toolbar" />
	</li>
	
	<s:if test="canSystemEdit">
		<li>
			<a class="edit1" href="ConAuditMaintain.action?auditID=<s:property value="auditID" />">
				<s:text name="Audit.button.SystemEdit" />
			</a>
		</li>
	</s:if>
	
	<li>
		<a class="percent" href="Audit.action?auditID=<s:property value="auditID"/>&button=Recalculate">
			<s:text name="Audit.button.Recalculate" />
		</a>
	</li>
	
	<s:if test="canVerifyPqf">
		<li>
			<a class="verify" href="VerifyView.action?id=<s:property value="id" />">
				<s:text name="button.Verify" />
			</a>
		</li>
	</s:if>
	
	<s:if test="canPreview">
		<li>
			<a class="preview" href="#mode=ViewQ">
				<s:text name="Audit.button.Preview" />
			</a>
		</li>
	</s:if>
	
	<li>
		<a class="file" href="#viewBlanks=false&mode=View" id="viewBlanks">
			<s:text name="Audit.button.ViewAnswered" />
		</a>
	</li>
	
	<s:if test="(permissions.admin || permissions.auditor) && conAudit.auditType.categories.size() > 1">
		<li>
			<a class="addremove" href="AddRemoveCategories.action?auditID=<s:property value="auditID"/>">
				<s:text name="Audit.button.AddRemoveCategories" />
			</a>
		</li>
	</s:if>
	
	<s:if test="canViewRequirements">
		<li>
			<a class="print" href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true">
				<s:text name="Audit.button.PrintRequirements" />
			</a>
		</li>

		<s:if test="permissions.auditor">
			<li>
				<a class="edit2" href="Audit.action?auditID=<s:property value="auditID"/>#onlyReq=true&mode=Edit">
					<s:text name="Audit.button.EditRequirements" />
				</a>
			</li>
		</s:if>
		
		<s:if test="permissions.admin">
			<li>
				<a class="uploadreq" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">
					<s:text name="Audit.button.UploadRequirements" />
				</a>
			</li>
		</s:if>
		<s:elseif test="permissions.onlyAuditor">
			<li>
				<a class="uploadreq" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">
					<s:text name="Audit.button.UploadRequirements" />
				</a>
			</li>
		</s:elseif>
		<s:elseif test="permissions.contractor">
			<li>
				<a class="uploadreq" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">
					<s:text name="Audit.button.UploadRequirements" />
				</a>
			</li>
		</s:elseif>
		
		<s:if test="permissions.operatorCorporate">
			<li>
				<a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">
					<s:text name="Audit.button.ReviewRequirements" />
				</a>
			</li>
		</s:if>
	</s:if>
	
	<s:if test="canSchedule">
		<li>
			<a class="calendar" href="ScheduleAudit<s:if test="conAudit.scheduledDate != null && permissions.admin">!edit</s:if>.action?auditID=<s:property value="conAudit.id"/>">
				<s:text name="Audit.button.ScheduleAudit" />
			</a>
		</li>
	</s:if>
	
	<li>
		<a class="print" href="#" onclick="printPreview(<s:property value="auditID"/>); return false;">
			<s:text name="Audit.button.PrintAll" />
		</a>
	</li>
	<li>
		<a class="excel" href="AuditDownload.action?auditID=<s:property value="auditID" />">
			<s:text name="Audit.DownloadToExcel" />
		</a>
	</li>

    <s:if test="permissions.admin">
    <li>
         <a class="excel" href="AuditTranslationDownload.action?contractor=<s:property value="contractor.id" />">
             <s:text name="Audit.button.AuditTranslationDownload" />
         </a>
    </li>
    </s:if>
</ul>

<div <s:if test="categories.keySet().size == 1"> style="display: none;"</s:if>>
	<ul id="aCatlist" class="vert-toolbar catUL">
		<li class="head">
			<s:text name="Audit.header.Categories" />
		</li>
		
		<s:iterator value="categoryNodes" id="catNode">
			<li id="category_<s:property value="#catNode.category.id"/>" class="catlist">
				<a class="hist-category" href="#categoryID=<s:property value="#catNode.category.id"/>">
					<span class="category-name"><s:property value="#catNode.category.name" /></span>
					
					<span class="cat-percent">
						<s:if test="permissions.admin || permissions.auditor">
							<s:if test="#catNode.override">
								<img src="images/add_remove.png" />
							</s:if>
						</s:if>
	
						<s:if test="!isEveryCAOCompleteOrHigher()">					
							<s:if test="showVerified">
								<s:if test="#catNode.percentVerified < 100" >
									<s:if test="!conAudit.auditType.annualAddendum" >
										<img src="images/icon_text_alert.png"/>
									</s:if>
								</s:if>
								<s:else>						
									<img src="images/okCheck.gif" />
								</s:else>
							</s:if>
							<s:else>
								<s:property value="#catNode.percentComplete" />%
							</s:else> 
						</s:if>
					</span>
				</a>
				
				<s:set name="subcatNode" value="%{#catNode}" /> 
				<s:if test="#catNode.subCategories.size() > 0">
					<div class="subcat">
						<s:include value="con_audit_sidebar_subcat.jsp" />
					</div>
				</s:if>
			</li>
		</s:iterator>
	</ul>
</div>