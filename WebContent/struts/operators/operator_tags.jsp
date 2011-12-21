<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<html>
	<head>
		<title><s:text name="OperatorTags.title.WithOperator"><s:param value="%{operator.name}" /></s:text></title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

		<s:include value="../jquery.jsp"/>
		<script type="text/javascript" src="js/operator_tags.js"></script>
	</head>
	<body>
		<s:include value="../actionMessages.jsp" />
		
		<s:if test="permissions.admin">
			<s:include value="opHeader.jsp"></s:include>
		</s:if>
		<s:else>
			<h1><s:text name="OperatorTags.title.DefineContractorTags" /></h1>
		</s:else>
		
		<a href="OperatorTags.action?id=<s:property value="id" />"><s:text name="button.Refresh" /></a>
		<div id="warnConfirm"></div>
		
		<table>
			<tr>
				<td>
					<div id="tagListDiv">
						<s:form id="operatorTagForm">
							<s:hidden name="id" />
							
							<table class="report">
								<thead>
									<tr>
										<th>
											<s:text name="OperatorTags.header.TagID" />
										</th>
										<th>
											<s:text name="OperatorTags.header.TagName" />
										</th>
										<th>
											<s:text name="OperatorTags.header.VisibleTo">
												<s:param value="%{getText('global.Operator')}" />
											</s:text>
										</th>
										<th>
											<s:text name="OperatorTags.header.VisibleTo">
												<s:param value="%{getText('global.Contractors')}" />
											</s:text>
										</th>
										
										<s:if test="operator.corporate">
											<th>
												<s:text name="OperatorTags.header.UsableBySites" />
											</th>
										</s:if>
										
										<pics:permission perm="ContractorTags" type="Delete">
											<th>
												<s:text name="button.Remove" />
											</th>
										</pics:permission>
										
										<s:if test="permissions.isCanAddRuleForOperator(operator) || permissions.hasPermission('ManageAudits')">
											<th>
												<s:text name="button.Edit" />
											</th>
										</s:if>
									</tr>
								</thead>
								
								<s:set var="globalOperator" value="operator" />
								
								<s:iterator value="tags" status="rowstatus">
									<s:hidden name="tags[%{#rowstatus.index}].id" value="%{id}" />
									
									<tr>
										<td class="right">
											<s:property value="id" />
										</td>
										
										<pics:permission perm="ContractorTags" type="Edit">
											<s:if test="operator.id == permissions.accountId">
												<td>
													<s:textfield name="tags[%{#rowstatus.index}].tag" value="%{tag}" />
												</td>
												<td>
													<s:checkbox name="tags[%{#rowstatus.index}].active" value="%{active}" />
												</td>
												<td>
													<s:checkbox name="tags[%{#rowstatus.index}].visibleToContractor" value="%{visibleToContractor}" />
												</td>
												
												<s:if test="operator.corporate">
													<td>
														<s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" />
													</td>
												</s:if>
											</s:if>
										</pics:permission>
										<s:else>
											<td>
												<s:property value="tag"/>
											</td>
											<td class="center">
												<s:if test="active">
													<s:text name="YesNo.Yes" />
												</s:if>
												<s:else>
													<s:text name="YesNo.No" />
												</s:else>
											</td>
											<td class="center">
												<s:if test="visibleToContractor">
													<s:text name="YesNo.Yes" />
												</s:if>
												<s:else>
													<s:text name="YesNo.No" />
												</s:else>
											</td>
										</s:else>
										
										<pics:permission perm="ContractorTags" type="Delete">
											<s:if test="#globalOperator.corporate && operator.id != permissions.accountId">
												<s:if test="operator.id != permissions.accountId">
													<td>
														<s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" disabled="true"/>
													</td>
												</s:if>
												<s:else>
													<td>
														<s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" />
													</td>
												</s:else>
											</s:if>
											
											<s:if test="operator.id != permissions.accountId">
												<td>
													<p title="<s:text name="OperatorTags.TagBelongsToAnotherOperator"><s:param value="operator"/></s:text>">
														<s:text name="OperatorTags.message.CannotRemove" />
													</p>
												</td>
											</s:if>
											<s:elseif test="!auditTypeRules.empty || !auditCategoryRules.empty || !operatorFlagCriteria.empty">
												<td>
													<p title="<s:text name="OperatorTags.TagInUseByConfig"/>">
														<s:text name="OperatorTags.message.CannotRemove" />
													</p>
												</td>
											</s:elseif>
											<s:elseif test="!contractorTags.empty">
												<td>
													<p title="<s:text name="OperatorTags.TagAppliedToContractors">
																<s:param value="contractorNames"/>
															  </s:text>">
														<s:text name="OperatorTags.message.CannotRemove" />
													</p>
												</td>
											</s:elseif>
											<s:else>
												<td>
													<a href="#" class="checkRemove" rel="<s:property value="id" />"><s:text name="button.Remove" /></a>
												</td>
											</s:else>
										</pics:permission>
										
										<s:if test="permissions.isCanAddRuleForOperator(operator) || permissions.hasPermission('ManageAudits')">
											<td>
												<s:if test="permissions.isCanAddRuleForOperator(operator)">
													<s:if test="!auditTypeRules.empty">
														<a href="AuditTypeRuleTableAjax.action?comparisonRule.tag.id=${id}&button=tags" rel="facebox" class="edit picsbutton">Edit Audit Rules</a>
													</s:if>
													
													<s:if test="!auditCategoryRules.empty">
														<a href="CategoryRuleTableAjax.action?comparisonRule.tag.id=${id}&button=tags" rel="facebox" class="edit picsbutton">Edit Category Rules</a>
													</s:if>
												</s:if>
												
												<s:if test="permissions.hasPermission('ManageAudits') && !operatorFlagCriteria.empty">
													<a rel="facebox" href="ManageFlagCriteriaOperator.action?id=<s:property value="operatorFlagCriteria.get(0).operator.id" />" class="edit picsbutton">Edit Flag Criteria</a>
												</s:if>
											</td>
										</s:if>
									</tr>
								</s:iterator>
								
								<s:if test="permissions.operatorCorporate">
									<pics:permission perm="ContractorTags" type="Edit">
										<tr>
											<td>
												<s:text name="OperatorTags.label.New" />
											</td>
											<td>
												<s:textfield name="tags[%{tags.size}].tag" value="%{tag}" />
											</td>
											<td colspan="<s:property value="operator.corporate ? 4 : 3"/>">
												<s:text name="OperatorTags.label.AddNewTag" />
											</td>
										</tr>
									</pics:permission>
								</s:if>
								<s:else>
									<tr>
										<td>
											<s:text name="OperatorTags.label.New" />
										</td>
										<td>
											<s:textfield name="tags[%{tags.size}].tag" value="%{tag}" />
										</td>
										<td colspan="<s:property value="operator.corporate ? 4 : 3"/>">
											<s:text name="OperatorTags.label.AddNewTag" />
										</td>
									</tr>
								</s:else>
							</table>
						
							<div>
								<s:submit method="save" value="%{getText('button.Save')}" cssClass="picsbutton positive" />
							</div>
						</s:form>
					</div>
				</td>
				<td>&nbsp;</td>
				<td>
					<div id="rules"></div>
				</td>
			</tr>
		</table>
	</body>
</html>