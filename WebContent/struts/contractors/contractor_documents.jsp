<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title><s:text name="%{scope}.title" /></title>
		
		<s:include value="../reports/reportHeader.jsp"/>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=${version}" />
		
		<s:include value="../jquery.jsp"/>
		
		<script type="text/javascript">
			function showAll(type) {
				if ($('#' + type + ' tr.hidden:hidden').length > 0)
					$('#'+type + ' tr.hidden').show();
				else
					$('#'+type + ' tr.hidden').hide();
			}
		</script>
		
		<style type="text/css">
			.hidden {
				display: none;
				font-size: smaller;
				background-color: #f3f3f3;
			}
			
			h3 {
				margin-top: 1em;
			}
		</style>
	</head>
	<body>
		<s:include value="conHeader.jsp" />
		
		<s:if test="manuallyAddAudit">
			<a class="add" href="AuditOverride.action?id=<s:property value="id"/>"><s:text name="%{scope}.link.CreateNewAudit" /></a>
		</s:if>
		
		<table style="width: 100%;">
			<tr>
				<td style="width: 45%; padding-right: 5%;">
					<s:iterator value="auditTypes.keySet()" var="classType" status="stat">
						<a name="<s:property value="getSafeName(#classType.name)" />"></a>
						
						<h3><s:property value="#classType" escape="false" /></h3>
						
						<table class="report" id="table_<s:property value="getSafeName(#classType.name)" />">
							<thead>
								<tr>
									<th>
										<s:text name="global.Name" />
									</th>
									
									<s:if test="#classType.audit || #classType.im">
										<th>
											<s:text name="global.SafetyProfessional" />
										</th>
									</s:if>
									<s:else>
										<th>
											<s:text name="global.CSR" />
										</th>
									</s:else>
									
									<th>
										<s:text name="%{scope}.header.Scheduled" />
									</th>
									<th>
										<s:text name="button.View" />
									</th>
									
									<pics:permission perm="AuditCopy">
										<th>
											<s:text name="button.Copy" />
										</th>
									</pics:permission>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="auditTypes.get(#classType)" id="auditType">
									<s:iterator value="auditMap.get(#auditType)" id="audit">
										<tr>
											<td>
												<a href="Audit.action?auditID=<s:property value="#audit.id" />">
													<s:text name="%{#audit.auditType.getI18nKey('name')}" />
													
													<s:if test="#audit.auditFor.length() > 0">
														:<s:property value="#audit.auditFor" />
													</s:if>
													
													<s:if test="#audit.auditType.classType.policy">
														<br />
														<span style="font-size: 10px"><s:date name="#audit.effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@MonthAndYear}" /></span>
													</s:if>
												</a>
											</td>
											<td>
												<s:property value="#audit.auditor.name" />
											</td>
											<td>
												<s:date name="#audit.scheduledDate" />
											</td>
											<td>
												<s:if test="#audit.operators.size > 0">
													<a href="#" onclick="$('tr.row_'+<s:property value="#audit.id" />).toggle(); return false;">
														<s:iterator value="#audit.getCaoStats(permissions).keySet()" id="status" status="stat">
															<s:if test="getCaoStats(permissions).get(#status) > 1 || #audit.getCaoStats(permissions).keySet().size > 1">
																<s:property value="getCaoStats(permissions).get(#status)" />
															</s:if>
															
															<s:text name="%{#status.getI18nKey()}" /><s:if test="!#stat.last">,</s:if>
														</s:iterator>
													</a>
												</s:if>
											</td>
											
											<pics:permission perm="AuditCopy">
												<td>
													<a href="ConAuditCopy.action?auditID=<s:property value="#audit.id" />"><s:text name="button.Copy" /></a>
												</td>
											</pics:permission>
										</tr>
										
										<s:if test="#audit.operators.size > 0">
											<tr class="row_<s:property value="#audit.id" /> hidden">
												<s:set name="colspan" value="4" />
												
												<pics:permission perm="AuditCopy">
													<s:set name="colspan" value="#colspan + 1" />
												</pics:permission>
												
												<td colspan="<s:property value="#colspan" />">
													<table class="inner">
														<s:iterator value="#audit.operators" id="cao">
															<s:if test="#cao.isVisibleTo(permissions)">
																<tr>
																	<td>
																		<pics:permission perm="ManageOperators">
																			<a href="FacilitiesEdit.action?operator=<s:property value="#cao.operator.id"/>"><s:property value="#cao.operator.name"/></a>
																		</pics:permission>
																		
																		<pics:permission perm="ManageOperators" negativeCheck="true">
																			<s:property value="#cao.operator.name" />
																		</pics:permission>
																	</td>
																	<td>
																		<s:text name="%{#cao.status.getI18nKey()}" />
																	</td>
																	<td>
																		<s:date name="#cao.statusChangedDate" />
																	</td>
																	
																	<s:if test="#audit.auditType.classType.im">
																		<td class="center">
																			<s:text name="ContractorAudit.PrintableScore.%{#audit.printableScore}" />
																		</td>
																	</s:if>
																</tr>
															</s:if>
														</s:iterator>
													</table>
												</td>
											</tr>
										</s:if>
									</s:iterator>
									
									<s:if test="#classType.name == getText('AuditType.17.name') && imScores.keySet().size > 0">
										<s:iterator value="imScores.keySet()" status="auditStatus" id="key">
											<tr>
												<td colspan="4">
													<s:text name="%{scope}.header.OverallScore" />:
													<s:text name="ContractorAudit.PrintableScore.%{imScores.get(#key)}" />
												</td>
											</tr>
										</s:iterator>
									</s:if>
								</s:iterator>
								
								<tr>
									<s:set name="colspan" value="4" />
									
									<pics:permission perm="AuditCopy">
										<s:set name="colspan" value="#colspan + 1" />
									</pics:permission>
									
									<td colspan="<s:property value="#colspan" />" class="center">
										<a href="#" onclick="showAll('table_<s:property value="getSafeName(#classType.name)" escape="false" />'); return false;" class="preview"><s:text name="%{scope}.link.ViewAll" /></a>
									</td>
								</tr>
							</tbody>
						</table>
					</s:iterator>
				</td>
				<td style="width: 45%; padding-right: 5%;">
					<s:if test="expiredAudits.size > 0">
						<h3><s:text name="%{scope}.header.ExpiredAudits" /></h3>
						
						<table class="report" id="expired">
							<thead>
								<tr>
									<th>
										<s:text name="global.Name" />
									</th>
									<th>
										<s:text name="AuditStatus.Expired" />
									</th>
								</tr>
							</thead>
							
							<s:iterator value="expiredAudits">
								<tr>
									<td>
										<a href="Audit.action?auditID=<s:property value="id" />">
											<s:property value="auditType.name" />
											
											<s:if test="auditFor.length() > 0">
												: <s:property value="auditFor" />
											</s:if>
											
											<s:if test="auditType.classType == 'Policy'">
												<br />
												<span style="font-size: 10px"><s:date name="effectiveDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></span>
											</s:if>
										</a>
									</td>
									<td>
										<s:date name="expiresDate" />
									</td>
								</tr>
							</s:iterator>
						</table>
					</s:if>
				</td>
			</tr>
		</table>
		
		<br clear="both" />
		
		<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>
	</body>
</html>