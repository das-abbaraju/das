<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<html>
	<head>
		<title>
			<s:text name="ContractorAuditFileUpload.title">
				<s:param><s:property value="conAudit.auditType.name" /></s:param>
				<s:param><s:property value="conAudit.contractorAccount.name" /></s:param>
			</s:text>
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
		
		<style type="text/css">
		.cluetipHolder {
			margin-left: 3px;
			margin-bottom: 3px;
		}
		</style>
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
		
		<script type="text/javascript">
			function showAuditUpload(auditID, fileID, desc, question) {
			    if (desc === null) {
			        url = 'AuditFileUpload.action?auditID='+auditID+'&fileID='+fileID+'&question='+question;
			    } else {
			        url = 'AuditFileUpload.action?auditID='+auditID+'&fileID='+fileID+'&desc='+desc+'&question='+question;
			    }
				title = 'Upload';
				pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
				fileUpload = window.open(url,title,pars);
				fileUpload.focus();
			}
			
			$(function(){
				$('.cluetip').cluetip({
					arrows: true,
					cluetipClass: 'jtip',
					local: true,
					clickThrough: false,
					activation: 'click',
					sticky: true,
					showTitle: false,
					closeText: "<img src='images/cross.png' width='16' height='16'>"
				});
			});
		</script>
	</head>
	<body>
	
		<s:include value="../audits/audit_catHeader.jsp"/>
		<s:include value="../actionMessages.jsp" />

        <s:if test="showUploadRequirementsBanner" >
            <div class="info">
                <s:text name="Audit.message.info.UploadRequirement" />
            </div>
        </s:if>

		<h3><s:property value="conAudit.auditType.name" />#<s:property value="conAudit.id" /></h3>
				
		<ul>
			<li style="list-style-type: none;">
				<b><s:text name="global.SafetyProfessional" />:</b> 
				<s:if test="conAudit.closingAuditor != null">
					<s:property value="conAudit.closingAuditor.name"/>
				</s:if>
				<s:else>
					<s:property value="conAudit.auditor.name"/>
				</s:else>
			</li>
			<li style="list-style-type: none;"><b><s:text name="User.phone" />:</b> 
				<s:if test="conAudit.closingAuditor != null">
					<s:property value="conAudit.closingAuditor.phone"/>
				</s:if>
				<s:else>
					<s:property value="conAudit.auditor.phone"/>
				</s:else>
			</li>
			<li style="list-style-type: none;"><b><s:text name="User.email" />:</b> 
				<s:if test="conAudit.closingAuditor != null">
					<a href="mailto:<s:property value="conAudit.closingAuditor.email"/>"><s:property value="conAudit.closingAuditor.email"/></a>
				</s:if>
				<s:else>
					<a href="mailto:<s:property value="conAudit.auditor.email"/>"><s:property value="conAudit.auditor.email"/></a>
				</s:else>
			</li>
		</ul>
		
		<table style="background-color:none; border:none; margin:10px;">
			<tr>
				<td style="vertical-align:top; width:65%;">
					<h3 style="padding:10px 0 10px 0;"><s:text name="Audit.header.Requirements" /></h3>
					
					<table class="report" style="width:95%;">
						<thead>
							<tr>
								<td>
									<s:text name="Audit.header.OpenRequirements" />
								</td>
							</tr>
						</thead>
						
						<s:iterator value="openReqs" id="data">
							<tr>
								<td>
									<s:if test="!isStringEmpty(question.helpText)">
										<div class="right cluetipHolder">
											<a class="cluetip help" rel="#cluetip_<s:property value="question.id"/>" title="<s:text name="Audit.AdditionalInformation" />"></a>
											<div id="cluetip_<s:property value="question.id" />">
												<s:property value="question.helpText" escape="false" />
											</div>
										</div>
									</s:if>
									
									<s:set name="fileDesc" value="getFileDesc(#data.question)"/>
									
									<s:property value="#fileDesc"/>&nbsp;&nbsp;
									
									<s:if test="(permissions.operatorCorporate && !permissions.auditor) || !#data.requirementOpen">
										<s:property value="question.requirement" />
										<s:if test="#data.dateVerified != null">
											<br/>
											<div class="verified-answer">
												<img src="images/okCheck.gif" />
												<s:text name="Audit.message.ClosedOn">
													<s:param><s:date name="#data.dateVerified" /></s:param>
												</s:text>
											</div>
										</s:if>
									</s:if>
									<s:else>
										<a style="cursor:pointer;" onclick="javascript: showAuditUpload(<s:property value="conAudit.id"/>,0,'<s:property value="#fileDesc"/>',<s:property value="question.id"/>); return false;" title="<s:text name="Audit.help.UploadRequirements" />"><s:property value="question.requirement" /></a>
									</s:else>	
									<br/>
									
									<s:if test="comment.length() > 0">
										<span class="redMain"><b><s:text name="Audit.message.SafetyProfessionalComment" />: </b><s:property value="comment" escape="false"/></span>
									</s:if>
									
									<s:if test="permissions.auditor">
									<br/>
										<a href="Audit.action?auditID=<s:property value="#data.audit.id"/>#categoryID=<s:property value="#data.question.category.id"/>&onlyReqs=true&mode=Edit"><s:text name="Audit.button.CloseRequirement" /></a>									
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</table>		
				</td>
				<td style="vertical-align:top;">
					<h3 style="padding:10px 0 10px 0;"><s:text name="Audit.header.Documents" /></h3>
					
					<table class="report" style="width:95%;">
						<thead>
							<tr>
								<td>
									<s:text name="global.Description" />
								</td>
								<td>
									<s:text name="button.View" />
								</td>
								<td>
									<s:text name="button.Edit" />
								</td>
								<td>
									<s:text name="Audit.header.Reviewed" />
								</td>
							</tr>
						</thead>
						
						<s:iterator value="auditFiles">
							<tr>
								<td>
									<s:property value="description" />
								</td>
								<td>
									<a href="AuditFileUpload.action?auditID=<s:property value="conAudit.id"/>&fileID=<s:property value="id"/>&button=download" target="_BLANK">
										<img src="images/icon_DA.gif" />
									</a>
								</td>
								<td>
									<s:if test="permissions.admin || (!reviewed && permissions.userId == createdBy.Id) || (!reviewed && permissions.auditor)">
										<a class="edit" href="#" onclick="showAuditUpload(<s:property value="conAudit.id"/>,<s:property value="id"/>, null, 0);"><s:text name="button.Edit" /></a>
									</s:if>
								</td>
								<td class="center">
									<s:if test="reviewed">
										<img src="images/okCheck.gif" />
									</s:if> 
									<s:else>
										<s:if test="permissions.hasPermission(@com.picsauditing.access.OpPerms@AuditDocumentReview)">
											<a class="verify" href="ContractorAuditFileUpload.action?auditID=<s:property value="conAudit.id"/>&fileID=<s:property value="id"/>&button=Review"><s:text name="Audit.button.MarkAsReviewed" /></a>
										</s:if>
									</s:else>
								</td>
							</tr>
						</s:iterator>
					</table>
				</td>
			</tr>
			<tr></tr>
			<tr>
				<td>
					<table class="report" style="width:95%;" >
						<thead>
							<tr>
								<td>
									<s:text name="Audit.header.ClosedRequirements" />
								</td>
							</tr>
						</thead>
						
						<s:iterator value="closedReqs" id="data">
							<tr>
								<td>
									<s:if test="!isStringEmpty(question.helpText)">
										<div class="right cluetipHolder">
											<a class="cluetip help" rel="#cluetip_<s:property value="question.id"/>" title="<s:text name="Audit.AdditionalInformation" />"></a>
											<div id="cluetip_<s:property value="question.id" />">
												<s:property value="question.helpText" escape="false" />
											</div>
										</div>
									</s:if>
									
									<s:set name="fileDesc" value="getFileDesc(#data.question)"/>
									
									<s:property value="#fileDesc"/>&nbsp;&nbsp;
									
									<s:if test="permissions.operatorCorporate || !#data.requirementOpen">
										<s:property value="question.requirement" />
										<s:if test="#data.dateVerified != null">
											<br/>
											<div class="verified-answer">
												<img src="images/okCheck.gif" />
												<s:text name="Audit.message.ClosedOn">
													<s:param><s:date name="#data.dateVerified" /></s:param>
												</s:text>
											</div>
										</s:if>
									</s:if>
									<s:else>
										<a style="cursor:pointer;" onclick="javascript: showAuditUpload(<s:property value="conAudit.id"/>,0,'<s:property value="#fileDesc"/>',<s:property value="question.id"/>); return false;" title="<s:text name="Audit.help.UploadRequirements" />"><s:property value="question.requirement" /></a>
									</s:else>	
									<br/>
									
									<s:if test="comment.length() > 0">
										<span class="redMain"><b><s:text name="Audit.message.SafetyProfessionalComment" />: </b><s:property value="comment" escape="false"/></span>
									</s:if>
								</td>
							</tr>
						</s:iterator>
					</table>		
				</td>
			</tr>
		</table>
		
		<br clear="all">
	</body>
</html>